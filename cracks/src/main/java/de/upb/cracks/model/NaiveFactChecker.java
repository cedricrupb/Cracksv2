package de.upb.cracks.model;

import de.upb.cracks.corpus.FactQueryEndpoint;
import de.upb.cracks.corpus.WikiSentence;
import de.upb.cracks.corpus.preprocess.FeatureExtractor;
import de.upb.cracks.io.FactCheckQueryEntity;
import de.upb.cracks.io.FactCheckTrainEntity;
import de.upb.cracks.rules.IFactQuery;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NaiveFactChecker implements IFactChecker {

    private FactQueryEndpoint endpoint = new FactQueryEndpoint();
    private MaxOccurrenceSelector selector;

    private MaxOccurrenceSelector initSelector(List<FactCheckTrainEntity> trainEntities){
        if(selector != null)return selector;

        selector = MaxOccurrenceSelector.load("maxSelector.json");

        if(selector != null)
            return selector;

        selector = new MaxOccurrenceSelector();

        FactQueryEndpoint endpoint = new FactQueryEndpoint();

        int i = 0;

        for(FactCheckTrainEntity t : trainEntities) {

            System.out.println("Process: " + (i++) + "/" + trainEntities.size());

            Optional<IFactQuery> queryOptional = FactUtil.matcher().match(t.getQuery());

            if (queryOptional.isPresent()) {

                IFactQuery query = queryOptional.get();

                List<WikiSentence> sentences = endpoint.getSentences(query);

                if (t.getLabel() > 0) {

                    String intention = query.getIntention();

                    for (WikiSentence sentence : sentences)
                        selector.train(intention, FeatureExtractor.extract(sentence.window(7).getSentence()));


                }


            }

        }

        selector.store("maxSelector.json");

        return selector;
    }

    private WikiSentence getSentence(FactCheckQueryEntity entity){

        String cachePath = "sentence_cache/"+entity.getId()+".json";
        Path p = Paths.get(cachePath);

        if(Files.exists(p)){
            return WikiSentence.load(cachePath);
        }

        WikiSentence max = null;

        Optional<IFactQuery> queryOptional = FactUtil.matcher().match(entity.getQuery());

        if(queryOptional.isPresent()) {

            IFactQuery query = queryOptional.get();

            double maxScore = -1;

            for (WikiSentence sentence : endpoint.getSentences(query)) {
                double score = selector.score(query.getIntention(), FeatureExtractor.extract(sentence.getSentence()));

                if (score > maxScore) {
                    maxScore = score;
                    max = sentence;
                }

            }


        }

        if(max != null)
            max.store(cachePath);

        return max;
    }

    @Override
    public IFactCheckModel train(List<FactCheckTrainEntity> trainEntities) {
        initSelector(trainEntities);

        Map<String, NaiveBayes> map = new HashMap<>();

        for(FactCheckTrainEntity trainEntity : trainEntities){

            Optional<IFactQuery> queryOptional = FactUtil.matcher().match(trainEntity.getQuery());

            if(queryOptional.isPresent()) {
                IFactQuery query = queryOptional.get();

                if(!map.containsKey(query.getIntention())){
                    map.put(query.getIntention(), new NaiveBayes());
                }

                NaiveBayes bayes = map.get(query.getIntention());

                WikiSentence sentence = getSentence(trainEntity);

                if(sentence == null)
                    continue;

                bayes.learn(
                        FeatureExtractor.extract(
                                sentence.window(7).getSentence()),
                        trainEntity.getLabel() > 0
                );

            }

        }


        return new NaiveFactCheckModel(map);
    }
}
