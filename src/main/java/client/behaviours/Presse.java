/*
 * Auteur : Aymeric ZANIRATO
 * Email: aymeric@zanirato.fr
 */
package client.behaviours;

import client.ClientAgent;
import client.outils.Log;
import client.outils.Produit;
import client.outils.TypeAgentClient;
import common.TypeAgent;
import jade.core.AID;
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

    private final ClientAgent presse;
    private final double facteurPrixMax = 1.2;
    // 1J
    private final int facteurDateMax = 86400000;

    public Presse(Agent agent) {
        this.presse = (ClientAgent) agent;
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
                presse.setNbReponseReçu(presse.getNbReponseReçu() + 1);

                if (presse.getNbReponseReçu() == presse.getNbRechercheEnvoye()) {
                    // on nettoye les propositions en fonction du critère du temps max
                    presse.nettoyerProposition(facteurPrixMax);
                    if (presse.getLproposition().size() > 0) {
                        // on nettoye les propositions en fonction du critère du temps max
                        presse.nettoyerProposition(facteurPrixMax);
                        presse.jeChoisis(presse.plusTot());
                    } else {
                        Log.arretRecherche();
                        presse.takeDown();
                    }
                }

            }

            if (object.containsKey("quantiteInsuffisante")) {
                presse.setNbReponseReçu(presse.getNbReponseReçu() + 1);
                JSONArray array = (JSONArray) object.get("quantiteInsuffisante");
                Log.reception(presse.nomAgent(message), message.getContent());
                if (presse.getNbReponseReçu() == presse.getNbRechercheEnvoye()) {
                    if (presse.getLproposition().size() > 0) {
                        presse.jeChoisis(presse.plusTot());
                    } else {
                        Log.arretRecherche();
                        presse.takeDown();
                    }
                }
            }

            if (object.containsKey("requeteInvalide")) {
                // aucune proposition correspond à la recherche pour cet agent
                presse.setNbReponseReçu(presse.getNbReponseReçu() + 1);
                Log.reception(presse.nomAgent(message), message.getContent());
                presse.afficherRaisonInvalide(object, message);

                // il n'y a pus d'attendre de réponse et aucune propostion existe dans la liste 
                if ((presse.getNbReponseReçu() != presse.getNbRechercheEnvoye())
                        && presse.getLproposition().isEmpty()) {
                    Log.arretRecherche();
                    presse.takeDown();
                }
            }

            if (object.containsKey("commandeOk")) {
                JSONObject obj = (JSONObject) object.get("commandeOk");
                presse.afficherAchat(obj, message);
                // laisser avis erep sur vendeur/fournisseur + produit
                presse.donneAvis(presse.getTypeAgentCible(), presse.nomAgent(message));
                presse.donneAvisProduit(obj.get("idProduit").toString());
            }

            if (object.containsKey("commandePasOK")) {
                JSONObject obj = (JSONObject) object.get("commandePasOK");
                presse.afficherRaison(obj, message);

                Produit produitAnnule = new Produit(obj, message.getSender().getName());

                // retirer la proposition
                presse.retirerProposition(produitAnnule);

                // choisir la meilleur proposition suivante si il y en a
                if (presse.getLproposition().size() > 0) {
                    presse.jeChoisis(presse.plusTot());
                } else {
                    Log.arretRecherche();
                    presse.takeDown();
                }
            }

            // A FAIRE
            if (object.containsKey("retourDesirabilite")) {
                JSONObject obj = (JSONObject) object.get("retourDesirabilite");
            }

        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(ClientAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
