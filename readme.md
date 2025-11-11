# Testing Strategy

This project uses a combination of testing strategies to ensure stability, reliability, and adequate coverage of the application.

## 1. Unit Tests

* **Frameworks:** JUnit 5 + Mockito
* **Scope:** Test individual classes or methods in isolation, mocking dependencies.
* **Purpose:** Ensure that core logic works as expected without involving external systems.

## 2. Integration Tests

* **Frameworks:** JUnit 5 + H2 in-memory database
* **Scope:** Test interactions between layers (e.g., service → repository → database).
* **Purpose:** Validate that multiple components work together correctly.

## 3. Component Tests

* **Frameworks:** Spring Cloud Contract / Pact
* **Scope:** High-level business scenarios covering a component.
* **Purpose:** Ensure that each component behaves correctly according to its contract.

## 4. Contract Tests

* **Frameworks:** Spring Cloud Contract / Pact
* **Scope:** Verify communication contracts (HTTP or messaging) between services.
* **Purpose:** Detect breaking changes in API contracts or message formats.

## 5. End-to-End Tests

* **Frameworks:** RestAssured
* **Scope:** Full system scenarios (upload → retrieve → process).
* **Purpose:** Ensure that the complete flow works from a client perspective.

## Test Coverage

* **Tool:** JaCoCo Maven plugin
* **Target:** 80% branch coverage based on the Pareto principle.
* **Rationale:** Ensures essential logic is covered without requiring 100% coverage, which is sufficient for training purposes and general project stability.

## Example Unit & Integration Test

```java
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class ResourceServiceTest {

    @Autowired
    private ResourceService resourceService;

    @MockBean
    private ResourceRepository resourceRepository;

    // Unit test example
    @Test
    void storeFileShouldReturnId() {
        byte[] data = "sample data".getBytes();
        Mockito.when(resourceRepository.save(any())).thenAnswer(invocation -> {
            ResourceEntity entity = invocation.getArgument(0);
            entity.setId(1);
            return entity;
        });

        Identifiable<Integer> result = resourceService.storeFile(data);

        assert(result.getId() == 1);
    }
}
```

## Component / Contract / E2E Example

**Component test (contract) using Spring Cloud Contract:**

```groovy
Contract.make {
    request {
        method 'POST'
        url '/files'
        body([data: "sample bytes"])
    }
    response {
        status 200
        body([id: 1])
    }
}
```

**End-to-End test using RestAssured:**

```java
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Paths;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class FileUploadE2ETest {

    @Test
    void uploadAndRetrieveFile() throws Exception {
        byte[] originalBytes = Files.readAllBytes(Paths.get("src/test/resources/sample.mp3"));

        int fileId = given()
                .contentType("application/json")
                .body(originalBytes)
            .when()
                .post("/files")
            .then()
                .statusCode(200)
                .extract()
                .path("id");

        byte[] retrievedBytes = given()
                .get("/files/{id}", fileId)
            .then()
                .statusCode(200)
                .extract()
                .asByteArray();

        assertArrayEquals(originalBytes, retrievedBytes);
    }
}
```

## Summary

* **Unit tests:** cover core logic.
* **Integration tests:** cover database and layer interactions.
* **Component/contract tests:** ensure business scenarios and service contracts are respected.
* **End-to-end tests:** validate full flows as a user would experience them.
* **Coverage monitoring:** JaCoCo ensures at least 80% of classes are covered, following the Pareto principle.


curl -X PUT "http://localhost:9200/traces-otel-2025.11.04" -H 'Content-Type: application/json' -d '{
"mappings": {
"properties": {
"traceId": { "type": "keyword" },
"spanId": { "type": "keyword" },
"name": { "type": "text" },
"timestamp": { "type": "date", "format": "epoch_millis" },
"service": {
"properties": {
"name": { "type": "keyword" }
}
}
}
}
}'


curl -X PUT "http://localhost:9200/_index_template/traces-otel-template" -H 'Content-Type: application/json' -d '{
"index_patterns": ["traces-otel-*"],
"template": {
"mappings": {
"dynamic": true,
"properties": {
"traceId": { "type": "keyword" },
"spanId": { "type": "keyword" },
"name": { "type": "text" },
"timestamp": { "type": "date", "format": "epoch_millis" },
"service": { "properties": { "name": { "type": "keyword" } } }
}
}
}
}'


