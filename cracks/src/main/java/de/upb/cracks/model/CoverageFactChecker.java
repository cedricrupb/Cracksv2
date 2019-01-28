package de.upb.cracks.model;

import de.upb.cracks.corpus.FactQueryEndpoint;
import de.upb.cracks.io.FactCheckQueryEntity;
import de.upb.cracks.io.FactCheckTrainEntity;
import de.upb.cracks.rules.IFactQuery;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class CoverageFactChecker implements  IFactChecker {


    @Override
    public IFactCheckModel train(List<FactCheckTrainEntity> trainEntities) {
        return new CoverageFactCheckModel();
    }

    @Override
    public IFactCheckModel load(Path path) throws IOException {
        return new CoverageFactCheckModel();
    }

    private class CoverageFactCheckModel implements IFactCheckModel {
        FactQueryEndpoint endpoint = new FactQueryEndpoint();

        @Override
        public double eval(FactCheckQueryEntity queryEntity) {

            Optional<IFactQuery> queryOpt = FactUtil.matcher().match(queryEntity.getQuery());

            if (queryOpt.isPresent()) {
                IFactQuery query = queryOpt.get();

                if(!endpoint.getSentences(query).isEmpty()){
                    return 1;
                }

            }

            return Math.random();
        }

        @Override
        public void store(Path p) throws IOException {
            return;
        }
    }
}
