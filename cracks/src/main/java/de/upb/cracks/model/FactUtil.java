package de.upb.cracks.model;

import de.upb.cracks.corpus.CoverageTest;
import de.upb.cracks.rules.QueryEntityMatcher;
import de.upb.cracks.rules.RuleMatcher;
import de.upb.cracks.rules.RuleParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FactUtil {

    public static QueryEntityMatcher matcher;

    public static QueryEntityMatcher matcher(){
        if(matcher == null){

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            CoverageTest.class.getClassLoader().getResourceAsStream("RuleSet.list")));


            String s = reader.lines().reduce((x, y) -> x+"\n"+y).get();
            RuleMatcher m = new RuleParser().compile(s);

            matcher = new QueryEntityMatcher(m);

        }
        return matcher;
    }
}
