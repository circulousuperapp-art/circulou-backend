# ADR-002 — Ciclo de Vida do Pedido e Política de Liberação para Produção

#ADR #ADR002 #Pedido #PedidoStatus #StateMachine #Cancelamento #UX #Produto #Loja #Backend #Arquitetura #DDD #Circulou

## Status

**Aprovada**

---

## Contexto

O Circulou busca equilibrar três objetivos fundamentais da plataforma:

- oferecer uma experiência transparente ao cliente;
- proteger o lojista contra desperdícios em produtos preparados sob encomenda;
- automatizar regras operacionais para reduzir conflitos e decisões manuais.

Durante a modelagem do domínio identificou-se que existem três conceitos independentes que não devem ser confundidos:

- janela de cancelamento da compra;
- tempo necessário para preparação do produto;
- política de cancelamento conforme a natureza do produto.

A arquitetura deverá tratar esses conceitos de forma separada.

---

# Decisão Arquitetural

Após a confirmação do pagamento, o pedido **não será imediatamente liberado para produção**.

O pedido entrará em um estado intermediário denominado:

AGUARDANDO_LIBERACAO

Durante esse período será aberta uma **janela de cancelamento sem custo**, inicialmente definida em **1 minuto**.

Caso o cliente cancele dentro desse período:

- o pedido será encerrado;
- haverá reembolso integral;
- nenhuma produção deverá ser iniciada.

Encerrada essa janela, o sistema enviará automaticamente uma autorização para que o estabelecimento inicie o preparo.

A partir desse momento considera-se encerrada a fase de composição do pedido.

---

# Política de Imutabilidade do Pedido

Enquanto o pedido estiver em **AGUARDANDO_LIBERACAO**, o cliente poderá:

- cancelar o pedido;
- receber reembolso integral.

Entretanto, após a confirmação da compra **não será permitido**:

- alterar sabores;
- alterar tamanhos;
- remover produtos;
- adicionar novos produtos;
- alterar observações que impactem a produção.

Caso o cliente deseje adquirir novos itens após esse momento, deverá realizar **um novo pedido**, totalmente independente do anterior.

Essa política garante previsibilidade operacional para o estabelecimento e evita inconsistências durante o processo produtivo.

---

# Jornada da Compra

A experiência de compra será dividida em duas etapas distintas.

## 1. Composição do Pedido

Durante a navegação o sistema poderá incentivar o aumento do ticket médio através de sugestões inteligentes como:

- refrigerantes;
- sobremesas;
- acompanhamentos;
- promoções;
- combos;
- ofertas relacionadas ao pedido atual.

Exemplo:

"Leve também um refrigerante de 2 litros e uma pizza broto por apenas R$ XX,XX."

Essas sugestões poderão ocorrer durante toda a fase de composição do pedido.

---

## 2. Revisão Final da Compra

Antes da confirmação do pagamento será apresentada uma tela de revisão contendo todos os itens do pedido.

Além do resumo da compra será exibida uma mensagem semelhante a:

"Revise sua compra com atenção. Este é o melhor momento para adicionar novos itens, alterar sabores, tamanhos ou realizar qualquer ajuste. Após a confirmação do pagamento, essas alterações não poderão mais ser realizadas neste pedido."

Somente após essa confirmação será iniciado o fluxo financeiro.

---

# Fluxo Operacional

Cliente monta o pedido
        │
        ▼
Tela de revisão
        │
        ▼
Pagamento confirmado
        │
        ▼
AGUARDANDO_LIBERACAO
        │
        │ (Janela de cancelamento)
        │
        ├────────────► Cancelamento
        │                 │
        │                 ▼
        │          Reembolso integral
        │
        ▼
Tempo expirado
        │
        ▼
Pedido liberado para produção
        │
        ▼
EM_PREPARO
        │
        ▼
PRONTO_PARA_RETIRADA
        │
        ▼
EM_ROTA
        │
        ▼
ENTREGUE

---

# Responsabilidades

## Plataforma

- controlar a janela de cancelamento;
- liberar automaticamente o início da produção;
- informar claramente as regras ao cliente;
- informar claramente as regras ao lojista;
- impedir alterações após a confirmação da compra.

---

## Cliente

- revisar cuidadosamente o pedido antes do pagamento;
- utilizar a janela de cancelamento caso desista da compra;
- realizar um novo pedido caso deseje adquirir novos produtos após a confirmação.

---

## Lojista

- aguardar a autorização do sistema antes de iniciar o preparo;
- iniciar a produção apenas após o alerta de liberação;
- respeitar o fluxo operacional definido pela plataforma.

---

# Mensagem ao Cliente

Antes da confirmação do pagamento:

Revise seu pedido antes de finalizar a compra.

Pedido: Pizza Calabresa Grande

✅ Seu pagamento será confirmado imediatamente.

⏳ Após a confirmação do pagamento, você terá até 1 minuto para cancelar este pedido com reembolso integral.

[Confirmar Compra]

---

# Mensagem ao Lojista

## Pedido Recebido

🔔 Novo pedido recebido

Cliente:
Pedro Arthur

Pedido:
Pizza Calabresa Grande

Pagamento:
✅ Confirmado

⏳ Aguarde 1 minuto antes de iniciar o preparo.

O cliente ainda está dentro da janela de cancelamento sem custo.

Você será notificado automaticamente quando o pedido estiver liberado para produção.

---

## Pedido Liberado

🔔 Pedido liberado para produção

Cliente:
Pedro Arthur

Pedido:
Pizza Calabresa Grande

✅ A janela de cancelamento foi encerrada.

Inicie o preparo agora.

Tempo estimado de preparo:
18 minutos.

---

# Impacto Arquitetural

Esta ADR estabelece uma separação clara entre:

- composição do pedido;
- compromisso da compra;
- autorização para produção;
- produção;
- logística.

Essa separação reduz conflitos entre clientes e lojistas, melhora a previsibilidade operacional e prepara a plataforma para futuras integrações com filas de mensagens, eventos de domínio e automações, preservando a Arquitetura Hexagonal e os princípios de DDD.
