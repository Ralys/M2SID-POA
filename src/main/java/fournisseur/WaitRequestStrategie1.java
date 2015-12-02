package fournisseur;

/**
 *
 * @author Tom
 */
public class WaitRequestStrategie1 extends WaitRequest{

    @Override
    public double definirPrix(int idProduit, int quantite, int delai) {
        return 0;
    }
    
}
