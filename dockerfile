FROM ccsadmindocker/ssbackend:base

#COPY . .

COPY ./build/libs/*-SNAPSHOT.jar ssbackend.jar

#WORKDIR /app

CMD ["java", "-jar", "ssbackend.jar" ]
