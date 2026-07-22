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

# Cria um usuário não-privilegiado
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copia apenas o artefato final (jar) para a imagem de runtime
COPY --from=build /app/target/circulou-backend-*.jar app.jar

# Exposição da porta padrão da aplicação
EXPOSE 8080

# Comando para iniciar a aplicação utilizando o formulário exec para propagação de sinais (SIGTERM)
ENTRYPOINT ["java", "-jar", "app.jar"]
