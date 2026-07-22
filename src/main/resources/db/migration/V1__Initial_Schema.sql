-- V1__Initial_Schema.sql
-- Created at: 2026-07-22

-- 1. Tables without Foreign Keys

CREATE TABLE endereco (
    id BIGSERIAL CONSTRAINT pk_endereco PRIMARY KEY,
    cep VARCHAR(255),
    logradouro VARCHAR(255),
    numero VARCHAR(255),
    complemento VARCHAR(255),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    estado VARCHAR(255)
);

CREATE TABLE usuario (
    id BIGSERIAL CONSTRAINT pk_usuario PRIMARY KEY,
    nome VARCHAR(255),
    email VARCHAR(255) NOT NULL CONSTRAINT uk_usuario_email UNIQUE,
    senha VARCHAR(100) NOT NULL,
    role VARCHAR(255),
    telefone VARCHAR(255),
    foto_perfil VARCHAR(255),
    ativo BOOLEAN,
    endereco_id BIGINT CONSTRAINT fk_usuario_endereco REFERENCES endereco(id)
);

CREATE TABLE produto (
    id BIGSERIAL CONSTRAINT pk_produto PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(500),
    marca VARCHAR(100),
    unidade_medida VARCHAR(20),
    peso DOUBLE PRECISION,
    codigo_barras VARCHAR(50) CONSTRAINT uk_produto_codigo_barras UNIQUE,
    imagem_principal VARCHAR(255),
    ativo BOOLEAN
);

CREATE TABLE outbox_event (
    id BIGSERIAL CONSTRAINT pk_outbox_event PRIMARY KEY,
    aggregate_id VARCHAR(255) NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    correlation_id VARCHAR(255),
    status VARCHAR(30) NOT NULL CONSTRAINT ck_outbox_event_status CHECK (status IN ('PENDENTE', 'PROCESSADO', 'FALHA', 'FALHA_DEFINITIVA')),
    attempt_count INTEGER NOT NULL DEFAULT 0,
    last_error TEXT,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    next_attempt_at TIMESTAMP
);

-- 2. Tables with Foreign Keys

CREATE TABLE lojista_profile (
    id BIGSERIAL CONSTRAINT pk_lojista_profile PRIMARY KEY,
    usuario_id BIGINT NOT NULL CONSTRAINT uk_lojista_profile_usuario UNIQUE CONSTRAINT fk_lojista_profile_usuario REFERENCES usuario(id),
    status_perfil VARCHAR(30) NOT NULL CONSTRAINT ck_lojista_profile_status CHECK (status_perfil IN ('ATIVO', 'INATIVO', 'EM_ANALISE')),
    cnpj VARCHAR(20) NOT NULL CONSTRAINT uk_lojista_profile_cnpj UNIQUE,
    razao_social VARCHAR(255) NOT NULL,
    segmento VARCHAR(100),
    status_documentacao VARCHAR(30) NOT NULL CONSTRAINT ck_lojista_profile_doc_status CHECK (status_documentacao IN ('EM_ANALISE', 'APROVADO', 'REJEITADO', 'PENDENTE')),
    rating_media DECIMAL(3, 2) NOT NULL DEFAULT 0.0
);

CREATE TABLE motorista_profile (
    id BIGSERIAL CONSTRAINT pk_motorista_profile PRIMARY KEY,
    usuario_id BIGINT NOT NULL CONSTRAINT uk_motorista_profile_usuario UNIQUE CONSTRAINT fk_motorista_profile_usuario REFERENCES usuario(id),
    status_perfil VARCHAR(30) NOT NULL CONSTRAINT ck_motorista_profile_status CHECK (status_perfil IN ('ATIVO', 'INATIVO', 'EM_ANALISE')),
    cnh VARCHAR(20) NOT NULL CONSTRAINT uk_motorista_profile_cnh UNIQUE,
    categoria_cnh VARCHAR(10) NOT NULL,
    status_documentacao VARCHAR(30) NOT NULL CONSTRAINT ck_motorista_profile_doc_status CHECK (status_documentacao IN ('EM_ANALISE', 'APROVADO', 'REJEITADO', 'PENDENTE')),
    rating_media DECIMAL(3, 2) NOT NULL DEFAULT 0.0
);

