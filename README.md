# BusMap

BusMap API es el motor de una plataforma de gestión de transporte urbano. Permite gestionar paradas, líneas de autobús, flota de vehículos en tiempo real y un sistema de seguridad basado en roles (RBAC).

## Características Principales

* **Gestión de Red de Transporte:** Modelado completo de paradas y líneas con relaciones Muchos a Muchos (N:M).
* **Seguridad Avanzada:** Sistema de autenticación y autorización mediante Roles y Permisos (RBAC).
* **Seguimiento en Tiempo Real:** Infraestructura preparada para la localización de vehículos y registro de histórico de rutas.
* **Integración de Datos:** Importador automatizado de datos desde archivos JSON.

## Stack Tecnologico

| Tecnología | Uso |
| :--- | :--- |
| Java 21 | Lenguaje de programación principal. |
| Spring Boot 3.5 | Framework para el desarrollo de la API. |
| Spring Data JPA | Persistencia y gestión de la base de datos. |
| PostgreSQL | Base de datos relacional. |
| Spring Security | Protección de rutas y gestión de usuarios. |
| Jackson | Procesamiento de datos JSON. |

## Modelo de Datos

El diseño de la base de datos se ha estructurado para ser escalable y eficiente:

* **Entidades:** Paradas, Líneas, Vehículos, Usuarios, Roles y Permisos.
* **Relaciones:**
    * Paradas <-> Lineas (Muchos-a-Muchos).
    * Vehiculos <-> VehiculosInfo (Uno-a-uno).
    * Usuarios <-> Roles <-> Permisos (RBAC).

> **Nota:** Puedes consultar el boceto completo del modelo de datos en el archivo `boceto modelo de datos.txt` incluido en la raíz del proyecto o en [dbdiagram.io](https://dbdiagram.io/d/BusMap-68b75a73777b52b76cc2c39e).

## Configuracion e Instalacion

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/sergio-gps/BusMap.git
   cd BusMap/bus-map-api

2. **Configurar la base de datos:**
   Crea una base de datos en PostgreSQL y actualiza el archivo src/main/resources/application.properties con tus credenciales:
   ```
   spring.datasource.url=jdbc:postgresql://localhost:5432/busmap_db
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
   spring.jpa.hibernate.ddl-auto=update
   ```
3. **Compilar y ejecutar:**
   ```bash
   mvn clean install
   mvn spring-boot:run

## Estructura del proyecto

- `bus-map-api/` – Spring Boot application
  - `src/main/java/.../BusMapApiApplication.java` – App entry point
  - `src/main/resources/application.properties` – Base app config
  - `Dockerfile` – Multi-stage build (Maven + Temurin JRE 21)
- `docker-compose.yml` – Services for API and Postgres
- `docker_volume/` – Persistent storage for Postgres data (bind mount)
- `.dockerignore` – Trim build context

## Requisitos

- Java 21
- Maven 3.9+
- Docker con docker compose

## Licencia

Ver [LICENSE](https://github.com/sergio-gps/BusMap/blob/main/LICENSE) para más detalles.
