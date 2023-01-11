exports.config = {
  token: "GqKVj97IUUOXDrAJPirAty6HsMIGUZMqvRi4mm7IlvlBupA0tf",
  project: "GqKVj97IUUOXDrAJPirAty6HsMIGUZMqvRi4mm7IlvlBupA0tf",
  gridName: "TESTIM-GRID",
  beforeSuite: function (suite) {
    //console.log("beforeSuite", suite);
    return {
      overrideTestData: {
        "Login with correct username and password using Scenario outline":{"iteration":"3", "username":"aaron", "password":"apass"}
      }
    }
  },
  beforeTest: function (test) {
    //console.log("beforeTest", test);
  },
  afterTest: function (test) {
    console.log("afterTest", test);
  },
  afterSuite: function (suite) {
    //console.log("afterSuite", suite);
  }
};
