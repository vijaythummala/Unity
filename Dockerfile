# Use a multi-stage build for efficiency

# Stage 1: Build the back-end (Java Spring Boot)
FROM maven:3.9.4-eclipse-temurin-17 AS backend-build

WORKDIR /app

# Copy back-end source
COPY back-end ./back-end

# Build core_service first (shared dependency)
WORKDIR /app/back-end/core_service
RUN mvn clean install -DskipTests

# Build required services
WORKDIR /app/back-end/eurekaserver
RUN mvn clean package -DskipTests

WORKDIR /app/back-end/api-gateway
RUN mvn clean package -DskipTests

WORKDIR /app/back-end/user-service
RUN mvn clean package -DskipTests

# Build other services (commented out)
# WORKDIR /app/back-end/account_service
# RUN mvn clean package -DskipTests

# WORKDIR /app/back-end/notification_service
# RUN mvn clean package -DskipTests

# WORKDIR /app/back-end/payment_service
# RUN mvn clean package -DskipTests

# WORKDIR /app/back-end/transaction_service
# RUN mvn clean package -DskipTests

# Stage 2: Build the front-end (React.js)
FROM node:20 AS frontend-build

WORKDIR /app

# Copy package files first for better caching
COPY front-end/package*.json ./
RUN npm install

# Copy source code
COPY front-end/ ./

# Build the React app
RUN npm run build

# Stage 3: Create the final image
FROM eclipse-temurin:17-jdk-slim

# Install Node.js
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy built JAR files
COPY --from=backend-build /app/back-end/eurekaserver/target/eurekaserver-0.0.1-SNAPSHOT.jar ./eurekaserver.jar
COPY --from=backend-build /app/back-end/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar ./api-gateway.jar
COPY --from=backend-build /app/back-end/user-service/target/user-service-0.0.1-SNAPSHOT.jar ./user-service.jar

# Copy other JAR files (commented out)
# COPY --from=backend-build /app/back-end/account_service/target/account-service-0.0.1-SNAPSHOT.jar ./account-service.jar
# COPY --from=backend-build /app/back-end/notification_service/target/notification-service-0.0.1-SNAPSHOT.jar ./notification-service.jar
# COPY --from=backend-build /app/back-end/payment_service/target/payment-service-0.0.1-SNAPSHOT.jar ./payment-service.jar
# COPY --from=backend-build /app/back-end/transaction_service/target/transaction-service-0.0.1-SNAPSHOT.jar ./transaction-service.jar

# Copy front-end build
COPY --from=frontend-build /app/dist ./front-end/dist
COPY --from=frontend-build /app/server.js ./front-end/
COPY --from=frontend-build /app/package*.json ./front-end/

# Install frontend dependencies
WORKDIR /app/front-end
RUN npm install

WORKDIR /app

EXPOSE 8080 10000

# Start services
CMD ["sh", "-c", "java -jar eurekaserver.jar & sleep 30 && java -jar api-gateway.jar & sleep 10 && java -jar user-service.jar & sleep 5 && cd front-end && npm run start"]
# Add other services to CMD when needed:
# CMD ["sh", "-c", "java -jar eurekaserver.jar & java -jar api-gateway.jar & java -jar user-service.jar & java -jar account-service.jar & java -jar notification-service.jar & java -jar payment-service.jar & java -jar transaction-service.jar & cd front-end && npm run start"]
