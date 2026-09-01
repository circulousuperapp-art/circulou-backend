-- V5__Create_Jornada_Tables.sql
-- Objetivo: Implementar a persistência estrutural do domínio de Jornada Drive-Thru

CREATE TABLE jornada (
    id BIGSERIAL CONSTRAINT pk_jornada PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    corrida_id BIGINT,
    tipo VARCHAR(50) NOT NULL CONSTRAINT ck_jornada_tipo CHECK (tipo IN ('PROPRIA', 'MOTORISTA')),
    status VARCHAR(50) NOT NULL CONSTRAINT ck_jornada_status CHECK (status IN ('CRIADA', 'EM_EXECUCAO', 'CONCLUIDA', 'CANCELADA')),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE jornada_parada (
    id BIGSERIAL CONSTRAINT pk_jornada_parada PRIMARY KEY,
    jornada_id BIGINT NOT NULL CONSTRAINT fk_jornada_parada_jornada REFERENCES jornada(id),
    loja_id BIGINT NOT NULL,
    pedido_id BIGINT NOT NULL CONSTRAINT uk_jornada_parada_pedido UNIQUE,
    ordem INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL CONSTRAINT ck_jornada_parada_status CHECK (status IN ('AGUARDANDO', 'EM_TRANSITO', 'NA_LOJA', 'RETIRADA_REALIZADA', 'CANCELADA')),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_jornada_parada_jornada_ordem UNIQUE (jornada_id, ordem),
    CONSTRAINT ck_jornada_parada_ordem CHECK (ordem BETWEEN 1 AND 5)
);

CREATE INDEX idx_jornada_cliente ON jornada(cliente_id);
-- O índice para jornada_id em jornada_parada é omitido por ser redundante com a constraint uk_jornada_parada_jornada_ordem
