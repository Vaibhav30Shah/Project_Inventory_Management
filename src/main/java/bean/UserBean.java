package bean;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserBean
{
    private static int userId;

    private String firstName, email, password;

    private boolean isAdmin = false;

    public ArrayList<ProductBean> cart = new ArrayList<>();

    public static String FILE_NAME = "src/main/java/files/userData.txt";

    public static String USER_HISTORY_FILE="src/main/java/files/userOrderHistory.txt";

    public UserBean(String firstName, String email, String password)
    {
        this.userId = userId++;

        this.firstName = firstName;

        this.email = email;

        this.password = password;
    }

    public void saveOrderHistory(List<Product> orderedProducts)
    {
        try
        {
            FileWriter writer = new FileWriter(USER_HISTORY_FILE, true); // Append mode

            writer.write(getEmail() + "," + orderedProducts.stream()
                    .map(Product::getProductName)
                    .collect(Collectors.joining(",")) + "\n");

            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void saveUserData(List<UserBean> users)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME)))
        {
            for (UserBean user : users)
            {
                writer.write(user.getFirstName() + "," + user.getEmail() + "," + user.getPassword() + "," + user.isAdmin() + "\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static List<UserBean> loadUserData()
    {
        List<UserBean> users = new ArrayList<>();

        try
        {
            File file = new File(FILE_NAME);

            if (file.exists())
            {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                String line;

                while ((line = reader.readLine()) != null)
                {
                    String[] userData = line.split(",");

                    if (userData.length == 4)
                    {
                        String name = userData[0];

                        String email = userData[1];

                        String password = userData[2];

                        boolean isAdmin = Boolean.parseBoolean(userData[3]);

                        UserBean user = new UserBean(name, email, password);

                        user.setAdmin(isAdmin);

                        users.add(user);
                    }
                }
                reader.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return users;
    }

    public boolean emailExist(String email, List<UserBean> users)
    {
        for (UserBean userBean : users)
        {
            if (email.equalsIgnoreCase(userBean.getEmail()))
            {
                return true;
            }
        }
        return false;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public boolean isAdmin()
    {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin)
    {
        this.isAdmin = isAdmin;
    }
}