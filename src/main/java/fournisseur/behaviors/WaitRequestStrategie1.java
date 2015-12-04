package fournisseur.behaviors;

import fournisseur.utils.Livraison;
import fournisseur.utils.StocksEtTransaction;

/**
 *
 * @author Tom
 */
public class WaitRequestStrategie1 extends WaitRequest{
    private double margeBase = 1.10;
    private double reductionQte = 0.01;
    
    @Override
    public double definirPrix(int idProduit, int quantite, int delai) {
        double prixBase = ((StocksEtTransaction)getDataStore()).getProduitById(idProduit).getPrixdeBase();
        return ((prixBase * margeBase) * (1-(reductionQte*quantite)))+Livraison.prixLivraisonByDelai(delai);
    }
    
}
