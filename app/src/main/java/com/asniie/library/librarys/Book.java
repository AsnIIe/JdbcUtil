package com.asniie.library.librarys;

import java.io.Serializable;

/*
 * Created by XiaoWei on 2019/1/12.
 */
public class Book implements Serializable {
    private String name;
    private double price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
