package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookitUtils;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DB_Util;
import com.bookit.utilities.Environment;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiStepDefs {

    String token;
    Response response;
    Map<String, String> credentials;
    String email;

    @Given("I logged Bookit api as a {string}")
    public void i_logged_bookit_api_as_a(String role) {
        token = BookitUtils.generateTokenByRole(role);

        credentials = BookitUtils.returnCredentials(role);
        email = credentials.get("email");
    }

    @When("I sent get request to {string} endpoint")
    public void i_sent_get_request_to_endpoint(String endpoint) {
        response = given()
                .accept(ContentType.JSON)
                .and().header("Authorization", "Bearer " + token)
                .when().get(Environment.BASE_URL + endpoint);
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

    @Then("the information about current user from api and database should match")
    public void the_information_about_current_user_from_api_and_database_should_match() {

        // GET data from API
        String actualLastName = response.path("lastName");
        String actualFirstName = response.path("firstName");
        String actualRole = response.path("role");

        // GET data from DB
        String query = "SELECT firstname, lastname, role FROM users WHERE email = '" + email + "'";
        DB_Util.runQuery(query);

        Map<String, String> dbMap = DB_Util.getRowMap(1);

        // Assertions
        Assert.assertEquals(dbMap.get("firstname"), actualFirstName);
        Assert.assertEquals(dbMap.get("lastname"), actualLastName);
        Assert.assertEquals(dbMap.get("role"), actualRole);
    }

    @Then("UI,API and Database user information must be match")
    public void ui_api_and_database_user_information_must_be_match() {

        // GET data from API
        String actualLastName = response.path("lastName");
        String actualFirstName = response.path("firstName");
        String actualRole = response.path("role");

        // GET data from DB
        String query = "SELECT firstname, lastname, role FROM users WHERE email = '" + email + "'";
        DB_Util.runQuery(query);

        Map<String, String> dbMap = DB_Util.getRowMap(1);

        // Assertions -- DB vs API
        Assert.assertEquals(dbMap.get("firstname"), actualFirstName);
        Assert.assertEquals(dbMap.get("lastname"), actualLastName);
        Assert.assertEquals(dbMap.get("role"), actualRole);

        // Get data from UI
        SelfPage selfPage = new SelfPage();

        // UI vs DB assertion
        Assert.assertEquals(dbMap.get("firstname") + " " + dbMap.get("lastname"), selfPage.name.getText());
        Assert.assertEquals(dbMap.get("role"), selfPage.role.getText());

        // UI vs API assertion
        Assert.assertEquals(response.path("firstName") + " " + response.path("lastName"), selfPage.name.getText());
        Assert.assertEquals(response.path("role"), selfPage.role.getText());

    }


}
