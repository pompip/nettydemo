package cn.pompip.httpserver;

import cn.pompip.utils.Res;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.Executors;

public class HttpMain {
    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(80),0);

            server.setExecutor(Executors.newCachedThreadPool());
            server.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange httpExchange) throws IOException {
                    OutputStream responseBody = httpExchange.getResponseBody();
                    File file = Res.get("/web/index.html");
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] bytes = new byte[fileInputStream.available()];
                    fileInputStream.read(bytes);
                    fileInputStream.close();
                    httpExchange.sendResponseHeaders(200,0);
                    responseBody.write(bytes);
                    responseBody.flush();
                    responseBody.close();

                }
            });
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
