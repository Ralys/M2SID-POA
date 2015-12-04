package fournisseur.behaviors.strategie;

import fournisseur.behaviors.WaitNegociation;

/**
 *
 * @author Tom
 */
public class WaitNegociationStrategie3 extends WaitNegociation {

    @Override
    public double définirNouveauPrix(int idProduit, int delai, String sender, double prixDemande) {
        return -1;
    }

}
