# Exercice 1
Tâche 1 : Ségrégation des responsabilités (Version Mise à jour)

    1. Principaux domaines métiers de l'application Order flow

    L'application est structurée autour d'un domaine principal et de plusieurs sous-domaines :

        Domaines principaux (Cœur métier) : Panier d'achat (Shopping Cart) et Traitement des commandes (Order Processing).

        Domaines de support : Registre des produits, Catalogue, Gestion des stocks et Gestion des clients.

        Domaines génériques : Notification, Facturation et Sourcing d'événements.

    2. Conception des services pour implémenter les domaines

    Les services utilisent une architecture SOA / Microservices avec les spécificités suivantes :

        Pattern CQRS : Séparation des flux d'écriture (Commands/Aggregates) et de lecture (Queries/Views) pour optimiser les performances.

        Architecture en oignon : Isolation de la logique métier (au centre) des détails techniques comme la persistance ou les interfaces web.

        Modularité (Apps vs Libs) : Les apps exposent les interfaces (IHM/API) tandis que les libs encapsulent la logique réutilisable et les outils d'infrastructure.

    3. Responsabilités des modules

        apps/store-back : Orchestration des commandes et logique serveur du tunnel d'achat.

        apps/store-front : Interface utilisateur (IHM) pour les clients.

        libs/kernel : Objets de base (Value Objects comme la Monnaie) et exceptions transverses.

        apps/product-registry-domain-service : Gestion des agrégats et des commandes (écriture) pour le référentiel produit.

        apps/product-registry-read-service : Gestion des projections et des requêtes (lecture) pour le catalogue.

        libs/bom-platform : Logique métier liée à la nomenclature et composition des produits (BOM).

        libs/cqrs-support : Support technique pour l'implémentation du pattern CQRS (bus de commandes/requêtes).

        libs/sql : Couche d'infrastructure pour la persistance des données en base relationnelle.


Tâche 2 : Analyse des concepts et implémentation

    1. Concepts principaux et mécanismes techniques

        Stockage des données : L'application utilise une persistance SQL (via libs/sql). Les données sont séparées entre les tables d'agrégats (écriture) et les tables de projections (lecture).

        Gestion des transactions : La cohérence transactionnelle est forte à l'intérieur d'un seul microservice, mais elle est éventuelle entre les services (système distribué).

        Événements métiers : Ils sont enregistrés dans un journal d'événements (Event Log). Contrairement à l'Event Sourcing, ils servent ici à la traçabilité et à la mise à jour des vues (projections).

        Gestion des erreurs :

            Techniques : Gérées par des mécanismes d'infrastructure (exceptions standards, logs).

            Métier : Les agrégats valident les invariants et rejettent les commandes invalides avant tout changement d'état.

        Échanges entre services : Basés sur une architecture EDA (Event-Driven Architecture) via des messages ou des appels API asynchrones.

    2. Implémentation dans les modules

    L'application utilise une approche hybride entre code spécifique et bibliothèques génériques :

        Sémantique des namespaces : Le code est organisé par domaines (ex: product-registry, store). On retrouve souvent la structure domain, application et infrastructure (Architecture en Oignon).

        Solutions génériques : Utilisation de Gradle pour la gestion des dépendances et de bibliothèques internes (libs/) pour uniformiser le comportement du CQRS ou de l'accès SQL sur tous les services.

        Parties mobiles : Les services "Read" et "Domain" sont les parties les plus actives, communiquant via le bus d'événements défini dans les bibliothèques de support.

    3. Rôle des bibliothèques (libs/)

        libs/cqrs-support : C'est le moteur technique du pattern CQRS. Elle fournit les abstractions pour définir des Commands, des Queries, et les Bus nécessaires pour router ces messages vers les bons handlers. Elle assure la séparation physique du code de traitement.

        libs/bom-platform : Elle centralise la logique métier de la Bill of Materials (nomenclature produit). C'est une bibliothèque "métier" plutôt que technique, permettant aux différents services de manipuler des structures de produits complexes de manière uniforme.

    4. Fiabilité des états internes (CQRS & Kernel)

    La fiabilité est assurée par plusieurs mécanismes :

        Encapsulation dans le Kernel : L'utilisation de Value Objects (comme la Monnaie dans libs/kernel) garantit que les données de base sont toujours valides et immuables.

        Validation par les Agrégats : Dans le modèle CQRS, seul l'agrégat peut modifier son état. Il vérifie toutes les règles métier avant d'accepter une commande.

        Journalisation : Le journal d'événements permet de garder une trace immuable de chaque changement, garantissant une auditabilité totale en cas de corruption d'une vue de lecture.


