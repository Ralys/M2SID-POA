/*
 * Auteur : Aymeric ZANIRATO
 * Email: aymeric@zanirato.fr
 */
package client.behaviours;

import client.Client;
import client.Jade;
import client.Produit;
import client.TypeAgentClient;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Aymeric
 */
public class Presse extends CyclicBehaviour {

    private final Client presse;
    private final double facteurPrixMax = 1.2;
    private final int facteurDateMax = 1;

    public Presse(Agent agent) {
        this.presse = (Client) agent;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            traiterMessage(msg);
            block();
        }
    }

    public void traiterMessage(ACLMessage message) {

        try {
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(message.getContent());

            if (object.containsKey("jePropose")) {
                JSONArray array = (JSONArray) object.get("jePropose");
                presse.ajouterProposition(array, message);
                presse.jeChoisis(presse.plusTot());
            }

            if (object.containsKey("commandeOk")) {
                JSONObject obj = (JSONObject) object.get("commandeOk");
                presse.afficherAchat(obj, message);
                // laisser avis erep
            }

            if (object.containsKey("commandePasOK")) {
                JSONObject obj = (JSONObject) object.get("commandePasOK");
                presse.afficherRaison(obj, message);

                Produit produitAnnule = new Produit(obj, message.getSender().getName());

                // retirer la proposition
                presse.retirerProposition(produitAnnule);

                // choisir la meilleur proposition suivante si il y en a
                if (presse.getLproposition().size() > 0) {
                    Date max = produitAnnule.getDateLivraison();
                    max.setDate(max.getDate() + facteurDateMax);

                    if (presse.offreInteressante(max)) {
                        presse.jeChoisis(presse.plusTot());
                    } else {
                        Jade.loggerArretRecherche();
                        presse.takeDown();
                    }
                }
            }
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
