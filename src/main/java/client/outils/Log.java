/*
 * Auteur : Aymeric ZANIRATO
 * Email: aymeric@zanirato.fr
 */
package client.outils;

import client.UI.FXMLController;
import javafx.application.Platform;

/**
 *
 * @author Aymeric
 */
public class Log {
    
    public static void achat(final String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
               FXMLController.listLog.add("Achat : " + message);
            }
        });
        
    }

    public static void envoi(final String AgentDestinataire ,final String message) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLController.listLog.add("Envoi à "+AgentDestinataire+" : "+ message);
            }
        });

    }

    public static void reception(final String AgentExpediteur , final String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLController.listLog.add("Reception de "+AgentExpediteur+" : "+ message);
            }
        });

    }
    
    public static void commandeAnnulee(final String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLController.listLog.add("Commande annulée : " + message);
            }
        });

    }
    
    
    public static void arretRecherche() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLController.listLog.add("Arrêt des recherches");
            }
        });

    }

    
    
}
