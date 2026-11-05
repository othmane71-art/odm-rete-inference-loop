# Contexte métier : la boucle de règles en cybersécurité

## Le problème
Un moteur de règles ne s'arrête que lorsque l'Agenda est vide. Si l'action d'une règle modifie la donnée qui déclenche une autre règle, et que cette dernière rétablit la donnée initiale, aucune règle ne cesse d'être applicable.

## Scénario de production : isolement automatique de postes (EDR / SOAR)
Ce scénario est représentatif des automatisations de réponse à incident. Il s'agit d'un cas type, pas d'un incident d'une entreprise précise.

Un SOC automatise la réponse sur ses postes de travail. Chaque poste porte un score de risque, alimenté par les alertes du SIEM et de l'EDR.

| Règle | Condition | Action |
|---|---|---|
| IsolateHost | score de risque supérieur au seuil | isoler le poste du réseau, puis ramener le score à un niveau bas, car le poste n'est plus exposé |
| ReleaseHost | score de risque inférieur au seuil | rétablir la connexion du poste |

Chaque règle est défendable seule. Ensemble, `IsolateHost` abaisse le score pour refléter l'isolement, ce qui rend `ReleaseHost` applicable. Le poste est libéré, la télémétrie remonte une activité suspecte, le score remonte, et `IsolateHost` se redéclenche.

## Conséquences opérationnelles
- Un poste qui bascule entre isolé et connecté, ce qui coupe les utilisateurs et fragilise la confiance dans l'automatisation.
- Une charge inutile sur l'EDR, le pare-feu et la messagerie (tickets, notifications).
- Un journal d'audit saturé de doublons, difficile à exploiter en investigation.
- Un risque de sécurité : fenêtres où un poste compromis est brièvement reconnecté.
- Un moteur qui consomme des ressources sans terminer.

## Équivalence avec la maquette thermostat
| Maquette | Cybersécurité |
|---|---|
| Room | Poste (Host) |
| temperature | score de risque mesuré |
| targetTemperature | seuil de réaction |
| heatingOn | poste isolé (oui / non) |
| numberOfCycles | nombre de bascules |
| StartHeating | IsolateHost |
| StopHeating | ReleaseHost |

La maquette du thermostat reproduit le même mécanisme avec un exemple simple à observer.

## Principe de correction
1. Séparer la mesure (donnée issue de la télémétrie) et l'action (décision d'isoler). Une règle de commande ne doit pas réécrire la mesure.
2. Introduire une hystérésis : isoler au-dessus de 80, libérer en dessous de 40, seulement si le poste est isolé.
3. Garder un état de commande cohérent (isolé / non isolé) dans les conditions.
4. Limiter les cycles et alerter l'analyste lorsque la limite est atteinte.


## Architecture de sécurité et flux réseau

Le schéma Mermaid ci-dessous est nativement rendu par GitHub. Il montre l'intégration du moteur de règles au sein de l'infrastructure SOC d'entreprise, avec la segmentation réseau (VLAN Utilisateurs vs VLAN Quarantaine), les équipements périmétriques (Firewall / Switch Core) et la boucle d'oscillation.

```mermaid
flowchart TB
    subgraph LAN_CORP ["VLAN 10 - Postes Clients (Utilisateurs)"]
        HostA["Poste Client Sain A\n(Agent EDR)"]
        HostB["Poste Compromis B\n(Agent EDR actif)"]
    end

    subgraph ISOLATION_ZONE ["VLAN 99 - Quarantaine & Isolement"]
        HostIso["Poste B Confiné\n(Trafic LAN/Internet bloqué, flux EDR préservé)"]
    end

    subgraph PERIMETER ["Périmètre Réseau & Filtrage"]
        FW["Firewall Next-Gen / Routeur L3\n(Politiques de filtrage & ACL dynamiques)"]
    end

    subgraph SOC_ZONE ["VLAN 30 - Zone SOC & Détection"]
        EDR_Srv["Console EDR / XDR\n(Télémétrie endpoints)"]
        SIEM["SIEM & NDR\n(Collecte logs & détection comportementale)"]
        ODM_Engine["Moteur de Décision ODM\n(Algorithme RetePlus - Rules Engine)"]
    end

    %% Flux normaux de détection
    HostB -- "1. Télémétrie & Alertes suspectes" --> EDR_Srv
    HostB -. "Trafic réseau inspecté" .-> FW
    FW -- "Syslog / NetFlow" --> SIEM
    EDR_Srv -- "2. Calcul du RiskScore (ex: 85/100)" --> SIEM
    SIEM -- "3. Événement de risque (Host, Score)" --> ODM_Engine

    %% Boucle de décision et oscillation
    ODM_Engine -- "4. Décision 'IsolateHost' (Ordre d'isolement)" --> EDR_Srv
    EDR_Srv -- "5. Confinement réseau" --> FW
    FW -- "Bascule vers VLAN Quarantaine" --> HostIso

    %% L'effet pervers / boucle d'inférence
    ODM_Engine -. "6. Action perverse : reset RiskScore à 10\n(Simule artificiellement la fin du risque)" .-> ODM_Engine
    ODM_Engine == "7. RetePlus réactive 'ReleaseHost'\n(Car RiskScore < 30)" ==> EDR_Srv
    EDR_Srv == "8. Ordre de dé-confinement" ==> FW
    FW == "9. Reconnexion VLAN 10 (Poste compromis réactivé)" ==> HostB
    HostB == "10. Nouvelle détection EDR -> RiskScore remonte" ==> SIEM

    classDef danger fill:#fee,stroke:#c00,stroke-width:2px;
    classDef safe fill:#efe,stroke:#0a0,stroke-width:1px;
    classDef core fill:#eef,stroke:#00a,stroke-width:1px;
    class HostB,HostIso danger;
    class ODM_Engine,EDR_Srv,SIEM core;
```

## Diagramme de séquence opérationnel : l'oscillation en production

```mermaid
sequenceDiagram
    autonumber
    participant Host as Poste Client (Endpoint)
    participant EDR as Console EDR
    participant ODM as Moteur ODM (RetePlus)
    participant FW as Pare-feu (Segmentation)

    Note over Host,FW: Phase 1 : Détection d'une compromission
    Host->>EDR: Activité suspecte (exécution malveillante / balise C2)
    EDR->>ODM: Objet Host(name="PC-01", riskScore=85, isolated=false)
    Note over ODM: Agenda : activation de IsolateHost (85 > seuil 70)

    Note over Host,FW: Phase 2 : Confinement et effet pervers
    ODM->>EDR: Commande d'isolement (isolate = true)
    EDR->>FW: Bascule PC-01 en quarantaine (VLAN 99)
    Note over ODM: Fait modifié : set riskScore = 15<br/>(L'action simule la résolution)
    Note over ODM: Agenda RetePlus : IsolateHost consommé,<br/>ReleaseHost devient actif (15 < seuil 30) !

    Note over Host,FW: Phase 3 : Reconnexion et oscillation sans fin
    ODM->>EDR: Commande de levée d'isolement (isolate = false)
    EDR->>FW: Rétablissement PC-01 sur VLAN 10 (LAN Utilisateurs)
    Note over Host: Le malware s'exécute à nouveau sur le LAN
    Host->>EDR: Nouvelle alerte -> riskScore = 85
    Note over ODM: Cycle infini : retour à la Phase 1
```
