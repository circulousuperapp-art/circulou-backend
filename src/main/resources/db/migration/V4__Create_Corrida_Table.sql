-- V4__Create_Corrida_Table.sql
-- Objetivo: Implementar a fundação do domínio de Mobilidade no EPIC D da Sprint 45

CREATE TABLE corrida (
    id BIGSERIAL CONSTRAINT pk_corrida PRIMARY KEY,
    cliente_id BIGINT NOT NULL CONSTRAINT fk_corrida_cliente REFERENCES usuario(id),
    motorista_id BIGINT CONSTRAINT fk_corrida_motorista REFERENCES motorista_profile(id),
    origem_logradouro VARCHAR(255),
    origem_numero VARCHAR(50),
    origem_bairro VARCHAR(100),
    origem_cidade VARCHAR(100),
    origem_estado VARCHAR(2),
    origem_latitude DOUBLE PRECISION,
    origem_longitude DOUBLE PRECISION,
    destino_logradouro VARCHAR(255),
    destino_numero VARCHAR(50),
    destino_bairro VARCHAR(100),
    destino_cidade VARCHAR(100),
    destino_estado VARCHAR(2),
    destino_latitude DOUBLE PRECISION,
    destino_longitude DOUBLE PRECISION,
    status VARCHAR(50) NOT NULL CONSTRAINT ck_corrida_status CHECK (status IN (
        'SOLICITADA', 'ACEITA', 'MOTORISTA_A_CAMINHO', 'CLIENTE_EMBARCADO', 
        'PARADA_NA_LOJA', 'EM_DESLOCAMENTO', 'FINALIZADA', 'CANCELADA'
    )),
    data_criacao TIMESTAMP NOT NULL,
    data_inicio TIMESTAMP,
    data_finalizacao TIMESTAMP,
    valor_estimado DECIMAL(10, 2),
    valor_final DECIMAL(10, 2),
    distancia_estimada_metros INTEGER,
    duracao_estimada_segundos INTEGER
);

CREATE INDEX idx_corrida_cliente ON corrida(cliente_id);
CREATE INDEX idx_corrida_motorista ON corrida(motorista_id);
CREATE INDEX idx_corrida_status ON corrida(status);
