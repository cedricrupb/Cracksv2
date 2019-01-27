package de.upb.cracks.corpus;

import de.upb.cracks.corpus.preprocess.GloveFilter;
import de.upb.cracks.rules.IFactQuery;
import de.upb.cracks.rules.QueryEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FactQueryEndpoint {

    private WikiCorpus corpus = new WikiCorpus();
    private GloveFilter filter = new GloveFilter(0.6);


    private List<WikiSentence> querySentence(QueryEntity e1, QueryEntity e2){

        List<WikiSentence> out = new ArrayList<>();

        WikiRealisation realisation = corpus.queryRealisation(e1);

        for(String s : realisation.getSections()) {
            Optional<WikiSection> section = realisation.getSection(s);

            if (section.isPresent()) {
                out.addAll(section.get()
                        .searchFor(e2));
            }
        }

        return out;
    }


    public List<WikiSentence> getSentences(IFactQuery query){

        List<WikiSentence> out = new ArrayList<>();

        if(query.getFirstEntity().isSearchFor()){
            out.addAll(querySentence(query.getFirstEntity(), query.getSecondEntity()));
        }

        if(query.getSecondEntity().isSearchFor()){
            out.addAll(querySentence(query.getSecondEntity(), query.getFirstEntity()));
        }


        return out;
    }

}
