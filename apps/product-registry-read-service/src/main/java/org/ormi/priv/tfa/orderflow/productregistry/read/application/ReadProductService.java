package org.ormi.priv.tfa.orderflow.productregistry.read.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.ormi.priv.tfa.orderflow.contracts.productregistry.v1.read.ProductStreamElementDto;
import org.ormi.priv.tfa.orderflow.kernel.product.ProductId;
import org.ormi.priv.tfa.orderflow.kernel.product.persistence.ProductViewRepository;
import org.ormi.priv.tfa.orderflow.kernel.product.views.ProductSummary;
import org.ormi.priv.tfa.orderflow.kernel.product.views.ProductView;

import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * TODO: Complete Javadoc
 */

@ApplicationScoped
public class ReadProductService {

    private final ProductViewRepository repository;
    private final ProductEventBroadcaster productEventBroadcaster;

    @Inject
    public ReadProductService(
        ProductViewRepository repository,
        ProductEventBroadcaster productEventBroadcaster) {
        this.repository = repository;
        this.productEventBroadcaster = productEventBroadcaster;
    }

    public Optional<ProductView> findById(ProductQuery.GetProductByIdQuery query) {
        return repository.findById(query.productId());
    }

    public SearchPaginatedResult searchProducts(ProductQuery.ListProductBySkuIdPatternQuery query) {
        final List<ProductView> views = repository.searchPaginatedViewsOrderBySkuId(
                query.skuIdPattern(), query.page(), query.size());
        final List<ProductSummary> summaries = views.stream()
                .map(view -> ProductSummary.Builder()
                        .id(view.getId())
                        .skuId(view.getSkuId())
                        .name(view.getName())
                        .status(view.getStatus())
                        .catalogs(view.getCatalogs().size())
                        .build())
                .toList();
        return new SearchPaginatedResult(summaries, 
                repository.countPaginatedViewsBySkuIdPattern(query.skuIdPattern()));
    }

    public Multi<ProductStreamElementDto> streamProductEvents(ProductId productId) {
        return productEventBroadcaster.streamByProductId(productId.value().toString());
    }

    public Multi<ProductStreamElementDto> streamProductListEvents(ProductQuery.ListProductBySkuIdPatternQuery query) {
        final List<ProductView> products = repository.searchPaginatedViewsOrderBySkuId(
                query.skuIdPattern(), query.page(), query.size());
        final List<UUID> productIds = products.stream()
                .map(p -> p.getId().value())
                .toList();
        return productEventBroadcaster.streamByProductIds(productIds);
    }

    public record SearchPaginatedResult(List<ProductSummary> page, long total) {
    }
}
