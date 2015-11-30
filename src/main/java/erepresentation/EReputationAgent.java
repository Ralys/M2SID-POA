package erepresentation;

import common.SuperAgent;
import common.TypeAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

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
        // TODO comportement traiter des messages
    }
    
}
