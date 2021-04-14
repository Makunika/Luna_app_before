package ru.pshiblo.base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class ComponentThread {
    private final ExecutorService executor;

    public ComponentThread() {
        executor = Executors.newSingleThreadExecutor();
    }

    public void reRun() {
        shutdown();
        start();
    }

    public void start() {
        if (!executor.isShutdown())
            throw new IllegalCallerException("executor is run!");

        executor.submit(this::runInThread);
    }

    public void shutdown() {
        try {
            System.out.println("attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        }
        finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executor.shutdownNow();
            System.out.println("shutdown finished");
        }
    }

    protected abstract void runInThread();
}
