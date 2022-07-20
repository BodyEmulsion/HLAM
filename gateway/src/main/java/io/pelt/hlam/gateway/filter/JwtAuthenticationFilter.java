package io.pelt.hlam.gateway.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
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
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory {
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Autowired
    @Lazy
    private AuthService authService;

    private JWTVerifier verifier;
    private Mono<Map<String, BigInteger>> publicKeyRequest;

    JwtAuthenticationFilter() {
        super();
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            if (this.publicKeyRequest == null) {
                requestPublicKey();
            }
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

    private void requestPublicKey(){
        this.publicKeyRequest = this.authService.getPublicKey();
        this.publicKeyRequest.subscribe(
                rsaPublicKeySpec -> {
                    try {
                        this.verifier = getJwtVerifier(rsaPublicKeySpec);
                        //TODO: come up with exceptions
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    } catch (InvalidKeySpecException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    logger.error("Request public key error", error);
                    this.publicKeyRequest = null;
                });
    }

    private JWTVerifier getJwtVerifier(Map<String, BigInteger> rsaPublicKeySpec)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        var publicKeySpec = new RSAPublicKeySpec(
                rsaPublicKeySpec.get("modulus"),
                rsaPublicKeySpec.get("exp"));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        var algorithm = Algorithm.RSA512(publicKey, null);
        return JWT.require(algorithm).build();
    }

    private Mono<Void> validate(ServerWebExchange exchange, ServerHttpRequest request) {
        if (!request.getHeaders().containsKey("jwt"))
            return getJWTNotPresentResponse(exchange);
        if (this.verifier == null)
            return getJWTVerifierNotPresentResponse(exchange);

        final String jwt = request.getHeaders().getOrEmpty("jwt").get(0);
        try {
            var decoded = this.verifier.verify(jwt);
            decoded.getClaims().forEach((String key, Claim claim) ->
                    exchange.getRequest().mutate().header("jwt-" + key, String.valueOf(claim)).build());
            exchange.getRequest().mutate().header("jwt", (String) null);
        } catch (JWTVerificationException error) {
            return getJWTVerificationErrorResponse(exchange, error);
        }
        return null;
    }

    private Mono<Void> getJWTVerificationErrorResponse(ServerWebExchange exchange, JWTVerificationException e) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        response.getHeaders().add("error-message", e.getMessage());
        //TODO: come up with something better
        return response.setComplete();
    }

    private Mono<Void> getJWTVerifierNotPresentResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        response.getHeaders().add("Retry-After", "30");
        return response.setComplete();
    }

    private Mono<Void> getJWTNotPresentResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
}
