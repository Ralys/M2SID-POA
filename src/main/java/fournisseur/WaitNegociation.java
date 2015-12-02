package fournisseur;

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
public class WaitNegociation extends CyclicBehaviour{

    @Override
    public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) { //Négociation d'un client/vendeur
            try {
                ACLMessage replyMessage = msg.createReply();
                //{“jeNegocie”:{”idProduit”:”67D”,”prix”:20.0,”date”:”20/02/2105”}}
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(msg.getContent());
                JSONObject achat = (JSONObject) object.get("jeNegocie");
                
                int idProduit = Integer.valueOf(achat.get("idProduit").toString());
                int prix = Integer.valueOf(achat.get("prix").toString());
                
                
                //Json réponse
                JSONObject replyJson = new JSONObject();
                JSONObject replyContenu = new JSONObject();

     
                String contenuMessage = replyJson.toJSONString();
                replyMessage.setContent(contenuMessage);
                myAgent.send(replyMessage);
                //Log
                String envoiMessage = "(" + myAgent.getLocalName() + ") Message envoyé : " + contenuMessage;
                Logger.getLogger(FournisseurAgent.class.getName()).log(Level.INFO, envoiMessage);
            } catch (ParseException ex) {
                Logger.getLogger(FournisseurAgent.class.getName()).log(Level.SEVERE, "Format de message invalide" );
            }
        } else {
            block();
        }
    }
    
}
