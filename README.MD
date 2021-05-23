This is the kafka-producer implementation, and some commands to run the server

# Single installation setup
1. Edit zookeeper.properties and server.properties to set the message persistent time, log location etc. [[Windows Version]](https://www.loginradius.com/blog/async/quick-kafka-installation/)
2. Start Zookeeper cluster as follow `.bin\windows\zookeeper-server-start.bat ..\..\config\zookeeper.properties`
3. Start Kafka cluster as follow `.bin\windows\kafka-server-start.bat ..\..\config\server.properties`
4. Creating a topic `.\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 -topic chat`
5. List all topics `.\kafka-topics.bat --list --bootstrap-server localhost:9092`
6. Publishing message to kafka cluster `.\kafka-console-producer.bat --broker-list localhost:9092 --topic chat`
7. Add a consumer to consume the queue `.\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic chat --from-beginning`

# Multi broker
1. Duplicate server.properties
2. change `broker.id` to another ID, must be unique
3. change `listeners` to another port e.g. `9093`, on production this will not be needed because differet brokers are on different computers
4. Change `log.dirs` to another path for it to persist all the data
5. launch 3 brokers `.\kafka-server-start.bat ..\..\config\server2.properties` & `.\kafka-server-start.bat ..\..\config\server1.properties
6. create a replication factor of 3 with 3 partitions `.\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 3 -topic purchases`. Do note that only a subset of servers should be bootstrap server
7. Describe the topic `.\kafka-topics.bat --describe --bootstrap-server localhost:9092 --topic purchases`. `Isr` is in-sync-replicas which means they are in-sync if same with replicas.
8. Try sending 4 messages `.\kafka-console-producer.bat --broker-list localhost:9092 --topic purchases`
9. Consuming `.\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic purchases --from-beginning` do note that order is not same because within partition is an ordered queue but there is no order queue between different partitions.

