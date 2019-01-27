package de.upb.cracks.corpus.preprocess;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.jungblut.glove.GloveRandomAccessReader;
import de.jungblut.glove.impl.GloveBinaryRandomAccessReader;
import de.jungblut.math.DoubleVector;
import edu.stanford.nlp.simple.Sentence;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;

public class GloveFilter {

    private double delta;
    private GloveRandomAccessReader db;
    private List<String> beacons = new ArrayList<>();
    private Map<String, DoubleVector> beaconIndex = new HashMap<>();

    private Table<String, String, Double> cache;
    private Set<String> interested = new HashSet<>();



    public GloveFilter(double delta) {
        this.delta = delta;
    }


    private void initIndex(){

        if(db != null)return;

        try {
            db = new GloveBinaryRandomAccessReader(Paths.get(GloveFilter.class.getClassLoader().getResource(
                    "glove"
            ).toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

    public void setBeacons(List<String> beacons) {
        this.beacons = beacons;
        initIndex();
        cache = HashBasedTable.create();
        for(String lemma: beacons){
            try {
                beaconIndex.put(lemma, db.get(lemma));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private double getSimilarity(String lemma, String beacon) throws IOException {
        if(!db.contains(lemma) || !beaconIndex.containsKey(beacon))return 0;

        DoubleVector vector1 = db.get(lemma);
        DoubleVector vector2 = beaconIndex.get(beacon);

        double comb = 0;
        double A = 0;
        double B = 0;

        for(int i = 0; i < Math.min(vector1.getLength(), vector2.getLength()); i++){
            comb += vector1.get(i) * vector2.get(i);
            A += vector1.get(i) * vector1.get(i);
            B += vector2.get(i) * vector2.get(i);
        }

        return comb / (Math.sqrt(A) * Math.sqrt(B));
    }

    private double getSimilarityCached(String lemma1, String lemma2){
        if(!cache.contains(lemma1, lemma2)){
            try {
                double s = getSimilarity(lemma1, lemma2);
                if(s >= delta){
                    interested.add(lemma1);
                }
                cache.put(lemma1, lemma2, s);
            } catch (IOException e) {
                cache.put(lemma1, lemma2, 0.0);
            }
        }
        return cache.get(lemma1, lemma2);
    }

    public double getScore(Sentence sentence){

        boolean hasUnseen = false;
        boolean isInteresting = false;

        for(String lemma : sentence.lemmas()){
            if(db.contains(lemma)){
                if(cache.containsRow(lemma)){
                    if(interested.contains(lemma)){
                        isInteresting = true;
                        break;
                    }
                }else{
                    hasUnseen = true;
                    break;
                }
            }
        }

        if(isInteresting || hasUnseen){
            double score = 0.0;

            for(String lemma: sentence.lemmas()){
                for(String beacon : beacons) {
                    score = Math.max(score, getSimilarityCached(lemma, beacon));
                }
            }

            return score;
        }

        return 0;
    }

    public boolean filter(Sentence sentence){
        return getScore(sentence) > delta;
    }

}
