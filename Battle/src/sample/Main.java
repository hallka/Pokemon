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

import java.io.*;

public class Main extends Application {
    private static int port = 54312;
    private SocketClient socket = null;
    private String host = "PC";
    private Boolean isInited = false;
    private Boolean isFinish = false;
    private Boolean isConnect1 = false;
    private Boolean isConnect2 = false;

    private Trainer t1 = null;
    private Trainer t2 = null;
    private Pokemon[] plist = {new chimaira(new Mazai()), new medusa(new Mazai()), new darkelf(new Mazai()), new kyubi(new Mazai()), new cthulhu(new Mazai()), new MT(new Mazai()), new seiryu(new Mazai()), new unicorn(new Mazai()), new dragon(new Mazai()), new yamata(new Mazai())};
    private Boolean[] isUsed = {false,false,false,false,false,false,false,false,false,false};
    private int[] reserve1 = {-1, -1, -1};
    private int[] reserve2 = {-1, -1, -1};
    private int cnt1 = 0;
    private int ptr1 = 0;
    private int cnt2 = 0;
    private int ptr2 = 0;

    private Scene Top = null;
    private Scene Connect = null;
    private Scene CharaSelect = null;
    private Scene Ready = null;
    private Scene Battle = null;
    private Scene WinResult = null;
    private Scene LoseResult = null;

    private static int WINDOW_WIDTH = 1024;
    private static int WINDOW_HEIGHT = 1024;

    public Main() throws IOException {
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Pokemon-ish Battle");

        this.Top(stage);
        stage.setScene(Top);
        stage.show();
    }

    public void Top(Stage stage) {
        Group root = new Group();
        this.Top = new Scene(root);
        ImageView image = new ImageView("sample/start.png");
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getChildren().add(canvas);
        root.getChildren().add(image);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(new Color(0.85, 0.85, 1.0, 1.0));
        gc.fillRect(0, 0, 1024, 1024);
        Top.setOnKeyPressed(event -> {
            this.Connect(stage);
            stage.setScene(Connect);
            stage.show();
        });
    }

    public void Connect(Stage stage) {
        Group root = new Group();
        this.Connect = new Scene(root);
        ImageView image = new ImageView("sample/text_tsushin.png");
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getChildren().add(canvas);
        root.getChildren().add(image);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(new Color(0.85, 0.85, 1.0, 1.0));
        gc.fillRect(0, 0, 1024, 1024);
        while(true){
            if (socket == null) {
                socket = new SocketClient(host, port);
                isConnect1 = true;
            }else if(isConnect1){
                this.CharaSelect(stage);
                stage.setScene(CharaSelect);
                stage.show();
                break;
            }
        }
    }

