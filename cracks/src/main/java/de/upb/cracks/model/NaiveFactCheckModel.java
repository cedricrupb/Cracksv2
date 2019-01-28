package de.upb.cracks.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.upb.cracks.corpus.FactQueryEndpoint;
import de.upb.cracks.corpus.WikiSentence;
import de.upb.cracks.corpus.preprocess.FeatureExtractor;
import de.upb.cracks.io.FactCheckQueryEntity;
import de.upb.cracks.rules.IFactQuery;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    @Override
    public void store(Path p) throws IOException {

        p = p.toAbsolutePath();

        if(!Files.exists(p.getParent())){
            Files.createDirectory(p.getParent());
        }

        if(!Files.exists(p)){
            Files.createFile(p);
        }

        BufferedWriter writer = Files.newBufferedWriter(p);
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        Gson gson = builder.create();
        gson.toJson(naive, writer);
        writer.close();

    }
}
