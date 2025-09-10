FROM maven:3.8.6-openjdk-8 AS build
WORKDIR /app
# 仅复制 pom 先走依赖缓存
COPY pom.xml ./
COPY mvn-settings.xml /root/.m2/settings.xml
RUN mvn -B -DskipTests dependency:go-offline
# 再复制源码
COPY . .
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:8-jre
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=prod
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","/app/app.jar"]


