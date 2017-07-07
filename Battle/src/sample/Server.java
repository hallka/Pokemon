package sample;

/**
 * Created by you on 2017/07/04.
 */
public class Server {
    static int port1 = 54312;
    static int port2 = 53412;
    private static SocketServer socket1;
    private static SocketServer socket2;
    private static Boolean ConnectFlag = false;

    public static void main(String args[]) {
        socket1 = new SocketServer(port1);
        socket2 = new SocketServer(port2);
        while (true) {
            while (true) {
                if (socket1.isConnected() && socket2.isConnected()) {
                    System.out.println("Connection Established");
                    socket1.onReceive(new OnReceiveListener() {
                        @Override
                        public void onReceive(String str) {
                            if (!socket2.isClosed()) {
                                if (!str.equals("null")) {
                                    System.out.println("1 -> 2 send:" + str);
                                    socket2.send(str);
                                }
                            }
                        }
                    });
                    socket2.onReceive(new OnReceiveListener() {
                        @Override
                        public void onReceive(String str) {
                            if (!socket1.isClosed()) {
                                if (!str.equals("null")) {
                                    System.out.println("2 -> 1 send:" + str);
                                    socket1.send(str);
                                }
                            }
                        }
                    });
                    break;
                }
            }
            if(socket1.isClosed()) socket1 = new SocketServer(port1);
            if(socket2.isClosed()) socket2 = new SocketServer(port2);
        }
    }
}
