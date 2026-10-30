# Playwright Java + Cucumber BDD Automation Framework

Enterprise-style **BDD** test automation framework for UI and API testing, built on **Playwright Java**, **Cucumber 7**, **TestNG**, **Allure** and **Maven**, with ready-to-use **Jenkins** and **Azure DevOps** pipelines.
The demo scenarios target the public Shopify store [sauce-demo.myshopify.com](https://sauce-demo.myshopify.com/).

## Tech stack

| Concern | Tool |
|---|---|
| Language / build | Java 21, Maven |
| BDD | Cucumber 7 (Gherkin feature files, Scenario Outlines, data tables, tags) |
| Runner | Cucumber TestNG (`AbstractTestNGCucumberTests`), scenarios run in parallel |
| Dependency injection | Cucumber PicoContainer (one `TestContext` shared by steps per scenario) |
| Browser + API automation | Playwright Java 1.62 (`Page`, `Locator`, `APIRequestContext`) |
| Assertions | Playwright web-first `assertThat` (UI), AssertJ (API/data) |
| Reporting | Allure (Cucumber 7 adapter), Cucumber HTML + JSON, Surefire JUnit XML |
| JSON / test data | Jackson (record models), Datafaker, `ProductData` enum |
| Logging | SLF4J + Log4j2 (console + `target/logs/test-run.log`) |
| CI/CD | Jenkinsfile, azure-pipelines.yml, Dockerfile (official Playwright Java image) |

## Project structure

```
src/main/java/com/ayush/framework          ← reusable framework (no Cucumber code)
├── config/       ConfigManager             – env files + -D / env-var overrides
├── core/         PlaywrightManager         – ThreadLocal Playwright/Browser per thread
│                 BrowserContextManager     – fresh context per scenario, tracing, video
├── components/   HeaderComponent           – shared header/navigation
├── pages/        BasePage, GenericPage, HomePage, CatalogPage, ProductPage, CartPage, LoginPage, SearchPage, AboutUsPage
├── api/          ApiClient, ProductApiClient, models/Product
├── data/         ProductData (expected catalogue), TestDataFactory (Datafaker)
├── listeners/    SuiteListener (Allure env + cleanup), RetryAnalyzer, RetryTransformer
└── utils/        AllureAttachments

src/test/java/com/ayush/bdd                ← BDD layer
├── runners/      TestRunner (all scenarios), FailedScenariosRunner (rerun failures)
├── hooks/        Hooks – browser per UI scenario, screenshot + trace on failure
├── context/      TestContext – scenario-scoped state injected by PicoContainer
└── steps/        CommonSteps, ProductSteps, CartSteps, LoginSteps, SearchSteps, ApiSteps

src/test/resources
├── features/     home, product, cart, login, search, product_api (.feature)
├── config/       qa.properties, stage.properties
├── testng.xml, testng-rerun.xml, cucumber.properties, log4j2.xml, allure.properties
```

## Feature files and scenarios

| Feature | Scenarios | Tags |
|---|---|---|
| `home.feature` | Home page loads with branding and empty cart · Featured products shown · Catalog lists every product · About Us opens | `@ui @home` |
| `product.feature` | **Scenario Outline**: product details for 5 products · Open product from catalog · Sold-out product can't be added | `@ui @product` |
| `cart.feature` | Add one product · Add several products (**data table**) · Remove product empties cart | `@ui @cart` |
| `login.feature` | Login page opens · Invalid login shows error · Forgot password shows reset form | `@ui @login` |
| `search.feature` | Search page opens · **Scenario Outline**: search returns only matching products | `@ui @search` |
| `product_api.feature` | **Scenario Outline**: product API data for all 7 products · Unknown product returns 404 | `@api` |

Every scenario is also tagged `@smoke` and/or `@regression`; negative tests carry `@negative`. Add `@wip` to keep a scenario out of runs while you write it.

Example:

```gherkin
@regression
Scenario: Add several products to the cart
  Given I have added the following products to the cart:
    | Grey jacket |
    | Striped top |
  Then the cart should show 2 items
  When I open the cart
  Then the cart should contain the following products:
    | Grey jacket |
    | Striped top |
```

## Running

Prerequisites: JDK 21 and Maven 3.9+. Playwright downloads the browsers automatically on the first run.

```bash
# All scenarios (except @wip)
mvn clean test

# By tag expression
mvn clean test "-Dcucumber.filter.tags=@smoke"
mvn clean test "-Dcucumber.filter.tags=@cart and not @negative"
mvn clean test "-Dcucumber.filter.tags=@api"

# Headed Firefox against another environment
mvn clean test "-Dcucumber.filter.tags=@smoke" -Dbrowser=firefox -Dheadless=false -Denv=stage

# A single feature file
mvn clean test -Dcucumber.features=src/test/resources/features/cart.feature

# Re-run only the scenarios that failed last time (no "clean", it needs target/rerun.txt)
mvn test -DsuiteXmlFile=src/test/resources/testng-rerun.xml
```

On Windows PowerShell, keep the quotes around `-Dcucumber.filter.tags=...`.

Running from IntelliJ: install the **Cucumber for Java** and **Gherkin** plugins, then right-click a `.feature` file or scenario and choose **Run**. You can also right-click `TestRunner`.

### Reports
- **Allure:** `mvn allure:serve`
- **Cucumber HTML:** `target/cucumber-reports/cucumber.html`. The JSON is next to it, for Jenkins or other report plugins.
- **Failure evidence:** a screenshot is embedded in both reports, and the Playwright trace is attached to Allure and saved in `target/traces/`. Open a trace at https://trace.playwright.dev.

### Parallel execution
Scenarios run in parallel through the TestNG data provider. Change the number of parallel browsers with `data-provider-thread-count` in `testng.xml`.
Each scenario gets its own browser context, and each thread gets its own browser (`ThreadLocal`), so scenarios never share state.

## Writing new tests

1. Add a scenario to a `.feature` file under `src/test/resources/features`, reusing existing steps where possible.
2. Run it. Cucumber prints snippets for any undefined steps.
3. Implement the new steps in the matching `steps/*Steps.java` class. Inject `TestContext` in the constructor, call page-object methods, and assert with Playwright's `assertThat`.
4. Put locators and actions in page objects under `framework/pages`, never in step classes.

### Design rules
- Steps are thin: they read like the Gherkin and delegate to page objects or API clients.
- Use no static state in steps. Share data through `TestContext`, which PicoContainer creates once per scenario.
- Locators use roles, labels or text first. CSS is only a fallback, through `Locator.or()`. No XPath.
- No `Thread.sleep()` or `waitForTimeout()`. Playwright's auto-waiting assertions handle timing.
- Secrets never go in the repo. Pass them as environment variables from Jenkins credentials or an Azure DevOps variable group.

## CI/CD

All pipelines run in `mcr.microsoft.com/playwright/java:v1.62.0-noble`, which has the JDK, Maven and browsers pre-installed. **When you bump `playwright.version` in the pom, keep this image tag in step.**

- **Jenkins (`Jenkinsfile`):**
  - parameters `ENV`, `TAGS` and `CROSS_BROWSER`, with a browser matrix and a nightly cron
  - publishes JUnit results, the Cucumber HTML report and one merged Allure report
  - plugins needed: Docker Pipeline, JUnit, HTML Publisher and Allure
- **Azure DevOps (`azure-pipelines.yml`):**
  - runs on PRs and merges to `main`, plus nightly `@regression`
  - parameters `env` and `tags`, with a browser matrix and a Maven cache
  - publishes JUnit results to the Tests tab; Cucumber, Allure and trace files go out as artifacts, and a final stage builds the merged Allure report
- **Docker:**
  ```bash
  docker build -t pw-java-tests .
  docker run --rm --ipc=host -v "$PWD/target:/app/target" pw-java-tests "-Dcucumber.filter.tags=@smoke"
  ```

## Notes on the demo site
- Shopify may show a bot-protection challenge after repeated failed logins. The invalid-login scenario is tagged `@negative` and kept out of `@smoke` for that reason.
- The cart is stored in cookies, and every scenario uses a fresh browser context, so cart scenarios don't affect each other.
