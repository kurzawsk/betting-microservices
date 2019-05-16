package pl.kk.services.common.service.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
public class AsyncLogicExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncLogicExecutor.class);

    @Async("threadPoolTaskExecutor")
    protected CompletableFuture<Void> runJobAsync(Supplier<Void> jobLogic) {
        System.out.println("Execute method asynchronously. "
                + Thread.currentThread().getName());
        try {
            jobLogic.get();
        } catch (Exception e) {
            LOGGER.error("Exception occurred while running async task",e);
            CompletableFuture<Void> cf = new CompletableFuture<>();
            cf.completeExceptionally(e);
            return cf;
        }
        return CompletableFuture.completedFuture(null);
    }
}
