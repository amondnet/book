package com.tamingtext.qa;

import java.io.IOException;
import java.net.MalformedURLException;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;

/**
 * Created by amond on 16. 1. 15.
 */
public class IndexClearer {


  private SolrServer server;

  public static final String DEFAULT_SOLR_URL = "http://localhost:8983/solr";

  public IndexClearer() throws MalformedURLException {
    server = new CommonsHttpSolrServer(DEFAULT_SOLR_URL);
  }

  public IndexClearer(SolrServer server) throws MalformedURLException {

    this.server = server;
  }


  private void clear() throws IOException, SolrServerException {
    server.deleteByQuery("*:*");
    server.commit();
    server.optimize();
  }


  public static void main(String[] args) throws Exception {
    IndexClearer indexer = new IndexClearer();
    indexer.clear();
  }


}
