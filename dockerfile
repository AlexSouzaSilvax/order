FROM eclipse-temurin:17-jdk-alpine
ENV TZ=America/Sao_Paulo
ENV ORDER_DATABASE_URL=jdbc:postgresql://localhost:5432/your_database
ENV ORDER_DATABASE_USERNAME=your_username
ENV ORDER_DATABASE_PASSWORD=your_password
ENV ORDER_EXTERNO_A_URL=url_api_externo_a
VOLUME /tmp
COPY target/order-0.0.1-SNAPSHOT.jar order.jar
ENTRYPOINT ["java","-Duser.timezone=America/Sao_Paulo","-jar","/order.jar"]