package com.hascode.tutorial;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.JetStreamManagement;
import io.nats.client.JetStreamSubscription;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;

public class StreamingExample {

  public static void main(String[] args) {
    // Connect to the NATS server
    try (Connection nc = Nats.connect("nats://localhost:4222")) {
      System.out.println("Connected to NATS server.");

      // Access the JetStream context
      JetStreamManagement jsm = nc.jetStreamManagement();
      JetStream js = nc.jetStream();

      // Define and add a stream
      String streamName = "example-stream";
      String subject = "example-subject";

      StreamConfiguration streamConfig = StreamConfiguration.builder()
          .name(streamName)
          .subjects(subject)
          .storageType(StorageType.Memory)
          .build();

      // Create the stream if it doesn't exist
      try {
        jsm.addStream(streamConfig);
        System.out.println("Stream created: " + streamName);
      } catch (JetStreamApiException e) {
        System.out.println("Stream already exists or another error occurred.");
      }

      // Publish a few messages to the stream
      for (int i = 1; i <= 5; i++) {
        String message = "Hello JetStream " + i;
        js.publish(subject, message.getBytes());
        System.out.println("Published message: " + message);
      }

      // Set up a consumer to read messages
      JetStreamSubscription subscription = js.subscribe(subject);
      System.out.println("Subscribed to subject: " + subject);

      // Read messages
      Message msg;
      while ((msg = subscription.nextMessage(2000)) != null) {
        System.out.println("Received message: " + new String(msg.getData()));
        msg.ack(); // Acknowledge the message
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
