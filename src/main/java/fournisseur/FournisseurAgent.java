package fournisseur;

import common.SuperAgent;
import common.TypeAgent;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Tom
 */
public class FournisseurAgent extends SuperAgent {

    private Stocks catalogue = new Stocks();

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

}
