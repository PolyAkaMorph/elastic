package com.example.demo;

import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {
  private final ElasticService service;

  public Controller(ElasticService service) {
    this.service = service;
  }

  @PostMapping("/index/{index}")
  public void createIndex(@PathVariable("index") String index) {
      service.createIndex(index);
  }

  @PostMapping("/document/{index}")
  public String createDocument(@PathVariable("index") String index, @RequestBody Object document) {
    return service.createDocument(index, document);
  }

  @GetMapping("/document/{index}/{documentId}")
  public Object getDocument(
      @PathVariable("index") String indexName, @PathVariable("documentId") String documentId) {
    return service.readDocument(indexName, documentId);
  }
}
