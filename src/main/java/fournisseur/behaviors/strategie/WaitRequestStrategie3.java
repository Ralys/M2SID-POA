package fournisseur.behaviors.strategie;

import fournisseur.behaviors.WaitRequest;
import fournisseur.utils.Livraison;
import fournisseur.utils.StocksEtTransaction;

/**
 *
 * @author Tom
 */
public class WaitRequestStrategie3 extends WaitRequest {

    private double marge = 1.05;

    public WaitRequestStrategie3() {
        super();
    }

    @Override
    public double definirPrix(int idProduit, int quantite, int delai) {
        double prixBase = ((StocksEtTransaction) getDataStore()).getProduitById(idProduit).getPrixDeBase();
        double prix = ((prixBase * marge));
        double prixDelai = Livraison.prixLivraisonByDelai(delai);
        prix = Math.ceil(prix * 100) / 100;
        return prix + prixDelai;
    }

}
