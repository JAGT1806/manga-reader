# Manga Reader

## Descripción

Manga Reader es una API REST desarrollada con Spring Boot que permite a los usuarios buscar mangas y añadirlos a sus favoritos. Para gestionar los favoritos, los usuarios deben autenticarse. La API implementa seguridad mediante JWT y expone sus endpoints a través de Swagger.
Toda información acerca de los mangas es extraída de API de MangaDex.
- [Documentación de la API de MangaDex](https://api.mangadex.org/docs/)
- [Swagger de la API de MangaDex](https://api.mangadex.org/docs/swagger.html)
## Tecnologías utilizadas

- Spring Boot

- PostgreSQL

- JWT (JSON Web Token) para autenticación

- Docker & Docker Compose

## Instalación y configuración

### Requisitos previos

- Java 21

- Maven

- PostgreSQL

- Docker (opcional, para despliegue en contenedores)

### Variables de entorno

La aplicación utiliza un archivo .env para la configuración de variables de entorno. Puedes ver las variables de entono utilizada en el .example.env

### Ejecución local

- Clona el repositorio:
```sh
git clone https://github.com/JAGT1806/manga-reader
cd manga-reader
```
> Configura las variables de entorno en un archivo .env.

- Compila y ejecuta la aplicación:
```sh
mvn spring-boot:run
```

### Ejecución con Docker

- Construye y ejecuta los contenedores con Docker Compose:
``` sh
docker-compose up --build
```

## Documentación de la API

Los endpoints de la API pueden consultarse a través de Swagger. Una vez en ejecución, accede a:

http://localhost:8080/swagger-ui.html

SI estableciste otra ruta en el .env, accedes a esa

## Seguridad y autenticación

La autenticación se realiza mediante JWT.

Para acceder a funcionalidades como agregar mangas a favoritos, el usuario debe autenticarse.

## Licencia

Este proyecto está bajo la Licencia MIT.

Autor: Jhon Alexander Gómez Trujillo
Proyecto desarrollado con fines educativos y de aprendizaje.