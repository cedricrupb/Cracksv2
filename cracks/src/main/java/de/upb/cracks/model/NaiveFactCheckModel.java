package de.upb.cracks.model;

import de.upb.cracks.corpus.FactQueryEndpoint;
import de.upb.cracks.corpus.WikiSentence;
import de.upb.cracks.corpus.preprocess.FeatureExtractor;
import de.upb.cracks.io.FactCheckQueryEntity;
import de.upb.cracks.rules.IFactQuery;

import java.util.Map;
import java.util.Optional;

public class NaiveFactCheckModel implements IFactCheckModel{

    private FactQueryEndpoint endpoint = new FactQueryEndpoint();
    private Map<String, NaiveBayes> naive;

    public NaiveFactCheckModel(Map<String, NaiveBayes> naive) {
        this.naive = naive;
    }

    @Override
    public double eval(FactCheckQueryEntity queryEntity) {

        Optional<IFactQuery> queryOptional = FactUtil.matcher().match(queryEntity.getQuery());

        if(queryOptional.isPresent()){
            IFactQuery query = queryOptional.get();

            if(naive.containsKey(query.getIntention())){

                NaiveBayes bayes = naive.get(query.getIntention());

                double max = 0.0;

                for(WikiSentence sentence : endpoint.getSentences(query)){
                    double score = bayes.predict(
                            FeatureExtractor.extract(sentence.window(7).getSentence())
                    );

                    max = Math.max(max, score);

                }


                return max;
            }


        }

        return 0;
    }
}
