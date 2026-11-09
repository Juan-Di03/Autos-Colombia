-- ================================================================
-- PARTE 1: Crear la base de datos AutosColombia
-- ----------------------------------------------------------------
-- Ejecutar este script conectado a la base de datos "postgres"
-- (la base por defecto) desde pgAdmin Query Tool
-- ================================================================

CREATE DATABASE "AutosColombia"
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Spanish_Colombia.1252'
    LC_CTYPE = 'Spanish_Colombia.1252'
    TEMPLATE = template0;
