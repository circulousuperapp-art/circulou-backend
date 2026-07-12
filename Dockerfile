# Estágio de Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia o pom.xml e o código fonte
COPY pom.xml .
COPY src ./src

# Gera o artefato (jar) ignorando os testes para agilizar o build da imagem
RUN mvn clean package -DskipTests

# Estágio de Execução (Runtime)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia apenas o artefato final (jar) para a imagem de runtime
COPY --from=build /app/target/circulou-backend-*.jar app.jar

# Exposição da porta padrão da aplicação
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
