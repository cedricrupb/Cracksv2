package de.upb.cracks.command;

import de.upb.cracks.io.FactCheckTSVParser;
import de.upb.cracks.io.FactCheckTrainEntity;
import de.upb.cracks.model.CoverageFactChecker;
import de.upb.cracks.model.IFactCheckModel;
import de.upb.cracks.model.IFactChecker;
import de.upb.cracks.model.NaiveFactChecker;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@CommandLine.Command(name="train", description = "Train a specified classifier and save the model.")
public class Train extends ModelDepended {

    @CommandLine.Parameters(index="0", paramLabel = "Train-File", description = "Train file as TSV file")
    private Path inputPath;

    @CommandLine.Parameters(index="1", paramLabel = "Model-Output", description = "Model file as Json")
    private Path outputPath;

    @CommandLine.Option(names = {"-e", "--encoding"}, description = "Encoding of the input file. Default: ISO-8859-1")
    private String encoding = "ISO-8859-1";


    @Override
    public void run() {
        IFactChecker checker = selectChecker();

        if(checker == null){
            System.out.println(String.format("Unsupported model %d. EXIT.", super.model));
            return;
        }

        if(!Files.exists(inputPath)){
            System.out.println(String.format("Unknown train file: %d. EXIT.", inputPath.toAbsolutePath()));
            return;
        }

        try {
            InputStream stream = Files.newInputStream(inputPath);
            FactCheckTSVParser parser = new FactCheckTSVParser(stream, encoding);
            List<FactCheckTrainEntity> trainData = parser.parse();
            stream.close();

            System.out.println("Start training model. This takes some time.");
            IFactCheckModel model = checker.train(trainData);

            System.out.println("Successfully trained model. Save to: "+outputPath.toString());
            model.store(outputPath);

        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
