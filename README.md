# Product Service

Microservicio CRUD reactivo para productos, implementado con Java 17, Spring Boot 3, WebFlux y Spring Data R2DBC con H2
en memoria.

## Arquitectura

`controller` gestiona HTTP y validación; `service` contiene reglas y mapeo; `repository`
usa `ReactiveCrudRepository`; `entity` representa persistencia; `dto` define contratos con Records; `exception`normaliza
errores. El flujo HTTP → servicio → repositorio permanece reactivo, sin JDBC/JPA ni llamadas `block()`.

## Requisitos

Java 17+ y Maven 3.9+ para ejecución local; Docker Engine y Docker Compose para contenedores. El proyecto incluye
scripts Maven Wrapper (`mvnw`, `mvnw.cmd`) y configuración en `.mvn/wrapper`; si el wrapper no está disponible en el
entorno, use Maven instalado.

## Ejecutar localmente

```bash
mvn clean spring-boot:run
```

Windows PowerShell: `mvn.cmd clean spring-boot:run`. Servicio en http://localhost:8080.

## Docker

```bash
docker compose up --build
```

Detener: `docker compose down`. H2 vive en memoria; los datos se reinician al detener la aplicación.

## Tests y cobertura

```bash
mvn clean test
mvn verify
```

JaCoCo genera `target/site/jacoco/index.html`. En `verify` se valida cobertura de líneas mínima del 70%
para `com.challenge.productservice.service`; el build falla si no se alcanza.

## API

Base URL: `/api/v1/products`.

| Método | Ruta                    | Resultado            |
|--------|-------------------------|----------------------|
| POST   | `/api/v1/products`      | 201, producto creado |
| GET    | `/api/v1/products`      | 200, lista           |
| GET    | `/api/v1/products/{id}` | 200 o 404            |
| PUT    | `/api/v1/products/{id}` | 200 o 404            |
| DELETE | `/api/v1/products/{id}` | 204 o 404            |

### Crear

```bash
curl -i -X POST http://localhost:8080/api/v1/products -H "Content-Type: application/json" -d '{"name":"Laptop","description":"Laptop empresarial","price":2500.00,"stock":10,"active":true}'
```

Respuesta de ejemplo (los identificadores y timestamps son generados):

```json
{
  "id": 1,
  "name": "Laptop",
  "description": "Laptop empresarial",
  "price": 2500.00,
  "stock": 10,
  "active": true,
  "createdAt": "2026-09-29T10:00:00",
  "updatedAt": "2026-09-29T10:00:00"
}
```

### Listar y consultar

```bash
curl http://localhost:8080/api/v1/products
curl http://localhost:8080/api/v1/products/1
```

### Actualizar

```bash
curl -i -X PUT http://localhost:8080/api/v1/products/1 -H "Content-Type: application/json" -d '{"name":"Laptop Pro","description":"Equipo empresarial","price":2800.00,"stock":7,"active":true}'
```

### Eliminar

```bash
curl -i -X DELETE http://localhost:8080/api/v1/products/1
```

### Error 404

```json
{
  "timestamp": "2026-09-29T15:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product with id 99 not found",
  "path": "/api/v1/products/99",
  "details": {}
}
```

### Error 400

Los campos inválidos devuelven `400 Bad Request`, `message` descriptivo y `details` con errores por campo. `name` es
obligatorio (máximo 120); `description` es opcional (máximo 1000); `price` debe ser ≥ 0.01; `stock` ≥ 0. `active` es
opcional y por defecto `true` al crear; en actualización, `null` conserva el valor actual.

## Decisiones técnicas

- Java 17 y Records para DTOs.
- `BigDecimal` para precios y `LocalDateTime` para auditoría.
- H2 R2DBC en memoria, inicializada con `schema.sql`.
- JaCoCo mide líneas del paquete de servicio.
