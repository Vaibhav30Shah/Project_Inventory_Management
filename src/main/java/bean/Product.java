package bean;

public interface Product
{
    String getProductId();

    String getProductName();

    void setProductname(String name);

    int getProductPrice();

    void setProductPrice(int price);

    int getQuantity();

    void setQuantity(int quantity);
}