package de.upb.cracks.command;

import de.upb.cracks.io.FactCheckQueryEntity;
import de.upb.cracks.io.FactCheckTSVParser;
import de.upb.cracks.model.IFactCheckModel;
import de.upb.cracks.model.IFactChecker;
import picocli.CommandLine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

@CommandLine.Command(name="predict", description = "Predicts the truthness of a list of statements. Reports a TTL with results for individual statements.")
public class Predict extends ModelDepended {

    @CommandLine.Parameters(index="0", paramLabel = "Model-File", description = "Model file as Json")
    private Path inputPath;

    @CommandLine.Parameters(index="1", paramLabel = "Test-File", description = "Test file as TSV")
    private Path testPath;

    @CommandLine.Parameters(index="2", paramLabel = "Export", description = "File with prediction as TTL")
    private Path outputPath;

    @CommandLine.Option(names = {"-e", "--encoding"}, description = "Encoding of the input file. Default: UTF-8")
    private String encoding = "UTF-8";


    @Override
    public void run() {
        IFactChecker checker = selectChecker();

        if(checker == null){
            System.out.println(String.format("Unsupported model %d. EXIT.", super.model));
            return;
        }

        if(!Files.exists(inputPath)){
            System.out.println(String.format("Unknown model file: %d. EXIT.", inputPath.toString()));
            return;
        }

        if(!Files.exists(testPath)){
            System.out.println(String.format("Unknown test file: %d. EXIT.", testPath.toString()));
            return;
        }
        
        if(outputPath == null) {
        	System.out.println("No output path is specified. EXIT.");
        	return;
        }

        try {
            System.out.println("Start prediction queries: "+testPath.toString());
            InputStream stream = Files.newInputStream(testPath);
            FactCheckTSVParser parser = new FactCheckTSVParser(stream, encoding);
            List<FactCheckQueryEntity> testData = parser.parseTest();
            stream.close();

            System.out.println("Start loading model: "+inputPath.toString());
            IFactCheckModel model = checker.load(inputPath);

            System.out.println("Successfully loaded model. Start prediction.");

            int i = 0;
            for(FactCheckQueryEntity query : testData){
                System.out.println("Predict on: " + (i++) + "/" + testData.size()+": "+query.getQuery());

                double score = model.eval(query);

                try{
                	Path parent = outputPath.getParent();
                	
                    if(parent != null && !Files.exists(parent)){
                        Files.createDirectory(parent);
                    }

                    if(!Files.exists(outputPath)){
                        Files.createFile(outputPath);
                    }

                    BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardOpenOption.APPEND);

                    writer.write(
                            String.format("<http://swc2017.aksw.org/task2/dataset/%d> <http://swc2017.aksw.org/hasTruthValue> \"%f\"^^<http://www.w3.org/2001/XMLSchema#double> .\n",
                                    query.getId(), score)
                    );

                    writer.close();

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}