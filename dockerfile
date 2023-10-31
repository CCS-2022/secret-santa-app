FROM ccsadmindocker/ssbackend:base

#COPY . .

COPY ./build/libs/*-SNAPSHOT.jar app/ssbackend.jar

WORKDIR /app

CMD ["java", "-jar", "ssbackend.jar" ]
