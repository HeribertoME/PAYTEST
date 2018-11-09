package com.hmelizarraraz.testbilling.skulist.row;

public class SkuRowData {
    private final String sku, title, price, description, billingType;

    public SkuRowData(String sku, String title, String price,
                      String description, String type) {
        this.sku            = sku;
        this.title          = title;
        this.price          = price;
        this.description    = description;
        this.billingType    = type;
    }

    public String getSku() {
        return sku;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getBillingType() {
        return billingType;
    }
}
