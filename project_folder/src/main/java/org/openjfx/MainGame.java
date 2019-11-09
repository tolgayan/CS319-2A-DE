package org.openjfx;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class MainGame {
    Pane gameRoot;
    double width;
    double height;
    GameController gameController;

    MainGame(Pane root, double width, double height){
        this.gameRoot = root;
        this.width = width;
        this.height = height;
    }
    public Parent createContent(){
        //create and set content and controller
        gameRoot.setPrefSize(width, height);
        gameController = new GameController(gameRoot , width, height);
        gameController.createContent();
        return gameRoot;
    }
    public void update(Game game){
        gameController.update();
    }

    public void setButtonHandler(Scene scene) { gameController.setButtonHandler(scene);}

}
