package fournisseur.utils;

import jade.core.behaviours.DataStore;
import jade.util.leap.Set;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
        Long dateNow = new Date().getTime() / 1000;
        Set cles = this.keySet();
        Iterator it = cles.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Produit) {
                Produit p = (Produit) o;
                if (p.getIdProduit() == idProduit && p.getDateSortie() < dateNow) {
                    return p;
                }
            }
        }
        return null;
    }

    public Transaction getTransaction(int idProduit, Long dateLivraison, String client) {
        Set cles = this.keySet();
        Iterator it = cles.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Transaction) {
                Transaction t = (Transaction) o;
                if (t.getClient().compareTo(client) == 0
                        && t.getIdProduit() == idProduit
                        && t.getDateLivraison() == dateLivraison) {
                    return t;
                }
            }
        }
        return null;
    }

    public boolean removeTransaction(int idProduit, Long dateLivraison, String client) {
        Set cles = this.keySet();
        Iterator it = cles.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Transaction) {
                Transaction t = (Transaction) o;
                if (t.getClient().compareTo(client) == 0
                        && t.getIdProduit() == idProduit
                        && t.getDateLivraison() == dateLivraison) {
                    t.setAbouti(true);
                    return true;
                }
            }
        }
        return false;
    }

    public void decrementerStock(int id, int qte) {
        Produit p = getProduitById(id);
        this.put(p, ((Integer) this.get(p)) - qte);
    }

    public void incrementerStock(Produit p, int qte) {
        this.put(p, ((Integer) this.get(p)) + qte);
    }

    public HashMap<Produit, Integer> listStock() {
        ArrayList<Produit> listProduit = listProduit();
        HashMap<Produit, Integer> res = new HashMap<>();
        for (Produit p : listProduit) {
            res.put(p, (Integer) this.get(p));
        }
        return res;
    }

    public ArrayList<Produit> listProduit() {
        Long dateNow = new Date().getTime() / 1000;
        ArrayList<Produit> res = new ArrayList<>();
        Set cles = this.keySet();
        Iterator it = cles.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Produit) {
                Produit p = (Produit) o;
                if (p.getDateSortie() < dateNow) {
                    res.add(p);
                }
            }
        }
        return res;
    }

    public int stockUse() {
        ArrayList<Produit> listProduit = listProduit();
        int res = 0;
        for (Produit p : listProduit) {
            res += (Integer) this.get(p);
        }
        return res;
    }

    public double getPesos() {
        return (double) this.get("Tresorerie");
    }

    public void changePesos(double valeur) {
        this.put("Tresorerie", getPesos() + valeur);
    }

    public ArrayList<Produit> rechercheProduit(String motRecherche, String typeProduit, int qte) {
        return null; //TODO
    }
}
