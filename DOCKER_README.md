# Bus Map API - Docker Setup

This project uses Docker Compose to orchestrate a Spring Boot API with a PostgreSQL database.

## Prerequisites

- Docker
- Docker Compose

## Services

### PostgreSQL Database
- **Container**: `bus-map-postgres`
- **Image**: `postgres:15-alpine`
- **Port**: `5432`
- **Database**: `bus_map_db`
- **User**: `busmap_user`
- **Password**: `busmap_password`

### Spring Boot API
- **Container**: `bus-map-api`
- **Port**: `8080`
- **Built from**: `./bus-map-api/Dockerfile`

## Quick Start

1. **Build and start all services:**
   ```bash
   docker-compose up --build
   ```

2. **Start services in detached mode:**
   ```bash
   docker-compose up -d --build
   ```

3. **View logs:**
   ```bash
   # All services
   docker-compose logs -f
   
   # Specific service
   docker-compose logs -f bus-map-api
   docker-compose logs -f postgres
   ```

4. **Stop services:**
   ```bash
   docker-compose down
   ```

5. **Stop services and remove volumes:**
   ```bash
   docker-compose down -v
   ```

## API Endpoints

Once running, the API will be available at:
- **Base URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Info**: http://localhost:8080/actuator/info

## Database Access

You can connect to the PostgreSQL database using:
- **Host**: localhost
- **Port**: 5432
- **Database**: bus_map_db
- **Username**: busmap_user
- **Password**: busmap_password

### Using psql
```bash
docker exec -it bus-map-postgres psql -U busmap_user -d bus_map_db
```

## Development

### Local Development
For local development without Docker, update the database URL in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bus_map_db
```

### Database Initialization
SQL scripts placed in `./docker_volume/` will be executed when the PostgreSQL container starts for the first time.

## Troubleshooting

1. **Port conflicts**: If ports 8080 or 5432 are in use, modify the port mappings in `docker-compose.yml`

2. **Database connection issues**: Ensure the PostgreSQL container is healthy before the API starts:
   ```bash
   docker-compose ps
   ```

3. **Build issues**: Clean Docker cache if needed:
   ```bash
   docker-compose down
   docker system prune -f
   docker-compose up --build
   ```

4. **View container logs**:
   ```bash
   docker-compose logs postgres
   docker-compose logs bus-map-api
   ```
