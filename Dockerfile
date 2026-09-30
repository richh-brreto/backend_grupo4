FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /workspace

# copia o pom e o wrapper primeiro para aproveitar cache de dependências
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B -Dmaven.test.skip=true dependency:go-offline

# aqui copia o código fonte, que é a parte que mais muda, para evitar invalidar o cache das dependências
COPY src ./src
RUN ./mvnw -B -Dmaven.test.skip=true package

# imagem final leve para rodar a aplicação
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# copia o jar gerado pelo builder
COPY --from=builder /workspace/target/*.jar /app/app.jar

# cria usuário sem root para rodar a aplicação, aumentando a segurança
RUN groupadd -r app && useradd -r -g app app && chown -R app:app /app
USER app

ENV JAVA_OPTS=""
ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
