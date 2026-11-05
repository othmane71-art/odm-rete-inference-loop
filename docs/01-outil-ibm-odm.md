# IBM Operational Decision Manager (ODM)

## Rôle
ODM est une suite de gestion de décisions qui sépare la logique de décision du code applicatif : les règles sont écrites dans un langage proche du langage naturel, puis déployées sur un moteur de règles. Une règle peut changer sans modifier ni redéployer l'application.

## Composants
| Composant | Rôle |
|---|---|
| Rule Designer | Environnement de développement basé sur Eclipse : modèles, règles, ruleflows, tests, debug |
| Decision Center | Outil collaboratif pour les analystes métier : édition, tests, simulation, gouvernance |
| Decision Server | Environnement d'exécution des règles |

## Modèles
- **XOM** (Execution Object Model) : classes Java manipulées à l'exécution.
- **BOM** (Business Object Model) : vocabulaire métier construit au-dessus du XOM ; il donne aux règles leur forme lisible (par exemple `the temperature of a room`).
- **Ruleflow** : ordre d'exécution des tâches de règles.
- **Decision Operation** : point d'entrée du service, avec sa signature.

## Modes d'exécution du moteur
| Mode | Comportement |
|---|---|
| RetePlus | Working Memory + Agenda. Chaque insertion, modification ou retrait d'un objet réévalue les règles concernées. Adapté au chaînage de règles et aux applications avec état |
| Fastpath | Même mécanisme d'agenda, mais pas de réévaluation après modification |
| Sequential | Exécution immédiate des règles dans l'ordre, sans agenda |

Dans l'Agenda, les instances de règles sont ordonnées par refraction, priorité et récence.

## Version utilisée
ODM 9.6 fournit un Rule Designer prêt à l'emploi, distribué sur GitHub (DecisionsDev/ruledesigner), avec JDK 21.

## Sources
- Documentation IBM ODM : https://www.ibm.com/docs/en/odm
- Algorithme RetePlus : https://www.ibm.com/docs/en/odm/9.0.0?topic=mode-reteplus-algorithm
- Concepts du moteur de règles : https://www.ibm.com/docs/en/odm/9.0.0?topic=mode-general-rule-engine-concepts
- IBM Redpaper sur ODM : https://www.redbooks.ibm.com/redpapers/pdfs/redp5333.pdf
