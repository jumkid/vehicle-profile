# Docker for Content Vault microserivce 
FROM openjdk:17-oracle
ARG env
# local file storage path
RUN mkdir -p /opt/vehicle-profile/logs

COPY certs/ca.crt /opt/vehicle-profile/certs/ca.crt
COPY src/main/resources/application.${env}.properties /opt/vehicle-profile/application.properties
COPY target/vehicle-profile-*.jar /opt/vehicle-profile/vehicle-profile.jar

RUN ln -sf /dev/stdout /opt/vehicle-profile/logs/vehicle-profile.sys.log
WORKDIR /opt/vehicle-profile

CMD ["java", "-jar", "vehicle-profile.jar", "--spring.config.additional-location=application.properties"]

EXPOSE 8084
