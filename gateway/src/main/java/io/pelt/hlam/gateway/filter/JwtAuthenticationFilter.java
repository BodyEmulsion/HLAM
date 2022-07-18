package io.pelt.hlam.gateway.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import io.pelt.hlam.gateway.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory {
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Lazy

    @Autowired
    private AuthService authService;

    private CompletableFuture<ResponseEntity<X509EncodedKeySpec>> publicKey;

    JwtAuthenticationFilter(){
        super();
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            this.logger.info("In JwtAuthenticationFilter");
            ServerHttpRequest request = exchange.getRequest();
            final List<String> apiEndpoints = List.of("auth-service/login");
            //TODO: move unsecured api to configration(?)

            Predicate<ServerHttpRequest> isApiSecured = r -> apiEndpoints.stream()
                    .noneMatch(uri -> r.getURI().getPath().contains(uri));

            if (isApiSecured.test(request)) {
                Mono<Void> response = validate(exchange, request);
                if (response != null) return response;
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> validate(ServerWebExchange exchange, ServerHttpRequest request) {
        if (!request.getHeaders().containsKey("jwt")) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        final String jwt = request.getHeaders().getOrEmpty("jwt").get(0);
        if (this.publicKey == null){
            this.publicKey =
                    CompletableFuture.supplyAsync(() -> this.authService.getPublicKey()).exceptionally(ex -> {
                        ex.printStackTrace();
                        throw new RuntimeException();
                    });
        }
        if (!this.publicKey.isDone()) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.I_AM_A_TEAPOT);
            return response.setComplete();
        } else if (this.publicKey.isCompletedExceptionally()){
            logger.info("Exceptionally");
            this.publicKey =
                    CompletableFuture.supplyAsync(() -> this.authService.getPublicKey()).exceptionally(ex -> {
                        ex.printStackTrace();
                        throw new RuntimeException();
                    });
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.I_AM_A_TEAPOT);
            return response.setComplete();
        } else {
            try {
                X509EncodedKeySpec keySpec = this.publicKey.get().getBody();
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);


                var algorithm = Algorithm.RSA512(pubKey, null);
                var verifier = JWT.require(algorithm).build();
                //TODO: try to make verifier singleton, after buing fire estinguisher
                var decoded = verifier.verify(jwt);
                decoded.getClaims().forEach((String key, Claim claim) ->
                        exchange.getRequest().mutate().header("jwt-" + key, String.valueOf(claim)).build());
            } catch (JWTVerificationException e) {
                //TODO: Add information about verification error
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return response.setComplete();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
