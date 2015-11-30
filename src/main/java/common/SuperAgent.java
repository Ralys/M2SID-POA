package common;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Mazzei Stéphane
 */
public abstract class SuperAgent extends Agent {

    /**
     * Enregistrement des services
     * @param type, le type de son agent (TypeAgent)
     */
    protected void registerService(String type) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        sd.setName(type);

        dfd.addServices(sd);
        
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            System.err.println(getLocalName() + " registration with DF unsucceeded. Reason: " + e.getMessage());
            doDelete();
        }
    }
    
    /**
     * Méthode implémentant le comportement personnalisé pour les messages reçus
     * @param message, message reçu
     */
    protected abstract void traiterMessage(ACLMessage message);
}
