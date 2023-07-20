package TopicModel;

import java.util.*;

import org.tartarus.snowball.ext.EnglishStemmer;




public class DataPreprocessor {


    private StringTokenizer tokenizer;
    private Set<String> stopWords;
    private EnglishStemmer stemmer;
    public DataPreprocessor(String text){
        tokenizer = new StringTokenizer(text);
        stopWords = new HashSet<>();
        stopWords.add("the");stopWords.add("is");stopWords.add("and");
        stemmer = new EnglishStemmer();
    }

    public List<String> tokenize(String text){
        List<String> tokens = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            tokens.add(token);
        }
        return tokens;

    }
    public List<String> stopWordRemoval(List<String> tokens){
        List<String> filteredTokens = new ArrayList<>();
        for (String token : tokens) {
            if (!stopWords.contains(token)) {
                filteredTokens.add(token);
            }
        }
        return filteredTokens;
    }
    public List<String> stemming(List<String> tokens){
        List<String> stemmedTokens = new ArrayList<>();
        for (String token : tokens) {
            stemmer.setCurrent(token);
            stemmer.stem();
            String stemmedToken = stemmer.getCurrent();
            stemmedTokens.add(stemmedToken);
        }
        return stemmedTokens;
    }
    public List<String> lowercase(List<String> stemmedTokens){
        List<String> result = new ArrayList<>();
        for(String token : stemmedTokens)
            result.add(token.toLowerCase());
        return result;
    }
    //TODO:lemmatizer would be needed, but P2

}

