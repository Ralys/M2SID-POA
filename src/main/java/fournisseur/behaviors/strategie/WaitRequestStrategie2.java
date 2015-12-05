package fournisseur.behaviors.strategie;

import fournisseur.behaviors.WaitRequest;
import fournisseur.utils.Livraison;
import fournisseur.utils.Produit;
import fournisseur.utils.StocksEtTransaction;

/**
 *
 * @author Tom
 */
public class WaitRequestStrategie2 extends WaitRequest {

    private double margeMax = 1.30;

    @Override
    public double definirPrix(int idProduit, int quantite, int delai) {
        Produit p = ((StocksEtTransaction) getDataStore()).getProduitById(idProduit);
        double prixBase = p.getPrixdeBase();
        int stock = (int) getDataStore().get(p);
        double reducStock = stock / 100;
        double prix = (prixBase * (margeMax - reducStock)) + Livraison.prixLivraisonByDelai(delai);
        return Math.round(prix * 100) / 100;
    }

}
