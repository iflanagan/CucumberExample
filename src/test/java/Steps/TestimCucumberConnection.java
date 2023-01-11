package Steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.module.Configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;

public class TestimCucumberConnection {

    public String configFile;
    public String project;
    public String token;
    public String runMode;
    public String gridName;
    public String baseUrl;
    public String reportFile;
    public String resultLabel;
    public String testConfigFile;

    public String testimCommand;
    public String testimArgs;

    public static class TestimResult {

        public TestimResult(String testName) {
            this.testName = testName;
        }

        @Override
        public String toString() {
            return "TestimResult{" +
                    "testName='" + testName + '\'' +
                    ", jsonTestData='" + jsonTestData + '\'' +
                    ", resultUrl='" + resultUrl + '\'' +
                    ", failureReason='" + failureReason + '\'' +
                    ", testStatus='" + testStatus + '\'' +
                    '}';
        }

        public String testName;
        public int iteration;
        public String resultSummary = "";
        public boolean failedRuns;
        public String failedRunDetails = "";

        public String jsonTestData = "";
        public String testConfigFile = "";
        public String testOutput = "";
        public String afterTest = "";

        public String resultUrl = "";
        public String failureReason = "Not Run";
        public String testStatus;

    }
    public TestimResult TestimResult;
    public ArrayList<TestimResult> TestimResults;

    public TestimCucumberConnection() {

        this.configFile = MyConfiguration.myConfigFile;
        this.token = MyConfiguration.token;
        this.project = MyConfiguration.project;
        this.gridName = "TESTIM-GRID";
        this.runMode = "selenium";
        this.baseUrl = null;
        this.reportFile = null;
        this.resultLabel = null;
        this.TestimResult = null;

        TestimResults = new ArrayList<>();
    }

    private @NotNull
    String dataTableToJSON(@NotNull DataTable testData) {
        List<Map<String, String>> values = testData.asMaps(String.class, String.class);
        String jsonTestData = values.toString().replace("{", "{\"").replace("}", "\"}").replace(", ", "\", \"").replace("=", "\":\"");
        jsonTestData = jsonTestData.replace(", \"{", ", {").replace("}\"", "}");
        System.out.println("   values: " + jsonTestData);
        return jsonTestData;
    }
    private @NotNull String dataTableToJSON(@NotNull DataTable testData, int iteration) {

        String jsonTestData;

        // Remove from testData all rows other than row <iteration-1>
        //
        AtomicReference<String> _jsonData = new AtomicReference<>("");
        AtomicReference<Integer> row_id = new AtomicReference<>(1);
        List<Map<String, String>> filtered_values = testData.asMaps(String.class, String.class);
        filtered_values.forEach(row -> {
            if (!row.isEmpty() && (row_id.get() == 0 || row_id.get() == iteration)) {
                //System.out.println(" --> add row " + row.toString());
                _jsonData.set(row.toString());
            }
            row_id.set(row_id.get() + 1);
        });
        //System.out.println(" --> _jsonData " + _jsonData);

        // Convert testData to JSON
        //
        jsonTestData = _jsonData.toString().replace("{", "{\"").replace("}", "\"}").replace(", ", "\", \"").replace("=", "\":\"");
        jsonTestData = jsonTestData.replace(", \"{", ", {").replace("}\"", "}");

        //System.out.println("   jsonTestData: " + jsonTestData);
        return jsonTestData;
    }

    public void TestimConfigFileGenerate(String filename, String jsonData) {

        testConfigFile = "";
        try {

            File myObj = new File(filename);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getPath() + "/" + myObj.getName());
            } else {
                System.out.println("Warning: File already exists.");
            }

            FileWriter myWriter = new FileWriter(filename, false);

            myWriter.write("exports.config = {\n");

            myWriter.write("  token: \"" + token + "\",\n");
            myWriter.write("  project: \"" + token + "\",\n");
            myWriter.write("  gridName: \"" + gridName + "\",\n");

            myWriter.write("  beforeSuite: function (suite) {\n");
            myWriter.write("    //console.log(\"beforeSuite\", suite);\n");
            myWriter.write("    return {\n");
            myWriter.write("      overrideTestData: {\n");
            myWriter.write("        \"" + TestimResult.testName + "\":" + jsonData + "\n");
            myWriter.write("      }\n");
            myWriter.write("    }\n");
            myWriter.write("  },\n");

            myWriter.write("  beforeTest: function (test) {\n");
            myWriter.write("    //console.log(\"beforeTest\", test);\n");
            myWriter.write("  },\n");

            myWriter.write("  afterTest: function (test) {\n");
            myWriter.write("    console.log(\"afterTest\", test);\n");
            myWriter.write("  },\n");

            myWriter.write("  afterSuite: function (suite) {\n");
            myWriter.write("    //console.log(\"afterSuite\", suite);\n");
            myWriter.write("  }\n");

            myWriter.write("};\n");

            myWriter.close();

            testConfigFile = myObj.getName(); // myObj.getAbsolutePath();
            //TestimResult.testConfigFile = testConfigFile;

