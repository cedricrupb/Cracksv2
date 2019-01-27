package de.upb.cracks.corpus;

import de.upb.cracks.corpus.preprocess.SentenceSelector;
import de.upb.cracks.rules.QueryEntity;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.Token;
import edu.stanford.nlp.util.Pair;

import java.util.List;


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



}
