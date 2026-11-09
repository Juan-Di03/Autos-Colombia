-- ================================================================
-- PARTE 2: Tablas y datos iniciales
-- ----------------------------------------------------------------
-- Ejecutar este script conectado a la base de datos "AutosColombia"
-- En pgAdmin: clic derecho sobre "AutosColombia" → Query Tool
-- ================================================================

-- ----------------------------------------------------------------
-- Eliminar tablas existentes para re-deploy limpio
-- (Comentar si ya tienes datos y solo quieres migrar)
-- ----------------------------------------------------------------
DROP TABLE IF EXISTS novedad CASCADE;
DROP TABLE IF EXISTS ticket CASCADE;
DROP TABLE IF EXISTS celda CASCADE;
DROP TABLE IF EXISTS vehiculo CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;

-- ----------------------------------------------------------------
-- TABLA 1: USUARIO
-- ----------------------------------------------------------------
CREATE TABLE usuario (
    codigo    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    documento VARCHAR(30)  UNIQUE NOT NULL,
    nombre    VARCHAR(120) NOT NULL,
    telefono  VARCHAR(25),
    correo    VARCHAR(100),
    password  VARCHAR(255),
    rol       VARCHAR(20)  NOT NULL DEFAULT 'CLIENTE'
        CHECK (rol IN ('ADMINISTRADOR', 'EMPLEADO', 'CLIENTE'))
);

-- ----------------------------------------------------------------
-- TABLA 2: VEHICULO
-- ----------------------------------------------------------------
CREATE TABLE vehiculo (
    placa          VARCHAR(10) PRIMARY KEY,
    tipo           VARCHAR(20) NOT NULL
        CHECK (tipo IN ('AUTOMOVIL', 'MOTOCICLETA')),
    modelo         VARCHAR(40),
    propietario_id BIGINT REFERENCES usuario(codigo) ON DELETE RESTRICT
);

-- ----------------------------------------------------------------
-- TABLA 3: CELDA
-- ----------------------------------------------------------------
CREATE TABLE celda (
    codigo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    numero VARCHAR(15) UNIQUE NOT NULL,
    tipo   VARCHAR(20) NOT NULL
        CHECK (tipo IN ('AUTOMOVIL', 'MOTOCICLETA')),
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE'
        CHECK (estado IN ('DISPONIBLE', 'OCUPADA', 'FUERA_DE_SERVICIO'))
);

-- ----------------------------------------------------------------
-- TABLA 4: TICKET (ESTADIA)
-- ----------------------------------------------------------------
CREATE TABLE ticket (
    codigo               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    placa_vehiculo       VARCHAR(10) NOT NULL REFERENCES vehiculo(placa)   ON DELETE RESTRICT,
    codigo_celda         BIGINT      NOT NULL REFERENCES celda(codigo)     ON DELETE RESTRICT,
    conductor_id         BIGINT      NOT NULL REFERENCES usuario(codigo)   ON DELETE RESTRICT,
    empleado_ingreso_id  BIGINT      NOT NULL REFERENCES usuario(codigo)   ON DELETE RESTRICT,
    empleado_salida_id   BIGINT               REFERENCES usuario(codigo)   ON DELETE RESTRICT,
    fecha_hora_ingreso   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_hora_salida    TIMESTAMP WITH TIME ZONE,
    estado               VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
        CHECK (estado IN ('ACTIVO', 'FINALIZADO')),
    pago_pendiente       BOOLEAN NOT NULL DEFAULT FALSE
);

-- ----------------------------------------------------------------
-- TABLA 5: NOVEDAD
-- ----------------------------------------------------------------
CREATE TABLE novedad (
    codigo        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticket_id     BIGINT REFERENCES ticket(codigo) ON DELETE RESTRICT,
    celda_id      BIGINT REFERENCES celda(codigo)  ON DELETE RESTRICT,
    descripcion   TEXT   NOT NULL,
    tipo_dano     VARCHAR(80),
    fecha         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    estado        VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
        CHECK (estado IN ('PENDIENTE', 'RESUELTA')),
    bloquea_salida BOOLEAN NOT NULL DEFAULT FALSE,
    empleado_id   BIGINT REFERENCES usuario(codigo) ON DELETE RESTRICT,
    CONSTRAINT chk_destino_novedad CHECK (
        (ticket_id IS NOT NULL AND celda_id IS NULL) OR
        (ticket_id IS NULL  AND celda_id IS NOT NULL)
    )
);

-- ----------------------------------------------------------------
-- ÍNDICES DE RENDIMIENTO
-- ----------------------------------------------------------------
CREATE INDEX idx_ticket_placa_estado   ON ticket(placa_vehiculo, estado);
CREATE INDEX idx_ticket_fecha_ingreso  ON ticket(fecha_hora_ingreso);
CREATE INDEX idx_celda_tipo_estado     ON celda(tipo, estado);
CREATE INDEX idx_novedad_ticket        ON novedad(ticket_id);
CREATE INDEX idx_novedad_celda         ON novedad(celda_id);
CREATE INDEX idx_usuario_rol           ON usuario(rol);
CREATE INDEX idx_usuario_documento     ON usuario(documento);

