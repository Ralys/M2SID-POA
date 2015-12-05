package fournisseur.behaviors.strategie;

import fournisseur.behaviors.WaitNegociation;
import fournisseur.utils.Livraison;
import fournisseur.utils.Produit;
import fournisseur.utils.StocksEtTransaction;
import fournisseur.utils.Transaction;

/**
 *
 * @author Tom
 */
public class WaitNegociationStrategie2 extends WaitNegociation {

    private double margeMax = 1.30;
    private double reducNego = 0.005;

    @Override
    public double définirNouveauPrix(int idProduit, Long date, String sender, double prixDemande) {
        Transaction t = ((StocksEtTransaction) getDataStore()).getTransaction(idProduit, date, sender);
        t.incNbNego();
        int delai = t.getDelai();
        int nbNego = Math.min(5, t.getNbNego());

        Produit p = ((StocksEtTransaction) getDataStore()).getProduitById(idProduit);
        double prixBase = p.getPrixDeBase();
        int stock = (int) getDataStore().get(p);
        double reducStock = stock / 100;
        double prix = (prixBase * (margeMax - reducStock + (reducNego * nbNego))) + Livraison.prixLivraisonByDelai(delai);
        return Math.round(prix * 100) / 100;
    }

}
