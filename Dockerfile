FROM openjdk:8

COPY ./out/production/CBRO /tmp

COPY ./lib/*.jar /tmp

COPY ./dist/lib/* /tmp

COPY ./data /tmp/data/

WORKDIR /tmp

ENV CLASSPATH=/tmp:/tmp/*:/tmp/data/*

ENTRYPOINT ["java", "com.girlkun.server.ServerManager"]