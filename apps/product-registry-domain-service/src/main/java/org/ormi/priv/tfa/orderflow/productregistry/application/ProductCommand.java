/**
 * Interface scellée définissant les commandes liées à la gestion des produits.
 * 
 * Cette interface représente le contrat pour toutes les opérations de commande
 * applicables aux produits du registre de produits. Utilise le pattern sealed
 * pour restreindre les implémentations aux enregistrements définis.
 * 
 * Les commandes disponibles sont :
 * <ul>
 *   <li>{@link RegisterProductCommand} - Enregistre un nouveau produit avec son nom, description et SKU</li>
 *   <li>{@link RetireProductCommand} - Retire un produit existant du registre</li>
 *   <li>{@link UpdateProductNameCommand} - Met à jour le nom d'un produit existant</li>
 *   <li>{@link UpdateProductDescriptionCommand} - Met à jour la description d'un produit existant</li>
 * </ul>
 * 
 * @author Product Registry Service
 * @since 1.0
 */
package org.ormi.priv.tfa.orderflow.productregistry.application;

import org.ormi.priv.tfa.orderflow.kernel.product.ProductId;
import org.ormi.priv.tfa.orderflow.kernel.product.SkuId;


public sealed interface ProductCommand {
    public record RegisterProductCommand(
            String name,
            String description,
            SkuId skuId) implements ProductCommand {
    }

    public record RetireProductCommand(ProductId productId) implements ProductCommand {
    }

    public record UpdateProductNameCommand(ProductId productId, String newName) implements ProductCommand {
    }

    public record UpdateProductDescriptionCommand(ProductId productId, String newDescription) implements ProductCommand {
    }
}
