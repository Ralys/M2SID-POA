package vendeur;

import common.SuperAgent;
import common.TypeAgent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import vendeur.behaviours.ACLController;
import vendeur.behaviours.PurchaseProduct;
import vendeur.tools.QueryBuilder;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Aurélien
 */

public class VendeurAgent extends SuperAgent {

    private AID BDDAgent;
    private final JSONParser parser = new JSONParser();

    @Override
    protected void setup() {
        this.registerService(TypeAgent.Vendeur);

        this.addBehaviour(new ACLController(this));
        this.addBehaviour(new PurchaseProduct(this));
    }

    public AID getBDDAgent() {
        if (this.BDDAgent == null) {
            this.BDDAgent = findBDDAgent();
        }

        return this.BDDAgent;
    }

    private AID findBDDAgent() {
        //Parametre : numero du fournisseur
        try {
            return this.findAgentsFromService(TypeAgent.BDD)[0];
        } catch (IndexOutOfBoundsException io) {
            System.out.println("Error Vendeur : Can't find BDDAgent");
            this.takeDown();
        }

        return null;
    }

    public ACLMessage sendMessage(int typeMessage, String contenu, AID destinataire) {
        return this.sendMessage(typeMessage, contenu, destinataire, false);
    }

    public ACLMessage sendMessage(int typeMessage, String contenu, AID destinataire, boolean withResponse) {
        ACLMessage messsage = new ACLMessage(typeMessage);
        messsage.setContent(contenu);
        messsage.addReceiver(destinataire);
        this.send(messsage);

        if (!withResponse) {
            return null;
        }

        MessageTemplate mt = MessageTemplate.MatchSender(destinataire);
        ACLMessage messageReponse = this.blockingReceive(mt);
        return messageReponse;
    }


    public void jeChercheReference(String typeAgent, String reference, int quantite) {

        // construction de l'objet JSON à envoyé
        JSONObject jeChercheReference = new JSONObject();
        JSONObject elementRecherche = new JSONObject();
        elementRecherche.put("quantite", quantite);
        elementRecherche.put("reference", reference);
        jeChercheReference.put("jeChercheRef", elementRecherche);

        // envoi du message de recherche à tous les agents
        // du type choisi
        AID[] agent = findAgentsFromService(TypeAgent.Fournisseur);
        for (AID f : agent) {
            String message = jeChercheReference.toString();
            sendMessage(ACLMessage.REQUEST, message, f, true);
        }
    }



    public void ClientRecherche(JSONObject jsonObject, AID sender, String typeRech) throws ParseException {

        String ref = (typeRech.compareTo("ChercheRef") == 0) ? jsonObject.get("reference").toString() : "";
        String quantite = jsonObject.get("quantite").toString();
        String recherche = (typeRech.compareTo("Cherche") == 0) ? jsonObject.get("recherche").toString() : "";
        String typeProduit = (typeRech.compareTo("Cherche") == 0) ? jsonObject.get("typeProduit").toString() : "";

        ACLMessage messageBDD;
        if (typeRech.compareTo("Cherche") == 0) {
            messageBDD = sendMessage(ACLMessage.REQUEST, QueryBuilder.recherche(recherche, typeProduit), getBDDAgent(), true);

        } else {
            messageBDD = sendMessage(ACLMessage.REQUEST, QueryBuilder.rechercheRef(ref), getBDDAgent(), true);
        }

        System.out.println(messageBDD.toString());

        JSONArray resultatsBDD = (JSONArray) this.parser.parse(messageBDD.getContent());

        JSONObject reponse = new JSONObject();
        JSONArray list = new JSONArray();
        for (Iterator iterator = resultatsBDD.iterator(); iterator.hasNext();) {
            JSONObject resultat = (JSONObject) iterator.next();
            JSONObject retourRecherche = jsonObject;
            retourRecherche.put("ref_produit", resultat.get("REF_PRODUIT"));
            retourRecherche.put("nom_produit", resultat.get("NOM_PRODUIT"));
            retourRecherche.put("prix", resultat.get("PRIX_CREATION"));

            //verifier la quantite
            retourRecherche.put("qte", resultat.get("QTE"));
            list.add(retourRecherche);
        }
        reponse.put("jePropose", list);

        String reponseJSON = reponse.toJSONString();

        // envoi de la réponse
        sendMessage(ACLMessage.PROPOSE, reponseJSON, sender);

        String envoiMessage = "(" + getLocalName() + ") Message envoyé : " + reponseJSON;
        Logger.getLogger(VendeurAgent.class.getName()).log(Level.INFO, envoiMessage);

    }

    public void fournisseurPropose(JSONObject jePropose, AID sender) {
        System.out.println(jePropose);
    }
}
