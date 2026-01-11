package org.ormi.priv.tfa.orderflow.kernel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.ormi.priv.tfa.orderflow.kernel.product.ProductLifecycle;
import org.ormi.priv.tfa.orderflow.kernel.product.SkuId;

import jakarta.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    // Méthode statique create : tester la création d'un produit valide
    @Test
    void create_validProduct_returnsProduct_active_noException() {
        assertDoesNotThrow(() -> {
            Product p = Product.create("Produit A", "Description", new SkuId("ABC-12345"));
            assertNotNull(p);
            assertNotNull(p.getId());
            assertEquals(ProductLifecycle.ACTIVE, p.getStatus());
            assertEquals(1L, p.getVersion());
            assertEquals("Produit A", p.getName());
            assertEquals("Description", p.getDescription());
            assertEquals("ABC-12345", p.getSkuId().value());
        });
    }

    // Méthode statique create : tester la création d'un produit invalide
    @Test
    void create_invalidProduct_nullName_throwsConstraintViolation() {
        Executable call = () -> Product.create(null, "Description", new SkuId("ABC-12345"));
        assertThrows(ConstraintViolationException.class, call);
    }

    @Test
    void create_invalidProduct_blankName_throwsConstraintViolation() {
        Executable call = () -> Product.create("", "Description", new SkuId("ABC-12345"));
        assertThrows(ConstraintViolationException.class, call);
    }

    @Test
    void create_invalidProduct_nullDescription_throwsConstraintViolation() {
        Executable call = () -> Product.create("Produit", null, new SkuId("ABC-12345"));
        assertThrows(ConstraintViolationException.class, call);
    }

    @Test
    void create_invalidProduct_nullSku_throwsConstraintViolation() {
        Executable call = () -> Product.create("Produit", "Description", null);
        assertThrows(ConstraintViolationException.class, call);
    }

    // Tester la mise à jour avec entrées invalides
    @Test
    void update_invalidInputs_onActiveProduct_throwConstraintViolation() {
        Product p = Product.create("Produit", "Description", new SkuId("ABC-12345"));

        assertAll(
            () -> assertThrows(ConstraintViolationException.class, () -> p.updateName(null)),
            () -> assertThrows(ConstraintViolationException.class, () -> p.updateName("")),
            () -> assertThrows(ConstraintViolationException.class, () -> p.updateDescription(null))
        );
    }

    // Mise à jour dans état valide (actif)
    @Test
    void update_valid_onActiveProduct_updatesAndNoException() {
        Product p = Product.create("Produit", "Description", new SkuId("ABC-12345"));

        assertDoesNotThrow(() -> p.updateName("Nouveau Nom"));
        assertEquals("Nouveau Nom", p.getName());
        assertEquals(2L, p.getVersion());

        assertDoesNotThrow(() -> p.updateDescription("Nouvelle Description"));
        assertEquals("Nouvelle Description", p.getDescription());
        assertEquals(3L, p.getVersion());
    }

    // Mise à jour dans état invalide (retiré)
    @Test
    void update_onRetiredProduct_throwsIllegalState() {
        Product p = Product.create("Produit", "Description", new SkuId("ABC-12345"));
        assertDoesNotThrow(p::retire);
        assertEquals(ProductLifecycle.RETIRED, p.getStatus());

        assertAll(
            () -> assertThrows(IllegalStateException.class, () -> p.updateName("Nom")),
            () -> assertThrows(IllegalStateException.class, () -> p.updateDescription("Desc"))
        );
    }

    // Suppression (retrait) dans état valide (actif)
    @Test
    void retire_onActiveProduct_setsStatusRetired_noException() {
        Product p = Product.create("Produit", "Description", new SkuId("ABC-12345"));
        assertDoesNotThrow(p::retire);
        assertEquals(ProductLifecycle.RETIRED, p.getStatus());
        assertEquals(2L, p.getVersion());
    }

    // Suppression (retrait) dans état invalide (retiré)
    @Test
    void retire_onRetiredProduct_throwsIllegalState() {
        Product p = Product.create("Produit", "Description", new SkuId("ABC-12345"));
        assertDoesNotThrow(p::retire);
        assertEquals(ProductLifecycle.RETIRED, p.getStatus());

        assertThrows(IllegalStateException.class, p::retire);
    }
}
