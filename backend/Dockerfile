# ---------- Stage 1: Build ----------
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /opt/app

RUN apt-get update && apt-get install -y dos2unix

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN dos2unix mvnw

RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:17-jre-jammy

WORKDIR /opt/app
EXPOSE 8081

# ✅ Install PostgreSQL 16 client (includes pg_dump 16)
RUN apt-get update && \
    apt-get install -y wget gnupg2 lsb-release && \
    echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list && \
    wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add - && \
    apt-get update && \
    apt-get install -y postgresql-client-16 && \
    rm -rf /var/lib/apt/lists/*

COPY --from=builder /opt/app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]