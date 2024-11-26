# Manga-reader

Este proyecto implementa una arquitectura monolítica para un sistema de lectura de mangas, utilizando Spring Boot como framework, y una API parra la consulta de los mangas. La api a utilizar es la de MangaDex, toda su documentación la encontrarás en [MangaDex API documentation](https://api.mangadex.org/docs/) y [MangaDex API Swagger](https://api.mangadex.org/docs/swagger.html).

## Estructura del Proyecto

Anteriormente dicho, este proyecto cuenta con un sistema de ficheros monolito, si estructura es la siguiente:
```
- com.proyecto.mangareader.app


```


## Requisitos Previos

- Java 21
- Maven
- PostgreSQL 16

## Puertos del proyecto

- Proyecto del proyecto: 9000
- PostgreSQL: 5432 

## Dependencias del proyecto

Estas dependencias se pueden encontrar en el `pom.xml`.
- **Spring JPA:** Java Persistent Api, una dependencia que nos permite hacer la manipulación de la base de datos y que estos persistan dentro de esta.
- **Lombok:** Dependencia que permite la reducción significativa de código, permitiendo la creación de los constructores, getters, setters, entre otros.
- **PostgreSQL Driver:**  Controlador que permite la conexión con la base de datos.
* **Spring Docs:** Dependencia para la documentación del backend.
* **OpenFeign:** Permite la realización a petición de un endpoint y/o una API externa.
* **Spring Security:** Se encarga de la seguridad del backend.
* **JWT:** Se encargará de la autenticación de usuarios por tokens.
* **Java Mail Sender:** Usado para enviar

## Configuración de la base de datos

Tras levantar la base de datos por medio del proyecto, se harán unas configuraciones a ciertas tablas para evitar la generación de datos duplicados, también se generará una tabla de auditoria que almacenará las modificaciones de los datos dentro de las tablas, tener en cuenta que esta tabla de monitoria no hará parte de la conexión del proyecto con la base de datos, sino, más bien, esto ayudará a conocer las modificaciones hechas a la base de datos.

Las entidades que se manejarán son:
- **favorites** : Se almacenarán los mangas favoritos del usuario
- **img**: Almacena las imágenes que se pueden colocar los usuarios.
- **roles** : Almacena los roles
- **users** : Almacena la información de los usuarios.
- **user_role:** Almacena el user id y 

Ahora vamos a hacer algunas restricciones a dos entidades, esto lo hacemos ya dentro de la misma base de datos:
``` sql
ALTER TABLE IF EXISTS public.favorites
    ADD CONSTRAINT unique_user_manga UNIQUE (user_id, id_manga);
```


Esto hará que no se generen valores duplicados en estas tablas.

### Tabla de auditorias
Se generó la tabla de auditoria, se hizo una en general para todas las tablas, cómo no se están haciendo por el momento validaciones de usuarios, se puso el valor del usuario como algo null por el momento, posteriormente se modificará.

Tener en cuenta que en el paquete de `entities` se encuentran todas las entidades y en el paquete de `audit` se encontrará todo con respecto a la auditoría. Posteriormente al levantar el proyecto, se inserta el siguiente query en la base de datos:

``` SQL
-- Función para manejar las auditorías 
CREATE OR REPLACE FUNCTION audit_trigger_function() 
RETURNS TRIGGER AS $$ 
DECLARE 
	old_data JSON; 
	new_data JSON; 
	current_user_id BIGINT; 
	current_username VARCHAR; 
BEGIN 
	-- Intentar obtener el usuario actual del contexto de la aplicación 
	-- Si no existe, asignar valores por defecto para sistema 
	BEGIN 
		current_user_id := current_setting('app.current_user_id')::BIGINT; 
		current_username := current_setting('app.current_username')::VARCHAR; 
	EXCEPTION WHEN OTHERS THEN 
		current_user_id := 0; 
		current_username := 'SYSTEM'; 
	END; 
	
	IF (TG_OP = 'DELETE') THEN 
		old_data := row_to_json(OLD); 
		new_data := NULL; 
	ELSIF (TG_OP = 'UPDATE') THEN 
		old_data := row_to_json(OLD); 
		new_data := row_to_json(NEW); 
	ELSIF (TG_OP = 'INSERT') THEN 
		old_data := NULL; 
		new_data := row_to_json(NEW); 
	END IF; 
	
	INSERT INTO audits ( 
		action, 
		table_name, 
		old_data, 
		new_data, 
		timestamp, 
		user_id, 
		username 
	) VALUES ( 
		TG_OP, 
		TG_TABLE_NAME, 
		old_data, 
		new_data, 
		CURRENT_TIMESTAMP, 
		current_user_id, 
		current_username 
	); 
	RETURN NEW; 
END; 
$$ LANGUAGE plpgsql; 


-- Crear triggers para cada tabla que queremos auditar 
CREATE TRIGGER users_audit_trigger 
AFTER INSERT OR UPDATE OR DELETE ON users 
FOR EACH ROW EXECUTE FUNCTION audit_trigger_function(); 

CREATE TRIGGER favorites_audit_trigger 
AFTER INSERT OR UPDATE OR DELETE ON favorites 
FOR EACH ROW EXECUTE FUNCTION audit_trigger_function(); 

CREATE TRIGGER img_audit_trigger 
AFTER INSERT OR UPDATE OR DELETE ON img 
FOR EACH ROW EXECUTE FUNCTION audit_trigger_function(); 

CREATE TRIGGER roles_audit_trigger 
AFTER INSERT OR UPDATE OR DELETE ON roles 
FOR EACH ROW EXECUTE FUNCTION audit_trigger_function(); 


-- Inserción de imagen por defecto
INSERT INTO img (url)  
VALUES ('https://i.ibb.co/WfP4Wv0/585e4beacb11b227491c3399.png')  
ON CONFLICT DO NOTHING;

-- Inserción inicial de roles
INSERT INTO roles (rol) VALUES ('ROLE_ADMIN'), ('ROLE_USER') 
ON CONFLICT (rol) DO NOTHING; 

-- Crear usuario admin inicial 
-- La contraseña deberá ser hasheada antes de insertar
INSERT INTO users (  
    username,  
    email,  
    password,  
    date_create,  
    profile_image_id,
    enable  
)  
SELECT  
    'admin',  
    'admin@mangareader.com',  
    -- '$2a$10$st4YvGfdaVAhnExpAeWH6eSZP.cqHoWu8GCFP9GlRiD6IpwNWFcTO',
    -- Asegúrate de cambiar esto por la contraseña hasheada real
	'$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 
	-- En este caso se usará 'admin123'
	-- $2a$10$st4YvGfdaVAhnExpAeWH6eSZP.cqHoWu8GCFP9GlRiD6IpwNWFcTO
	-- Puede cambiar luego esta contraseña.  
    CURRENT_TIMESTAMP,  
    i.id,
    true  
FROM img i  
WHERE i.url = 'https://i.ibb.co/WfP4Wv0/585e4beacb11b227491c3399.png'  
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)  
SELECT u.id_user, r.id_rol  
FROM users u  
         CROSS JOIN roles r  
WHERE u.email = 'admin@mangareader.com'  
  AND r.rol = 'ROLE_ADMIN'  
ON CONFLICT DO NOTHING;

-- Función para establecer el usuario actual 
CREATE OR REPLACE FUNCTION set_current_user(p_user_id BIGINT, p_username VARCHAR)
RETURNS VOID AS $$ 
BEGIN 
	PERFORM set_config('app.current_user_id', p_user_id::TEXT, false); 
	PERFORM set_config('app.current_username', p_username, false); 
END; 
$$ LANGUAGE plpgsql;
```

## Documentación del proyecto

Toda la documentación del proyecto se podrán encontrar en el swagger dentro del mismo proyecto con la ruta de http://localhost:8080/doc/swagger-ui.html.

Además de poder observar el resto de documentación en los javadocs.

