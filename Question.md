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
