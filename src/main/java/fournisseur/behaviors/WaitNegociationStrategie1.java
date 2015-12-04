package fournisseur.behaviors;

import fournisseur.utils.Livraison;
import fournisseur.utils.StocksEtTransaction;
import fournisseur.utils.Transaction;

/**
 *
 * @author tom
 */
public class WaitNegociationStrategie1 extends WaitNegociation {

    private double margeBase = 1.10;
    private double reductionQte = 0.01;
    private double reductionNego = 0.01;

    @Override
    public double définirNouveauPrix(int idProduit, int delai, String sender, double prixDemande) {
        Transaction t = ((StocksEtTransaction) getDataStore()).getTransaction(idProduit, delai, sender);
        t.incNbNego();
        double prixBase = ((StocksEtTransaction) getDataStore()).getProduitById(idProduit).getPrixdeBase();
        int nbNego = Math.min(5, t.getNbNego());
        double reducNego = reductionNego * nbNego;
        return ((prixBase * (margeBase - reducNego)) * (1 - (reductionQte * t.getQte()))) + Livraison.prixLivraisonByDelai(delai);
    }

}
