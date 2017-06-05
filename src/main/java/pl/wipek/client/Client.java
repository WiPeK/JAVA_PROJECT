package pl.wipek.client;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author Created by Krzysztof Adamczyk on 15.04.2017.
 * Class is responding for communication with Server:
 * sending and receiving from server
 */
public class Client {
    /**
     * Server ip
     */
    private String ip;

    /**
     * server port
     */
    private int port;

    /**
     * Client socket to connect with server
     */
    private Socket socket;

    /**
     * output stream it is writer objects to server
     */
    private ObjectOutputStream output;

    /**
     * input stream it is reading objects from server
     */
    private ObjectInputStream input;

    /**
     * @author Krzysztof Adamczyk
     * @param ip it is ip to connect with server
     * @param port it is port to connect with server
     * Constructor also open connection with server
     */
    Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.openClient();
    }

    /**
     * @author Krzysztof Adamczyk
     * @param o containt object which will be send to server
     * @return Object with response from server
     */
    public Object requestServer(Object o) {
        Object result = null;
        try{
            this.output.writeObject(o);
            this.output.flush();
            while(true) {
                try {
                    result = this.input.readObject();
                    break;
                } catch (EOFException e) {
                    break;
                }
            }
        }catch (Exception e) {
            Controller.getLogger().error(e);
            e.printStackTrace();

            try {
                if(!this.socket.isClosed() || this.socket != null) {
                    this.socket.close();
                }
                if(this.output != null) {
                    this.output.close();
                }
                if(this.input != null) {
                    this.input.close();
                }
            }catch (Exception ex) {
                if(!(ex instanceof EOFException)) {
                    Controller.getLogger().error(ex);
                    ex.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * open socket, output and input with connection to server
     * if errors then log into file
     */
    private void openClient() {
        try {
            this.socket = new Socket(this.ip, this.port);
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());
        }catch(Exception e) {
            Controller.getLogger().error(e);
            e.printStackTrace();
        }
    }

    /**
     * close socket, output and input when are not closed
     */
    void closeClient() {
        try{
            if(!this.socket.isClosed() || this.socket != null) {
                this.socket.close();
            }
            if(this.output != null) {
                this.output.close();
            }
            if(this.input != null) {
                this.input.close();
            }
        }catch (Exception e) {
            Controller.getLogger().error(e);
            e.printStackTrace();
        }
    }

    /**
     * Check is server socket is open
     * @return Boolean true when socket is open otherwise false
     */
    boolean isSocketClosed() {
        return this.socket.isClosed();
    }
}

