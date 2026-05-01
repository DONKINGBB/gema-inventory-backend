# Fase 1: Compilación
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copiar el archivo pom.xml y descargar dependencias (para caché)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Fase 2: Ejecución
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copiar el archivo JAR generado desde la fase de compilación
# Nota: El nombre del JAR suele ser [artifactId]-[version].jar
# Usamos un comodín para que funcione independientemente del nombre exacto
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto 8080
EXPOSE 8080

# Comando para iniciar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
