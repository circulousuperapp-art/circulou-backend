# ADR-001 — Separação entre Produto, Oferta e Loja

#arquitetura #adr #ddd #hexagonal #produto #oferta #loja #circulou #backend #design #cleanarchitecture #solid

## Status

✅ Aprovada

## Data

14/07/2026

---

# Contexto

Durante o início do Épico 3 (Gestão de Lojas), surgiu a necessidade de definir corretamente a responsabilidade de cada agregado de domínio antes da implementação do módulo de Produtos.

A principal dúvida era:

- O Produto pertence à Loja?
- O Produto pertence ao Lojista?
- O Produto deve armazenar preço, estoque e promoções?

Após análise da arquitetura e comparação com plataformas como iFood, Uber Eats, DoorDash, Mercado Livre, Amazon e Shopify, concluiu-se que misturar essas responsabilidades geraria forte acoplamento e limitaria a evolução do sistema.

---

# Decisão Arquitetural

O domínio será dividido em três conceitos independentes:

Loja

↓

Oferta

↓

Produto

Cada agregado possuirá responsabilidades bem definidas.

## Loja

Responsável apenas pelo estabelecimento comercial.

Pertence à Loja:

- nome
- endereço
- telefone
- logo
- tempo médio de preparo
- selo de confiança
- status
- dados operacionais

A Loja **não conhece**:

- preço
- estoque
- promoções

---

## Produto

Representa exclusivamente o item comercial.

Pertence ao Produto:

- nome
- descrição
- categoria
- marca
- imagens
- unidade de medida
- informações técnicas

O Produto **não conhece**:

- preço
- estoque
- promoções
- disponibilidade

O Produto representa apenas o catálogo.

---

## Oferta

A Oferta representa a comercialização de um Produto por uma Loja.

É nela que ficarão informações como:

- preço
- estoque
- disponibilidade
- promoções
- descontos
- quantidade mínima
- quantidade máxima
- tempo de preparo específico

Exemplo:

Loja A

↓

Oferta

↓

Coca-Cola 2L

↓

R$ 8,99

Loja B

↓

Oferta

↓

Coca-Cola 2L

↓

R$ 10,49

O Produto permanece o mesmo.

A Oferta muda conforme cada Loja.

---

# Estrutura Oficial do Domínio

Usuario
        │
        ▼
LojistaProfile
        │
        ▼
Loja
        │
        ▼
Oferta
        │
        ▼
Produto
        │
        ▼
Categoria

---

# Motivações

Esta decisão foi tomada para:

- reduzir acoplamento;
- aumentar reutilização do catálogo;
- permitir comparação de preços entre lojas;
- permitir estoques independentes;
- permitir promoções independentes;
- permitir expansão para múltiplas lojas;
- preservar a Arquitetura Hexagonal;
- respeitar os princípios do DDD.

---

# Benefícios de Médio e Longo Prazo

## Comparador de preços

O sistema poderá localizar todas as ofertas de um mesmo produto em diferentes lojas sem duplicar registros.

---

## Estoque descentralizado

Cada loja administrará seu próprio estoque, sem interferir nas demais.

---

## Promoções independentes

Cada loja poderá criar campanhas promocionais específicas para seus produtos.

---

## Escalabilidade

A arquitetura permitirá suportar desde pequenos comércios até grandes redes varejistas sem remodelagem do domínio.

---

## Evolução futura

A introdução de conceitos como:

- franquias;
- redes de lojas;
- marketplace;
- centros de distribuição;
- múltiplas unidades;

poderá ocorrer sem alterações estruturais significativas.

---

# Impacto na Arquitetura Hexagonal

Esta decisão fortalece a Arquitetura Hexagonal ao manter cada agregado com uma única responsabilidade.

Cada domínio evoluirá de forma independente, reduzindo dependências entre módulos e facilitando testes, manutenção e evolução contínua do sistema.

---

# Impacto no DDD

A decisão respeita os princípios do Domain-Driven Design:

- Aggregate Roots bem definidos;
- baixo acoplamento entre agregados;
- alta coesão;
- responsabilidades únicas;
- linguagem ubíqua consistente.

---

# Roadmap

A implementação seguirá esta sequência:

1. Gestão de Lojas
2. Gestão de Produtos
3. Gestão de Ofertas
4. Pedidos
5. Itens do Pedido
6. Estoque
7. Promoções
8. Dashboard

---

# Observação

A entidade **Oferta** faz parte da arquitetura oficial do Circulou, porém sua implementação será realizada em um Épico futuro.

Até esse momento, o desenvolvimento dos módulos de Loja e Produto deverá preservar esta decisão arquitetural, evitando qualquer acoplamento que dificulte sua introdução posteriormente.
