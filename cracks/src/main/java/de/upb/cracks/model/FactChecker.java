package de.upb.cracks.model;

import de.upb.cracks.evaluation.Evaluator;
import de.upb.cracks.io.FactCheckQueryEntity;
import de.upb.cracks.io.FactCheckTSVParser;
import de.upb.cracks.io.FactCheckTrainEntity;
import de.upb.cracks.rules.IFactQuery;
import de.upb.cracks.rules.QueryEntityMatcher;
import de.upb.cracks.rules.RuleMatcher;
import de.upb.cracks.rules.RuleParser;
import de.upb.cracks.wiki.WikiSection;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.Token;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FactChecker implements IFactChecker {

    private QueryEntityMatcher matcher;
    private FactBackend backend = new FactBackend();

    private QueryEntityMatcher matcher(){
        if(matcher == null){

            BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                FactChecker.class.getClassLoader().getResourceAsStream("RuleSet.list")));


            String s = reader.lines().reduce((x, y) -> x+"\n"+y).get();
            RuleMatcher m = new RuleParser().compile(s);

            matcher = new QueryEntityMatcher(m);

        }
        return matcher;
    }

    private List<String> preprocessSearch(String query){

        query = query.replaceAll("\\(.*\\)", "");
        query = query.trim();

        String[] search = query.split(" ");

        return Arrays.asList(search);
    }

    private Map<String, WikiSection> sectionCoverage(Map<String, WikiSection> map, List<String> search){

        Map<String, WikiSection> out = new HashMap<>();

        for(WikiSection section : map.values()){

            boolean found = false;

            for(String s : search){
                if(section.getText().contains(s)){
                    found = true;
                    break;
                }
            }

            if(found)out.put(section.getTitle(), section);
        }


        return out;
    }


    private List<Sentence> sentenceCoverage(String text, List<String> search){

        List<Sentence> sentences = new ArrayList<>();

        Document document = new Document(text);

        for(Sentence sentence : document.sentences()){
            boolean found = false;

            for(String s : search){
                if(sentence.text().contains(s)){
                    found = true;
                    break;
                }
            }

            if(found)sentences.add(sentence);
        }


        return sentences;
    }

    private List<Sentence> preprocess(FactCheckQueryEntity queryEntity){

        List<Sentence> sentences = new ArrayList<>();

        Optional<IFactQuery> queryOpt = matcher().match(queryEntity.getQuery());

        if(queryOpt.isPresent()){
            IFactQuery query = queryOpt.get();
            try {
                FactBackendResult result = backend.query(query);

                if(query.getFirstEntity().isSearchFor()){

                    List<String> search = preprocessSearch(query.getSecondEntity().getText());

                    Map<String, WikiSection> covered = sectionCoverage(result.getFirstResult(), search);

                    for(WikiSection section: covered.values()){
                        sentences.addAll(sentenceCoverage(section.getText(), search));
                    }

                }

                if(query.getSecondEntity().isSearchFor()){

                    List<String> search = preprocessSearch(query.getFirstEntity().getText());

                    Map<String, WikiSection> covered = sectionCoverage(result.getSecondResult(), search);

                    for(WikiSection section: covered.values()){
                        sentences.addAll(sentenceCoverage(section.getText(), search));
                    }

                }



            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sentences;
    }


    private List<String> sentenceProcess(Sentence sentence, IFactQuery query){
        Set<String> stopwords = Stopwords.loadStopwords();

        List<String> tokens = new ArrayList<>();

        boolean start = true;

        for(Token token : sentence.tokens()){
            String lemma = token.lemma();

            lemma = lemma.replaceAll("[^a-zA-Z]", "");

            if(!start && !lemma.toLowerCase().equals(lemma))
                continue;

            start = false;

            if(lemma.length() <= 3)
                continue;

            if(stopwords.contains(lemma))continue;

            if(query.getFirstEntity().getText().contains(token.word()))
                continue;

            if(query.getSecondEntity().getText().contains(token.word()))
                continue;

            tokens.add(lemma);
        }

        return tokens;
    }

    @Override
    public IFactCheckModel train(List<FactCheckTrainEntity> trainEntities) {

        int i = 0;

        for(FactCheckTrainEntity e: trainEntities) {
            System.out.println("Process: "+e.getQuery()+"("+(i++)+"/"+trainEntities.size()+")");
            Optional<IFactQuery> queryOpt = matcher().match(e.getQuery());

            if(queryOpt.isPresent()) {
                IFactQuery query = queryOpt.get();
                List<Sentence> sentences = preprocess(e);

                Path path = Paths.get(query.getIntention()+".txt");

                try {
                    if(!Files.exists(path)){
                        Files.createFile(path);
                    }

                    BufferedWriter writer = Files.newBufferedWriter(
                            path, StandardOpenOption.APPEND
                    );

                    writer.write(e.toString()+"\n");

                    for(Sentence sentence: sentences){
                        for(String lemma : sentenceProcess(sentence, query)){
                            writer.write(lemma+" ");
                        }
                        writer.write("\n");
                    }

                    writer.close();

                } catch (IOException e1) {
                    e1.printStackTrace();
                }


            }




        }

        return new IFactCheckModel(){

            Random random = new Random();

            @Override
            public double eval(FactCheckQueryEntity queryEntity) {
                return random.nextDouble();
            }
        };
    }

    private static List<FactCheckTrainEntity> initTrainData(String path) throws FileNotFoundException {

        InputStream stream;
        if(path == null){
            stream = Evaluator.class.getClassLoader().getResourceAsStream("train.tsv");
        }else{
            stream = new FileInputStream(
                    new File(path)
            );
        }

        return new FactCheckTSVParser(stream).parse();
    }

    public static void main(String[] args) throws FileNotFoundException {
        new FactChecker().train(initTrainData(null));
    }
}
