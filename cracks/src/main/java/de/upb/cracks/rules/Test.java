package de.upb.cracks.rules;

import de.upb.cracks.io.FactCheckTSVParser;
import de.upb.cracks.io.FactCheckTrainEntity;
import de.upb.cracks.wiki.WikiSource;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class Test {

    public static void main(String[] args) throws Exception {

        String text = FileUtils.readFileToString(
                new File("/Users/cedricrichter/IdeaProjects/Cracksv2/cracks/src/main/resources/RuleSet")
        );

        List<FactCheckTrainEntity> trainEntities = new FactCheckTSVParser(
                new FileInputStream("/Users/cedricrichter/IdeaProjects/Cracksv2/cracks/src/main/resources/train.tsv")
        ).parse();

        RuleParser parser = new RuleParser();
        RuleMatcher matcher = parser.compile(text);
        QueryEntityMatcher m = new QueryEntityMatcher(matcher);

        WikiSource source = new WikiSource();

        for(FactCheckTrainEntity e : trainEntities){

            System.out.println(e.getQuery());

            Optional<IFactQuery> queryOpt = m.match(e.getQuery());

            if(queryOpt.isPresent()){

                IFactQuery query = queryOpt.get();

                if(query.getFirstEntity().isSearchFor()){
                    System.out.println(source.queryByName(query.getFirstEntity().getText()).size() > 0);
                }

                if(query.getSecondEntity().isSearchFor()){
                    System.out.println(source.queryByName(query.getSecondEntity().getText()).size() > 0);
                }

            }

        }


    }

}
