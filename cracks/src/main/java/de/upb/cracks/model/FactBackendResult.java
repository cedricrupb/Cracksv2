package de.upb.cracks.model;

import de.upb.cracks.wiki.WikiSection;

import java.util.Map;

public class FactBackendResult {

    private Map<String, WikiSection> firstResult;
    private Map<String, WikiSection> secondResult;

    public FactBackendResult(Map<String, WikiSection> firstResult, Map<String, WikiSection> secondResult) {
        this.firstResult = firstResult;
        this.secondResult = secondResult;
    }

    public Map<String, WikiSection> getFirstResult() {
        return firstResult;
    }

    public Map<String, WikiSection> getSecondResult() {
        return secondResult;
    }

}
