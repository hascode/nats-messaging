package com.hascode.tutorial;

import io.nats.client.Connection;
import io.nats.client.Nats;

public class PublishManyGreetingsExample {

  public static void main(String[] args) {
    try (Connection natsConnection = Nats.connect("nats://localhost:4222")) {
      String subject = "tasks.greetings";

      for (int i = 0; i < 100; i++) {
        String message = "Hello, NATS! Visit www.hascode.com! #" + i;
        natsConnection.publish(subject, message.getBytes());
        System.out.printf("Message %d published to subject: %s and server %n", i, subject);
      }
      natsConnection.flushBuffer();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
