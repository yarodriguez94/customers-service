# Customers Service

Servicio REST de clientes basado en Spring Boot y PostgreSQL.

## Requisitos

- Docker Engine o Docker Desktop con Docker Compose.

## Levantar la aplicación

Primero levanta el RabbitMQ que administra `banking-service`:

```bash
docker compose -f ../banking-service/compose.yaml up -d rabbitmq
```

Después, desde la raíz de este proyecto, ejecuta:

```bash
docker compose up --build
```

Compose construye la imagen de la aplicación e inicia PostgreSQL 17. Customers se conecta al contenedor `rabbitmq` del Compose de Banking mediante la red externa `banking-service_default`; esa red debe existir antes de iniciar este servicio. Flyway aplica las migraciones al iniciar.

- Aplicación: <http://localhost:8080>
- PostgreSQL: `localhost:5432` (base `customers`, usuario y contraseña `devsu`)
- RabbitMQ compartido: `localhost:5672` (panel de administración en <http://localhost:15672>, credenciales de desarrollo `banking` / `banking`)

Las operaciones de creación, actualización y cambio de estado publican eventos en el exchange `customers.events`. La publicación se ejecuta después del commit de PostgreSQL y utiliza confirmaciones del broker. Como el servicio todavía no usa outbox, un fallo de RabbitMQ se registra y no revierte la operación; ese evento puede perderse y requerir publicación manual.

Para ejecutar Customers en segundo plano, añade `-d` al comando. Para detener PostgreSQL y Customers, ejecuta:

```bash
docker compose down
```

Si algún servicio no inicia, revisa sus logs con:

```bash
docker compose logs -f postgres customers-service
```

Los datos de PostgreSQL se conservan en el volumen `postgres_data`. Para eliminarlos también, ejecuta `docker compose down -v`.

> Si `postgres_data` ya contiene datos creados con PostgreSQL 18, PostgreSQL 17 no podrá abrirlos directamente. Conserva el volumen y realiza una migración compatible antes de cambiar de versión; no elimines el volumen si necesitas esos datos.
