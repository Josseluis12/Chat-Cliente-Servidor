package servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static servidor.Servidor.ADMIN_PASSWORD;

public class Usuario implements Runnable{

    private String nick;
    private BufferedReader entrada;
    private BufferedWriter salida;
    private long loginTime;
    private boolean conectado, superUser, heartBeatOn;
    private String IP;
    private long ping;
    private Sala sala;
    private long lastBeat;

    public Usuario(String nick){
        this.nick = nick;
    }

    public Usuario(Socket s, Sala sala) throws IOException{
        this.sala = sala;
        this.loginTime = System.currentTimeMillis();
        this.IP = s.getInetAddress().getHostAddress();
        this.ping = 0;
        this.superUser = false;
        this.heartBeatOn = true;
        entrada = new BufferedReader(new InputStreamReader(s.getInputStream()));
        salida = new BufferedWriter(new PrintWriter(s.getOutputStream()));
    }

    @Override
    public void run(){
        String login = recibir();
        if(!login.startsWith("NICK")){
            enviar("400 Paquete inválido recibido");
            Log.log("Paquete de login inválido: " + login);
            conectado = false;
        }else{
            if(login.split("[ ]")[1].length() >= 12){
                enviar("400 El nick elegido es demasiado largo, introduce un nick de como máximo 12 carácteres");
                Log.log("Un usuario ha tratado de entrar con un nick demasiado largo. Nick: " + login.split("[ ]")[1]);
            }else{
                conectado = !sala.existeUsuario(this);
            }
        }
        if(conectado){
            nick = login.split("[ ]")[1];
            enviar(sala.entrar(this));
            enviarListaUsuarios();
            if(heartBeatOn){
                lastBeat = System.currentTimeMillis();
                asyncBeatCheck();
            }
            enviar("SALA " + sala.getNombre());
            do{
                String packet = recibir();
                if(packet != null && !packet.isEmpty()){
                    analizarPacket(packet);
                }
            }while (conectado);
            enviar("400 Has sido desconectado del chat");
            sala.salir(this);
        }
    }

