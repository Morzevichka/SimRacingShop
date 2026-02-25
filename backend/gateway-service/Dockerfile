FROM maven:3.9.12-amazoncorretto-25-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY ./src ./src
RUN rm -rf /root/.m2 && mvn clean package -DskipTests

FROM amazoncorretto:25-alpine AS corretto-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

RUN apk add --no-cache binutils
RUN $JAVA_HOME/bin/jlink \
	--verbose \
	--add-modules $(java --list-modules | cut -d@ -f1 | paste -sd ',') \
	--strip-debug \
	--no-man-pages \
	--no-header-files \
	--compress=2 \
	--output /customjre

FROM alpine:3.23
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"
RUN apk add --no-cache curl

COPY --from=corretto-jdk /customjre $JAVA_HOME

ARG APPLICATION_USER=appuser
RUN adduser --no-create-home -u 1000 -D $APPLICATION_USER
RUN mkdir /app

COPY --from=builder /app/target/*.jar /app/app.jar

RUN chown -R $APPLICATION_USER /app \
    && chmod 755 -R /app

WORKDIR /app

USER 1000

EXPOSE 8080

ENTRYPOINT ["/jre/bin/java", "-jar", "app.jar"]