to run Kafka locally:
cd /Users/viktoriakonopleva/Downloads/kafka_2.13-3.6.0
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
*
 bin/kafka-topics.sh --create --topic USER_STATE --bootstrap-server localhost:9092
 bin/kafka-topics.sh --create --topic ORDER_STATE --bootstrap-server localhost:9092