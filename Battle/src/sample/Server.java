package sample;

/**
 * Created by you on 2017/07/04.
 */
public class Server {
    static int port = 54312;
    private static SocketServer socket1;
    private static SocketServer socket2;
    private static Boolean ConnectFlag = false;

    public static void main(String args[]){
        socket1 = new SocketServer(port);
        socket2 = new SocketServer(port);
        while(true){
            if(socket1.isConnected() && socket2.isConnected()){
                System.out.println("Connection Established");
                break;
            }
        }
        socket1.onReceive(new OnReceiveListener() {
            @Override
            public void onReceive(String str) {
                if(socket2.isConnected()) {
                    if (str.equals("CLOSE")) {
                        socket2.close();
                    } else if (!str.equals("null")){
                        System.out.println("1 -> 2 send:" + str);
                        socket2.send(str);
                    }
                }
            }
        });
        socket2.onReceive(new OnReceiveListener() {
            @Override
            public void onReceive(String str) {
                if (socket1.isConnected()) {
                    if (str.equals("CLOSE")) {
                        System.out.println("2 -> 1 send:" + str);
                        socket1.close();
                    } else if (!str.equals("null")) socket1.send(str);
                }
            }
        });
    }
}
