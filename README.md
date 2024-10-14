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

### Tabla de auditorias
Se generó la tabla de auditoria, se hizo una en general para todas las tablas, cómo no se están haciendo por el momento validaciones de usuarios, se puso el valor del usuario como algo null por el momento, posteriormente se modificará.

``` SQL
-- Primero, creamos una tabla de auditoría general
CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    action VARCHAR(10) NOT NULL,
    user_id BIGINT,
    old_data JSONB,
    new_data JSONB,
    change_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Función para manejar las auditorías sin user_id
CREATE OR REPLACE FUNCTION audit_trigger_func()
RETURNS TRIGGER AS $$
DECLARE
    old_row JSONB;
    new_row JSONB;
    audit_user_id BIGINT;
BEGIN
    IF TG_TABLE_NAME = 'roles' THEN
        audit_user_id = NULL;
    ELSIF TG_OP = 'DELETE' THEN
        audit_user_id = OLD.user_id;
    ELSE
        audit_user_id = NEW.user_id;
    END IF;

    IF (TG_OP = 'DELETE') THEN
        old_row = row_to_json(OLD)::JSONB;
        INSERT INTO audit_log (table_name, action, old_data, user_id)
        VALUES (TG_TABLE_NAME::TEXT, 'DELETE', old_row, audit_user_id);
        RETURN OLD;
    ELSIF (TG_OP = 'UPDATE') THEN
        old_row = row_to_json(OLD)::JSONB;
        new_row = row_to_json(NEW)::JSONB;
        INSERT INTO audit_log (table_name, action, old_data, new_data, user_id)
        VALUES (TG_TABLE_NAME::TEXT, 'UPDATE', old_row, new_row, audit_user_id);
        RETURN NEW;
    ELSIF (TG_OP = 'INSERT') THEN
        new_row = row_to_json(NEW)::JSONB;
        INSERT INTO audit_log (table_name, action, new_data, user_id)
        VALUES (TG_TABLE_NAME::TEXT, 'INSERT', new_row, audit_user_id);
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;


-- Creamos triggers para las tablas principales

-- Para la tabla users
CREATE TRIGGER users_audit_trigger
AFTER INSERT OR UPDATE OR DELETE ON users
FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

-- Para la tabla favorites
CREATE TRIGGER favorites_audit_trigger
AFTER INSERT OR UPDATE OR DELETE ON favorites
FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

-- Para la tabla img
CREATE TRIGGER img_audit_trigger
AFTER INSERT OR UPDATE OR DELETE ON img
FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

-- Para la tabla img_users
CREATE TRIGGER img_users_audit_trigger
AFTER INSERT OR UPDATE OR DELETE ON img_users
FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

-- Para la tabla roles
CREATE TRIGGER roles_audit_trigger
AFTER INSERT OR UPDATE OR DELETE ON roles
FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

```
## Documentación del proyecto

Toda la documentación del proyecto se podrán encontrar en el swagger dentro del mismo proyecto con la ruta de http://localhost:8080/doc/swagger-ui.html.
