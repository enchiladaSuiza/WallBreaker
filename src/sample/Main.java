package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Optional;

public class Main extends Application {
    private static Datos datos;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Ventana para conseguir el nombre de usuario
        TextInputDialog inputUsuario = new TextInputDialog("root");
        inputUsuario.setContentText("Nombre de usuario de MySQL para acceder a la base de datos");
        inputUsuario.setTitle("Wall Breaker");
        inputUsuario.setHeaderText("Usuario de MySQL");
        Button cancelar = (Button)inputUsuario.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelar.addEventFilter(ActionEvent.ACTION, actionEvent -> System.exit(0));
        Optional<String> usuario = inputUsuario.showAndWait();

        // Ventana para conseguir la contraseña
        Dialog<String> inputPassword = new Dialog<>();
        inputPassword.setTitle("Wall Breaker");
        inputPassword.setHeaderText("Contraseña de usuario");
        inputPassword.setGraphic(inputUsuario.getGraphic());
        inputPassword.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        PasswordField passwordField = new PasswordField();
        passwordField.setText("owoeweuwu");
        HBox passwordLayout = new HBox();
        passwordLayout.setAlignment(Pos.CENTER_LEFT);
        passwordLayout.setSpacing(10);
        passwordLayout.getChildren().addAll(new Label("Contraseña de usuario"), passwordField);
        inputPassword.getDialogPane().setContent(passwordLayout);
        inputPassword.setResultConverter(boton -> {
            if (boton == ButtonType.OK) {
                return passwordField.getText();
            }
            else {
                return null;
            }
        });
        passwordField.requestFocus();
        Optional<String> password = inputPassword.showAndWait();

        if (password.isPresent() && usuario.isPresent()) {
            try {
                datos = new Datos(usuario.get(), password.get());
            } catch (Exception e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Wall Breaker");
                error.setHeaderText("Error conectando a la base de datos");
                error.setContentText("Puede que el usuario y/o contraseña sean incorrectos, o que el servidor no esté " +
                        "corriendo. Mensaje de error: " + e.getMessage());
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
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/Icono.png"))));
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
}
