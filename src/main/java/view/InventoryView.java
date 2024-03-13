package view;


import bean.ProductBean;
import bean.UserBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class InventoryView {
    private Scanner scanner=new Scanner(System.in);

    private BufferedReader in;
    private PrintWriter out;
    String DOUBLE_TAB="\t\t";

    public InventoryView(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

//    public InventoryView() {
//        scanner = new Scanner(System.in);
//    }

    public void displayMenu() {
        System.out.println("1 For Signup\n2 For Login\n3 For exit");
    }

    public void displayProducts(List<ProductBean> products)
    {
        System.out.println("*********** Product List ******************");

        System.out.println("ProductId ---- Name ---- price-----");

        for (ProductBean product : products)
        {

            System.out.println(product.getProductId() + DOUBLE_TAB + product.getProductName() + DOUBLE_TAB + product.getPrice());

        }
    }
    public void displayUsers(List<UserBean> users) {

        for (UserBean user : users) {
            System.out.println(user.getUserId()+"\t"+user.getFirstName());
        }
    }

    public String getUserInput(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine();
    }

    public int getUserIntInput(String prompt) {
        try {
            System.out.println(prompt);
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            out.println("Invalid input. Please enter an integer.");
            return getUserIntInput(prompt);
        }
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}