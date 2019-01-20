package de.upb.cracks.model;


import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Stopwords {

    private static Set<String> EN;

    public static Set<String> loadStopwords(){
        if(EN != null)
            return EN;

        InputStream stream = Stopwords.class.getClassLoader().getResourceAsStream("stopwords.list");

        EN = new HashSet<>();
        Scanner scanner = new Scanner(stream);

        while (scanner.hasNextLine()){
            EN.add(scanner.nextLine());
        }

        return EN;
    }


}
