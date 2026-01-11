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
import org.ormi.priv.tfa.orderflow.kernel.product.views.ProductSummary;
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

    private ProductSummary sampleSummary(UUID id, String sku, String name) {
        return ProductSummary.Builder()
                .id(new ProductId(id))
                .skuId(new SkuId(sku))
                .name(name)
                .status(ProductLifecycle.ACTIVE)
                .catalogs(0)
                .build();
    }

    @Test
    void get_search_withMatch_returns200WithList() {
        var s = sampleSummary(UUID.fromString("77777777-7777-7777-7777-777777777777"), "ABC-12345", "Prod1");
        when(readProductService.searchProducts(any())).thenReturn(new ReadProductService.SearchPaginatedResult(List.of(s), 1));

        given()
        .when()
            .get("/api/products?sku=ABC&page=0&size=10")
        .then()
            .statusCode(200)
            .body("products.size()", equalTo(1));
    }

    @Test
    void get_search_withoutMatch_returns200EmptyList() {
        when(readProductService.searchProducts(any())).thenReturn(new ReadProductService.SearchPaginatedResult(List.of(), 0));

        given()
        .when()
            .get("/api/products?sku=XYZ&page=0&size=10")
        .then()
            .statusCode(200)
            .body("products.size()", equalTo(0));
    }

    @Test
    void get_search_noFilter_returns200List() {
        var s1 = sampleSummary(UUID.fromString("88888888-8888-8888-8888-888888888888"), "DEF-54321", "Prod2");
        when(readProductService.searchProducts(any())).thenReturn(new ReadProductService.SearchPaginatedResult(List.of(s1), 1));

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
        when(readProductService.findById(any())).thenReturn(Optional.of(v));

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
        when(readProductService.findById(any())).thenReturn(Optional.empty());

        given()
        .when()
            .get("/api/products/" + id)
        .then()
            .statusCode(404);
    }

    @Test
    void get_search_withNegativePage_returns400() {
        given()
        .when()
            .get("/api/products?sku=ABC&page=-1&size=10")
        .then()
            .statusCode(400);
    }

    @Test
    void get_search_withZeroSize_returns400() {
        given()
        .when()
            .get("/api/products?sku=ABC&page=0&size=0")
        .then()
            .statusCode(400);
    }

    @Test
    void get_search_withSizeTooLarge_returns400() {
        given()
        .when()
            .get("/api/products?sku=ABC&page=0&size=101")
        .then()
            .statusCode(400);
    }

    @Test
    void get_byId_withInvalidUUID_returns400() {
        given()
        .when()
            .get("/api/products/not-a-valid-uuid")
        .then()
            .statusCode(400);
    }

    @Test
    void get_byId_withMalformedUUID_returns400() {
        given()
        .when()
            .get("/api/products/12345")
        .then()
            .statusCode(400);
    }
}
