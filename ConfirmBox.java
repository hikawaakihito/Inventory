import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.*;
import javafx.geometry.*;


public class ConfirmBox {
    private static boolean answer;
    private static Stage window;
    
    public static boolean category(String parent, String name){
        String title = "カテゴリーの新規追加";
        String message = parent + " / " + name + "を本当に追加しますか？\n"
                    + "(ヒント：メインビューで選択したカテゴリーに入ります。)";
        return confirm(title, message);
    }

    public static boolean item(String parent, String name, double price, int quantity){
        String title = "在庫の新規追加";
        String message = parent + " | " + name + " | " + price + "円 | " + quantity + "個　" + "\n\n"
                    + "を本当に追加しますか？ (ヒント：メインビューで選択したカテゴリーに入ります。)";
        return confirm(title, message);
    }

    public static boolean confirm(String title, String message){
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(300);

        Label label = new Label();
        label.setText(message);

        Button yesButton = new Button("はい");
        Button noButton = new Button("戻る");

        yesButton.setOnAction(e -> {
            answer = true;
            window.close();
        });
        noButton.setOnAction(e -> {
            answer = false;
            window.close();
        });

        HBox buttonHBox = new HBox(10);
        buttonHBox.setAlignment(Pos.CENTER);
        buttonHBox.getChildren().addAll(yesButton, noButton);


        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10,10,10,10));
        layout.getChildren().addAll(label, buttonHBox);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                Node focusOwner = scene.getFocusOwner();
                if (focusOwner instanceof Button) {
                    ((Button) focusOwner).fire();
                }
            }
        });

        window.setScene(scene);
        window.showAndWait();

        return answer;
    }

    public static void alert(String title, String message){
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(300);

        Label label = new Label();
        label.setText(message);

        Button noButton = new Button("戻る");

        noButton.setOnAction(e -> {
            window.close();
        });

        HBox buttonHBox = new HBox(10);
        buttonHBox.setAlignment(Pos.CENTER);
        buttonHBox.getChildren().addAll(noButton);


        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10,10,10,10));
        layout.getChildren().addAll(label, buttonHBox);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                Node focusOwner = scene.getFocusOwner();
                if (focusOwner instanceof Button) {
                    ((Button) focusOwner).fire();
                }
            }
        });

        window.setScene(scene);
        window.showAndWait();
    }
}