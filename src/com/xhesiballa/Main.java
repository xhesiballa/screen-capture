package com.xhesiballa;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.String.format;

public class Main {
    private static final String file = "C:\\Users\\user\\Desktop\\screenshot.jpeg";
    private static final int FPS = 10;
    private static final double SLEEP_TIME = 1 / FPS * 1000;

    private static final int DEFAULT_PORT = 8000;

    private static Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    private static Robot robot;

    public static void main(String[] args) throws AWTException, IOException {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        startServerSocket(args);
    }

    private static void startServerSocket(String[] args) {
        int argSize = args.length;
        int portNumber = DEFAULT_PORT;
        if (argSize == 0) {
            System.out.println("No port provided. Default port will used ");
        } else {
            portNumber = Integer.parseInt(args[0]);
        }

        System.out.println("Startig server ... ");

        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println(format("Server listening on port %s", portNumber));
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");
            OutputStream outputStream = clientSocket.getOutputStream();
            InputStream inputStream = clientSocket.getInputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            int opCode;

            loop:
            while ((opCode = dataInputStream.readInt()) != 0) {
                switch (opCode) {
                    case 1:
                        byte[] bytes = getScreenCapture();
                        dataOutputStream.writeInt(bytes.length);
                        outputStream.write(bytes);
                        break;
                    default:
                        break loop;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static byte[] getScreenCapture() {
        BufferedImage capture = robot.createScreenCapture(screenRect);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[0];
        try {
            ImageIO.write(capture, "JPG", baos);
            baos.flush();
            bytes = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