    public void CharaSelect(Stage stage) {

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

        double p1_x = 40;
        double p1_y = 480;

        double p2_x = 500;
        double p2_y = 20;

        p1.setX(p1_x);
        p1.setY(p1_y);
        p2.setX(p2_x);
        p2.setY(p2_y);
        this.Ready(stage);

        //keyboard
        CharaSelect.setOnKeyPressed(event -> {
            String str = event.getCode().toString();
            if(str.equals("DOWN")){
                socket.send("DOWN");
                if(ptr1<2) {
                    cnt1++;
                    while (true) {
                        if (cnt1 > 9) cnt1 = 0;
                        if (isUsed[cnt1]) {
                            cnt1++;
                        } else {
                            break;
                        }
                    }
                }
            }else if(str.equals("UP")){
                if(ptr1<2) {
                    cnt1--;
                    while (true) {
                        if (cnt1 < 0) cnt1 = 9;
                        if (isUsed[cnt1]) {
                            cnt1--;
                        } else {
                            break;
                        }
                    }
                }
            }else if(str.equals("Z")){
                socket.send("SELECT");
                if(ptr1<3) {
                    if (!isUsed[cnt1]) {
                        isUsed[cnt1] = true;
                        reserve1[ptr1] = cnt1;
                        ptr1++;
                    }
                }
            }else if(str.equals("C")){
                socket.send("BACK");
                if(ptr1 > 0) {
                    isUsed[reserve1[ptr1]] = false;
                    reserve1[ptr1] = -1;
                    ptr1--;
                }
            }
        });

        final long startNanoTime = System.nanoTime();

        //Battle Loop
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if(!isFinish) {
                    //Message Receive init
                    if (isConnect1 && !isInited) {
                        socket.onReceive(new OnReceiveListener() {
                            @Override
                            public void onReceive(String str) {
                                if(str.equals("DOWN")){
                                    if(ptr2 < 3) {
                                        cnt2++;
                                        while (true) {
                                            if (cnt2 > 9) cnt2 = 0;
                                            if (isUsed[cnt2]) {
                                                cnt2++;
                                            } else {
                                                break;
                                            }
                                        }
                                    }
                                }else if(str.equals("UP")){
                                    if(ptr2 < 3) {
                                        cnt2--;
                                        while (true) {
                                            if (cnt2 < 0) cnt2 = 9;
                                            if (isUsed[cnt2]) {
                                                cnt2--;
                                            } else {
                                                break;
                                            }
                                        }
                                    }
                                }else if(str.equals("SELECT")){
                                    if(ptr2<3) {
                                        if (!isUsed[cnt2]) {
                                            isUsed[cnt2] = true;
                                            reserve2[ptr2] = cnt2;
                                            ptr2++;
                                        }
                                    }
                                }else if(str.equals("BACK")){
                                    if(ptr2 > 0) {
                                        isUsed[reserve2[ptr2]] = false;
                                        reserve2[ptr2] = -1;
                                        ptr2--;
                                    }
                                }

                            }
                        });
                        isInited = true;
                    }

                    double t = (currentNanoTime - startNanoTime) / 1000000000.0;

                    gc.setFill(new Color(0.85, 0.85, 1.0, 1.0));
                    gc.fillRect(0, 0, 1024, 1024);

                    p1.setImage(plist[cnt1].getImage());
                    p2.setImage(plist[cnt2].getImage());

                    CheckName1(gc, p2_x - 50, p1_y);
                    CheckName2(gc, p1_x, p2_y);

                    if(ptr1 == 3 && ptr2 == 3){
                        t1 = new Trainer(plist[reserve1[1]],plist[reserve1[0]],plist[reserve1[2]]);
                        t2 = new Trainer(plist[reserve2[1]],plist[reserve2[0]],plist[reserve2[2]]);
                        isFinish = true;
                        socket.close();
                        stage.setScene(Ready);
                        stage.show();
                    }

                }
            }
        }.start();

    }

    public void DrawName(GraphicsContext gc, Pokemon p, double x, double y) {
        gc.strokeText(p.getName() + "(" + p.getItem().getName() + ")", x, y);
    }

    public void CheckName1(GraphicsContext gc, double x, double y) {
        for(int i = 0;i<ptr1;i++) {
            DrawName(gc, plist[reserve1[i]], x, y+i*30);
        }
    }

    public void CheckName2(GraphicsContext gc, double x, double y) {
        for(int i = 0;i<ptr2;i++) {
            DrawName(gc, plist[reserve1[i]], x, y+i*30);
        }
    }

    public void Ready(Stage stage) {
        Group root = new Group();
        this.Connect = new Scene(root);
        ImageView image = new ImageView("sample/text_tsushin.png");
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getChildren().add(canvas);
        root.getChildren().add(image);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(new Color(0.85, 0.85, 1.0, 1.0));
        gc.fillRect(0, 0, 1024, 1024);
        while(true){
            if (socket == null) {
                socket = new SocketClient(host, port);
                isConnect2 = true;
            }else if(isConnect2){
                isInited = false;
                isFinish = false;
                this.Battle(stage);
                stage.setScene(Battle);
                stage.show();
                break;
            }
        }
    }


    public void Battle(Stage stage) {
        this.LoseResult(stage);
        this.WinResult(stage);

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
        });

        final long startNanoTime = System.nanoTime();

        //Battle Loop
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if(!isFinish) {
                    //Message Receive init
                    if (isConnect2 && !isInited) {

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
                    }else if(isInited){

                        double t = (currentNanoTime - startNanoTime) / 1000000000.0;

                        gc.setFill(new Color(0.85, 0.85, 1.0, 1.0));
                        gc.fillRect(0, 0, 1024, 1024);
                        p1.setImage(t1.getCenter().getImage());
                        p2.setImage(t2.getCenter().getImage());

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
                            while (true) {
                                if (!socket.isClosed()) {
                                    socket.close();
                                } else {
                                    break;
                                }
                            }
                            stage.setScene(WinResult);
                            stage.show();
                        }

                        if (t2.isWin() || t1.checkLose()) {
                            isFinish = true;
                            while (true) {
                                if (socket.isClosed()) {
                                    break;
                                }
                            }
                            stage.setScene(LoseResult);
                            stage.show();
                        }
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
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getChildren().add(canvas);
        root.getChildren().add(image);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(new Color(0.85, 0.85, 1.0, 1.0));
        gc.fillRect(0, 0, 1024, 1024);
        WinResult.setOnKeyPressed(event -> {
            Platform.exit();
        });
    }

    public void LoseResult(Stage stage) {
        Group root = new Group();
        this.LoseResult = new Scene(root);
        ImageView image = new ImageView("sample/pose_lose_boy.png");
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getChildren().add(canvas);
        root.getChildren().add(image);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(new Color(0.85, 0.85, 1.0, 1.0));
        gc.fillRect(0, 0, 1024, 1024);
        LoseResult.setOnKeyPressed(event -> {
            Platform.exit();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}