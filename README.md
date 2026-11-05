# ODM : cycle d'inférence RETE et boucle de règles

Étude pratique avec **IBM Operational Decision Manager (ODM) 9.6** : comprendre comment le moteur de règles (algorithme RetePlus) gère la *Working Memory* et l'*Agenda*, pourquoi deux règles correctes isolément peuvent provoquer une boucle infinie, et comment la stabiliser.

## Problématique

Dans un moteur d'inférence, une règle qui modifie un fait peut rendre une autre règle applicable. Si la seconde annule l'effet de la première, le système oscille sans fin. C'est un risque réel dans l'automatisation de la réponse à incident (SOAR, isolement automatique de machines, blocage d'IP).

## Contenu

| Dossier | Contenu |
|---|---|
| `docs/01-outil-ibm-odm.md` | Présentation de l'outil IBM ODM |
| `docs/02-contexte-metier-cybersecurite.md` | Explication métier et cas de production en cybersécurité |
| `docs/03-mise-en-oeuvre.md` | Mise en oeuvre pas à pas (avec emplacements de captures) |
| `docs/04-analyse-et-corrections.md` | Observation, diagnostic et stratégies de stabilisation |
| `src/` | Modèle Java (XOM), règles et fonction d'initialisation |
| `assets/` | Captures d'écran |

## Environnement

- IBM ODM Rule Designer 9.6.0 (version on-premise)
- JDK 21 (Eclipse Temurin)
- Windows 11

## Résultat en un coup d'oeil

Avec RetePlus, les règles `StartHeating` et `StopHeating` s'activent alternativement sans condition d'arrêt : 19 -> 21 -> 19 -> ... Les corrections (limite de cycles, séparation mesure/action, hystérésis) rétablissent un comportement stable.
