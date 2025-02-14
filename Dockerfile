FROM openjdk:8u181-jdk-alpine
WORKDIR /app
# 假设打包后的 jar 位于 target 目录下，文件名根据实际情况调整
COPY target/java-sec-code-1.0.0.jar jsc.jar
EXPOSE 8080
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "jsc.jar"]
