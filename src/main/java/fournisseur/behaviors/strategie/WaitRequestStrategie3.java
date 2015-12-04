package fournisseur.behaviors.strategie;

import fournisseur.behaviors.WaitRequest;

/**
 *
 * @author Tom
 */
public class WaitRequestStrategie3 extends WaitRequest{

    @Override
    public double definirPrix(int idProduit, int quantite, int delai) {
        return -1;
    }
    
}