# Execice 2

Tâche 5 : Stratégie de tests et Architecture

1. Différence entre tests unitaires et tests d'intégration

    Tests Unitaires : Ils vérifient le fonctionnement d'une petite unité de code isolée (généralement une classe ou une méthode). Les dépendances externes (base de données, services tiers) sont remplacées par des doublures (mocks). Ils sont très rapides à exécuter.

    Tests d'Intégration : Ils vérifient que plusieurs modules ou composants fonctionnent correctement ensemble. Ils testent souvent les interactions avec des éléments réels (base de données, serveurs web, APIs) pour s'assurer que la configuration et la communication sont correctes.

2. Pertinence d'une couverture de code à 100%

Non, il n'est pas pertinent de viser systématiquement 100% de couverture.

    Rapport coût/bénéfice : Tester des éléments triviaux (getters/setters, code généré) apporte peu de valeur mais coûte cher en maintenance.

    Fausse sécurité : Une couverture à 100% ne garantit pas l'absence de bugs, elle indique seulement que chaque ligne a été exécutée, pas que tous les scénarios métier ont été vérifiés.

    Focus : Il est préférable de couvrir à 100% les domaines critiques (logique métier complexe) et d'être plus souple sur le code d'infrastructure.

3. Avantages de l'architecture en oignon pour les tests

L'architecture en oignon place le Domaine au centre, indépendant de toute technologie.

    Testabilité du métier : Comme le domaine ne dépend pas de la base de données ou du framework, on peut écrire des tests unitaires ultra-rapides pour la logique complexe sans démarrer de serveur.

    Isolation : On peut tester les couches externes (infra) indépendamment de la logique métier en utilisant des interfaces.

    Exemple (Tâche 3) : On observe que les tests du domaine (les agrégats) sont simples à écrire car ils ne manipulent que des objets métier purs, tandis que les tests de persistance (JPA) sont isolés dans leurs propres modules.

4. Nomenclature des packages

Cette structure permet de respecter le principe de responsabilité unique (SRP) :

    model (ou Domain) : Contient le cœur métier (agrégats, entités, value objects). Aucune dépendance technique ici.

    application : Orchestre les cas d'utilisation (services applicatifs). Elle fait le pont entre le monde extérieur et le domaine.

    infra (Infrastructure) : Contient l'implémentation des détails techniques.

    jpa / sql : Sous-package d'infra dédié à la persistance des données et aux dépôts (repositories).

    web : Sous-package d'infra gérant les points d'entrée API (Controllers REST).

    client : Contient généralement les clients pour appeler des services externes (APIs tierces).


# Exercice 4
Tâche 1 : L'interface Projector et la Gestion d'Erreurs

1. Rôle de l'interface Projector