    public void analizarPacket(String s){
        if(s.startsWith("EXIT")) {
            conectado = false;
        }else if (s.startsWith("BEAT")){
            String[] p;
            p = s.split("[ ]");
            if(p.length > 2){
                enviar("500 Sintaxis incorrecta");
                Log.log("Paquete inválido: " + s);
            }else{
                lastBeat = System.currentTimeMillis();
                ping = System.currentTimeMillis() - Long.parseLong(p[1]);
            }
        }else if(s.startsWith("/NICK ")){
            String[] p;
            p = s.split("[ ]");
            if(p.length > 2){
                enviar("500 Sintaxis incorrecta");
                Log.log("Paquete inválido: " + s);
            }else{
                String oldname = nick;
                if(sala.existeUsuario(new Usuario(p[1]))){
                    enviar("500 Ya hay un usuario llamado " + nick + " en la sala");
                    nick = oldname;
                }else{
                    if(!(p[1].length() > 12)){
                        nick = p[1];
                        sala.actualizarListadoUsuarios();
                        Log.log(oldname + " a cambiado de nombre a " + nick);
                        sala.difundir(oldname + " a cambiado de nombre a " + nick);
                        enviar("200 OK");
                    }else{
                        enviar("500 El nick elegido es demasiado largo. Max 12 carácteres");
                    }
                }
            }
        }else if(s.startsWith("/HELP")){
            String[] p;
            p = s.split("[ ]");
            enviar("======================\nComandos\n======================\n");
            enviar("- /P <usr> <msg>: Envia un mensaje privado a un usuario de la sala\n- /NICK <nuevo>: Cambia tu nombre de usuario");
            enviar("- /C <nombre>: Crea una sala nueva y te mete en ella.\n- /J <nombre>: Cambia a la sala especificada");
            enviar("- /LIST: Lista las salas disponibles en el servidor\n- EXIT: Sale del chat\n======================");
        }else if(s.startsWith("/P")){
            String[] p;
            p = s.split("[ ]");
            if(!sala.existeUsuario(new Usuario(p[1]))){
                enviar("500 No hay ningun usuario con el nombre " + p[1]);
            }else{
                Usuario tmp = sala.obtenerUsuario(p[1]);
                sala.enviarMensajePrivado(this, tmp, s.substring(3+tmp.getNick().length()+1));
                Log.log("Mensaje privado de " + this.getNick() + " y " + tmp.getNick() + ": " + s.substring(3+tmp.getNick().length()));
            }
        }else if(s.startsWith("/C ")){
            String[] p;
            p = s.split("[ ]");
            if(p.length > 3){
                enviar("500 Sintaxis incorrecta");
                Log.log("Paquete inválido: " + s);
            }else{
                if(!Servidor.existeSala(new Sala(p[1]))){
                    Sala sl = null;
                    if(p.length == 2){
                        sl = new Sala(p[1]);
                    }else if (p.length == 3){
                        sl = new Sala(p[1], p[2]);
                    }
                    if(sl != null){
                        Servidor.agregarSala(sl);
                        sala.salir(this);
                        sl.entrar(this);
                        sala = sl;
                        enviar("SALA " + sala.getNombre());
                        sala.actualizarListadoUsuarios();
                    }
                }else{
                    enviar("500 Ya existe una sala con ese nombre");
                }
            }
        }else if(s.startsWith("/J ")){
            String[] p;
            p = s.split("[ ]");
            if(p.length > 3){
                enviar("500 Sintaxis incorrecta");
                Log.log("Paquete inválido: " + s);
            }else{
                if(Servidor.existeSala(new Sala(p[1]))){

                    if(p.length == 2){
                        Sala sl = Servidor.obtenerSala(p[1]);

                            sala.salir(this);
                            sl.entrar(this);
                            sala = sl;
                            enviar("SALA " + sala.getNombre());
                            sala.actualizarListadoUsuarios();

                    }

                }else{
                    enviar("500 No existe ninguna sala llamada " + p[1]);
                }
            }
        }else if(s.startsWith("/D ")){
            String[] p;
            p = s.split("[ ]");
            if(p.length > 2){
                enviar("500 Sintaxis incorrecta");
                Log.log("Paquete inválido: " + s);
            }else{
                if(p.length == 2 && superUser){
                    if(p[1].equalsIgnoreCase("Principal")){
                        enviar("500 La sala principal no puede ser eliminada!");
                    }else{
                        if(Servidor.existeSala(new Sala(p[1]))){
                            Sala sl = servidor.Servidor.obtenerSala(p[1]);
                            sl.difundir(nick + " ha eliminado la sala");
                            Servidor.eliminarSala(sl);
                        }else{
                            enviar("500 No existe ninguna sala llamada " + p[1]);
                        }
                    }
                }else{
                    enviar("500 Privilegios insuficientes");
                }
            }
        }else if(s.startsWith("/LIST")){
            Sala[] sl = servidor.Servidor.obtenerSalas();
            enviar("===========================");
            enviar("Salas disponibles: " + sl.length);
            enviar("===========================");
            for(Sala sl1 : sl){
                enviar(sl1.getNombre() + " - Usuarios: " + sl1.getCountUsuarios());
            }
            enviar("===========================");
        }else{
            //En el caso de no ser ningun comando
            //Para que no sean mucho los mensajes, que sean menos de 140 caracteres
            if(s.length() < 140){
                sala.difundir(nick + ": " + s);
                Log.log("Recibido mensaje de " + nick + " en la sala " + sala.getNombre() + ". Contenido: " + s);
            }else{
                Log.log("Recibido mensaje demasiado largo de " + nick);
            }
        }
    }

    public void enviarListaUsuarios(){
        StringBuilder strb = new StringBuilder();
        strb.append("LIST ");
        for(Usuario usr : sala.getUsuarios()){
            strb.append(usr.getNick());
            strb.append(" ");
        }
        enviar(strb.toString());
    }

    public void enviar(String s){
        try{
            salida.write(s + "\n");
            salida.flush();
        }catch(IOException ex){
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String recibir(){
        String s = "";
        try{
            s = entrada.readLine();
        }catch(IOException ex){

        }
        return s;
    }

    public String getNick(){
        return nick;
    }

    public void setNick(String nick){
        this.nick = nick;
    }

    public long getLoginTime(){
        return loginTime;
    }

    public void setLoginTime(long loginTime){
        this.loginTime = loginTime;
    }

    public String getIP(){
        return IP;
    }

    public void setIP(String IP){
        this.IP = IP;
    }

    public long getPing(){
        return ping;
    }

    public void setPing(long ping){
        this.ping = ping;
    }

    public boolean isSuperUser(){
        return superUser;
    }

    public void setSuperUser(boolean superUser){
        this.superUser = superUser;
    }

    public boolean isConectado(){
        return conectado;
    }

    public void setConectado(boolean conectado){
        this.conectado = conectado;
    }

    private void asyncBeatCheck(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                while(conectado){
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException ex){

                    }
                    if(System.currentTimeMillis() - lastBeat >= 7000){
                        enviar("400 Desconectado por inactividad");
                        conectado = false;
                        Log.log(nick + " a sido desconectado por inactividad");
                    }
                }
            }
        }).start();
    }

    public Sala getSala(){
        return sala;
    }

    public void setSala(Sala sala){
        this.sala = sala;
    }

}