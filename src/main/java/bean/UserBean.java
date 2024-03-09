package bean;

import java.util.ArrayList;

public class UserBean {

    private int userId;
    private String firstName, email, password;
    private boolean isAdmin = false;
    public ArrayList<ProductBean> cart = new ArrayList<>();

    public UserBean(String firstName, String email, String password) {
        this.userId = (int) (Math.random() * 100000);
        this.firstName = firstName;
        this.email = email;
        this.password = password;
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