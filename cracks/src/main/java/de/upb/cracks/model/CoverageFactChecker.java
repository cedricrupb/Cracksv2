package de.upb.cracks.model;

import de.upb.cracks.corpus.CoverageTest;
import de.upb.cracks.corpus.FactQueryEndpoint;
import de.upb.cracks.corpus.WikiSentence;
import de.upb.cracks.io.FactCheckQueryEntity;
import de.upb.cracks.io.FactCheckTrainEntity;
import de.upb.cracks.rules.IFactQuery;
import de.upb.cracks.rules.QueryEntityMatcher;
import de.upb.cracks.rules.RuleMatcher;
import de.upb.cracks.rules.RuleParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CoverageFactChecker implements  IFactChecker {


    @Override
    public IFactCheckModel train(List<FactCheckTrainEntity> trainEntities) {
        return new IFactCheckModel() {

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

                return 0;
            }
        };
    }
}
