package sample;

import java.io.*;

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

public class MainServer extends Application {
    //Param of connection
    static int port = 54312;
    private SocketServer socket;
    private Boolean isConnect = false; //Whether to build connection or not
    private Boolean isInited = false; //Whether to be initialized to PvP battle or not
    private Boolean isReady = false; //Whether to be ready to build connection or not
    private Boolean isFinish = false;

    private Scene ModeSelect = null;
    private Scene Connect = null;
    private Scene Battle = null;
    private Scene WinResult = null;
    private Scene LoseResult = null;

    private static int WINDOW_WIDTH = 1024;
    private static int WINDOW_HEIGHT = 1024;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Pokemon Battle @server");

        this.ModeSelect(stage);
        this.Connect(stage);
        this.Battle(stage);
        this.LoseResult(stage);
        this.WinResult(stage);

        stage.setScene(ModeSelect);
        stage.show();
    }

    public void ModeSelect(Stage stage) {
        Group root = new Group();
        this.ModeSelect = new Scene(root);
        ImageView image = new ImageView("sample/start.png");
        root.getChildren().add(image);
        ModeSelect.setOnKeyPressed(event -> {
            String code = event.getCode().toString();
            if (code.equals("Z")) {
                stage.setScene(Battle);
                stage.show();
            } else if (code.equals("C")) {
                isConnect = true;
                stage.setScene(Connect);
                stage.show();
            }
        });
    }

    public void Connect(Stage stage) {
        Group root = new Group();
        this.Connect = new Scene(root);
        ImageView image = new ImageView("sample/text_tsushin.png");
        root.getChildren().add(image);
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                image.setImage(new Image("sample/text_tsushin.png"));
                if(!isInited) {
                    if (isConnect && !isReady) {
                        socket = new SocketServer(port);
                        isReady = true;
                    }
                    if (isReady && socket.isConnected()) {
                        stage.setScene(Battle);
                        stage.show();
                    }
                }
            }
        }.start();

    }

    public void Battle(Stage stage) {

        Group root = new Group();
        this.Battle = new Scene(root);

        ImageView p1 = new ImageView();
        ImageView p2 = new ImageView();

        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getChildren().add(canvas);
        root.getChildren().add(p1);
        root.getChildren().add(p2);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image damage = new Image("sample/damage.png");

        //init Pokemon and Item
        Trainer t1 = new Trainer(new MT(), new yamata(), new darkelf());
        Trainer t2 = new Trainer(new MT(), new seiryu(), new dragon());

        t1.setItem(new Mazai(), new Aburasoba(), new Coffee());
        t2.setItem(new Coffee(), new Aburasoba(), new Coffee());

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

        //keyboard
        Battle.setOnKeyPressed(event -> {
            String code = event.getCode().toString();
            if (isConnect) {
                if (code.equals("LEFT") && t1.isRotable()) {
                    t1.setRotLflag(true);
                    socket.send("LEFT");
                } else if (code.equals("RIGHT") && t1.isRotable()) {
                    t1.setRotRflag(true);
                    socket.send("RIGHT");
                } else if (t1.getCenter().isAlive()) {
                    if (code.equals("UP") && t1.checkRefreshed()) {
                        t1.setAttackflag(true);
                        socket.send("UP");
                    } else if (code.equals("DOWN") && t1.checkItem()) {
                        t1.setItemflag(true);
                        socket.send("DOWN");
                    }
                }
            } else {
                if (code.equals("LEFT") && t1.isRotable()) {
                    t1.setRotLflag(true);
                } else if (code.equals("RIGHT") && t1.isRotable()) {
                    t1.setRotRflag(true);
                } else if (t1.getCenter().isAlive()) {
                    if (code.equals("UP") && t1.checkRefreshed()) {
                        t1.setAttackflag(true);
                    } else if (code.equals("DOWN") && t1.checkItem()) {
                        t1.setItemflag(true);
                    }
                }

                if (code.equals("Z") && t2.isRotable()) {
                    t2.setRotLflag(true);
                } else if (code.equals("C") && t2.isRotable()) {
                    t2.setRotRflag(true);
                } else if (t2.getCenter().isAlive()) {
                    if (code.equals("S") && t2.checkRefreshed()) {
                        t2.setAttackflag(true);
                    } else if (code.equals("X") && t2.checkItem()) {
                        t2.setItemflag(true);
                    }
                }
            }
        });

        final long startNanoTime = System.nanoTime();
        //Battle Loop
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if(!isFinish) {
                    if (isConnect && !isInited) {
                        //Message Receive init
                        socket.onReceive(new OnReceiveListener() {
                            @Override
                            public void onReceive(String str) {
                                if (str.equals("LEFT") && t2.isRotable()) {
                                    t2.setRotLflag(true);
                                } else if (str.equals("RIGHT") && t2.isRotable()) {
                                    t2.setRotRflag(true);
                                } else if (t2.getCenter().isAlive()) {
                                    if (str.equals("UP") && t2.checkRefreshed()) {
                                        t2.setAttackflag(true);
                                    } else if (str.equals("DOWN") && t2.checkItem()) {
                                        t2.setItemflag(true);
                                    }
                                }

                            }
                        });
                        isInited = true;
                    }

                    double t = (currentNanoTime - startNanoTime) / 1000000000.0;

                    gc.setFill(new Color(0.85, 0.85, 1.0, 1.0));
                    gc.fillRect(0, 0, 1024, 1024);

                    t1.refresh(3);
                    t2.refresh(3);

                    //if alive, show HP
                    CheckGage(gc, t1, p2_x - 50, p1_y);
                    CheckGage(gc, t2, p1_x, p2_y);

                    //Check ActionFlag
                    CheckAction(t1, p1, t2);
                    CheckAction(t2, p2, t1);

                    //if win or lose, jump next scene
                    if (t1.isWin() || t2.checkLose()) {
                        isFinish = true;
                        if (isConnect) {
                            while(true) {
                                if(!socket.isClosed()) {
                                    socket.close();
                                }else{
                                    break;
                                }
                            }
                        }
                        stage.setScene(WinResult);
                        stage.show();
                    }
                    if (t2.isWin() || t1.checkLose()) {
                        isFinish = true;
                        if(isConnect) {
                            while (true) {
                                if (socket.isClosed()) {
                                    break;
                                }
                            }
                        }
                        stage.setScene(LoseResult);
                        stage.show();
                    }

                }
            }
        }.start();

    }

    public void DrawGage(GraphicsContext gc, Pokemon p, double x, double y) {
        gc.setFill(new Color(0.3, 0.3, 0.3, 1.0));
        gc.fillRect(x, y + 10, 75 * 5, 20);
        gc.setFill(new Color(1.0, 0.0, 0, 1.0));
        gc.fillRect(x, y + 10, p.getHP() * 5, 20);
        gc.setFill(new Color(0.3, 0.3, 0.3, 1.0));
        gc.fillRect(x, y + 30, 10000 * 0.03, 15);
        gc.setFill(new Color(1.0, 1.0, 0, 1.0));
        gc.fillRect(x, y + 30, p.getRefresh() * 0.03, 15);
        gc.strokeText(p.getName(), x, y);
    }

    public void CheckGage(GraphicsContext gc, Trainer t, double x, double y) {
        if (t.getCenter().isAlive()) {
            DrawGage(gc, t.getCenter(), x, y);
        }
        if (t.getLeft().isAlive()) {
            DrawGage(gc, t.getLeft(), x, y + 60);
        }
        if (t.getRight().isAlive()) {
            DrawGage(gc, t.getRight(), x, y + 120);
        }
    }

    public void TrotL(Trainer t, ImageView p) {
        t.rotL();
        p.setImage(t.getCenter().getImage());
    }

    public void TrotR(Trainer t, ImageView p) {
        t.rotR();
        p.setImage(t.getCenter().getImage());
    }

    public void Attack(Trainer ta, Trainer tb) {
        ta.attack(tb.getCenter());
        ta.setRotable(true);
        tb.setRotable(true);
    }

    public void UseItem(Trainer tu, Trainer to) {
        tu.useItem();
        tu.setRotable(true);
        to.setRotable(true);
    }

    public void CheckAction(Trainer tm, ImageView p, Trainer ts) {
        if (tm.getRotLflag()) {
            TrotL(tm, p);
            tm.setRotLflag(false);
        }
        if (tm.getRotRflag()) {
            TrotR(tm, p);
            tm.setRotRflag(false);
        }
        if (tm.getAttackflag()) {
            Attack(tm, ts);
            tm.setAttackflag(false);
        }
        if (tm.getItemflag()) {
            UseItem(tm, ts);
            tm.setItemflag(false);
        }
    }

    public void WinResult(Stage stage) {
        Group root = new Group();
        this.WinResult = new Scene(root);
        ImageView image = new ImageView("sample/pose_win_boy.png");
        root.getChildren().add(image);
        WinResult.setOnKeyPressed(event -> {
            String code = event.getCode().toString();
            if (code.equals("Z")) {
                Platform.exit();
            }
        });
    }

    public void LoseResult(Stage stage) {
        Group root = new Group();
        this.LoseResult = new Scene(root);
        ImageView image = new ImageView("sample/pose_lose_boy.png");
        root.getChildren().add(image);

        LoseResult.setOnKeyPressed(event -> {
            String code = event.getCode().toString();
            if (code.equals("Z")) {
                Platform.exit();
            }
        });
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }

}