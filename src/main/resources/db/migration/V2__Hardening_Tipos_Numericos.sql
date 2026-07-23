-- V2__Hardening_Tipos_Numericos.sql
-- Objetivo: Migrar Produto.peso de DOUBLE PRECISION para DECIMAL(10, 3) conforme auditoria da Sprint 44.3

ALTER TABLE produto 
ALTER COLUMN peso TYPE DECIMAL(10, 3) 
USING peso::DECIMAL(10, 3);
