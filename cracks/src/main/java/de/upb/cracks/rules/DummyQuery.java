package de.upb.cracks.rules;

public class DummyQuery implements IFactQuery {

    //Pär Lagerkvist's award is Nobel Prize in Physics.

    @Override
    public String getIntention() {
        return "award";
    }

    @Override
    public String getWikipediaArticle() {
        return null;
    }

    @Override
    public QueryEntity getFirstEntity() {
        return new QueryEntity(
                "Pär Lagerkvist", "PERSON", true
        );
    }

    @Override
    public QueryEntity getSecondEntity() {
        return new QueryEntity(
               "Nobel Prize in Physics", "AWARD", false
        );
    }
}
