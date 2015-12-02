package vendeur.behaviours;

import common.TypeAgent;
import vendeur.tools.QueryBuilder;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import vendeur.VendeurAgent;

/**
 *
 * @author Aurélien
 */
public class OfferRequests extends CyclicBehaviour {

    private final JSONParser parser;

    public OfferRequests(VendeurAgent aThis) {
        super(aThis);
        this.parser = new JSONParser();
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            //Réception
            String content = msg.getContent();

            try {
                JSONObject object = (JSONObject) this.parser.parse(content);

                if (object.containsKey("jeCherche")) {
                    this.recherche((JSONObject) object.get("jeCherche"), msg.getSender());
                }
            } catch (ParseException ex) {
                Logger.getLogger(myAgent.getLocalName()).log(Level.WARNING, "Format de message invalide");
            }
        }
    }

    private void recherche(JSONObject jsonObject, AID sender) throws ParseException {
        String quantite = "";
        String recherche = "";
        String typeProduit = "";
        String type = jsonObject.get("type").toString();

        switch (type) {
            case TypeAgent.Client:
                quantite = jsonObject.get("quantite").toString();
                recherche = jsonObject.get("recherche").toString();
                typeProduit = jsonObject.get("typeProduit").toString();
                break;
            default:
                break;
        }

        VendeurAgent vendeur = (VendeurAgent) myAgent;

        ACLMessage messageBDD = vendeur.sendMessage(ACLMessage.REQUEST, QueryBuilder.recherche(type, vendeur.getLocalName(), recherche, typeProduit), vendeur.getBDDAgent(), true);
        JSONArray resultatsBDD = (JSONArray) this.parser.parse(messageBDD.getContent());

        JSONObject reponse = new JSONObject();
        JSONArray list = new JSONArray();
        for (Iterator iterator = resultatsBDD.iterator(); iterator.hasNext();) {
            JSONObject resultat = (JSONObject) iterator.next();
            JSONObject retourRecherche = jsonObject;
            retourRecherche.put("ref_produit", resultat.get("REF_PRODUIT"));
            retourRecherche.put("nom_produit", resultat.get("NOM_PRODUIT"));
            retourRecherche.put("prix", resultat.get("PRIX_UNITAIRE"));
            retourRecherche.put("qte", resultat.get("QTE"));
            list.add(retourRecherche);
        }
        reponse.put("jePropose", list);

        String reponseJSON = reponse.toJSONString();

        // envoi de la réponse
        vendeur.sendMessage(ACLMessage.PROPOSE, reponseJSON, sender);

        String envoiMessage = "(" + myAgent.getLocalName() + ") Message envoyé : " + reponseJSON;
        Logger.getLogger(VendeurAgent.class.getName()).log(Level.INFO, envoiMessage);
    }

}