Le Projector a pour rôle de transformer un événement métier (provenant du journal d'événements) en une vue de lecture (état stocké en base de données). C'est le pont qui permet de synchroniser le modèle d'écriture avec le modèle de lecture dans le pattern CQRS.

2. Rôle du type 

Le type générique ```<S>``` représente le State (l'état) ou la Source de la projection. Il s'agit généralement de l'entité de vue (Read Model) qui est mise à jour par le projecteur après avoir traité l'événement.

3. Javadoc de l'interface Projector

```Java
@param <E> Le type de l'événement à projeter.
 @param <S> Le type de l'état (vue de lecture) résultant ou impacté par la projection.
 
public interface Projector<E, S> { ... }
```

4. Intérêt de l'interface vs Classe concrète

L'interface permet le découplage. On peut ainsi :

    Interchanger les stratégies de projection sans modifier le code qui appelle le projecteur.

    Faciliter les tests unitaires via des mocks.

    Appliquer plusieurs projections différentes à partir d'un même événement.

5. Rôle de ProjectionResult et concept de Monade

ProjectionResult agit comme une Monade. Une monade est une structure qui enveloppe une valeur et permet de chaîner des opérations tout en gérant les effets de bord (comme les erreurs) de manière fluide.

    Avantages par rapport à la gestion traditionnelle (try-catch) :

        Déclaratif : On décrit le flux de succès/échec sans interrompre brutalement l'exécution par des exceptions.

        Immuabilité : Le résultat est encapsulé, forçant le développeur à traiter le cas d'erreur explicitement.

        Lisibilité : Évite le "Pyramid of Doom" des blocs try-catch imbriqués.

Tâche 2 : Outboxing (Fiabilité des événements)

1. Rôle de OutboxRepository

Il sert à persister les événements dans une table temporaire ("Outbox") au sein de la même transaction que la modification métier. Cela garantit que si la commande est enregistrée, l'événement l'est aussi.

2. Garantie de livraison dans un système distribué

L'Outbox Pattern résout le problème du "Two-Phase Commit". Au lieu d'essayer d'écrire en base ET d'envoyer un message sur un bus (ce qui peut échouer), on écrit tout en base. Un processus séparé (Relay) lit ensuite la table Outbox pour envoyer les messages. Si l'envoi échoue, il peut être rejoué.

3. Fonctionnement concret et Flux

    Flux : Commande → Modification Base de données + Insertion Outbox (1 Transaction) → Service de Relay → Message Broker.

    Diagramme de séquence simplifié :

        ApplicationService démarre une transaction.

        DomainService met à jour l'agrégat.

        OutboxRepository insère l'événement dans la table OUTBOX.

        La transaction est commitée.

        Un OutboxProcessor (asynchrone) récupère l'entrée, publie l'événement, et marque l'entrée comme "traitée".

4. Gestion des erreurs (Schéma Liquibase)

Dans les fichiers XML Liquibase, on observe généralement des colonnes comme processed, retry_count ou last_error.

    Si un événement ne peut pas être livré, le retry_count augmente.

    Cela permet d'isoler les événements problématiques sans bloquer l'ensemble du système.

Tâche 3 : Journal d'événements (Event Log)
1. Rôle du journal

Il sert de source de vérité historique. Il enregistre de manière immuable tout ce qui s'est passé dans le système, garantissant la traçabilité et l'auditabilité (crucial pour le commerce).
2. Pourquoi seulement la méthode append ?

    Immuabilité : On ne modifie jamais le passé. On ne peut qu'ajouter (append) de nouveaux faits.

    Pas de suppression : Supprimer un événement casserait l'historique et la capacité d'audit.

    Pas de récupération (ici) : Dans cette implémentation, la récupération est déléguée aux vues de lecture (CQRS). Le journal est une "écriture seule" pour maximiser les performances.

3. Implications et autres usages

    Implications : Le système est conçu pour la traçabilité plus que pour la reconstruction d'état complète (puisque ce n'est pas de l'Event Sourcing).

    Autres usages : Audit légal, analyse de données (Business Intelligence), relecture pour corriger des erreurs de projection passées.

Tâche 4 : Limites de CQRS

1. Principales limites

    Complexité : Doublement du code (modèles de lecture vs écriture).

    Cohérence éventuelle : L'utilisateur peut ne pas voir sa modification immédiatement après avoir cliqué sur "valider".

2. Limites déjà compensées

L'utilisation de l'Outbox Pattern compense la perte de fiabilité des messages. L'application assure que l'événement finira forcément par être projeté, réduisant le risque de désynchronisation totale.

3. Nouvelles limites introduites

    Latence technique : Le passage par une table Outbox et un processus de relay ajoute un délai supplémentaire avant que la vue ne soit à jour.

    Volume de données : Le journal d'événements et la table Outbox peuvent croître très rapidement.

4. Cas d'une projection multiple

Si un événement déclenche plusieurs actions (ex: mise à jour stock + notification + facture), une erreur dans une seule de ces actions peut rendre le système incohérent.

    Risque : Si la projection "Stock" réussit mais "Facture" échoue, la vue globale est partiellement fausse.

5. Bonus : Solutions

    Idempotence : S'assurer que rejouer un événement plusieurs fois n'a pas d'effet négatif.

    Sagas / Transactions compensatoires : Pour gérer les échecs complexes dans les flux distribués.

    Monitoring des délais de projection : Pour alerter si la "cohérence éventuelle" devient trop longue.