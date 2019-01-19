package de.upb.cracks.rules;

import java.util.regex.Pattern;

public class Rule {

    private String intention;
    private Pattern regex;
    private String firstType;
    private String secondType;


    public Rule(String intention, Pattern regex, String firstType, String secondType) {
        this.intention = intention;
        this.regex = regex;
        this.firstType = firstType;
        this.secondType = secondType;
    }

    public String getIntention() {
        return intention;
    }

    public Pattern getRegex() {
        return regex;
    }

    public String getFirstType() {
        return firstType;
    }

    public String getSecondType() {
        return secondType;
    }

    @Override
    public String toString(){
        return "Rule("+intention+", type1: "+firstType+", type2: "+secondType+", regex: "+regex.pattern()+")";
    }

}
