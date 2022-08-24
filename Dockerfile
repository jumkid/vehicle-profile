# Docker for Content Vault microserivce 
FROM openjdk:17-oracle
ARG env
# local file storage path
RUN mkdir -p /opt/vehicle-profile/log
COPY src/main/resources/application.${env}.properties /opt/vehicle-profile/application.properties
COPY target/vehicle-profile-*.jar /opt/vehicle-profile/vehicle-profile.jar
RUN ln -sf /dev/stdout /opt/vehicle-profile/log/vehicle-profile.sys.log

CMD ["java", "-jar", "/opt/vehicle-profile/vehicle-profile.jar", "--spring.config.additional-location=/opt/vehicle-profile/application.properties"]

EXPOSE 8084
