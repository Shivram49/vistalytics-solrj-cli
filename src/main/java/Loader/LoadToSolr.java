package Loader;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;

public class LoadToSolr {
    //initialize solr
    private static final String urlString = "http://localhost:8983/solr/enron"; // Replace with your Solr collection URL
    private static SolrClient solr = new HttpSolrClient.Builder(urlString).build();
    private static int count = 0,countFolders=0;
    public static void main(String[] args) throws IOException, SolrServerException {
        // Specify the directory containing the Enron emails

        File dir = new File("/Users/shivram/Desktop/Workspace/vistalytics/EnronEmails/maildir");

        listFilesForFolder(dir);
        System.out.println(count);
    }

    public static void listFilesForFolder(final File folder) throws IOException, SolrServerException {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
//                countFolders++;
                listFilesForFolder(fileEntry);
            } else {
                count++;
                if(count > 118979) {
                    indexFile(fileEntry.toPath());
                }
            }
        }
    }

    public static void indexFile(Path filePath) throws IOException, SolrServerException {
        // This should be your email parsing function
        Map<String, String> email = EmailParser.parseEnronEmails(new String(Files.readAllBytes(filePath)));
        SolrInputDocument document = new SolrInputDocument();
        for (Map.Entry<String, String> entry : email.entrySet()) {
            document.addField(entry.getKey(), entry.getValue());
        }
        if(document.size() != 0) {
            solr.add(document);
            solr.commit();  // You may want to commit less often if performance is a concern
        }
    }

}
