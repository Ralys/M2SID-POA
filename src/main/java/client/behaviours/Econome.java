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
public class Econome extends CyclicBehaviour {

    private final Client econome;
    private final double facteurPrixMax = 1.2;

    public Econome(Agent agent) {
        this.econome = (Client) agent;
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
                econome.ajouterProposition(array, message);
                econome.jeChoisis(econome.moinsCher());
            }

            if (object.containsKey("commandeOk")) {
                JSONObject obj = (JSONObject) object.get("commandeOk");
                econome.afficherAchat(obj, message);
                // laisser avis erep
            }

            if (object.containsKey("commandePasOK")) {
                JSONObject obj = (JSONObject) object.get("commandePasOK");
                econome.afficherRaison(obj, message);

                Produit produitAnnule = new Produit(obj, message.getSender().getName());

                // retirer la proposition
                econome.retirerProposition(produitAnnule);

                // choisir la meilleur proposition suivante si il y en a
                if (econome.getLproposition().size() > 0) {
                    if (econome.offreInteressante(produitAnnule.getPrix() * facteurPrixMax)) {
                        econome.jeChoisis(econome.moinsCher());
                    } else {
                        Jade.loggerArretRecherche();
                        econome.takeDown();
                    }
                }
            }
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
