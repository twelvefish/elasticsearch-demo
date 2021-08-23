package com.twelvefish.elasticsearchdemo.utils;

import com.sun.org.apache.xml.internal.res.XMLErrorResources_tr;
import com.twelvefish.elasticsearchdemo.model.Book;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author twelvefish
 * @create 2021-08-22 下午 04:22
 */
@Component
public class HtmlParseUtils {
    public static void main(String[] args) throws Exception {
        new HtmlParseUtils().parseJD("演算法").forEach(System.out::println);
    }

    public List<Book> parseJD(String keywords) throws Exception {

        // URL參數中文編碼
        String keywordsUTF8 = URLEncoder.encode(keywords, "UTF-8");
        String url ="https://www.tenlong.com.tw/search?keyword=" + keywordsUTF8;

        Document document = Jsoup.parse(new URL(url),30000);
        Elements elements = document.getElementsByClass("col-span-6 md:col-span-3 lg:col-span-2 h-full");

        ArrayList<Book> bookArrayList = new ArrayList<>();

        for(Element el : elements){
            String bookName = el.getElementsByClass("book-data").eq(0).get(0).getElementsByTag("a").eq(0).text();
            String bookhref = el.getElementsByClass("book-data").eq(0).get(0).getElementsByTag("a").eq(0).attr("href");
            String img = el.getElementsByTag("img").eq(0).attr("src");
            String price = el.getElementsByClass("price").eq(0).text();
            String date = el.getElementsByClass("publish-date").eq(0).text();

            Book book = new Book();
            book.setBookName(bookName);
            book.setBookhref("https://www.tenlong.com.tw/" + bookhref);
            book.setImg(img);
            book.setPrice(price);
            book.setDate(date);
            bookArrayList.add(book);
        }
        return bookArrayList;
    }
}
