package br.com.camel.controllers;

import br.com.camel.model.Client;
import br.com.camel.model.DadosRouterDto;
import br.com.camel.model.Product;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.camel.ProducerTemplate;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {

    private final ProducerTemplate producerTemplate;

    @Inject
    public ProductController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @GET
    public Response getProduct() {
        var product = new Product("Notebook Lenovo", 10);
        var client = new Client("Fulano", "fulano@gmail.com");
        var dados = new DadosRouterDto(product, client);

        String productRouter = producerTemplate.requestBody(
                "direct:getProduct",
                dados,
                String.class
        );

        return Response.status(Response.Status.OK).entity(dados).build();
    }
}
