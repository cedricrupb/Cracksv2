package de.upb.cracks.rules;

public class QueryEntity {

    private String text;
    private String type;
    private boolean searchFor;

    public QueryEntity(String text, String type, boolean searchFor) {
        this.text = text;
        this.type = type;
        this.searchFor = searchFor;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public boolean isSearchFor() {
        return searchFor;
    }


}
