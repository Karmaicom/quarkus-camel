package br.com.camel.services;

import br.com.camel.model.DadosRouterDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductService {

    public DadosRouterDto getProduct(DadosRouterDto dados) {
        return dados;
    }

}
