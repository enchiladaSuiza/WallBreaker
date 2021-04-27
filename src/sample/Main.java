package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Objects;
import java.util.Optional;

public class Main extends Application {
    private static Datos datos;
    private static Image icono;

    @Override
    public void start(Stage primaryStage) throws Exception {
        icono = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/Icono.png")));
        // Ventana para conseguir los datos
        Dialog<Pair<String, String>> dialogo = new Dialog<>();
        dialogo.setTitle("Wall Breaker");
        dialogo.setHeaderText("Datos de usuario");
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Stage temp = (Stage)dialogo.getDialogPane().getScene().getWindow();
        temp.getIcons().add(icono);
        TextField usuarioField = new TextField("root");
        PasswordField passwordField = new PasswordField();
        passwordField.setText("owoeweuwu"); // Mi contraseña
        GridPane dialogoLayout = new GridPane();
        dialogoLayout.setHgap(10.0);
        dialogoLayout.setVgap(5.0);
        dialogoLayout.setAlignment(Pos.CENTER_LEFT);
        dialogoLayout.addRow(0, new Label("Nombre de usuario"), usuarioField);
        dialogoLayout.addRow(1, new Label("Contraseña"), passwordField);
        dialogo.getDialogPane().setContent(dialogoLayout);
        dialogo.setResultConverter(boton -> {
            if (boton == ButtonType.OK) {
                return new Pair<>(usuarioField.getText(), passwordField.getText());
            }
            else {
                return null;
            }
        });
        usuarioField.requestFocus();
        Optional<Pair<String, String>> usuarioConPassword = dialogo.showAndWait();
        String usuario;
        String password;
        if (usuarioConPassword.isPresent()) {
            usuario = usuarioConPassword.get().getKey();
            password = usuarioConPassword.get().getValue();
        } else {
            return;
        }
        if (!(usuario.isBlank() && password.isBlank())) {
            try {
                datos = new Datos(usuario, password);
            } catch (Exception e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Wall Breaker");
                error.setHeaderText("Error conectando a la base de datos");
                error.setContentText("Puede que el usuario y/o contraseña sean incorrectos, o que el servidor no esté " +
                        "corriendo. Mensaje de error:\n" + e.getMessage());
                error.showAndWait();
                return;
            }
        } else {
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Principal.fxml"));
        loader.getController();
        Parent root = loader.load();
        primaryStage.setTitle("Wall Breaker");
        primaryStage.getIcons().add(icono);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);
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
    public static Image conseguirIcono() { return icono; }
}
