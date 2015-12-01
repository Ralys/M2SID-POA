package fournisseur;

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
        if (msg != null) { //Acceptation d'un achat reçu
            try {
                ACLMessage replyMessage = msg.createReply();
                //{“jeChoisis”:{”idProduit”:”67D”,”nomProduit”:”Spectre”,”quantite”:3,”prix”:20.0,”date”:”20/02/2105”}}
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(msg.getContent());
                JSONObject achat = (JSONObject) object.get("jeChoisis");
                String idProduit = achat.get("idProduit").toString();
                String nomProduit = achat.get("nomProduit").toString();
                String date = achat.get("date").toString();
                int quantite = Integer.valueOf(achat.get("quantite").toString());
                int prix = Integer.valueOf(achat.get("prix").toString());

                //Vérifier les stocks
                boolean stockOk = true;
                //TODO
                
                //Json réponse
                JSONObject replyJson = new JSONObject();
                JSONObject replyContenu = new JSONObject();

                if (stockOk) {
                    //{“commandeOk”:{“idProduit”:”67D”,”nomProduit”:”Spectre”,”quantite”:1,”prix”:17.3,”date”:”20/02/2105”}}
                    replyJson.put("commandeOK", replyContenu);
                    replyContenu.put("idProduit", idProduit);
                    replyContenu.put("nomProduit", nomProduit);
                    replyContenu.put("prix", prix);
                    replyContenu.put("quantite", quantite);
                    replyContenu.put("date", date);
                    replyMessage.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                } else {
                    //{“commandePasOK”:{“raison”:”Stock insuffisant”}}
                    replyJson.put("commandePasOK", replyContenu);
                    replyContenu.put("raison", "Stock insuffisant");
                    replyMessage.setPerformative(ACLMessage.REFUSE);
                }
                String contenuMessage = replyJson.toJSONString();
                replyMessage.setContent(contenuMessage);
                myAgent.send(replyMessage);
                //Log
                String envoiMessage = "(" + myAgent.getLocalName() + ") Message envoyé : " + contenuMessage;
                Logger.getLogger(FournisseurAgent.class.getName()).log(Level.INFO, envoiMessage);
            } catch (ParseException ex) {
                Logger.getLogger(FournisseurAgent.class.getName()).log(Level.SEVERE, null, "Format de message invalide");
            }
        } else {
            block();
        }
    }
}
