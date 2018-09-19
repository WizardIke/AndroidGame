package wizardike.assignment3;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WorkerThread extends Thread {
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    public void addTask(Runnable runnable) {
        queue.add(runnable);
    }

    public void run() {
        try {
            while (!isInterrupted()) {
                Runnable runnable = queue.take();
                runnable.run();
            }
        } catch (InterruptedException ex) {
            //quit
        }
    }
}
