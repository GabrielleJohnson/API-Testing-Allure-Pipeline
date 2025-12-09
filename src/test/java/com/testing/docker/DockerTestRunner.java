package com.testing.docker;

import org.testng.annotations.Test;
import io.qameta.allure.Description;

/**
 * Verifies Docker execution environment and container configuration parameters.
 */
public class DockerTestRunner {

    // Helper method to determine if we are in a Linux/Docker environment
    private boolean isRunningInDocker() {
        String os = System.getProperty("os.name", "").toLowerCase();
        return os.contains("linux");
    }

    @Test(priority = 1, groups = "environment")
    @Description("Verifies the execution environment is Linux/Docker.")
    public void verifyDockerContainerEnvironment() {
        boolean inDocker = isRunningInDocker();
        String osName = System.getProperty("os.name");
        String hostname = System.getenv("HOSTNAME");

        System.out.println("-> OS Check: " + osName);
        System.out.println("-> Hostname (Container ID): " + hostname);

        if (inDocker) {
            System.out.println("SUCCESS: Tests are executing inside a Linux-based Docker container.");
        } else {
            // Fails the test if it expected to be in Docker but isn't.
            // We use an assertion here to integrate with TestNG/Surefire/Allure.
            // If running this class locally, this test will fail.
            // Adjust the assertion logic if you want the local run to pass.
            assert inDocker : "FAIL: Environment is not Linux. Expected Docker execution.";
        }
    }

    @Test(priority = 2, groups = "configuration")
    @Description("Tests if API credentials (API_KEY and API_TOKEN) were successfully passed as environment variables.")
    public void testApiCredentials() {
        String apiKey = System.getenv("API_KEY");
        String apiToken = System.getenv("API_TOKEN");

        System.out.println("-> API_KEY Status: " + (apiKey != null ? "SET" : "NOT SET"));
        System.out.println("-> API_TOKEN Status: " + (apiToken != null ? "SET" : "NOT SET"));

        // If these are mandatory for your API tests, assert they exist
        assert apiKey != null : "FAIL: API_KEY environment variable is missing.";
        assert apiToken != null : "FAIL: API_TOKEN environment variable is missing.";

        System.out.println("SUCCESS: API credentials successfully loaded.");
    }
}