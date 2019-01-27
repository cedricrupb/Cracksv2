package de.upb.cracks.corpus;

import com.bitplan.mediawiki.japi.api.S;
import de.upb.cracks.corpus.preprocess.SentenceSelector;
import de.upb.cracks.evaluation.Evaluator;
import de.upb.cracks.io.FactCheckQueryEntity;
import de.upb.cracks.io.FactCheckTSVParser;
import de.upb.cracks.io.FactCheckTrainEntity;;
import de.upb.cracks.rules.IFactQuery;
import de.upb.cracks.rules.QueryEntityMatcher;
import de.upb.cracks.rules.RuleMatcher;
import de.upb.cracks.rules.RuleParser;

import java.io.*;
import java.util.*;

public class CoverageTest {

    private static QueryEntityMatcher matcher;

    private static QueryEntityMatcher matcher(){
        if(matcher == null){

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            CoverageTest.class.getClassLoader().getResourceAsStream("RuleSet.list")));


            String s = reader.lines().reduce((x, y) -> x+"\n"+y).get();
            RuleMatcher m = new RuleParser().compile(s);

            matcher = new QueryEntityMatcher(m);

        }
        return matcher;
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

        List<FactCheckTrainEntity> trainData = initTrainData(null);

        FactQueryEndpoint endpoint = new FactQueryEndpoint();

        Map<String, Integer> positive = new HashMap<>();
        Map<String, Integer> negative = new HashMap<>();
        positive.put("all", 0);
        negative.put("all", 0);
        int i = 0;

        for (FactCheckTrainEntity e : trainData) {

            System.out.println("Process: " + e.getQuery() + "(" + (i++) + "/" + trainData.size() + ")");
            Optional<IFactQuery> queryOpt = matcher().match(e.getQuery());

            List<WikiSentence> sentences = new ArrayList<>();

            if (queryOpt.isPresent()) {
                IFactQuery query = queryOpt.get();

                sentences = endpoint.getSentences(query);

                if (e.getLabel() > 0) {

                    positive.put("all", positive.get("all") + 1);

                    if(!positive.containsKey("all_"+query.getIntention())){
                        positive.put("all_"+query.getIntention(), 0);
                    }

                    positive.put("all_"+query.getIntention(), positive.get("all_"+query.getIntention()) + 1);

                    if(!sentences.isEmpty()){

                        if(!positive.containsKey(query.getIntention())){
                            positive.put(query.getIntention(), 0);
                        }

                        positive.put(query.getIntention(), positive.get(query.getIntention()) + 1);

                    }
                } else {
                    negative.put("all", negative.get("all") + 1);

                    if(!negative.containsKey("all_"+query.getIntention())){
                        negative.put("all_"+query.getIntention(), 0);
                    }

                    negative.put("all_"+query.getIntention(), negative.get("all_"+query.getIntention()) + 1);


                    if(!sentences.isEmpty()){

                        if(!negative.containsKey(query.getIntention())){
                            negative.put(query.getIntention(), 0);
                        }

                        negative.put(query.getIntention(), negative.get(query.getIntention()) + 1);

                    }
                }
            }


        }

        System.out.println("Coverage");
        System.out.println("positive: ");
        int sum = 0;
        for(Map.Entry<String, Integer> e: positive.entrySet()){
            if(e.getKey().startsWith("all"))continue;
            System.out.println("\t"+e.getKey()+": "+((e.getValue()/(double)positive.get("all_"+e.getKey()))));
            sum += e.getValue();
        }
        System.out.println("ALL: "+(sum/(double)positive.get("all")));
        System.out.println("negative: ");
        sum = 0;
            for(Map.Entry<String, Integer> e: negative.entrySet()){
                if(e.getKey().startsWith("all"))continue;
                System.out.println("\t"+e.getKey()+": "+((e.getValue()/(double)negative.get("all_"+e.getKey()))));
                sum += e.getValue();
            }
        System.out.println("ALL: "+(sum/(double)negative.get("all")));

    }


}
