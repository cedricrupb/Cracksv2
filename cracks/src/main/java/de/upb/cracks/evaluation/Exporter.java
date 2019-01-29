package de.upb.cracks.evaluation;

import de.upb.cracks.io.FactCheckQueryEntity;
import de.upb.cracks.io.FactCheckTSVParser;
import de.upb.cracks.io.FactCheckTrainEntity;
import de.upb.cracks.model.IFactCheckModel;
import de.upb.cracks.model.IFactChecker;
import de.upb.cracks.model.NaiveFactCheckModel;
import de.upb.cracks.model.NaiveFactChecker;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Exporter {

    private List<FactCheckTrainEntity> trainData;

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

    private List<FactCheckQueryEntity> initTestData(String path) throws FileNotFoundException {

        InputStream stream;
        if(path == null){
            stream = Evaluator.class.getClassLoader().getResourceAsStream("test.tsv");
        }else{
            stream = new FileInputStream(
                    new File(path)
            );
        }

        return new FactCheckTSVParser(stream).parseTest();
    }


    public void export(String path) throws FileNotFoundException {
        initTrainData(null);

        IFactChecker check = new NaiveFactChecker();
        IFactCheckModel model = check.train(trainData);

        Path p = Paths.get(path);

        for(FactCheckQueryEntity query : initTestData(null)){
            double score = model.eval(query);

            try{
                if(!Files.exists(p)){
                    Files.createFile(p);
                }

                BufferedWriter writer = Files.newBufferedWriter(p, StandardOpenOption.APPEND);

                writer.write(
                        String.format("<http://swc2017.aksw.org/task2/dataset/%d> <http://swc2017.aksw.org/hasTruthValue> \"%f\"^^<http://www.w3.org/2001/XMLSchema#double> .\n",
                                query.getId(), score)
                );

                writer.close();

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    public static void main(String[] args) throws FileNotFoundException {
        new Exporter().export("result_absolute.ttl");
    }


}
