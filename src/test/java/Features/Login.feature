Feature: LoginFeature
  This feature deals with the login functionality of the application

  Scenario: Login with correct username and password
    Given I navigate to the login page of the application
    And I enter the following for Login
      | username | password  |
      | John     | whatever  |
    And I click the login button
    Then I should see the UserForm page


    Then Testim Testcase Run (String "Login with correct username and password")
      | username | password  |
      | John     | whatever  |

    Then Testim Run Results Process


  Scenario Outline: Login with correct username and password using Scenario outline
    Given I navigate to the login page of the application
    And I enter <username> and <password>
    And I click the login button
    And I select the remember me button
    Then I should see the UserForm page

    Then Testim Testcase Run (Integer <iteration>) (String "Login with correct username and password using Scenario outline")
      | iteration | username | password |
      | 1         | nicole   | npass    |

    Then Testim Run Results Process

    Examples:ultimate, earliest (buggy is ok) version
      | iteration | username | password |
      | 1         | nicole   | npass    |

