# Дипломный проект - Облачное хранилище

### Описание:
Проект представляет из себя REST-севрис разработанный на фреймворке Spring Boot в соответсвии с [протоколом](https://github.com/netology-code/jd-homeworks/blob/master/diploma/CloudServiceSpecification.yaml).
### Интерфейс сервера реализует следующий функционал:
- загрузке файлов
- изменении имени
- вывод списка всех файлов
- скачивание уже сохраненных файлов пользователем
- удаление файлов
- аутентификация и авторизация с помощью логина и пароля, а так же JWTToken'а

### Стек используемых технологий:
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgresSQL
- Gradle
- Liquibase
- Docker
- Docker compose
- log4j2
- JWTToken

### Тесты:
- JUnit Jupiter
- Mockito
- Testcontainers

### Запуск
Находясь в терминале в корневой директории проекта набрать docker compose up, в результате поднимутся два контейнера, непосредственно сервер, а так же БД postgres,
которые будут связаны между собой.

Учетные данные для входа:  
ROLE_USER  
login: bob@mail.ru  
password: password1  
Доступен весь функционал кроме изменения имени.
  
ROLE_ADMIN  
login: mark@mail.ru  
password: password2  
Доступен весь функционал кроме скачивания файла.
