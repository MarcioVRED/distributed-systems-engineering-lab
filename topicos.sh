
#!/bin/bash

set -e

echo "Criando tópicos Kafka..."

TOPICS=(
  "order.created"
  "order.cancelled" 
  "stock.reserved" 
  "stock.rejected"
  "payment.requested" 
  "payment.approved" 
  "payment.rejected"
  "order.created.DLT" 
  "order.cancelled.DLT"
  "stock.reserved.DLT" 
  "stock.rejected.DLT"
  "payment.requested.DLT" 
  "payment.approved.DLT" 
  "payment.rejected.DLT"
)
for topic in "${TOPICS[@]}"; do

  echo "Verificando tópico: $topic"

  docker exec ecommerce-kafka1 \
    /opt/kafka/bin/kafka-topics.sh \
    --bootstrap-server localhost:29092 \
    --create \
    --if-not-exists \
    --topic "$topic" \
    --partitions 3 \
    --replication-factor 3

done

echo ""
echo "Tópicos disponíveis:"

docker exec ecommerce-kafka1 \
  /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:29092 \
  --list

echo ""
echo "Finalizado!"
