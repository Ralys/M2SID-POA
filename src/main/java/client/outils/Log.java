/*
 * Auteur : Aymeric ZANIRATO
 * Email: aymeric@zanirato.fr
 */
package client.outils;

import client.UI.FXMLController;
import javafx.application.Platform;

/**
 * Classe permettant d'afficher des logs dans l'interface graphique
 * @author Aymeric
 */
public class Log {
    
    /**
     * Permet d'ajouter un log pour un achat de produit dans l'interface graphique
     * @param message Le message du log (correspond à l'achat effectué)
     */
    public static void achat(final String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
               FXMLController.listLog.add("Achat : " + message);
            }
        });
        
    }

    /**
     * Permet d'ajouter un log précisant l'envoi d'un message à un agent destinataire
     * @param AgentDestinataire L'agent à qui envoyer le message
     * @param message Le message envoyé à l'agent destinataire
     */
    public static void envoi(final String AgentDestinataire ,final String message) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLController.listLog.add("Envoi à "+AgentDestinataire+" : "+ message);
            }
        });

    }
    
    /**
     * Méthode permettant d'afficher un message reçu de la part d'un autre agent
     * @param AgentExpediteur L'agent nous ayant envoyé le message
     * @param message Le message à afficher.
     */
    public static void reception(final String AgentExpediteur , final String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLController.listLog.add("Reception de "+AgentExpediteur+" : "+ message);
            }
        });

    }
    
    /**
     * Méthode permettant d'ajouter un log pour annuler une commande
     * @param message Le message précisant l'annulation de la commande
     */
    public static void commandeAnnulee(final String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLController.listLog.add("Commande annulée : " + message);
            }
        });

    }
    
    /**
     * Méthode qui permet d'ajouter un log avec le message que l'on veut
     * @param message Le message que l'on souhaite afficher
     */
    public static void affiche(final String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLController.listLog.add(message);
            }
        });

    }
    
    /**
     * Ajoute un log précisant que les recherches ont été arrêtés
     */
    public static void arretRecherche() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLController.listLog.add("Arrêt des recherches");
            }
        });

    }

}
