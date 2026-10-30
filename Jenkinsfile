// Jenkins declarative pipeline for the Playwright Java framework.
// Plugins required: Docker Pipeline, JUnit, HTML Publisher, Allure Jenkins Plugin, (optional) Credentials Binding.
pipeline {
    agent none

    parameters {
        choice(name: 'ENV',     choices: ['qa', 'stage'], description: 'Target environment (config/<env>.properties)')
        string(name: 'TAGS',    defaultValue: '@smoke', description: 'Cucumber tag expression, e.g. @smoke, @regression, "@cart and not @negative"')
        booleanParam(name: 'CROSS_BROWSER', defaultValue: false, description: 'Run UI on chromium, firefox and webkit in parallel')
    }

    options {
        timestamps()
        timeout(time: 60, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }

    triggers {
        // Nightly regression at ~01:00. PR/push builds come from the multibranch/webhook setup.
        cron('H 1 * * *')
    }

    stages {
        stage('Test') {
            matrix {
                axes {
                    axis {
                        name 'BROWSER'
                        values 'chromium', 'firefox', 'webkit'
                    }
                }
                when {
                    anyOf {
                        expression { params.CROSS_BROWSER }
                        expression { env.BROWSER == 'chromium' }
                    }
                }
                agent {
                    docker {
                        // Keep this tag equal to <playwright.version> in pom.xml
                        image 'mcr.microsoft.com/playwright/java:v1.62.0-noble'
                        args '-v $HOME/.m2:/root/.m2 --ipc=host'
                    }
                }
                stages {
                    stage('Run') {
                        steps {
                            // Example of injecting a secret: add a Jenkins "Secret text" credential with id 'api-key'
                            // withCredentials([string(credentialsId: 'api-key', variable: 'API_KEY')]) { ... }
                            sh """
                                mvn -B -ntp clean test \
                                  -Denv=${params.ENV} \
                                  -Dbrowser=${BROWSER} \
                                  -Dheadless=true \
                                  -Dretry.count=1 \
                                  "-Dcucumber.filter.tags=${params.TAGS}"
                            """
                        }
                        post {
                            always {
                                junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                                // Cucumber's own HTML report (needs the HTML Publisher plugin)
                                publishHTML(target: [reportDir: 'target/cucumber-reports', reportFiles: 'cucumber.html',
                                                     reportName: "Cucumber Report - ${BROWSER}", keepAll: true, allowMissing: true,
                                                     alwaysLinkToLastBuild: true])
                                archiveArtifacts artifacts: 'target/traces/**, target/videos/**, target/logs/**', allowEmptyArchive: true
                                stash name: "allure-${BROWSER}", includes: 'target/allure-results/**', allowEmpty: true
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                node {
                    ['chromium', 'firefox', 'webkit'].each { b ->
                        try { unstash "allure-${b}" } catch (ignored) { /* browser not run */ }
                    }
                    allure results: [[path: 'target/allure-results']]
                }
            }
        }
    }
}
