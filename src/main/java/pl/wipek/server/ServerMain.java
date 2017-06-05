package pl.wipek.server;

/**
 * @author Krzysztof Adamczyk
 * Created by Krzysztof Adamczyk on 13.04.2017.
 */
public class ServerMain {

    /**
     * max number of clients who can connect to server in the same time
     */
    private static final int MAXCLIENTSNUMBER = 10000;

    /**
     * default port on which server will be running
     */
    private static final int DEFAULTPORT = 54321;

    /**
     * Start server
     * @param args
     * args[0] is number of max clients connected to server if empty then number is 10000
     * args[1] is number of port on which server is running if empty then port is 54321
     */
    public static void main(String[] args) throws Exception {
        int maxNumberOfClients = ((args.length > 0) ? Integer.parseInt(args[0]) : MAXCLIENTSNUMBER);
        int portNumber = (args.length > 1 ? Integer.parseInt(args[1]) : DEFAULTPORT);
        Server server = new Server(maxNumberOfClients, portNumber);
        server.startServer();
    }
}
