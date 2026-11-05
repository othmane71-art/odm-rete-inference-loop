# Mise en oeuvre

Les captures sont à ajouter dans `assets/` avec les noms indiqués.

## 1. Workspace
Rule Designer : File > Switch Workspace > `C:\Users\<user>\workspace-odm\ODM-Thermostat`.

![Switch Workspace](../assets/01-switch-workspace.png)

## 2. Projet Java (XOM)
Perspective Java > Package Explorer : projet `ThermostatXOM`, JavaSE-21, sans `module-info.java`.

![Projet Java](../assets/02-java-project.png)

## 3. Classe Room
Package `thermostat`, classe `Room` avec getters et setters (voir `src/ThermostatXOM/thermostat/Room.java`).

![Classe Room](../assets/03-room-class.png)

## 4. Rule Project et lien XOM
Perspective Rule > Rule Explorer : projet `ThermostatRules`, puis Properties > Java Execution Object Model > cocher `ThermostatXOM`.

![Rule Project](../assets/04-rule-project.png)
![Lien XOM](../assets/04-xom-link.png)

## 5. BOM Entry
Rule Explorer > `bom` > New > BOM Entry > `thermostat.Room`.

![BOM Entry](../assets/05-bom-entry.png)

## 6. Vocabulaire
Contrôle de `model.bom` et `model_en_US.voc`.

![Vocabulaire](../assets/06-voc.png)

## 7. Update object state
Cocher *Update object state* sur `temperature`, `heatingOn` et `numberOfCycles`. Cette option informe le moteur qu'un attribut modifié doit réévaluer les règles.

![Update object state](../assets/07-update-object-state.png)

## 8. Règles
Package `thermostat` : `StartHeating` et `StopHeating` (voir `src/rules/`).

![Package et règles](../assets/08-rules.png)

## 9. Ruleflow
Ruleflow `main` : Start > `heatingTask` > End. Algorithme **RetePlus**, package `thermostat` sélectionné.

![Ruleflow](../assets/09-ruleflow.png)
![RetePlus](../assets/09-reteplus.png)

## 10. Fonction d'initialisation
Package `functions`, fonction `InsertObjects`, appelée dans les *initial actions* du nœud Start.

![InsertObjects](../assets/10-insert-objects.png)

## 11. Exécution
Decision Operation `thermostat` dans `deployment`, puis Run Configuration. La console alterne `Heating Meeting room` et `Stopping heating in Meeting room` sans fin.

![Decision Operation](../assets/11-decision-operation.png)
![Console](../assets/11-console-loop.png)
