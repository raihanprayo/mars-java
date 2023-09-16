package dev.scaraz.mars.app.gateway;

import dev.scaraz.mars.common.tools.enums.Witel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@EnableEurekaClient
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RouteLocator witelRouteLocator(RouteLocatorBuilder builder) {
        return () -> {
            RouteLocatorBuilder.Builder routes = builder.routes();
            for (Witel witel : Witel.values()) {
                String name = witel.name();

                String id = "mars-" + name;
                String path = String.format("/api/witel/%s", name);

                routes.route(id, spec -> spec
                        .path(true, path + "/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .rewritePath(path + "/(?<path>.*)", "/$\\{path}"))
                        .uri("lb://" + id)
                );
            }

            return routes.build().getRoutes();
        };
    }

}
