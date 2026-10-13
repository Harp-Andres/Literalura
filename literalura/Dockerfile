FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S literalura && adduser -S literalura -G literalura
WORKDIR /app
COPY --chown=literalura:literalura target/literalura.jar /app/app.jar
USER literalura
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
