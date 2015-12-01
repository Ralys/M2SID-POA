package fournisseur;

import common.SuperAgent;
import common.TypeAgent;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Tom
 */
public class FournisseurAgent extends SuperAgent {

    private DataStore catalogue = new DataStore();

    /**
     * Méthode de mise en place de l'agent
     */
    @Override
    protected void setup() {
        this.registerService(TypeAgent.Fournisseur);
        
        WaitAchat waitAchatBehavior = new WaitAchat();
        waitAchatBehavior.setDataStore(catalogue);
        this.addBehaviour(waitAchatBehavior);
        
        WaitRequestStrategie1 WaitRequestBehaviorStrategie = new WaitRequestStrategie1();
        WaitRequestBehaviorStrategie.setDataStore(catalogue);
        this.addBehaviour(WaitRequestBehaviorStrategie);
        
    }

    @Override
    protected void traiterMessage(ACLMessage message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



}
