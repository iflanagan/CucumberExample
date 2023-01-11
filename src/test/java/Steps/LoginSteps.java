package Steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.util.List;

public class LoginSteps {

    @Given("I navigate to the login page of the application")
    public void iNavigateToTheLoginPageOfTheApplication() {
    }

    @And("I click the login button")
    public void iClickTheLoginButton() {
    }

    @And("I enter the following for Login")
    public void iEnterTheFollowingForLogin(DataTable table) {
        List<List<String>>  data = table.cells();
        //System.out.println("I enter the username as '" + data.get(1).get(0) + "' and the password as '" + data.get(1).get(1) +"'");
    }

    @And("^I enter ([^\"]*) and ([^\"]*)$")
    public void iEnterUsernameAndPassword(String username, String password) {
    }

    @And("I select the remember me button")
    public void iSelectTheRememberMeButton() {
    }

    @Then("I should see the UserForm page")
    public void iShouldSeeTheUserFormPage() {
    }

    @Given("I enter information to the contact form")
    public void iEnterInformationToTheContactForm() {

    }

    @And("asdfasdf asdf sdfs")
    public void asdfasdfAsdfSdfs() {
    }

    @And("I do something else")
    public void iDoSomethingElse() {

    }
}
