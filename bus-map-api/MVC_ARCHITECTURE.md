# Bus Map API - MVC Architecture Implementation

This document outlines the Model-View-Controller (MVC) architecture implementation for the Bus Map API.

## Architecture Overview

The API follows Spring Boot MVC patterns with clear separation of concerns:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controller    │───▶│    Service      │───▶│   Repository    │
│   (REST API)    │    │   (Business     │    │   (Data Access) │
│                 │    │    Logic)       │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│      DTOs       │    │     Models      │    │    Database     │
│ (Data Transfer) │    │   (Entities)    │    │   (PostgreSQL)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Components

### 1. Controller Layer (`/controller`)

**`HealthController`** - REST endpoints for API health monitoring:

- `GET /api/health` - Comprehensive health status with database connectivity
- `GET /api/health/status` - Simple UP/DOWN status 
- `GET /api/health/ping` - Basic connectivity test

**Features:**
- RESTful endpoint design
- Proper HTTP status codes
- JSON response format
- Error handling with appropriate status codes

### 2. Service Layer (`/service`)

**`HealthService`** - Business logic for health checks:

- Database connectivity validation
- Response time measurement
- Status determination logic
- Error handling and recovery

**Features:**
- Transactional operations
- Database connection testing
- Response time monitoring
- Clean separation from controller logic

### 3. Data Transfer Objects (`/dto`)

**`HealthDto`** - Response data structure:

```json
{
  "status": "UP|DOWN",
  "message": "descriptive message",
  "timestamp": "2025-09-01T21:10:25.131Z",
  "version": "1.0.0",
  "database": {
    "status": "UP|DOWN",
    "responseTimeMs": 150
  }
}
```

### 4. Repository Layer (Ready for Extension)

Currently configured with Spring Data JPA for future entity management.

## Health Endpoints

### Comprehensive Health Check
```bash
GET /api/health
```

**Response Example:**
```json
{
  "status": "UP",
  "message": "Bus Map API is running successfully",
  "timestamp": "2025-09-01T21:10:25.131238671Z",
  "version": "1.0.0",
  "database": {
    "status": "UP",
    "responseTimeMs": 145
  }
}
```

### Simple Status Check
```bash
GET /api/health/status
```

**Response:** `UP` or `DOWN`

### Ping Test
```bash
GET /api/health/ping
```

**Response:** `pong`

## Configuration

### Database Configuration
Located in `application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/bus_map_db
spring.datasource.username=busmap_user
spring.datasource.password=busmap_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### Docker Integration
The service integrates with the docker-compose setup:
- Automatic database connectivity testing
- Container health monitoring
- Environment-based configuration override

## Testing

### Unit Tests
- **`HealthControllerTest`** - Controller layer testing with mocked services
- Validates endpoint responses and status codes
- Tests error handling scenarios

### Integration Tests  
- **`HealthIntegrationTest`** - End-to-end testing with real database
- Validates complete request/response cycle
- Tests actual database connectivity

## Best Practices Implemented

1. **Separation of Concerns**: Clear boundaries between layers
2. **Dependency Injection**: Loose coupling through Spring's IoC
3. **Error Handling**: Proper exception handling with meaningful responses
4. **Testing**: Comprehensive unit and integration tests
5. **Documentation**: Clear API documentation and examples
6. **Health Monitoring**: Built-in health checks for monitoring
7. **Configuration Management**: Externalized configuration
8. **Database Abstraction**: JPA/Hibernate for database independence

## Future Extensions

The current architecture is designed to easily accommodate:

- Additional business entities (Bus, Route, Location, etc.)
- CRUD operations for business data
- Authentication and authorization
- Caching layers
- Additional monitoring endpoints
- Rate limiting and throttling

## Usage Examples

### Using curl
```bash
# Health check
curl http://localhost:8080/api/health

# Simple status
curl http://localhost:8080/api/health/status

# Ping test
curl http://localhost:8080/api/health/ping
```

### Using Docker Compose
```bash
# Start services
docker compose up -d

# Check health
curl http://localhost:8080/api/health

# View logs
docker compose logs bus-map-api
```

This MVC implementation provides a solid foundation for a scalable, maintainable Spring Boot API with proper health monitoring capabilities.
