# Quarkus + Apache Camel

API REST demonstrativa construída com **Java 21**, **Quarkus** e **Apache Camel**. O projeto mostra como expor um recurso HTTP com Jakarta REST, iniciar uma rota Camel por um endpoint Direct, processar a mensagem em um bean CDI e devolver os dados em JSON.

## Visão geral

Ao receber uma requisição **GET /products**, a aplicação:

1. cria um produto e um cliente de exemplo;
2. agrupa os dados em um DadosRouterDto;
3. envia o DTO para a rota direct:getProduct por meio de ProducerTemplate;
4. converte e encaminha a mensagem ao ProductService;
5. registra o início e o fim do processamento nos logs;
6. responde com os dados em JSON e o status HTTP 200 OK.

## Tecnologias, frameworks e bibliotecas

| Tecnologia | Versão/configuração | Uso no projeto |
| --- | --- | --- |
| Java | 21 | Linguagem e runtime |
| Quarkus | 3.39.2 | Framework principal, CDI, execução e build |
| Camel Quarkus | BOM 3.39.2 | Integração do Apache Camel com Quarkus |
| Jakarta REST + Jackson | quarkus-rest-jackson | Endpoint HTTP e serialização JSON |
| Camel Core | camel-quarkus-core | Motor e APIs fundamentais de rotas |
| Camel Direct | camel-quarkus-direct | Chamada síncrona da rota direct:getProduct |
| Camel Bean | camel-quarkus-bean | Invocação do ProductService pela rota |
| Lombok | 1.18.42 | Construtores, getters, setters e métodos utilitários |
| JUnit 5 + Quarkus Test | quarkus-junit | Estrutura de testes automatizados |
| REST Assured | versão gerenciada pelo BOM | Testes HTTP |
| Maven Wrapper | incluído | Dependências, testes e empacotamento |
| Docker / UBI | Dockerfiles incluídos | Imagens JVM e nativas |

As versões das extensões Quarkus e Camel Quarkus são centralizadas nos BOMs declarados no pom.xml.

## Arquitetura e fluxo

~~~text
Cliente HTTP
    | GET /products
    v
ProductController
    | ProducerTemplate.requestBody(...)
    v
direct:getProduct
    v
ProductRouter
    | bean(ProductService.class, "getProduct")
    v
ProductService
    v
Resposta JSON (Product + Client)
~~~

### Principais componentes

- **ProductController:** recurso Jakarta REST publicado em /products. Monta os dados de demonstração, chama a rota Camel de forma síncrona e cria a resposta HTTP.
- **ProductRouter:** RouteBuilder responsável pela rota getProduct. Recebe mensagens em direct:getProduct, converte o corpo para DadosRouterDto, registra logs e chama o serviço.
- **ProductService:** bean CDI com escopo de aplicação usado pelo componente Camel Bean. Neste exemplo, retorna o DTO recebido sem alterações.
- **DadosRouterDto:** record que transporta um Product e um Client.
- **Product e Client:** modelos de domínio com código repetitivo gerado pelo Lombok.

## Estrutura do projeto

~~~text
.
├── pom.xml
├── mvnw / mvnw.cmd
└── src
    ├── main
    │   ├── docker
    │   │   ├── Dockerfile.jvm
    │   │   ├── Dockerfile.legacy-jar
    │   │   ├── Dockerfile.native
    │   │   └── Dockerfile.native-micro
    │   ├── java/br/com/camel
    │   │   ├── controllers/ProductController.java
    │   │   ├── model/{Client,DadosRouterDto,Product}.java
    │   │   ├── routes/ProductRouter.java
    │   │   └── services/ProductService.java
    │   └── resources/application.properties
    └── test/java/br/com/camel
        ├── GreetingResourceTest.java
        └── GreetingResourceIT.java
~~~

## Pré-requisitos

- JDK 21;
- Git;
- Docker ou Podman, somente para imagens de contêiner;
- GraalVM, somente para gerar um executável nativo localmente.

