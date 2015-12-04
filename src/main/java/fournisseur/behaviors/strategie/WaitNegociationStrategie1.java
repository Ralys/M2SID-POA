package fournisseur.behaviors.strategie;

import fournisseur.behaviors.WaitNegociation;
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
    public double définirNouveauPrix(int idProduit, Long date, String sender, double prixDemande) {
        Transaction t = ((StocksEtTransaction) getDataStore()).getTransaction(idProduit, date, sender);
        t.incNbNego();
        int delai = t.getDelai();
        double prixBase = ((StocksEtTransaction) getDataStore()).getProduitById(idProduit).getPrixdeBase();
        int nbNego = Math.min(5, t.getNbNego());
        double reducNego = reductionNego * nbNego;
        double prix = ((prixBase * (margeBase - reducNego)) * (1 - (reductionQte * t.getQte()))) + Livraison.prixLivraisonByDelai(delai);
        return Math.round(prix * 100) / 100;
    }

}
