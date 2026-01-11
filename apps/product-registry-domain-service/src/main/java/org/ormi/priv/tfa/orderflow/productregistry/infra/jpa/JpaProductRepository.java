package org.ormi.priv.tfa.orderflow.productregistry.infra.jpa;

import java.util.Optional;
import java.util.UUID;

import org.ormi.priv.tfa.orderflow.kernel.Product;
import org.ormi.priv.tfa.orderflow.kernel.product.ProductId;
import org.ormi.priv.tfa.orderflow.kernel.product.ProductIdMapper;
import org.ormi.priv.tfa.orderflow.kernel.product.SkuId;
import org.ormi.priv.tfa.orderflow.kernel.product.SkuIdMapper;
import org.ormi.priv.tfa.orderflow.kernel.product.persistence.ProductRepository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Implémentation JPA de {@link ProductRepository} basée sur Panache.
 *
 * Ce dépôt gère la persistance des produits, la recherche par identifiant,
 * et la vérification d'existence par {@link SkuId}. Les conversions entre
 * modèle de domaine et entités JPA sont déléguées à {@link ProductJpaMapper}
 * et aux mappers d'identifiants ({@link ProductIdMapper}, {@link SkuIdMapper}).
 *
 * @since 1.0
 */

@ApplicationScoped
public class JpaProductRepository implements PanacheRepositoryBase<ProductEntity, UUID>, ProductRepository {

    ProductJpaMapper mapper;
    ProductIdMapper productIdMapper;    
    SkuIdMapper skuIdMapper;

    @Inject
    public JpaProductRepository(ProductJpaMapper mapper, ProductIdMapper productIdMapper, SkuIdMapper skuIdMapper) {
        this.mapper = mapper;
        this.productIdMapper = productIdMapper;
        this.skuIdMapper = skuIdMapper;
    }

    /**
     * Persiste le produit : met à jour l'entité existante ou insère une nouvelle
     * entité si elle n'existe pas.
     *
     * @param product produit à persister
     */
    @Override
    @Transactional
    public void save(Product product) {
        findByIdOptional(productIdMapper.map(product.getId()))
                .ifPresentOrElse(e -> {
                    mapper.updateEntity(product, e);
                }, () -> {
                    ProductEntity newEntity = mapper.toEntity(product);
                    getEntityManager().merge(newEntity);
                });
    }

    /**
     * Recherche un produit par identifiant.
     *
     * @param id identifiant produit
     * @return produit optionnel
     */
    @Override
    public Optional<Product> findById(ProductId id) {
        return findByIdOptional(productIdMapper.map(id))
                .map(mapper::toDomain);
    }

    /**
     * Vérifie l'existence d'un produit à partir de son {@link SkuId}.
     *
     * @param skuId identifiant SKU
     * @return {@code true} si un produit existe avec ce SKU, sinon {@code false}
     */
    @Override
    public boolean existsBySkuId(SkuId skuId) {
        return count("skuId", skuIdMapper.map(skuId)) > 0;
    }
}
