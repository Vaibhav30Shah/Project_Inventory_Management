package server;

import authentcation.AuthenticationServer;
import bean.ProductBean;
import bean.UserBean;
import client.InventoryClient;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryServer {
    private static final int PORT = 1428;
    private static List<ProductBean> products = new ArrayList<>();
    private static List<UserBean> users = new ArrayList<>();


    public static void main(String[] args) {
        initializeDummyData();

        AuthenticationServer authServer = new AuthenticationServer(users);
        Thread authThread=new Thread(authServer);
        authThread.start();


        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Inventory Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Create a new thread to handle the client
                Thread clientThread = new Thread(new InventoryClient(clientSocket, products, users));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initializeDummyData() {
        // Add dummy users
        users.add(new UserBean("Vaibhav", "vaibhav@gmail.com", "vaibhav123"));
        users.add(new UserBean("Aagnesh", "aagnesh@gmail.com", "aagnesh123"));
        users.add(new UserBean("Dhruv", "dhruv@gmail.com", "dhruv123"));
        users.add(new UserBean("Ram", "ram@gmail.com", "ram123"));
        UserBean admin = new UserBean("admin", "admin@admin.com", "admin");
        admin.setAdmin(true);
        users.add(admin);

        // Add dummy products
        products.add(new ProductBean(50000, "iPhone"));
        products.add(new ProductBean(55000, "LED TV"));
        products.add(new ProductBean(5000, "Monitor"));
        products.add(new ProductBean(400, "Mouse"));
        products.add(new ProductBean(1000, "Keyboard"));
        products.add(new ProductBean(100, "USB Cable"));
        products.add(new ProductBean(17000, "Airpods"));
        products.add(new ProductBean(1000, "Speaker"));
        products.add(new ProductBean(60000, "HP Laptop"));
        products.add(new ProductBean(4500, "Graphics Card"));

    }

}