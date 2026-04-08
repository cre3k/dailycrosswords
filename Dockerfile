# syntax=docker/dockerfile:1

############################################
# deps stage
############################################

FROM eclipse-temurin:21-jdk-jammy AS deps

WORKDIR /build

COPY --chmod=0755 gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew dependencies --no-daemon || true

############################################
# build stage
############################################

FROM deps AS package

WORKDIR /build

COPY src src

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew bootJar --no-daemon

############################################
# runtime stage
############################################

FROM eclipse-temurin:21-jre-jammy AS final

ARG UID=10001

RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser

USER appuser

COPY --from=package /build/build/libs/*.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java","-jar","app.jar"]