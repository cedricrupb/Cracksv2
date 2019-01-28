package de.upb.cracks.corpus.preprocess;

import de.upb.cracks.model.Stopwords;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FeatureExtractor {

    public static List<String> extract(Sentence sentence){
        List<String> out = new ArrayList<>();
        Set<String> stopwords = Stopwords.loadStopwords();

        for(Token token : sentence.tokens()){
            if(stopwords.contains(token.lemma())){
                continue;
            }
            if(!token.word().toLowerCase().equals(token.word()))
                continue;

            if(token.word().length() < 3){
                continue;
            }

            String lemma = token.lemma();
            lemma = lemma.replaceAll("[^a-zA-Z]", "");

            if(lemma.isEmpty())
                continue;

            out.add(token.lemma());

        }

        return out;
    }

}
