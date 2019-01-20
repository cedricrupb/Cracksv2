package de.upb.cracks.model;

import de.upb.cracks.rules.IFactQuery;
import de.upb.cracks.wiki.WikiSection;
import de.upb.cracks.wiki.WikiSource;

import java.util.HashMap;
import java.util.Map;

public class FactBackend {

    private WikiSource source = new WikiSource();

    private Map<String, WikiSection> getResult(String title) throws Exception {
        Map<String, WikiSection> map = new HashMap<>();

        for(WikiSection section: source.queryByName(title)){
            map.put(section.getTitle(), section);
        }

        return map;
    }


    public FactBackendResult query(IFactQuery query) throws Exception {

        Map<String, WikiSection> first = new HashMap<>();

        if(query.getFirstEntity().isSearchFor()){
            first = getResult(query.getFirstEntity().getText());
        }

        Map<String, WikiSection> second = new HashMap<>();
        if(query.getSecondEntity().isSearchFor()){
            second = getResult(query.getSecondEntity().getText());
        }

        return new FactBackendResult(first, second);
    }

}
