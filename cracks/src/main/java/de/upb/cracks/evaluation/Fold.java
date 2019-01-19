package de.upb.cracks.evaluation;

import de.upb.cracks.io.FactCheckQueryEntity;
import de.upb.cracks.io.FactCheckTrainEntity;

import java.util.List;

public class Fold {

    private List<FactCheckTrainEntity> train;
    private List<FactCheckTrainEntity> query;

    public Fold(List<FactCheckTrainEntity> train, List<FactCheckTrainEntity> query) {
        this.train = train;
        this.query = query;
    }

    public List<FactCheckTrainEntity> getTrain() {
        return train;
    }

    public List<FactCheckTrainEntity> getQuery() {
        return query;
    }

}
