package TopicModel;
import cc.mallet.pipe.*;
import cc.mallet.topics.*;
import cc.mallet.types.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.*;

public class LDATopicModellingAlgorithm {
    private int numTopics;
    private int numIterations;
    private ParallelTopicModel model;
    private InstanceList instances;
    private String modelFilePath;
    private static int messageId = 0;

    //All the processing steps in a pipe
    private Pipe[] getPreprocessingSteps() {
        ArrayList<Pipe> pipeList = new ArrayList<>();

        // Specify your preprocessing steps here
        pipeList.add(new CharSequenceLowercase());  // Convert text to lowercase
        pipeList.add(new CharSequence2TokenSequence());  // Tokenize the text

        //removing stopwords include subject
        ArrayList<String> stopwords = new ArrayList<>(); stopwords.add("subject");
        TokenSequenceRemoveStopwords removeStopwords = new TokenSequenceRemoveStopwords(false);
        removeStopwords.addStopWords(stopwords.toArray(new String[stopwords.size()]));
        pipeList.add(removeStopwords);
        pipeList.add(new TokenSequence2FeatureSequence());  // Convert token sequence to feature sequence
        return pipeList.toArray(new Pipe[0]);
    }

    //constructor
    public LDATopicModellingAlgorithm(int numTopics, int numIterations,String modelFilePath){
        this.numTopics = numTopics;
        this.numIterations = numIterations;
        model = new ParallelTopicModel(numTopics);
        instances = new InstanceList(new SerialPipes(getPreprocessingSteps()));
        this.modelFilePath = modelFilePath;
    }

    //add content from stream to the instance
    public void addDocumentsToModelInstance(String text){
        instances.addThruPipe(new Instance(text,null,null,null));
    }
    public void addDocumentsToModelInstance(List<String> batch){
        for(String text : batch)
            instances.addThruPipe(new Instance(text,null,null,null));
    }

    public void trainModel(){
        model.addInstances(instances);
        model.setNumThreads(4); // Set the number of threads for parallel training
        model.setNumIterations(numIterations);
        Alphabet alphabet;
        try {
            model.estimate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void topTopicsInCorpus(int topWords){
        List<String> topTopics = new ArrayList<>();
        // Set the instances for the model
        model.addInstances(instances);
        model.setNumThreads(4); // Set the number of threads for parallel training
        model.setNumIterations(numIterations);
        Alphabet alphabet;
        try {
            model.estimate();
        } catch (IOException e) {
            e.printStackTrace();
        }
        alphabet = model.getAlphabet();
        //Extract Topics
        ArrayList<TreeSet<IDSorter>> topicWords = model.getSortedWords();
        Alphabet modelAlphabet = model.getAlphabet();
        //topics ranked and added to result
        for (int topic = 0; topic < numTopics; topic++) {
            Iterator<IDSorter> iterator = topicWords.get(topic).iterator();

            System.out.println("Topic " + (topic + 1) + ":");
            int rank = 0;
            while (iterator.hasNext() && rank < topWords) {
                IDSorter idCountPair = iterator.next();
                int wordId = idCountPair.getID();
                String word = modelAlphabet.lookupObject(wordId).toString();
                double weight = idCountPair.getWeight();
                System.out.print(word + ",");
                rank++;
            }
            System.out.println("...");
        }
    }


    public void saveModel(){
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(this.modelFilePath));
            oos.writeObject(model);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
