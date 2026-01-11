package org.ormi.priv.tfa.orderflow.productregistry.infra.jpa;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.ormi.priv.tfa.orderflow.kernel.Product;
import org.ormi.priv.tfa.orderflow.kernel.product.ProductIdMapper;
import org.ormi.priv.tfa.orderflow.kernel.product.SkuIdMapper;

/**
 * Mapper MapStruct pour convertir entre le modèle de domaine {@link org.ormi.priv.tfa.orderflow.kernel.Product}
 * et l'entité JPA {@link ProductEntity}.
 *
 * Ce composant est généré avec {@code componentModel = "cdi"} et utilise
 * {@link ProductIdMapper} et {@link SkuIdMapper} pour la conversion des identifiants.
 * Les champs non mappés sont ignorés afin d'éviter des erreurs de compilation.
 *
 * @since 1.0
 */

@Mapper(
    componentModel = "cdi",
    builder = @Builder(disableBuilder = false),
    uses = { ProductIdMapper.class, SkuIdMapper.class },
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ProductJpaMapper {

    /**
     * Convertit une entité JPA en objet de domaine.
     *
     * @param entity entité persistée
     * @return instance de domaine {@link Product}
     */
    public abstract Product toDomain(ProductEntity entity);

    /**
     * Met à jour l'entité JPA cible à partir de l'état courant de l'objet de
     * domaine.
     *
     * @param product objet de domaine source
     * @param entity entité cible à mettre à jour
     */
    public abstract void updateEntity(Product product, @MappingTarget ProductEntity entity);

    /**
     * Convertit un objet de domaine en entité JPA prête à être persistée.
     *
     * @param product objet de domaine
     * @return entité JPA {@link ProductEntity}
     */
    public abstract ProductEntity toEntity(Product product);
}
