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


    public FactCheckTSVParser(InputStream stream) {
        this.stream = stream;
    }


    public List<FactCheckTrainEntity> parse(){

        TsvParserSettings settings = new TsvParserSettings();

        settings.setHeaderExtractionEnabled(true);

        TSVObjectProcessor processor = new TSVObjectProcessor();
        settings.setProcessor(processor);


        TsvParser parser = new TsvParser(settings);
        parser.parse(stream, Charset.forName("ISO-8859-1"));

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

            if(objects[2] instanceof Double){
                label = (Double) objects[2];
            }

            entities.add(new FactCheckTrainEntity(id, query, label));

        }
    }



}
