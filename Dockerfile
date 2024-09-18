FROM gradle:8.4.0-jdk17

WORKDIR /

COPY / .

RUN gradle installDist

CMD .build/install/app/bin/app
