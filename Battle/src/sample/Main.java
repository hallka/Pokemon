package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.animation.AnimationTimer;

public class Main extends Application
{

    private Scene Select = null;
    private Scene Battle = null;
    private Scene WinResult = null;
    private Scene LoseResult = null;

    private static int WINDOW_WIDTH = 1024;
    private static int WINDOW_HEIGHT = 1024;

    @Override
    public void start(Stage stage)
    {
        stage.setTitle( "Pokemon Battle" );

        this.Select(stage);
        this.Battle(stage);
        this.LoseResult(stage);
        this.WinResult(stage);

        stage.setScene(Select);
        stage.show();
    }

    public void Select(Stage stage){
        Group root = new Group();
        this.Select = new Scene(root);
        ImageView image = new ImageView("sample/start.png");
        root.getChildren().add(image);
        Select.setOnKeyPressed(event -> {
            String code = event.getCode().toString();
            if(code.equals("Z")){
                stage.setScene(Battle);
                stage.show();
            }
        });
    }

    public void Battle(Stage stage){

        Group root = new Group();
        this.Battle = new Scene( root );

        ImageView p1 = new ImageView();
        ImageView p2 = new ImageView();

        Canvas canvas = new Canvas( WINDOW_WIDTH, WINDOW_HEIGHT );
        root.getChildren().add( canvas );
        root.getChildren().add(p1);
        root.getChildren().add(p2);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image damage = new Image("sample/damage.png");

        //ポケモンとアイテムの選択
        Trainer t1 = new Trainer(new MT(), new yamata(), new darkelf());
        Trainer t2 = new Trainer(new unicorn(), new seiryu(), new dragon());

        t1.getCenter().setItem(new Heal("mazai", 30));
        t1.getRight().setItem(new Heal("mazai", 30));
        t1.getLeft().setItem(new Heal("mazai", 30));
        t2.getRight().setItem(new Heal("mazai", 30));
        t2.getCenter().setItem(new Heal("mazai", 30));
        t2.getLeft().setItem(new Heal("mazai", 30));
        p1.setImage(t1.getCenter().getImage());
        p2.setImage(t2.getCenter().getImage());

        double p1_x = 40;
        double p1_y = 480;

        double p2_x = 500;
        double p2_y = 20;

        p1.setX(p1_x);
        p1.setY(p1_y);
        p2.setX(p2_x);
        p2.setY(p2_y);


        //キーボード判定
        Battle.setOnKeyPressed(event -> {
            String code = event.getCode().toString();

            if (code.equals("LEFT") && t1.isRotable()) {
                t1.rotL();
                p1.setImage(t1.getCenter().getImage());
            }
            else if (code.equals("RIGHT") && t1.isRotable()) {
                t1.rotR();
                p1.setImage(t1.getCenter().getImage());
            }
            else if (code.equals("UP") && t1.checkRefreshed()) {
                t1.attack(t2.getCenter());
                t1.setRotable(true);
                t2.setRotable(true);
            }
            else if (code.equals("DOWN") && t1.checkItem() && t1.getCenter().isAlive()){
                t1.useItem();
                t1.setRotable(true);
                t2.setRotable(true);
            }

            if (code.equals("Z") && t2.isRotable()) {
                t2.rotL();
                p2.setImage(t2.getCenter().getImage());
            }
            else if (code.equals("C") && t2.isRotable()) {
                t2.rotR();
                p2.setImage(t2.getCenter().getImage());
            }
            else if (code.equals("S") && t2.checkRefreshed()) {
                t2.attack(t1.getCenter());
                t2.setRotable(true);
                t1.setRotable(true);
            }
            else if (code.equals("X") && t2.checkItem() && t2.getCenter().isAlive()){
                t2.useItem();
                t2.setRotable(true);
                t1.setRotable(true);
            }

        });

        final long startNanoTime = System.nanoTime();

        //戦闘のループ処理
        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;

                gc.setFill( new Color(0.85, 0.85, 1.0, 1.0) );
                gc.fillRect(0,0, 1024,1024);

                t1.refresh(3);
                t2.refresh(3);

                //生死判定
                //その後ゲージ回復，表示
                if(t1.getCenter().isAlive()) {
                    gc.setFill(new Color(0.3,0.3,0.3,1.0));
                    gc.fillRect(p2_x - 50, p1_y+10, 75*5, 20);
                    gc.setFill(new Color(1.0,0.0,0,1.0));
                    gc.fillRect(p2_x - 50, p1_y+10, t1.getCenter().getHP()*5, 20);
                    gc.setFill(new Color(0.3,0.3,0.3,1.0));
                    gc.fillRect(p2_x - 50, p1_y+30, 10000*0.03, 15);
                    gc.setFill(new Color(1.0,1.0,0,1.0));
                    gc.fillRect(p2_x - 50, p1_y+30, t1.getCenter().getRefresh()*0.03, 15);
                    gc.strokeText(t1.getCenter().getName(), p2_x -50, p1_y);
                }

                if(t1.getLeft().isAlive()) {
                    gc.setFill(new Color(0.3,0.3,0.3,1.0));
                    gc.fillRect(p2_x - 50, p1_y+70, 75*5, 15);
                    gc.setFill(new Color(1.0,0.0,0,1.0));
                    gc.fillRect(p2_x - 50, p1_y+70, t1.getLeft().getHP()*5, 15);
                    gc.setFill(new Color(0.3,0.3,0.3,1.0));
                    gc.fillRect(p2_x - 50, p1_y+85, 10000*0.03, 10);
                    gc.setFill(new Color(1.0,1.0,0,1.0));
                    gc.fillRect(p2_x - 50, p1_y+85, t1.getLeft().getRefresh()*0.03, 10);
                    gc.strokeText(t1.getLeft().getName(), p2_x -50, p1_y+60);
                }

                if(t1.getRight().isAlive()) {
                    gc.setFill(new Color(0.3,0.3,0.3,1.0));
                    gc.fillRect(p2_x - 50, p1_y+120, 75*5, 15);
                    gc.setFill(new Color(1.0,0.0,0,1.0));
                    gc.fillRect(p2_x - 50, p1_y+120, t1.getRight().getHP()*5, 15);
                    gc.setFill(new Color(0.3,0.3,0.3,1.0));
                    gc.fillRect(p2_x - 50, p1_y+135, 10000*0.03, 10);
                    gc.setFill(new Color(1.0,1.0,0,1.0));
                    gc.fillRect(p2_x - 50, p1_y+135, t1.getRight().getRefresh()*0.03, 10);
                    gc.strokeText(t1.getRight().getName(), p2_x -50, p1_y+110);
                }

                if(t2.getCenter().isAlive()) {
                    gc.setFill(new Color(0.3,0.3,0.3,1.0));
                    gc.fillRect(p1_x, p2_y+10, 75*5, 20);
                    gc.setFill(new Color(1.0,0.0,0,1.0));
                    gc.fillRect(p1_x, p2_y+10, t2.getCenter().getHP()*5, 20);
                    gc.setFill(new Color(0.3,0.3,0.3,1.0));
                    gc.fillRect(p1_x, p2_y+30, 10000*0.03, 15);
                    gc.setFill(new Color(1.0,1.0,0,1.0));
                    gc.fillRect(p1_x, p2_y+30, t2.getCenter().getRefresh()*0.03, 15);
                    gc.strokeText(t2.getCenter().getName(), p1_x, p2_y);
                }

                if(t2.getLeft().isAlive()) {
                    gc.setFill(new Color(0.3,0.3,0.3,1.0));
                    gc.fillRect(p1_x, p2_y+70, 75*5, 15);
                    gc.setFill(new Color(1.0,0.0,0,1.0));
                    gc.fillRect(p1_x, p2_y+70, t2.getLeft().getHP()*5, 15);
                    gc.setFill(new Color(0.3,0.3,0.3,1.0));
                    gc.fillRect(p1_x, p2_y+85, 10000*0.03, 10);
                    gc.setFill(new Color(1.0,1.0,0,1.0));
                    gc.fillRect(p1_x, p2_y+85, t2.getLeft().getRefresh()*0.03, 10);
                    gc.strokeText(t2.getLeft().getName(), p1_x, p2_y+60);
                }

                if(t2.getRight().isAlive()) {
                    gc.setFill(new Color(0.3,0.3,0.3,1.0));
                    gc.fillRect(p1_x, p2_y+120, 75*5, 15);
                    gc.setFill(new Color(1.0,0.0,0,1.0));
                    gc.fillRect(p1_x, p2_y+120, t2.getRight().getHP()*5, 15);
                    gc.setFill(new Color(0.3,0.3,0.3,1.0));
                    gc.fillRect(p1_x, p2_y+135, 10000*0.03, 10);
                    gc.setFill(new Color(1.0,1.0,0,1.0));
                    gc.fillRect(p1_x, p2_y+135, t2.getRight().getRefresh()*0.03, 10);
                    gc.strokeText(t2.getRight().getName(), p1_x, p2_y+110);
                }

                //勝利判定
                if(t1.isWin()){
                    stage.setScene( WinResult );
                    stage.show();
                }
                if(t2.isWin()){
                    stage.setScene( LoseResult );
                    stage.show();
                }

            }
        }.start();

    }

    public void WinResult(Stage stage){
        Group root = new Group();
        this.WinResult = new Scene(root);
        ImageView image = new ImageView("sample/pose_win_boy.png");
        root.getChildren().add(image);
        WinResult.setOnKeyPressed(event -> {
            String code = event.getCode().toString();
            if(code.equals("Z")){
                Platform.exit();
            }
        });
    }

    public void LoseResult(Stage stage){
        Group root = new Group();
        this.LoseResult = new Scene(root);
        ImageView image = new ImageView("sample/pose_lose_boy.png");
        root.getChildren().add(image);
        LoseResult.setOnKeyPressed(event -> {
            String code = event.getCode().toString();
            if(code.equals("Z")){
                Platform.exit();
            }
        });
    }
    public static void main(String[] args)
    {
        launch(args);
    }

}