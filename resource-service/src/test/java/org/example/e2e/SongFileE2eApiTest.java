package org.example.e2e;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Disabled("e2e test: should be run separately")
class SongFileE2eApiTest {
    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    @Test
    void shouldUploadAndRetrieveSongFile() throws IOException {
        File file = new File("src/test/resources/fortecya-bahmut.mp3");
        byte[] originalBytes = Files.readAllBytes(file.toPath());

        Response uploadResponse = RestAssured
                .given()
                .contentType(ContentType.BINARY)
                .body(originalBytes)
                .when()
                .post("/resources")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String songId = uploadResponse.jsonPath().getString("id");
        assertThat(songId, notNullValue());

        byte[] downloadedBytes =
                given()
                        .accept(ContentType.BINARY)
                        .when()
                        .get("/resources/{id}", songId)
                        .then()
                        .statusCode(200)
                        .extract().asByteArray();

        assertThat(downloadedBytes, is(originalBytes));
    }
}
