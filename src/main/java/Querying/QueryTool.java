package Querying;

import TopicModel.DataPreprocessor;
import TopicModel.LDATopicModellingAlgorithm;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QueryTool {
    public static void main(String[] args) {
        SolrClient solr = new HttpSolrClient.Builder("http://localhost:8983/solr/enron").build();
        SolrQuery query = new SolrQuery();
        String searchQuery = "Body:"+args[0] + " || Subject:"+args[0];
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
            LDATopicModellingAlgorithm tma = new LDATopicModellingAlgorithm(10,200);

            //email
            for(SolrDocument email : emails){
//                Object fromObject = email.getFieldValue("From");
//                Object toObject = email.getFieldValue("To");
//                String from = "NULL `",to = "NULL";
//                if(fromObject != null)  from = fromObject.toString().replaceAll("[\\[\\]]","");
//                if(toObject != null) to = toObject.toString().replaceAll("[\\[\\]]","");
//                System.out.println(++count+":"+ from+ "->" + to);
                tma.addDocumentsToModelInstance(email.getFieldValue("Body").toString(),email.getFieldValue("Message-ID").toString());
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
