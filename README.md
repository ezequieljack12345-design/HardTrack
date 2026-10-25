# HardTrack

API REST para la gestión de dispositivos con Spring Boot 4, Spring Security (autenticación JDBC) y JPA/Hibernate.

## Características

- **Gestión de dispositivos**: CRUD completo con control de acceso por roles
- **Gestión de tipos de dispositivo**: Consulta de categorías
- **Sistema de pedidos**: Creación de órdenes con validación de disponibilidad
- **Seguridad**: Autenticación HTTP Basic con roles ADMIN/USER
- **Acceso de administración**: La gestión de dispositivos está disponible desde la web para administradores; las operaciones de escritura se validan también en el servidor
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
| `POST` | `/api/device-types` | **ADMIN** | Crear un tipo de dispositivo |

En la página **Gestión de Dispositivos**, los administradores pueden crear tipos desde **Nuevo Tipo**. El nombre es obligatorio y admite hasta 45 caracteres.

## Configuración

El proyecto usa variables de entorno para las credenciales. Ver sección [Variables de Entorno](#variables-de-entorno).

`src/main/resources/application.properties`:

```properties
spring.application.name=${SPRING_APPLICATION_NAME:hardtrack}
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
server.port=${SERVER_PORT:8097}
spring.config.import=optional:file:.env[.properties]
```

> **Nota**: La autenticación usa las tablas `users` y `authorities` con `JdbcUserDetailsManager`. Las contraseñas se almacenan con BCrypt. En la base de datos inicial, las credenciales de ejemplo son `admin` / `admin` y `usuari` / `usuari`; el primer arranque convierte los valores existentes a hashes sin cambiar las claves.

## Ejecución

`application.properties` importa opcionalmente el archivo `.env` del directorio desde el que se ejecuta Maven. Al iniciar desde `hardtrack/`, basta con tener allí el archivo `.env` configurado. Las variables definidas directamente en el entorno también se pueden usar y prevalecen sobre las del archivo.

Para preparar el archivo la primera vez:

```bash
cd hardtrack
cp .env.example .env
```

Edita `hardtrack/.env` con los valores de tu entorno y arranca desde `hardtrack/`:

```bash
./mvnw spring-boot:run
```

El `.env` es opcional si ya has definido las variables en el entorno o en la configuración de ejecución de tu IDE. Si no se encuentran `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` ni en `.env` ni en el entorno, la conexión a la base de datos no podrá configurarse y el arranque fallará.

La aplicación estará disponible en `http://localhost:8097`

## Tests

```bash
./mvnw test
```

## Documentación Detallada

Para información completa sobre arquitectura, seguridad, flujo de datos, uso de servicios/DTOs y relaciones entre clases, consulta:

- [`data/DOCUMENTACION_HARDTRACK.md`](data/DOCUMENTACION_HARDTRACK.md)
- [Modelo entidad-relación](/HardTrack/data/MODELO_ER.md)
- [Modelo lógico de la base de datos](/HardTrack/data/MODELO_LOGICO.md)

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

**Carga de variables**: al ejecutar Maven desde `hardtrack/`, Spring Boot importa automáticamente `hardtrack/.env` mediante `spring.config.import=optional:file:.env[.properties]`. También puedes definir las variables directamente en el entorno o en la configuración de ejecución de tu IDE. Si ejecutas la aplicación desde otro directorio, el archivo `.env` se buscará en ese directorio.
