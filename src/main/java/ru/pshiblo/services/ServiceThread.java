package ru.pshiblo.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class ServiceThread implements Service {

    private ExecutorService executor;

    public void reRun() {
        shutdown();
        start();
    }

    @Override
    public void start() {
        if (executor != null)
            throw new IllegalCallerException("executor is run!");


        executor = Executors.newSingleThreadExecutor();
        executor.submit(this::runInThread);
    }

    @Override
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
            executor = null;
        }
    }

    @Override
    public boolean isInitializer() {
        return executor != null;
    }

    protected abstract void runInThread();
}
