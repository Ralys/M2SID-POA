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
import fournisseur.behaviors.strategie.GestionStockBehaviorDesir;
import fournisseur.behaviors.strategie.GestionStockBehaviorNormal;
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

    private StocksEtTransaction catalogue;

    /**
     * Méthode de mise en place de l'agent
     */
    @Override
    protected void setup() {
        this.catalogue = new StocksEtTransaction();
        this.registerService(TypeAgent.Fournisseur);
        AID agentBDD = null;
        //Parametre : numero du fournisseur
        try {
            agentBDD = this.findAgentsFromService(TypeAgent.BDD)[0];
        } catch (IndexOutOfBoundsException io) {
            Logger.getLogger(FournisseurAgent.class.getName()).log(Level.SEVERE, "L'agent BDD doit être lancé !");
            this.takeDown();
        }
        Object[] tabParam = this.getArguments();
        int numFournisseur = 0;
        try {
            numFournisseur = Integer.valueOf((String) tabParam[0]);
        } catch (IndexOutOfBoundsException io) {
            Logger.getLogger(FournisseurAgent.class.getName()).log(Level.SEVERE, "Veuillez insérer un paramètre : nomAgent:fournisseur.FournisseurAgent(1)");
            this.takeDown();
        }
        //Choix de la stratégie
        WaitRequest waitRequestBehaviorStrategie = null;
        WaitNegociation waitNegociationBehaviorStrategie = null;
        GestionStockBehavior gestionStockBehavior = null;
        switch (numFournisseur) {
            case 1:
                waitRequestBehaviorStrategie = new WaitRequestStrategie1();
                waitNegociationBehaviorStrategie = new WaitNegociationStrategie1();
                gestionStockBehavior = new GestionStockBehaviorNormal(this);
                break;
            case 2:
                waitRequestBehaviorStrategie = new WaitRequestStrategie2();
                waitNegociationBehaviorStrategie = new WaitNegociationStrategie2();
                gestionStockBehavior = new GestionStockBehaviorNormal(this);
                break;
            case 3:
                AID agentERep = null;
                try {
                    agentERep = this.findAgentsFromService(TypeAgent.EReputation)[0];
                } catch (IndexOutOfBoundsException e) {
                    Logger.getLogger(FournisseurAgent.class.getName()).log(Level.SEVERE, "L'agent E-réputation doit être lancé");
                    this.takeDown();
                }
                gestionStockBehavior = new GestionStockBehaviorDesir(this, agentERep);
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
