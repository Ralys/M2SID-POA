package vendeur.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import vendeur.VendeurAgent;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by yann on 12/6/2015.
 */
public class ACLController extends CyclicBehaviour {

    private final JSONParser parser;

    public ACLController(VendeurAgent aThis) {
        super(aThis);
        this.parser = new JSONParser();
    }

    @Override
    public void action() {
        VendeurAgent vendeur = (VendeurAgent) myAgent;

        //vendeur.CheckStock();

        //MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        ACLMessage msg = myAgent.receive();


        while (msg != null) {
            //Réception
            String content = msg.getContent();

            try {
                JSONObject object = (JSONObject) this.parser.parse(content);

                Logger.getLogger(vendeur.getLocalName()).log(Level.INFO, "("+vendeur.getLocalName()+") Message reçu de "+msg.getSender().getLocalName() +" : "+content);

                if(object.containsKey("jeCherche")) {
                    vendeur.clientRecherche((JSONObject) object.get("jeCherche"), msg.getSender(), "Cherche");
                }
                else if (object.containsKey("jeChercheRef")) {
                    vendeur.clientRecherche((JSONObject) object.get("jeChercheRef"), msg.getSender(), "ChercheRef");
                }
                else if(object.containsKey("jeChoisis")) {
                    vendeur.clientChoisis((JSONObject) object.get("jeChoisis"), msg.getSender());
                }
                else if(object.containsKey("jeNégocie")) {
                    vendeur.clientNegocie((JSONObject) object.get("jeNégocie"), msg.getSender());
                }
                else if (object.containsKey("jePropose")) {
                    if(object.get("jePropose") instanceof JSONObject)
                        vendeur.fournisseurPropose((JSONObject) object.get("jePropose"), msg.getSender());
                    else if(object.get("jePropose") instanceof JSONArray)
                        vendeur.fournisseurPropose((JSONArray) object.get("jePropose"), msg.getSender());
                }
                else if (object.containsKey("commandeOk")) {
                    vendeur.ajoutStock((JSONObject) object.get("commandeOk"), msg.getSender());
                }

            } catch (ParseException ex) {
                Logger.getLogger(myAgent.getLocalName()).log(Level.WARNING, "("+vendeur.getLocalName()+") Format de message invalide");
            }

            msg = myAgent.receive();
        }
    }



}
