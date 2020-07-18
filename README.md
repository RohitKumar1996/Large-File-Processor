# Large-File-Processor

The objective of this application is to build a pipeline which is able to import products data from a large CSV file into an elasticsearch index.

## Project setup steps

Pull the docker image of the kafka-producer springboot app using :

```bash
docker pull rk6991/kafka-elk-file-processor:kafka-producer
```

Clone this github repository and change the logstash volumes source path that by default is "/home/rk/Downloads/logstash-7.8.0/config/docker-logstash/" to the complete path in your local system (till the Logstash-Conf-File directory in this project like /{path}/Large-File-Processor/Logstash-Conf-File/
) in the docker-compose.yml

Now, go to the project directory and setup the ELK stack using :
```bash
docker-compose up
```

To validate if the ELK services are up, go to browser and enter :
```bash
http://localhost:9200/
http://localhost:5601/
```

Kafka setup :

Go to <https://kafka.apache.org/quickstart>, download kafka 2.5.0 and untar-it. Next go to ~/Downloads/kafka_2.12-2.5.0 directory and do the following to establish a multi broker kafka cluster :

Make a config file for each of the brokers :

```bash
cp config/server.properties config/server-1.properties
cp config/server.properties config/server-2.properties
```

edit these new files and set the following properties:

```bash
config/server-1.properties:
    broker.id=1
    listeners=PLAINTEXT://:9093
    log.dirs=/tmp/kafka-logs-1
 
config/server-2.properties:
    broker.id=2
    listeners=PLAINTEXT://:9094
    log.dirs=/tmp/kafka-logs-2
```

Now, set up a single-node ZooKeeper instance along with multi broker kafka cluster (all on single machine itself) using the following commands each in a separate tab:

```bash
bin/zookeeper-server-start.sh config/zookeeper.properties

bin/kafka-server-start.sh config/server.properties

bin/kafka-server-start.sh config/server-1.properties &

bin/kafka-server-start.sh config/server-2.properties &
```

Create a new topic with partition and replication factor of 3 :

```bash
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 3 --topic my-test-topic
```
Now, start the container for kafka-producer image pulled in the beginning, copy the csv file to be imported to docker container environment and test the API response using :

```bash
docker run --network host -p 8085:8085 {IMAGE ID}
docker cp /{path-to-file}/products.csv {NAMES}:/home/products.csv

Note :
{IMAGE ID} can be found using the command - "docker images"
{NAMES} can be found via - "docker ps"

http://localhost:8085/testMyAPI 

The response for above URL should be "Successfully running :)"
```

Go to kibana UI on <http://localhost:5601/>, on the top left dropdown select dev tools to go to the kibana console. Copy the command from create_index.txt in Elasticsearch-Commands directory in the project and run this to create the index on elasticsearch.

Go to the browser and run the following :

```bash
http://localhost:8085/pushToKafka?KafkaBrokerEndpoint=127.0.0.1:9092&KafkaTopic=my-test-topic&CsvFile=/home/products.csv
```
