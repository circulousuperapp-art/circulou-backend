package br.com.circulou.circulou_backend.model;

public enum OutboxStatus {
    PENDENTE,
    PROCESSADO,
    FALHA,
    FALHA_DEFINITIVA
}
