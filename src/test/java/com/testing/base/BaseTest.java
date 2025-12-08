package com.testing.base;

import com.POJOClasses.config.ApiConfig;
import com.testing.utils.TestSteps;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;



public abstract class BaseTest {
    protected static TestSteps testSteps;

    @BeforeClass
    public void setup() {
        testSteps = new TestSteps();
    }

    @AfterClass
    public void teardown() {

    }
    protected String getAuthParams() {
        return String.format("key=%s&token=%s", ApiConfig.getApiKey(), ApiConfig.getApiToken());
    }
}
