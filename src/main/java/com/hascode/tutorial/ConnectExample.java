package com.hascode.tutorial;

import io.nats.client.Connection;
import io.nats.client.Nats;

public class ConnectExample {

  public static void main(String[] args) {
    try (Connection natsConnection = Nats.connect("nats://localhost:4222")) {
      System.out.printf("Connected to NATS! %s %n", natsConnection.getServerInfo());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
