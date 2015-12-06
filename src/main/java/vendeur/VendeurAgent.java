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
            Logger.getLogger(getLocalName()).log(Level.INFO, "("+getLocalName()+") Message envoyé à " + f.getLocalName() + " : " + message);
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
            messageBDD = sendMessage(ACLMessage.REQUEST, QueryBuilder.rechercheRef(ref,getLocalName()), getBDDAgent(), true);
        }

        System.out.println(messageBDD.toString());

        JSONArray resultatsBDD = (JSONArray) this.parser.parse(messageBDD.getContent());
        JSONObject reponse = new JSONObject();
        JSONArray list = new JSONArray();

        if (!resultatsBDD.isEmpty()) { // si le produit existe
            if (typeRech.compareTo("ChercheRef") == 0) { //qu'un seul resultat
                for (Iterator iterator = resultatsBDD.iterator(); iterator.hasNext();) { //iterator sur chaque objet
                    JSONObject resultat = (JSONObject) iterator.next();
                    String refProd = (String) resultat.get("REF_PRODUIT");
                    String nomProd = (String) resultat.get("NOM_PRODUIT");
                    Double prixUProd = (Double) resultat.get("PRIX_UNITAIRE");
                    Double prixLProd = (Double) resultat.get("PRIX_LIMITE");
                    Long qteProd = (Long) resultat.get("QTE");

                    JSONObject retourRecherche1 = new JSONObject();
                    JSONObject retourRecherche2 = new JSONObject();
                    JSONObject retourRecherche3 = new JSONObject();

                    // /!\ faire un truc avec le prix pour savoir a combien vendre /!\

                    //int qte = 1;
                    if (qteProd > 0) { // il y a assez de stock
                        //proposer 3 prix a chaque fois

                        //prix avec 1 jour de livraison
                        retourRecherche1.put("idProduit", refProd);
                        retourRecherche1.put("nomProduit", nomProd);
                        retourRecherche1.put("quantite", quantite);
                        retourRecherche1.put("prix", prixLProd);
                        retourRecherche1.put("date", Dates.addDays(1).toString());
                        list.add(retourRecherche1);

                        //prix avec 3 jours de livraison
                        retourRecherche2.put("idProduit", refProd);
                        retourRecherche2.put("nomProduit", nomProd);
                        retourRecherche2.put("quantite", quantite);
                        retourRecherche2.put("prix", prixLProd);
                        retourRecherche2.put("date", Dates.addDays(3).toString());
                        list.add(retourRecherche2);

                        //prix avec 10 jours de livraison
                        retourRecherche3.put("idProduit", refProd);
                        retourRecherche3.put("nomProduit", nomProd);
                        retourRecherche3.put("quantite", quantite);
                        retourRecherche3.put("prix", prixLProd);
                        retourRecherche3.put("date", Dates.addDays(10).toString());

                        list.add(retourRecherche3);
                        if (qteProd >= quantite) {
                            reponse.put("jePropose", list);
                        } else {
                            reponse.put("quantiteInsuffisante", list);

                        }
                    } else if (qteProd == 0) {
                        retourRecherche1.put("idProduit", resultat.get("REF_PRODUIT"));
                        retourRecherche1.put("raison", "quantite a zero");
                        reponse.put("requeteInvalide", retourRecherche1);
                    }
                }
            } else { //plusieurs resultats
                for (Iterator iterator = resultatsBDD.iterator(); iterator.hasNext();) { //iterator sur chaque objet
                    JSONObject resultat = (JSONObject) iterator.next();
                    String refProd = (String) resultat.get("REF_PRODUIT");
                    String nomProd = (String) resultat.get("NOM_PRODUIT");
                    Double prixUProd = (Double) resultat.get("PRIX_UNITAIRE");
                    Double prixLProd = (Double) resultat.get("PRIX_LIMITE");
                    Long qteProd = (Long) resultat.get("QTE");

                    JSONObject retourRecherche1 = new JSONObject();
                    JSONObject retourRecherche2 = new JSONObject();
                    JSONObject retourRecherche3 = new JSONObject();

                    //faire un truc avec le prix pour savoir a combien vendre

                    //int qte = 1;
                    if (qteProd > 0) { // il y a assez de stock
                        //proposer 3 prix a chaque fois

                        //prix avec 1 jour de livraison
                        retourRecherche1.put("idProduit", refProd);
                        retourRecherche1.put("nomProduit", nomProd);
                        retourRecherche1.put("quantite", quantite);
                        retourRecherche1.put("prix", prixLProd);
                        retourRecherche1.put("date", Dates.addDays(1).toString());
                        list.add(retourRecherche1);

                        //prix avec 3 jours de livraison
                        retourRecherche2.put("idProduit", refProd);
                        retourRecherche2.put("nomProduit", nomProd);
                        retourRecherche2.put("quantite", quantite);
                        retourRecherche2.put("prix", prixLProd);
                        retourRecherche2.put("date", Dates.addDays(3).toString());
                        list.add(retourRecherche2);

                        //prix avec 10 jours de livraison
                        retourRecherche3.put("idProduit", refProd);
                        retourRecherche3.put("nomProduit", nomProd);
                        retourRecherche3.put("quantite", quantite);
                        retourRecherche3.put("prix", prixLProd);
                        retourRecherche3.put("date", Dates.addDays(10).toString());

                        list.add(retourRecherche3);
                        if (qteProd >= quantite) {
                            reponse.put("jePropose", list);
                        } else {
                            reponse.put("quantiteInsuffisante", list);

                        }
                    } else if (qteProd == 0) {
                        retourRecherche1.put("idProduit", resultat.get("REF_PRODUIT"));
                        retourRecherche1.put("raison", "quantite a zero");
                        reponse.put("requeteInvalide", retourRecherche1);
                    }
                }
            }

        } else { // si le produit n'existe pas
            //requete invalide, raison n'existe pas
        }

        String reponseJSON = reponse.toJSONString();

        // envoi de la réponse
        sendMessage(ACLMessage.PROPOSE, reponseJSON, sender);

        Logger.getLogger(getLocalName()).log(Level.INFO, "("+getLocalName()+") Message envoyé à " + sender.getLocalName() + " : " + reponseJSON);
    }

    public void CheckStock() {

        // Get quantity for each product

        ACLMessage stockProducts = sendMessage(ACLMessage.REQUEST, QueryBuilder.getRefListStock(getLocalName()), getBDDAgent(), true);


        JSONArray resultatsStockProducts = null;
        try {
            resultatsStockProducts = (JSONArray) this.parser.parse(stockProducts.getContent());

            JSONObject reponse = new JSONObject();
            JSONArray list = new JSONArray();

            for (Iterator iterator = resultatsStockProducts.iterator(); iterator.hasNext();) {
                JSONObject resultat = (JSONObject) iterator.next();

                String QTE_ = resultat.get("QTE")+"";
                String REF_PRODUIT = resultat.get("REF_PRODUIT")+"";

                //test quantity
                if(QTE_.contains("null")) {
                    prendreCommande(REF_PRODUIT, 5);
                }
                else {
                    Integer QTE = Integer.valueOf(QTE_);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void prendreCommande(String ref, Integer qte) {
        jeChercheReference(TypeAgent.Fournisseur, ref, qte);
    }

    public void fournisseurPropose(JSONArray jePropose, AID sender) {
        JSONObject reponse = new JSONObject();
        JSONArray list = new JSONArray();

        Double minPrix = -1.00;
        JSONObject min = null;

        for (Iterator iterator = jePropose.iterator(); iterator.hasNext();) {
            JSONObject resultat = (JSONObject) iterator.next();
            if(Double.valueOf(resultat.get("prix")+"") <  minPrix || minPrix == -1) {
                minPrix = Double.valueOf(resultat.get("prix")+"");
                min = resultat;
            }
        }

        fournisseurPropose(min, sender);
    }

    public void fournisseurPropose(JSONObject jePropose, AID sender) {
        // construction de l'objet JSON à envoyé
        JSONObject jeChercheReference = new JSONObject();
        JSONObject elementRecherche = new JSONObject();

        elementRecherche.put("idProduit", jePropose.get("idProduit")+"");
        elementRecherche.put("nomProduit", jePropose.get("nomProduit")+"");
        elementRecherche.put("prix", jePropose.get("prix")+"");
        elementRecherche.put("quantite", jePropose.get("quantite")+"");
        elementRecherche.put("date", jePropose.get("date")+"");

        jeChercheReference.put("jeChoisis", elementRecherche);

        Logger.getLogger(getLocalName()).log(Level.INFO, "("+getLocalName()+") Message envoyé à "+sender.getLocalName()+" : "+jeChercheReference.toJSONString());

        sendMessage(ACLMessage.ACCEPT_PROPOSAL, jeChercheReference.toJSONString(), sender, false);
    }


    public void ajoutStock(JSONObject commandeOK, AID sender) {
        String idProduit =  commandeOK.get("idProduit")+"";
        Integer quantite =  Integer.valueOf(commandeOK.get("quantite")+"");
        Float prix = Double.valueOf(commandeOK.get("prix")+"").floatValue() ;

        ACLMessage messageBDD = sendMessage(ACLMessage.REQUEST, QueryBuilder.getRefStock(idProduit, sender.getLocalName()), getBDDAgent(), true);


        try {
            JSONArray resultatsBDD = (JSONArray) this.parser.parse(messageBDD.getContent());
            if(resultatsBDD.size() == 0) {
                sendMessage(ACLMessage.INFORM, QueryBuilder.newStock(idProduit, quantite, prix, sender.getLocalName()), getBDDAgent(), true);
            }
            else {
                sendMessage(ACLMessage.INFORM, QueryBuilder.updateStock(idProduit, resultatsBDD.size()+quantite, prix, sender.getLocalName()), getBDDAgent(), true);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
