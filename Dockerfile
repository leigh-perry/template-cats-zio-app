# Build using ./buildDockerImage.sh

FROM openjdk:11-jre-slim

COPY target/pack/lib /app/lib
COPY docker-components/run_app.sh /app/run_app.sh

# buildDockerImage.sh explicitly moves app jars so they can be layered on top of the less-changeable jars
COPY target/pack/appjars /app/lib

# Using a non-privileged user
USER nobody
WORKDIR /app

ENTRYPOINT ["/app/run_app.sh"]
