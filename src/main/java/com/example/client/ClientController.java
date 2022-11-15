package com.example.client;

import com.example.client.wsdl.Request;
import com.example.client.wsdl.Response;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@FxmlView("client.fxml")
public class ClientController extends WebServiceGatewaySupport {
    @FXML
    private Label fileName;
    @FXML
    private TextArea result;

    private File file;

    @FXML
    protected void sendFile() {
        if (this.file != null) {
            Response getResponse = getFileHashCode();
            result.setText(getResponse.getHash());
        } else {
            result.setText("You have not selected a file to send. Upload file.");
        }
    }

    public void upload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            fileName.setText(file.getName());
            this.file = file;
        }
    }

    public Response getFileHashCode() {
        Request request = new Request();

        byte[] fileContent;
        try {
            fileContent = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        request.setData(fileContent);

        Response response = (Response) getWebServiceTemplate()
                .marshalSendAndReceive("http://localhost:8080/ws/service",
                        request,
                        new SoapActionCallback(
                                "http://com/example/server/request"));

        return response;
    }
}
