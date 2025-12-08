FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

# Install Chrome for Selenium and Allure
RUN apk add --no-cache \
    chromium \
    chromium-chromedriver \
    curl \
    && rm -rf /var/cache/apk/*

# Install Allure
RUN curl -o allure-2.24.1.tgz -L https://github.com/allure-framework/allure2/releases/download/2.24.1/allure-2.24.1.tgz \
    && tar -zxvf allure-2.24.1.tgz -C /opt \
    && mv /opt/allure-2.24.1 /opt/allure \
    && ln -s /opt/allure/bin/allure /usr/bin/allure \
    && rm allure-2.24.1.tgz

# Set Chrome environment
ENV CHROME_BIN=/usr/bin/chromium-browser \
    CHROME_DRIVER=/usr/bin/chromedriver \
    ALLURE_HOME=/opt/allure

WORKDIR /app
COPY . .
RUN mvn clean compile -DskipTests

# Runtime image
FROM eclipse-temurin:21-jre-alpine

# Install Chrome and Allure dependencies
RUN apk add --no-cache \
    chromium \
    chromium-chromedriver \
    curl \
    bash \
    && rm -rf /var/cache/apk/*

# Copy Allure from builder
COPY --from=builder /opt/allure /opt/allure

# Set environment
ENV CHROME_BIN=/usr/bin/chromium-browser \
    CHROME_DRIVER=/usr/bin/chromedriver \
    ALLURE_HOME=/opt/allure \
    PATH=$PATH:/opt/allure/bin

WORKDIR /app
COPY --from=builder /app /app

# Create directories for results
RUN mkdir -p /app/allure-results /app/surefire-reports

CMD ["mvn", "test", "allure:report"]