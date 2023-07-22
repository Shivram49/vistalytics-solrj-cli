package Querying;

import Querying.KWIC.KeywordInContext;
import TopicModel.LDATopicModellingAlgorithm;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QueryTool {
    public static void main(String[] args) {
        SolrClient solr = new HttpSolrClient.Builder("http://localhost:8983/solr/updated_enron").build();
        SolrQuery query = new SolrQuery();
        String inputQuery = args[0];
//        String searchQuery = "Body:"+inputQuery + " || Subject:"+inputQuery;
        String searchQuery = "content:"+inputQuery;

        query.setQuery(searchQuery);
        query.setRows(Integer.MAX_VALUE);


        QueryResponse response = null;
        try {
            response = solr.query(query);
            SolrDocumentList emails = response.getResults();
            long numFound = emails.getNumFound();
            System.out.println("Number of Emails matching: " + numFound);
            System.out.println("Email ids in the form from -> to");
            int count = 0;
            LDATopicModellingAlgorithm tma = new LDATopicModellingAlgorithm(10,200,"src/main/resources/ldamodel.ser");

            List<String> keywordsInContext = new ArrayList<>();
            //email
            for(SolrDocument email : emails){
                Object fromObject = email.getFieldValue("From");
                Object toObject = email.getFieldValue("To");
                String from = "NULL `",to = "NULL";
                if(fromObject != null)  from = fromObject.toString().replaceAll("[\\[\\]]","");
                if(toObject != null) to = toObject.toString().replaceAll("[\\[\\]]","");
                System.out.println(++count+":"+ from+ "->" + to);
                tma.addDocumentsToModelInstance(email.getFieldValue("content").toString());
                String body = email.getFieldValue("content").toString();
               KeywordInContext kwic = new KeywordInContext(inputQuery,50,body);
                String highlightedText = kwic.highlightKeywordInContext();
                if(highlightedText.length() > 0)
                    System.out.println(++count+":"+highlightedText);

            }
            tma.topTopicsInCorpus(10);

            solr.close();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }
}
