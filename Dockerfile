FROM node:10 AS WEB

WORKDIR app
COPY web/package*.json ./
RUN npm i

COPY web/angular.json ./
COPY web/ts*.json ./
COPY web/e2e ./e2e
COPY web/src ./src

RUN npm run-script build

FROM gradle:5.6.0-jdk8 AS BACK

USER root

WORKDIR /app

COPY --from=WEB app/dist/eitangular ./src/main/resources/public

COPY build.gradle ./

COPY src ./src

RUN gradle :assemble

FROM openjdk:8-jre-alpine AS RUN

USER root

WORKDIR /app

COPY --from=BACK /app/build/libs/app-0.0.1-SNAPSHOT.jar ./main.jar

EXPOSE 8080

CMD ["java", "-jar", "main.jar"]
