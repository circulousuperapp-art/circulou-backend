# ADR-003: Padrão de Testes de Integração

## Status
Aprovado

## Contexto
O projeto Circulou Backend utiliza uma arquitetura hexagonal e processamento assíncrono via Transactional Outbox. Para garantir a estabilidade do sistema, especialmente em ambientes de CI/CD efêmeros como GitHub Actions, é necessário padronizar como os testes de integração são escritos e executados.

## Decisões

### 1. Uso Obrigatório do BaseIntegrationTest
Todos os novos testes de integração (arquivos com sufixo `IT.java`) devem estender a classe `BaseIntegrationTest`.
- **Motivo:** Centraliza a configuração do contexto Spring, Testcontainers e limpeza de banco de dados.

### 2. Reutilização de Testcontainers
Os testes devem utilizar a configuração fornecida por `TestcontainersConfiguration` através do `BaseIntegrationTest`.
- **PostgreSQL:** Iniciado automaticamente para cada execução de testes.
- **Kafka:** Atualmente simulado ou ignorado em testes de integração de persistência (Outbox), para manter os testes rápidos e estáveis.

### 3. Quando utilizar Testes Unitários vs Integração
- **Testes Unitários:** Para lógica de negócio pura, serviços, mappers e adapters de infraestrutura que não dependem de recursos externos (Mockando dependências).
- **Testes de Integração:** Para validar a persistência no banco de dados, contratos de API (MockMvc) e integração completa do fluxo Outbox.

### 4. Padrão para Novos Testes
- Sufixo `Test.java` para testes unitários.
- Sufixo `IT.java` para testes de integração.
- Anotação `@Transactional` deve ser usada com cautela para garantir que o comportamento do banco reflita a realidade (especialmente em fluxos Outbox).

## Consequências
- Maior estabilidade no pipeline de CI/CD.
- Menor duplicação de código de configuração de testes.
- Facilidade na criação de novos cenários de teste.
