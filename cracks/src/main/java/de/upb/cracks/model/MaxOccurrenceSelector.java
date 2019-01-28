package de.upb.cracks.model;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaxOccurrenceSelector {

    private Map<String, Map<String, Integer>> occurrence = new HashMap<>();

    public void train(String intention, List<String> features){

        if(!occurrence.containsKey(intention)){
            occurrence.put(intention, new HashMap<>());
        }

        Map<String, Integer> occ = occurrence.get(intention);

        for(String s : features){

            if(!occ.containsKey(s)){
                occ.put(s, 0);
            }

            occ.put(s, occ.get(s) + 1);
        }

    }


    public double score(String intention, List<String> features){

        if(!occurrence.containsKey(intention)){
            occurrence.put(intention, new HashMap<>());
        }

        Map<String, Integer> occ = occurrence.get(intention);

        int c = 0;

        for(String f: features){
            if(occ.containsKey(f))
                c = Math.max(c, occ.get(f));
        }

        return c;
    }

    public boolean store(String path){

        Path p = Paths.get(path);

        Gson gson = new Gson();
        try {
            BufferedWriter writer = Files.newBufferedWriter(p);
            gson.toJson(this, writer);
            writer.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static MaxOccurrenceSelector load(String path){
        Path p = Paths.get(path);

        Gson gson = new Gson();
        try {
            return gson.fromJson(
                    Files.newBufferedReader(p),
                    MaxOccurrenceSelector.class
            );
        } catch (IOException e) {
        }
        return null;
    }

}
