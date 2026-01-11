package org.ormi.priv.tfa.orderflow.productregistry;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * Point d'entrée Quarkus du service de registre de produits.
 *
 * Cette classe démarre l'application en déléguant à une implémentation
 * {@link QuarkusApplication} qui attend la terminaison du processus.
 *
 * @since 1.0
 */

@QuarkusMain
public class Main {

    public static void main(String... args) {
        Quarkus.run(
            ProductRegistryDomainApplication.class,
            (exitCode, exception) -> {},
            args);
    }

    /**
     * Application Quarkus qui démarre le service et attend son arrêt.
     */
    public static class ProductRegistryDomainApplication implements QuarkusApplication {

        /**
         * Bloque le thread principal jusqu'à la terminaison de l'application.
         *
         * @param args arguments de ligne de commande
         * @return code de sortie
         * @throws Exception en cas d'erreur au démarrage
         */
        @Override
        public int run(String... args) throws Exception {
            Quarkus.waitForExit();
            return 0;
        }
    }
}
