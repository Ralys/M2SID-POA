package fournisseur.behaviors.strategie;

import fournisseur.behaviors.WaitRequest;
import fournisseur.utils.Livraison;
import fournisseur.utils.StocksEtTransaction;
import jade.core.AID;

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
        double prix = ((prixBase * marge)) + Livraison.prixLivraisonByDelai(delai);
        return Math.ceil(prix * 100) / 100;
    }

}
