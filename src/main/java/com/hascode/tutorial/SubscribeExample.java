package com.hascode.tutorial;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;

public class SubscribeExample {

  public static void main(String[] args) {
    try (Connection natsConnection = Nats.connect("nats://localhost:4222")) {
      String subject = "communication.greetings";

      Dispatcher dispatcher = natsConnection.createDispatcher((msg) -> {
        String received = new String(msg.getData());
        System.out.printf("Received message: %s via connection: %s %n", received,
            natsConnection.getServerInfo());
      });

      dispatcher.subscribe(subject);
      System.out.println("Subscribed to subject: " + subject);

      // We're keeping it running to receive messages
      Thread.sleep(60000); // 1 minute
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