-- ----------------------------------------------------------------
-- DATOS SEMILLA (SEED DATA)
-- ----------------------------------------------------------------

-- Usuarios: 1 Administrador, 2 Empleados, 2 Clientes (Password para todos: 123456)
INSERT INTO usuario (documento, nombre, telefono, correo, password, rol) VALUES
  ('1000000001', 'Juan Diego Johnson',  '3109876543', 'admin@autoscolombia.co',        '$2a$10$ww5Wl8mobEiqCaOAkAto8OL3hwmtrXQKVUa9PaG9VlB12BdWS0sDW', 'ADMINISTRADOR'),
  ('1024567890', 'Valentina Rojas Méndez',  '3001234567', 'valerojas@autoscolombia.co',    '$2a$10$ww5Wl8mobEiqCaOAkAto8OL3hwmtrXQKVUa9PaG9VlB12BdWS0sDW', 'EMPLEADO'),
  ('1024567891', 'Andrés Patiño Torres',    '3156789012', 'andrespatino@autoscolombia.co', '$2a$10$ww5Wl8mobEiqCaOAkAto8OL3hwmtrXQKVUa9PaG9VlB12BdWS0sDW', 'EMPLEADO'),
  ('1009876543', 'Camila Duarte López',     '3123456789', 'camila.d@gmail.com',            '$2a$10$ww5Wl8mobEiqCaOAkAto8OL3hwmtrXQKVUa9PaG9VlB12BdWS0sDW', 'CLIENTE'),
  ('1009876544', 'Ricardo Mejía Suárez',    '3187654321', 'rmejia@hotmail.com',            '$2a$10$ww5Wl8mobEiqCaOAkAto8OL3hwmtrXQKVUa9PaG9VlB12BdWS0sDW', 'CLIENTE');

-- Vehículos de los clientes
INSERT INTO vehiculo (placa, tipo, modelo, propietario_id) VALUES
  ('ABC123', 'AUTOMOVIL',   'Chevrolet Spark 2020', (SELECT codigo FROM usuario WHERE documento = '1009876543')),
  ('XYZ789', 'MOTOCICLETA', 'Honda CB 2022',         (SELECT codigo FROM usuario WHERE documento = '1009876544'));

-- Celdas: 12 Automóvil (A-001 a A-012) + 8 Motocicleta (B-001 a B-008)
INSERT INTO celda (numero, tipo, estado) VALUES
  ('A-001', 'AUTOMOVIL',   'DISPONIBLE'),
  ('A-002', 'AUTOMOVIL',   'DISPONIBLE'),
  ('A-003', 'AUTOMOVIL',   'DISPONIBLE'),
  ('A-004', 'AUTOMOVIL',   'DISPONIBLE'),
  ('A-005', 'AUTOMOVIL',   'DISPONIBLE'),
  ('A-006', 'AUTOMOVIL',   'DISPONIBLE'),
  ('A-007', 'AUTOMOVIL',   'DISPONIBLE'),
  ('A-008', 'AUTOMOVIL',   'DISPONIBLE'),
  ('A-009', 'AUTOMOVIL',   'DISPONIBLE'),
  ('A-010', 'AUTOMOVIL',   'DISPONIBLE'),
  ('A-011', 'AUTOMOVIL',   'DISPONIBLE'),
  ('A-012', 'AUTOMOVIL',   'DISPONIBLE'),
  ('B-001', 'MOTOCICLETA', 'DISPONIBLE'),
  ('B-002', 'MOTOCICLETA', 'DISPONIBLE'),
  ('B-003', 'MOTOCICLETA', 'DISPONIBLE'),
  ('B-004', 'MOTOCICLETA', 'DISPONIBLE'),
  ('B-005', 'MOTOCICLETA', 'DISPONIBLE'),
  ('B-006', 'MOTOCICLETA', 'DISPONIBLE'),
  ('B-007', 'MOTOCICLETA', 'DISPONIBLE'),
  ('B-008', 'MOTOCICLETA', 'DISPONIBLE');

-- ----------------------------------------------------------------
-- VERIFICACIÓN FINAL
-- ----------------------------------------------------------------
SELECT 'USUARIOS' AS tabla, COUNT(*) AS registros FROM usuario
UNION ALL
SELECT 'VEHICULOS',  COUNT(*) FROM vehiculo
UNION ALL
SELECT 'CELDAS',     COUNT(*) FROM celda
UNION ALL
SELECT 'TICKETS',    COUNT(*) FROM ticket
UNION ALL
SELECT 'NOVEDADES',  COUNT(*) FROM novedad;
