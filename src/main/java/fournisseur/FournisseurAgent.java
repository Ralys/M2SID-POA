package fournisseur;

import common.SuperAgent;
import common.TypeAgent;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Tom
 */
public class FournisseurAgent extends SuperAgent {
    
    
    
     /**
     * Méthode de mise en place de l'agent
     */
    @Override
    protected void setup() {
        this.registerService(TypeAgent.Fournisseur);
        
            
    }
    
    @Override
    protected void traiterMessage(ACLMessage message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
   
    
}
