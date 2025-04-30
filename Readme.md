Приложение банковских карт



Для запуска в контейнере выполнить команду из директории программы
mvn clean package -DskipTests
docker-compose -f docker-compose.yml up --build
После можно отправлять запросы с помощью Postman

Для запуска на локальной машине необходимо иметь PostgreSql
Выполнить команду
liquibase update для миграции базы данных
после на локально машине уже можно будет совершать запросы.

spring.datasource.url=jdbc:postgresql://localhost:5432/banksCards
spring.datasource.url=jdbc:postgresql://postgres:5450/banksCards

Адресс для обращения в контейнере
http://localhost:8081/
Адресс для обращения на локальной машине
http://localhost:8080/operations/createNewCard

для просмотра всех методов с помощью swagger 
нужно обратиться по ссылке [http://localhost:8080/swagge-ui/index.html
