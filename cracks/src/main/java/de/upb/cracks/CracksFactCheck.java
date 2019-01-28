package de.upb.cracks;

import de.upb.cracks.command.Predict;
import de.upb.cracks.command.Train;
import picocli.CommandLine;

@CommandLine.Command(name="cracksFactCheck", version = "Cracks FactChecker v2.0")
public class CracksFactCheck {


    public static void main(String[] args){
        args = new String[]{"predict", "/Users/cedricrichter/IdeaProjects/Cracksv2/cracks/src/main/resources/model.json", "/Users/cedricrichter/IdeaProjects/Cracksv2/cracks/src/main/resources/test.tsv", "/Users/cedricrichter/IdeaProjects/Cracksv2/cracks/src/main/resources/prediction.ttl"};
        CommandLine commandLine = new CommandLine(new CracksFactCheck());
        commandLine.addSubcommand("train", new Train());
        commandLine.addSubcommand("predict", new Predict());


        commandLine.parseWithHandler(new CommandLine.RunLast(), args);


    }
}
