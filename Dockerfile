# Runs the suite in the same image the pipelines use.
#   docker build -t pw-java-tests .
#   docker run --rm --ipc=host -v "$PWD/target:/app/target" pw-java-tests -DsuiteXmlFile=src/test/resources/suites/smoke.xml
FROM mcr.microsoft.com/playwright/java:v1.62.0-noble

WORKDIR /app
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src

ENTRYPOINT ["mvn", "-B", "-ntp", "test"]
