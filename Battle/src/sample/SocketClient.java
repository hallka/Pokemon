package sample;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by you on 2017/06/22.
 */

public class SocketClient {
    private Socket socket;
    private InetAddress addr;
    private PrintWriter Writer;
    private BufferedReader Reader;
    private OnReceiveListener onReceiveListener;

    private Boolean CloseFlag = false;

    public SocketClient(String host, int port){
        try{
            addr = InetAddress.getByName(host);
            this.socket = new Socket(addr, port);
            this.Writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream())));
            this.Reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void send(String data){
        try{
            Writer.println(data);
            Writer.flush();
            System.out.println("Send :" + data);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void onReceive(final OnReceiveListener onReceive){
        new Thread(new Runnable(){
            public void run(){
                while(true){
                    if(CloseFlag) break;
                    try{
                        String line = Reader.readLine();
                        if(!line.equals("null")) {
                            System.out.println("Receive :" + line);
                        }
                        onReceive.onReceive(line);
                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void close(){
        try {
            CloseFlag = true;
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected(){
        return socket.isConnected();
    }
}
