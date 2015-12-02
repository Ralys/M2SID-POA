package fournisseur;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    
    private final SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
    
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {            //Reception d'une demande de produit
            try {
                //{“jeCherche”:{“typeProduit”:”DVD”,”recherche”:”Spectre”,”quantite”:1}}
                //{“jeChercheRef”:{”reference”:”67D”,”quantite”:1}}
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(msg.getContent());
                ArrayList<Produit> listProduit = new ArrayList<>();
                int quantite;
                if (object.containsKey("jeCherche")) {
                    JSONObject requete = (JSONObject) object.get("jeCherche");
                    String typeProduit = requete.get("typeProduit").toString();
                    String recherche = requete.get("recherche").toString();
                    quantite = Integer.valueOf(requete.get("quantite").toString());
                    
                    //Récupération de tout ce que peut etre proposé pour la recherche
                    listProduit = ((Stocks) getDataStore()).rechercheProduit(recherche, typeProduit, quantite);
                } else if (object.containsKey("jeChercheRef")) {
                    JSONObject requete = (JSONObject) object.get("jeChercheRef");
                    int reference = Integer.valueOf(requete.get("reference").toString());
                    quantite = Integer.valueOf(requete.get("quantite").toString());
                    listProduit.add(((Stocks) getDataStore()).getProduitById(reference));
                } else {
                    throw new ParseException(0);//TODO Exception moins sale ?
                }

                //Json réponse
                JSONObject replyJson = new JSONObject();
                JSONArray tabProduit = new JSONArray();
                HashMap<Integer, Date> listDate = DateLivraison.getListeDateLivraison();
                Set<Integer> listDelai = listDate.keySet();

                //{“jePropose”:[{“idProduit”:”67D”,”nomProduit”:”Spectre”,”quantite”:2,”prix”:6.7,”date”:”27/02/2105”},...]}
                for (Produit p : listProduit) { //Pour tous les produits, on fais une proposition
                    //Pour les trois date possible
                    for (Integer delai : listDelai) {
                        JSONObject produitJson = new JSONObject();
                        produitJson.put("idProduit", p.getIdProduit());
                        produitJson.put("nomProduit", p.getNomProduit());
                        produitJson.put("prix", this.definirPrix(p.getIdProduit(), quantite, delai));
                        produitJson.put("quantite", quantite);
                        produitJson.put("date", formater.format(listDate.get(delai)));
                        tabProduit.add(produitJson);
                    }
                }
                // Si on a une réponse, on envoie un tableau Json de tout les produits a proposer
                if (!listProduit.isEmpty()) {
                    replyJson.put("jePropose", tabProduit);
                    String contenuMessage = replyJson.toJSONString().replace("\\", "");

                    //Envoie de la réponse
                    ACLMessage replyMessage = msg.createReply();
                    replyMessage.setPerformative(ACLMessage.PROPOSE);
                    replyMessage.setContent(contenuMessage);
                    myAgent.send(replyMessage);
                    //Log
                    String envoiMessage = "(" + myAgent.getLocalName() + ") Message envoyé : " + contenuMessage;
                    System.out.println(envoiMessage);
                    Logger.getLogger(FournisseurAgent.class.getName()).log(Level.INFO, envoiMessage);
                } else {
                    Logger.getLogger(FournisseurAgent.class.getName()).log(Level.INFO, "Aucun produit correspondant à la recherche");
                }
            } catch (ParseException ex) {
                Logger.getLogger(FournisseurAgent.class.getName()).log(Level.SEVERE, "Format de message invalide");
            }
        } else {
            block();
        }
    }

    //Méthode défini par une stratégie
    public abstract double definirPrix(int idProduit, int quantite, int delai);

}
