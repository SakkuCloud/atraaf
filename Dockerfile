# Build Stage
FROM majid7221/openjdk:8

WORKDIR /atraaf

COPY . .
RUN set -ex \
    && mvn -X package 

# Main Stage
FROM majid7221/openjdk:8

WORKDIR /app

COPY --from=0 /atraaf/target/sakku-atraaf-jar-with-dependencies.jar /app/atraaf.jar

EXPOSE 8080

ENTRYPOINT ["/usr/bin/dumb-init", "-cv"]
CMD ["java","-jar","atraaf.jar"]
