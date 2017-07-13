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
    static int port1 = 54312;
    private SocketServer socket1 = null;
    private SocketServer socket2 = null;
    private Boolean isConnect1 = false;
    private Boolean isConnect2 = false;
    private Boolean isInited = false; //Whether to be initialized to PvP battle or not
    private Boolean isReady = false; //Whether to be ready to build connection or not
    private Boolean isFinish1 = false;
    private Boolean isFinish2 = false;

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

    @Override
    public void start(Stage stage) {
        stage.setTitle("Pokemon Battle @server");

        this.Top(stage);
        this.Connect(stage);
        this.CharaSelect(stage);
        this.Ready(stage);
        this.Battle(stage);
        this.LoseResult(stage);
        this.WinResult(stage);

        stage.setScene(Top);
        stage.show();
    }

    public void Top(Stage stage) {
        Group root = new Group();
        this.Top = new Scene(root);
        ImageView image = new ImageView("sample/start.png");
        root.getChildren().add(image);
        Top.setOnKeyPressed(event -> {
            String code = event.getCode().toString();
            if (code.equals("C")) {
                isConnect1 = true;
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
                if(!isInited && !isFinish1) {
                    if (isConnect1 && !isReady) {
                        socket1 = new SocketServer(port1);
                        isReady = true;
                    }
                    if (isReady && socket1.isConnected()) {
                        stage.setScene(CharaSelect);
                        stage.show();
                    }
                }
            }
        }.start();
    }

    public void CharaSelect(Stage stage) {

        Group root = new Group();
        this.CharaSelect = new Scene(root);

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
        CharaSelect.setOnKeyPressed(event -> {
            String str = event.getCode().toString();
            if(str.equals("DOWN")){
                socket1.send("DOWN");
                if(ptr1<3) {
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
                socket1.send("UP");
                if(ptr1<3) {
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
                socket1.send("SELECT");
                if(ptr1<3) {
                    if (!isUsed[cnt1]) {
                        isUsed[cnt1] = true;
                        reserve1[ptr1] = cnt1;
                        ptr1++;
                    }
                }
            }else if(str.equals("C")){
                socket1.send("BACK");
                if(ptr1 > 0) {
                    ptr1--;
                    isUsed[reserve1[ptr1]] = false;
                    reserve1[ptr1] = -1;
                }
            }
        });

        final long startNanoTime = System.nanoTime();

        //Battle Loop
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if(!isFinish1) {
                    //Message Receive init
                    if (isConnect1 && !isInited) {
                        socket1.onReceive(new OnReceiveListener() {
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
                                        ptr2--;
                                        isUsed[reserve2[ptr2]] = false;
                                        reserve2[ptr2] = -1;
                                    }
                                }else if(str.equals("END")){
                                    isFinish1 = true;
                                    socket1.send("END");
                                }

                            }
                        });
                        isInited = true;
                    }

                    gc.setFill(new Color(0.85, 0.85, 1.0, 1.0));
                    gc.fillRect(0, 0, 1024, 1024);

                    p1.setImage(plist[cnt1].getImage());
                    p2.setImage(plist[cnt2].getImage());

                    CheckName1(gc, p2_x - 50, p1_y);
                    CheckName2(gc, p1_x, p2_y);

                    if(ptr1 == 3 && ptr2 == 3){
                        t1 = new Trainer(plist[reserve1[1]],plist[reserve1[0]],plist[reserve1[2]]);
                        t2 = new Trainer(plist[reserve2[1]],plist[reserve2[0]],plist[reserve2[2]]);
                        while(true) {
                            if(isFinish1) {
                                stage.setScene(Ready);
                                stage.show();
                                break;
                            }
                        }
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
            if(reserve1[i] != -1) DrawName(gc, plist[reserve1[i]], x, y+i*30);
        }
    }

    public void CheckName2(GraphicsContext gc, double x, double y) {
        for(int i = 0;i<ptr2;i++) {
            if(reserve2[i] != -1) DrawName(gc, plist[reserve2[i]], x, y+i*30);
        }
    }

    public void Ready(Stage stage) {
        Group root = new Group();
        this.Ready = new Scene(root);
        ImageView image = new ImageView("sample/text_tsushin.png");
        root.getChildren().add(image);
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if(isFinish1 && !isFinish2) {
                    if(!isConnect2) {
                        socket1.send("CLOSE");
                        socket1.close();
                        socket2 = new SocketServer(port1);
                        isConnect2 = true;
                    }
                    if (isConnect2 && socket2.isConnected()){
                        isInited = false;
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
            if (isConnect2) {
                if (code.equals("LEFT") && t1.isRotable()) {
                    t1.setRotLflag(true);
                    socket2.send("LEFT");
                } else if (code.equals("RIGHT") && t1.isRotable()) {
                    t1.setRotRflag(true);
                    socket2.send("RIGHT");
                } else if (t1.getCenter().isAlive()) {
                    if (code.equals("UP") && t1.checkRefreshed()) {
                        t1.setAttackflag(true);
                        socket2.send("UP");
                    } else if (code.equals("DOWN") && t1.checkItem()) {
                        t1.setItemflag(true);
                        socket2.send("DOWN");
                    }
                }
            }
        });

        final long startNanoTime = System.nanoTime();
        //Battle Loop
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if(isConnect2 && !isFinish2) {
                    if (!isInited) {

                        socket2.onReceive(new OnReceiveListener() {
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
                        isFinish2 = true;
                        while(true) {
                            if(!socket2.isClosed()) {
                                socket2.send("CLOSE");
                                socket2.close();
                            }else{
                                break;
                            }
                        }
                        stage.setScene(WinResult);
                        stage.show();
                    }
                    if (t2.isWin() || t1.checkLose()) {
                        isFinish2 = true;
                        while (true) {
                            if (socket2.isClosed()) {
                                break;
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