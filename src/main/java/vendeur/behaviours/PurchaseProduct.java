package vendeur.behaviours;

import common.TypeAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import vendeur.VendeurAgent;
import vendeur.tools.QueryBuilder;

import java.util.Iterator;
/**
 *
 * @author Aurélien
 */
public class PurchaseProduct extends CyclicBehaviour {
    private final JSONParser parser;

    public PurchaseProduct(VendeurAgent aThis) {
        super(aThis);
        this.parser = new JSONParser();
    }

    @Override
    public void action() {

        VendeurAgent vendeur = (VendeurAgent) myAgent;

        // Get quantity for each product

        ACLMessage stockProducts = vendeur.sendMessage(ACLMessage.REQUEST, QueryBuilder.getRefListStock(vendeur.getLocalName()), vendeur.getBDDAgent(), true);


        JSONArray resultatsStockProducts = null;
        try {
            resultatsStockProducts = (JSONArray) this.parser.parse(stockProducts.getContent());

            JSONObject reponse = new JSONObject();
            JSONArray list = new JSONArray();

            for (Iterator iterator = resultatsStockProducts.iterator(); iterator.hasNext();) {
                JSONObject resultat = (JSONObject) iterator.next();

                String QTE_ = resultat.get("QTE")+"";
                String REF_PRODUIT = resultat.get("REF_PRODUIT")+"";

                //test quantity
                if(QTE_.contains("null")) {
                    command(REF_PRODUIT, 5);
                }
                else {
                    Integer QTE = Integer.valueOf(QTE_);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void command(String ref, Integer qte) {
        VendeurAgent vendeur = (VendeurAgent) myAgent;
        System.out.println("jeChercheReference "+ ref);
        vendeur.jeChercheReference(TypeAgent.Fournisseur, ref, qte);
    }


}
