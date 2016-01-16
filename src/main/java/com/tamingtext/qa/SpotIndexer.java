package com.tamingtext.qa;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by amond on 16. 1. 15.
 */
public class SpotIndexer {
  private transient static Logger LOGGER = LoggerFactory.getLogger(SpotIndexer.class);
  private static SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
  private static SimpleDateFormat solrFormatter =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);

  private static ObjectMapper objectMapper = new ObjectMapper();

  public static TimeZone UTC = TimeZone.getTimeZone("UTC");

  private SolrServer server;

  public static final String DEFAULT_SOLR_URL = "http://localhost:8983/solr";

  public SpotIndexer() throws MalformedURLException {
    server = new CommonsHttpSolrServer(DEFAULT_SOLR_URL);
  }

  public SpotIndexer(SolrServer server) throws MalformedURLException {

    this.server = server;
  }

  private void index() {
    try {

      JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, Spot.class);
      List<Spot> spotList = objectMapper.readValue(new File("insideplanet/hongkong.json"), type);

      for (Spot spot : spotList) {
        String body = objectMapper.writeValueAsString(spot);
        LOGGER.info(body);

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("docid", spot.getId());
        doc.addField("name", spot.getName());
        doc.addField("doctitle", spot.getName());

        doc.addField("address", spot.getAddress());
        doc.addField("cost", spot.getCost());
        doc.addField("home", spot.getHome());
        doc.addField("hours", spot.getHours());
        doc.addField("tel", spot.getTel());
        doc.addField("description", spot.getDescription());

        doc.addField("body", body);
        server.add(doc);
      }
      server.commit();
      server.optimize();
    } catch (IOException e) {
      LOGGER.error("json parsing error", e);
    } catch (SolrServerException e) {
      e.printStackTrace();
    }

  }

  public static void main(String[] args) throws Exception {
    SpotIndexer indexer = new SpotIndexer();
    indexer.index();
  }
}
