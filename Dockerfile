FROM gradle:8.5-jdk21
WORKDIR /app
COPY . .
EXPOSE 80
RUN chmod +x gradlew
RUN ./gradlew build
CMD ["./gradlew", "run"]