package vendeur.tools;

import org.json.simple.JSONObject;

/**
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
                + "WHERE STOCK.REF_PRODUIT = \"" + ref_prod + "\" "
                + "AND VENDEUR_NAME = \"" + vendeur + "\" ";
        return JSONRequest("select", sql);
    }

    public static String recherche(String recherche, String typeProduit, String vendeur) {
        String sql = "SELECT PRODUIT.REF_PRODUIT, NOM_PRODUIT, PRIX_UNITAIRE, PRIX_LIMITE, QTE "
                + "FROM PRODUIT, STOCK, CATEGORIE, POSSEDE, TAGS "
                + "WHERE PRODUIT.ID_CATEGORIE = CATEGORIE.ID_CATEGORIE "
                + "AND PRODUIT.REF_PRODUIT = POSSEDE.REF_PRODUIT "
                + "AND STOCK.REF_PRODUIT = PRODUIT.REF_PRODUIT "
                + "AND POSSEDE.ID_TAG = TAGS.ID_TAG "
                + "AND NOM_CATEGORIE LIKE \"" + typeProduit + "\" "
                + "AND (LABEL_TAG LIKE \"%" + recherche + "%\" "
                + "OR NOM_PRODUIT LIKE \"%" + recherche + "%\") "
                + "AND STOCK.VENDEUR_NAME = \"" + vendeur + "\"";
        return JSONRequest("select", sql);
    }

    public static String rechercheRef(String reference, String vendeur) {
        String sql = "SELECT STOCK.REF_PRODUIT, NOM_PRODUIT, PRIX_UNITAIRE, PRIX_LIMITE, QTE "
                + "FROM PRODUIT,STOCK "
                + "WHERE PRODUIT.REF_PRODUIT = STOCK.REF_PRODUIT "
                + "AND PRODUIT.REF_PRODUIT = \""+reference+"\" "
                + "AND STOCK.VENDEUR_NAME = \"" + vendeur + "\"";
        return JSONRequest("select", sql);
    }

    /**
     * Get list of product's refs and stock
     *
     * @return
     */
    public static String getRefListStock(String vendeur) {
        String sql = "SELECT PRODUIT.REF_PRODUIT, QTE "
                + "FROM PRODUIT LEFT JOIN STOCK ON STOCK.REF_PRODUIT = PRODUIT.REF_PRODUIT"
                + " WHERE VENDEUR_NAME = \"" + vendeur + "\" OR VENDEUR_NAME IS NULL";


        return JSONRequest("select", sql);
    }


    public static String getRefStock(String ref, String vendeur) {
        String sql = "SELECT REF_PRODUIT, QTE FROM STOCK WHERE VENDEUR_NAME = \"" + vendeur + "\" AND REF_PRODUIT = " + ref;

        return JSONRequest("select", sql);
    }


    public static String newStock(String idProduit, Integer quantite, Float prix, String localName) {
        String sql = "INSERT INTO STOCK(REF_PRODUIT, VENDEUR_NAME, PRIX_LIMITE, PRIX_UNITAIRE, QTE) VALUES(" + idProduit + ", " + quantite + ", " + prix + "," + prix * 1.30f + ", \"" + localName + "\") ";

        return JSONRequest("insert", sql);
    }

    public static String updateStock(String idProduit, Integer quantite, Float prix, String localName) {
        String sql = "UPDATE STOCK SET QTE = " + quantite + ", PRIX_UNITAIRE = " + prix * 1.30f + ", PRIX_LIMITE = " + prix + " WHERE VENDEUR_NAME = \"" + localName + "\" AND REF_PRODUIT = " + idProduit;

        return JSONRequest("insert", sql);
    }

    public static String updateStock(String idProduit, Integer quantite, String localName) {
        String sql = "UPDATE STOCK SET QTE = " + quantite +" WHERE VENDEUR_NAME = \"" + localName + "\" AND REF_PRODUIT = " + idProduit;

        return JSONRequest("insert", sql);
    }

    public static String isSoldes(String localName, Long dateJour) {
        String sql = "SELECT * FROM SOLDE WHERE VENDEUR LIKE \"" + localName + "\" AND DATE_START <= " + dateJour + " AND DATE_END >= " + dateJour;

        return JSONRequest("select", sql);
    }

    public static String vente(String reference, Integer quantite, Float prix, long date, String vendeur, String acheteur) {
        String sql = "INSERT INTO VENTE(REF_PRODUIT, PRIX, QTE, PROVIDER, ACHETEUR, DATE_VENTE) VALUES("+reference+", "+prix+", "+quantite+", "+vendeur+", "+acheteur+", "+date+")";

        return JSONRequest("insert", sql);
    }

}
