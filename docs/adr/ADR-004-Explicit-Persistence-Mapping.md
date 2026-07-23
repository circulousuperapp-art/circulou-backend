# ADR-004 — Explicit Persistence Mapping

#arquitetura #adr #jpa #hibernate #flyway #postgresql #ddd #persistence #mapping #hardening

## Status

✅ Aprovada

## Data

22/07/2026

---

# Contexto

Durante a Sprint 43 — Etapa 1 (Hardening da Plataforma), identificou-se a necessidade de garantir paridade absoluta entre o modelo de domínio expresso em Java (JPA) e o schema físico do banco de dados (PostgreSQL/Flyway). 

Até então, o projeto utilizava mapeamentos que dependiam parcialmente de estratégias implícitas do Hibernate (`ImplicitNamingStrategy`), o que gerava ambiguidades e riscos de dessincronização durante refatorações ou mudanças de configuração global.

---

# Problema

Mapeamentos implícitos criam um "contrato invisível" entre o código e o banco de dados. Os principais problemas identificados foram:

1.  **Risco de Refatoração:** Alterar o nome de um atributo Java poderia quebrar a persistência silenciosamente se o nome da coluna dependesse da inferência do Hibernate.
2.  **Dificuldade de Auditoria:** Para entender as restrições de uma coluna (tamanho, nulidade, unicidade), o desenvolvedor precisava consultar a migration SQL ou o banco, pois o código Java era omisso.
3.  **Inconsistência de Naming:** Divergências entre `camelCase` (Java) e `snake_case` (DB) nem sempre estavam explicitadas, dificultando a leitura de logs de SQL.
4.  **Validação Tardia:** A ausência de metadados de persistência no Java impedia que o Hibernate Validate ou o próprio JPA antecipassem erros de integridade antes da execução do SQL.

---

# Decisão Arquitetural

Adotamos a filosofia de **Explicit Persistence Mapping**. O modelo Java passa a ser a representação explícita e mandatória do contrato físico de persistência.

As entidades JPA devem refletir fielmente o schema definido pelas migrations Flyway através de anotações detalhadas.

### Diretrizes de Mapeamento

Sempre que um atributo possuir características definidas na Migration, elas deverão estar refletidas no modelo JPA utilizando:

*   **@Table(name = "...")**: Nome da tabela sempre explícito em snake_case.
*   **@Column(name = "...", ...)**: Nome da coluna sempre explícito em snake_case.
*   **nullable**: Refletir obrigatoriedade (`NOT NULL`).
*   **unique**: Refletir restrições de unicidade (`UNIQUE`).
*   **length**: Refletir limites de caracteres (`VARCHAR(N)`).
*   **precision / scale**: Refletir precisão numérica para valores financeiros e decimais.

**Nota Importante:** Não é necessário adicionar todos os atributos a todas as colunas. Devem ser explicitados apenas aqueles que fazem sentido para representar corretamente o contrato físico definido na Migration.

---

# Consequências

### Benefícios
*   **Blindagem Arquitetural:** O sistema torna-se imune a variações de estratégias de nomenclatura global do JPA/Hibernate.
*   **Documentação Viva:** A entidade JPA serve como referência imediata das restrições do banco de dados, facilitando o desenvolvimento e o code review.
*   **Previsibilidade:** Redução de erros de `DataIntegrityViolationException` inesperados, pois as restrições estão visíveis no domínio.
*   **Facilidade de Debug:** O mapeamento explícito facilita a correlação entre consultas SQL geradas e o código Java.

### Trade-offs
*   **Verbosidade:** Aumento da quantidade de anotações e metadados nas classes de entidade.
*   **Manutenção Dupla:** Exige que o desenvolvedor atualize tanto a Migration quanto a Entidade JPA para manter a paridade (o que é desejado para evitar dessincronização).

---

# Relações e Impactos

### Relação com DDD
O Aggregate Root e suas Entidades continuam sendo os donos da lógica de negócio. O mapeamento explícito apenas garante que a "casca de persistência" do agregado seja robusta e bem definida, protegendo a integridade do estado do domínio.

### Relação com Arquitetura Hexagonal
Esta decisão impacta o **Persistence Adapter**. Embora a entidade JPA viva tecnicamente na camada de domínio (ou em uma camada de model compartilhada), ela atua como o contrato de saída para o banco de dados. O mapeamento explícito fortalece este contrato.

### Relação com Flyway e Hibernate
*   **Flyway:** Continua sendo a **Fonte Oficial da Verdade**.
*   **Hibernate:** Deixa de ser um "tomador de decisão" sobre o schema e passa a ser apenas um executor de um mapeamento pré-definido. A configuração `hibernate.ddl-auto` deve permanecer em `validate` (dev) ou `none` (prod).

### Relação com Bean Validation
O mapeamento explícito (`@Column(length=X)`) deve trabalhar em conjunto com o Bean Validation (`@Size(max=X)`). Enquanto o primeiro instrui o JPA, o segundo permite validações programáticas na camada de aplicação e DTOs, antecipando falhas.

### Relação com Segurança
Garante que campos sensíveis tenham limites de tamanho e restrições de nulidade explicitados, prevenindo ataques de buffer overflow no nível de persistência ou ingestão de dados malformados.

---

# Alternativas Consideradas

1.  **Configuração de Naming Strategy Global:** Rejeitada por não ser granular o suficiente e falhar em representar restrições específicas como `length` e `precision` de forma autoexplicativa no código.
2.  **Geração Automática de Entidades via DB:** Rejeitada por retirar o controle do desenvolvedor sobre o modelo de domínio e gerar código poluído.

---

# Considerações Futuras

Esta ADR estabelece o padrão para todas as futuras sprints. Qualquer nova entidade ou alteração em tabelas existentes deve seguir rigorosamente o mapeamento explícito. Em etapas futuras de refatoração de performance (Etapa 2), este mapeamento servirá de base para a implementação de índices e otimizações de query.
