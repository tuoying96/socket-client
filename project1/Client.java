import java.io.*;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.*;
import java.security.*;



public class Client {
    private static Socket serverSocket;
    //a PrintWriter to write to Server
    private static PrintWriter out;
    //a BufferedReader to get the response from erver
    private static BufferedReader in;

    private static String MESSAGE_PREFIX = "cs5700spring2021";
    private static String EVAL_PATTERN = MESSAGE_PREFIX + " EVAL " + "[\\s\\S]*";
    private static String BYE_PATTERN = MESSAGE_PREFIX + " BYE " + "\\w{64}";

    public Client(int port, boolean isSSL, String host) throws IOException {




        try {
            if (isSSL) {
                // disable certificate validation, because the server's certificate is self-signed
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(host, port);
                this.serverSocket = (SSLSocket) socket;

                // Without disable certificate validation
//                SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
//                SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
//                this.serverSocket = (SSLSocket) socket;
            } else {
                this.serverSocket = new Socket(host, port);
            }
            this.out = new PrintWriter(this.serverSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.serverSocket.getInputStream()));
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            System.out.println(e);
            System.out.println("The host cannot be reached");
        }

        // Without SSL
//        this.serverSocket = new Socket(host, port);
//        this.out = new PrintWriter(this.serverSocket.getOutputStream(), true);
//        this.in = new BufferedReader(new InputStreamReader(this.serverSocket.getInputStream()));
    }

    /**
     * This method returns the solution in EVAL message.
     */
    public static String getSolution(String response) throws IOException, InterruptedException {
        File directory = new File(".");
        String path = directory.getCanonicalPath() + "/eval.py";
        String input = response.substring(MESSAGE_PREFIX.length() + 6);

        File file = new File(directory, "output.txt");
        file.createNewFile();

        ProcessBuilder pb = new ProcessBuilder("python",path, input).inheritIO();
        Process p = pb.start();
        p.waitFor();

        BufferedReader in = new BufferedReader(new FileReader("output.txt"));
        String line = in.readLine();

        file.delete();

        return line;
    }


    public static boolean getInfo(String response, String pattern){
        Pattern messagePattern = Pattern.compile(pattern);
        Matcher matcher = messagePattern.matcher(response);

        if (matcher.find()){
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 4){
            System.err.println("Usage: java Client <port> <useSSL> <host name> <nuid>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        boolean isSSL = Boolean.parseBoolean(args[1]);
        String host = args[2];
        String nuid = args[3];


        Client client = new Client(port, isSSL, host);

        if (out == null) {
            System.out.println("The socket is not properly set.");
            System.exit(1);
        }

        out.println(MESSAGE_PREFIX + " HELLO " + nuid);

        String response;
        int i = 1;
        while(true){
            response = in.readLine();
            if (response == null){
                System.out.println("The server closes the connections");
                break;
            }
            if (getInfo(response, EVAL_PATTERN)) {
                // System.out.println("Here？？？");
                System.out.println(MESSAGE_PREFIX + getSolution(response));
                out.println(MESSAGE_PREFIX + getSolution(response));

                System.out.println("count: " + i++);
                continue;
            }else if (getInfo(response, BYE_PATTERN)) {
                // System.out.println(response);
                System.out.println("Here is BYE!");
                System.out.println(response.split(" ")[2]); // cs5700spring2021 BYE [a 64 byte secret flag]
            }else {

                System.out.println("i am a test");
                System.out.println(response);
            }
            break;
        }
        // When done, just close the connection and exit
        out.close();
        in.close();
        serverSocket.close();
    }
}
