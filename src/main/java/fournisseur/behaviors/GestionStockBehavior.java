package fournisseur.behaviors;

import fournisseur.utils.Produit;
import fournisseur.utils.StocksEtTransaction;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

/**
 *
 * @author Tom
 */
public abstract class GestionStockBehavior extends TickerBehaviour {

    protected int stockMax = 1000;//A définir avec les vendeurs
    private double reducQte = 0.002; //1%*qte de produit fabriqué de reduction sur le prix unitaire lors de la fabrication d'un produit

    public GestionStockBehavior(Agent a) {
        super(a, 20000);
        // this.creationproduit();
    }

    @Override
    protected void onTick() {
        this.creationproduit();
    }

    public abstract void creationproduit();

    public void dataStoreOperation(Produit p, int qteProduite) {
        //Spam de trop
        // Logger.getLogger(FournisseurAgent.class.getName()).log(Level.INFO, "Produit : {0}, création de " + qteProduite, p.getNomProduit());
        double prixProduction = p.getPrixDeBase();
        double prixProductionTotal = (prixProduction * (1 - (qteProduite * reducQte))) * qteProduite;
        ((StocksEtTransaction) this.getDataStore()).incrementerStock(p, qteProduite);
        ((StocksEtTransaction) this.getDataStore()).changePesos(-prixProductionTotal);
    }

}
