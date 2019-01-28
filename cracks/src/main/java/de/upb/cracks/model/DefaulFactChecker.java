package de.upb.cracks.model;

import de.upb.cracks.io.FactCheckQueryEntity;
import de.upb.cracks.io.FactCheckTrainEntity;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public class DefaulFactChecker implements IFactChecker {
    @Override
    public IFactCheckModel train(List<FactCheckTrainEntity> trainEntities) {
        return new DefaultFactCheckModel();
    }

    @Override
    public IFactCheckModel load(Path path) throws IOException {
        return new DefaultFactCheckModel();
    }

    private class DefaultFactCheckModel implements IFactCheckModel{

        Random random = new Random();

        @Override
        public double eval(FactCheckQueryEntity queryEntity) {
            return random.nextDouble();
        }

        @Override
        public void store(Path p) throws IOException {
            return;
        }
    }
}
