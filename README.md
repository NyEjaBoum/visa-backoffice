# visa-backoffice
PROJET VISA (MR NAINA)

## Présentation du projet
Ce projet gère le back-office de la gestion des demandes de visa. Il est basé sur Spring Boot (Java) pour le back-end et le framework Ewan pour le front-office. La sécurité repose sur JWT : les rôles et permissions sont embarqués dans le token pour limiter les accès à la base de données. Les tables de référence (types de visa, statuts) sont mises en session côté client pour optimiser les performances.

## Architecture Technique
- **Back-office** : Spring Boot (Java)
- **Sécurité** : Authentification JWT (rôles et permissions dans le token)
- **Optimisation** : Mise en session des tables de référence

## Gestion des migrations de base de données

### 1. Initialisation de l’environnement
Avant toute migration, il faut initialiser l’environnement PostgreSQL :
- Créer la base de données, les schémas (dev, staging, prod) et les rôles d’accès avec le script `sql/00_init_env.sql`.
- Ce script doit être exécuté manuellement par un administrateur :
  ```sql
  \i sql/00_init_env.sql
  ```

### 2. Migrations applicatives (structure et données)
- Les migrations sont gérées automatiquement par **Liquibase**.
- Les scripts SQL de migration sont dans `src/main/resources/db/changelog/`.
- L’ordre d’exécution est défini dans `db.changelog-master.xml`.
- À chaque démarrage de l’application, Liquibase applique les nouveaux scripts non encore exécutés.
- **Important** :
  - Ne jamais modifier un script déjà appliqué. Pour toute évolution, ajouter un nouveau fichier SQL et l’inclure dans le changelog.
  - Les scripts de reset ou de suppression de tables ne doivent pas être dans le changelog.

### 3. Réinitialisation de la base (développement uniquement)
- Utiliser le script `reset.sql` pour vider toutes les tables métier sans les supprimer.
- Exécution :
  ```sql
  \i reset.sql
  ```
- Cela permet de repartir d’une base propre sans casser l’historique Liquibase.

### 4. Bonnes pratiques
- Les scripts d’administration (création de base, rôles, schémas) sont séparés des migrations applicatives.
- Les migrations Liquibase ne concernent que la structure métier (tables, données, évolutions).
- Toujours ajouter un nouveau fichier pour chaque modification de la structure ou des données.

---
Pour toute question sur la gestion des migrations ou l’architecture, voir la documentation ou contacter l’équipe technique.
