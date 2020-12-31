FROM openjdk:8

MAINTAINER "Landon Patmore <landon.patmore@gmail.com>"
LABEL repo = "Project Omega Bot <https://github.com/LandonPatmore/project-omega-bot>"

ARG BOT_TOKEN
ENV TOKEN=$BOT_TOKEN

COPY . /usr/src/omega

WORKDIR /usr/src/omega

RUN ./gradlew clean shadowJar

WORKDIR build/libs/

CMD ["java", "-jar", "omega.jar"]
