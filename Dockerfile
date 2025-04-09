FROM eclipse-temurin:24_36-jdk-alpine-3.21 as build

WORKDIR /bot

RUN apk add maven
COPY . /bot
RUN mvn verify


FROM eclipse-temurin:24_36-jre-alpine-3.21

COPY --from=build /bot/target/anisearch-discord-merch-fetcher-*.jar /bot/anisearch-discord-merch-fetcher.jar

WORKDIR /bot

ENTRYPOINT ["java", "-jar", "anisearch-discord-merch-fetcher.jar"]