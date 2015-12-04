package fournisseur;

import fournisseur.utils.StocksEtTransaction;
import fournisseur.behaviors.CreationCatalogueBehavior;
import fournisseur.behaviors.WaitAchat;
import fournisseur.behaviors.strategie.WaitRequestStrategie1;
import common.SuperAgent;
import common.TypeAgent;
import fournisseur.behaviors.GestionStockBehavior;
import fournisseur.behaviors.WaitNegociation;
import fournisseur.behaviors.WaitRequest;
import fournisseur.behaviors.strategie.WaitNegociationStrategie1;
import fournisseur.behaviors.strategie.WaitNegociationStrategie2;
import fournisseur.behaviors.strategie.WaitNegociationStrategie3;
import fournisseur.behaviors.strategie.WaitRequestStrategie2;
import fournisseur.behaviors.strategie.WaitRequestStrategie3;
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
        
        //Parametre : numero du fournisseur
        AID agentBDD = this.findAgentsFromService(TypeAgent.BDD)[0];
        Object[] tabParam = this.getArguments();
        int numFournisseur = Integer.valueOf((String) tabParam[0]);

        //Choix de la stratégie
        WaitRequest waitRequestBehaviorStrategie = null;
        WaitNegociation waitNegociationBehaviorStrategie = null;
        switch (numFournisseur) {
            case 1:
                waitRequestBehaviorStrategie = new WaitRequestStrategie1();
                waitNegociationBehaviorStrategie = new WaitNegociationStrategie1();
                break;
            case 2:
                waitRequestBehaviorStrategie = new WaitRequestStrategie2();
                waitNegociationBehaviorStrategie = new WaitNegociationStrategie2();
                break;
            case 3:
                waitRequestBehaviorStrategie = new WaitRequestStrategie3();
                waitNegociationBehaviorStrategie = new WaitNegociationStrategie3();
                break;
            default:
                Logger.getLogger(FournisseurAgent.class.getName()).log(Level.SEVERE, "Paramètre incorrect");
                this.takeDown();
        }
        
        //Comportement création d'un catalogue
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
        waitRequestBehaviorStrategie.setDataStore(catalogue);
        this.addBehaviour(waitRequestBehaviorStrategie);

        //Comportement de la négociation
        waitNegociationBehaviorStrategie.setDataStore(catalogue);
        this.addBehaviour(waitNegociationBehaviorStrategie);

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
