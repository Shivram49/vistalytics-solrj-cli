package Querying;

import Querying.Config.SolrConfig;
import Querying.KWIC.KeywordInContext;
import TopicModel.LDATopicModellingAlgorithm;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;


import java.io.*;
import java.util.Scanner;

//For loading the data to a custom core on solr
//bin/post -c <core_name> <path of emails_cleaned.csv>

public class QueryTool {

    //change the term after localhost:8983/solr, to change the core name
    public static final String baseUrl = "http://localhost:8983/solr/updated_enron";
    //sets the number of rows the query has to return
    public static final int queryRows = 4000;
    public static final Scanner key = new Scanner(System.in);


    //function to print emails
    private static void printEmailTable(SolrDocumentList solrDocumentList) {
        int count = 0;
        for (SolrDocument solrDocument : solrDocumentList) {
            Object fromEmail = solrDocument.getFieldValue("From");
            Object toEmail = solrDocument.getFieldValue("To");
            String from = "N/A `",to = "N/A";
            if(fromEmail != null)  from = fromEmail.toString().replaceAll("[\\[\\]]","");
            if(toEmail != null) to = toEmail.toString().replaceAll("[\\[\\]]","");
            System.out.printf("%d : From  %s to %s \n",++count, from, to);
        }

    }

    //function to print query phrase in context
    private static void printQueryInContext(SolrDocumentList emails,String searchPhrase){
        int count = 0;
        for (SolrDocument email : emails) {
            String body = email.getFieldValue("content").toString();
            KeywordInContext kwic = new KeywordInContext(searchPhrase, 50, body);
            String highlightedText = kwic.highlightKeywordInContext();
            if (highlightedText.length() > 0)
                System.out.println(++count + ":" + highlightedText);
        }
    }

    //function to using topic model to print the top few topics
    private static void AnalyseTopics(SolrDocumentList emails) throws Exception {
        System.out.println("Enter the number of topics you want:\n");
        int numTopics = key.nextInt();
        System.out.println("Analysing emails...");
        LDATopicModellingAlgorithm tma = new LDATopicModellingAlgorithm(numTopics,200,"src/main/resources/ldamodel.ser");//email
        StringBuilder currentContentInstance = new StringBuilder();
        emails.stream().forEach(i -> currentContentInstance.append(i));
        tma.addDocumentsToModelInstance(currentContentInstance.toString());
        tma.topTopicsInCorpus();
    }

    public static void main(String[] args) {
        //search phrase entered in the command line arguements

        System.out.println("Enter the Search phrase");
        String searchPhrase = key.next();

        //Test case 1: Number of documents
        //Test case 2: Emails from and to
        //Test case 3: Top k Topics


        try {
            SolrConfig solrAccess = new SolrConfig(baseUrl,queryRows);
            String choice = "1";
            SolrDocumentList emails = solrAccess.getEmails(searchPhrase);
            while(choice.equals("1") || choice.equals("2") || choice.equals("3")){
                System.out.println("\n\n\n\n------------------- Welcome to the Menu -------------------\n\n\n\n");
                System.out.println("1. Show number of documents that matched.");
                System.out.println("2. From and to emails.");
                System.out.println("3. Query in context");
                System.out.println("4. Get top k topics in the emails");
                System.out.println("Enter which choice you wanna pick(1,2,3) or any other num for exit.");
                choice = key.next();
                if(choice.equals("1")){
                    System.out.println("Emails matching : " + emails.getNumFound());
                }
                else if(choice.equals("2")){
                    //prints from and to emails
                    System.out.println("Number of Emails matching: " + emails.getNumFound() + ".\n Senders and recievers below\n");
                    printEmailTable(emails);
                }
                else if(choice.equals("3")){
                    //prints query in context
                    System.out.println("Query in context");
                    printQueryInContext(emails,searchPhrase);
                }
                else if(choice.equals("4")){
                    //prints topics from the email
                    System.out.println("Top k topics");
                    //Analyse emails
                    AnalyseTopics(emails);
                }
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}


