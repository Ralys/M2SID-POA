package ereputation.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.json.simple.JSONObject;

/**
 * Constructeur de requête SQL
 * 
 * @author Team E-réputation
 */
public class QueryBuilder {

    /**
     * Permet d'obtenir l'avis sur un élément (produit, vendeur, fournisseur)
     * @param type Produit | Vendeur | Fournisseur
     * @param name le nom
     * @return le message JSON contenant la requête SQL
     */
    public static String selectAvis(String type, String name) {
        String sql = "SELECT AVG(AVIS) AS AVIS FROM AVIS WHERE NOM_DESTINATAIRE = \"" + type + "_" + name + "\"";
        return JSONRequest("select", sql);
    }
    
    /**
     * Permet d'ajouter un avis sur un élément (produit, vendeur, fournisseur)
     * @param nomEmetteur le nom de l'agent qui émet l'avis
     * @param nomDestinataire l'élément sur lequel l'avis porte
     * @param avis l'avis (entre 0 et 5)
     * @return le message JSON contenant la requête SQL
     */
    public static String insertAvis(String nomEmetteur, String nomDestinataire, Long avis) {
         String sql = "INSERT INTO AVIS(NOM_EMETTEUR, NOM_DESTINATAIRE, AVIS) VALUES(\"" + nomEmetteur + "\",\"" + nomDestinataire + "\"," +  avis + ")";
         return JSONRequest("insert", sql);
    }
    
    /**
     * Permet d'obtenir la date de sortie d'un produit
     * @param ref la référence du produit
     * @return le message JSON contenant la requête SQL
     */
    public static String selectDateSortie(String ref) {
        String sql = "SELECT DATE_SORTIE FROM PRODUIT WHERE REF_PRODUIT = \"" + ref + "\"";
        return JSONRequest("select", sql);
    }
    
    /**
     * Permet d'obtenir le nombre de jours de soldes flottantes pour un vendeur
     * @param vendeur
     * @param dateDebut
     * @param dateFin
     * @return le message JSON contenant la requête SQL
     */
    public static String selectRetourSolde(String vendeur, String dateDebut, String dateFin) {
        DateFormat df = new SimpleDateFormat("yyyy");
        String year = df.format(new java.sql.Date(Long.valueOf(dateDebut)*1000));
        String sql = "SELECT COUNT(DAY(FROM_UNIXTIME((DATE_END-DATE_START)))) AS nbJourSolde FROM `SOLDE` WHERE VENDEUR = '"+vendeur+"' AND YEAR(FROM_UNIXTIME(DATE_START))="+year+" AND YEAR(FROM_UNIXTIME(DATE_END))="+year+"";
        return JSONRequest("select", sql);
    }
    
    /**
     * Permet d'obtenir l'ensemble des périodes de soldes de tous les vendeurs
     * @param vendeur le vendeur ayant fait la demande
     * @return le message JSON contenant la requête SQL
     */
    public static String selectRetourAllSolde(String vendeur) {
        String sql = "SELECT VENDEUR, DATE_START, DATE_END FROM SOLDE WHERE VENDEUR!='"+vendeur+"'";
        return JSONRequest("select", sql);
    }
    
    /**
     * Permet de vérifier si une vente est conforme
     * (prix avec perte, prix réduit alors que le vendeur n'a pas déclaré de
     * soldes flottantes)
     * @param idVente l'identifiant en base de données de la vente
     * @return le message JSON contenant la requête SQL
     */
    public static String verifierVente(String idVente) {
        String sql = "SELECT  ((VENTE.PRIX>=PRIX_CREATION) " +
                    "OR " +
                "((DATE_VENTE < ANY(SELECT DATE_START FROM SOLDE WHERE VENDEUR ='DARTY')) " +
                    "AND " +
                "(DATE_VENTE < ANY (SELECT DATE_END FROM SOLDE WHERE VENDEUR ='DARTY')))) AS statusVente " +
                "FROM `VENTE` INNER JOIN `PRODUIT` ON VENTE.REF_PRODUIT = PRODUIT.REF_PRODUIT " +
                "WHERE ID = "+idVente;
        return JSONRequest("select", sql);
    }
    
    /**
     * Permet d'enregistrer les informations d'une négociation
     * @param comportement stratégie/comportement du client
     * @param success si oui ou non la négociation a abouti à un succès
     * @param nb_negociations le nombre d'interactions entre le client et le vendeur
     * @return le message JSON contenant la requête SQL
     */
    public static String insertNegociation(String comportement, boolean success, long nb_negociations) {
        String sql = "INSERT INTO NEGOCIATION(COMPORTEMENT_CLIENT, SUCCESS, NB_NEGOCIATIONS) VALUES(\"" + comportement + "\"," + (success ? 1 : 0) + "," +  nb_negociations + ")";
        return JSONRequest("insert", sql);
    }
    
    /**
     * Permet d'enregistrer la période flottante de soldes pour un vendeur
     * @param vendeur le vendeur ayant fait la demande (14 jours avant)
     * @param dateDebut
     * @param dateFin
     * @return le message JSON contenant la requête SQL
     */
    public static String insertSolde(String vendeur, String dateDebut, String dateFin) {
        String sql = "INSERT INTO SOLDES(VENDEUR, DATE_START, DATE_END) VALUES(\"" + vendeur + "\"," + dateDebut+ "," +  dateFin + ")";
        return JSONRequest("insert", sql);
    }
    
    /**
     * Méthode générale pour construire un message pour l'agent BDD
     * @param typeRequest select | insert
     * @param sql la requête SQL à exécuter
     * @return le message JSON contenant la requête SQL
     */
    private static String JSONRequest(String typeRequest, String sql) {
        JSONObject request = new JSONObject();
        request.put("type", typeRequest);
        request.put("sql", sql);
        
        // retourne JSON contenant SQL
        return request.toJSONString();
    }
}
