package de.upb.cracks.rules;

public class RuleMatch {

    private Rule rule;
    private String firstMatch;
    private String secondMatch;
    private String originalSentence;


    public RuleMatch(Rule rule, String firstMatch, String secondMatch, String originalSentence) {
        this.rule = rule;
        this.firstMatch = firstMatch;
        this.secondMatch = secondMatch;
        this.originalSentence = originalSentence;
    }

    public Rule getRule() {
        return rule;
    }

    public String getFirstMatch() {
        return firstMatch;
    }

    public String getSecondMatch() {
        return secondMatch;
    }

    public String getOriginalSentence() {
        return originalSentence;
    }

    @Override
    public String toString(){
        return "RuleMatch( "+rule.getIntention()+", first: "+firstMatch+", second: "+secondMatch+" )";
    }
}
