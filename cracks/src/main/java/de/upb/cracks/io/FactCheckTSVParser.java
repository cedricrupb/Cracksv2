package de.upb.cracks.io;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.ObjectRowProcessor;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class FactCheckTSVParser {

    private InputStream stream;
    private String encoding;


    public FactCheckTSVParser(InputStream stream, String encoding) {
        this.stream = stream;
        this.encoding = encoding;
    }

    public FactCheckTSVParser(InputStream stream) {
        this(stream, null);
    }


    public List<FactCheckTrainEntity> parse(){

        TsvParserSettings settings = new TsvParserSettings();

        settings.setHeaderExtractionEnabled(true);

        TSVObjectProcessor processor = new TSVObjectProcessor();
        settings.setProcessor(processor);


        TsvParser parser = new TsvParser(settings);
        parser.parse(stream, Charset.forName(encoding==null?"latin1":encoding));

        return processor.entities;
    }

    public List<FactCheckQueryEntity> parseTest(){

        TsvParserSettings settings = new TsvParserSettings();

        settings.setHeaderExtractionEnabled(true);

        TSVTestObjectProcessor processor = new TSVTestObjectProcessor();
        settings.setProcessor(processor);


        TsvParser parser = new TsvParser(settings);
        parser.parse(stream, Charset.forName(encoding==null?"UTF-8":encoding));

        return processor.entities;
    }


    private class TSVObjectProcessor extends ObjectRowProcessor{

        List<FactCheckTrainEntity> entities = new ArrayList<>();

        @Override
        public void rowProcessed(Object[] objects, ParsingContext parsingContext) {
            long id = -1;
            String query = "";
            double label = -1;

            id = Long.parseLong((String) objects[0]);

            if(objects[1] instanceof String){
                query = (String) objects[1];
            }


            try {
                label = Double.parseDouble((String) objects[2]);
            } catch(NumberFormatException e){

            }

            entities.add(new FactCheckTrainEntity(id, query, label));

        }
    }

    private class TSVTestObjectProcessor extends ObjectRowProcessor{

        List<FactCheckQueryEntity> entities = new ArrayList<>();

        @Override
        public void rowProcessed(Object[] objects, ParsingContext parsingContext) {
            long id = -1;
            String query = "";
            double label = -1;

            id = Long.parseLong((String) objects[0]);

            if(objects[1] instanceof String){
                query = (String) objects[1];
            }


            entities.add(new FactCheckQueryEntity(id, query));

        }
    }



}
