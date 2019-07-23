package de.upb.cracks.corpus;

import de.upb.cracks.corpus.preprocess.SentenceSelector;
import de.upb.cracks.rules.QueryEntity;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

import java.util.ArrayList;
import java.util.List;

public class WikiSection {

    private QueryEntity entity;
	private Document document;

    WikiSection(QueryEntity entity, String section) {
        this.entity = entity;
        this.document = new Document(section);
    }
    
    public QueryEntity getEntity() {
  		return entity;
  	}

    public List<WikiSentence> searchFor(QueryEntity search){

        List<WikiSentence> sentences = new ArrayList<>();

        for(Sentence sentence : document.sentences()){
            if(SentenceSelector.getInstance().containsEntity(sentence, search)){
                sentences.add(new WikiSentence(this, search, sentence));
            }
        }

        return sentences;
    }

    public List<WikiSentence> searchNotFor(QueryEntity search){

        List<WikiSentence> sentences = new ArrayList<>();

        for(Sentence sentence : document.sentences()){
            if(!SentenceSelector.getInstance().containsEntity(sentence, search)){
                sentences.add(new WikiSentence(this, search, sentence));
            }
        }

        return sentences;
    }

    public List<WikiSentence> sentences(){

        List<WikiSentence> sentences = new ArrayList<>();

        for(Sentence sentence : document.sentences()){
            sentences.add(new WikiSentence(this, null, sentence));
        }

        return sentences;
    }

}
