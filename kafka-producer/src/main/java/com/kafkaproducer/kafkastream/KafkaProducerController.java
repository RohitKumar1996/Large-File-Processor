package com.kafkaproducer.kafkastream;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kafkaproducer.kafkastream.Services.kafkaProducerService;

@Controller
public class KafkaProducerController {

//	private static String KafkaBrokerEndpoint = "localhost:9092";
//	private static String KafkaTopic = "test-topic";
//	private static String CsvFile = "/home/rk/postman-test/products.csv";

	@RequestMapping(value = "/pushToKafka", method = RequestMethod.GET)
	public @ResponseBody Map<String, String> PublishMessages(@Context HttpHeaders headers,
			@QueryParam("KafkaBrokerEndpoint") String KafkaBrokerEndpoint, @QueryParam("KafkaTopic") String KafkaTopic,
			@QueryParam("CsvFile") String CsvFile) throws JSONException {
		return kafkaProducerService.PublishMessagesImpl(KafkaBrokerEndpoint, KafkaTopic, CsvFile);
	}
}
