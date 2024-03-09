package view;


import bean.ProductBean;
import bean.UserBean;

import java.util.List;
import java.util.Scanner;

public class InventoryView {
    private Scanner scanner;

    public InventoryView() {
        scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("0 for List all users\n1 For Signup\n2 For Login\n3 For exit\nEnter your choice");
    }

    public void displayProducts(List<ProductBean> products) {
        System.out.println("*********** Product List ******************");
        System.out.println("ProductId ---- Name ---- price-----");
        for (ProductBean product : products) {
            System.out.println(product.getProductId() + "\t\t" + product.getProductName() + "\t\t" + product.getPrice());
        }
    }

    public void displayUsers(List<UserBean> users) {
        for (UserBean user : users) {
            System.out.println(user.getFirstName());
        }
    }

    public String getUserInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int getUserIntInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextInt();
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}