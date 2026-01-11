package org.ormi.priv.tfa.orderflow.productregistry.read.infra.api;

import org.jboss.resteasy.reactive.RestResponse;
import org.ormi.priv.tfa.orderflow.contracts.productregistry.v1.read.PaginatedProductListDto;
import org.ormi.priv.tfa.orderflow.contracts.productregistry.v1.read.ProductViewDto;
import org.ormi.priv.tfa.orderflow.kernel.product.ProductId;
import org.ormi.priv.tfa.orderflow.kernel.product.ProductIdMapper;
import org.ormi.priv.tfa.orderflow.productregistry.read.application.ProductQuery;
import org.ormi.priv.tfa.orderflow.productregistry.read.application.ReadProductService;
import org.ormi.priv.tfa.orderflow.productregistry.read.application.ReadProductService.SearchPaginatedResult;
import org.ormi.priv.tfa.orderflow.productregistry.read.infra.web.dto.ProductSummaryDtoMapper;
import org.ormi.priv.tfa.orderflow.productregistry.read.infra.web.dto.ProductViewDtoMapper;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * TODO: Complete Javadoc
 */

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductRegistryQueryResource {

    private final ReadProductService readProductService;
    private final ProductViewDtoMapper productViewDtoMapper;
    private final ProductSummaryDtoMapper productSummaryDtoMapper;
    private final ProductIdMapper productIdMapper;

    @Inject
    public ProductRegistryQueryResource(
            ReadProductService readProductService,
            ProductViewDtoMapper productViewDtoMapper,
            ProductSummaryDtoMapper productSummaryDtoMapper,
            ProductIdMapper productIdMapper) {
        this.readProductService = readProductService;
        this.productViewDtoMapper = productViewDtoMapper;
        this.productSummaryDtoMapper = productSummaryDtoMapper;
        this.productIdMapper = productIdMapper;
    }

    @GET
    public RestResponse<PaginatedProductListDto> searchProducts(
            @QueryParam("sku") @DefaultValue("") String sku,
            @QueryParam("page") @Min(0) int page,
            @QueryParam("size") @Min(1) @Max(100) int size) {
        final ProductQuery.ListProductBySkuIdPatternQuery query = 
                new ProductQuery.ListProductBySkuIdPatternQuery(sku, page, size);
        final SearchPaginatedResult result = readProductService.searchProducts(query);
        final PaginatedProductListDto list = new PaginatedProductListDto(
                result.page().stream()
                        .map(productSummaryDtoMapper::toDto)
                        .toList(),
                page, size, result.total());
        return RestResponse.ok(list);
    }

    @GET
    @Path("/{id}")
    public RestResponse<ProductViewDto> getProductById(
            @PathParam("id") @NotBlank String id) {
        try {
            final ProductId productId = productIdMapper.map(java.util.UUID.fromString(id));
            final ProductQuery.GetProductByIdQuery query = new ProductQuery.GetProductByIdQuery(productId);
            final var product = readProductService.findById(query);
            if (product.isEmpty()) {
                return RestResponse.status(RestResponse.Status.NOT_FOUND);
            }
            return RestResponse.ok(productViewDtoMapper.toDto(product.get()));
        } catch (IllegalArgumentException e) {
            // UUID invalide
            return RestResponse.status(RestResponse.Status.BAD_REQUEST);
        }
    }
}