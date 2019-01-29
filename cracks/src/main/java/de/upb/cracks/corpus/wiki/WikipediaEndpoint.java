package de.upb.cracks.corpus.wiki;

import com.bitplan.mediawiki.japi.Mediawiki;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikipediaEndpoint {

    private Mediawiki wiki;

    private void init() {
        if (wiki != null) return;
        try {
            wiki = new Mediawiki("https://en.wikipedia.org");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String un = pattern.matcher(temp).replaceAll("");

        un = un.replace("ø", "o");

        return un;
    }

    private String implQueryRAW(String title) throws Exception {
        init();
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
        }else{
            throw new NoSuchElementException("Didn't find "+title+" on Wikipedia.");
        }

        text = "== Abstract == \n"+text;

        return text;
    }

    public String queryRAW(String title) throws Exception {
        title = title.replaceAll(" ", "_");
        title = this.unAccent(title);

        Path path = Paths.get("cache/"+title+".txt");

        if(Files.exists(path)){
            String s = FileUtils.readFileToString(
                    path.toFile()
            );
            return s;
        }

        String text = this.implQueryRAW(title);

        Path p = path.getParent();

        if(!Files.exists(p)){
            Files.createDirectory(p);
        }

        Files.createFile(path);
        BufferedWriter writer = Files.newBufferedWriter(path);
        writer.write(text);
        writer.close();

        return text;
    }

    private int determineHeadlineDepth(String text, int pos){
        if(text.charAt(pos) != '=')return 0;

        int depth = 0;
        int open = 1;
        int state = 0;

        for(int p = pos + 1; p < text.length() && open > 0; p++){
            if(state == 0){
                if(text.charAt(p) == '='){
                    open++;
                    depth++;
                }else{
                    state = 1;
                }
            }else if(state == 1){
                if(text.charAt(p) == '='){
                    state = 2;
                }
            }else if(state == 2){
                if(text.charAt(p) == '='){
                    open--;
                }else{
                    return 0;
                }
            }
        }

        return depth;
    }

    private List<String> determineSplit(String text){
        Pattern pattern = Pattern.compile("==+([^=]+)+==+");
        Matcher matcher = pattern.matcher(text);

        List<String> out = new ArrayList<>();

        if(matcher.find()){
            int start = 0;

            while(matcher.find(start)){
                if(matcher.start() - start > 0)
                    out.add(text.substring(start, matcher.start()));
                out.add(text.substring(matcher.start(), matcher.end()));
                start = matcher.end();
            }

            if(start < text.length())
                out.add(text.substring(start));

        }else{
            out.add(text);
        }

        return out;
    }

    private String clean(String text){
        text = text.trim();

        Pattern pattern = Pattern.compile("[a-z]\\.[A-Z]");
        Matcher matcher = pattern.matcher(text);

        String nText = "";

        int i = 0;
        while (matcher.find(i)){
            nText = nText + text.substring(i, matcher.start()+1);
            nText = nText +".\n";
            nText = nText + text.substring(matcher.end()-1, matcher.end());
            i = matcher.end();
        }

        if(i < text.length())
            nText = nText + text.substring(i, text.length());

        nText = nText.replaceAll("\\.(\\s*)\\.", ".");

        return nText;
    }


    private Map<HeadlinePath, String> parse(String text){

        Map<HeadlinePath, String> sections = new HashMap<>();
        List<String> splits = determineSplit(text);

        Pattern pattern = Pattern.compile("==+([^=]+)==+");

        HeadlinePath base = new HeadlinePath();
        int depth = 0;

        for(String s : splits){

            if(s.startsWith("=")){
                int d = determineHeadlineDepth(s, 0);

                if(d > 0){
                    Matcher matcher = pattern.matcher(s);

                    if(matcher.find()){
                        String title = matcher.group(1).trim();

                        while (depth >= d){
                            base = base.suspendLast();
                            depth --;
                        }

                        base = base.append(title);
                        depth ++;
                        continue;
                    }

                }

            }

            String txt = clean(s);

            if(txt.isEmpty())
                continue;

            if(!sections.containsKey(base)){
                sections.put(base, "");
            }

            sections.put(base, sections.get(base) + txt);
        }


        return sections;
    }

    public Map<HeadlinePath, String> query(String title) throws Exception {
        return parse(queryRAW(title));
    }


}
