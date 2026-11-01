# Customers Service

Servicio REST de clientes basado en Spring Boot y PostgreSQL.

## Requisitos

- Docker Engine o Docker Desktop con Docker Compose.

## Levantar la aplicación

Desde la raíz del proyecto, ejecuta:

```bash
docker compose up --build
```

Compose construye la imagen de la aplicación, inicia PostgreSQL 17 y espera a que la base de datos esté disponible antes de arrancar el servicio. Se fija la versión de PostgreSQL para evitar cambios inesperados en el formato y la ruta de almacenamiento de datos. Flyway aplica las migraciones al iniciar.

- Aplicación: <http://localhost:8080>
- PostgreSQL: `localhost:5432` (base `customers`, usuario y contraseña `devsu`)

Para ejecutar en segundo plano, añade `-d` al comando. Para detener los servicios, ejecuta:

```bash
docker compose down
```

Si algún servicio no inicia, revisa sus logs con:

```bash
docker compose logs -f postgres customers-service
```

Los datos de PostgreSQL se conservan en el volumen `postgres_data`. Para eliminarlos también, ejecuta `docker compose down -v`.

> Si `postgres_data` ya contiene datos creados con PostgreSQL 18, PostgreSQL 17 no podrá abrirlos directamente. Conserva el volumen y realiza una migración compatible antes de cambiar de versión; no elimines el volumen si necesitas esos datos.
