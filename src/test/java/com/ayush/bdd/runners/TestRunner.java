package com.ayush.bdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Main Cucumber runner.
 * <p>
 * Select scenarios with tags at run time, e.g.
 * {@code mvn test -Dcucumber.filter.tags="@smoke and not @wip"}.
 * Failed scenarios are written to target/rerun.txt so {@link FailedScenariosRunner} can re-run only those.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.ayush.bdd"},
        tags = "not @wip",
        plugin = {
                "pretty",
                "summary",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json",
                "rerun:target/rerun.txt",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    /** parallel = true runs scenarios concurrently; thread count is set in testng.xml. */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
