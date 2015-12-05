# Projet de Programmation Orientée Agents
#### M2 SID MIAGE - Promotion 2015/2016

L’objectif du projet est de concevoir le système de vente en ligne d’un revendeur multimédia.
Ce projet met en œuvre la plateforme Jade distribué au travers d’un mini réseau de 4
machines.

##### Exemple de commande pour lancer plusieurs agents en même temps :
``
Main Class: jade Boot
Arguments: -gui -agents BDD:vendeur.BDDAgent;Darty:fournisseur.FournisseurAgent(1);ERep:ereputation.EReputationAgent"
``

##### Lancement des agents sur un container distant :

Argument : -container -host <IP-HOTE> -port 1099 -agents F1:fournisseur.FournisseurAgent(1);F2:fournisseur.FournisseurAgent(2)
