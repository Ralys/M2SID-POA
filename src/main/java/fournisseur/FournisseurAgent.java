package fournisseur;

import fournisseur.behaviors.CreationCatalogueBehavior;
import fournisseur.behaviors.WaitAchat;
import fournisseur.behaviors.WaitRequestStrategie1;
import common.SuperAgent;
import common.TypeAgent;
import fournisseur.behaviors.GestionStockBehavior;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tom
 */
public class FournisseurAgent extends SuperAgent {

    private StocksEtTransaction catalogue = new StocksEtTransaction();

    /**
     * Méthode de mise en place de l'agent
     */
    @Override
    protected void setup() {
        this.registerService(TypeAgent.Fournisseur);

        //Comportement création d'un catalogue
        AID agentBDD = this.findAgentsFromService(TypeAgent.BDD)[0];
        Object[] tabParam = this.getArguments();
        int numFournisseur = Integer.valueOf((String) tabParam[0]);
        CreationCatalogueBehavior creationCatalogueBehavior = new CreationCatalogueBehavior(this, numFournisseur, agentBDD);
        creationCatalogueBehavior.setDataStore(catalogue);
        this.addBehaviour(creationCatalogueBehavior);

        //Gestion du stock
        GestionStockBehavior gestionStockBehavior = new GestionStockBehavior(this);
        gestionStockBehavior.setDataStore(catalogue);
        this.addBehaviour(gestionStockBehavior);

        //Comportement Attente d'un achat
        WaitAchat waitAchatBehavior = new WaitAchat();
        waitAchatBehavior.setDataStore(catalogue);
        this.addBehaviour(waitAchatBehavior);

        //Comportement attente d'une requete
        WaitRequestStrategie1 waitRequestBehaviorStrategie = new WaitRequestStrategie1();
        waitRequestBehaviorStrategie.setDataStore(catalogue);
        this.addBehaviour(waitRequestBehaviorStrategie);

    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
            Logger.getLogger(this.getLocalName()).log(Level.INFO, "Fin de l'agent ! " + this.getName());
        } catch (FIPAException ex) {
            Logger.getLogger(FournisseurAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
