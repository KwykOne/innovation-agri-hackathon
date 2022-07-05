package com.akashorderandpickup.akashadminonp.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Item {
    private String itemName;
    private String itemPrice;
    private String photo;
    private String catalogue;
    private Boolean stock;//Item is available in Stock or Out of stock

    //Adding NEW Fields created when product page was designed
    private ArrayList<String> productImage = new ArrayList<>();
    private String productName;
    private List<String> productCategory;
    private Long productPrice;
    private Long productDiscountedPrice;
    private Long productQuantity;
    private String productUnit;
    private String productDescription;
    private Boolean inStock;
    private Boolean isFromCatalogue;
    private Boolean salePrice;
    private Boolean exists;
    //private String variants[];

    //Creating the same as product class object
    public Item(ArrayList<String> productImage, String productName, List<String> productCategory, Long productPrice, Long productDiscountedPrice, Long productQuantity, String productUnit, String productDescription, Boolean inStock, Boolean salePrice, Boolean exists, Boolean isFromCatalogue) {
        this.productImage = productImage;
        this.productName = productName;
        this.productCategory = productCategory;
        this.productPrice = productPrice;
        this.productDiscountedPrice = productDiscountedPrice;
        this.productQuantity = productQuantity;
        this.productUnit = productUnit;
        this.productDescription = productDescription;
        this.inStock = inStock;
        this.salePrice = salePrice;
        this.exists = exists;
        this.isFromCatalogue = isFromCatalogue;

        setPreviousFields();
    }

    private void setPreviousFields(){
        setItemName(productName);
        setItemPrice(productDiscountedPrice.toString());
        setStock(inStock);
        setCatalogue(isFromCatalogue.toString());

        if(productImage.size()>0){
            setPhoto(productImage.get(0));
        }else{
            setPhoto("NA");
        }
    }

    public Item(){}
    public Item(String itemName, String itemPrice){
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.photo = "NA";
        this.catalogue = "false";
    }
    public Item(String itemName, String itemPrice, String photo) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.photo = photo;
        this.catalogue = "false";
    }

    public Item(String itemName, String itemPrice, Boolean stock) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.stock = stock;
    }

    public Item(String itemName, String itemPrice, String photo, Boolean stock) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.photo = photo;
        this.stock = stock;
    }

    public Item(String itemName, String itemPrice, String photo, String catalogue) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.photo = photo;
        this.catalogue = catalogue;
    }

    public Item(String itemName, String itemPrice, String photo, String catalogue, Boolean stock) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.photo = photo;
        this.catalogue = catalogue;
        this.stock = stock;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCatalogue() {
        return catalogue;
    }

    public void setCatalogue(String catalogue) {
        this.catalogue = catalogue;
    }

    public Boolean getStock() {
        return stock;
    }

    public void setStock(Boolean stock) {
        this.stock = stock;
    }



    //Setter Getters from Product Class
    public ArrayList<String> getProductImage() {
        return productImage;
    }

    public void setProductImage(ArrayList<String> productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<String> getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(List<String> productCategory) {
        this.productCategory = productCategory;
    }

    public Number getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Long productPrice) {
        this.productPrice = productPrice;
    }

    public Number getProductDiscountedPrice() {
        return productDiscountedPrice;
    }

    public void setProductDiscountedPrice(Long productDiscountedPrice) {
        this.productDiscountedPrice = productDiscountedPrice;
    }

    public Number getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Boolean getFromCatalogue() {
        return isFromCatalogue;
    }

    public void setFromCatalogue(Boolean fromCatalogue) {
        isFromCatalogue = fromCatalogue;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    public Boolean getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Boolean salePrice) {
        this.salePrice = salePrice;
    }

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }
}
