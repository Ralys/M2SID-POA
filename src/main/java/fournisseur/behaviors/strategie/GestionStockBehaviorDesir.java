/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fournisseur.behaviors.strategie;

import fournisseur.FournisseurAgent;
import fournisseur.behaviors.GestionStockBehavior;
import fournisseur.utils.Produit;
import fournisseur.utils.StocksEtTransaction;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tom
 */
public class GestionStockBehaviorDesir extends GestionStockBehavior {

    private AID erep;

    public GestionStockBehaviorDesir(Agent a, AID erep) {
        super(a);
        this.erep = erep;
    }

    public void creationproduit() {
        try {
            ArrayList<Produit> listProduit = ((StocksEtTransaction) this.getDataStore()).listProduit();
            for (Produit produit : listProduit) { //Mis a jour de la désirabilité des produits
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(erep);

                JSONObject replyJson = new JSONObject();
                JSONObject replyContenu = new JSONObject();
                replyContenu.put("reference", produit.getIdProduit());
                replyContenu.put("type", "OsefOnSenSertPas");
                replyJson.put("demandeDesirabilite", replyContenu);

                msg.setContent(replyJson.toJSONString());
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                ACLMessage response = myAgent.receive(mt);

                String messageContent = response.getContent();
                String sender = response.getSender().getName();
                String receptionMessage = "(" + myAgent.getLocalName() + ") reçoit désirabilité : " + messageContent + "de" + sender;
                Logger.getLogger(FournisseurAgent.class.getName()).log(Level.INFO, receptionMessage);
                JSONParser parser = new JSONParser();

                JSONObject repObject = (JSONObject) parser.parse(messageContent);
                JSONObject repObjectContent = (JSONObject) repObject.get("desirabilite");
                double desir = Double.valueOf(repObjectContent.get("retourDesirabilite").toString());
                produit.setDesirabilite(desir);
                ((StocksEtTransaction) this.getDataStore()).put(produit, ((StocksEtTransaction) this.getDataStore()).get(produit));
            }
            Collections.sort(listProduit);
            int stockUse = ((StocksEtTransaction) this.getDataStore()).stockUse();
            int nbProduitACreer = (stockMax - stockUse) / 50; //On ne crée pas moins de 50 produits
            //Pour les 5 produits les plus désiré crée un maximum de produit
            for (int i = 0; i < nbProduitACreer; i++) {
                this.dataStoreOperation(listProduit.get(i), 50);
            }

        } catch (ParseException ex) {
            Logger.getLogger(WaitRequestStrategie3.class.getName()).log(Level.SEVERE, "Format du message incorrect");
        }
    }
}
