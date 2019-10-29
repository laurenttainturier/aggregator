/**
 * aggregator-socket
 * File: net.bigeon.mcdas.aggregator.socket.AdministrationServer.java
 * Created on: Oct. 28, 2019
 */
package net.bigeon.mcdas.aggregator.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.net.ServerSocketFactory;

import net.bigeon.mcdas.admin.AggregatorManager;
import net.bigeon.mcdas.aggregator.AggregatorType;

/** Implementation of a server for the application
 *
 * @author Emmanuel Bigeon */
public class AdministrationServer implements Runnable {

    private static final char[] LIST    = { 'l', 'i', 's', 't' };
    private static final char[] STOP    = { 's', 't', 'o', 'p' };
    private static final char[] STAR    = { 's', 't', 'a', 'r' };
    private boolean             running = true;
    private final AggregatorManager   admin;

    public AdministrationServer(AggregatorManager admin) {
        super();
        this.admin = admin;
    }

    /** Handle new connections. */
    private void handleConnection(ServerSocket server) {
        try (Socket socket = server.accept();
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),
                        StandardCharsets.UTF_8)) {
            char[] cbuf = new char[4];
            if (cbuf == LIST) {
                handleList(osw);
                return;
            } else if (cbuf == STOP && isr.read() == ' ') {
                handleStop(isr, osw);
                return;
            } else if (cbuf == STAR && isr.read() == 't') {
                if (isr.read() == ' ') {
                    handleStart(isr, osw);
                    return;
                }
            }
            osw.append("1");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void handleList(OutputStreamWriter osw) throws IOException {
        osw.append("{elements = {");
        boolean first = true;
        for (String c : admin.list()) {
            if (first) {
                osw.append('\"');
            } else {
                osw.append(", \"");
            }
            osw.append(c);
            osw.append('"');
        }
        osw.append("}}");
    }

    private void handleStart(InputStreamReader isr, OutputStreamWriter osw)
            throws IOException {
        String id = readPart(isr);
        if (admin.list().contains(id)) {
            osw.append('2');
            return;
        }
        String type = readPart(isr);
        AggregatorType aggType = AggregatorType.valueOf(type.toUpperCase(Locale.ENGLISH));
        if (aggType == null) {
            osw.append('3');
            return;
        }
        String length = readPart(isr);
        if (length.isEmpty()) {
            osw.append('5');
            return;
        }
        int period;
        try {
            period = Integer.parseUnsignedInt(length);
        } catch (NumberFormatException e) {
            osw.append('5');
            return;
        }
        String node = readPart(isr);
        admin.add(id, aggType, node, period);
    }

    private void handleStop(InputStreamReader isr, OutputStreamWriter osw)
            throws IOException {
        // read all content to end?
        String id = readPart(isr);
        if (admin.list().contains(id.toString())) {
            admin.delete(id.toString());
            osw.append('0');
        } else {
            osw.append('2');
        }
    }

    private synchronized boolean isRunning() {
        return running;
    }

    private String readPart(InputStreamReader isr) throws IOException {
        StringBuilder id = new StringBuilder();
        int c;
        while ((c = isr.read()) > 0) {
            if (Character.isWhitespace(c)) {
                if (id.length() > 0) {
                    break;
                } else {
                    continue;
                }
            }
            id.append(c);
        }
        return id.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run() */
    @Override
    public void run() {
        ServerSocketFactory factory = ServerSocketFactory.getDefault();
        try (ServerSocket server = factory.createServerSocket(1, 3031,
                InetAddress.getLocalHost())) {
            while (isRunning()) {
                handleConnection(server);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public synchronized void stop() {
        running = false;
    }
}
