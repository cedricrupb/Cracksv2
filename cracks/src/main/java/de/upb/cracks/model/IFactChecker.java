package de.upb.cracks.model;

import de.upb.cracks.io.FactCheckTrainEntity;

import java.util.List;

public interface IFactChecker {

    public IFactCheckModel train(List<FactCheckTrainEntity> trainEntities);

}
