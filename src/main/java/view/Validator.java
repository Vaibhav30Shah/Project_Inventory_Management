package view;

public class Validator
{
    public static boolean isValidEmail(String email)
    {
        return email.matches("^[\\w\\.-]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    public static boolean isValidPassword(String password)
    {
        if (password == null || password.length() < 6) //length
        {
            return false;
        }

        boolean hasUppercase = !password.equals(password.toLowerCase()); //min 1 capital

        if (!hasUppercase)
        {
            return false;
        }

        boolean hasLowercase = !password.equals(password.toUpperCase()); //min 1 lowercase

        if (!hasLowercase)
        {
            return false;
        }

        boolean hasDigit = password.matches(".*\\d.*"); //min 1 digit

        if (!hasDigit)
        {
            return false;
        }

        boolean hasSpecialChar = !password.matches("[A-Za-z0-9 ]*"); //min 1 special character

        if (!hasSpecialChar)
        {
            return false;
        }

        if(!(password.length() == password.trim().length()))
        {
            return false;
        }

        return true;
    }

}
