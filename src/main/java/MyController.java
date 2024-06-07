import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MyController implements Initializable {
    public Button sendButton;
    public Text whosOnline;
    public Text chooseMessage;
    public Text individual;
    public Text group;
    public Text everyone;
    public AnchorPane initialRoot;

    public ListView allClientsList;
    public Text recipients;
    @FXML
    AnchorPane serverRoot;
    @FXML
    AnchorPane clientRoot;
    @FXML
    ListView serverListView;
    @FXML
    ListView clientListView;
    @FXML
    TextField typeHere;
    @FXML
    Button serverButton;
    @FXML
    Button clientButton;
    @FXML
    Button individualButton;
    @FXML
    Button groupButton;
    @FXML
    Button everyoneButton;
    @FXML
    Button exitButton;
    Server serverConnection;
    Client clientConnection;

    int yourNum = 0;
    boolean Once = true;

    MessageInfo messageInfo = new MessageInfo();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void serverChosen(javafx.event.ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MyFXML1.fxml"));
        Parent serverRoot = loader.load();
        MyController myctr = loader.getController();
        initialRoot.getScene().setRoot(serverRoot);
        serverRoot.getStylesheets().add("Style1.css");
        myctr.sendServer();


    }

    public void sendServer() {
        serverConnection = new Server(data -> {
            Platform.runLater(() -> {
                if(serverListView != null && !((MessageInfo) data).updateSelected) {
                    serverListView.getItems().add(((MessageInfo) data).message);
                }
            });
        });
    }
    public void clientChosen(javafx.event.ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MyFXML2.fxml"));
        Parent clientRoot = loader.load();
        MyController myctr = loader.getController();
        clientRoot.getStylesheets().add("Style2.css");//set style

        initialRoot.getScene().setRoot(clientRoot);

        myctr.sendClient();
    }

    public void sendClient() {
        clientConnection = new Client(
                data->{
            Platform.runLater(()->{
                if(Once){
                    yourNum = ((MessageInfo) data).clientNum;
                    Once = false;
                }
                if(clientListView != null && !((MessageInfo) data).updateSelected) {
                    clientListView.getItems().add(((MessageInfo) data).message);
                }
                if(((MessageInfo) data).updateSelected && ((MessageInfo) data).groupMessage) {
                    recipients.setText("You (Client " + ((MessageInfo) data).clientNum + ") are talking with Clients " + ((MessageInfo) data).groupClients.toString());
                }
                else if(((MessageInfo) data).updateSelected && ((MessageInfo) data).singleMessage){
                    recipients.setText("You (Client " + ((MessageInfo) data).clientNum + ") are talking with Client " + ((MessageInfo) data).singleClient);
                }
            });
        },
                data2->{
            Platform.runLater(()->{
                if(allClientsList != null && !((MessageInfo) data2).updateSelected) {
                    allClientsList.getItems().clear();
                    for(int i =0; i < ((MessageInfo) data2).clients.size(); i++){
                        allClientsList.getItems().add("Client: "+ ((MessageInfo) data2).clients.get(i));
                    }
                }
            });
        }
        );
        clientConnection.start();
    }

    public void indivButton(javafx.event.ActionEvent actionEvent) {
        recipients.setText("You (Client " + yourNum + ") are talking with Client (select client from list)");
        messageInfo.groupClients.clear();
        messageInfo.singleClient = 0;
        everyoneButton.setDisable(false);
        groupButton.setDisable(false);
        individualButton.setDisable(true);

        // talking to only one other person
        typeHere.setDisable(false);
        messageInfo.everyoneMessage = false;
        messageInfo.groupMessage = false;
        messageInfo.singleMessage = true;
    }
    public void groupButton(javafx.event.ActionEvent actionEvent) {
        recipients.setText("You (Client " + yourNum + ") are talking with Clients: (select clients from list and deselect clients by clicking them again)");
        messageInfo.groupClients.clear();
        messageInfo.singleClient = 0;
        everyoneButton.setDisable(false);
        groupButton.setDisable(true);
        individualButton.setDisable(false);

        // talking to some other people
        typeHere.setDisable(false);
        messageInfo.everyoneMessage = false;
        messageInfo.groupMessage = true;
        messageInfo.singleMessage = false;
    }
    public void everyoneButton(javafx.event.ActionEvent actionEvent) {
        recipients.setText("You (Client " + yourNum + ") are talking with Everyone");
        messageInfo.groupClients.clear();
        messageInfo.singleClient = 0;
        everyoneButton.setDisable(true);
        groupButton.setDisable(false);
        individualButton.setDisable(false);

        // talking to everyone
        typeHere.setDisable(false);
        messageInfo.everyoneMessage = true;
        messageInfo.groupMessage = false;
        messageInfo.singleMessage = false;
    }
    public void exit(javafx.event.ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    public void send(ActionEvent actionEvent) {
        messageInfo.message = typeHere.getText();
        messageInfo.updateSelected = false;
        clientConnection.send(messageInfo);
        typeHere.clear();
    }

    public void handleMouseClick(MouseEvent mouseEvent) {
        Integer selectedNum = 0;
        if(allClientsList.getSelectionModel().getSelectedItem() != null){
            String clientName = allClientsList.getSelectionModel().getSelectedItem().toString();
            selectedNum = Integer.parseInt(clientName.substring(8));
        }



        if(messageInfo.groupMessage) {
            if (messageInfo.groupClients.contains(selectedNum)) {
                messageInfo.groupClients.remove(selectedNum);
            } else {
                messageInfo.groupClients.add(selectedNum);
            }
            messageInfo.updateSelected = true;
            clientConnection.send(messageInfo);

        }
        else if(messageInfo.singleMessage){
            messageInfo.singleClient = selectedNum;
            messageInfo.updateSelected = true;
            clientConnection.send(messageInfo);
        }

        allClientsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        allClientsList.requestFocus();
    }
}
