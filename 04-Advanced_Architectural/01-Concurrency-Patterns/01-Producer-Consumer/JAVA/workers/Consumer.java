package workers;

import queue.MessageQueue;

public class Consumer implements Runnable {
    private final MessageQueue queue;
    private final int id;

    public Consumer(MessageQueue queue, int id) {
        this.queue = queue;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Blocks here if the queue is empty
                String message = queue.consume();
                
                // Simulate time taken to process the item (e.g., Sending an Email)
                Thread.sleep(1000); 
            }
        } catch (InterruptedException e) {
            System.out.println("🛑 [Consumer-" + id + "] Shutting down...");
            Thread.currentThread().interrupt();
        }
    }
}
