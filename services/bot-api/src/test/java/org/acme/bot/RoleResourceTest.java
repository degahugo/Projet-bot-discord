package org.acme.bot;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

/**
 *
 * Integration test of the Role REST API
 * Quarkus starts with a real PostgreSQL (Dev Services / Testcontainers : Docker is required)
 *
 */
@QuarkusTest
class RoleResourceTest {

    @Test
    void fullCrudScenario() {
        // create -> 201
        given().contentType(ContentType.JSON).body("{\"name\":\"Moderator\"}")
                .when().post("/api/v1/roles")
                .then().statusCode(201).body("name", equalTo("Moderator"));

        // list -> 200
        given().when().get("/api/v1/roles")
                .then().statusCode(200).body("name", hasItem("Moderator"));

        // get by name -> 200
        given().when().get("/api/v1/roles/Moderator")
                .then().statusCode(200).body("name", equalTo("Moderator"));

        // same name a second time -> 409
        given().contentType(ContentType.JSON).body("{\"name\":\"Moderator\"}")
                .when().post("/api/v1/roles")
                .then().statusCode(409);

        // delete -> 204, then not found -> 404
        given().when().delete("/api/v1/roles/Moderator").then().statusCode(204);
        given().when().get("/api/v1/roles/Moderator").then().statusCode(404);
    }

    @Test
    void blankNameIsRejected() {
        given().contentType(ContentType.JSON).body("{\"name\":\"\"}")
                .when().post("/api/v1/roles")
                .then().statusCode(400);
    }
}
