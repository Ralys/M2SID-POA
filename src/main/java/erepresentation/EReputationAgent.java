package erepresentation;

import common.SuperAgent;
import common.TypeAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Team EReputation
 */
public class EReputationAgent extends SuperAgent {
    
    /**
     * Méthode de mise en place de l'agent
     */
    @Override
    protected void setup() {
        this.registerService(TypeAgent.EReputation);
        
        this.addBehaviour(new CyclicBehaviour(this) {
            
            @Override
            public void action() {
                ACLMessage message = EReputationAgent.this.receive();
                
                if (message != null) {
                    System.out.println(myAgent.getLocalName() + "Message reçu : " + message.getContent() + " from " + message.getSender().getName());
                    EReputationAgent.this.traiterMessage(message);
                    block();
                }
            }
        });
    }

    @Override
    protected void traiterMessage(ACLMessage message) {
        String content = message.getContent();
        
        try {
            
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(content);
            
            if(object.containsKey("demandeAvis")) 
                this.traiterDemandeAvis(object, message.getSender());
            
            if(object.containsKey("donnerAvis"))
                this.traiterDonnerAvis(object, message.getSender());
            
        } catch (ParseException ex) {
            Logger.getLogger(EReputationAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void traiterDemandeAvis(JSONObject demandeAvis, AID agent) {
        // TODO
    }
    
    private void traiterDonnerAvis(JSONObject donnerAvis, AID agent) {
        // TODO
    }
    
}
