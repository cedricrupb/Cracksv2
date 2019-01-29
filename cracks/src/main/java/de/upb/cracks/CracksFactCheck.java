package de.upb.cracks;

import de.upb.cracks.command.Predict;
import de.upb.cracks.command.Test;
import de.upb.cracks.command.Train;
import picocli.CommandLine;

@CommandLine.Command(name="cracks", version = "Cracks FactChecker v2.0")
public class CracksFactCheck implements Runnable{

    CommandLine commandLine;

    public static void main(String[] args){
        CracksFactCheck cracksFactCheck = new CracksFactCheck();
        cracksFactCheck.commandLine = new CommandLine(cracksFactCheck);
        CommandLine commandLine = cracksFactCheck.commandLine;
        commandLine.addSubcommand("train", new Train());
        commandLine.addSubcommand("predict", new Predict());
        commandLine.addSubcommand("test", new Test());


        commandLine.parseWithHandler(new CommandLine.RunLast(), args);


    }

    @Override
    public void run() {
        commandLine.usage(System.out);
    }
}
