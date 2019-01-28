package de.upb.cracks.command;

import de.upb.cracks.model.CoverageFactChecker;
import de.upb.cracks.model.DefaulFactChecker;
import de.upb.cracks.model.IFactChecker;
import de.upb.cracks.model.NaiveFactChecker;
import picocli.CommandLine;

public abstract class ModelDepended implements Runnable {

    @CommandLine.Option(names={"-m", "--model"}, description = "Selects a fact check model." +
            "Currently following options are supported: naive, coverage, random")
    protected String model = "naive";


    protected IFactChecker selectChecker(){
        switch (model){
            case "naive":
                return new NaiveFactChecker();
            case "coverage":
                return new CoverageFactChecker();
            case "random":
                return new DefaulFactChecker();
            default:
                return null;
        }
    }

}
