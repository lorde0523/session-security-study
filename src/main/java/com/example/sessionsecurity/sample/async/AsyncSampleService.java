package com.example.sessionsecurity.sample.async;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncSampleService {

    @Async("applicationTaskExecutor")
    public CompletableFuture<String> runAsyncMessage(String message) {
        String result = "async message: " + message + ", thread: " + Thread.currentThread().getName()
                + ", time: " + LocalDateTime.now();
        return CompletableFuture.completedFuture(result);
    }
}
