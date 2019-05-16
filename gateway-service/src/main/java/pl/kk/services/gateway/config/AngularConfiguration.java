package pl.kk.services.gateway.config;

import com.google.common.io.ByteStreams;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import pl.kk.services.gateway.GatewayApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Configuration
public class AngularConfiguration {

    @Bean
    public WebFilter angularFilter() {
        return new WebFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange ctx, WebFilterChain chain) {
                ServerHttpRequest request = ctx.getRequest();
                ServerHttpResponse response = ctx.getResponse();
                String path = request.getURI().getPath();
                if (path.startsWith("/ui")) {
                    byte[] content = isResourceFile("/static" + path) ? getResourceContent("/static" + path) : getResourceContent("/static/ui/index.html");
                    return response.writeWith(Flux.just(response.bufferFactory()
                            .wrap(content)));

                }
                return chain.filter(ctx);
            }

            private byte[] getResourceContent(String path) {
                try (InputStream is = getClass().getResourceAsStream(path)) {
                    return Objects.nonNull(is) ? ByteStreams.toByteArray(is) : new byte[0];
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            private boolean isResourceFile(String path) {
                return path.contains(".") && Objects.nonNull(GatewayApplication.class.getResource(path));

            }
        };
    }
}
