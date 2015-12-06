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

    @Override
    public double définirNouveauPrix(int idProduit, Long date, String sender, double prixDemande) {
        Transaction t = ((StocksEtTransaction) getDataStore()).getTransaction(idProduit, date, sender);
        t.incNbNego();
        double prixDelai =Livraison.prixLivraisonByDelai(t.getDelai());
        double prix = ((StocksEtTransaction) getDataStore()).getProduitById(idProduit).getPrixDeBase();
        double prixNego = Math.max(prix, prixDemande - prixDelai);
        double prixVente = prix + (margeBase * prix - prixNego);
        prixVente = Math.round(prix * 100) / 100;
        t.setPrixPropose(prixVente);
        return prixVente + prixDelai;
    }

}
