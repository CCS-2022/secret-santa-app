FROM ccsadmindocker/ssbackend:base

COPY *-SNAPSHOT.jar app/ssbackend.jar

WORKDIR /app

CMD ["java", "-jar", "ssbackend.jar" ]
