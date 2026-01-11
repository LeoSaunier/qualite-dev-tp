package org.ormi.priv.tfa.orderflow.productregistry.read.infra.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.ormi.priv.tfa.orderflow.kernel.product.ProductId;
import org.ormi.priv.tfa.orderflow.kernel.product.ProductLifecycle;
import org.ormi.priv.tfa.orderflow.kernel.product.SkuId;
import org.ormi.priv.tfa.orderflow.kernel.product.views.ProductView;
import org.ormi.priv.tfa.orderflow.productregistry.read.application.ReadProductService;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ProductRegistryQueryResourceTest {

    @InjectMock
    ReadProductService readProductService;

    private ProductView sampleView(UUID id, String sku, String name) {
        return ProductView.Builder()
                .id(new ProductId(id))
                .version(1L)
                .skuId(new SkuId(sku))
                .name(name)
                .description("Description")
                .status(ProductLifecycle.ACTIVE)
                .catalogs(List.of())
                .events(List.of())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void get_search_withMatch_returns200WithList() {
        var v = sampleView(UUID.fromString("77777777-7777-7777-7777-777777777777"), "ABC-12345", "Prod1");
        when(readProductService.searchProducts("ABC", 0, 10)).thenReturn(new ReadProductService.SearchPaginatedResult(List.of(v), 1));

        given()
        .when()
            .get("/api/products?sku=ABC&page=0&size=10")
        .then()
            .statusCode(200)
            .body("products.size()", equalTo(1));
    }

    @Test
    void get_search_withoutMatch_returns200EmptyList() {
        when(readProductService.searchProducts("XYZ", 0, 10)).thenReturn(new ReadProductService.SearchPaginatedResult(List.of(), 0));

        given()
        .when()
            .get("/api/products?sku=XYZ&page=0&size=10")
        .then()
            .statusCode(200)
            .body("products.size()", equalTo(0));
    }

    @Test
    void get_search_noFilter_returns200List() {
        var v1 = sampleView(UUID.fromString("88888888-8888-8888-8888-888888888888"), "DEF-54321", "Prod2");
        when(readProductService.searchProducts("", 0, 10)).thenReturn(new ReadProductService.SearchPaginatedResult(List.of(v1), 1));

        given()
        .when()
            .get("/api/products?sku=&page=0&size=10")
        .then()
            .statusCode(200)
            .body("products.size()", equalTo(1));
    }

    @Test
    void get_byId_existing_returns200WithProduct() {
        UUID id = UUID.fromString("99999999-9999-9999-9999-999999999999");
        var v = sampleView(id, "ABC-12345", "Prod1");
        when(readProductService.findById(new ProductId(id))).thenReturn(Optional.of(v));

        given()
        .when()
            .get("/api/products/" + id)
        .then()
            .statusCode(200)
            .body("id", equalTo(id.toString()));
    }

    @Test
    void get_byId_nonExisting_returns404() {
        UUID id = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        when(readProductService.findById(new ProductId(id))).thenReturn(Optional.empty());

        given()
        .when()
            .get("/api/products/" + id)
        .then()
            .statusCode(404);
    }
}
