package com.example.gomokuexample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GomokuGameFX extends Application {
    private static final int CELL_SIZE = 40;
    private static final int BOARD_SIZE = 9; //The size is 15 actually
    private GomokuGame game;

    @Override
    public void start(Stage primaryStage) {
        game = new GomokuGame(BOARD_SIZE);
        BorderPane root = new BorderPane();
        Canvas canvas = new Canvas(CELL_SIZE * BOARD_SIZE, CELL_SIZE * BOARD_SIZE);
        root.setCenter(canvas);
        //MenuPane setup
        StackPane menuPane = new StackPane();
        menuPane.setPadding(new Insets(10));
        menuPane.setPrefWidth(CELL_SIZE * BOARD_SIZE / 5 * 2);
        menuPane.setStyle("-fx-background-color: lightgray;");
        //Labels setup
        Label settings = new Label("Size: " + (BOARD_SIZE-1) + "*" + (BOARD_SIZE-1) + "\nPlayer: 1 and 2");
        settings.setAlignment(Pos.CENTER);
        settings.setStyle("-fx-font-size: 20px;");
        Label currentPlayer = new Label(game.getCurrentPlayer());
        currentPlayer.setAlignment(Pos.CENTER);
        currentPlayer.setStyle("-fx-font-size: 20px;");
        Label gameResult = new Label(game.getGameResult());
        gameResult.setAlignment(Pos.CENTER);
        gameResult.setStyle("-fx-font-size: 20px;");
        //Buttons setup
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e ->{
            game.reset();
            drawBoard(canvas.getGraphicsContext2D());
        });
        resetButton.setStyle("-fx-min-width: 100px; -fx-min-height: 40px;");
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> primaryStage.close());
        exitButton.setStyle("-fx-min-width: 100px; -fx-min-height: 40px;");
        HBox.setHgrow(resetButton, Priority.ALWAYS);
        HBox.setHgrow(exitButton, Priority.ALWAYS);
        buttonBox.getChildren().addAll(resetButton, exitButton);

        VBox menuContent = new VBox();
        menuContent.getChildren().addAll(settings, currentPlayer, gameResult, buttonBox);
        StackPane.setAlignment(menuContent, Pos.CENTER);
        menuPane.getChildren().add(menuContent);

        root.setRight(menuPane);

        drawBoard(canvas.getGraphicsContext2D());
        //Mouse click event(it can tolerate error)
        canvas.setOnMouseClicked(e -> {
            double mouseX = e.getX();
            double mouseY = e.getY();
            int x = (int) (mouseX / CELL_SIZE);
            int y = (int) (mouseY / CELL_SIZE);
            double xOffset = mouseX - x * CELL_SIZE;
            double yOffset = mouseY - y * CELL_SIZE;
            // Place the stone at the next intersection if the offset exceeds a certain threshold
            if (xOffset > CELL_SIZE / 2) {
                x++;
            }
            if (yOffset > CELL_SIZE / 2) {
                y++;
            }

            if (game.move(x, y)) {
                drawBoard(canvas.getGraphicsContext2D());
                gameResult.setText(game.getGameResult());
                currentPlayer.setText(game.getCurrentPlayer());
                if (game.isGameOver()) {
                    System.out.println("Game over! The winner is player " + game.getWinner() + "!");
                }
            } else {
                System.out.println("Invalid move!");
            }
        });
        //Scene setup
        Scene scene = new Scene(root, CELL_SIZE * BOARD_SIZE * 1.4, CELL_SIZE * BOARD_SIZE);
        primaryStage.setTitle("Gomoku Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    //Draw the game board and stones
    private void drawBoard(GraphicsContext gc) {
        //Draw the game board
        gc.clearRect(0, 0, CELL_SIZE * BOARD_SIZE, CELL_SIZE * BOARD_SIZE);
        gc.setStroke(Color.BLACK);
        for (int i = 0; i < BOARD_SIZE; i++) {
            gc.strokeLine(i * CELL_SIZE, 0, i * CELL_SIZE, CELL_SIZE * BOARD_SIZE);
            gc.strokeLine(0, i * CELL_SIZE, CELL_SIZE * BOARD_SIZE, i * CELL_SIZE);
        }
        //Draw the stones
        int[][] board = game.getBoard();
        for (int i = 1; i < BOARD_SIZE; i++) {
            for (int j = 1; j < BOARD_SIZE; j++) {
                double x = (i - 0.4) * CELL_SIZE;
                double y = (j - 0.4) * CELL_SIZE;
                double w = CELL_SIZE * 0.8;
                double h = CELL_SIZE * 0.8;
                if (board[i][j] == 1) {
                    gc.setFill(Color.BLACK);
                    gc.fillOval(x, y, w, h);
                    gc.strokeOval(x, y, w, h);
                } else if (board[i][j] == 2) {
                    gc.setFill(Color.WHITE);
                    gc.fillOval(x, y, w, h);
                    gc.strokeOval(x, y, w, h);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}