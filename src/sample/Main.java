package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    private static Datos datos;

    @Override
    public void start(Stage primaryStage) throws Exception{
        TextInputDialog inputUsuario = new TextInputDialog("root");
        inputUsuario.setContentText("Nombre de usuario de MySQL para acceder a la base de datos");
        inputUsuario.setTitle("Wall Breaker");
        inputUsuario.setHeaderText("Usuario de MySQL");
        inputUsuario.showAndWait();
        String usuario = inputUsuario.getEditor().getText();

        TextInputDialog inputPassword = new TextInputDialog();
        inputPassword.setContentText("Contraseña del usuario");
        inputPassword.setTitle("Wall Breaker");
        inputPassword.setHeaderText("Contraseña de usuario");
        inputPassword.showAndWait();
        String password = inputPassword.getEditor().getText();

        try {
            datos = new Datos(usuario, password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Wall Breaker");
            error.setHeaderText("Error conectando a la base de datos");
            error.setContentText("Puede que el usuario y/o contraseña sean incorrectos, o que el servidor no esté " +
                    "corrinedo");
            error.showAndWait();
            return;
        }

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Principal.fxml")));
        primaryStage.setTitle("Wall Breaker");
        Scene scene = new Scene(root);
        String css = Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Datos conseguirDatos() {
        return datos;
    }
}
