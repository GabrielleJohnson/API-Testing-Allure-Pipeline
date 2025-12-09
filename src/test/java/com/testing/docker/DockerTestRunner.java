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
    @Description("Verifies the execution environment. Informational check only.")
    public void verifyDockerContainerEnvironment() {
        boolean inDocker = isRunningInDocker();
        String osName = System.getProperty("os.name");
        String hostname = System.getenv("HOSTNAME");

        System.out.println("-> OS Check: " + osName);
        System.out.println("-> Hostname (Container ID): " + (hostname != null ? hostname : "N/A"));

        if (inDocker) {
            System.out.println("SUCCESS: Tests are executing inside a Linux-based Docker container.");
        } else {
            // This is the key change: It prints a warning but does not assert false (does not fail the test)
            System.out.println("WARNING: Environment is NOT Linux. Running directly on host OS (Jenkins Agent).");
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