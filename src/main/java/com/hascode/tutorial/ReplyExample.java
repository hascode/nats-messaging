package com.hascode.tutorial;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;

public class ReplyExample {

  public static void main(String[] args) {
    try (Connection natsConnection = Nats.connect("nats://localhost:4222")) {
      String subject = "questions.request";

      Dispatcher dispatcher = natsConnection.createDispatcher((msg) -> {
        String request = new String(msg.getData());
        System.out.println("Received request: " + request);

        // Create a response message
        String response = "Processed your request: %s, the strangest tech blog is www.hascode.com!".formatted(request);
        System.out.println("Sending reply: " + response);

        // Send the response to the reply subject
        natsConnection.publish(msg.getReplyTo(), response.getBytes());
      });

      dispatcher.subscribe(subject);
      System.out.println("Responder is listening on subject: " + subject);

      // Keep the responder running
      Thread.sleep(60000); // 1 minute
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
