package de.upb.cracks.rules;

import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.Token;
import edu.stanford.nlp.util.PropertiesUtils;

import java.util.*;
import java.util.regex.Matcher;

public class RuleMatcher {

    private List<Rule> rules;

    RuleMatcher(List<Rule> rules) {
        this.rules = rules;
    }

    public Optional<RuleMatch> match(String text){

        RuleMatch bestMatch = null;


        for(Rule rule : rules){

            Matcher matcher = rule.getRegex().matcher(text);

            if(matcher.matches()){

                if(bestMatch != null && bestMatch.getRule().getRegex().pattern().length() >= rule.getRegex().pattern().length()){
                    continue;
                }

                String firstGroup = matcher.group(1);
                String secondGroup = matcher.group(2);

                bestMatch = new RuleMatch(rule, firstGroup, secondGroup, text);

            }
        }

        if(bestMatch == null){
            System.out.println("Unknown rule for: "+text);
        }


        return Optional.ofNullable(bestMatch);
    }


}
