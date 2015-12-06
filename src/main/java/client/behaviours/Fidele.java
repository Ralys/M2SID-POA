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
 * Classe qui permet de définir le comportement fidèle d'un client.
 * @author mercier
 */
public class Fidele extends CyclicBehaviour {
    
    /**
     * Le client en question
     */
    private final ClientAgent fidele;
    
    /**
     * Constructeur permettant d'affecter le comportement fidèle à un client
     * @param agent Agent qui possèdera le comportement fidèle
     */
    public Fidele(Agent agent) {
        this.fidele = (ClientAgent) agent;
    }
    
    /**
     * Action effectuée par le type comportement fidèle.
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
     * Comportement effectué pour traiter un message
     * @param message Le message à traiter.
     */
    public void traiterMessage(ACLMessage message) {
        try {
//            System.out.println("Agent FIDELE");
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(message.getContent());
            
            // On récupère les propositions des fournisseurs ou des vendeurs
            if (object.containsKey("jePropose")) {
//                System.out.println("Je propose fidele");
                JSONArray array = (JSONArray) object.get("jePropose");
                fidele.ajouterProposition(array, message);
                fidele.setNbReponseReçu(fidele.getNbReponseReçu() + 1);
                if (fidele.getNbReponseReçu() == fidele.getNbRechercheEnvoye()) {
//                    System.out.println("If du fidèle");
                    fidele.jeChoisis(fidele.choixFidelite());
                }
            }
            
            if (object.containsKey("quantiteInsuffisante")) {
                fidele.setNbReponseReçu(fidele.getNbReponseReçu() + 1);
                JSONArray array = (JSONArray) object.get("quantiteInsuffisante");
                Log.reception(fidele.nomAgent(message), message.getContent());
                if (fidele.getNbReponseReçu() == fidele.getNbRechercheEnvoye()) {
                    if (fidele.getLproposition().size() > 0) {
                        fidele.jeChoisis(fidele.choixFidelite());
                    } else {
                        Log.arretRecherche();
                        fidele.takeDown();
                    }
                }
            }
            
            // En cas de requête invalide
            if (object.containsKey("requeteInvalide")) {
                // Aucune proposition correspond à la recherche pour cet agent
                fidele.setNbReponseReçu(fidele.getNbReponseReçu() + 1);
                Log.reception(fidele.nomAgent(message), message.getContent());

                fidele.afficherRaisonInvalide(object, message);
                
                if ((fidele.getNbReponseReçu() == fidele.getNbRechercheEnvoye())){
                    if (fidele.getLproposition().size() > 0) {
                        fidele.jeChoisis(fidele.choixFidelite());
                    } else {
                        Log.arretRecherche();
                        fidele.takeDown();
                    }
                }
            }
            
            // Action lorsque la commande est OK
            if (object.containsKey("commandeOk")) {
                JSONObject obj = (JSONObject) object.get("commandeOk");
                fidele.afficherAchat(obj, message);
                
                // Laisser avis erep sur vendeur/fournisseur + produit
                fidele.donneAvis(fidele.getTypeAgentCible(), fidele.nomAgent(message));
                fidele.donneAvisProduit(obj.get("idProduit").toString());
                
                // Arreter agent
                fidele.takeDown();
            }
            
            // Action lorsque la commande n'est pas OK
            if (object.containsKey("commandePasOK")) {
                JSONObject obj = (JSONObject) object.get("commandePasOK");
                fidele.afficherRaison(obj, message);
                
                Produit produitAnnule = new Produit(obj, message.getSender().getName());
                
                // Retirer la proposition
                fidele.retirerProposition(produitAnnule);
                
                // Choisir la meilleur proposition suivante si il y en a
                if (fidele.getLproposition().size() > 0) {
                    fidele.jeChoisis(fidele.plusTot());
                } else {
                    Log.arretRecherche();
                    fidele.takeDown();
                }
            }
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(ClientAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}