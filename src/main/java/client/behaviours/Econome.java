/*
 * Auteur : Aymeric ZANIRATO
 * Email: aymeric@zanirato.fr
 */
package client.behaviours;

import client.ClientAgent;
import client.outils.Log;
import client.outils.Produit;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Classe qui permet de définir le comportement économe d'un client.
 * @author Aymeric
 */
public class Econome extends CyclicBehaviour {

    /**
     * Le client en question
     */
    private final ClientAgent econome;

    /**
     * Constructeur permettant d'affecter le comportement économe à un client
     * @param agent Agent qui possèdera le comportement econome
     */
    public Econome(Agent agent) {
        this.econome = (ClientAgent) agent;
    }

    /**
     * Action effectuée par le type comportement économe.
     */
    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            traiterMessage(msg);
            block();
        }
    }

    /**
     * Comportement effectué pour traiter un message en fonction de son type
     * @param message Le message à traiter.
     */
    public void traiterMessage(ACLMessage message) {

        try {
            Logger.getLogger(Econome.class.getName()).log(Level.INFO, message.getContent());
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(message.getContent());

            if (object.containsKey("jePropose")) {
                JSONArray array = (JSONArray) object.get("jePropose");
                econome.ajouterProposition(array, message);
                econome.setNbReponseReçu(econome.getNbReponseReçu() + 1);
                if (econome.getNbReponseReçu() == econome.getNbRechercheEnvoye()) {
                    // on nettoye les propositions en fonction du critère de prix max
                    econome.nettoyerPropositionPrix(econome.getLimitePrix());
                    if (econome.getLproposition().size() > 0) {
                        econome.jeChoisis(econome.moinsCher());
                    } else {
                        Log.arretRecherche();
                        econome.takeDown();
                    }
                }
            }

            if (object.containsKey("quantiteInsuffisante")) {
                econome.setNbReponseReçu(econome.getNbReponseReçu() + 1);
                JSONArray array = (JSONArray) object.get("quantiteInsuffisante");
                econome.ajouterProposition(array, message);
                Log.reception(econome.nomAgent(message), message.getContent());
                if (econome.getNbReponseReçu() == econome.getNbRechercheEnvoye()) {
                    // on nettoye les propositions en fonction du critère de prix max
                    econome.nettoyerPropositionPrix(econome.getLimitePrix());
                    if (econome.getLproposition().size() > 0) {
                        econome.jeChoisis(econome.moinsCher());
                    } else {
                        Log.arretRecherche();
                        econome.takeDown();
                    }
                }
            }

            if (object.containsKey("requeteInvalide")) {
                // aucune proposition correspond à la recherche pour cet agent
                econome.setNbReponseReçu(econome.getNbReponseReçu() + 1);
                Log.reception(econome.nomAgent(message), message.getContent());

                econome.afficherRaisonInvalide(object, message);
                
                if ((econome.getNbReponseReçu() == econome.getNbRechercheEnvoye())){
                    
                    // on nettoye les propositions en fonction du critère de prix max
                    econome.nettoyerPropositionPrix(econome.getLimitePrix());
                    if (econome.getLproposition().size() > 0) {
                        econome.jeChoisis(econome.moinsCher());
                    } else {
                        Log.arretRecherche();
                        econome.takeDown();
                    }
                }
            }

            if (object.containsKey("commandeOk")) {
                JSONObject obj = (JSONObject) object.get("commandeOk");
                econome.afficherAchat(obj, message);
                
                // laisser avis erep sur vendeur/fournisseur + produit
                econome.donneAvis(econome.getTypeAgentCible(), econome.nomAgent(message));
                econome.donneAvisProduit(obj.get("idProduit").toString());
                
                // arreter agent
                econome.takeDown();
            }

            if (object.containsKey("commandePasOK")) {
                JSONObject obj = (JSONObject) object.get("commandePasOK");
                econome.afficherRaison(obj, message);

                Produit produitAnnule = new Produit(obj, message.getSender().getName());

                // retirer la proposition
                econome.retirerProposition(produitAnnule);

                // choisir la meilleur proposition suivante si il y en a
                if (econome.getLproposition().size() > 0) {
                    econome.jeChoisis(econome.moinsCher());
                } else {
                    Log.arretRecherche();
                    econome.takeDown();
                }

            }


        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(ClientAgent.class.getName()).log(Level.SEVERE, "Parse impossible, format JSON invalide");
        }
    }

}
