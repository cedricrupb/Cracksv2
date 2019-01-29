package de.upb.cracks.evaluation;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import de.upb.cracks.io.FactCheckQueryEntity;
import de.upb.cracks.io.FactCheckTSVParser;
import de.upb.cracks.io.FactCheckTrainEntity;
import de.upb.cracks.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class Evaluator {

    private List<FactCheckTrainEntity> trainData;
    private String path;
    private int k;

    public Evaluator(int k, String path) {
        this.path = path;
        this.k = k;
    }

    public Evaluator(int k){
        this(k, null);
    }

    public Evaluator(){
        this(10);
    }

    private void initTrainData(String path) throws FileNotFoundException {
        if(trainData != null)return;

        InputStream stream;
        if(path == null){
            stream = Evaluator.class.getClassLoader().getResourceAsStream("train.tsv");
        }else{
            stream = new FileInputStream(
                    new File(path)
            );
        }

        trainData = new FactCheckTSVParser(stream).parse();
    }

    private List<Fold> crossfold() throws FileNotFoundException {
        initTrainData(path);

        Iterable<List<FactCheckTrainEntity>> subSets = Iterables.partition(trainData, (trainData.size()/k + 1));
        List<List<FactCheckTrainEntity>> subSetList = Lists.newArrayList(subSets);

        List<Fold> out = new ArrayList<>();

        for(int i = 0; i < subSetList.size(); i++){
            List<FactCheckTrainEntity> train = new ArrayList<>();

            for(int j = 0; j < subSetList.size(); j++){
                if(j != i)train.addAll(subSetList.get(j));
            }


            out.add(new Fold(
                    train, subSetList.get(i)
            ));
        }

        return out;
    }


    private double auc(List<Result> list){

        int goodRank = 0;
        int ranks = 0;

        for(int i = 0; i < list.size(); i++){
            for(int j = i+1; j < list.size(); j++){
                Result lI = list.get(i);
                Result lJ = list.get(j);

                if(lI.truth != lJ.truth) {
                    if ((lI.truth > lJ.truth && lI.value > lJ.value) || (lI.truth < lJ.truth) && (lI.value < lJ.value)) {
                        goodRank++;
                    }

                    ranks++;
                }
            }
        }

        if(ranks == 0){
            return 0;
        }

        return (double)goodRank / (double)ranks;
    }

    private double precision(List<Result> list){

        int tp = 0;
        int fp = 0;

        for(int i = 0; i < list.size(); i++){
            for(int j = i+1; j < list.size(); j++){
                Result lI = list.get(i);
                Result lJ = list.get(j);

                if(lI.truth != lJ.truth) {
                    if(lI.value > lJ.value){
                        if(lI.truth > lJ.truth){
                            tp ++;
                        }else{
                            fp ++;
                        }
                    }
                }
            }
        }

        return tp / (double)(tp + fp);

    }

    private double recall(List<Result> list){

        int tp = 0;
        int fn = 0;

        for(int i = 0; i < list.size(); i++){
            for(int j = i+1; j < list.size(); j++){
                Result lI = list.get(i);
                Result lJ = list.get(j);

                if(lI.truth != lJ.truth) {
                    if(lI.truth > lJ.truth){
                        if(lI.value > lJ.value){
                            tp ++;
                        }else{
                            fn ++;
                        }
                    }
                }
            }
        }

        return tp / (double)(tp + fn);

    }

    private void printMeanStd(List<Double> value, String measure){
        double mean = 0.0;
        for(double d: value)
            mean += 1.0/value.size() * d;

        double std = 0.0;
        for(double d: value)
            std += 1.0/value.size() * (d - mean) * (d - mean);

        System.out.println("Mean "+measure+": "+mean+"(+- "+std+" )");

    }


    public EvaluationResult evaluate(IFactChecker checker){

        try {

            List<Double> aucs = new ArrayList<>();
            List<Double> precision = new ArrayList<>();
            List<Double> recall = new ArrayList<>();

            for(Fold fold : crossfold()){

                IFactCheckModel model = checker.train(fold.getTrain());

                List<Result> results = new ArrayList<>();

                for(FactCheckTrainEntity q  : fold.getQuery()){
                    double val = model.eval(q);
                    results.add(new Result(val, q.getLabel()));
                }

                aucs.add(auc(results));
                precision.add(precision(results));
                recall.add(recall(results));

                System.out.println("AUC: "+aucs.get(aucs.size() - 1));

            }

            printMeanStd(aucs, "AUC");
            printMeanStd(precision, "Precision");
            printMeanStd(recall, "recall");

            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class Result {

        private double value;
        private  double truth;

        public Result(double value, double truth) {
            this.value = value;
            this.truth = truth;
        }

    }

    public static void main(String[] args){

        Evaluator evaluator = new Evaluator();
        evaluator.evaluate(new NaiveFactChecker());

    }


}
