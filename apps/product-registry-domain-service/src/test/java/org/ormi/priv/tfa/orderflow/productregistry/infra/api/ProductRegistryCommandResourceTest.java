package org.ormi.priv.tfa.orderflow.productregistry.infra.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.ormi.priv.tfa.orderflow.contracts.productregistry.v1.write.RegisterProductCommandDto;
import org.ormi.priv.tfa.orderflow.contracts.productregistry.v1.write.UpdateProductNameParamsDto;
import org.ormi.priv.tfa.orderflow.kernel.product.ProductId;
import org.ormi.priv.tfa.orderflow.productregistry.application.RegisterProductService;
import org.ormi.priv.tfa.orderflow.productregistry.application.RetireProductService;
import org.ormi.priv.tfa.orderflow.productregistry.application.UpdateProductService;
import org.ormi.priv.tfa.orderflow.productregistry.application.ProductCommand.UpdateProductNameCommand;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.validation.ConstraintViolationException;

@QuarkusTest
public class ProductRegistryCommandResourceTest {

    @InjectMock
    RegisterProductService registerProductService;
    @InjectMock
    UpdateProductService updateProductService;
    @InjectMock
    RetireProductService retireProductService;

    @Test
    void post_register_validProduct_returns201WithLocation() {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(registerProductService.handle(any())).thenReturn(new ProductId(id));

        given()
            .contentType(ContentType.JSON)
            .body(new RegisterProductCommandDto("Name", "Desc", "ABC-12345"))
        .when()
            .post("/api/products")
        .then()
            .statusCode(201)
            .header("Location", endsWith("/api/products/products/" + id));
    }

    @Test
    void post_register_invalidSku_returns400() {
        // MapStruct mapper will throw IllegalArgumentException due to invalid SKU format
        given()
            .contentType(ContentType.JSON)
            .body(new RegisterProductCommandDto("Name", "Desc", "BADSKU"))
        .when()
            .post("/api/products")
        .then()
            .statusCode(400);
    }

    @Test
    void post_register_missingField_returns400() {
        // Missing skuId (null) leads to NullPointerException in mapper path
        given()
            .contentType(ContentType.JSON)
            .body(new RegisterProductCommandDto("Name", "Desc", null))
        .when()
            .post("/api/products")
        .then()
            .statusCode(400);
    }

    @Test
    void post_register_nullBody_returns400() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .post("/api/products")
        .then()
            .statusCode(400);
    }

    @Test
    void patch_updateName_valid_returns204() {
        // No exception thrown by service
        doAnswer(invocation -> null).when(updateProductService).handle(Mockito.any(UpdateProductNameCommand.class));

        UUID id = UUID.fromString("22222222-2222-2222-2222-222222222222");
        given()
            .contentType(ContentType.JSON)
            .body(new UpdateProductNameParamsDto("New Name"))
        .when()
            .patch("/api/products/" + id + "/name")
        .then()
            .statusCode(204);
    }

    @Test
    void patch_updateName_invalid_returns400() {
        // Service will throw validation exception for blank name
        doAnswer(invocation -> {
            UpdateProductNameCommand cmd = invocation.getArgument(0);
            String newName = cmd.newName();
            if (newName == null || newName.isBlank()) {
                throw new ConstraintViolationException(null);
            }
            return null;
        }).when(updateProductService).handle(Mockito.any(UpdateProductNameCommand.class));

        UUID id = UUID.fromString("33333333-3333-3333-3333-333333333333");
        given()
            .contentType(ContentType.JSON)
            .body(new UpdateProductNameParamsDto(""))
        .when()
            .patch("/api/products/" + id + "/name")
        .then()
            .statusCode(400);
    }

    @Test
    void patch_updateName_nullBody_returns400() {
        UUID id = UUID.fromString("44444444-4444-4444-4444-444444444444");
        given()
            .contentType(ContentType.JSON)
        .when()
            .patch("/api/products/" + id + "/name")
        .then()
            .statusCode(400);
    }

    @Test
    void delete_retire_valid_returns204() {
        doAnswer(invocation -> null).when(retireProductService).handle(any());
        UUID id = UUID.fromString("55555555-5555-5555-5555-555555555555");
        given()
        .when()
            .delete("/api/products/" + id)
        .then()
            .statusCode(204);
    }

    @Test
    void delete_retire_nonExisting_returns400() {
        doThrow(new IllegalArgumentException("Product not found")).when(retireProductService).handle(any());
        UUID id = UUID.fromString("66666666-6666-6666-6666-666666666666");
        given()
        .when()
            .delete("/api/products/" + id)
        .then()
            .statusCode(400);
    }
}
