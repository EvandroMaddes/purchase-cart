# Dockerfile
FROM openjdk:21-jdk-slim

# Install Maven with caching
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,target=/var/lib/apt,sharing=locked  \
    apt-get update && apt-get install -y maven
WORKDIR /mnt

# Copy only pom.xml first to cache dependencies
COPY pom.xml /mnt/

# Download dependencies and cache them
RUN mvn dependency:go-offline

# Copy the rest of the project (this prevents unnecessary re-downloads)
COPY . /mnt

EXPOSE 9090
