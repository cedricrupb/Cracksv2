package de.upb.cracks.corpus;

import de.upb.cracks.corpus.wiki.HeadlinePath;
import de.upb.cracks.rules.IFactQuery;
import de.upb.cracks.rules.QueryEntity;
import de.upb.cracks.rules.RuleMatch;

import java.util.*;

public class WikiRealisation {

    private QueryEntity entity;
    private Map<HeadlinePath, String> content = new HashMap<>();

    WikiRealisation(QueryEntity entity, Map<HeadlinePath, String> content) {
        this.entity = entity;
        this.content = content;
    }

    public QueryEntity getEntity() {
        return entity;
    }


    public Set<String> getSections(){

        Set<String> keys = new HashSet<>();

        for(HeadlinePath p: content.keySet())
            keys.add(p.toString());

        return keys;
    }

    public Optional<WikiSection> getSection(String path){
        HeadlinePath p = new HeadlinePath(path);

        if(content.containsKey(p)){
            return Optional.of(new WikiSection(entity, content.get(p)));
        }

        return Optional.empty();
    }


}
