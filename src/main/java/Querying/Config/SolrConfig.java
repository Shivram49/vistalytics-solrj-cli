package Querying.Config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;

public class SolrConfig {
    private String baseSolrUrl;
    private int queryRows;
    private String searchPhrase;

    public SolrConfig(String baseSolrUrl, int queryRows) {
        this.baseSolrUrl = baseSolrUrl;
        this.queryRows = queryRows;
    }

    private void setSearchPhrase(String searchPhrase) {
        this.searchPhrase = "content:" + searchPhrase;
    }

    private QueryResponse getQueryResponse() throws SolrServerException, IOException {
        SolrClient solr = new HttpSolrClient.Builder(this.baseSolrUrl).build();
        SolrQuery query = new SolrQuery();
        query.setQuery(this.searchPhrase);
        query.setRows(queryRows);
        QueryResponse response = solr.query(query);
        solr.close();
        return response;
    }
    public SolrDocumentList getEmails(String inputPhrase) throws SolrServerException, IOException {
        //setting the searchPhrase object
        setSearchPhrase(inputPhrase);

        //returns all the email objects
        return getQueryResponse().getResults();

    }



}
