package de.upb.cracks.rules;

import java.util.List;
import java.util.Optional;
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


        return Optional.ofNullable(bestMatch);
    }



    public static void main(String[] args){

        String rule = "[FILM] stars [PERSON]#001";

        RuleMatcher matcher = new RuleParser().compile(rule);

        String test = "The Right Stuff (film) stars Scott Glenn";

        System.out.println(
                matcher.match(test)
        );


    }

}
