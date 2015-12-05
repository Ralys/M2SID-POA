package fournisseur.behaviors.strategie;

import fournisseur.behaviors.WaitNegociation;
import jade.core.AID;

/**
 *
 * @author Tom
 */
public class WaitNegociationStrategie3 extends WaitNegociation {

    private AID erep;

    public WaitNegociationStrategie3(AID agentERep) {
        super();
        this.erep = agentERep;
    }

    @Override
    public double définirNouveauPrix(int idProduit, Long date, String sender, double prixDemande) {
        return -1;
    }

}
