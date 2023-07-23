package TopicModel;
import cc.mallet.pipe.*;
import cc.mallet.topics.*;
import cc.mallet.types.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

public class LDATopicModellingAlgorithm {
    private int numTopics;
    private int numIterations;
    private ParallelTopicModel model;
    private InstanceList instances;
    private String modelFilePath;
    private static int messageId = 0;


    public ParallelTopicModel getModel() {
        return model;
    }

    //All the processing steps in a pipe
    private Pipe[] getPreprocessingSteps() {
        ArrayList<Pipe> pipeList = new ArrayList<>();

        // Specify your preprocessing steps here
        pipeList.add(new CharSequenceLowercase());  // Convert text to lowercase
        pipeList.add(new CharSequence2TokenSequence());  // Tokenize the text

        //removing stopwords include subject
        ArrayList<String> stopwords = new ArrayList<>();
        String[] emailHeaderStopWords = {"from", "to", "cc", "bcc", "subject","www","http","font","a", "abbr", "acronym", "address", "b", "bdo", "big", "blockquote", "br", "button",
                "caption", "cite", "code", "col", "colgroup", "dd", "del", "dfn", "dir", "dl", "dt",
                "em", "fieldset", "font", "form", "h1", "h2", "h3", "h4", "h5", "h6", "i", "iframe",
                "img", "input", "ins", "kbd", "label", "legend", "li", "map", "menu", "ol", "optgroup",
                "option", "p", "pre", "q", "s", "samp", "select", "small", "span", "strike", "strong",
                "sub", "sup", "table", "tbody", "td", "textarea", "tfoot", "th", "thead", "tr", "tt",
                "u", "ul", "var"};
        stopwords.addAll(Arrays.asList(emailHeaderStopWords));
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
    public void topTopicsInCorpus() throws Exception {
        ParallelTopicModel trainedModel = ParallelTopicModel.read(new File("src/main/resources/ldamodel.ser"));
        double[] topicDistribution;
        int numTopWords = 10;

        // Perform topic inference on new data
        for (Instance newInstance : this.instances) {
            topicDistribution = trainedModel.getInferencer().getSampledDistribution(newInstance, 100, 10, 10);
            // topicDistribution contains the inferred topic distribution for the new instance
            // Note: The arguments 100, 10, 10 are optional and are related to the inference process.
            // You can adjust these values based on your requirements.

            // Extract the top topics from the inferred distribution
            ArrayList<IDSorter> sortedTopics = new ArrayList<>();
            for (int topic = 0; topic < topicDistribution.length; topic++) {
                sortedTopics.add(new IDSorter(topic, topicDistribution[topic]));
            }
            sortedTopics.sort((o1, o2) -> -Double.compare(o1.getWeight(), o2.getWeight()));

            // Display the top topics and their top words
            System.out.println("Top topics for instance:");
            int rank = 0;
            Iterator<IDSorter> iterator = sortedTopics.iterator();
            int i = 0;
            while (iterator.hasNext() && rank < numTopics) {
                IDSorter topicInfo = iterator.next();
                int topic = topicInfo.getID();
                double weight = topicInfo.getWeight();
                System.out.println("Weight:" + weight);
//                System.out.println("Topic " + (topic + 1) + " (" + weight + "):");
                printTopWords(trainedModel, topic, numTopWords,++i);
                rank++;
            }
        }
    }

    private static void printTopWords(ParallelTopicModel model, int topic, int numTopWords, int i) {
        Alphabet modelAlphabet = model.getAlphabet();
        TreeSet<IDSorter> topicWords = model.getSortedWords().get(topic);

        if (topicWords == null) {
            System.err.println("Topic " + topic + " is not available in the model.");
            return;
        }

        System.out.println("Topic " + i + ":");
        int rank = 0;
        for (IDSorter wordInfo : topicWords) {
            if (rank >= numTopWords) {
                break;
            }
            int wordId = wordInfo.getID();
            String word = modelAlphabet.lookupObject(wordId).toString();
            double weight = wordInfo.getWeight();
            System.out.println("\t" + word + " (" + weight + ")");
            rank++;
        }
    }

    public void saveModel(ParallelTopicModel topicModel){
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(this.modelFilePath));
            oos.writeObject(topicModel);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
