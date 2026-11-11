# bot-discord

 ## Roadmap du projet :

- Définir le workflow de travail (workflow Git vincent avec une branche de travail : "work" et des pull requests)
- Définir l'architecture du projet (organisation des classes)
- Définir les cas d'usages de l'application
- Modéliser le projet avec un Diagramme UML
- Implémenter les entités Java et les mapping en Jpa
- Implémenter les requetes REST
- Implémenter les tests
- Dockeriser l'application (multi-stage)


## Workflow 
- Le workflow utiliser pour ce projet est le workflow gitflow (par Vincent Driessen).
- Il contient la branche principale (main). 
- Les features sont codées sur des branches séparées puis après un pull request sont mergé vers le main.


## Commandes : 

### Compilation et tests

#### Tout compiler, sans les tests
```shell script
./mvnw clean install -DskipTests
```

#### Tout compiler et lancer tous les tests (Docker requis pour bot-api)
```shell script
./mvnw clean verify
```

#### Tester uniquement le bot (sans Docker)
```shell script
./mvnw verify -pl services/discord-bot -am
```

#### Tester uniquement l'API
```shell script
./mvnw verify -pl services/bot-api -am
```

### Démarrage

#### Démarrage de l'API uniquement (port 8080)
```shell script
./mvnw install -DskipTests
```

```shell script
./mvnw quarkus:dev -pl services/bot-api
```

#### Démarrage du bot discord uniquement (port 8081) :

```shell script
./mvnw install -DskipTests
```

```shell script 
./mvnw quarkus:dev -pl services/discord-bot
```

#### Démarrage complet (deux terminaux)
Lancer d'abord l'API, puis le bot :
```shell script
# Terminal 1
./mvnw quarkus:dev -pl services/bot-api
# Terminal 2
./mvnw quarkus:dev -pl services/discord-bot
```

Variables d'environnement utiles :

| Variable | Service | Rôle | Défaut |
| --- | --- | --- | --- |
| `DISCORD_TOKEN` | discord-bot | Jeton du bot | lu dans `.env` |
| `BOT_API_URL` | discord-bot | URL de bot-api | `http://localhost:8080` |
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | bot-api | Base PostgreSQL (profil prod) | `jdbc:postgresql://localhost:5432/bot`, `bot`, `bot` |

### Commandes Discord

| Commande | Effet |
| --- | --- |
| `!ping` | Répond `pong!` |
| `!roles` | Affiche les rôles stockés dans bot-api |

### Tester l'API à la main

```shell script
# Lister les rôles
curl http://localhost:8080/api/v1/roles
 
# Récupérer un rôle par son nom
curl http://localhost:8080/api/v1/roles/Admin
 
# Créer un rôle (201, ou 409 s'il existe déjà)
curl -X POST http://localhost:8080/api/v1/roles -H 'Content-Type: application/json' -d '{"name":"Admin"}'
 
# Supprimer un rôle (204, ou 404 s'il n'existe pas)
curl -X DELETE http://localhost:8080/api/v1/roles/Admin
```

Mêmes routes pour `/api/v1/channels` et `/api/v1/guild`.

| URL | Contenu |
| --- | --- |
| http://localhost:8080/q/swagger-ui | Documentation interactive de l'API |
| http://localhost:8080/q/openapi | Contrat OpenAPI (YAML) |
| http://localhost:8080/q/health | État de bot-api |
| http://localhost:8081/q/health | État de discord-bot |
| http://localhost:8080/q/dev-ui | Dev UI Quarkus (mode dev uniquement) |

### Génération de code

#### Lister tous les fichiers générés par les processeurs d'annotations
```shell script
find services/bot-api/target/generated-sources -name "*.java" | sort
```

#### Lister le client REST généré dans le bot (depuis le contrat OpenAPI)
```shell script
find services/discord-bot/target/generated-sources/open-api -name "*.java" | sort
```

#### Après la modification d'un modèle annoté (Role, Channel, Guild...)
Régénère le code de l'API, le contrat `contracts/openapi.yaml` et le client du bot :
```shell script
./mvnw clean install -DskipTests
```

Puis commiter le nouveau contrat :
```shell script
git add contracts/openapi.yaml
```



