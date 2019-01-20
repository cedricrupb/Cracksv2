package de.upb.cracks.rules;

import java.util.Optional;

public class RuleMatchPostprocessor {

    private String postprocessUnit(String name, String type, String originalSentence){

        if(name.contains("\'")){

            if(name.endsWith("\'s")){
                name = name.substring(0, name.length() - 2);
            }else if(name.endsWith("\'")){
                name = name.substring(0, name.length() - 1);
            }

        }

        name = name.trim();

        return name;
    }

    public RuleMatch postprocess(RuleMatch match){

        String firstMatch = postprocessUnit(match.getFirstMatch(), match.getRule().getFirstType(), match.getOriginalSentence());
        String secondMatch = postprocessUnit(match.getSecondMatch(), match.getRule().getSecondType(), match.getOriginalSentence());

        return new RuleMatch(match.getRule(), firstMatch, secondMatch, match.getOriginalSentence());
    }

}
