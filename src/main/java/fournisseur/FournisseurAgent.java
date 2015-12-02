package fournisseur;

import fournisseur.behaviors.WaitAchat;
import fournisseur.behaviors.WaitRequestStrategie1;
import common.SuperAgent;
import common.TypeAgent;

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

        WaitAchat waitAchatBehavior = new WaitAchat();
        waitAchatBehavior.setDataStore(catalogue);
        this.addBehaviour(waitAchatBehavior);

        WaitRequestStrategie1 WaitRequestBehaviorStrategie = new WaitRequestStrategie1();
        WaitRequestBehaviorStrategie.setDataStore(catalogue);
        this.addBehaviour(WaitRequestBehaviorStrategie);

        this.creationCatalogue();

    }

    private void creationCatalogue() {
        //Ajout dans le catalogue par la BDD
        Produit p = new Produit(0, "Chibre", 5.0, "CD");
        catalogue.put(p, 0);
    }

}
