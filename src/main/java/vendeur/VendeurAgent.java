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
import vendeur.behaviours.strategies.Normale;
import vendeur.behaviours.strategies.Soldes;
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
    private boolean enSoldes;
    private int nbNegoce = 0;
    private AID ERepAgent;

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

    public AID getERepAgent() {
        if (this.ERepAgent == null) {
            this.ERepAgent = findERepAgent();
        }

        return this.ERepAgent;
    }

    private AID findBDDAgent() {
        try {
            return this.findAgentsFromService(TypeAgent.BDD)[0];
        } catch (IndexOutOfBoundsException io) {
            System.out.println("Error Vendeur : Can't find BDDAgent");
            this.takeDown();
        }

        return null;
    }

    private AID findERepAgent() {
        try {
            return this.findAgentsFromService(TypeAgent.EReputation)[0];
        } catch (IndexOutOfBoundsException io) {
            System.out.println("Error Vendeur : Can't find ERepAgent");
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
            Logger.getLogger(getLocalName()).log(Level.INFO, "(" + getLocalName() + ") Message envoyé à " + f.getLocalName() + " : " + message);
        }
    }

    public void clientRecherche(JSONObject jsonObject, AID sender, String typeRech) throws ParseException {

        String ref = (typeRech.compareTo("ChercheRef") == 0) ? jsonObject.get("reference").toString() : "";
        int quantite = Integer.parseInt(jsonObject.get("quantite").toString());
        String recherche = (typeRech.compareTo("Cherche") == 0) ? jsonObject.get("recherche").toString() : "";
        String typeProduit = (typeRech.compareTo("Cherche") == 0) ? jsonObject.get("typeProduit").toString() : "";

        ACLMessage messageBDD;
        messageBDD = sendMessage(ACLMessage.REQUEST, QueryBuilder.isSoldes(getLocalName(), Dates.dateJour), getBDDAgent(), true);
        enSoldes = !messageBDD.getContent().isEmpty();

        if (typeRech.compareTo("Cherche") == 0) {
            messageBDD = sendMessage(ACLMessage.REQUEST, QueryBuilder.recherche(recherche, typeProduit, getLocalName()), getBDDAgent(), true);

        } else {
            messageBDD = sendMessage(ACLMessage.REQUEST, QueryBuilder.rechercheRef(ref, getLocalName()), getBDDAgent(), true);
        }

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
                final JSONObject demande = new JSONObject();
                demande.put("type", "Produit");
                demande.put("id", refProd);
                ACLMessage desirabilite = sendMessage(ACLMessage.REQUEST, (new JSONObject() {{
                    put("demandeDesirabilite", demande);
                }}).toJSONString(), getERepAgent(), true);
                JSONObject resultats = (JSONObject) this.parser.parse(desirabilite.getContent());
                Double desir = 5.0;
                if (!(resultats.get("desirabilite") + "").contains("null"))
                    desir = Double.valueOf(resultats.get("desirabilite") + "");
                //int qte = 1;
                if (qteProd > 0) { // il y a assez de stock
                    //proposer 3 prix a chaque fois

                    int[] range = {1, 3, 10}; //jour de livraison
                    for (int jour : range) {
                        JSONObject retour = new JSONObject();
                        retour.put("idProduit", refProd);
                        retour.put("nomProduit", nomProd);
                        retour.put("quantite", quantite);
                        if (!enSoldes)
                            retour.put("prix", Normale.getPrix(prixUProd, jour, desir));
                        else
                            retour.put("prix", Soldes.getPrix(prixUProd, prixLProd, jour, desir));
                        retour.put("date", Dates.addDays(jour).toString());
                        list.add(retour);
                    }
                    if (qteProd >= quantite) {
                        reponse.put("jePropose", list);
                    } else {
                        reponse.put("quantiteInsuffisante", list);
                    }
                    reponse.put("jePropose", list);
                } else if (qteProd == 0) {
                    JSONObject retour = new JSONObject();
                    retour.put("idProduit", resultat.get("REF_PRODUIT"));
                    retour.put("raison", "quantite a zero");
                    reponse.put("requeteInvalide", retour);
                }
            }

        } else { // si le produit n'existe pas
            //requete invalide, raison n'existe pas
            JSONObject retourRecherche1 = new JSONObject();
            if (typeRech.compareTo("ChercheRef") == 0) {
                retourRecherche1.put("idProduit", ref);
            } else {
                retourRecherche1.put("recherche", recherche);
            }
            retourRecherche1.put("raison", "n'existe pas");
            reponse.put("requeteInvalide", retourRecherche1);
        }

        String reponseJSON = reponse.toJSONString();

        // envoi de la réponse
        sendMessage(ACLMessage.PROPOSE, reponseJSON, sender);

        Logger.getLogger(getLocalName()).log(Level.INFO, "(" + getLocalName() + ") Message envoyé à " + sender.getLocalName() + " : " + reponseJSON);
    }

    public void CheckStock() {

        // Get quantity for each product
        ACLMessage stockProducts = sendMessage(ACLMessage.REQUEST, QueryBuilder.getRefListStock(getLocalName()), getBDDAgent(), true);


        JSONArray resultatsStockProducts = null;
        try {
            resultatsStockProducts = (JSONArray) this.parser.parse(stockProducts.getContent());

            for (Iterator iterator = resultatsStockProducts.iterator(); iterator.hasNext(); ) {
                JSONObject resultat = (JSONObject) iterator.next();

                String QTE_ = resultat.get("QTE") + "";
                String REF_PRODUIT = resultat.get("REF_PRODUIT") + "";

                //test quantity
                if (QTE_.contains("null")) {
                    prendreCommande(REF_PRODUIT, 5);
                } else {
                    Integer QTE = Integer.valueOf(QTE_);
                    final JSONObject demande = new JSONObject();
                    demande.put("type", "Produit");
                    demande.put("id", REF_PRODUIT);
                    ACLMessage desirabilite = sendMessage(ACLMessage.REQUEST, (new JSONObject() {{
                        put("demandeDesirabilite", demande);
                    }}).toJSONString(), getERepAgent(), true);

                    JSONObject resultats = (JSONObject) this.parser.parse(desirabilite.getContent());
                    Double desir = 5.0;
                    if (!(resultats.get("desirabilite") + "").contains("null")) {
                        desir = Double.valueOf(resultats.get("desirabilite") + "");
                        int stockCible = (int) (3*desir-QTE);
                        if(stockCible > 0)
                            prendreCommande(REF_PRODUIT, stockCible);
                    }
                    else
                        prendreCommande(REF_PRODUIT, 5);
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
        // construction de l'objet JSON à envoyé
        JSONObject jeChercheReference = new JSONObject();
        JSONObject elementRecherche = new JSONObject();

        elementRecherche.put("idProduit", jePropose.get("idProduit") + "");
        elementRecherche.put("nomProduit", jePropose.get("nomProduit") + "");
        elementRecherche.put("prix", jePropose.get("prix") + "");
        elementRecherche.put("quantite", jePropose.get("quantite") + "");
        elementRecherche.put("date", jePropose.get("date") + "");

        jeChercheReference.put("jeChoisis", elementRecherche);

        Logger.getLogger(getLocalName()).log(Level.INFO, "(" + getLocalName() + ") Message envoyé à " + sender.getLocalName() + " : " + jeChercheReference.toJSONString());

        sendMessage(ACLMessage.ACCEPT_PROPOSAL, jeChercheReference.toJSONString(), sender, false);
    }


    public void ajoutStock(JSONObject commandeOK, AID sender) {
        String idProduit = commandeOK.get("idProduit") + "";
        Integer quantite = Integer.valueOf(commandeOK.get("quantite") + "");
        Float prix = Double.valueOf(commandeOK.get("prix") + "").floatValue();

        ACLMessage messageBDD = sendMessage(ACLMessage.REQUEST, QueryBuilder.getRefStock(idProduit, sender.getLocalName()), getBDDAgent(), true);


        try {
            JSONArray resultatsBDD = (JSONArray) this.parser.parse(messageBDD.getContent());
            if (resultatsBDD.size() == 0) {
                sendMessage(ACLMessage.INFORM, QueryBuilder.newStock(idProduit, quantite, prix, sender.getLocalName()), getBDDAgent(), true);
            } else {
                sendMessage(ACLMessage.INFORM, QueryBuilder.updateStock(idProduit, resultatsBDD.size() + quantite, prix, sender.getLocalName()), getBDDAgent(), true);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void clientChoisis(final JSONObject jeChoisis, AID sender) {
        String reference = jeChoisis.get("idProduit") + "";
        Integer quantite = Integer.valueOf(jeChoisis.get("quantite") + "");
        Float prix = Double.valueOf(jeChoisis.get("prix") + "").floatValue();
        long date = Long.valueOf(jeChoisis.get("date") + "");

        ACLMessage messageBDD = sendMessage(ACLMessage.REQUEST, QueryBuilder.getRefStock(reference, sender.getLocalName()), getBDDAgent(), true);

        try {
            JSONArray resultatsBDD = (JSONArray) this.parser.parse(messageBDD.getContent());
            if (resultatsBDD.size() == 0) {
                jeChoisis.put("raison", "stock insuffisant");
                sendMessage(ACLMessage.DISCONFIRM, (new JSONObject() {{
                    put("commandePasOK", jeChoisis);
                }}).toJSONString(), sender, true);
            } else {
                ACLMessage a = sendMessage(ACLMessage.REQUEST, QueryBuilder.vente(reference, quantite, prix, date, getLocalName(), sender.getLocalName()), getBDDAgent(), true);
                sendMessage(ACLMessage.REQUEST, QueryBuilder.updateStock(reference, quantite, getLocalName()), getBDDAgent(), true);
                sendMessage(ACLMessage.CONFIRM, (new JSONObject() {{
                    put("commandeOk", jeChoisis);
                }}).toJSONString(), sender, true);
                System.out.println(a);
                //TODO Notifier agentERep !!!!!
                //Erreur client jade.domain.FIPAAgentManagement.FailureException pour INFOS: (trinity) Message envoyé à Bob : {"jePropose":[{"date":"1449509503","idProduit":"2","prix":10.75,"nomProduit":"La ligne verte","quantite":1},{"date":"1449682303","idProduit":"2","prix":8.75,"nomProduit":"La ligne verte","quantite":1},{"date":"
            }
            nbNegoce = 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void clientNegocie(final JSONObject jeNegocie, AID sender) throws ParseException {
        String reference = jeNegocie.get("idProduit") + "";
        Double prix = Double.valueOf(jeNegocie.get("prix") + "");
        int max = 0;
        if (enSoldes) {
            max = 2;
        } else {
            max = 4;
        }
        if (nbNegoce < max) {
            nbNegoce++;
            ACLMessage messageBDD = sendMessage(ACLMessage.PROPOSE, QueryBuilder.rechercheRef(reference, getLocalName()), getBDDAgent(), true);
            JSONArray resultatsBDD = (JSONArray) this.parser.parse(messageBDD.getContent());

            for (Iterator iterator = resultatsBDD.iterator(); iterator.hasNext(); ) { //iterator sur chaque objet
                JSONObject resultat = (JSONObject) iterator.next();
                String refProd = (String) resultat.get("REF_PRODUIT");
                Double prixUProd = Double.valueOf(resultat.get("PRIX_UNITAIRE") + "");
                Double prixLProd = Double.valueOf(resultat.get("PRIX_LIMITE") + "");

                final JSONObject demande = new JSONObject();
                demande.put("type", "Produit");
                demande.put("id", refProd);
                ACLMessage desirabilite = sendMessage(ACLMessage.REQUEST, (new JSONObject() {{
                    put("demandeDesirabilite", demande);
                }}).toJSONString(), getERepAgent(), true);

                JSONObject resultats = (JSONObject) this.parser.parse(desirabilite.getContent());
                Double desir = 5.0;
                if (!(resultats.get("desirabilite") + "").contains("null")) {
                    desir = Double.valueOf(resultats.get("desirabilite") + "");
                }
                Double newPrice;
                if (enSoldes) {
                    newPrice = Normale.getNegoce(prix, nbNegoce, desir);
                    if (newPrice < prixLProd) {
                        newPrice = prixLProd;
                        nbNegoce = max;
                    }
                } else {
                    newPrice = Normale.getNegoce(prix, nbNegoce, desir);
                    if (newPrice < prixLProd - (max * 10 * prixUProd / 100)) {
                        newPrice = prixLProd - (max * 10 * prixUProd / 100);
                        nbNegoce = max;
                    }
                }

                jeNegocie.replace("prix", newPrice);
                sendMessage(ACLMessage.PROPOSE, (new JSONObject() {{
                    put("jeNegocie", jeNegocie);
                }}).toJSONString(), sender, true);
            }
        } else {
            ACLMessage messageBDD = sendMessage(ACLMessage.PROPOSE, QueryBuilder.rechercheRef(reference, getLocalName()), getBDDAgent(), true);
            JSONArray resultatsBDD = (JSONArray) this.parser.parse(messageBDD.getContent());

            for (Iterator iterator = resultatsBDD.iterator(); iterator.hasNext(); ) { //iterator sur chaque objet
                JSONObject resultat = (JSONObject) iterator.next();
                Double prixLProd = Double.valueOf(resultat.get("PRIX_LIMITE") + "");

                if (!enSoldes && prix < prixLProd) {
                    jeNegocie.put("raison", "je vend pas à perte !");
                    sendMessage(ACLMessage.DISCONFIRM, (new JSONObject() {{
                        put("commandePasOK", jeNegocie);
                    }}).toJSONString(), sender, true);
                } else {
                    clientChoisis(jeNegocie, sender);
                }
            }
        }
    }
}
