# Manga-reader

Este proyecto implementa una arquitectura monolítica para un sistema de lectura de mangas, utilizando Spring Boot como framework, y una API parra la consulta de los mangas. La api a utilizar es la de MangaDex, toda su documentación la encontrarás en [MangaDex API documentation](https://api.mangadex.org/docs/) y [MangaDex API Swagger](https://api.mangadex.org/docs/swagger.html).

## Estructura del Proyecto

El proyecto está compuesto por los siguientes paquetes:

## Requisitos Previos

- Java 21
- Maven
- PostgreSQL

## Puertos del proyecto

- Proyecto en general: 8080
- PostgreSQL: 5433 (Por defecto para este servicio es el puerto 5432)}

## Dependencias del proyecto

Estas dependencias se pueden encontrar en el `pom.xml`.
## Configuración de la base de datos

Tras levantar la base de datos por medio del proyecto, se harán unas configuraciones a ciertas tablas para evitar la generación de datos duplicados, también se generará una tabla de auditoria que almacenará las modificaciones de los datos dentro de las tablas, tener en cuenta que esta tabla de monitoria no hará parte de la conexión del proyecto con la base de datos, sino, más bien, esto ayudará a conocer las modificaciones hechas a la base de datos.

Las entidades que se manejarán son:
- favorites
- img
- img_users
- roles
- users

Ahora vamos a hacer algunas restricciones a dos entidades, esto lo hacemos ya dentro de la misma base de datos:
``` sql
ALTER TABLE IF EXISTS public.favorites
    ADD CONSTRAINT unique_user_manga UNIQUE (user_id, id_manga);
```

``` sql
ALTER TABLE IF EXISTS public.img_users
    ADD CONSTRAINT unique_user_image UNIQUE (user_id, img_id);
```

Esto hará que no se generen valores duplicados en estas tablas.

## Documentación del proyecto

Toda la documentación del proyecto se podrán encontrar en el swagger dentro del mismo proyecto con la ruta de http://localhost:8080/doc/swagger-ui.html.
