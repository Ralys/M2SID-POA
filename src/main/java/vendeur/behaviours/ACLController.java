package vendeur.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
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

        //MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            //Réception
            String content = msg.getContent();

            try {
                JSONObject object = (JSONObject) this.parser.parse(content);

                System.out.println(content);

                if(object.containsKey("jeCherche")) {
                    vendeur.ClientRecherche((JSONObject) object.get("jeCherche"), msg.getSender(), "Cherche");
                } else if (object.containsKey("jeChercheRef")) {
                    vendeur.ClientRecherche((JSONObject) object.get("jeChercheRef"), msg.getSender(), "ChercheRef");
                } else if (object.containsKey("jePropose")) {
                    vendeur.fournisseurPropose((JSONObject) object.get("jePropose"), msg.getSender());
                }

            } catch (ParseException ex) {
                Logger.getLogger(myAgent.getLocalName()).log(Level.WARNING, "Format de message invalide");
            }
        }
    }



}