CREATE TABLE loja (
    id BIGSERIAL CONSTRAINT pk_loja PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    logo VARCHAR(255),
    tempo_medio_preparo INTEGER,
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    selo_confianca BOOLEAN,
    endereco_id BIGINT CONSTRAINT fk_loja_endereco REFERENCES endereco(id),
    lojista_profile_id BIGINT NOT NULL CONSTRAINT fk_loja_lojista_profile REFERENCES lojista_profile(id)
);

CREATE TABLE oferta (
    id BIGSERIAL CONSTRAINT pk_oferta PRIMARY KEY,
    loja_id BIGINT NOT NULL CONSTRAINT fk_oferta_loja REFERENCES loja(id),
    produto_id BIGINT NOT NULL CONSTRAINT fk_oferta_produto REFERENCES produto(id),
    preco DECIMAL(10, 2) NOT NULL,
    estoque INTEGER NOT NULL,
    estoque_minimo INTEGER NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    permite_retirada BOOLEAN NOT NULL DEFAULT FALSE,
    permite_entrega BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL,
    CONSTRAINT uk_oferta_loja_produto UNIQUE (loja_id, produto_id)
);

CREATE TABLE pedido (
    id BIGSERIAL CONSTRAINT pk_pedido PRIMARY KEY,
    valor_total DECIMAL(10, 2) NOT NULL DEFAULT 0.0,
    status VARCHAR(50) NOT NULL CONSTRAINT ck_pedido_status CHECK (status IN ('PENDENTE', 'AGUARDANDO_LIBERACAO', 'EM_PREPARO', 'PRONTO_PARA_RETIRADA', 'EM_ROTA', 'ENTREGUE', 'CANCELADO')),
    data_criacao TIMESTAMP NOT NULL,
    data_limite_cancelamento TIMESTAMP,
    usuario_id BIGINT NOT NULL CONSTRAINT fk_pedido_usuario REFERENCES usuario(id),
    loja_id BIGINT NOT NULL CONSTRAINT fk_pedido_loja REFERENCES loja(id)
);

CREATE TABLE item_pedido (
    id BIGSERIAL CONSTRAINT pk_item_pedido PRIMARY KEY,
    quantidade INTEGER NOT NULL,
    pedido_id BIGINT NOT NULL CONSTRAINT fk_item_pedido_pedido REFERENCES pedido(id),
    oferta_id BIGINT NOT NULL CONSTRAINT fk_item_pedido_oferta REFERENCES oferta(id),
    nome_produto VARCHAR(255) NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL
);

CREATE TABLE forma_pagamento (
    id BIGSERIAL CONSTRAINT pk_forma_pagamento PRIMARY KEY,
    usuario_id BIGINT NOT NULL CONSTRAINT fk_forma_pagamento_usuario REFERENCES usuario(id),
    tipo VARCHAR(255),
    apelido VARCHAR(255),
    ultimos4_digitos VARCHAR(255),
    token_pagamento VARCHAR(255),
    principal BOOLEAN,
    ativa BOOLEAN
);

CREATE TABLE historico_destino (
    id BIGSERIAL CONSTRAINT pk_historico_destino PRIMARY KEY,
    usuario_id BIGINT NOT NULL CONSTRAINT fk_historico_destino_usuario REFERENCES usuario(id),
    nome_local VARCHAR(255),
    endereco VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    ultima_utilizacao TIMESTAMP,
    quantidade_utilizacoes INTEGER
);

-- 3. Indexes

-- Outbox Polling Index (Composite)
CREATE INDEX idx_outbox_polling ON outbox_event (status, next_attempt_at, created_at, id);

-- Foreign Key Performance Indexes
CREATE INDEX idx_usuario_endereco ON usuario(endereco_id);
CREATE INDEX idx_loja_lojista_profile ON loja(lojista_profile_id);
CREATE INDEX idx_loja_endereco ON loja(endereco_id);
CREATE INDEX idx_oferta_produto ON oferta(produto_id);
CREATE INDEX idx_oferta_loja ON oferta(loja_id); -- Added as per review
CREATE INDEX idx_pedido_usuario ON pedido(usuario_id);
CREATE INDEX idx_pedido_loja ON pedido(loja_id);
CREATE INDEX idx_item_pedido_pedido ON item_pedido(pedido_id); -- Added as per review
CREATE INDEX idx_item_pedido_oferta ON item_pedido(oferta_id);
CREATE INDEX idx_forma_pagamento_usuario ON forma_pagamento(usuario_id);
CREATE INDEX idx_historico_destino_usuario ON historico_destino(usuario_id);
