# Autos Colombia - Sistema de Gestión de Parqueadero

Repositorio del caso de estudio de Autos Colombia para la materia de Ingeniería de Software.

Sistema de información desarrollado para la gestión de operaciones diarias del parqueadero **"Autos Colombia"**, permitiendo el control de entradas y salidas de vehículos, gestión de usuarios, tablero de celdas en tiempo real, registro de novedades y consistencia transaccional.

---

## 🛠️ Tecnologías y Arquitectura

- **Lenguaje:** Java 17 (LTS)
- **Framework:** Spring Boot 3.3.4
- **Arquitectura:** Modelo-Vista-Controlador (MVC)
- **Persistencia:** Spring Data JPA / Hibernate
- **Base de Datos:**
  - **Entorno Local / Producción:** PostgreSQL (`AutosColombia`) con scripts DDL y DML estructurados en la carpeta `database/`.
  - **Entorno de Pruebas:** H2 en memoria (`mem:testdb`) para ejecución automatizada desacoplada.
- **Frontend / Vistas:** Thymeleaf + Bootstrap 5 + Bootstrap Icons (inspirado en los mockups de Figma).
- **Pruebas Automatizadas:** JUnit 5, Spring Boot Test, MockMvc (23 pruebas automatizadas unitarias e integradas - 100% aprobadas).

---

## 📁 Estructura del Proyecto

```text
Autos Colombia/
├── database/
│   ├── 01_crear_base_de_datos.sql      # Script para crear la base de datos AutosColombia
│   ├── 02_tablas_y_datos.sql           # Tablas, índices y datos semilla
│   └── schema-postgresql.sql           # Guía de despliegue en PostgreSQL
├── src/
│   ├── main/
│   │   ├── java/com/autoscolombia/parqueadero/
│   │   │   ├── config/                 # Precarga inicial en entorno de pruebas
│   │   │   ├── controller/             # Estadia, Usuario, Celda, Novedad, Historial, Consulta
│   │   │   ├── dto/                    # RegistroIngresoDto, ValidacionSalidaDto
│   │   │   ├── exception/              # ReglaNegocioException
│   │   │   ├── model/                  # Usuario, Vehiculo, Celda, Estadia, Novedad y Enums
│   │   │   ├── repository/             # Interfaces Spring Data JPA
│   │   │   └── service/                # Lógica transaccional de negocio
│   │   └── resources/
│   │       ├── application.properties.template # Plantilla para configuración segura
│   │       ├── static/css/styles.css   # Estilos visuales
│   │       └── templates/              # Vistas Thymeleaf (estadias, usuarios, celdas, novedades, etc.)
│   └── test/                           # 23 Pruebas automatizadas (Unitarias y de Integración)
├── .gitignore
├── pom.xml
└── README.md
```

---

## 🗄️ Despliegue de la Base de Datos PostgreSQL

1. **Crear la Base de Datos:**
   - Abre **pgAdmin** o la consola `psql` conectado a la base `postgres`.
   - Ejecuta el script: `database/01_crear_base_de_datos.sql`
2. **Crear Tablas y Datos Semilla:**
   - Conéctate a la base de datos `AutosColombia`.
   - Ejecuta el script: `database/02_tablas_y_datos.sql`
3. **Configuración de la Aplicación:**
   - Copia `src/main/resources/application.properties.template` como `src/main/resources/application.properties` y ajusta tus credenciales locales (`username` y `password`).

---

## 🚀 Ejecución de la Aplicación

Desde la terminal en la raíz del proyecto:

```bash
mvn spring-boot:run
```

La aplicación iniciará en:
👉 **http://localhost:8081**

### Rutas principales:
- **Ingreso de Vehículos:** `http://localhost:8081/ingreso`
- **Salida de Vehículos:** `http://localhost:8081/salida`
- **Gestión de Usuarios:** `http://localhost:8081/usuarios`
- **Tablero de Celdas:** `http://localhost:8081/celdas`
- **Novedades de Celda:** `http://localhost:8081/novedades/celda`
- **Novedades de Vehículo:** `http://localhost:8081/novedades/vehiculo`
- **Historial de Estadías:** `http://localhost:8081/historial`
- **Consulta de Celda (Cliente):** `http://localhost:8081/consultar-celda`

---

## 🧪 Pruebas Automatizadas

Para ejecutar la suite completa de pruebas:

```bash
mvn test
```

**Resultado:**
- 23 pruebas ejecutadas (0 fallos, 0 errores, BUILD SUCCESS).
