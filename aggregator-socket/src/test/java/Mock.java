import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class MockOutputStream extends OutputStream {
    private ByteArrayOutputStream writtenBytes = new ByteArrayOutputStream();

    @Override
    public void write(int b) {
        this.writtenBytes.write(b);
    }

    @Override
    public String toString() {
        return this.writtenBytes.toString();
    }
}


class MockSocket extends Socket {
    private InputStream inputStream;
    private OutputStream outputStream;

    MockSocket() {
    }

    void sendMessage(String message) {
        this.inputStream = new ByteArrayInputStream(message.getBytes());
        this.outputStream = new MockOutputStream();
    }

    @Override
    public InputStream getInputStream() {
        return this.inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        return this.outputStream;
    }
}

class MockServerSocket extends ServerSocket {

    private MockSocket socket;

    MockServerSocket() throws IOException {
        this.socket = new MockSocket();
    }

    void sendMessage(String message) {
        this.socket.sendMessage(message);
    }

    @Override
    public Socket accept() {
        return this.socket;
    }
}