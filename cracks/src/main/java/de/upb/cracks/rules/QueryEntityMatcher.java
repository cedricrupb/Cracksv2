package de.upb.cracks.rules;

import java.util.Optional;

public class QueryEntityMatcher {

    private RuleMatcher matcher;
    private RuleMatchPostprocessor postprocessor = new RuleMatchPostprocessor();

    public QueryEntityMatcher(RuleMatcher matcher) {
        this.matcher = matcher;
    }

    public Optional<IFactQuery> match(String text){

        Optional<RuleMatch> matchOpt = matcher.match(text);

        if(!matchOpt.isPresent()){
            System.out.println("Could not parse text: "+ text);
            return Optional.empty();
        }

        RuleMatch match = matchOpt.get();

        match = postprocessor.postprocess(match);

        if(match == null){
            System.out.println("Postprocessor detected wrong rule: "+text);
            return Optional.empty();
        }

        String name = match.getFirstMatch();
        String type = match.getRule().getFirstType();
        boolean searchFor = false;

        if(type.endsWith("*")){
            type = type.substring(0, type.length() - 1);
            searchFor = true;
        }

        QueryEntity firstE = new QueryEntity(name, type, searchFor);

        name = match.getSecondMatch();
        type = match.getRule().getSecondType();
        searchFor = false;

        if(type.endsWith("*")){
            type = type.substring(0, type.length() - 1);
            searchFor = true;
        }

        QueryEntity secondE = new QueryEntity(name, type, searchFor);


        return null;
    }

    private class FactQuery implements IFactQuery{

        String intention;
        QueryEntity first;
        QueryEntity second;

        @Override
        public String getIntention() {
            return null;
        }

        @Override
        public QueryEntity getFirstEntity() {
            return null;
        }

        @Override
        public QueryEntity getSecondEntity() {
            return null;
        }
    }

}
