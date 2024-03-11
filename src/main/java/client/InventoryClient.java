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
    private Socket clientSocket;
    private InventoryController controller;

    public InventoryClient(Socket socket, List<ProductBean> products, List<UserBean> users) {
        this.clientSocket = socket;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            InventoryView view = new InventoryView(in, out);
            controller = new InventoryController(products, users, view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            controller.start();

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}