package de.upb.cracks.corpus;

import de.upb.cracks.corpus.wiki.HeadlinePath;
import de.upb.cracks.corpus.wiki.WikipediaEndpoint;
import de.upb.cracks.rules.IFactQuery;
import de.upb.cracks.rules.QueryEntity;
import de.upb.cracks.rules.Rule;

import java.util.HashMap;
import java.util.Map;

public class WikiCorpus {

    private WikipediaEndpoint endpoint = new WikipediaEndpoint();

    public WikiRealisation queryRealisation(QueryEntity entity){

        Map<HeadlinePath, String> first = new HashMap<>();

        try {
            first = endpoint.query(
                    entity.getText()
            );
        } catch (Exception e) {
            System.out.println("Couldn't query "+entity.getText());
        }



        return new WikiRealisation(entity, first);
    }

}
