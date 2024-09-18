FROM eclipse-temurin:21-jdk

WORKDIR /

COPY / .

RUN gradle installDist

CMD .build/install/app/bin/app
