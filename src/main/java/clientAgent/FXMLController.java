package clientAgent;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
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
import javafx.scene.layout.Pane;
import common.TypeAgent;
import common.TypeProduit;
import java.util.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.RadioButton;

public class FXMLController implements Initializable {

    @FXML
    private Label messageErreur;

    @FXML
    private TextField ip;

    @FXML
    private TextField port;

    @FXML
    private TextField nomAgent;

    @FXML
    private ComboBox<String> choixProd;

    @FXML
    private ComboBox<String> choixClient;

    @FXML
    private ComboBox<String> choixVendeur;

    @FXML
    private ComboBox<String> choixQte;

    @FXML
    private TextField nomProd;

    @FXML
    private Button btnValider;

    @FXML
    private TextField qteProd;

    @FXML
    private AnchorPane selecCli;

    @FXML
    private TextField reference;

    @FXML
    private RadioButton rbRecherche;

    @FXML
    private RadioButton rbReference;

    @FXML
    private ListView log;

    private final ObservableList<String> listTypeAgentVend = FXCollections.observableArrayList(TypeAgent.Fournisseur, TypeAgent.Vendeur);

    private final ObservableList<String> listQte = FXCollections.observableArrayList(
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

    private final ObservableList<String> listTypeProduit = FXCollections.observableArrayList(TypeProduit.tabTypeProduit);

    private final ObservableList<String> listTypeClient = FXCollections.observableArrayList(TypeAgentClient.Presse, TypeAgentClient.Econome);

    public static ObservableList<String> listLog = FXCollections.observableArrayList();


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
                Object[] arguments = {choixClient.getValue(), choixVendeur.getValue(), choixProd.getValue(), nomProd.getText(), choixQte.getValue(), typeRecherche};

                // création de l'agent
                AgentController agent = ac.createNewAgent(nomAgent.getText(), "clientAgent.Client", arguments);
                // lancement de l'agent
                agent.start();

            }
        } catch (StaleProxyException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleTypeRechercheClassique(ActionEvent event) {
        reference.setDisable(true);
        nomProd.setDisable(false);
        choixProd.setDisable(false);

    }

    @FXML
    private void handleTypeRechercheReference(ActionEvent event) {
        nomProd.setDisable(true);
        choixProd.setDisable(true);
        reference.setDisable(false);
    }

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
                && !reference.getText().isEmpty()))) {

            valide = true;
            messageErreur.setVisible(false);

            // verrouillage du bouton de validation
            btnValider.setDisable(true);
            
        } else {
            messageErreur.setText("Merci de remplir tous les champs !");
            messageErreur.setVisible(true);
        }

        return valide;
    }
    
    


}
