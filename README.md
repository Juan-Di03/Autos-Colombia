# Parqueadero Autos Colombia - Sistema de Gestión Operativa

Sistema de información desarrollado para la gestión de operaciones diarias del parqueadero **"Autos Colombia"**, permitiendo el control de entradas y salidas de vehículos, validación de celdas compatibles, registro de novedades y consistencia transaccional.

---

## 🛠️ Tecnologías y Arquitectura

- **Lenguaje:** Java 17 (LTS)
- **Framework:** Spring Boot 3.3.4
- **Arquitectura:** Modelo-Vista-Controlador (MVC Monolítico)
- **Persistencia:** Spring Data JPA / Hibernate
- **Base de Datos:**
  - **Local inmediata:** H2 en modo PostgreSQL (sin requerir software adicional ni configuraciones externas).
  - **Producción / Externa:** PostgreSQL (archivo DDL incluido).
- **Frontend / Vistas:** Thymeleaf + Bootstrap 5 + Bootstrap Icons (fiel a los mockups de Figma).
- **Pruebas:** JUnit 5, Spring Boot Test (8 pruebas unitarias e integradas).

---

## 📁 Estructura del Proyecto

```text
c:\Users\juan-\Desktop\Autos Colombia\
├── database/
│   └── schema-postgresql.sql       # Script DDL completo con tablas, restricciones y datos semilla
├── src/
│   ├── main/
│   │   ├── java/com/autoscolombia/parqueadero/
│   │   │   ├── config/             # Precarga de datos (20 celdas y usuarios)
│   │   │   ├── controller/         # Controladores MVC (Ingreso y Salida)
│   │   │   ├── dto/                # Data Transfer Objects y validaciones
│   │   │   ├── exception/          # Manejo de reglas de negocio
│   │   │   ├── model/              # Entidades JPA y Enums
│   │   │   ├── repository/         # Interfaces Spring Data JPA
│   │   │   └── service/            # Lógica transaccional
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/css/styles.css # Estilos visuales de Figma
│   │       └── templates/          # Vistas Thymeleaf (ingreso, salida, etc.)
│   └── test/                       # Pruebas automatizadas (8 tests aprobados)
├── .gitignore
├── pom.xml
└── README.md
```

---

## 🚀 Instrucciones para Ejecución Local

### 1. Requisitos Previos
- **Java 17 JDK** o superior.
- **Apache Maven 3.9+**.

### 2. Ejecutar la Aplicación

Desde la terminal en la raíz del proyecto:

```bash
mvn spring-boot:run
```

O compilar y empaquetar el JAR:

```bash
mvn clean package
java -jar target/parqueadero-0.0.1-SNAPSHOT.jar
```

La aplicación iniciará en:
👉 **http://localhost:8080**

### 3. Consola de Base de Datos Embebida (H2 Console)
- **URL:** `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:file:./data/autos_colombia_db;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1;MODE=PostgreSQL`
- **Usuario:** `sa`
- **Contraseña:** *(vacía)*

---

## 🗄️ Conexión con PostgreSQL

Si deseas usar un servidor PostgreSQL (local o en la nube como Neon / Supabase):

1. Ejecuta el script SQL ubicado en:
   `database/schema-postgresql.sql`
2. En `src/main/resources/application.properties`, descomenta las líneas de PostgreSQL:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/autos_colombia
   spring.datasource.driverClassName=org.postgresql.Driver
   spring.datasource.username=postgres
   spring.datasource.password=tu_password
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   ```

---

## 🧪 Pruebas Automatizadas

Para ejecutar la suite de pruebas unitarias e integradas:

```bash
mvn test
```

### Casos de prueba cubiertos:
- **Ciclo 1:**
  - `HU-01, HU-03, HU-04`: Registro válido, asignación automática de celda compatible y registro de fecha/hora.
  - `HU-02`: Rechazo de formato de placa inválido por tipo.
  - `HU-05`: Rechazo de ingreso cuando no hay celdas disponibles para el tipo.
  - `RF-19`: Impedir más de un ingreso activo para el mismo vehículo.
- **Ciclo 2:**
  - `HU-06, HU-07, HU-08`: Salida confirmada, finalización de estadía y liberación atómica de la celda.
  - `HU-09`: Bloqueo de salida ante novedades pendientes de revisión.
  - `HU-09`: Bloqueo de salida ante pago pendiente.
  - `HU-10`: Manejo consistente de vehículo sin ingreso activo.
