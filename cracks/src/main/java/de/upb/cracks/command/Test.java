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

@CommandLine.Command(name="predict", description = "Estimate the truthfulness of a single statement.")
public class Test extends ModelDepended {

    @CommandLine.Parameters(index="0", paramLabel = "Model-File", description = "Model file as Json")
    private Path inputPath;

    @CommandLine.Parameters(index="1", paramLabel = "Query string", description = "The query string to query with.")
    private String query;


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

        try {

            System.out.println("Start loading model: "+inputPath.toString());
            IFactCheckModel model = checker.load(inputPath);

            System.out.println("Successfully loaded model. Start prediction.");

            System.out.println("Statement: "+query+" is true (Confidence: "+
                    model.eval(new FactCheckQueryEntity(0, query))+" )");

        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}