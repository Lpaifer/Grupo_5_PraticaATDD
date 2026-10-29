FROM eclipse-temurin:26-jdk-alpine AS build

WORKDIR /workspace
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
COPY src/ src/
RUN for attempt in 1 2 3; do \
      ./mvnw -B -ntp -DskipTests package && exit 0; \
      if [ "$attempt" -eq 3 ]; then exit 1; fi; \
      sleep 3; \
    done

FROM eclipse-temurin:26-jre-alpine

WORKDIR /app
COPY --from=build /workspace/target/Grupo_5_PraticaATDD-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
