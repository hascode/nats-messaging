package com.hascode.tutorial;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;

public class QueueSubscribersExample {

  public static void main(String[] args) {
    String subject = "tasks.greetings";
    String queueGroup = "task-workers";

    for (int i = 1; i <= 4; i++) {
      int workerId = i;
      new Thread(() -> {
        try (Connection natsConnection = Nats.connect("nats://localhost:4222")) {
          Dispatcher dispatcher = natsConnection.createDispatcher((msg) -> {
            String received = new String(msg.getData());
            System.out.println("Subscriber-" + workerId + " received: " + received);
          });

          dispatcher.subscribe(subject, queueGroup);
          System.out.println(
              "Subscriber-" + workerId + " subscribed to tasks in queue group: " + queueGroup);

          // Keep the subscriber thread running
          Thread.sleep(60000); // 1 minute
        } catch (Exception e) {
          e.printStackTrace();
        }
      }, "Subscriber-" + i).start();
    }
  }
}
