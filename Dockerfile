# Use a multi-stage build for efficiency

# Stage 1: Build the back-end (Java Spring Boot)
FROM maven:3.9.4-eclipse-temurin-17 AS backend-build

# Set working directory for the back-end
WORKDIR /app

# Copy the back-end source code
COPY back-end ./back-end

# Build each back-end service
WORKDIR /app/back-end/account_service
RUN mvn clean package -DskipTests

WORKDIR /app/back-end/api-gateway
RUN mvn clean package -DskipTests

WORKDIR /app/back-end/eurekaserver
RUN mvn clean package -DskipTests

WORKDIR /app/back-end/notification_service
RUN mvn clean package -DskipTests

WORKDIR /app/back-end/payment_service
RUN mvn clean package -DskipTests

WORKDIR /app/back-end/transaction_service
RUN mvn clean package -DskipTests

WORKDIR /app/back-end/user-service
RUN mvn clean package -DskipTests

# Stage 2: Build the front-end (React.js)
FROM node:18 AS frontend-build

# Set working directory for the front-end
WORKDIR /app/front-end

# Copy the front-end source code
COPY front-end/package.json ./package.json
COPY front-end/package-lock.json ./package-lock.json
COPY front-end ./front-end

# Install dependencies and build the front-end
RUN npm install
RUN npm run dev

# Stage 3: Create the final image
FROM openjdk:17-jdk-slim

# Set working directory for the final image
WORKDIR /app

# Copy the built back-end JAR files
COPY --from=backend-build /app/back-end/account_service/target/account-service-0.0.1-SNAPSHOT.jar ./account-service.jar
COPY --from=backend-build /app/back-end/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar ./api-gateway.jar
COPY --from=backend-build /app/back-end/eurekaserver/target/eurekaserver-0.0.1-SNAPSHOT.jar ./eurekaserver.jar
COPY --from=backend-build /app/back-end/notification_service/target/notification-service-0.0.1-SNAPSHOT.jar ./notification-service.jar
COPY --from=backend-build /app/back-end/payment_service/target/payment-service-0.0.1-SNAPSHOT.jar ./payment-service.jar
COPY --from=backend-build /app/back-end/transaction_service/target/transaction-service-0.0.1-SNAPSHOT.jar ./transaction-service.jar
COPY --from=backend-build /app/back-end/user-service/target/user-service-0.0.1-SNAPSHOT.jar ./user-service.jar

# Copy the built front-end files
COPY --from=frontend-build /app/front-end/build ./front-end

# Expose ports for the back-end and front-end
EXPOSE 8080 3000

# Command to run the back-end and front-end
CMD ["sh", "-c", "java -jar /app/account-service.jar & java -jar /app/eurekaserver.jar & npx serve -s /app/front-end -l 3000"]