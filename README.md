# Distributed Systems Engineering Lab

**Java 25 · Spring Boot · Microsserviços · Apache Kafka · Event-Driven Architecture · Saga Orchestration · MySQL · Kubernetes · Observabilidade**

[Repositório no GitHub](https://github.com/MarcioVRED/distributed-systems-engineering-lab)

> **Laboratório de engenharia de sistemas distribuídos.** O cenário de e-commerce é utilizado apenas como domínio de demonstração, enquanto o foco do repositório está em arquitetura de microsserviços, integração orientada a eventos, infraestrutura Cloud Native e observabilidade. Objetivo: implementar um fluxo distribuído com **Saga por orquestração**, transações locais, eventos Kafka, compensações, **Transactional Outbox**, idempotência, tratamento de falhas e observabilidade. O fluxo completo de Saga orquestrada e todas as métricas de negócio **ainda precisam ser validados/concluídos**; não confundir arquitetura-alvo com funcionalidade comprovada.

## Visão geral

O **distributed-systems-engineering-lab** é um ambiente prático para estudar, implementar e validar técnicas de desenvolvimento de APIs e sistemas distribuídos. O projeto integra serviços independentes, comunicação síncrona via REST e assíncrona via **Apache Kafka**, persistência desacoplada por domínio, **Saga por orquestração** e instrumentos de monitoramento. O domínio de pedidos é um exemplo de negócio; o laboratório foi nomeado para permitir novos cenários e serviços no futuro.

**Repositório:** https://github.com/MarcioVRED/distributed-systems-engineering-lab

**Estado atual:** laboratório em evolução. As APIs, conexão MySQL, migrações Flyway e infraestrutura de observabilidade foram trabalhadas localmente. O funcionamento de toda a Saga, compensações, deploy no Kubernetes e tracing distribuído ainda exige validação e/ou implementação específica.

## 1. Objetivo e cenário

Simular a jornada **cliente → pedido → reserva de estoque → pagamento → confirmação → expedição**, mantendo dados separados por serviço. A orquestração deverá decidir os próximos passos e acionar compensações em caso de falha (por exemplo, liberar estoque reservado se o pagamento for rejeitado). O Kafka transporta eventos; o MySQL persiste o estado de cada domínio; Flyway versiona esquemas; Prometheus e Grafana monitoram o comportamento operacional.

**Arquitetura-alvo (conceitual):**

```text
Cliente/Postman
     |
     v
Order Service (orquestrador da Saga — objetivo)
     |  eventos/comandos via Kafka
     +--> Product Service: reservar/liberar estoque
     +--> Payment Service: autorizar/rejeitar pagamento
     +--> [Shipping/Notification: evolução futura]
     |
     +--> estado do pedido + Outbox + compensações

Cada serviço -> MySQL (schema próprio) -> Flyway
Cada serviço -> /actuator/prometheus -> Prometheus -> Grafana
Kafka -> Kafka UI; tracing distribuído -> evolução/validação futura
```

## 2. Componentes

| Componente | Responsabilidade | Porta local |
|---|---|---|
| `product-service` | Cadastro de produtos, estoque, eventos/outbox | `8081` |
| `customer-service` | Cadastro e consulta de clientes | `8082` |
| `order-service` | Pedidos, itens, transições de estado e coordenação dos fluxos | `8083` |
| `payment-service` | Pagamentos e integração assíncrona | `8084` |
| MySQL 8.4 | Bancos `ecommerce_product`, `ecommerce_customer`, `ecommerce_order`, `ecommerce_payment` | `3306` |
| Kafka (cluster local) | Mensageria e processamento assíncrono | `9092`, `9094`, `9096` (configuração conhecida do laboratório) |
| Kafka UI (Kafbat) | Inspeção de tópicos, mensagens e grupos | `8084` **na configuração anterior** — conflito com `payment-service` se ambos usarem a mesma porta no host |
| Prometheus | Coleta de métricas | `9090` |
| Grafana | Dashboards | `3000` |
| Jaeger | Visualização de traces, quando habilitado | `16686` |
| OTLP Collector | Recepção de telemetria, quando habilitado | `4318` |

**Atenção ao conflito de portas:** o Kafka UI já foi configurado em `8084` no laboratório, mesma porta do Payment Service. Se os dois estiverem ativos no mesmo host, altere a publicação de porta do Kafka UI (por exemplo, `8085:8080`, conforme a porta interna real do contêiner) e use `http://localhost:8085`. Verifique o `docker-compose.yml` efetivo antes de executar.

## 3. Tecnologias

- Java 25, Spring Boot 4.1.2, Spring Web MVC, Spring Data JPA, Hibernate 7, Bean Validation.
- Apache Kafka, Spring Kafka, processamento orientado a eventos.
- MySQL 8.4, Flyway, migrações versionadas `db/migration/V...__...sql`.
- Micrometer, Spring Boot Actuator, Prometheus, Grafana.
- Docker / Docker Compose; JUnit, Mockito e testes de integração conforme cada módulo.
- Outbox, idempotência, retry e DLT: componentes/padrões trabalhados no laboratório; conferir cobertura e comportamento de ponta a ponta antes de considerar completos.
- Kubernetes: objetivo posterior de implantação e escala.

## 4. Pré-requisitos

Linux/Ubuntu com Docker e Docker Compose, JDK 25, Maven Wrapper (`./mvnw`), portas livres e Postman.

Para obter o projeto a partir do GitHub:

```bash
git clone https://github.com/MarcioVRED/distributed-systems-engineering-lab.git
cd distributed-systems-engineering-lab
```

> **Observação:** o projeto também pode estar em uma pasta local com outro nome, como `~/labs/projeto-estudo-ecommerce`. Nos comandos seguintes, use o caminho real do seu clone. Os exemplos pressupõem que os serviços estejam em subpastas da raiz e que os arquivos Docker Compose já existam nos diretórios de infraestrutura indicados.

## 5. Sequência recomendada para iniciar o ambiente

### 5.1 Subir infraestrutura de dados e mensageria

Em um terminal, entre no diretório que contém o **Compose do MySQL** e execute:

```bash
docker compose up -d
docker ps
```

Faça o mesmo no diretório que contém o **Compose do Kafka**. Os nomes exatos desses diretórios não foram confirmados; não execute `docker compose` na raiz sem antes verificar que ali existe `compose.yaml` ou `docker-compose.yml`.

```bash
# Exemplos de verificação
docker ps
docker logs --tail 50 ecommerce-mysql
```

Confirme os brokers Kafka, o MySQL e a interface Kafka UI. Resolva o conflito da porta 8084 antes de iniciar o Payment Service.

### 5.2 Subir observabilidade

Se o pacote `infra/` de observabilidade estiver presente:

```bash
cd distributed-systems-engineering-lab/infra  # a partir do diretório pai do clone; confirme que a pasta existe
docker compose up -d
docker compose ps
```

A infraestrutura de referência do laboratório inclui Prometheus e Grafana; Jaeger e OTEL Collector dependem da configuração efetivamente presente no ambiente. **Prometheus/Grafana são a parte validada do laboratório; tracing com Jaeger foi adiado.** Não é necessário habilitar tracing para executar o fluxo principal.

### 5.3 Iniciar os quatro serviços (um terminal por serviço)

```bash
cd distributed-systems-engineering-lab/product-service
./mvnw spring-boot:run
```

```bash
cd distributed-systems-engineering-lab/customer-service
./mvnw spring-boot:run
```

```bash
cd distributed-systems-engineering-lab/order-service
./mvnw spring-boot:run
```

```bash
cd distributed-systems-engineering-lab/payment-service
./mvnw spring-boot:run
```

Na primeira execução, se necessário: `chmod +x mvnw`. O Flyway deve executar/validar as migrações **antes** da validação JPA. Com `ddl-auto: validate`, não use `update` como solução de erros de esquema. Se uma porta estiver ocupada, identifique o processo (`sudo ss -ltnp | grep ':8083'`, por exemplo) antes de encerrar qualquer instância.

### 5.4 Validar saúde e métricas

```bash
for port in 8081 8082 8083 8084; do
  echo "=== $port ==="
  curl -s "http://localhost:$port/actuator/health"
  echo
done

curl -s http://localhost:8083/actuator/prometheus | head
```

A disponibilidade dos endpoints depende da exposição configurada no Actuator. A porta `8084` aqui se refere ao **Payment Service**, não ao Kafka UI.

## 6. Painéis e ferramentas

| Ferramenta | Endereço | Uso |
|---|---|---|
| Grafana | http://localhost:3000 | Dashboard dos quatro microserviços; no pacote de referência, usuário/senha iniciais `admin` / `admin` (alterar fora do laboratório) |
| Prometheus | http://localhost:9090 | Consultas PromQL |
| Targets do Prometheus | http://localhost:9090/targets | Confirmar se os quatro serviços estão `UP` |
| Product metrics | http://localhost:8081/actuator/prometheus | Métricas da aplicação |
| Customer metrics | http://localhost:8082/actuator/prometheus | Métricas da aplicação |
| Order metrics | http://localhost:8083/actuator/prometheus | Métricas da aplicação |
| Payment metrics | http://localhost:8084/actuator/prometheus | Métricas da aplicação |
| Jaeger (opcional) | http://localhost:16686 | Traces após instrumentação/validação |
| Kafka UI | Porta publicada no Compose efetivo | Tópicos, mensagens, consumers, lag |

Na configuração local de referência, o `prometheus.yml` coleta `/actuator/prometheus` de `host.docker.internal:8081` a `:8084` a cada **10 segundos**. O dashboard Grafana de referência distingue os serviços quando importado e configurado corretamente.

**Nota sobre OTLP:** houve aviso `404` em `http://localhost:4318/v1/metrics` durante o encerramento do Order Service. Isso não causou a falha de inicialização (que era a porta 8083 ocupada). Validar o receiver/configuração OTLP antes de usar exportação de métricas via push; a coleta Prometheus via scrape é independente.

## 7. Coleção Postman e exemplos de payload

Importe **`ecommerce_microservices_atualizada.postman_collection.json`** no Postman (se este arquivo estiver disponível no repositório ou na documentação distribuída com o projeto). Variáveis definidas na coleção:

```text
productUrl  = http://localhost:8081
customerUrl = http://localhost:8082
orderUrl    = http://localhost:8083
paymentUrl  = http://localhost:8084
productId   = 1
customerId  = 1
orderId     = 1
```

Atualize os IDs conforme as respostas reais do ambiente. Os exemplos abaixo são os payloads presentes na coleção.

### 7.1 Criar produto

`POST http://localhost:8081/products`

```json
{
  "name": "Notebook Teste Kafka",
  "price": 3500.0,
  "stock": 10
}
```

### 7.2 Criar cliente

`POST http://localhost:8082/customers`

```json
{
  "name": "Cliente Teste Postman",
  "email": "cliente.postman@example.com"
}
```

### 7.3 Criar pedido (duas unidades)

`POST http://localhost:8083/orders`

```json
{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "unitPrice": 3500.0
    }
  ]
}
```

Na coleção, use `{{customerId}}` e `{{productId}}` sem aspas para inserir valores numéricos.

### 7.4 Simular estoque insuficiente

`POST http://localhost:8083/orders`

```json
{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 20,
      "unitPrice": 3500.0
    }
  ]
}
```

Esse cenário deve ser testado após criar um produto com estoque 10. Aguarde o processamento assíncrono e confira estoque e status do pedido. Não pressupor resposta síncrona de rejeição.

### 7.5 Consultas e cancelamento

```http
GET  http://localhost:8081/products
GET  http://localhost:8081/products/1
GET  http://localhost:8082/customers/1
GET  http://localhost:8083/orders
GET  http://localhost:8083/orders/1
GET  http://localhost:8083/orders/customer/1
POST http://localhost:8083/orders/1/cancel
GET  http://localhost:8084/payments
GET  http://localhost:8084/payments/order/1
GET  http://localhost:8084/actuator/health
```

A coleção contempla cancelamento de pedido em `PENDING`; não executar o cancelamento indiscriminadamente em pedidos de outros estados.

## 8. Kubernetes e evolução Cloud Native

A execução atual descrita neste README utiliza **Docker Compose para infraestrutura** e processos Spring Boot locais. A implantação dos microsserviços em **Kubernetes é uma etapa planejada**, não um deploy completo comprovado por esta documentação. Evoluções previstas incluem Deployment, Service, ConfigMap, Secret, probes de saúde, escalonamento horizontal e tratamento de reinicialização/reprocessamento de consumidores Kafka.

Para ambientes distribuídos, considere particionamento de tópicos, chaves de correlação, entrega pelo menos uma vez, consumidores idempotentes, timeouts, retries com backoff, DLT e compensações explícitas no fluxo Saga.

## 9. Roteiro de teste ponta a ponta

1. Confirmar MySQL, Kafka, quatro APIs e Prometheus `UP`.
2. Criar produto com estoque **10** e cliente; guardar IDs retornados.
3. Criar pedido com **2 unidades** e observar eventos Kafka.
4. Consultar o produto após processamento; no cenário de reserva bem-sucedida, estoque esperado **8**.
5. Consultar pedido e pagamento associado, acompanhando transições reais.
6. Criar pedido de **20 unidades** para o mesmo produto e validar rejeição de estoque sem redução adicional.
7. Verificar logs, tópicos, outbox, retries/DLT (se habilitados) e dashboards.
8. Executar testes de falha/compensação apenas após confirmar a implementação efetiva da orquestração.



**Nota de segurança:** credenciais de exemplo (`admin/admin`, senhas locais de banco) são somente para laboratório. Nunca versionar segredos reais nem expor Actuator, Kafka UI, Prometheus ou Grafana publicamente sem proteção.
