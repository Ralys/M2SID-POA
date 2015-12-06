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
        String sql = "SELECT PRODUIT.REF_PRODUIT "
                + "FROM PRODUIT, STOCK, CATEGORIE, POSSEDE, TAGS "
                + "WHERE PRODUIT.ID_CATEGORIE = CATEGORIE.ID_CATEGORIE "
                + "AND PRODUIT.REF_PRODUIT = POSSEDE.REF_PRODUIT "
                + "AND STOCK.REF_PRODUIT = PRODUIT.REF_PRODUIT "
                + "AND POSSEDE.ID_TAG = TAGS.ID_TAG "
                + "AND NOM_CATEGORIE LIKE \"" + typeProduit + "\" "
                + "AND (LABEL_TAG LIKE \"%" + recherche + "%\" "
                + "OR NOM_PRODUIT LIKE \"%" + recherche + "%\") ";
        System.out.println(sql);
        return JSONRequest("select", sql);
    }

    public static String rechercheRef(String reference, String vendeur) {
        String sql = "SELECT STOCK.REF_PRODUIT, NOM_PRODUIT, PRIX_UNITAIRE, PRIX_LIMITE, QTE "
                + "FROM PRODUIT,STOCK "
                + "WHERE PRODUIT.REF_PRODUIT = \"" +reference+"\" "
                + "AND STOCK.REF_PRODUIT = \""+reference+"\" "
                + "AND STOCK.VENDEUR_NAME = \""+ vendeur +"\"";
        return JSONRequest("select", sql);
    }

    /**
     * Get list of product's refs and stock
     * @return
     */
    public static String getRefListStock(String vendeur) {
       String sql = "SELECT PRODUIT.REF_PRODUIT, QTE "
                + "FROM PRODUIT LEFT JOIN STOCK ON STOCK.REF_PRODUIT = PRODUIT.REF_PRODUIT"
                + " WHERE VENDEUR_NAME = \"vendeur_"+ vendeur + "\" OR VENDEUR_NAME IS NULL";


        return JSONRequest("select", sql);
    }
}
