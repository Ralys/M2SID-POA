package fournisseur.behaviors;

import fournisseur.FournisseurAgent;
import fournisseur.utils.Produit;
import fournisseur.utils.StocksEtTransaction;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import java.util.Set;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tom
 */
public class GestionStockBehavior extends TickerBehaviour {

    private int stockMax = 1000;//A définir avec les vendeurs
    private double reducQte = 0.01; //1%*qte de produit fabriqué de reduction sur le prix unitaire lors de la fabrication d'un produit

    public GestionStockBehavior(Agent a) {
        super(a, 60000);
    }

    @Override
    protected void onTick() {
        HashMap<Produit, Integer> catalogue = ((StocksEtTransaction) this.getDataStore()).listStock();
        Set<Produit> cles = catalogue.keySet();
        for (Produit cle : cles) {
            int stockRestant = (Integer) ((StocksEtTransaction) this.getDataStore()).get(cle);
            if (stockRestant <= 2) {
                int valeurProduite = (stockMax / 40) - stockRestant;
                double prixProduction = cle.getPrixdeBase();
                double prixProductionTotal = (prixProduction * (1 - (valeurProduite * reducQte))) * valeurProduite;
                ((StocksEtTransaction) this.getDataStore()).incrementerStock(cle, valeurProduite);
                ((StocksEtTransaction) this.getDataStore()).changePesos(-prixProductionTotal);
                Logger.getLogger(FournisseurAgent.class.getName()).log(Level.INFO, "'{'0'}' stock insuffisant {0} : creation de nouveaux produits : {1}", new Object[]{stockRestant, valeurProduite});

            } else {
                Logger.getLogger(FournisseurAgent.class.getName()).log(Level.INFO, "{0} stock suffisant : {1}", new Object[]{cle.getNomProduit(), stockRestant});
            }
        }
        // Pour chaque produit
        // Si le stock est inférieur a 2
        // Produire 12 - stockRestant
        // décremneter tresorerie production * prixDeBase*Reduction
    }

}
