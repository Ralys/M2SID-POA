package fournisseur;

import jade.core.behaviours.DataStore;
import jade.util.leap.Set;
import java.util.Iterator;

/**
 *
 * @author Tom
 */
public class Stocks extends DataStore {

    public boolean verifierStock(int idProduit, int qte) {
        int qteDispo = (int) this.get(getIdProduit(idProduit));
        if (qteDispo < qte) {
            return false;
        }
        return true;
    }

    public Produit getIdProduit(int idProduit) {
        Set cles = this.keySet();
        Iterator it = cles.iterator();
        while (it.hasNext()) {
            Produit p = (Produit) it.next();
            if (p.getIdProduit() == idProduit) {
                return p;
            } 
        }
        return null;
    }
    
    public Produit[] rechercheProduit(String motRecherche, int qte){
        return null; //TODO
    }
}
