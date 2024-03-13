package bean;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserBean {

    private static int userId;
    private String firstName, email, password;
    private boolean isAdmin = false;
    public ArrayList<ProductBean> cart = new ArrayList<>();
    static String FILE_NAME="userData.txt";

    public UserBean(String firstName, String email, String password) {
        this.userId = userId++;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
    }

    public static void saveUserData(List<UserBean> users){
        try(BufferedWriter writer=new BufferedWriter(new FileWriter("userData.txt"))){
            for(UserBean user:users){
                writer.write(user.getFirstName()+","+user.getEmail()+","+user.getPassword()+","+user.isAdmin()+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<UserBean> loadUserData(){
        List<UserBean> users=new ArrayList<>();
        try(BufferedReader reader=new BufferedReader(new FileReader(FILE_NAME))){
            String line;
            while((line=reader.readLine())!=null){
                String[] splitArray=line.split(",");
                String firstName=splitArray[0];
                String email=splitArray[1];
                String password=splitArray[2];
                boolean isAdmin= Boolean.parseBoolean(splitArray[3]);
                UserBean user=new UserBean(firstName, email, password);
                user.setAdmin(isAdmin);
                users.add(user);
            }
        } catch (FileNotFoundException e) {
            File file=new File(FILE_NAME);
            try {
                file.createNewFile();
            } catch (IOException ex) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean emailExist(String email, List<UserBean> users){
        for(UserBean userBean:users){
            if(email.equalsIgnoreCase(userBean.getEmail())){
                return true;
            }
        }
        return false;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

}