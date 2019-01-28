package de.upb.cracks.model;

import de.upb.cracks.io.FactCheckTrainEntity;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface IFactChecker {

    public IFactCheckModel train(List<FactCheckTrainEntity> trainEntities);

    public IFactCheckModel load(Path path) throws IOException;

}
