# ShadowRepository – Bus Map API

A Spring Boot (3.5, Java 21) REST API scaffold with a Docker container setup and an optional PostgreSQL database. This repo uses a multi-stage Dockerfile and a docker-compose.yml for Docker.

## Project structure

- `bus-map-api/` – Spring Boot application
  - `src/main/java/.../BusMapApiApplication.java` – App entry point
  - `src/main/resources/application.properties` – Base app config
  - `Dockerfile` – Multi-stage build (Maven + Temurin JRE 21)
- `docker-compose.yml` – Services for API and Postgres
- `docker_volume/` – Persistent storage for Postgres data (bind mount)
- `.dockerignore` – Trim build context

## Requirements

- Java 21 (Temurin recommended) if running locally
- Maven 3.9+ if building locally
- Docker with docker compose

## Quick start with containers (Docker)

Build and start both services:

```bash
docker compose up -d --build
```

Check status:

```bash
docker compose ps
```

Stop and remove containers:

```bash
docker compose down
```

> If your host uses SELinux (Fedora, RHEL, etc.), you may need to add `:Z` to the Postgres volume in `docker-compose.yml`:
> `./docker_volume:/var/lib/postgresql/data:Z`

## Services

- API: http://localhost:8080
- Postgres: localhost:5432
  - DB: `busmap`
  - User: `busmap`
  - Password: `busmap`

To connect the Spring app to the DB, set these environment variables (compose already defines the DB service named `db`):

```
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/busmap
SPRING_DATASOURCE_USERNAME=busmap
SPRING_DATASOURCE_PASSWORD=busmap
```

You can add them in `docker-compose.yml` under `bus-map-api.environment` or in `application.properties` for local development.

## Local development (no containers)

Run tests and package the JAR:

```cmd
cd bus-map-api
mvn -B -DskipTests package
```

Run the app:

```cmd
cd bus-map-api
mvn spring-boot:run
```

The app will start on http://localhost:8080 by default.

## Building images manually

Build the image:

```bash
docker build -t bus-map-api:local -f bus-map-api/Dockerfile bus-map-api
```

Run the container:

```bash
docker run --rm -p 8080:8080 --name bus-map-api bus-map-api:local
```

## Configuration

- `JAVA_OPTS` can be set to pass JVM flags (e.g., memory, GC) in `docker-compose.yml`.
- Activate Spring profiles with `SPRING_PROFILES_ACTIVE`.
- Override the server port with `SERVER_PORT` or `server.port` in `application.properties`.

## Database persistence

- Postgres uses a bind mount to `./docker_volume` at the repository root for durability.
- The folder includes a `.gitignore` so data files don’t get committed.

## Troubleshooting

- Port already in use: change the host port mapping in `docker-compose.yml` (e.g., `8081:8080`).
- DB not ready when app starts: compose has a DB healthcheck and `depends_on`; if needed, increase the retries/intervals or add a startup wait in the app.
- On SELinux hosts, ensure the volume has the proper label using `:Z`.

## License

See `LICENSE` for details.
