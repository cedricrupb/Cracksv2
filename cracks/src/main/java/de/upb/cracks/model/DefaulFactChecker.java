package de.upb.cracks.model;

import de.upb.cracks.io.FactCheckQueryEntity;
import de.upb.cracks.io.FactCheckTrainEntity;

import java.util.List;
import java.util.Random;

public class DefaulFactChecker implements IFactChecker {
    @Override
    public IFactCheckModel train(List<FactCheckTrainEntity> trainEntities) {
        return new IFactCheckModel(){

            Random random = new Random();

            @Override
            public double eval(FactCheckQueryEntity queryEntity) {
                return random.nextDouble();
            }
        };
    }
}
