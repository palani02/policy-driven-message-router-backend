package com.grootan.router.queue;

import com.grootan.router.model.QueuedMessage;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class MessageQueue {

    private final BlockingQueue<QueuedMessage> queue =
            new LinkedBlockingQueue<>();

    /**
     * Add message to queue.
     */
    public void enqueue(QueuedMessage queuedMessage) {

        try {

            queue.put(queuedMessage);

            System.out.println("---------------------------------------");
            System.out.println("MESSAGE ADDED TO QUEUE");
            System.out.println("Message ID : " + queuedMessage.getMessage().getId());
            System.out.println("Queue Size : " + queue.size());
            System.out.println("---------------------------------------");

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException("Failed to enqueue message.", e);

        }

    }

    /**
     * Remove next message.
     */
    public QueuedMessage dequeue() {

        try {

            QueuedMessage queuedMessage = queue.take();

            System.out.println("---------------------------------------");
            System.out.println("MESSAGE REMOVED FROM QUEUE");
            System.out.println("Message ID : " + queuedMessage.getMessage().getId());
            System.out.println("Queue Size : " + queue.size());
            System.out.println("---------------------------------------");

            return queuedMessage;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException("Failed to dequeue message.", e);

        }

    }

    public int size() {

        return queue.size();

    }

    public boolean isEmpty() {

        return queue.isEmpty();

    }

}