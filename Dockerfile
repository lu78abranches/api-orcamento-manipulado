# Estágio 1: Build da aplicação utilizando Maven e Java 17
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copia o arquivo de dependências e baixa para o cache do contêiner
COPY pom.xml .
# Copia o código-fonte do projeto
COPY src ./src

# Compila o projeto gerando o arquivo .jar (ignora testes para o deploy ser ultra veloz)
RUN mvn clean package -DskipTests

# Estágio 2: Execução da aplicação em uma imagem leve e segura
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copia apenas o .jar gerado no estágio anterior para a imagem final
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta interna que configuramos no application.properties
EXPOSE 8082

# Comandos de inicialização configurando o fuso horário e injetando o perfil de produção
ENTRYPOINT ["java", "-Duser.timezone=America/Sao_Paulo", "-jar", "app.jar", "--spring.profiles.active=prod"]
