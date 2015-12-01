package fournisseur;


import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Tom
 */
public abstract class WaitRequest extends CyclicBehaviour {

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {            //Reception d'une demande de produit
            try {
                //{“jeCherche”:{“typeProduit”:”DVD”,”recherche”:”Spectre”,”quantite”:1}}
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(msg.getContent());
                JSONObject requete = (JSONObject) object.get("jeCherche");

                String typeProduit = requete.get("typeProduit").toString();
                String recherche = requete.get("recherche").toString();
                int quantite = Integer.valueOf(requete.get("quantite").toString());

                //Récupération de tout ce que peut etre proposé
                ArrayList<Integer> listProduit = new ArrayList<>(); //Liste de produit
                //TODO
                
                //Json réponse
                JSONObject replyJson = new JSONObject();
                JSONArray tabProduit = new JSONArray();
                
                //{“jePropose”:[{“idProduit”:”67D”,”nomProduit”:”Spectre”,”quantite”:2,”prix”:6.7,”date”:”27/02/2105”},...]}
                for (Integer idProduit : listProduit) { //Pour tous les produits, on fais une proposition
                    JSONObject produitJson = new JSONObject();
                    produitJson.put("idProduit", idProduit);
                    produitJson.put("nomProduit", (String) getDataStore().get(idProduit));
                    produitJson.put("prix", this.definirPrix(idProduit, quantite)); 
                    produitJson.put("quantite", quantite);
                    produitJson.put("date", "");//TODO définir date
                    tabProduit.add(produitJson);

                }
                replyJson.put("jePropose", tabProduit);
                String contenuMessage = replyJson.toJSONString();
                
                //Envoie de la réponse
                ACLMessage replyMessage = msg.createReply();
                replyMessage.setPerformative(ACLMessage.PROPOSE);
                replyMessage.setContent(contenuMessage);
                myAgent.send(replyMessage);
                //Log
                String envoiMessage = "(" + myAgent.getLocalName() + ") Message envoyé : " + contenuMessage;
                Logger.getLogger(FournisseurAgent.class.getName()).log(Level.INFO, envoiMessage);
            } catch (ParseException ex) {
                Logger.getLogger(WaitRequest.class.getName()).log(Level.SEVERE, null, "Format de message invalide");
            }

        } else {
            block();
        }
    }
    
    //Méthode défini par une stratégie
    public abstract int definirPrix(int idProduit, int quantite);

}
