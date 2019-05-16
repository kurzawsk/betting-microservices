package pl.kk.services.common.misc;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;

public class AsyncUtil {

    public static <T> CompletableFuture<List<T>> allOf(List<CompletableFuture<T>> cfs) {
        CompletableFuture<Void> allFutures = CompletableFuture
                .allOf(cfs.toArray(new CompletableFuture[cfs.size()]));
        return allFutures.thenApply(future ->
                cfs.stream()
                        .map(CompletableFuture::join)
                        .collect(toList())
        );
    }

}
