package com.hascode.tutorial;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import java.time.Duration;

public class RequestExample {

  public static void main(String[] args) {
    try (Connection natsConnection = Nats.connect("nats://localhost:4222")) {
      String subject = "questions.request";
      String requestMessage = "What is the strangest tech blog?";

      System.out.println("Sending request: " + requestMessage);

      // Send the request and wait for the reply (timeout: 2 seconds)
      Message reply = natsConnection.request(subject, requestMessage.getBytes(),
          Duration.ofSeconds(2));

      if (reply != null) {
        String response = new String(reply.getData());
        System.out.println("Received reply: " + response);
      } else {
        System.out.println("No reply received within timeout!");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
