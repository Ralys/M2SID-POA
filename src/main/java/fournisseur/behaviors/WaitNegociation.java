package fournisseur.behaviors;

import fournisseur.FournisseurAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private final SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
        ACLMessage msg = myAgent.receive(mt);
        String messageContent = msg.getContent();
        String sender = msg.getSender().toString();
        
        String receptionMessage = "(" + myAgent.getLocalName() + ") reçoit négociation : " + messageContent + "de" + sender;
        Logger.getLogger(FournisseurAgent.class.getName()).log(Level.INFO, receptionMessage);
        
        if (msg != null) { //Négociation d'un client/vendeur
            try {
                ACLMessage replyMessage = msg.createReply();
                //{“jeNegocie”:{”idProduit”:”67D”,”prix”:20.0,”date”:”20/02/2105”}}
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(msg.getContent());
                JSONObject achat = (JSONObject) object.get("jeNegocie");

                Date date = formater.parse(achat.get("date").toString());

                int idProduit = Integer.valueOf(achat.get("idProduit").toString());
                double prix = Double.valueOf(achat.get("prix").toString());

                //Json réponse
                JSONObject replyJson = new JSONObject();
                JSONObject replyContenu = new JSONObject();

                String contenuMessage = replyJson.toJSONString().replace("\\", "");
                replyMessage.setContent(contenuMessage);
                myAgent.send(replyMessage);
                //Log
                String envoiMessage = "(" + myAgent.getLocalName() + ") Message envoyé : " + contenuMessage + " : envoyé à " + msg.getSender().toString();
                Logger.getLogger(FournisseurAgent.class.getName()).log(Level.INFO, envoiMessage);
            } catch (ParseException ex) {
                Logger.getLogger(FournisseurAgent.class.getName()).log(Level.SEVERE, "Format de message invalide" + " de " + msg.getSender().toString());
            } catch (java.text.ParseException ex) {
                Logger.getLogger(WaitNegociation.class.getName()).log(Level.SEVERE, "Format de date incorrect" + ex.getLocalizedMessage() + " de " + msg.getSender().toString());
            }
        } else {
            block();
        }
    }

    public abstract double définirNouveauPrix(int idProduit, int delai, String sender);

}