import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class ListaSockets{
    private Socket[] socket; // Los otros sockets
    private PrintStream[] salida;
    private int num;
    private String nombre;
    //Nombre

    public ListaSockets(int n, String nombre) { //Por parametro nomClient
        socket = new Socket[n];
        salida = new PrintStream[n];
        num = 0;
        this.nombre = nombre;
        //Nombre = nomclient
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
    private ArrayList<String> messagesSaved = new ArrayList<String>();

    public Serv(Socket s, ListaSockets ls) {
        socket = s;
        listaSockets = ls;
    }

    public void run() {
        BufferedReader entrada;

        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String mensaje;
            //guardar arraylist strings para mensajes MENSAJES NORMALES
            //***NomCLIENT,Tots (Todos los mensajes) envia todos los mensajes del arraylist al cliente que lo ha dicho(nomclient)
            //Buscar en llistaSockets para buscar el nombre del cliente que correspongui amb nomClient
            //Si coincide el NomClient con el nombre del que hace la petición listaSockets.getSalida(i).println(mensaje); mensaje pertenece un string de ArrayList mensaje

            //Si solo quieres a una persona,***Yo;DestinoPersona;Mensaje
            //Split
            while( (mensaje = entrada.readLine()) != null){
                String[] parts = mensaje.split("\\*"); //Esto detectara si hay un asterisco...
                if (parts.length >= 2) {
                    for (int i = 0; i< messagesSaved.size(); i++) {
                        listaSockets.getSalida(0).println(messagesSaved.get(i));
                    }
                } else {
                    for (int i = 0; i < listaSockets.length(); i++) {
                        listaSockets.getSalida(i).println(mensaje);
                        messagesSaved.add(mensaje);
                    }
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
        ListaSockets listaSockets = new ListaSockets(max,Cliente.login);
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