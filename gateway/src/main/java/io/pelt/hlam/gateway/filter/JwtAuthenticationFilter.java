package io.pelt.hlam.gateway.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
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

import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.function.Predicate;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory {
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    JwtAuthenticationFilter(){
        super();
    }

    @Autowired
    @Lazy
    private RSAPublicKey publicKey;

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
                if (!request.getHeaders().containsKey("jwt")) {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }

                final String jwt = request.getHeaders().getOrEmpty("jwt").get(0);

                try {
                    var algorithm = Algorithm.RSA512(publicKey, null);
                    var verifier = JWT.require(algorithm).build();
                    //TODO: try to make verifier singleton, after buing fire estinguisher
                    var decoded = verifier.verify(jwt);
                    decoded.getClaims().forEach((String key, Claim claim) ->
                            exchange.getRequest().mutate().header(key, String.valueOf(claim)).build());
                } catch (JWTVerificationException e) {
                    //TODO: Add information about verification error
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.BAD_REQUEST);
                    return response.setComplete();
                }
            }
            return chain.filter(exchange);
        };
    }
}
