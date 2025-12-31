# URL Shortener (Spring Boot)

A simple, production‑ready URL shortening service built with Spring Boot, JPA/Hibernate, and MySQL. It lets you generate short links, redirect users to the original URLs, and collect basic click analytics (timestamp, referrer, rough location).

> Current local datetime: 2025‑12‑31 18:51

---

## Features
- Create short URLs from long URLs
- Optional custom shortcode
- Optional expiry (validity) for links
- Redirect short URL to the original URL
- Click analytics per shortcode:
  - timestamp
  - HTTP referrer (if present)
  - rough location (resolved from IP; best‑effort)
- Simple REST API

## Tech Stack
- Java, Spring Boot
- Spring Web, Spring Data JPA, Hibernate
- MySQL (primary DB)
- Maven

## Project Structure (key parts)
- `src/main/java/org/ganesh/urlshortener/UrlShortenerApplication.java` – app entry point
- `src/main/java/org/ganesh/urlshortener/controller/UrlShortenerController.java` – REST endpoints
- `src/main/java/org/ganesh/urlshortener/service/UrlService.java` – business logic
- `src/main/java/org/ganesh/urlshortener/model/Url.java` – URL entity (shortcode, long URL, created/expiry, click count, etc.)
- `src/main/java/org/ganesh/urlshortener/model/ClickLog.java` – click log entity (timestamp, referrer, location)
- `src/main/java/org/ganesh/urlshortener/repository/*.java` – Spring Data repositories
- `src/main/java/org/ganesh/urlshortener/util/GeoResolver.java` – resolves a rough location from an IP (best‑effort)
- `src/main/resources/application.properties` – configuration

## Getting Started

### Prerequisites
- Java 17+ (recommended)
- Maven 3.8+
- MySQL 8+

### Database Setup
Create a database (e.g., `urlshortenerdb`) in your MySQL instance.

Update the credentials in `src/main/resources/application.properties` to match your local setup:

```
spring.datasource.url=jdbc:mysql://localhost:3306/urlshortenerdb
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

Security note: Never commit real passwords or secrets. Prefer environment variables or externalized configs in production.

You can also configure via environment variables at runtime:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

### Build
```
mvn clean package
```

### Run (two options)
- With Maven:
  ```
  mvn spring-boot:run
  ```
- With the fat JAR:
  ```
  java -jar target/urlshortener-*.jar
  ```
The app starts on `http://localhost:8080` by default.

## API

Base URL: `http://localhost:8080`

### 1) Create short URL
- Endpoint: `POST /shorturls`
- Content-Type: `application/json`
- Body fields:
  - `url` (string, required) – the long/original URL (must be a valid absolute URL)
  - `validity` (integer, optional) – validity in days (expiry will be computed accordingly)
  - `shortcode` (string, optional) – custom shortcode to use (must be unique)

Request example:
```
curl -X POST http://localhost:8080/shorturls \
  -H "Content-Type: application/json" \
  -d '{
        "url": "https://example.com/some/very/long/path?with=params",
        "validity": 30,
        "shortcode": "exmpl"
      }'
```

Success response (201):
```
{
  "shortLink": "http://localhost:8080/exmpl",
  "expiry": "2026-01-30T12:34:56Z"
}
```

Error response (400):
```
{
  "message": "Invalid URL format"
}
```

### 2) Redirect to original URL
- Endpoint: `GET /{shortcode}`
- Behavior: 302 redirect to the original long URL
- Side effect: Records a click with timestamp, HTTP referrer (if present), and approximate location based on IP.

Example:
```
open http://localhost:8080/exmpl
```

### 3) Get stats for a shortcode
- Endpoint: `GET /shorturls/{shortcode}`
- Response example (200):
```
{
  "originalUrl": "https://example.com/some/very/long/path?with=params",
  "createdAt": "2025-12-01T10:15:30Z",
  "expiry": "2026-01-30T12:34:56Z",
  "clicks": 42,
  "clickData": [
    { "timestamp": "2025-12-10T12:00:00Z", "referrer": "https://google.com", "location": "Berlin, DE" },
    { "timestamp": "2025-12-11T08:20:00Z", "referrer": null, "location": "Mumbai, IN" }
  ]
}
```

Not found (404):
```
{
  "message": "Shortcode not found"
}
```

## Configuration Notes
- `spring.jpa.hibernate.ddl-auto` is set to `validate`. Ensure your schema exists and matches the entities. For initial local development you can switch to `update` or `create` to let Hibernate manage the schema, but do not use `create` in production.
- H2 console is disabled (`spring.h2.console.enabled=false`). You can enable it for quick local testing and switch the datasource to H2 if desired.

## Entities (high-level)
- `Url`
  - `id`, `shortcode`, `longUrl`, `createdAt`, `expiry`, `clickCount`
  - `clickLogs` – one‑to‑many relationship with `ClickLog`
- `ClickLog`
  - `id`, `timestamp`, `referrer`, `location`, (and relation to `Url`)

## Running Tests
```
mvn test
```

## Production Considerations
- Use environment variables or a secrets manager for credentials
- Put the app behind a reverse proxy/load balancer (e.g., Nginx)
- Rate limiting and abuse prevention
- Shortcode collision handling and character policy
- Robust IP/location resolution and PII considerations
- Structured logging and tracing
- Observability (metrics, health checks, readiness probes)

## Troubleshooting
- 400 on POST `/shorturls`: ensure `url` is a valid absolute URL (e.g., starts with `http://` or `https://`).
- 404 on `GET /shorturls/{shortcode}`: shortcode doesn’t exist.
- Redirect loops: verify the original URL itself is not pointing back to the short domain.
- DB errors: check MySQL is up, credentials are correct, and the schema matches the entities.


