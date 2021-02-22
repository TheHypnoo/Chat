import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import javax.swing.*;

class AccionEnviar implements ActionListener{
    private JTextField areaTexto;
    private PrintStream salida;
    private String login;

    public AccionEnviar(Socket s, JTextField at, String l){
        areaTexto = at;
        try {
            salida = new PrintStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        login = l;
    }


    public void actionPerformed(ActionEvent e){
        salida.println(login + "> " + areaTexto.getText() );
        areaTexto.setText("");
    }
}

class Talk {
    private Socket socket;
    private String login;

    public Talk(Socket s, String l){
        socket = s;
        login = l;
    }

    public void hablar(){
        JFrame marco = new JFrame(login);
        marco.setLayout(new BorderLayout());
        JTextArea areaTexto = new JTextArea("");
        areaTexto.setEditable(false);
        marco.add(areaTexto, "Center");
        JPanel panel = new JPanel(new FlowLayout());
        marco.add(panel, "South");
        JTextField campoTexto = new JTextField(30);
        panel.add(campoTexto);
        JButton botonEnviar = new JButton("Enviar");
        JButton botonRecuperar = new JButton("Recuperar chat");
        AccionEnviar ae = new AccionEnviar(socket, campoTexto, login);
        botonEnviar.addActionListener(ae);
        panel.add(botonEnviar);
        panel.add(botonRecuperar);
        marco.setSize(600,800);
        marco.setVisible(true);

        BufferedReader entrada;
        PrintStream salida;
        try {
            salida = new PrintStream(socket.getOutputStream());
            salida.println(login + " se he conectado" );

            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String mensaje;
            while( (mensaje = entrada.readLine()) != null){
                areaTexto.setText(areaTexto.getText() + mensaje + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Cliente {
    public static void  main(String[] args)throws IOException {
        String direccion = "localhost";
        int puerto = 9999;
        String login = JOptionPane.showInputDialog(null,
                "Escribe tu nombre");


        if(args.length >= 1){
            login = args[0];
        }
        if(args.length >= 2){
            direccion = args[1];
        }
        if(args.length >= 3){
            puerto = Integer.parseInt(args[2]);
        }
        Socket socket = new Socket(direccion, puerto);

        Talk talk = new Talk(socket, login);
        talk.hablar();

        socket.close();
        System.exit(0);
    }

}
