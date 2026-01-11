
# Development Quality - Order Flow

A comprehensive DDD, CQRS, event-driven application for customer's order and stock management.

This application stack is designed for integrating into an ecosystem needing generic customer and order management.

## Installation

### Dev environment

This software project is designed to be run in a Docker environment. It uses devcontainer specification to provide a consistent development environment.

To run this project in a dev environment, you need to have Docker and Docker Compose installed on your machine.

1. Clone the repository
2. Open the project in Visual Studio Code / IntelliJ IDEA or any other IDE that supports devcontainer.
3. Open the project in the devcontainer.

Supported IDEs :
- Visual Studio Code
- IntelliJ IDEA

#### Pre-requisites

- Docker
- Docker Compose
- Visual Studio Code / IntelliJ IDEA
- Java 17+ (included)
- Gradle 8.14+ (included)
- Node.js 24+ (included)
- pnpm 10.15+ (included)

#### Mono-repository

This project is a mono-repository. It contains multiple packages that are designed to work together.

Applications :
- `apps/store-back` : Store Back-For-Front exposing backend features
- `apps/store-front` : Store Front-End exposing GUI features
- `apps/product-registry` : the product registry microservices, managing products
  - `product.registry` : the command microservice, handling product registry business logic
  - `product.registry.read` : the read microservice, handling product registry read queries
- `apps/product-catalog` : the product catalog microservices, managing product catalog
  - `product.catalog` : the command microservice, handling product catalog business logic
  - `product.catalog.read` : the read microservice, handling product catalog read queries

Libraries :
- `libs/cqrs-support` : a library exposing utilities for event, projection, typings and persistence
- `libs/kernel` : a library exposing the core business logic and domain models
- `libs/sql`: a package containing Liquibase changelog
- `libs/bom-platform` : a library factorizing the Bill of Materials for the platform
- `libs/contracts/*` : modules exposing the contracts for the different services, holding transitional data structures

## Features

This application allows to manage products, catalogs as an admin and consult catalogs as a customer.
This application does not cover customer management, order processing nor delivery processing.
This application does not cover stock management.

### API

The main API is exposed through various Back-For-Front services. Internal services communicate with each other using events and commands passed to RESTful endpoints.

### Product registry

The product registry is a list of products that can be integrated into a catalog. Each product has a name, a description

### Product catalog

The product catalog is a list of products that can be ordered by customers. Each entry includes a price.

## Documentation

[Go to index](./doc/index.md)

## Installation

### Development

Run to install the Node dependencies:

```bash
pnpm install
```

Run to build the java project:

```bash
gradle build
```

Run quarkus application modules:

```bash
gradle <module_name>:quarkusDev
# Module names typically follows the pattern: apps:<app_name>
```

Run angular application:

```bash
pnpm run --filter apps-store-front start
```

### Production deployment

TODO

## Collaboration

### Modèle de contrôle de version

- Stratégie: Trunk-Based Development avec branches de courte durée.
- Les équipes travaillent sur des branches `feature/`, `fix/`, `chore/`, puis fusionnent régulièrement vers `main`.
- Une branche `release/x.y.z` est créée lors de la préparation d'une version pour stabiliser et corriger sans bloquer `main`.

### Stratégie de branchement

- Noms de branches:
  - `feature/<scope>-<desc-courte>` (ex: `feature/product-registry-create`)
  - `fix/<scope>-<desc-courte>`
  - `chore/<scope>-<desc-courte>`
  - `release/0.1.0`
- Fusion: privilégier le squash merge avec une description propre.
- Revues: au moins 1 reviewer de l'autre équipe pour les changements cross-modules.

### Responsabilités des équipes

- Équipe Command/Write:
  - Services de commande (apps/product-registry-domain-service, apps/store-back)
  - Contrats write (libs/contracts/*)
  - Domaine et validations (libs/kernel)
  - SQL de base (libs/sql)
- Équipe Read/UI:
  - Services de lecture (apps/product-registry-read-service)
  - Front-end (apps/store-front)
  - Projections/outbox, DTO read (libs/contracts read)
- Partagé: `libs/bom-platform`, `libs/cqrs-support` et conventions.

### Règles de contribution

- Commits: Conventional Commits (`feat:`, `fix:`, `chore:`, `docs:`...).
- Tests & Lint: exécuter `gradle build` et `pnpm mega-linter-runner -p $WORKSPACE_ROOT` avant MR.
- Documentation: mettre à jour README/CONTRIBUTING et docs liées à la modification.
- Voir les détails dans [CONTRIBUTING.md](CONTRIBUTING.md).

### Versioning & Release

- Versionnement sémantique par module (SemVer). Incrémentez uniquement les modules impactés.
- Baseline: `0.1.0` (précoce, non production).
- Tag: `v0.1.0` au niveau du monorepo avec notes de version.
- Commandes utiles:

```bash
# Créer la branche de release
git checkout -b release/0.1.0

# Mettre à jour les versions des modules impactés (exemples)
sed -i 's/0.1.0-SNAPSHOT/0.1.0/' apps/product-registry-domain-service/build.gradle
sed -i 's/0.1.0-SNAPSHOT/0.1.0/' apps/product-registry-read-service/build.gradle
sed -i 's/0.1.0-SNAPSHOT/0.1.0/' libs/kernel/build.gradle

# Build & tests
gradle build

# Tag + notes
git tag -a v0.1.0 -m "Release 0.1.0: baseline du monorepo"

# Pousser
git push origin release/0.1.0 --tags
```

## Journal de bord

Le journal est initialisé dans [doc/journal/README.md](doc/journal/README.md) et regroupe:
- Décisions architecturales et techniques importantes
- Réponses aux questions du TP pour l'évaluation
    
## Authors

- Thibaud FAURIE :
  - [@thibaud.faurie (Private GitLab)](https://gitlab.cloud0.openrichmedia.org/thibaud.faurie)
  - [@thibaud-faurie (LinkedIn)](https://www.linkedin.com/in/thibaud-faurie/)

