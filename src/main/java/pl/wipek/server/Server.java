package pl.wipek.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Krzysztof Adamczyk
 * @version 1.0
 * Class Server manage clients connections and launch Client Tasks
 */
public class Server {

    /**
     * max number of clients who can connect to server in the same time
     */
    private final int maxNumberOfClients;

    /**
     * port on which server will be running
     */
    private final int portNumber;

    /**
     * Log4j2 logger using to log events to file
     */
    private final static Logger log = LogManager.getRootLogger();

    /**
     * @author Krzysztof Adamczyk
     * initializing Server with params
     * @param maxNumberOfClients max number of clients who can connect to server in the same time
     * @param portNumber port on which server will be running
     */
    public Server(int maxNumberOfClients, int portNumber) {
        this.maxNumberOfClients = maxNumberOfClients;
        this.portNumber = portNumber;
    }

    /**
     * @author Krzysztof Adamczyk
     * Start server opening sockets for every connections and execute it in new threads
     */
    void startServer() {
        String ip = null;
        try{
            ip = InetAddress.getLocalHost().getHostAddress();
        }catch (Exception e) {
            log.error(e);
        }

        log.info("Start server at the port number: " + this.portNumber + " ip: " + ip + " max number of clients: " + this.maxNumberOfClients);
        final ExecutorService pool = Executors.newFixedThreadPool(this.maxNumberOfClients);

        while(true) {
            try {
                ServerSocket serverSocket = new ServerSocket(this.portNumber);
                Socket socket = serverSocket.accept();

                Thread serverThread = new Thread(() -> {
                    pool.submit(new ClientTask(socket));
                });
                serverThread.start();

            } catch (Exception e) {
                log.error(e);
                break;
            }
        }
    }
}