            System.out.println("Successfully wrote to the file. " + testConfigFile);

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public void TestimCliCommandCreate() {

        this.testimCommand = "testim";

        this.testimArgs = " --token " + this.token
                + " --project \"" + this.project + "\""
                + " --grid \"" + this.gridName + "\""
                + " --name \"" + TestimResult.testName + "\""
                + ((baseUrl != null) ? " --base-url \"" + this.baseUrl + "\"" : "")
                + ((reportFile != null) ? " --report-file \"" + this.reportFile + "\"" : "")
                + ((resultLabel != null) ? " --result-label \"" + this.resultLabel + "\"" : "")
                + ((testConfigFile != null) ? " --config-file \"" + this.testConfigFile + "\"" : "")
                + " --mode \"" + this.runMode + "\""
        ;

        System.out.println(this.testimCommand + this.testimArgs);

        //return this.testimCommand + this.testimArgs;
    }

    public void TestimRunMonitor(@org.jetbrains.annotations.NotNull Process process) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {

            System.out.println(line);
            TestimResult.testOutput += line;

            // Get the result link
            //
            if (line.contains("Test \"" + TestimResult.testName)) {
                String patternString = "Test.*started.*url: (.*)";
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    TestimResult.resultUrl = matcher.group(1);
                }
            }

            // Collect Report Information
            //
            if (line.contains("failureReason: \"")) {
                TestimResult.failureReason =  "\n" + line;
            }
            if (line.startsWith("afterTest:")) {
                TestimResult.afterTest =  line;
            }
            // Test plan anonymous (Name: Login with correct username and password) completed PASSED: 1 FAILED: 2 FAILED-EVALUATING: 0 ABORTED: 0 SKIPPED: 0 D
            if (line.startsWith("Test plan ") && line.contains("completed PASSED:")) {
                TestimResult.resultSummary =  line;
            }

            // Test plan anonymous (Name: Login with correct username and password) completed PASSED: 1 FAILED: 2 FAILED-EVALUATING: 0 ABORTED: 0 SKIPPED: 0 D
            if (line.startsWith("Failed runs are:")) {
                TestimResult.failedRuns =  true;
            }
            if (TestimResult.failedRuns && line.startsWith(TestimResult.testName)) {
                TestimResult.failedRunDetails +=  "\n\t" + line;
            }

            // Failure checks
            //
            if (line.contains("Argument Error: No tests to run")) {
                TestimResult.testStatus = "Failed";
                TestimResult.failureReason = "No tests to run";
            }
            if (line.contains("finished status: FAILED")) {
                TestimResult.testStatus = "Failed";
            }

        }

    }
    public void TestimTestcaseExecute() throws IOException, InterruptedException {

        System.out.println("TestimTestcaseExecute => " + TestimResult.testName);

        TestimResult.testStatus = "Executing";

        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");

        ProcessBuilder builder = new ProcessBuilder();

        if (isWindows) {
            builder.command("cmd.exe", "/c", this.testimCommand + this.testimArgs);
        } else {
            builder.command("sh", "-c", this.testimCommand + this.testimArgs);
        }
        builder.directory(new File(System.getProperty("user.home")));

        Process process = builder.start();

        TestimRunMonitor(process);

        // Wait for completion
        int exitCode = process.waitFor();
        assert exitCode == 0;

        TestimResults.add(TestimResult);
    }

    @Then("Testim Testcase Run \\(String {string})")
    public void testimTestcaseRunString(String testName, DataTable testData) throws InterruptedException, IOException {

        TestimResult = new TestimResult(testName);
        TestimResult.testConfigFile = "/Users/ianflanagan/testimConfig.js";
        TestimResult.jsonTestData = dataTableToJSON(testData);

        TestimConfigFileGenerate(TestimResult.testConfigFile, TestimResult.jsonTestData);
        TestimCliCommandCreate();

        TestimTestcaseExecute();

    }

    @Then("Testim Testcase Run \\(Integer {int}) \\(String {string})")
    public void testimTestcaseRunIntegerString(int iteration, String testName, DataTable testData) throws InterruptedException, IOException {

        TestimResult = new TestimResult(testName);
        TestimResult.testConfigFile = "/Users/ianflanagan/testimConfig.js";
        TestimResult.jsonTestData = dataTableToJSON(testData, iteration);
        TestimResult.iteration = iteration;

        System.out.println("\n--------------------- TESTCASE (iteration " + iteration + ") ----------------------------------");

        TestimResult.jsonTestData = dataTableToJSON(testData, iteration);

        TestimConfigFileGenerate(TestimResult.testConfigFile, TestimResult.jsonTestData);

        TestimCliCommandCreate();

        TestimTestcaseExecute();
    }

    @Then("Testim Run Results Process")
    public void testimRunResultsProcess() {

        for (TestimResult testimResult : TestimResults) {

            System.out.println("\n================= " + TestimResult.testName + " ==> " + TestimResult.testStatus + " ===============");
            System.out.println(testimResult.toString());

            // Failure checks
            //
            if (Objects.equals(testimResult.testStatus, "Failed")) {

                switch (testimResult.failureReason) {
                    case "No tests to run":
                        System.out.println("=====================================================================================================");
                        fail("\nTestim Testcase '" + TestimResult.testName + "' does not exist");
                        break;
                    default:
                        if (TestimResult.failedRunDetails != null)
                            System.out.println("Failed runs are:" + TestimResult.failedRunDetails);
                        System.out.println("=====================================================================================================");
                        fail("\n\n" + TestimResult.failureReason + "\nResultUrl: " + TestimResult.resultUrl + "\n");
                        break;
                }

            }

            System.out.println("=====================================================================================================");

        }


    }
}
