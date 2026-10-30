# Playwright Java Enterprise Automation Framework

UI + API test automation framework built on **Playwright Java**, **TestNG**, **Allure** and **Maven**, with ready-to-use **Jenkins** and **Azure DevOps** pipelines.
The demo tests target the public Shopify store [sauce-demo.myshopify.com](https://sauce-demo.myshopify.com/).

## Tech stack

| Concern | Tool |
|---|---|
| Language / build | Java 21, Maven |
| Browser + API automation | Playwright Java 1.62 (`Page`, `Locator`, `APIRequestContext`) |
| Test runner | TestNG (groups, suites, data providers, parallel, listeners) |
| Assertions | Playwright web-first `assertThat` (UI), AssertJ (data/API) |
| Reporting | Allure (+ screenshots, traces, API logs), Surefire JUnit XML |
| JSON | Jackson (records as API models) |
| Test data | Datafaker + `ProductData` enum |
| Logging | SLF4J + Log4j2 (console + `target/logs/test-run.log`) |
| CI/CD | Jenkinsfile, azure-pipelines.yml, Dockerfile (official Playwright Java image) |

## Project structure

```
src/main/java/com/ayush/framework
├── config/       ConfigManager       – env files + -D / env-var overrides
├── core/         PlaywrightManager   – ThreadLocal Playwright/Browser per thread
│                 BrowserContextManager – fresh context per test, tracing, video
├── components/   HeaderComponent     – shared header/nav (component object)
├── pages/        BasePage, HomePage, CatalogPage, ProductPage, CartPage, LoginPage, SearchPage, AboutUsPage
├── api/          ApiClient, ProductApiClient, models/Product
├── data/         ProductData (expected catalogue), TestDataFactory (Datafaker)
├── listeners/    SuiteListener, TestListener, RetryAnalyzer, RetryTransformer
└── utils/        AllureAttachments
src/test/java/com/ayush/tests
├── base/         BaseTest            – per-test setup/teardown, failure evidence
├── ui/           HomePageTests, ProductTests, CartTests, LoginTests, SearchTests
└── api/          ProductApiTests
src/test/resources
├── config/       qa.properties, stage.properties
├── suites/       smoke.xml, regression.xml, ui.xml, api.xml
├── log4j2.xml, allure.properties
```

## Test cases included

| Class | Test | Groups |
|---|---|---|
| HomePageTests | Home page loads (title, tagline, empty cart) | smoke, regression |
| | Featured products are displayed | smoke, regression |
| | Catalog shows all 7 products | regression |
| | About Us page opens | regression |
| ProductTests | Product details correct (**data-driven**, every in-stock product) | regression |
| | Open product from catalog | smoke, regression |
| | Sold-out product cannot be added | regression |
| CartTests | Add product to cart → count + cart line | smoke, regression |
| | Add multiple products | regression |
| | Remove product empties cart | regression |
| LoginTests | Login page opens from header | smoke, regression |
| | Invalid login shows error | regression |
| | Forgot password shows reset form | regression |
| SearchTests | Search page opens from header | regression |
| | Search "jacket" returns only jackets | smoke, regression |
| ProductApiTests | Product JSON matches catalogue (**data-driven**, 7 products) | smoke, regression, api |
| | Product endpoint returns JSON 200 | regression, api |
| | Unknown product returns 404 | regression, api |

## Running locally

Prerequisites: JDK 21 and Maven 3.9+. Browsers are downloaded automatically by Playwright on the first run.
(Optional, to pre-install them: `mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps"`.)

```bash
# Default: regression suite, qa env, headless chromium
mvn clean test

# Smoke suite, headed Firefox
mvn clean test -DsuiteXmlFile=src/test/resources/suites/smoke.xml -Dbrowser=firefox -Dheadless=false

# API only
mvn clean test -DsuiteXmlFile=src/test/resources/suites/api.xml

# Other environment + retries
mvn clean test -Denv=stage -Dretry.count=1

# Allure report (opens a browser)
mvn allure:serve
```

Any key in `config/<env>.properties` can be overridden with `-Dkey=value` or an environment variable (`base.url` → `BASE_URL`).

### Debugging failures
For every failed test the framework attaches to Allure: a full-page screenshot, the page URL and the **Playwright trace**.
Traces are also saved to `target/traces/*.zip` — open one with
`mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="show-trace target/traces/<file>.zip"`
or drag it into https://trace.playwright.dev.

## Design rules

- **Thread safety:** Playwright objects are not thread-safe. `PlaywrightManager` keeps one Playwright + Browser per thread (`ThreadLocal`), and every test gets its own `BrowserContext`, so suites run in parallel (`parallel="classes"`) without shared state.
- **Locators:** role/label/text first (`getByRole`, `getByLabel`), CSS only as a fallback via `Locator.or()`. No XPath.
- **Page objects** expose actions and locators, never assertions. Navigation methods return the next page object.
- **Assertions** use Playwright's auto-waiting `assertThat` — no `Thread.sleep()` / `waitForTimeout()`.
- **Data:** expected data lives in `ProductData`; dynamic data comes from `TestDataFactory`, so tests never depend on each other.
- **Secrets** never go in the repo: pass them as environment variables from Jenkins credentials or an Azure DevOps variable group (Key Vault).

## CI/CD

All pipelines run inside `mcr.microsoft.com/playwright/java:v1.62.0-noble` (JDK, Maven and browsers pre-installed). **When you bump `playwright.version` in the pom, bump the image tag too.**

**Jenkins (`Jenkinsfile`)** — parameters `ENV`, `SUITE`, `CROSS_BROWSER`; matrix over chromium/firefox/webkit; publishes JUnit, archives traces/logs and builds one merged Allure report. Needs the Docker Pipeline, JUnit and Allure plugins. Nightly cron included.

**Azure DevOps (`azure-pipelines.yml`)** — runs on PRs/merges to `main` and nightly; parameters `env` and `suite`; browser matrix; Maven cache; publishes JUnit results to the Tests tab, Allure results and failure traces as artifacts; a Report stage merges and generates the Allure HTML report.

**Docker**
```bash
docker build -t pw-java-tests .
docker run --rm --ipc=host -v "$PWD/target:/app/target" pw-java-tests -DsuiteXmlFile=src/test/resources/suites/smoke.xml
```

## Notes on the demo site

- Shopify may show a bot-protection challenge after repeated failed logins from one IP, which can affect `invalidLoginShowsError`. It is kept out of the smoke suite for that reason.
- The cart lives in cookies, and each test uses a fresh browser context, so cart tests don't affect each other.

## Extending the framework

- **Logged-in tests:** save `storageState` after login once and override `storageState()` in a test class.
- **More APIs:** extend `ApiClient`, add record models under `api/models`.
- **Accessibility:** add `com.deque.html.axe-core:playwright` and scan pages in a new `a11y` group.
- **Visual checks:** Applitools/Percy or an image-comparison library (Playwright Java has no built-in `toHaveScreenshot`).
- **Quality gates:** add Checkstyle/SpotBugs plugins and SonarQube analysis to the pipeline.
