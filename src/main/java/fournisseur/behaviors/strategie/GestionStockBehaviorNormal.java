/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fournisseur.behaviors.strategie;

import fournisseur.behaviors.GestionStockBehavior;
import fournisseur.utils.Produit;
import fournisseur.utils.StocksEtTransaction;
import jade.core.AID;
import jade.core.Agent;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author tom
 */
public class GestionStockBehaviorNormal extends GestionStockBehavior {

    public GestionStockBehaviorNormal(Agent a) {
        super(a);
    }

    public void creationproduit() {
        HashMap<Produit, Integer> catalogue = ((StocksEtTransaction) this.getDataStore()).listStock();
        Set<Produit> cles = catalogue.keySet();
        for (Produit cle : cles) {
            int stockRestant = (Integer) ((StocksEtTransaction) this.getDataStore()).get(cle);
            if (stockRestant <= 2) {
                int valeurProduite = (stockMax / 40) - stockRestant;
                this.dataStoreOperation(cle, valeurProduite);
            }
        }
    }

}
