package io.github.slangerosuna.engine.core.scheduler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WorkerThread implements Runnable {
    private BlockingQueue<Task> tasks;
    private Lock lock;

    public WorkerThread(BlockingQueue<Task> tasks) {
        this.tasks = tasks;
        lock = new ReentrantLock();
        new Thread(this).start();
    }

    public void waitUntilDone() {
        lock.lock();
        lock.unlock();
    }

    public void run() {
        lock.lock();
        Task task = getNextTask();
        while (task != null) {
            task.execute();
            task = getNextTask();
        }
        lock.unlock();

        try { Thread.sleep(10); }
        catch (InterruptedException e)
            {Thread.currentThread().interrupt();}
        run();
    }

    private Task getNextTask() {
        try { return tasks.take(); }
        catch (InterruptedException e)
            { Thread.currentThread().interrupt(); }
        return getNextTask();
    }
}
