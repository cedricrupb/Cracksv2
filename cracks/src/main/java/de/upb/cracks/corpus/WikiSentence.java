package de.upb.cracks.corpus;

import com.google.gson.Gson;
import de.upb.cracks.corpus.preprocess.SentenceSelector;
import de.upb.cracks.model.MaxOccurrenceSelector;
import de.upb.cracks.rules.QueryEntity;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.Token;
import edu.stanford.nlp.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WikiSentence {

    private WikiSection source;
    private QueryEntity search;
    private Sentence sentence;

    WikiSentence(WikiSection source, QueryEntity search, Sentence sentence) {
        this.source = source;
        this.search = search;
        this.sentence = sentence;
    }

    public WikiSection getSource() {
        return source;
    }

    public QueryEntity getSearch() {
        return search;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public WikiSentence window(int k){

        Pair<Integer, Integer> p = SentenceSelector.getInstance().find(
                sentence, search
        );

        if(p != null){

            List<Token> tokenList = sentence.tokens();

            String s = "";

            for(int i = Math.max(0, p.first - k); i <= Math.min(tokenList.size()-1, p.second + k); i++ ){
                s += tokenList.get(i).word() + " ";
            }
            s = s.substring(0, s.length() - 1);

            return new WikiSentence(
                    source, search, new Sentence(s)

            );

        }

        return this;

    }


    public boolean store(String path){
        Path p = Paths.get(path);

        Map<String, String> map = new HashMap<>();
        map.put("entity", search.getText());
        map.put("entityType", search.getType());
        map.put("sentence", sentence.text());

        Gson gson = new Gson();
        try {
            if(!Files.exists(p.getParent())){
                Files.createDirectory(p.getParent());
            }

            BufferedWriter writer = Files.newBufferedWriter(p);
            gson.toJson(map, writer);
            writer.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static WikiSentence load(String path){
        Path p = Paths.get(path);

        Gson gson = new Gson();
        try {
            BufferedReader reader = Files.newBufferedReader(p);
            Map<String, String> map = gson.fromJson(
                    reader,
                    Map.class
            );
            reader.close();

            QueryEntity search = new QueryEntity(
                    map.get("entity"), map.get("entityType"), false
            );

            return new WikiSentence(null, search, new Sentence(
                    map.get("sentence")
            ));

        } catch (IOException e) {
        }
        return null;
    }

}
