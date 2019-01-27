package de.upb.cracks.corpus.wiki;

import com.bitplan.mediawiki.japi.Mediawiki;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiSource {

    private Mediawiki wiki;

    private ImmutableSet<String> ignoreSet = ImmutableSet.of(
            "Bibliography", "Audiobooks"
    );

    public WikiSource() {
        init();
    }

    private void init() {
        if (wiki != null) return;
        try {
            wiki = new Mediawiki("https://en.wikipedia.org");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
 
    //==
    //=== Super Headline
    //====
    public List<WikiSection> queryByName(String title) throws Exception {
        Pattern extract = Pattern.compile("\"extract\":\"(.*)\"");
        Matcher matcher;

        title = title.replaceAll(" ", "_");
        title = unAccent(title);

        String text = wiki.getActionResultText("query",
                "&format=json&prop=extracts&utf8=1&explaintext=1&&exsectionformat=wiki&redirects=1&titles=" + title,
                null, null, "json");
        matcher = extract.matcher(text);

        if (matcher.find()) {
            text = "{" + matcher.group() + "}";

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(text);

            if(element instanceof JsonObject){
                JsonObject obj = (JsonObject)element;
                text = obj.get("extract").getAsString();
            }
        }

        text = "== Abstract == "+text;

        Pattern headline = Pattern.compile("==+([^\\n=]+)==+");
        Matcher matcherHeadline = headline.matcher(text);

        List<Match> matches = new ArrayList<>();
        while (matcherHeadline.find()){

            String titleMatch = matcherHeadline.group(1).trim();

            if(titleMatch.contains("See also"))break;

            matches.add(new Match(titleMatch, matcherHeadline.start(0), matcherHeadline.end(0)));
        }


        List<WikiSection> sections = new ArrayList<>();

        for(int i = 0; i < matches.size(); i++){

            int end = text.length();

            if(i < matches.size() - 1){
                end = matches.get(i + 1).start;
            }

            Match m  = matches.get(i);

            String section = text.substring(m.end, end);

            if(!section.isEmpty())
                sections.add(new WikiSection(
                        m.title, section
                ));

        }


        List<WikiSection> txts = new ArrayList<>();

        for(WikiSection section: sections){
            if(!ignoreSet.contains(section.getTitle()))
                txts.add(section);
        }


        return txts;
    }
    
    public List<String> queryByID(String wikiTextID) throws Exception {
        Pattern extract = Pattern.compile("\"extract\":\"(.*)\"");
        Matcher matcher;

        String text = wiki.getActionResultText("query",
                "&format=json&prop=extracts&utf8=1&explaintext=1&&exsectionformat=wiki&pageids=" + wikiTextID,
                null, null, "json");
        matcher = extract.matcher(text);

        if (matcher.find()) {
            text = "{" + matcher.group() + "}";
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(text);

            if(element instanceof JsonObject){
                JsonObject obj = (JsonObject)element;
                text = obj.get("extract").getAsString();
            }
        }

        text = "== Abstract == "+text;

        Pattern headline = Pattern.compile("==+([^\\n=]+)==+");
        Matcher matcherHeadline = headline.matcher(text);

        List<Match> matches = new ArrayList<>();
        while (matcherHeadline.find()){

            String title = matcherHeadline.group(1).trim();

            if(title.contains("See also"))break;

            matches.add(new Match(title, matcherHeadline.start(0), matcherHeadline.end(0)));
        }


        List<WikiSection> sections = new ArrayList<>();

        for(int i = 0; i < matches.size(); i++){

            int end = text.length();

            if(i < matches.size() - 1){
                end = matches.get(i + 1).start;
            }

            Match m  = matches.get(i);

            String section = text.substring(m.end, end);

            if(!section.isEmpty())
                sections.add(new WikiSection(
                        m.title, section
                ));

        }


        List<String> txts = new ArrayList<>();

        for(WikiSection section: sections){
            if(!ignoreSet.contains(section.getTitle()))
                txts.add(section.getText());
        }


        return txts;
    }
  
    private class Match {

        public Match(String title, int start, int end) {
            this.title = title;
            this.start = start;
            this.end = end;
        }

        String title;
        int start;
        int end;

    }


}
