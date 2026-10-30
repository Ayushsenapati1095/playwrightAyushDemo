package com.ayush.bdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Re-runs only the scenarios that failed in the last run (listed in target/rerun.txt).
 * Usage: mvn test -DsuiteXmlFile=src/test/resources/testng-rerun.xml
 */
@CucumberOptions(
        features = "@target/rerun.txt",
        glue = {"com.ayush.bdd"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber-rerun.html",
                "json:target/cucumber-reports/cucumber-rerun.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true
)
public class FailedScenariosRunner extends AbstractTestNGCucumberTests {
}
