# Large-File-Processor

The objective of this application is to build a pipeline which is able to import products data from a large CSV file into an elasticsearch index.

## Project setup steps

pull the docker image of the kafka-producer springboot app using :

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

Go to <https://kafka.apache.org/quickstart>, download kafka 2.5.0 and untar-it. Next go to ~/Downloads/kafka_2.12-2.5.0 directory and set up a single-node ZooKeeper instance along with multi broker kafka cluster (all on single machine itself) using :

```bash
http://localhost:9200/
http://localhost:5601/
```

## Usage

```python
import foobar

foobar.pluralize('word') # returns 'words'
foobar.pluralize('goose') # returns 'geese'
foobar.singularize('phenomena') # returns 'phenomenon'
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)
