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

    public static String getStock(String type, String vendeur, String ref_prod) {
        String sql = "SELECT REF_PRODUIT, NOM_PRODUIT, PRIX_UNITAIRE, QTE "
                + "FROM STOCK, PRODUIT "
                + "WHERE STOCK.REF_PRODUIT = PRODUIT.REF_PRODUIT "
                + "AND VENDEUR_NAME = \"" + type + "_" + vendeur + "\" "
                + "AND REF_PRODUIT LIKE \"" + ref_prod + "\"";
        return JSONRequest("select", sql);
    }

    public static String recherche(String type, String localName, String recherche, String typeProduit) {
        String sql = "SELECT REF_PRODUIT "
                + "FROM PRODUIT, CATALOGUE, POSSEDE, TAGS "
                + "WHERE PRODUIT.ID_CATEGORIE = CATEGORIE.ID_CATEGORIE "
                + "AND PRODUIT.REF_PRODUIT = POSSEDE.REF_PRODUIT "
                + "AND POSSEDE.ID_TAG = TAGS.ID_TAG "
                + "AND NOM_CATEGORIE LIKE \"" + typeProduit + "\" "
                + "AND LABEL_TAG LIKE \"" + recherche + "\"";
        return JSONRequest("select", sql);
    }

}
