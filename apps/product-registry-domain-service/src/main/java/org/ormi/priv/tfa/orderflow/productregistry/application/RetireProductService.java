package org.ormi.priv.tfa.orderflow.productregistry.application;

import org.ormi.priv.tfa.orderflow.cqrs.EventEnvelope;
import org.ormi.priv.tfa.orderflow.cqrs.infra.jpa.EventLogEntity;
import org.ormi.priv.tfa.orderflow.cqrs.infra.jpa.OutboxEntity;
import org.ormi.priv.tfa.orderflow.cqrs.infra.persistence.EventLogRepository;
import org.ormi.priv.tfa.orderflow.cqrs.infra.persistence.OutboxRepository;
import org.ormi.priv.tfa.orderflow.kernel.Product;
import org.ormi.priv.tfa.orderflow.kernel.product.ProductEventV1.ProductRetired;
import org.ormi.priv.tfa.orderflow.kernel.product.persistence.ProductRepository;
import org.ormi.priv.tfa.orderflow.productregistry.application.ProductCommand.RetireProductCommand;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service d'application responsable de la mise hors service (retrait) d'un produit.
 *
 * Ce service charge le produit, déclenche l'événement de domaine
 * {@code ProductRetired}, persiste l'état, journalise l'événement dans le
 * journal des événements, puis publie le message via l'outbox.
 *
 * Il s'inscrit dans un schéma CQRS avec journalisation d'événements et
 * publication outbox afin d'assurer fiabilité et traçabilité des opérations.
 *
 * @since 1.0
 */
@ApplicationScoped
public class RetireProductService {

    @Inject
    ProductRepository repository;
    @Inject
    EventLogRepository eventLog;
    @Inject
    OutboxRepository outbox;

    /**
     * Retire un produit du registre et publie l'événement associé.
     *
     * @param cmd commande contenant l'identifiant du produit à retirer
     * @throws IllegalArgumentException si le produit n'existe pas
     */
    @Transactional
    public void handle(RetireProductCommand cmd) throws IllegalArgumentException {
        Product product = repository.findById(cmd.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        EventEnvelope<ProductRetired> evt = product.retire();
        repository.save(product);
        // Append event to the log
        final EventLogEntity persistedEvent = eventLog.append(evt);
        // Publish outbox
        outbox.publish(OutboxEntity.Builder()
                .sourceEvent(persistedEvent)
                .build());
    }
}
