package view;


import bean.ProductBean;
import bean.UserBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class InventoryView {
    private Scanner scanner;

    private BufferedReader in;
    private PrintWriter out;

    public InventoryView(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

//    public InventoryView() {
//        scanner = new Scanner(System.in);
//    }

    public void displayMenu() {
        out.println("1 For Signup\n2 For Login\n3 For exit");
    }

    public void displayProducts(List<ProductBean> products) {
        out.println("*********** Product List ******************");
        out.println("ProductId ---- Name ---- price-----");
        for (ProductBean product : products) {
            out.println(product.getProductId() + "\t\t" + product.getProductName() + "\t\t" + product.getPrice());
        }
    }
    public void displayUsers(List<UserBean> users) {
        for (UserBean user : users) {
            out.println(user.getFirstName());
        }
    }

    public String getUserInput(String prompt) {
        try {
            out.println(prompt);
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getUserIntInput(String prompt) {
        try {
            out.println(prompt);
            return Integer.parseInt(in.readLine());
        } catch (NumberFormatException e) {
            out.println("Invalid input. Please enter an integer.");
            return getUserIntInput(prompt);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void displayMessage(String message) {
        out.println(message);
    }
}