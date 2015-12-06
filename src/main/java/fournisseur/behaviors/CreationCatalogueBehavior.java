package fournisseur.behaviors;

import fournisseur.utils.Produit;
import fournisseur.utils.StocksEtTransaction;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.HashMap;
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
public class CreationCatalogueBehavior extends OneShotBehaviour {

    private int numFournisseur;
    private AID agentBDD;

    public CreationCatalogueBehavior(Agent a, int numFournisseur, AID agentBDD) {
        super(a);
        this.numFournisseur = numFournisseur;
        this.agentBDD = agentBDD;
    }

    private ArrayList<Integer> getProduitFournisseur() throws ParseException {
        String SQL = "SELECT REF_PRODUIT FROM FOURNISSEUR_PRODUIT WHERE ID_FOURNISSEUR = " + numFournisseur;
        JSONArray tabIdProduit = sendRequete(SQL);

        ArrayList<Integer> listProduitFournisseur = new ArrayList<>();
        for (Object tabIdProduit1 : tabIdProduit) {
            JSONObject jsonProduit = (JSONObject) tabIdProduit1;
            int idProduit = Integer.valueOf(jsonProduit.get("REF_PRODUIT").toString());
            listProduitFournisseur.add(idProduit);
        }

        return listProduitFournisseur;
    }

    private HashMap<Integer, Produit> getProduit() throws ParseException {
        String SQL = "SELECT REF_PRODUIT,NOM_PRODUIT,DATE_SORTIE,NOM_CATEGORIE,PRIX_CREATION "
                + "FROM PRODUIT,CATEGORIE "
                + "WHERE CATEGORIE.ID_CATEGORIE=PRODUIT.ID_CATEGORIE";
        JSONArray tabProduit = sendRequete(SQL);
        HashMap<Integer, Produit> listProduit = new HashMap<>();
        for (Object tabProduit1 : tabProduit) {
            JSONObject jsonProduit = (JSONObject) tabProduit1;
            int idProduit = Integer.valueOf(jsonProduit.get("REF_PRODUIT").toString());
            double prixProduit = Double.valueOf(jsonProduit.get("PRIX_CREATION").toString());
            long date = Long.valueOf(jsonProduit.get("DATE_SORTIE").toString());
            String nomProduit = jsonProduit.get("NOM_PRODUIT").toString();
            String typeProduit = jsonProduit.get("NOM_CATEGORIE").toString();

            //Récupération des tags
            ArrayList<String> listTag = new ArrayList<>();
            SQL = "SELECT LABEL_TAG "
                    + "FROM POSSEDE,TAGS "
                    + "WHERE POSSEDE.ID_TAG=TAGS.ID_TAG AND REF_PRODUIT=" + idProduit;
            JSONArray tabTag = sendRequete(SQL);
            for (Object tagObject : tabTag) {
                JSONObject jsonTag = (JSONObject) tagObject;
                String tag = jsonTag.get("LABEL_TAG").toString();
                listTag.add(tag);
            }

            Produit p = new Produit(idProduit, nomProduit, prixProduit, typeProduit, date, listTag);
            listProduit.put(idProduit, p);
        }
        return listProduit;
    }

    private JSONArray sendRequete(String sql) throws ParseException {
        JSONObject request = new JSONObject();
        request.put("type", "select");
        request.put("sql", sql);

        ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
        req.addReceiver(agentBDD);
        req.setContent(request.toJSONString());
        myAgent.send(req);

        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage reponse = myAgent.blockingReceive(mt, 3000);
        JSONParser parser = new JSONParser();
        return (JSONArray) parser.parse(reponse.getContent());

    }

    @Override
    public void action() {
        try {
            HashMap<Integer, Produit> listProduit = this.getProduit();
            ArrayList<Integer> listProduitFournisseur = this.getProduitFournisseur();
            for (Integer numProduit : listProduitFournisseur) {
                getDataStore().put(listProduit.get(numProduit), 0);
            }
            ((StocksEtTransaction) getDataStore()).initPesos(100000);// set du montant initial de pesos
        } catch (ParseException ex) {
            Logger.getLogger(CreationCatalogueBehavior.class.getName()).log(Level.SEVERE, "Format de message BDD incorrect");
        }
    }

}
