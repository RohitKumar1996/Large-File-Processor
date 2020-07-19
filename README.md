# Large-File-Processor

The objective of this application is to build a pipeline which is able to import products data from a large CSV file into an elasticsearch index.

## Project setup steps

Pull the docker image of the kafka-producer springboot app using :

```bash
docker pull rk6991/kafka-elk-file-processor:kafka-producer
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

Clone this github repository and change the logstash volumes source path that by default is "/home/rk/Downloads/logstash-7.8.0/config/docker-logstash/" to the complete path in your local system (till the Logstash-Conf-File directory in this project like /{path}/Large-File-Processor/Logstash-Conf-File/
) in the docker-compose.yml

Add the following binding in /etc/hosts :

```bash
127.0.0.1       elasticsearch
```
Now, go to the project directory and setup the ELK stack using :
```bash
docker-compose up
```

To validate if the ELK services are up, go to browser and enter :
```bash
http://localhost:9200/
http://localhost:5601/
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

Now, go to kibana console and copy and run the query in check_index_data.txt in Elasticsearch-Commands project directory to check the data. 

Once the indexing gets completed, run the query in create_agg_transform.txt followed by the one in create_agg_index.txt to build the aggregated index which contains product names and count of that product. This data can be validated by replacing index name in 
check_index_data.txt query and triggering the same. 

## Project Output : 

- [ ] ![Screenshot from 2020-07-19 11-44-15](https://user-images.githubusercontent.com/28498767/87870325-5b414a80-c9c4-11ea-9d79-7d28bcceb216.png)
- [ ] ![Screenshot from 2020-07-19 11-57-19](https://user-images.githubusercontent.com/28498767/87870326-5d0b0e00-c9c4-11ea-9dd6-47282973b363.png)
- [ ] ![Screenshot from 2020-07-19 11-59-45](https://user-images.githubusercontent.com/28498767/87870327-5da3a480-c9c4-11ea-863e-87f04cb18d2b.png)
- [ ] ![Screenshot from 2020-07-19 12-06-14](https://user-images.githubusercontent.com/28498767/87870328-5ed4d180-c9c4-11ea-80d1-ed5821539b70.png)

## Elasticsearch index details : 

In this project we have created 2 indices - one for the products data (with 'sku' as the roll up field) and the second aggregated index containing name and no. of products with that name. The details such as their schema, sample data (documents in this index) along with the commands to create the index are in elasticsearch-index-fields folder in this project repository.

## Points to Achieve :

1. The kafka-producer springboot app follows the abstraction and encapsulation concepts of object oriented programming.
2. There is regular non-blocking parallel ingestion of the given file into the elasticsearch index and it was imported in less than 2 minutes.
3. I have added the support for updating existing products in the index based on 'sku' as the primary key. I achieved this in the logstash step by making sku field as the document_id.
4. All product details have been ingested into a single index (products_index).
5. I was able to create an aggregated index 'agg_products_index' over 'products_index' with 'name' and 'no. of products' as the columns. I had used the transform API in elasticsearch to achieve this.

## What would you improve if given more days :

I have used elasticsearch transform API to create the aggregate index. If given more time, I would have worked on some way to do this processing in an intermediate step and then index the aggregated result on elasticsearch. 

