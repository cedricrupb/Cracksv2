package de.upb.cracks.io;

public class FactCheckTrainEntity extends FactCheckQueryEntity {

    private double label;

    public FactCheckTrainEntity(long id, String query, double label) {
        super(id, query);
        this.label = label;
    }

    public double getLabel() {
        return label;
    }

    @Override
    public String toString(){
        return "("+this.getId()+", "+this.getQuery()+", "+this.getLabel()+")";
    }
}
