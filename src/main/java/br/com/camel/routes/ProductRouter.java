package br.com.camel.routes;

import br.com.camel.model.DadosRouterDto;
import br.com.camel.services.ProductService;
import org.apache.camel.builder.RouteBuilder;

public class ProductRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:getProduct")
                .routeId("getProduct")
                .convertBodyTo(DadosRouterDto.class)
                .log("CAMEL: Iniciando...")
                .bean(ProductService.class, "getProduct")
                .log("CAMEL: Mensagem enviada!");
    }
}
