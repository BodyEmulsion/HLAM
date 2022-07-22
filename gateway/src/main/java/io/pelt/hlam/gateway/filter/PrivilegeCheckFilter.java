package io.pelt.hlam.gateway.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PrivilegeCheckFilter extends AbstractGatewayFilterFactory<PrivilegeCheckFilter.Config> {
    PrivilegeCheckFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            var request = exchange.getRequest();
            var rawJwtPrivileges = request.getHeaders().get("jwt-privileges").get(0);
            var jwtPrivileges =
                    Arrays.stream(rawJwtPrivileges
                                    .substring(1, rawJwtPrivileges.length() - 1)
                                    .split(","))
                            .map(str -> str.trim().substring(1, str.length() - 1))
                            .collect(Collectors.toList());
            if(jwtPrivileges.contains(config.getPrivilege())) {
                var response = exchange.getResponse();
                response.setStatusCode(HttpStatus.FORBIDDEN);
                response.getHeaders().add("error-message", "JWT doesn't contain required privilege");
                return response.setComplete();
            }
            return chain.filter(exchange);
        }, 1);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("privilege");
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config {
        private String privilege;
    }
}
