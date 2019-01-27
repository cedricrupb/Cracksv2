package de.upb.cracks.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaiveBayes {

    private Map<String, Integer> vocab = new HashMap<>();
    private Map<String, Integer> inClazzVocab = new HashMap<>();

    private int totalInClazzWords = 0;
    private int totalInClazz = 0;

    private int totalWords = 0;
    private int totalDocs = 0;

    private int k = 1;

    public void learn(List<String> features, boolean inClass){
        totalDocs += 1;
        totalInClazz += inClass?1:0;

        for(String f : features){

            if(!vocab.containsKey(f)){
                vocab.put(f, 0);
            }
            vocab.put(f, vocab.get(f) + 1);

            totalWords++;

            if(inClass){

                if(!inClazzVocab.containsKey(f)){
                    inClazzVocab.put(f, 0);
                }
                inClazzVocab.put(f, inClazzVocab.get(f) + 1);

                totalInClazzWords++;

            }

        }
    }


    public double predict(List<String> features){

        double prob = 0.0;
        double counterProb = 0.0;

        for(String f : features){

            int occ = k;

            if(inClazzVocab.containsKey(f)){
                occ += inClazzVocab.get(f);
            }

            double wordProb = occ / (double)(totalInClazzWords + k * vocab.size());
            prob += Math.log(wordProb);

            int cOcc = 0;

            if(vocab.containsKey(f)){
                cOcc = vocab.get(f);

                if(inClazzVocab.containsKey(f)){
                    cOcc -= inClazzVocab.get(f);
                }
            }

            cOcc = cOcc + k;

            wordProb = cOcc / (double)((totalWords - totalInClazzWords) + k * vocab.size());
            counterProb += Math.log(wordProb);

        }

        prob += Math.log(totalInClazz/(double)totalDocs);
        counterProb += Math.log((totalDocs - totalInClazz) / (double)totalDocs);


        return Math.exp(prob)/(Math.exp(counterProb)+Math.exp(prob));
    }

}
