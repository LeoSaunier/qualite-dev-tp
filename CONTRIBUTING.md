# CONTRIBUTING

Guide de collaboration pour le monorepo Order Flow.

## Modèle de développement

- **Trunk-Based Development**: branches courtes fusionnées fréquemment vers `main`.
- **Branches de release**: `release/x.y.z` pour stabiliser une version sans bloquer `main`.

## Stratégie de branchement

- **Noms de branches**
  - `feature/<scope>-<desc>` (ex: `feature/product-registry-create`)
  - `fix/<scope>-<desc>`
  - `chore/<scope>-<desc>`
  - `release/0.1.0`
- **Fusion**: squash merge par défaut, rebase si nécessaire.
- **Revues**: au moins 1 reviewer de l'autre équipe quand le changement impacte plusieurs modules.

## Commits

- **Conventional Commits**: `feat:`, `fix:`, `chore:`, `docs:`, `refactor:`, `test:`.
- Messages concis + corps décrivant le contexte et l'impact.

## Tests & Qualité

- **Build**: `gradle build` doit être vert.
- **Tests**: ajouter/mettre à jour les tests unitaires et d'intégration.
- **Linter**: `pnpm mega-linter-runner -p $WORKSPACE_ROOT`.

## Versioning & Release

- **SemVer** par module. Incrémentez seulement les modules modifiés.
- **Baseline**: `0.1.0` (non prod).
- **Tags**: `vX.Y.Z` au niveau du monorepo.
- **Notes de version**: documenter les modules impactés, changements, migrations.

### Processus type

1. Créer la branche: `git checkout -b feature/<scope>-<desc>`.
2. Développer + tests.
3. Mettre à jour versions des modules impactés (`*-SNAPSHOT` -> stable si release).
4. Mettre à jour docs (README, CONTRIB, `doc/journal`).
5. MR avec description propre, squash merge.
6. `git checkout -b release/X.Y.Z` (si release), `gradle build`.
7. Tag: `git tag -a vX.Y.Z -m "Release X.Y.Z"` puis `git push --tags`.

## Responsabilités des équipes

- **Équipe Command/Write**
  - `apps/product-registry-domain-service`, `apps/store-back`
  - `libs/kernel`, `libs/contracts/*` (write)
  - `libs/sql` (changelogs Liquibase)
- **Équipe Read/UI**
  - `apps/product-registry-read-service`, `apps/store-front`
  - Projections/outbox, DTO read, `libs/contracts/*` (read)
- **Partagé**
  - `libs/bom-platform`, `libs/cqrs-support`

## Communication

- **Outils**: Slack/Teams pour synchronisation, Trello/Jira pour suivi, Live Share pour pair programming.
- **Rituels**: daily court, revue croisée des PR, planning hebdo.
- **UML**: diagrammes pour préciser modèles et flux (placer sous `doc/`).

## Dépendances cross-modules

- Changements de contrats (DTO) doivent être annoncés et versionnés.
- Modifications du domaine (`libs/kernel`) exigent coordination + tests d’intégration.
- Outbox/Projection: toute évolution nécessite vérification des consommateurs.

## Journal de bord

- Voir `doc/journal/README.md`. Ajoutez une entrée par décision (date, auteur, contexte, décision, impact).
