package com.twelvefish.elasticsearchdemo.model;

import lombok.Data;

/**
 * @author twelvefish
 * @create 2021-08-22 下午 06:20
 */
@Data
public class Book {
    private String bookName;
    private String bookhref;
    private String img;
    private String price;
    private String date;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookhref() {
        return bookhref;
    }

    public void setBookhref(String bookhref) {
        this.bookhref = bookhref;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
