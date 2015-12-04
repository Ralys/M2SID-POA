package fournisseur.behaviors;

import fournisseur.FournisseurAgent;
import fournisseur.utils.Livraison;
import fournisseur.utils.Produit;
import fournisseur.utils.StocksEtTransaction;
import fournisseur.utils.Transaction;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
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
            String messageContent = msg.getContent();
            String sender = msg.getSender().getName();

            String receptionMessage = "(" + myAgent.getLocalName() + ") reçoit requête : " + messageContent + " de " + sender;
            Logger.getLogger(FournisseurAgent.class.getName()).log(Level.INFO, receptionMessage);
            String recherche = "";
            int reference = -1;
            try {
                //{“jeCherche”:{“typeProduit”:”DVD”,”recherche”:”Spectre”,”quantite”:1}}
                //{“jeChercheRef”:{”reference”:”67D”,”quantite”:1}}
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(messageContent);
                ArrayList<Produit> listProduit = new ArrayList<>();
                int quantite = 0;
                if (object.containsKey("jeCherche")) {
                    JSONObject requete = (JSONObject) object.get("jeCherche");
                    String typeProduit = requete.get("typeProduit").toString();
                    recherche = requete.get("recherche").toString();
                    quantite = Integer.valueOf(requete.get("quantite").toString());

                    //Récupération de tout ce que peut etre proposé pour la recherche
                    listProduit = ((StocksEtTransaction) getDataStore()).rechercheProduit(recherche, typeProduit, quantite);
                } else if (object.containsKey("jeChercheRef")) {
                    JSONObject requete = (JSONObject) object.get("jeChercheRef");
                    reference = Integer.valueOf(requete.get("reference").toString());
                    quantite = Integer.valueOf(requete.get("quantite").toString());
                    listProduit.add(((StocksEtTransaction) getDataStore()).getProduitById(reference));
                }

                //Json réponse
                JSONObject replyJson = new JSONObject();
                JSONArray tabProduit = new JSONArray();
                HashMap<Integer, Long> listDate = Livraison.getListeDateLivraison();
                Set<Integer> listDelai = listDate.keySet();

                //{“jePropose”:[{“idProduit”:”67D”,”nomProduit”:”Spectre”,”quantite”:2,”prix”:6.7,”date”:”27/02/2105”},...]}
                for (Produit p : listProduit) { //Pour tous les produits, on fais une proposition
                    //Pour les trois date possible
                    for (Integer delai : listDelai) {
                        Transaction t = new Transaction(p.getIdProduit(), listDate.get(delai), sender, quantite, delai);
                        ((StocksEtTransaction) getDataStore()).put(t, p);

                        JSONObject produitJson = new JSONObject();
                        produitJson.put("idProduit", p.getIdProduit());
                        produitJson.put("nomProduit", p.getNomProduit());
                        produitJson.put("prix", this.definirPrix(p.getIdProduit(), quantite, delai));
                        produitJson.put("quantite", quantite);
                        produitJson.put("date", listDate.get(delai));
                        tabProduit.add(produitJson);
                    }
                }
                ACLMessage replyMessage = msg.createReply();
                String contenuMessage = "";

                // Si on a une réponse, on envoie un tableau Json de tout les produits a proposer
                if (!listProduit.isEmpty()) {
                    replyJson.put("jePropose", tabProduit);
                    //Log
                    contenuMessage = replyJson.toJSONString();
                    String envoiMessage = "(" + myAgent.getLocalName() + ") Message envoyé : " + contenuMessage + " : envoyé à " + sender;
                    Logger.getLogger(WaitRequest.class.getName()).log(Level.INFO, envoiMessage);
                } else {
                    //réponse
                    JSONObject reqInvalide = new JSONObject();
                    reqInvalide.put("recherche", recherche);
                    reqInvalide.put("idProduit", reference);
                    replyJson.put("requeteInvalide", tabProduit);
                    //Log
                    Logger.getLogger(WaitRequest.class.getName()).log(Level.INFO, "Aucun produit correspondant à la recherche");
                }
                //Envoie de la réponse
                contenuMessage = replyJson.toJSONString();
                replyMessage.setContent(contenuMessage);
                myAgent.send(replyMessage);
            } catch (ParseException ex) {
                Logger.getLogger(WaitRequest.class.getName()).log(Level.SEVERE, "Format de message invalide");
            }
        } else {
            block();
        }
    }

    //Méthode défini par une stratégie
    public abstract double definirPrix(int idProduit, int quantite, int delai);

}
