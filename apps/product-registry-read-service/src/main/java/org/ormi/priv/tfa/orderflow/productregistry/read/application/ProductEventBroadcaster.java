package org.ormi.priv.tfa.orderflow.productregistry.read.application;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ormi.priv.tfa.orderflow.contracts.productregistry.v1.read.ProductStreamElementDto;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.MultiEmitter;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * TODO: Complete Javadoc
 */

@ApplicationScoped
public class ProductEventBroadcaster {

    private final CopyOnWriteArrayList<MultiEmitter<? super ProductStreamElementDto>> emitters = new CopyOnWriteArrayList<>();

    public void broadcast(ProductStreamElementDto element) {
        emitters.forEach(emitter -> emitter.emit(element));
    }

    public Multi<ProductStreamElementDto> stream() {
        return Multi.createFrom().emitter(emitter -> {
            emitters.add(emitter);
            // TODO: log a debug, "New emitter added"

            // TODO: Hey! remove emitters, my RAM is melting! (and log for debugging)
            // TODO: TODO
            emitter.onTermination(() -> emitters.remove(emitter));
        });
    }

    public Multi<ProductStreamElementDto> streamByProductId(String productId) {
        return stream()
                .select().where(e -> e.productId().equals(productId));
    }

    public Multi<ProductStreamElementDto> streamByProductIds(List<UUID> productIds) {
        return stream()
                .select().where(e -> productIds.contains(UUID.fromString(e.productId())));
    }
}
