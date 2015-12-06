package vendeur.tools;

import org.json.simple.JSONObject;

/**
 *
 * @author Aurélien
 */
public class QueryBuilder {

    private static String JSONRequest(String typeRequest, String sql) {
        JSONObject request = new JSONObject();
        request.put("type", typeRequest);
        request.put("sql", sql);

        // retourne JSON contenant SQL
        return request.toJSONString();
    }

    public static String getStock(String vendeur, String ref_prod) {
        String sql = "SELECT QTE "
                + "FROM STOCK "
                + "WHERE STOCK.REF_PRODUIT = PRODUIT.REF_PRODUIT "
                + "AND VENDEUR_NAME = \""+ vendeur + "\" ";
        return JSONRequest("select", sql);
    }

    public static String recherche(String recherche, String typeProduit) {
        String sql = "SELECT REF_PRODUIT "
                + "FROM PRODUIT, CATEGORIE, POSSEDE, TAGS "
                + "WHERE PRODUIT.ID_CATEGORIE = CATEGORIE.ID_CATEGORIE "
                + "AND PRODUIT.REF_PRODUIT = POSSEDE.REF_PRODUIT "
                + "AND POSSEDE.ID_TAG = TAGS.ID_TAG "
                + "AND NOM_CATEGORIE LIKE \"" + typeProduit + "\" "
                + "AND LABEL_TAG LIKE \"" + recherche + "\"";
        return JSONRequest("select", sql);
    }

    public static String rechercheRef(String reference, String vendeur) {
        String sql = "SELECT STOCK.REF_PRODUIT, NOM_PRODUIT, PRIX_UNITAIRE, PRIX_LIMITE, QTE "
                + "FROM PRODUIT,STOCK "
                + "WHERE PRODUIT.REF_PRODUIT = \"" +reference+"\" "
                + "AND STOCK.REF_PRODUIT = \""+reference+"\" "
                + "AND STOCK.VENDEUR_NAME = \""+ vendeur +"\"";
        System.out.println(sql);
        return JSONRequest("select", sql);
    }

}
