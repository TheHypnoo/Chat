import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

class ListaSockets{
    private Socket[] socket; // Los otros sockets
    private PrintStream[] salida;
    private int num;

    public ListaSockets(int n) {
        socket = new Socket[n];
        salida = new PrintStream[n];
        num = 0;
    }

    public void add(Socket s){
        try {
            salida[num] = new PrintStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket[num++] = s;
    }

    public int length(){
        return num;
    }

    public Socket get(int n) {
        return socket[n];
    }

    public PrintStream getSalida(int n) {
        return salida[n];

    }
}

class Serv implements Runnable{
    private Socket socket; // Socket propio.
    private ListaSockets listaSockets; // Los otros sockets

    public Serv(Socket s, ListaSockets ls) {
        socket = s;
        listaSockets = ls;
    }

    public void run() {
        BufferedReader entrada;

        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String mensaje;
            while( (mensaje = entrada.readLine()) != null){
                for (int i = 0; i < listaSockets.length(); i++) {
                    listaSockets.getSalida(i).println(mensaje);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

public class Servidor {
    private static final int puerto = 9999;
    private static final int max = 10000;

    public static void main(String[] args) {
        ServerSocket serverSocket;
        ListaSockets listaSockets = new ListaSockets(max);
        Serv[] serv = new Serv[max];
        Thread[] thread = new Thread[max];
        try {
            serverSocket = new ServerSocket(puerto);
            for(int i = 0; i < max; i++){
                Socket socket = serverSocket.accept();
                listaSockets.add(socket);
                serv[i] = new Serv(socket, listaSockets);
                thread[i] = new Thread(serv[i]);
                thread[i].start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}