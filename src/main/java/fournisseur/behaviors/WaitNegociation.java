package fournisseur.behaviors;

import fournisseur.FournisseurAgent;
import fournisseur.utils.StocksEtTransaction;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tom
 */
public abstract class WaitNegociation extends CyclicBehaviour {

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) { //Négociation d'un client/vendeur
            String messageContent = msg.getContent();
            String sender = msg.getSender().getName();

            String receptionMessage = "(" + myAgent.getLocalName() + ") reçoit négociation : " + messageContent + "de" + sender;
            Logger.getLogger(FournisseurAgent.class.getName()).log(Level.INFO, receptionMessage);

            try {
                ACLMessage replyMessage = msg.createReply();
                //{“jeNegocie”:{”idProduit”:”67D”,”prix”:20.0,”date”:”20/02/2105”}}
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(msg.getContent());
                JSONObject nego = (JSONObject) object.get("jeNegocie");

                long date = Long.valueOf(nego.get("date").toString());
                int idProduit = Integer.valueOf(nego.get("idProduit").toString());
                double prix = Double.valueOf(nego.get("prix").toString());
                String nomProduit = nego.get("nomProduit").toString();
                int qte = Integer.valueOf(nego.get("quantite").toString());

                //Json réponse
                JSONObject replyJson = new JSONObject();
                JSONObject replyContenu = new JSONObject();
                double newPrix = this.définirNouveauPrix(idProduit, date, sender, prix);
                replyContenu.put("idProduit", idProduit);
                replyContenu.put("date", date);
                replyContenu.put("nomProduit", nomProduit);
                replyContenu.put("quantite", qte);
                if (newPrix <= prix) {
                    //Send commande OK
                    //{“commandeOk”:{“idProduit”:”67D”,”nomProduit”:”Spectre”,”quantite”:1,”prix”:17.3,”date”:”20/02/2105”}}
                    replyContenu.put("prix", newPrix);
                    replyJson.put("commandeOk", replyContenu);
                    replyMessage.setPerformative(ACLMessage.CONFIRM);
                    ((StocksEtTransaction) getDataStore()).removeTransaction(idProduit, date, sender);
                    ((StocksEtTransaction) getDataStore()).decrementerStock(idProduit, qte);

                    ((StocksEtTransaction) getDataStore()).changePesos(prix);
                } else {
                    //send je négocie
                    //{"jeNégocie": {"idProduit": "67D","prix": 20,"date": "20/02/2105",”nomProduit”:”Spectre”,”quantite”:1}}
                    replyContenu.put("prix", newPrix);
                    replyJson.put("jeNegocie", replyContenu);
                    replyMessage.setPerformative(ACLMessage.PROPOSE);
                }
                String contenuMessage = replyJson.toJSONString();
                replyMessage.setContent(contenuMessage);
                myAgent.send(replyMessage);
                //Log
                String envoiMessage = "(" + myAgent.getLocalName() + ") Message envoyé : " + contenuMessage + " : envoyé à " + msg.getSender().toString();
                Logger.getLogger(FournisseurAgent.class.getName()).log(Level.INFO, envoiMessage);
            } catch (ParseException ex) {
                Logger.getLogger(FournisseurAgent.class.getName()).log(Level.SEVERE, "Format de message invalide" + " de " + msg.getSender().toString());
            }
        } else {
            block();
        }
    }

    public abstract double définirNouveauPrix(int idProduit, Long date, String sender, double prixDemande);

}