O Maven não precisa ser instalado globalmente, pois o repositório inclui o Maven Wrapper.

## Como executar

~~~bash
git clone https://github.com/Karmaicom/quarkus-camel.git
cd quarkus-camel
./mvnw quarkus:dev
~~~

No Windows:

~~~powershell
.\mvnw.cmd quarkus:dev
~~~

O modo de desenvolvimento oferece live reload. A Dev UI fica disponível em http://localhost:8080/q/dev/.

## Como usar a API

~~~bash
curl http://localhost:8080/products
~~~

Resposta esperada:

~~~json
{
  "product": {
    "name": "Notebook Lenovo",
    "quantity": 10
  },
  "client": {
    "nome": "Fulano",
    "email": "fulano@gmail.com"
  }
}
~~~

| Método | Caminho | Descrição | Resposta |
| --- | --- | --- | --- |
| GET | /products | Processa os dados de exemplo pela rota Camel | 200 OK com JSON |

## Testes

~~~bash
./mvnw test
~~~

No Windows, use mvnw.cmd test.

> **Observação:** GreetingResourceTest e GreetingResourceIT vieram do projeto inicial do Quarkus e ainda exercitam o endpoint /hello. Como o código atual expõe /products, eles precisam ser atualizados para representar corretamente a aplicação.

## Build e execução em JVM

~~~bash
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
~~~

Para produzir um único über-jar:

~~~bash
./mvnw package -Dquarkus.package.jar.type=uber-jar
java -jar target/*-runner.jar
~~~

## Executável nativo

Com GraalVM:

~~~bash
./mvnw package -Dnative
~~~

Sem GraalVM local, use o build em contêiner:

~~~bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
./target/quarkus-camel-1.0.0-SNAPSHOT-runner
~~~

## Docker

### Imagem JVM

~~~bash
./mvnw package
docker build -f src/main/docker/Dockerfile.jvm -t quarkus-camel:jvm .
docker run --rm -p 8080:8080 quarkus-camel:jvm
~~~

### Imagem legacy JAR

~~~bash
./mvnw package -Dquarkus.package.jar.type=legacy-jar
docker build -f src/main/docker/Dockerfile.legacy-jar -t quarkus-camel:legacy-jar .
docker run --rm -p 8080:8080 quarkus-camel:legacy-jar
~~~

### Imagem nativa

~~~bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
docker build -f src/main/docker/Dockerfile.native -t quarkus-camel:native .
docker run --rm -p 8080:8080 quarkus-camel:native
~~~

O Dockerfile.native-micro usa a imagem micro do Quarkus para uma imagem nativa mais enxuta.

## Configuração

O arquivo src/main/resources/application.properties está vazio. A aplicação usa os padrões do Quarkus, incluindo a porta HTTP 8080. Exemplo para alterar a porta:

~~~properties
quarkus.http.port=8081
~~~

## Possíveis evoluções

- substituir os objetos fixos do controller por dados recebidos na requisição ou de uma fonte externa;
- utilizar na resposta HTTP o resultado retornado pela rota Camel;
- atualizar os testes para cobrir /products e seu JSON;
- adicionar validação e tratamento centralizado de erros;
- integrar bancos de dados, mensageria ou APIs externas com componentes Camel;
- incluir OpenAPI/Swagger, observabilidade e health checks.

## Referências

- [Quarkus](https://quarkus.io/guides/)
- [Camel Quarkus](https://camel.apache.org/camel-quarkus/latest/)
- [Camel Core](https://camel.apache.org/camel-quarkus/latest/reference/extensions/core.html)
- [Camel Direct](https://camel.apache.org/camel-quarkus/latest/reference/extensions/direct.html)
- [Camel Bean](https://camel.apache.org/camel-quarkus/latest/reference/extensions/bean.html)

## Licença

Este repositório não possui um arquivo de licença no momento. Antes de reutilizar ou distribuir o código, adicione uma licença compatível com o objetivo do projeto.
