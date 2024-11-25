package com.hascode.tutorial;

import io.nats.client.Connection;
import io.nats.client.Nats;

public class PublishExample {

  public static void main(String[] args) {
    try (Connection natsConnection = Nats.connect("nats://localhost:4222")) {
      String subject = "tasks.greetings";
      String message = "Hello, NATS! Visit www.hascode.com!";
      natsConnection.publish(subject, message.getBytes());
      System.out.printf("Message published to subject: %s and server %s", subject,
          natsConnection.getServerInfo());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
