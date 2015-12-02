
package ereputation.behaviours;

import common.TypeAgent;
import ereputation.EReputationAgent;
import ereputation.tools.QueryBuilder;
import jade.core.AID;
import jade.core.Agent;
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
 * @author Team EReputation
 */
public class InformBehaviour extends CyclicBehaviour {
    private JSONParser parser;
    
    public InformBehaviour(Agent agent) {
        super(agent);
        this.parser = new JSONParser();
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage message = myAgent.receive(mt);
        if(message == null) return;
        
        String receptionMessage = "(" + myAgent.getLocalName() + ") Message reçu : " + message.getContent().replace("\n", "").replace("\t", "") + " de " + message.getSender().getName();
        Logger.getLogger(myAgent.getLocalName()).log(Level.INFO, receptionMessage);
        
        traiterInformation(message);
        block();
    }
    
    private void traiterInformation(ACLMessage message) {
        String content = message.getContent();
        
        try {
            JSONObject object = (JSONObject) this.parser.parse(content);
            
            if(object.containsKey("donneAvis")) 
                this.donneAvis((JSONObject)object.get("donneAvis"), message.getSender());
            
        } catch (ParseException ex) {
            Logger.getLogger(myAgent.getLocalName()).log(Level.WARNING, "Format de message invalide");
        }
    }
    
    private void donneAvis(JSONObject donneAvis, AID agent) throws ParseException {
        String nom = "";
        String type = donneAvis.get("type").toString();
        
        Long avis = (Long) donneAvis.get("avis");
        
        switch(type) {
            case TypeAgent.Fournisseur:
            case TypeAgent.Vendeur:
                nom = donneAvis.get("nom").toString();
                break;
            
            case EReputationAgent.Produit:
                nom = donneAvis.get("ref").toString();
                break;
        }
        
        EReputationAgent erep = (EReputationAgent)myAgent;
        
        // ajout en base de données
        erep.sendMessage(ACLMessage.INFORM, QueryBuilder.insertAvis(agent.getLocalName(), type+"_"+nom, avis), erep.getBDDAgent());
    }
}
