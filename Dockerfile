# ----------------------------------------------------------------------
# Stage 1: Builder - Downloads dependencies and compiles once (as root user)
# ----------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

# Install system dependencies (Chromium/Curl/Allure)
RUN apk add --no-cache chromium chromium-chromedriver curl

# Install Allure CLI
RUN curl -o allure-2.24.1.tgz -L https://github.com/allure-framework/allure2/releases/download/2.24.1/allure-2.24.1.tgz \
    && tar -zxvf allure-2.24.1.tgz -C /opt \
    && mv /opt/allure-2.24.1 /opt/allure

ENV PATH="/opt/allure/bin:$PATH"

WORKDIR /app

# Copy all source files and configuration
COPY . .
RUN chmod -R 777 /app

# Compile and fetch dependencies (this populates /root/.m2 and /app/target)
RUN mvn compile -DskipTests

# ----------------------------------------------------------------------
# Stage 2: Final Test Runner - Copies only necessary, clean artifacts
# ----------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21-alpine AS test-runner-final

# Install system dependencies again
RUN apk add --no-cache chromium chromium-chromedriver curl

# Copy Allure installation
COPY --from=builder /opt/allure /opt/allure
ENV PATH="/opt/allure/bin:$PATH"

WORKDIR /app

# Copy the local Maven repository cache (to avoid re-downloading)
COPY --from=builder /root/.m2 /root/.m2

# Copy the actual source code and configuration files
COPY pom.xml .
COPY src src

# Create the output directories required for Allure and Surefire *before* running tests
RUN mkdir -p /app/allure-results /app/surefire-reports && \
    chmod -R 777 /app