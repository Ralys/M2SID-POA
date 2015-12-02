package fournisseur;

import jade.core.behaviours.DataStore;
import jade.util.leap.Set;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Tom
 */
public class StocksEtTransaction extends DataStore {

    public boolean verifierStock(int idProduit, int qte) {
        int qteDispo = (int) this.get(getProduitById(idProduit));
        if (qteDispo < qte) {
            return false;
        }
        return true;
    }

    public Produit getProduitById(int idProduit) {
        Set cles = this.keySet();
        Iterator it = cles.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Produit) {
                Produit p = (Produit) o;
                if (p.getIdProduit() == idProduit) {
                    return p;
                }
            }
        }
        return null;
    }

    public Transaction getTransaction(int idProduit, int delai, String client) {
        Set cles = this.keySet();
        Iterator it = cles.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Transaction) {
                Transaction t = (Transaction) o;
                if (t.getClient().compareTo(client) == 0
                        && t.getIdProduit() == idProduit
                        && t.getDelai() == delai) {
                    return t;
                }
            }
        }
        return null;
    }

    public void removeTransaction(int idProduit, int delai, String client) {
        this.remove(this.getTransaction(idProduit, delai, client));
    }

    public void decrementerStock(int id, int qte) {
        Produit p = getProduitById(id);
        this.put(p, ((Integer) this.get(p)) - qte);
    }

    public ArrayList<Produit> rechercheProduit(String motRecherche, String typeProduit, int qte) {
        return null; //TODO
    }
}
