/*
 * Auteur : Aymeric ZANIRATO
 * Email: aymeric@zanirato.fr
 */
package client;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import javafx.application.Platform;

/**
 *
 * @author Aymeric
 */
public class Jade {
    
    
    public static void envoyerMessage(Agent client,int typeMessage, AID receiver, String message) {
        ACLMessage msg = new ACLMessage(typeMessage);
        msg.setContent(message);
        msg.addReceiver(receiver);
        client.send(msg);
    }
    
    
    public static void loggerAchat(final String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
               FXMLController.listLog.add("Achat : " + message);
            }
        });
        
    }

    public static void loggerEnvoi(final String AgentDestinataire ,final String message) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLController.listLog.add("Envoi à "+AgentDestinataire+" : "+ message);
            }
        });

    }

    public static void loggerReception(final String AgentExpediteur , final String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLController.listLog.add("Reception de "+AgentExpediteur+" : "+ message);
            }
        });

    }
    
    public static void loggerCommandeAnnulee(final String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLController.listLog.add("Commande annulée : " + message);
            }
        });

    }
    
    
    public static void loggerArretRecherche() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLController.listLog.add("Arrêt des recherches");
            }
        });

    }

    
    
}
