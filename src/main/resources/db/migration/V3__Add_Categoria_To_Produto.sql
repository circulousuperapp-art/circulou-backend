-- V3__Add_Categoria_To_Produto.sql
-- Objetivo: Adicionar o campo categoria na tabela produto para habilitar busca por categorias no EPIC A da Sprint 45

ALTER TABLE produto ADD COLUMN categoria VARCHAR(100);
