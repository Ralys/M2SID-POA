package client.UI;

import client.outils.Produit;
import client.outils.TypeAgentClient;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import common.TypeAgent;
import common.TypeProduit;
import java.util.HashMap;
import javafx.scene.control.RadioButton;

/**
 * Controleur de l'interface graphique 
 * @author mercier
 */
public class FXMLController implements Initializable {

    /**
     * Label du message d'erreur lorsque tous les champs ne sont pas remplis
     */
    @FXML
    private Label messageErreur;

    /**
     * Champ de saisi de l'IP
     */
    @FXML
    private TextField ip;

    /**
     * Champ de saisi du port
     */
    @FXML
    private TextField port;

    /**
     * Champ de saisi du nom de l'agent
     */
    @FXML
    private TextField nomAgent;

    /**
     * Liste des choix des produits
     */
    @FXML
    private ComboBox<String> choixProd;

    /**
     * Choix du type de client
     */
    @FXML
    private ComboBox<String> choixClient;

    /**
     * Choix du type de vendeur (fournisseur ou vendeur)
     */
    @FXML
    private ComboBox<String> choixVendeur;

    /**
     * Choix de la quantité de 1 à 10
     */
    @FXML
    private ComboBox<String> choixQte;

    /**
     * Nom du produit
     */
    @FXML
    private TextField nomProd;

    /**
     * Bouton valider permettant de lancer la recherche de produit et d'achat avec la configuration saisi
     */
    @FXML
    private Button btnValider;

    /**
     * Quantité du produit
     */
    @FXML
    private TextField qteProd;

    /**
     * Panneau contenant le choix du client
     */
    @FXML
    private AnchorPane selecCli;

    /**
     * Champ de saisi de la référence du produit à acheter
     */
    @FXML
    private TextField reference;

    /**
     * Permet de choisir la recherche par nom de produit
     */
    @FXML
    private RadioButton rbRecherche;

    /**
     * Permet de choisir la recherche par ID produit
     */
    @FXML
    private RadioButton rbReference;

    /**
     * Permet d'afficher la liste des messages
     */
    @FXML
    private ListView log;
    
    /**
     * Défini la limite
     */
    @FXML
    private TextField limite;

    /**
     * Initialise la liste des agents vendeur (fournisseur et vendeur)
     */
    private final ObservableList<String> listTypeAgentVend = FXCollections.observableArrayList(TypeAgent.Fournisseur, TypeAgent.Vendeur);

