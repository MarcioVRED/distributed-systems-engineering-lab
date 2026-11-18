## 9. Diagnóstico rápido

```bash
# Serviços e portas
docker ps
sudo ss -ltnp | grep -E ':8081|:8082|:8083|:8084|:9090|:3000'

# Flyway no banco de pedidos
docker exec -it ecommerce-mysql mysql -uroot -p
```

```sql
USE ecommerce_order;
SHOW TABLES;
SELECT version, description, success FROM flyway_schema_history;
```

- **`Access denied`**: conferir credenciais JDBC e permissões do usuário MySQL.
- **`missing table`**: conferir `spring-boot-starter-flyway`, `flyway-mysql`, caminho `db/migration` e histórico da migração.
- **`payload` LONGTEXT versus CLOB/TINYTEXT**: alinhar entidade e SQL; no laboratório foi necessário remover `@Lob` e mapear `@Column(columnDefinition = "LONGTEXT")`.
- **`BindException: Endereço já em uso`**: outra aplicação ocupa a porta; conferir IntelliJ/terminal/Docker.
- **Prometheus target `DOWN`**: conferir API, endpoint `/actuator/prometheus`, rede Docker e porta.
- **OTLP 404**: verificar endpoint/receiver; não confundir com falha do scrape Prometheus.

## 10. Próximas etapas

- Finalizar e comprovar **Saga orquestrada**: máquina de estados, comandos, respostas, timeouts e compensações.
- Validar outbox ponta a ponta, idempotência, reprocessamento, retry, DLT e garantias de ordenação Kafka.
- Criar **métricas de negócio com `MeterRegistry`**, começando pelo `order-service`: pedidos criados, cancelados, rejeitados, aprovados, latência por etapa, estoque insuficiente, pagamentos recusados e backlog de outbox.
- Construir alertas no Grafana/Prometheus e testes automatizados dos cenários de falha.
- Retomar tracing distribuído com Jaeger/OpenTelemetry quando o fluxo de negócio estiver estabilizado.
- Preparar implantação Kubernetes, escalabilidade horizontal, configuração externa e segurança.

## 11. Estado ao encerrar esta etapa

Os quatro serviços foram reportados como **funcionando no ambiente local**. A migração do Order Service via Flyway foi validada, com o esquema na versão 1 e Hibernate inicializado. A última falha identificada do Order Service foi conflito na porta 8083, posteriormente resolvido no ambiente. Prometheus e Grafana estão operacionais. O próximo foco acordado é **métricas de negócio no Order Service**.

---