package de.upb.cracks.corpus.preprocess;


import de.upb.cracks.rules.QueryEntity;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.Token;
import edu.stanford.nlp.util.Pair;


import java.util.*;

public class SentenceSelector {

    private static SentenceSelector selector;

    public static SentenceSelector getInstance(){
        if(selector == null){
            selector = new SentenceSelector();
        }
        return selector;
    }

    private enum Flexibility {
        EXACT, NAME, PLACE, DYNAMIC
    }

    private Map<String, Flexibility> flexibility = new HashMap<String, Flexibility>(){{
       put("Person", Flexibility.NAME);
       put("Place", Flexibility.PLACE);
       put("Award", Flexibility.DYNAMIC);
    }};


    private String prepareEntity(QueryEntity e){
        String name = e.getText();
        name = name.replaceAll("\\(.*\\)", "");
        name = name.trim();

        return name;
    }


    public boolean containsEntity(Sentence sentence, QueryEntity entity){

        Set<String> search = pattern(entity);

        for(String s : search){
            if(sentence.text().contains(s))
                return true;
        }

        return false;
    }


    private Set<String> pattern(QueryEntity entity){

        Flexibility flex = Flexibility.EXACT;

        if(flexibility.containsKey(entity.getType())){
            flex = flexibility.get(entity.getType());
        }

        String name = prepareEntity(entity);

        switch (flex){
            case EXACT:
                return exactPattern(name);
            case NAME:
                return namePattern(name);
            case PLACE:
                return placePattern(name);
            case DYNAMIC:
                return dynamicPattern(name);
        }

        return new HashSet<>();
    }

    private Set<String> exactPattern(String name){
        Set<String> pattern = new HashSet<>();
        pattern.add(name);

        return pattern;
    }

    private Set<String> namePattern(String name){
        Set<String> search = new HashSet<>();

        String[] split = name.split(" ");

        search.add(split[0]);

        for(int i = 1; i < split.length; i++){
            search.add(split[0]+" "+split[i]);
        }

        search.add(name);

        return search;
    }

    private Set<String> placePattern(String name){
        if(name.contains(",")){
            name = name.split(",")[0];
            name = name.trim();
        }

        Set<String> search = new HashSet<>();
        search.add(name);

        String[] placePattern = new String[]{"City"};

        for(String p : placePattern){
            if(name.endsWith(p)){
                search.add(name.replace(p, "").trim());
            }
        }

        return search;
    }

    private Set<String> dynamicPattern(String name){
        Set<String> search = new HashSet<>();

        search.add(name);

        if (name.contains("in"))
            search.add(name.replaceAll("in", "for"));
        if (name.contains("for"))
            search.add(name.replaceAll("for", "in"));

        return search;
    }


    public Pair<Integer, Integer> find(Sentence sentence, QueryEntity entity){

        int start = -1;
        int end = -1;

        Set<String> pattern = pattern(entity);

        for(String s : pattern){
            int ix = sentence.text().indexOf(s);
            if(ix != -1){
                start = ix;
                end = ix + s.length();
                break;
            }
        }

        if(start == -1)
            return null;


        int startToken = -1;
        int endToken = -1;

        int base = -1;

        int i = 0;
        for(Token token : sentence.tokens()){

            if(base == -1){
                base = token.characterOffsetBegin();
            }

            if(startToken == -1 && token.characterOffsetBegin() - base >= start){
                startToken = i;
            }

            if(startToken != -1 && token.characterOffsetEnd() - base >= end){
                endToken = i;
                break;
            }

            i++;
        }

        return new Pair<>(startToken, endToken);

    }

}
