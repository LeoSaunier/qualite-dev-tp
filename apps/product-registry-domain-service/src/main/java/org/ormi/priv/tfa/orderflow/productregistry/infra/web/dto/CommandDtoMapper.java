package org.ormi.priv.tfa.orderflow.productregistry.infra.web.dto;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.ormi.priv.tfa.orderflow.productregistry.application.ProductCommand.RegisterProductCommand;
import org.ormi.priv.tfa.orderflow.contracts.productregistry.v1.write.RegisterProductCommandDto;
import org.ormi.priv.tfa.orderflow.kernel.product.SkuIdMapper;

/**
 * Mapper de DTO pour les commandes du registre de produits.
 *
 * Convertit entre le DTO d'entrée/sortie {@link RegisterProductCommandDto} et
 * la commande de domaine {@link RegisterProductCommand}. Généré via MapStruct
 * avec {@code componentModel = "cdi"} et utilisation de {@link SkuIdMapper}.
 *
 * @since 1.0
 */

@Mapper(
    componentModel = "cdi",
    builder = @Builder(disableBuilder = true),
    uses = { SkuIdMapper.class },
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CommandDtoMapper {
    /**
     * Convertit un DTO en commande de domaine.
     *
     * @param dto DTO de création de produit
     * @return commande de domaine
     */
    public RegisterProductCommand toCommand(RegisterProductCommandDto dto);

    /**
     * Convertit une commande de domaine en DTO.
     *
     * @param command commande de domaine
     * @return DTO correspondant
     */
    public RegisterProductCommandDto toDto(RegisterProductCommand command);
}
