# Etapa 1: Construcción (Build)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copiamos el pom.xml y descargamos dependencias primero (optimiza la caché de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B
# Copiamos el código fuente y compilamos el JAR saltando los tests
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Runtime)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copiamos el JAR generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar
# Exponemos el puerto (Render inyectará el suyo, pero es buena práctica documentarlo)
EXPOSE 8080
# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
