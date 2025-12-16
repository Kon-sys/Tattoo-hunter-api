package com.example.gatewayservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    // эндпоинты, которые НЕ требуют токена вообще
    private static final List<String> openApiEndpoints = List.of(
            "/api/auth/sign-in",
            "/api/auth/sign-up",
            "/api/auth/refresh",
            "/api/auth/counters",
            "/api/admin"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // 1. Белый список без авторизации
        if (isOpenEndpoint(path)) {
            return chain.filter(exchange);
        }

        // 2. Читаем и проверяем JWT
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtService.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String login = jwtService.getLoginFromToken(token);
        String role  = jwtService.getRoleFromToken(token); // "ROLE_EMPLOYEE" / "ROLE_COMPANY"

        // 3. Проверяем, что роль имеет доступ к этому пути
        if (!hasAccess(path, role)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        // 4. Прокидываем логин и роль в заголовки
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Login", login)
                .header("X-User-Role", role)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private boolean isOpenEndpoint(String path) {
        return openApiEndpoints.stream().anyMatch(path::startsWith);
    }

    /**
     * Правила доступа по ролям.
     */
    private boolean hasAccess(String path, String role) {

        // -------- EMPLOYEE --------
        if ("ROLE_EMPLOYEE".equals(role)) {
            if (path.startsWith("/api/profile/employee")) return true;
            if (path.startsWith("/api/vacancies"))        return true;
            if (path.startsWith("/api/responses"))        return true;
            // 👇 все чаты (список, сообщения и т.п.) доступны работнику
            if (path.startsWith("/api/chats"))            return true;
        }

        // -------- COMPANY --------
        if ("ROLE_COMPANY".equals(role)) {
            if (path.startsWith("/api/profile/company")) return true;
            if (path.startsWith("/api/vacancy"))         return true;
            if (path.startsWith("/api/responses"))       return true;
            if (path.startsWith("/api/vacancies"))        return true;
            // 👇 все чаты доступны компании
            if (path.startsWith("/api/chats"))           return true;
        }

        if ("ROLE_ADMIN".equals(role)) {
            if (path.startsWith("/api/admin")) return true;
        }

        // если путь не подходит ни под одно правило – запрещаем
        return false;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
