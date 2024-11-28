package com.hascode.tutorial;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.JetStreamManagement;
import io.nats.client.JetStreamSubscription;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.PullSubscribeOptions;
import io.nats.client.api.AckPolicy;
import io.nats.client.api.ConsumerConfiguration;
import io.nats.client.api.DeliverPolicy;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;
import java.time.Duration;
import java.time.ZonedDateTime;

public class DeliveryExamples {

    public static void main(String[] args) {
      try (Connection nc = Nats.connect("nats://localhost:4222")) {
        JetStreamManagement jsm = nc.jetStreamManagement();
        JetStream js = nc.jetStream();

        String streamName = "example-stream";
        String subject = "example-subject";

        // Create Stream if not exists
        try {
          jsm.addStream(StreamConfiguration.builder()
              .name(streamName)
              .subjects(subject)
              .storageType(StorageType.Memory)
              .build());
        } catch (JetStreamApiException e) {
          System.out.println("Stream already exists.");
        }

        // Publish messages for demonstration
        for (int i = 1; i <= 10; i++) {
          js.publish(subject, ("Message " + i).getBytes());
        }

        System.out.println("Published 10 messages.");

        // ---- Poll from a Specific Sequence ----
        System.out.println("\nPolling from sequence 5:");
        ConsumerConfiguration seqConsumerConfig = ConsumerConfiguration.builder()
            .ackPolicy(AckPolicy.Explicit)
            .deliverPolicy(DeliverPolicy.ByStartSequence)
            .startSequence(5)
            .build();

        JetStreamSubscription seqSubscription = js.subscribe(subject, PullSubscribeOptions.builder()
            .configuration(seqConsumerConfig)
            .build());

        seqSubscription.pull(5); // Pull 5 messages
        for (Message msg : seqSubscription.fetch(5, Duration.ofSeconds(2))) {
          System.out.println("Received: " + new String(msg.getData()));
          msg.ack();
        }

        // ---- Poll from a Specific Time ----
        System.out.println("\nPolling messages after a specific time:");
        ZonedDateTime startTime = ZonedDateTime.now().minusMinutes(1); // Messages from the last 1 minute
        ConsumerConfiguration timeConsumerConfig = ConsumerConfiguration.builder()
            .ackPolicy(AckPolicy.Explicit)
            .deliverPolicy(DeliverPolicy.ByStartTime)
            .startTime(startTime)
            .build();

        JetStreamSubscription timeSubscription = js.subscribe(subject, PullSubscribeOptions.builder()
            .configuration(timeConsumerConfig)
            .build());

        timeSubscription.pull(5); // Pull 5 messages
        for (Message msg : timeSubscription.fetch(5, Duration.ofSeconds(2))) {
          System.out.println("Received: " + new String(msg.getData()));
          msg.ack();
        }

        // ---- Poll the Latest Messages ----
        System.out.println("\nPolling the latest message:");
        ConsumerConfiguration latestConsumerConfig = ConsumerConfiguration.builder()
            .ackPolicy(AckPolicy.Explicit)
            .deliverPolicy(DeliverPolicy.Last)
            .build();

        JetStreamSubscription latestSubscription = js.subscribe(subject, PullSubscribeOptions.builder()
            .configuration(latestConsumerConfig)
            .build());

        latestSubscription.pull(3); // Pull the latest message
        for (Message msg : latestSubscription.fetch(1, Duration.ofSeconds(2))) {
          System.out.println("Received: " + new String(msg.getData()));
          msg.ack();
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

}
