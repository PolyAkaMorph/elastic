package com.example.demo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

import static io.restassured.RestAssured.given;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RestTest {
  private static final String INDEX = "abc";
  private static final String DOCUMENT = """
{"item":"value"}""";
  @LocalServerPort private Integer port;
  static ElasticsearchContainer container =
      new ElasticsearchContainer(
              DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.8.2"))
          .withEnv("discovery.type", "single-node")
          .withEnv("xpack.security.enabled", "false");

  @BeforeAll
  static void start() {
    container.start();
  }

  @AfterAll
  static void stop() {
    container.stop();
  }

  @BeforeEach
  void setUp() {
    RestAssured.baseURI = "http://localhost:" + port;
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("app.elastic.port", container::getFirstMappedPort);
  }

  @Test
  @Order(1)
  void createIndex() {
    // curl -v -X POST localhost:8080/index/abc
    given().when().post("/index/" + INDEX).then().statusCode(200);
  }

  @Test
  @Order(2)
  void createAndReadDocument() {
    // curl -v -X POST localhost:8080/document/abc -d '{"ad": "absda"}' -H "Content-Type:application/json"
    Response response =
        given().contentType(ContentType.JSON).body(DOCUMENT).when().post("/document/" + INDEX);

    String documentId = response.getBody().asString();

    response.then().statusCode(200);


    // curl -v -X GET localhost:8080/document/abc/FR-VOowBCKl0aVLX4ydH
    String document =
        given()
            .when()
            .get("/document/" + INDEX + "/" + documentId)
            .then()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .asString();

    assert Objects.equals(DOCUMENT, document);
  }
}
