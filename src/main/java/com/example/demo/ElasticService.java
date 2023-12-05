package com.example.demo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
public class ElasticService {
  private final ElasticsearchClient client;

  public ElasticService(ElasticsearchClient client) {
    this.client = client;
  }

  public void createIndex(String index) {
    try {
      CreateIndexResponse response = client.indices().create(i -> i.index(index));

      if (!response.acknowledged()) {
        throw new RuntimeException("Not created");
      }
      log.info("Index was created");

    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  public String createDocument(String index, Object document) {
    try {
      IndexResponse response = client.index(i -> i.index(index).document(document));

      if (!Objects.equals(Result.Created, response.result())) {
        throw new RuntimeException("Not created");
      }

      log.info("Document was created");

      return response.id();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    return null;
  }

  public Object readDocument(String index, String documentId) {
    try {
      GetResponse<Object> response =
          client.get(request -> request.index(index).id(documentId), Object.class);

      if (!response.found()) {
        throw new RuntimeException( documentId + " was not found");
      }

      log.info("Document was read");

      return response.source();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    return null;
  }
}
