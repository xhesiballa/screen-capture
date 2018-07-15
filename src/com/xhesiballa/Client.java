package com.xhesiballa;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

import static java.lang.String.format;

/**
 * @author Xhesi Balla
 */
public class Client extends Application {
    private static final String file = "C:\\Users\\user\\Desktop\\screenshot.jpeg";
    private final static String hostName = "localhost";
    private static int portNumber = 8000;

    private static InputStream socketInputStream;
    private static OutputStream socketOutputStream;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;

    public static void main(String[] args) {
        initialiseSocketClient();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        byte[] bytes = getImg();

        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
        Image image = new Image(byteInputStream);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitHeight(400);
        imageView.setPreserveRatio(true);

        StackPane root = new StackPane();
        root.getChildren().add(imageView);
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();

        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        Thread t = new Thread(() -> {
            while (true) {
                ByteArrayInputStream b = new ByteArrayInputStream(getImg());
                Image i = new Image(b);
                imageView.setImage(i);
//                System.out.println("Update image");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private static void initialiseSocketClient() {
        try {
            System.out.println("Initialising socket client...");
            Socket clientSocket = new Socket(hostName, portNumber);
            socketInputStream = clientSocket.getInputStream();
            socketOutputStream = clientSocket.getOutputStream();
            dataInputStream = new DataInputStream(socketInputStream);
            dataOutputStream = new DataOutputStream(socketOutputStream);
            System.out.println(format("Conected on %s:%s", hostName, portNumber));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] getImg() {
        try {
            dataOutputStream.writeInt(1);

            int bytesToRead = dataInputStream.readInt();

            byte[] buffer = new byte[1024];
            int bytesRead;
            ByteOutputStream os = new ByteOutputStream();
            while (bytesToRead > 0) {
                bytesRead = socketInputStream.read(buffer);
                os.write(buffer);
                bytesToRead -= bytesRead;
            }
            return os.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
