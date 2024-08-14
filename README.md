# Order-Service
Order-Service for online shopping

When an order is created, send a message containing comprehensive details of the order. This allows other services like inventory and shipping to initiate their respective processes.

For an order update, which might include status changes due to payment completion or shipment, focus on what has changed, along with the current state.

For cancellations, specify the reason and the immediate previous state to help other services revert any provisional actions they may have taken.

run cassandra and kafka in docker
docker-compose up -d


create keyspace manually
- docker exec -it orderservice-cassandra-1 cqlsh
- CREATE KEYSPACE order_service WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
