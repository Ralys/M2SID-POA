package ereputation;

import common.SuperAgent;
import common.TypeAgent;
import common.TypeLog;
import ereputation.behaviours.HandleInform;
import ereputation.behaviours.HandleRequest;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * L'agent E-réputation
 * @author Team E-réputation
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
        this.addBehaviour(new HandleRequest(this));
        this.addBehaviour(new HandleInform(this));
    }
    
    /**
     * Méthode lorsque l'agent a été "éliminé"
     */
    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException ex) {
            TypeLog.logEreputation.Erreur(EReputationAgent.class.getName()+":"+ex.getMessage());
            Logger.getLogger(EReputationAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Permet d'obtenir l'adresse de l'agent BDD
     * @return l'adresse de l'agent BDD
     */
    public AID getBDDAgent() {
        if(this.BDDAgent == null) {
            AID[] agents = this.findAgentsFromService(TypeAgent.BDD);
            
            if(agents != null) {
                this.BDDAgent = agents[0];
            }
        }
        
        return this.BDDAgent;
    }
    
    /**
     * Surcharge de la méthode sendMessage
     * @param typeMessage Inform | Request
     * @param contenu le contenu JSON du message
     * @param destinataire l'agent destinaire
     * @return null
     */
    public ACLMessage sendMessage(int typeMessage, String contenu, AID destinataire) {
        return this.sendMessage(typeMessage, contenu, destinataire, false);
    }
    
    /**
     * 
     * @param typeMessage Inform | Request
     * @param contenu le contenu JSON du message
     * @param destinataire l'agent destinaire
     * @param withResponse si on souhaite attendre et récupérer une réponse
     * @return null | un message de réponse
     */
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
