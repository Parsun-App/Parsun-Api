# Entorno Docker local de Parsun

## Alcance

`compose.parsun.yaml` levanta App, API, MySQL y MinIO con datos separados de otros proyectos. `minio-init` crea el bucket y termina correctamente; no es un servicio permanente. Este entorno es de desarrollo. No representa un despliegue de producción ni una extracción a microservicios.

Clona ambos repositorios en carpetas hermanas `Parsun-Api` y `Parsun-App`. El directorio de esta entrega incluye `compose.yaml`, que carga la configuración de la API; también puedes ejecutar directamente el archivo del repositorio:

```powershell
docker volume create parsun-maven-cache
docker compose --env-file ..\.env -f compose.parsun.yaml up -d --build
```

Este comando se ejecuta desde `Parsun-Api`. Las rutas de construcción se resuelven respecto a `compose.parsun.yaml`.

## Variables locales

El `.env` de la carpeta Parsun se ha generado con valores aleatorios y permanece fuera de Git. En una instalación nueva, crea ese archivo y rellena `PARSUN_DB_PASSWORD`, `PARSUN_DB_ROOT_PASSWORD`, `PARSUN_STORAGE_PASSWORD` y `PARSUN_JWT_SECRET`. Usa secretos distintos; JWT debe tener al menos 32 bytes de entropía. No reutilices secretos de otro proyecto ni pongas credenciales en URLs.

Compose exige estas variables. El volumen externo `parsun-maven-cache` solo guarda dependencias Maven; se crea con el comando anterior.

| Servicio | Contenedor | Acceso local | Datos |
| --- | --- | --- | --- |
| App | `parsun-app` | `http://localhost:4201` | Imagen y dependencias propias |
| API | `parsun-api` | `http://localhost:8081` | Java 24 / Spring Boot 3.5.5 |
| MySQL | `parsun-mysql` | `127.0.0.1:3308` | Base `parsun`, volumen `parsun_mysql_data` |
| MinIO API | `parsun-minio` | `http://localhost:9002` | Bucket `parsun-files`, volumen `parsun_minio_data` |
| MinIO consola | `parsun-minio` | `http://localhost:9003` | Usuario `parsun`, contraseña `PARSUN_STORAGE_PASSWORD` |

Los puertos se publican únicamente en la interfaz de loopback. App consume API mediante su proxy `/api` y `/ws`, con el alias interno `parsun-api-app`. Las comprobaciones de salud ordenan el arranque: MySQL/MinIO → bucket → API → App.

## Datos y arranque

Flyway crea el esquema completo al iniciar API. Las migraciones son scripts de creación para una base de desarrollo nueva; no conservan el historial de alteraciones anterior. La base debe inicializarse desde cero con este conjunto de migraciones antes de cargar datos.

### Seed Java de desarrollo

Los datos de desarrollo se cargan mediante `DatabaseSeedRunner` y 29 pasos Java ordenados, uno por cada migración. Configura `PARSUN_SEED_ENABLED=true` en `.env` para ejecutarlos automáticamente después de que Flyway haya aplicado el esquema. Si la variable no existe, el valor predeterminado es `false`.

Acceso local: `gabriel@gmail.com`, contraseña `49510561Gabriel.`, rol `SUPER_ADMIN`. Los pasos son idempotentes, por lo que reiniciar la API con el seed habilitado no duplica registros. Esta opción debe permanecer deshabilitada sobre bases de producción o datos que deban conservarse.

La reconstrucción de esquema vacía requiere eliminar el volumen MySQL local y volver a levantar la API. Este proceso elimina todos los datos locales de Parsun; no debe usarse sobre una base que necesite conservar información. Desde la carpeta Parsun:

```powershell
docker compose --env-file .env down
docker volume rm parsun_mysql_data
docker compose --env-file .env up -d --build
```
MinIO se construye desde las versiones fijadas del servidor y cliente en `Dockerfile.minio`, porque la descarga del repositorio de imágenes original no estaba disponible durante esta entrega. App usa Bun 1.3.3 y `bun.lock` para instalaciones reproducibles. Los cambios de código requieren reconstruir la imagen; este Compose no monta el código del host ni ofrece recarga automática.

## Operación

Desde la carpeta Parsun:

```powershell
docker compose --env-file .env config --quiet
docker compose --env-file .env up -d --build
docker compose --env-file .env ps
docker compose --env-file .env logs --tail 100 api app
docker compose --env-file .env build app
docker compose --env-file .env up -d app
docker compose --env-file .env stop
```

`stop` conserva contenedores y volúmenes. `down` retira contenedores y red, manteniendo volúmenes. No añadas `-v` si necesitas conservar los datos.

## Comprobación

```powershell
docker compose --env-file .env exec api curl --fail --silent http://localhost:8080/actuator/health
docker compose --env-file .env exec app node node_modules/@angular/cli/bin/ng.js build --configuration production
docker compose --env-file .env exec app node node_modules/vitest/vitest.mjs run --coverage
```

La salida de salud debe indicar `UP`. Para pruebas API, usa un contenedor separado del proceso de aplicación y ejecuta `mvn verify`; no ejecutes Flyway de pruebas contra la base local activa. Los resultados observados de esta entrega se documentan en [MIGRATION-VERIFICATION.md](MIGRATION-VERIFICATION.md).
