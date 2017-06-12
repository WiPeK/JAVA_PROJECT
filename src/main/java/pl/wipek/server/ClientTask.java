package pl.wipek.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.wipek.common.Action;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author Krzysztof Adamczyk on 13.04.2017.
 * Managing Object receiving from clients and depending on Object Actions executes them
 */
public class ClientTask implements Runnable {

    /**
     * Server socket with client's connection
     */
    private final Socket socket;

    /**
     * Log4j2 logger using to log events to file
     */
    private static final Logger log = LogManager.getRootLogger();

    /**
     * Socket receiving from server to communicate with client
     * @param socket server socket
     */
    ClientTask(Socket socket) {
        this.socket = socket;
    }

    /**
     * @see EntityManagerFactory
     * contains session with connection to database
     */
    final static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("pl.wipek.database");

    /**
     * @author Krzysztof Adamczyk
     * reading object from socket, managing them depending of action and writing to socket answer
     */
    public void run() {
        log.info("Start new thread with connection");
        log.info("Server: Connect with: " + this.socket.getRemoteSocketAddress().toString());
        ObjectInputStream input = null;
        ObjectOutputStream output = null;

        try {
            input = new ObjectInputStream(this.socket.getInputStream());
            output = new ObjectOutputStream(this.socket.getOutputStream());
            while(true) {
                Object received = input.readObject();
                if(received instanceof Action) {
                    if(((Action)received).getAction().equals("EXIT")) {
                        this.socket.close();
                        log.info("Disconnect: " + this.socket.getRemoteSocketAddress().toString());
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                output.writeObject(Router.manageReceivedObject(received));
                output.flush();
            }
        }catch (Exception e) {
            if(e instanceof SocketException) {
                try {
                    if(input != null) {
                        input.close();
                    }
                    if(output != null) {
                        output.close();
                    }
                    if(!this.socket.isClosed()) {
                        this.socket.close();
                    }
                }catch (Exception ex) {
                    log.error(ex);
                    e.printStackTrace();
                }
            }
            log.error(e);
            e.printStackTrace();
        }
    }


}
