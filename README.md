# HardTrack

API REST para la gestión de dispositivos con Spring Boot 4, Spring Security (autenticación JDBC) y JPA/Hibernate.

## Características

- **Gestión de dispositivos**: CRUD completo con control de acceso por roles
- **Gestión de tipos de dispositivo**: Consulta de categorías
- **Sistema de pedidos**: Creación de órdenes con validación de disponibilidad
- **Seguridad**: Autenticación HTTP Basic con roles ADMIN/USER
- **Manejo de errores**: Respuestas unificadas con `@ControllerAdvice`
- **Arquitectura en capas**: Controller → Service → DAO → Entity

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 25 |
| Spring Boot | 4.1.1 |
| Spring Security | (incluido) |
| Spring Data JPA / Hibernate | 7.4.5.Final |
| MySQL Connector/J | (runtime) |
| Maven | 3.9+ |

## Estructura del Proyecto

```text
src/main/java/com/edad/hardtrack/
├── HardtrackApplication.java
├── dao/           # Capa de acceso a datos (EntityManager)
├── dto/           # DTOs para petición/respuesta
├── entity/        # Entidades JPA
├── exception/     # Excepciones y manejador global
├── rest/          # Controladores REST
├── security/      # Configuración de Spring Security
└── service/       # Lógica de negocio
    └── impl/
```

## Endpoints

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/devices` | Público | Listar todos los dispositivos |
| `GET` | `/api/devices/{id}` | Público | Obtener dispositivo por ID |
| `POST` | `/api/devices` | **ADMIN** | Crear dispositivo |
| `PUT` | `/api/devices` | **ADMIN** | Actualizar dispositivo |
| `DELETE` | `/api/devices/{id}` | **ADMIN** | Eliminar dispositivo |
| `POST` | `/api/devices/order` | Público | Crear pedido (valida disponibilidad, marca como no disponible) |
| `GET` | `/api/device-types` | Público | Listar tipos de dispositivo |
| `GET` | `/api/device-types/{id}` | Público | Obtener tipo por ID |

## Configuración

El proyecto usa variables de entorno para las credenciales. Ver sección [Variables de Entorno](#variables-de-entorno).

`src/main/resources/application.properties`:

```properties
spring.application.name=${SPRING_APPLICATION_NAME:hardtrack}
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
server.port=${SERVER_PORT:8097}
```

> **Nota**: La autenticación usa tablas `users` y `authorities` con `JdbcUserDetailsManager`. Las contraseñas usan `NoOpPasswordEncoder` (solo para desarrollo).

## Ejecución

`application.properties` lee las credenciales de variables de entorno y **Spring Boot no carga automáticamente el archivo `.env`**. Hay que exportarlas antes de arrancar; si no, el arranque falla con `'url' must start with "jdbc"`.

Ejemplo con **datos dummy** (sustitúyelos por los de tu entorno):

```bash
cd hardtrack

DB_URL='jdbc:mysql://localhost:3306/hard_track_db' \
DB_USERNAME='usuario_demo' \
DB_PASSWORD='password_demo' \
SERVER_PORT=8097 \
SPRING_APPLICATION_NAME=hardtrack \
./mvnw spring-boot:run
```

También puedes cargar tu `.env` en la shell:

```bash
set -a
. ./.env
set +a
./mvnw spring-boot:run
```

> **Cuidado con caracteres especiales**: con `source`, un `$` sin escapar se expande como variable de shell y puede corromper la contraseña. Entrecomilla los valores o escapa el `$`. También puedes definir las variables en la configuración de ejecución de tu IDE.

La aplicación estará disponible en `http://localhost:8097`

## Tests

```bash
./mvnw test
```

## Documentación Detallada

Para información completa sobre arquitectura, seguridad, flujo de datos, uso de servicios/DTOs y relaciones entre clases, consulta:

- [`data/DOCUMENTACION_HARDTRACK.md`](/HardTrack/data/DOCUMENTACION_HARDTRACK.md)

## Licencia

Proyecto educativo.

## Variables de Entorno

Copia la plantilla al archivo `.env` dentro de `hardtrack/` (la carpeta desde la que se ejecuta Maven):

```bash
cd hardtrack
cp .env.example .env
```

Edita `hardtrack/.env` con los valores de tu entorno. Ejemplo con **datos dummy**:

```env
DB_URL=jdbc:mysql://localhost:3306/hard_track_db
DB_USERNAME=usuario_demo
DB_PASSWORD=password_demo
SERVER_PORT=8097
SPRING_APPLICATION_NAME=hardtrack
```

> Si MySQL está en el host y la aplicación se ejecuta en un contenedor, usa `host.docker.internal` en lugar de `localhost`.

> **Importante**: El archivo `.env` contiene información sensible y está incluido en `.gitignore`. Nunca lo subas a GitHub. Usa `.env.example` como plantilla.

**Carga de variables**: `.env` no se carga automáticamente. Exporta las variables antes de arrancar (ver [Ejecución](#ejecución)), defínelas en tu IDE, o cárgalo con `set -a; . ./.env; set +a`. Evita `export $(cat .env | xargs)` si hay espacios o caracteres especiales.
