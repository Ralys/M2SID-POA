package common;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

/**
 *
 * @author Mazzei Stéphane
 */
public class SuperAgent extends Agent {

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
    
    protected AID[] findAgentsFromService(String service) {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(service);
        dfd.addServices(sd);

        SearchConstraints ALL = new SearchConstraints();
        ALL.setMaxResults(new Long(-1));

        AID[] agents = null;
        
        try {
            DFAgentDescription[] result = DFService.search(this, dfd, ALL);
            agents = new AID[result.length];
            
            for (int i = 0; i < result.length; i++) {
                agents[i] = result[i].getName();
            }
            
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        return agents;
    }
}
