package ereputation;

import common.SuperAgent;
import common.TypeAgent;
import ereputation.behaviours.InformBehaviour;
import ereputation.behaviours.RequestBehaviour;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Team EReputation
 */
public class EReputationAgent extends SuperAgent {
    
    public static final String Produit = "Produit";
    
    private AID BDDAgent;
    
    /**
     * Méthode de mise en place de l'agent
     */
    @Override
    protected void setup() {
        this.registerService(TypeAgent.EReputation);
       
        this.addBehaviour(new RequestBehaviour(this));
        this.addBehaviour(new InformBehaviour(this));
    }
    
    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException ex) {
            Logger.getLogger(EReputationAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public AID getBDDAgent() {
        if(this.BDDAgent == null) {
            this.BDDAgent = findBDDAgent();
        }
        
        return this.BDDAgent;
    }
    
    private AID findBDDAgent() {
        // temporaire
        return new AID("BDD", AID.ISLOCALNAME);
    }
    
    public ACLMessage sendMessage(int typeMessage, String contenu, AID destinataire) {
        return this.sendMessage(typeMessage, contenu, destinataire, false);
    }
    
    public ACLMessage sendMessage(int typeMessage, String contenu, AID destinataire, boolean withResponse) {
        ACLMessage messsage = new ACLMessage(typeMessage);
        messsage.setContent(contenu);
        messsage.addReceiver(destinataire);
        this.send(messsage);
        
        if(!withResponse) return null;
        
        MessageTemplate mt = MessageTemplate.MatchSender(destinataire);
        ACLMessage messageReponse = this.blockingReceive(mt);
        return messageReponse;
    }
}
