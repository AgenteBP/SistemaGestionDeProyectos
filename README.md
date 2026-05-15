# Sistema de Gestión de Proyectos — API REST

Desarrollada  en Spring Boot 4, Java 21 y base de datos en MySQL.

# Luego de la creacion de la base de datos
CREATE DATABASE DB_SISTEMA_GESTION_PROYECTOS;

# Para incluir los datos iniciales de prueba descomentar la primera vez que se ejecuta la aplicacion (en application.properties) y luego volver a comentar para que no se sobreescriba la base de datos

spring.sql.init.mode=always

spring.sql.init.data-locations=classpath:data.sql

spring.jpa.defer-datasource-initialization=true

# Para ver la documentacion una vez levantada la aplicacion
http://localhost:8080/swagger-ui.html
