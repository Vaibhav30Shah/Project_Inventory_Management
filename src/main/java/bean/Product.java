package bean;

public interface Product
{
    int getProductId();

    String getProductName();

    int getProductPrice();

    int getProductQuantity();

    void setProductQuantity(int quantity);
}