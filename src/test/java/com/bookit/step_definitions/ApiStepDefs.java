package com.bookit.step_definitions;

import com.bookit.utilities.BookitUtils;
import com.bookit.utilities.ConfigurationReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;

import static io.restassured.RestAssured.given;

public class ApiStepDefs {

    String token;
    Response response;

    @Given("I logged Bookit api as a {string}")
    public void i_logged_bookit_api_as_a(String role) {
        token = BookitUtils.generateTokenByRole(role);
    }

    @When("I sent get request to {string} endpoint")
    public void i_sent_get_request_to_endpoint(String endpoint) {
        response = given()
                .accept(ContentType.JSON)
                .and().header("Authorization", "Bearer " + token)
                .when().get(ConfigurationReader.getProperty("base_url") + endpoint);
    }

    @Then("status code should be {int}")
    public void status_code_should_be(int expectedStatusCode) {
        Assert.assertEquals(expectedStatusCode, response.statusCode());
    }

    @Then("content type is {string}")
    public void content_type_is(String expectedContentType) {
        Assert.assertEquals(expectedContentType, response.getContentType());
    }

    @Then("role is {string}")
    public void role_is(String expectedRole) {
        Assert.assertEquals(expectedRole, response.path("role"));
    }

}
