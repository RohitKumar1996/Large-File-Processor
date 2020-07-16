package com.kafkaproducer.kafkastream.Services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.ResponseBody;

public class kafkaProducerService {

	private static Producer<String, String> ProducerProperties(String KafkaBrokerEndpoint) {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaBrokerEndpoint);
		properties.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaCsvProducer");
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		return new KafkaProducer<String, String>(properties);
	}

	static String[] headers(String path) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			return br.readLine().split(",");
		}
	}

	public static Map<String, String> PublishMessagesImpl(String KafkaBrokerEndpoint, String KafkaTopic, String CsvFile)
			throws JSONException {
		final Producer<String, String> CsvProducer = ProducerProperties(KafkaBrokerEndpoint);
		Map<String, String> response = new HashMap<String, String>();

		try {

			// Read headers
			String[] headers = headers(CsvFile);

//			List<Map<String, String>> result = null;

			Stream<String> FileStream = Files.lines(Paths.get(CsvFile));

			FileStream.skip(1).forEach(line -> {

				String[] lineArray = line.split(",");

				Map<String, String> map = new HashMap<>();

				for (int i = 0; i < lineArray.length; i++) {
					map.put(headers[i], lineArray[i]);
				}

				JSONObject obj = new JSONObject(map);

				final ProducerRecord<String, String> jsoRecord = new ProducerRecord<String, String>(KafkaTopic, null,
						obj.toString());

				System.out.println("\n****\n" + obj.toString() + "\n****\n");

				CsvProducer.send(jsoRecord, (metadata, exception) -> {
					if (metadata != null) {
						System.out.println("jsonData: -> " + jsoRecord.key() + " | " + jsoRecord.value());
					} else {
						System.out.println("Error Sending json Record -> " + jsoRecord.value());
					}
				});
			});

			response.put("Message", "Success");

		} catch (IOException e) {
			response.put("Message", "Failed");
			e.printStackTrace();
		}

		return response;
	}

}
