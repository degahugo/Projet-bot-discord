package org.acme.bot;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 *
 * Checks that the service starts without connecting to Discord
 * (%test.discord.enabled=false in application.properties) and answers on /q/health
 *
 */
@QuarkusTest
class ExampleQuarkusTest {

    @Test
    void applicationStartsAndIsHealthy() {
        given().when().get("/q/health").then().statusCode(200);
    }
}
