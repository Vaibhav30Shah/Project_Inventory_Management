package view;

import bean.Product;
import bean.UserBean;

import java.util.List;
import java.util.Scanner;

public class InventoryView
{
    private Scanner scanner = new Scanner(System.in);

    String SINGLE_TAB="\t";

    String DOUBLE_TAB = "\t\t";

    String MULTI_TAB="\t\t\t\t";


    public void displayMenu()
    {
        System.out.println("1 For Signup\n2 For Login\n3 For exit");
    }

    public void displayProducts(List<Product> products)
    {
        System.out.println("*********** Product List ******************");

        System.out.println("ProductId"+SINGLE_TAB+"Name"+MULTI_TAB+"Price");

        for (Product product : products)
        {
            System.out.println(product.getProductId() + SINGLE_TAB + product.getProductName() + MULTI_TAB + product.getProductPrice());
        }
    }

    public void displayUsers(List<UserBean> users)
    {
        for (UserBean user : users)
        {
            System.out.println(user.getUserId() + "\t" + user.getFirstName());
        }
    }

    public String getUserInput(String prompt)
    {
        System.out.println(prompt);

        String result= scanner.nextLine();

        if(result==null){
            return "Can't be null";
        }
        return result;
    }

    public int getUserIntInput(String prompt)
    {
        try
        {
            System.out.println(prompt);

            return Integer.parseInt(scanner.nextLine());
        }
        catch (NumberFormatException e)
        {
            System.out.println("Invalid input. Please enter an integer.");

            return getUserIntInput(prompt);
        }
    }

    public void displayMessage(String message)
    {
        System.out.println(message);
    }

    public boolean getUserBooleanInput(String prompt)
    {
        System.out.println(prompt);

        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("y"))

            return true;

        else if (input.equalsIgnoreCase("n"))

            return false;

        else
        {
            System.out.println("Invalid input. Please enter an Y or N.");

            return getUserBooleanInput(prompt);
        }
    }
}