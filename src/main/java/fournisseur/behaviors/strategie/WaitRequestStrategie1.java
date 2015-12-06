package fournisseur.behaviors.strategie;

import fournisseur.behaviors.WaitRequest;
import fournisseur.utils.Livraison;
import fournisseur.utils.StocksEtTransaction;

/**
 *
 * @author Tom
 */
public class WaitRequestStrategie1 extends WaitRequest {

    private double margeBase = 1.10;
    private double reductionQte = 0.01;

    @Override
    public double definirPrix(int idProduit, int quantite, int delai) {
        double prixBase = ((StocksEtTransaction) getDataStore()).getProduitById(idProduit).getPrixDeBase();
        double prix = ((prixBase * margeBase) * (1 - (reductionQte * quantite))) + Livraison.prixLivraisonByDelai(delai);
        double prixDelai = Livraison.prixLivraisonByDelai(delai);
        prix = Math.ceil(prix * 100) / 100;
        return prix + prixDelai;
    }

}
