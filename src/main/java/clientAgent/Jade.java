/*
 * Auteur : Aymeric ZANIRATO
 * Email: aymeric@zanirato.fr
 */
package clientAgent;

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
    
    
    /**
     * Méthode qui retourne tous les agents du service en paramètre
     *
     * @param client
     * @param service
     * @return
     */
    public static AID[] searchDF(Agent client,String service) {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(service);
        dfd.addServices(sd);

        SearchConstraints ALL = new SearchConstraints();
        ALL.setMaxResults(new Long(-1));

        try {
            DFAgentDescription[] result = DFService.search(client, dfd, ALL);
            AID[] agents = new AID[result.length];
            for (int i = 0; i < result.length; i++) {
                agents[i] = result[i].getName();
            }
            return agents;

        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        return null;
    }
    
    
    
    public static void deRegisterService(Agent client){
        try {
            DFService.deregister(client);
        } catch (Exception e) {
        }
    }
    
    public static void envoyerMessage(Agent client, AID receiver, String message) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
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

    public static void loggerEnvoi(final String message) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLController.listLog.add("Envoi : " + message);
            }
        });

    }

    public static void loggerReception(final String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLController.listLog.add("Reception : " + message);
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

    
    
}