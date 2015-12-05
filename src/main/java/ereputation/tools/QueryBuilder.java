package ereputation.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.json.simple.JSONObject;

/**
 *
 * @author Team E-réputation
 */
public class QueryBuilder {

    public static String selectAvis(String type, String name) {
        String sql = "SELECT AVG(AVIS) AS AVIS FROM AVIS WHERE NOM_DESTINATAIRE = \"" + type + "_" + name + "\"";
        return JSONRequest("select", sql);
    }
    
    public static String insertAvis(String nomEmetteur, String nomDestinataire, Long avis) {
         String sql = "INSERT INTO AVIS(NOM_EMETTEUR, NOM_DESTINATAIRE, AVIS) VALUES(\"" + nomEmetteur + "\",\"" + nomDestinataire + "\"," +  avis + ")";
         return JSONRequest("insert", sql);
    }
    
    public static String selectDateSortie(String ref) {
        String sql = "SELECT DATE_SORTIE FROM PRODUIT WHERE REF_PRODUIT = \"" + ref + "\"";
        return JSONRequest("select", sql);
    }
    
    public static String selectRetourSolde(String vendeur, String dateDebut, String dateFin) {
        DateFormat df = new SimpleDateFormat("yyyy");
        String year = df.format(new java.sql.Date(Long.valueOf(dateDebut)*1000));
        String sql = "SELECT COUNT(DAY(FROM_UNIXTIME((DATE_END-DATE_START)))) AS nbJourSolde FROM `SOLDE` WHERE VENDEUR = '"+vendeur+"' AND YEAR(FROM_UNIXTIME(DATE_START))="+year+" AND YEAR(FROM_UNIXTIME(DATE_END))="+year+"";
        return JSONRequest("select", sql);
    }
    
    public static String selectRetourAllSolde(String vendeur) {
        String sql = "SELECT VENDEUR, DATE_START, DATE_END FROM SOLDE WHERE VENDEUR!='"+vendeur+"'";
        return JSONRequest("select", sql);
    }
    
    public static String verifierVente(String idVente) {
        //TO DO ecrire la requête
        String sql = "";
        return JSONRequest("select", sql);
    }
    
    public static String insertNegociation(String comportement, boolean success, long nb_negociations) {
        String sql = "INSERT INTO NEGOCIATION(COMPORTEMENT_CLIENT, SUCCESS, NB_NEGOCIATIONS) VALUES(\"" + comportement + "\"," + (success ? 1 : 0) + "," +  nb_negociations + ")";
        return JSONRequest("insert", sql);
    }
            
    private static String JSONRequest(String typeRequest, String sql) {
        JSONObject request = new JSONObject();
        request.put("type", typeRequest);
        request.put("sql", sql);
        
        // retourne JSON contenant SQL
        return request.toJSONString();
    }
}
