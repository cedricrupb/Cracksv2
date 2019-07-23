package de.upb.cracks.corpus.wiki;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HeadlinePath {

    private List<String> path;

    private static List<String> parse(String text){
        String[] split = text.split("\\.");
        return Arrays.asList(split);
    }

    public HeadlinePath(List<String> path) {
        this.path = path;
    }

    public HeadlinePath(String path){
        this(parse(path));
    }

    public HeadlinePath() {
        this(new ArrayList<>());
    }

    public HeadlinePath append(String headline){
        List<String> p = new ArrayList<>(path);
        p.add(headline);

        return new HeadlinePath(p);
    }

    public HeadlinePath suspendLast(){
        List<String> p = new ArrayList<>(path);

        if(!p.isEmpty()){
            p.remove(p.size() - 1);
        }

        return new HeadlinePath(p);
    }

    @Override
    public String toString(){
        if(path.isEmpty())
            return "EMPTY";

        StringBuilder txtBuilder = new StringBuilder(path.get(0));

        for(int i = 1; i < path.size(); i++){
            txtBuilder.append(".").append(path.get(i));
        }

        return txtBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeadlinePath that = (HeadlinePath) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

}