    /**
     * Initialise la comboBox des choix de quantité désirée
     */
    private final ObservableList<String> listQte = FXCollections.observableArrayList(
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

    /**
     * Initialise la liste des produits disponibles
     */
    private final ObservableList<String> listTypeProduit = FXCollections.observableArrayList(TypeProduit.allProducts);

    
    /**
     * Initialise la liste des différents types de client (comportements du client)
     */
    private final ObservableList<String> listTypeClient = FXCollections.observableArrayList(TypeAgentClient.Presse, TypeAgentClient.Econome, TypeAgentClient.Mefiant, TypeAgentClient.Negociateur);

    /**
     * Contient la liste des logs de l'application
     */
    public static ObservableList<String> listLog = FXCollections.observableArrayList();

    /**
     * Liste des achats nomClient - Produit
     */
    public static HashMap<String, Produit> lAchatsEffectues = new HashMap();
    
    /**
     * Méthode appelé lors de la création de l'interface javafx
     * @param url L'URL
     * @param rb La ressourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        choixProd.setItems(listTypeProduit);
        choixClient.setItems(listTypeClient);
        choixVendeur.setItems(listTypeAgentVend);
        choixQte.setItems(listQte);
        log.setItems(listLog);

        // remplissage par default
        ip.setText("192.168.0.13");
        port.setText("1099");
        nomAgent.setText("Bob");
    }

    /**
     * Méthode appelé lors de l'appuie sur le bouton valider, permet aussi la créer l'agent cliendt
     * @param event L'évenement sur le bouton
     */
    @FXML
    private void handlebtProd(ActionEvent event) {
        try {

            // vérification du remplissage des champs
            if (valider()) {

                jade.core.Runtime rt = jade.core.Runtime.instance();
                rt.setCloseVM(true);
                Profile p = new ProfileImpl();
                p.setParameter(Profile.MAIN_HOST, ip.getText());
                p.setParameter(Profile.MAIN_PORT, port.getText());
                // on recuprère l'information sur le type de recherche
                // true: recherche false:recherche par reference
                boolean typeRecherche = rbRecherche.isSelected();
                AgentContainer ac = rt.createAgentContainer(p);
                Object[] arguments = {choixClient.getValue(), choixVendeur.getValue(), choixProd.getValue(), nomProd.getText(), reference.getText(), choixQte.getValue(), typeRecherche, limite.getText()};

                // création de l'agent
                AgentController agent = ac.createNewAgent(nomAgent.getText(), "client.ClientAgent", arguments);
                // lancement de l'agent
                agent.start();

            }
        } catch (StaleProxyException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Permet de désactiver le choix par référence si le choix par nom a été choisi
     * @param event L'évènement sur le radiobouton
     */
    @FXML
    private void handleTypeRechercheClassique(ActionEvent event) {
        reference.setDisable(true);
        nomProd.setDisable(false);
        choixProd.setDisable(false);

    }

    /**
     * Permet de désactiver le choix par nom du produit si le choix par référence a été choisi
     * @param event L'évènement sur le radiobouton
     */
    @FXML
    private void handleTypeRechercheReference(ActionEvent event) {
        nomProd.setDisable(true);
        choixProd.setDisable(true);
        reference.setDisable(false);
    }

    /**
     * Permet de vérifier si tous les champs ont été remplis
     * @return true si tous les champs sont remplis, sinon false et affiche un message d'erreur dans les logs
     */
    public boolean valider() {
        boolean valide = false;

        // vérification de la selection du type de produit
        if (choixClient.getSelectionModel().getSelectedIndex() != -1
                && choixVendeur.getSelectionModel().getSelectedIndex() != -1
                && choixQte.getSelectionModel().getSelectedIndex() != -1
                && !ip.getText().isEmpty()
                && !port.getText().isEmpty()
                && !nomAgent.getText().isEmpty()
                && ((rbRecherche.isSelected()
                && !nomProd.getText().isEmpty()
                && choixProd.getSelectionModel().getSelectedIndex() != -1)
                || (rbReference.isSelected()
                && !reference.getText().isEmpty()))
                && testerLimite(limite)) {
            valide = true;
            messageErreur.setVisible(false);
        } else {
            testerContenu(choixClient, choixVendeur, choixQte);
            testerContenu(ip, port, nomAgent, limite);

            if (rbRecherche.isSelected()) {
                testerContenu(nomProd);
                testerContenu(choixProd);
            }
            if (rbReference.isSelected()) {
                testerContenu(reference);
            }
            testerLimite(limite);
            // messageErreur.setText("Merci de remplir tous les champs !");
            //messageErreur.setVisible(true);
        }

        return valide;
    }
    
    /**
     * Permet de vérifier si la limite saisi est un entier
     * @param tf Le champ "limite" à vérifier 
     * @return true si la limite est un entier, sinon false
     */
    public boolean testerLimite(TextField tf){
        boolean res = false;
        if(!limite.getText().isEmpty()){
               res = true;
               try {
                   int lim = Integer.parseInt(limite.getText().toString());
               }
               catch(Exception e){
                messageErreur.setText("Ce champ ne peux contenir que des nombres");
                messageErreur.setVisible(true);
                limite.setStyle("-fx-border-color:red");
                res = false;
            }
        }
        return res;
    }

    /**
     * Permet de vérifié si un choix à bien été fait dans une combobox
     * @param cbx La comboBox à vérifier. Un message d'erreur dans les logs est affiché.
     */
    public void testerContenu(ComboBox... cbx) {
        for (ComboBox cb : cbx) {
            if (cb.getSelectionModel().getSelectedIndex() != -1) {
                cb.setStyle("-fx-border-color:green");
            } else {
                cb.setStyle("-fx-border-color:red");
                messageErreur.setText("Merci de remplir tous les champs !");
                messageErreur.setVisible(true);
            }
        }
    }

    /**
     * Permet de tester si un textField a bien été rempli
     * @param tfs Le textField à tester. Affiche un message d'erreur dans les logs si le champ n'est pas rempli
     */
    public void testerContenu(TextField... tfs) {
        for (TextField tf : tfs) {
            if (tf.isVisible() && tf.getText().isEmpty()) {
                tf.setStyle("-fx-border-color:red");
                messageErreur.setText("Merci de remplir tous les champs !");
                messageErreur.setVisible(true);
            } else {
                tf.setStyle("-fx-border-color:green");
            }
        }
    }
    
    /**
     * Permet d'afficher le champ limite si le client est de type économe ou pressé;
     */
    public void selectionType(){
        if (choixClient.getSelectionModel().getSelectedItem().equals("Econome")){
            limite.setPromptText("Prix maximal");
            limite.setVisible(true);
            
        } else if (choixClient.getSelectionModel().getSelectedItem().equals("Pressé")) {
            limite.setPromptText("Delai maximal(en jours)");
            limite.setVisible(true);
                
        }else limite.setVisible(false);
    }

}
