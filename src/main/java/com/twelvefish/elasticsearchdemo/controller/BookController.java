package com.twelvefish.elasticsearchdemo.controller;

import com.twelvefish.elasticsearchdemo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author twelvefish
 * @create 2021-08-22 下午 06:36
 */
@RestController
@CrossOrigin(value = "http://localhost:63342/")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("/parse/{keywords}")
    public Boolean parse(@PathVariable("keywords") String keywords) throws Exception {
        return bookService.ParseContent(keywords);
    }

    @GetMapping("/search/{keywords}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> search(@PathVariable("keywords") String keywords,
                                           @PathVariable("pageNo") int pageNo,
                                           @PathVariable("pageSize") int pageSize) throws Exception {
        return bookService.searchHighPage(keywords, pageNo, pageSize);
    }

}
