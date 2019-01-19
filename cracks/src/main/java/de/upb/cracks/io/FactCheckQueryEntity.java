package de.upb.cracks.io;

public class FactCheckQueryEntity {

    private long id;
    private String query;

    public FactCheckQueryEntity(long id, String query) {
        this.id = id;
        this.query = query;
    }

    public long getId() {
        return id;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public String toString(){
        return "("+id+", "+query+")";
    }

}
