package fournisseur.behaviors;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

/**
 *
 * @author Tom
 */
public class GestionStockBehavior extends TickerBehaviour {

    private int stockMax = 500;//A définir avec les vendeurs

    public GestionStockBehavior(Agent a) {
        super(a, 60000);
    }

    @Override
    protected void onTick() {
        // Tout les minutes, Regarde les stocks et fabrique ce qu'il faut
    }

}
