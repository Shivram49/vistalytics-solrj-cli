package TopicModel;

import cc.mallet.pipe.iterator.CsvIterator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class LDATrainer {
    private String inputFileName;
    private CsvIterator csvIterator;
    private int numTopics;
    private int numInterations;
    private LDATopicModellingAlgorithm model;
    public LDATrainer(String fileName,int numTopics,int numInterations) {
        this.inputFileName = fileName;
        this.model = new LDATopicModellingAlgorithm(numTopics,numInterations,"src/main/resources/ldamodel.ser");
    }
    public void loadDataToModel(int batchSize){
        List<List<String>> batches = new ArrayList<>();


        // Step 2: Use Apache Commons CSV to read the "content" column from the CSV
        try (FileReader reader = new FileReader(inputFileName);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            // Assuming "content" is the header name for the "content" column
            int rowCount = 0;
            List<String> contents = new ArrayList<>();
            for (CSVRecord csvRecord : csvParser) {
                String content = csvRecord.get("content");
                contents.add(content);
                model.addDocumentsToModelInstance(content);
                if(rowCount > 413920){
                    model.trainModel();
                    break;
                }
                rowCount++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("saving model");
        model.saveModel(model.getModel());
    }
}
