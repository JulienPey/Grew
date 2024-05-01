package engine.pixel.grew;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPool {

    private final ExecutorService threadPool;

    public ThreadPool() {

        this.threadPool = Executors.newFixedThreadPool(2);
    }

    public Future<?> addThread(Runnable codeToExecute) {
        return threadPool.submit(codeToExecute);
    }

}

