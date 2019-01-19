package de.upb.cracks.rules;

public interface IFactQuery {

    public String getIntention();

    public String getWikipediaArticle();

    public QueryEntity getFirstEntity();

    public QueryEntity getSecondEntity();

}
