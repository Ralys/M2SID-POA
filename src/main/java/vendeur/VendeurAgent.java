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
import vendeur.tools.QueryBuilder;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import vendeur.tools.Dates;


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
            sendMessage(ACLMessage.REQUEST, message, f, false);
        }
    }


    public void ClientRecherche(JSONObject jsonObject, AID sender, String typeRech) throws ParseException {

        String ref = (typeRech.compareTo("ChercheRef") == 0) ? jsonObject.get("reference").toString() : "";
        int quantite = Integer.parseInt(jsonObject.get("quantite").toString());
        String recherche = (typeRech.compareTo("Cherche") == 0) ? jsonObject.get("recherche").toString() : "";
        String typeProduit = (typeRech.compareTo("Cherche") == 0) ? jsonObject.get("typeProduit").toString() : "";

        ACLMessage messageBDD;
        if (typeRech.compareTo("Cherche") == 0) {
            messageBDD = sendMessage(ACLMessage.REQUEST, QueryBuilder.recherche(recherche, typeProduit), getBDDAgent(), true);

        } else {
            messageBDD = sendMessage(ACLMessage.REQUEST, QueryBuilder.rechercheRef(ref, getLocalName()), getBDDAgent(), true);
        }

        System.out.println(messageBDD.toString());

        JSONArray resultatsBDD = (JSONArray) this.parser.parse(messageBDD.getContent());
        JSONObject reponse = new JSONObject();
        JSONArray list = new JSONArray();

        if (!resultatsBDD.isEmpty()) { // si le produit existe
            for (Iterator iterator = resultatsBDD.iterator(); iterator.hasNext(); ) { //iterator sur chaque objet
                JSONObject resultat = (JSONObject) iterator.next();
                String refProd = (String) resultat.get("REF_PRODUIT");
                String nomProd = (String) resultat.get("NOM_PRODUIT");
                Double prixUProd = (Double) resultat.get("PRIX_UNITAIRE");
                Double prixLProd = (Double) resultat.get("PRIX_LIMITE");
                Long qteProd = (Long) resultat.get("QTE");

                // /!\ faire un truc avec le prix pour savoir a combien vendre /!\

                //int qte = 1;
                if (qteProd > 0) { // il y a assez de stock
                    //proposer 3 prix a chaque fois

                    int[] range = {1, 3, 10}; //jour de livraison
                    for (int i : range) {
                        JSONObject retour = new JSONObject();
                        retour.put("idProduit", refProd);
                        retour.put("nomProduit", nomProd);
                        retour.put("quantite", quantite);
                        retour.put("prix", prixLProd);
                        retour.put("date", Dates.addDays(i).toString());
                        list.add(retour);
                    }
                    if (qteProd >= quantite) {
                        reponse.put("jePropose", list);
                    } else {
                        reponse.put("quantiteInsuffisante", list);

                    }
                } else if (qteProd == 0) {
                    JSONObject retour = new JSONObject();
                    retour.put("idProduit", resultat.get("REF_PRODUIT"));
                    retour.put("raison", "quantite a zero");
                    reponse.put("requeteInvalide", retour);
                }
            }

        } else { // si le produit n'existe pas
            //requete invalide, raison n'existe pas
        }

        String reponseJSON = reponse.toJSONString();

        // envoi de la réponse
        sendMessage(ACLMessage.PROPOSE, reponseJSON, sender);

        String envoiMessage = "(" + getLocalName() + ") Message envoyé : " + reponseJSON;
        Logger.getLogger(VendeurAgent.class.getName()).log(Level.INFO, envoiMessage);

    }

    public void CheckStock() {

        // Get quantity for each product

        ACLMessage stockProducts = sendMessage(ACLMessage.REQUEST, QueryBuilder.getRefListStock(getLocalName()), getBDDAgent(), true);


        JSONArray resultatsStockProducts = null;
        try {
            resultatsStockProducts = (JSONArray) this.parser.parse(stockProducts.getContent());

            JSONObject reponse = new JSONObject();
            JSONArray list = new JSONArray();

            for (Iterator iterator = resultatsStockProducts.iterator(); iterator.hasNext(); ) {
                JSONObject resultat = (JSONObject) iterator.next();

                String QTE_ = resultat.get("QTE") + "";
                String REF_PRODUIT = resultat.get("REF_PRODUIT") + "";

                //test quantity
                if (QTE_.contains("null")) {
                    prendreCommande(REF_PRODUIT, 5);
                } else {
                    Integer QTE = Integer.valueOf(QTE_);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void prendreCommande(String ref, Integer qte) {
        System.out.println("jeChercheReference " + ref);
        jeChercheReference(TypeAgent.Fournisseur, ref, qte);
    }

    public void fournisseurPropose(JSONArray jePropose, AID sender) {
        JSONObject reponse = new JSONObject();
        JSONArray list = new JSONArray();

        Double minPrix = -1.00;
        JSONObject min = null;

        for (Iterator iterator = jePropose.iterator(); iterator.hasNext(); ) {
            JSONObject resultat = (JSONObject) iterator.next();
            if (Double.valueOf(resultat.get("prix") + "") < minPrix || minPrix == -1) {
                minPrix = Double.valueOf(resultat.get("prix") + "");
                min = resultat;
            }
        }

        fournisseurPropose(min, sender);
    }

    public void fournisseurPropose(JSONObject jePropose, AID sender) {
        System.out.println(jePropose);
    }
}
