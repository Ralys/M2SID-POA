package fournisseur.behaviors;

import fournisseur.FournisseurAgent;
import fournisseur.Stocks;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WaitAchat extends CyclicBehaviour {

    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
        ACLMessage msg = myAgent.receive(mt);
        
        String messageContent = msg.getContent();
        String sender = msg.getSender().toString();

        String receptionMessage = "(" + myAgent.getLocalName() + ") reçoit achat : " + messageContent + "de" + sender;
        Logger.getLogger(FournisseurAgent.class.getName()).log(Level.INFO, receptionMessage);

        if (msg != null) { //Acceptation d'un achat reçu
            try {
                ACLMessage replyMessage = msg.createReply();
                //{“jeChoisis”:{”idProduit”:”67D”,”nomProduit”:”Spectre”,”quantite”:3,”prix”:20.0,”date”:”20/02/2105”}}
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(messageContent);
                JSONObject achat = (JSONObject) object.get("jeChoisis");
                int idProduit = Integer.valueOf(achat.get("idProduit").toString());
                String nomProduit = achat.get("nomProduit").toString();
                String date = achat.get("date").toString();
                int quantite = Integer.valueOf(achat.get("quantite").toString());
                double prix = Double.valueOf(achat.get("prix").toString());

                //Vérifier les stocks
                boolean stockOk = ((Stocks) getDataStore()).verifierStock(idProduit, quantite);

                //Json réponse
                JSONObject replyJson = new JSONObject();
                JSONObject replyContenu = new JSONObject();

                if (stockOk) {
                    //{“commandeOk”:{“idProduit”:”67D”,”nomProduit”:”Spectre”,”quantite”:1,”prix”:17.3,”date”:”20/02/2105”}}
                    replyContenu.put("idProduit", idProduit);
                    replyContenu.put("nomProduit", nomProduit);
                    replyContenu.put("prix", prix);
                    replyContenu.put("quantite", quantite);
                    replyContenu.put("date", date);
                    replyJson.put("commandeOK", replyContenu);
                    replyMessage.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                } else {
                    //{“commandePasOK”:{“raison”:”Stock insuffisant”}}
                    replyJson.put("commandePasOK", replyContenu);
                    replyContenu.put("raison", "Stock insuffisant");
                    replyMessage.setPerformative(ACLMessage.REFUSE);
                }
                String contenuMessage = replyJson.toJSONString().replace("\\", "");
                replyMessage.setContent(contenuMessage);
                myAgent.send(replyMessage);
                //Log
                String envoiMessage = "(" + myAgent.getLocalName() + ") Message envoyé : " + contenuMessage + " : envoyé à "+sender;
                Logger.getLogger(FournisseurAgent.class.getName()).log(Level.INFO, envoiMessage);
            } catch (ParseException ex) {
                Logger.getLogger(FournisseurAgent.class.getName()).log(Level.SEVERE, "Format de message invalide");
            }
        } else {
            block();
        }
    }
}
