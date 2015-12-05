package fournisseur.behaviors.strategie;

import fournisseur.behaviors.WaitNegociation;
import fournisseur.utils.Livraison;
import fournisseur.utils.StocksEtTransaction;
import fournisseur.utils.Transaction;

/**
 *
 * @author Tom
 */
public class WaitNegociationStrategie3 extends WaitNegociation {

    private double marge = 1.05;

    public WaitNegociationStrategie3() {
        super();
    }

    @Override
    public double définirNouveauPrix(int idProduit, Long date, String sender, double prixDemande) {
        Transaction t = ((StocksEtTransaction) getDataStore()).getTransaction(idProduit, date, sender);
        t.incNbNego();
        double prixBase = ((StocksEtTransaction) getDataStore()).getProduitById(idProduit).getPrixDeBase();
        double prix = ((prixBase * marge)) + Livraison.prixLivraisonByDelai(t.getDelai());
        return Math.ceil(prix * 100) / 100;
    }

}
