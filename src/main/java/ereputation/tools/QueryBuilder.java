package ereputation.tools;

import java.sql.Timestamp;
import org.json.simple.JSONObject;

/**
 *
 * @author Team EReputation
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
    
    public static String selectRetourSolde(String agentName, String dateDebut, String dateFin) {
        //TO DO ecrire la requête
        String sql = "SELECT COUNT(ID) AS nbSolde FROM SOLDE WHERE VENDEUR= \""+agentName+"\"";
        return JSONRequest("select", sql);
    }
    
    public static String selectRetourAllSolde(String vendeur) {
        //TO DO ecrire la requête
        String sql = "SELECT COUNT(ID) AS nbSolde FROM SOLDE WHERE VENDEUR != \""+vendeur+"\"";
        return JSONRequest("select", sql);
    }
    
    public static String verifierVente(String idVente) {
        //TO DO ecrire la requête
        String sql = "";
        return JSONRequest("select", sql);
    }
            
    private static String JSONRequest(String typeRequest, String sql) {
        JSONObject request = new JSONObject();
        request.put("type", typeRequest);
        request.put("sql", sql);
        
        // retourne JSON contenant SQL
        return request.toJSONString();
    }
}
