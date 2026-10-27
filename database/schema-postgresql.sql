-- ===============================================================
-- PARQUEADERO AUTOS COLOMBIA - DDL & DML POSTGRESQL
-- Iteración 1: Gestión de entrada y salida de vehículos
-- ===============================================================

-- 1. TABLA: USUARIO
CREATE TABLE IF NOT EXISTS usuario (
    codigo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    documento VARCHAR(30) UNIQUE,
    nombre VARCHAR(120) NOT NULL,
    telefono VARCHAR(25),
    rol VARCHAR(20) NOT NULL DEFAULT 'CLIENTE'
        CHECK (rol IN ('ADMINISTRADOR', 'EMPLEADO', 'CLIENTE'))
);

-- 2. TABLA: VEHICULO
CREATE TABLE IF NOT EXISTS vehiculo (
    placa VARCHAR(10) PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL
        CHECK (tipo IN ('AUTOMOVIL', 'MOTOCICLETA')),
    modelo VARCHAR(40),
    propietario_id BIGINT REFERENCES usuario(codigo) ON DELETE RESTRICT
);

-- 3. TABLA: CELDA
CREATE TABLE IF NOT EXISTS celda (
    codigo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    numero VARCHAR(15) UNIQUE NOT NULL,
    tipo VARCHAR(20) NOT NULL
        CHECK (tipo IN ('AUTOMOVIL', 'MOTOCICLETA')),
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE'
        CHECK (estado IN ('DISPONIBLE', 'OCUPADA', 'FUERA_DE_SERVICIO'))
);

-- 4. TABLA: TICKET (ESTADIA)
CREATE TABLE IF NOT EXISTS ticket (
    codigo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    placa_vehiculo VARCHAR(10) NOT NULL REFERENCES vehiculo(placa) ON DELETE RESTRICT,
    codigo_celda BIGINT NOT NULL REFERENCES celda(codigo) ON DELETE RESTRICT,
    conductor_id BIGINT NOT NULL REFERENCES usuario(codigo) ON DELETE RESTRICT,
    empleado_ingreso_id BIGINT NOT NULL REFERENCES usuario(codigo) ON DELETE RESTRICT,
    empleado_salida_id BIGINT REFERENCES usuario(codigo) ON DELETE RESTRICT,
    fecha_hora_ingreso TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_hora_salida TIMESTAMP WITH TIME ZONE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
        CHECK (estado IN ('ACTIVO', 'FINALIZADO')),
    pago_pendiente BOOLEAN NOT NULL DEFAULT FALSE
);

-- 5. TABLA: NOVEDAD
CREATE TABLE IF NOT EXISTS novedad (
    codigo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticket_id BIGINT REFERENCES ticket(codigo) ON DELETE RESTRICT,
    celda_id BIGINT REFERENCES celda(codigo) ON DELETE RESTRICT,
    descripcion TEXT NOT NULL,
    tipo_dano VARCHAR(80),
    fecha TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
        CHECK (estado IN ('PENDIENTE', 'RESUELTA')),
    bloquea_salida BOOLEAN NOT NULL DEFAULT FALSE,
    empleado_id BIGINT REFERENCES usuario(codigo) ON DELETE RESTRICT,
    CONSTRAINT chk_destino_novedad CHECK (
        (ticket_id IS NOT NULL AND celda_id IS NULL) OR
        (ticket_id IS NULL AND celda_id IS NOT NULL)
    )
);

-- Índices de consulta y rendimiento
CREATE INDEX IF NOT EXISTS idx_ticket_placa_estado ON ticket(placa_vehiculo, estado);
CREATE INDEX IF NOT EXISTS idx_ticket_fecha_ingreso ON ticket(fecha_hora_ingreso);
CREATE INDEX IF NOT EXISTS idx_celda_tipo_estado ON celda(tipo, estado);
CREATE INDEX IF NOT EXISTS idx_novedad_ticket ON novedad(ticket_id);
CREATE INDEX IF NOT EXISTS idx_novedad_celda ON novedad(celda_id);

-- ===============================================================
-- DATOS INICIALES SEMILLA (SEED DATA)
-- ===============================================================

-- Usuarios base de prueba (Empleado y Administrador)
INSERT INTO usuario (documento, nombre, telefono, rol) VALUES
('1024567890', 'Valentina Rojas Méndez', '3001234567', 'EMPLEADO'),
('1000000001', 'Esteban Montoya Molina', '3109876543', 'ADMINISTRADOR'),
('1009876543', 'Camila Duarte López', '3123456789', 'CLIENTE')
ON CONFLICT (documento) DO NOTHING;

-- Celdas según Mockup de Figma (12 de Automóvil, 8 de Motocicleta = 20 celdas)
INSERT INTO celda (numero, tipo, estado) VALUES
('A-001', 'AUTOMOVIL', 'DISPONIBLE'),
('A-002', 'AUTOMOVIL', 'DISPONIBLE'),
('A-003', 'AUTOMOVIL', 'DISPONIBLE'),
('A-004', 'AUTOMOVIL', 'DISPONIBLE'),
('A-005', 'AUTOMOVIL', 'DISPONIBLE'),
('A-006', 'AUTOMOVIL', 'DISPONIBLE'),
('A-007', 'AUTOMOVIL', 'DISPONIBLE'),
('A-008', 'AUTOMOVIL', 'DISPONIBLE'),
('A-009', 'AUTOMOVIL', 'DISPONIBLE'),
('A-010', 'AUTOMOVIL', 'DISPONIBLE'),
('A-011', 'AUTOMOVIL', 'DISPONIBLE'),
('A-012', 'AUTOMOVIL', 'DISPONIBLE'),
('B-001', 'MOTOCICLETA', 'DISPONIBLE'),
('B-002', 'MOTOCICLETA', 'DISPONIBLE'),
('B-003', 'MOTOCICLETA', 'DISPONIBLE'),
('B-004', 'MOTOCICLETA', 'DISPONIBLE'),
('B-005', 'MOTOCICLETA', 'DISPONIBLE'),
('B-006', 'MOTOCICLETA', 'DISPONIBLE'),
('B-007', 'MOTOCICLETA', 'DISPONIBLE'),
('B-008', 'MOTOCICLETA', 'DISPONIBLE')
ON CONFLICT (numero) DO NOTHING;
