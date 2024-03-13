package client;

import controller.InventoryController;
import bean.ProductBean;
import bean.UserBean;
import view.InventoryView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class InventoryClient implements Runnable {
    private static Socket clientSocket;
    private static InventoryController controller;
    List<ProductBean> products;
    List<UserBean> users;

    public InventoryClient(Socket socket, List<ProductBean> products, List<UserBean> users) {
        this.clientSocket = socket;
        this.products=products;
        this.users=users;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            InventoryView view = new InventoryView(in, out);
            controller = new InventoryController(products, users, view, clientSocket);
            controller.start();

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}