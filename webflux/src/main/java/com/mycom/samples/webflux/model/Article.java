package com.mycom.samples.webflux.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

//@Data
//@AllArgsConstructor
@Document(collection = "articles")
public class Article {

    @Id
    private Integer id;
    private String title;
    private String content;
    private String author;
    private Date publishedAt;

    public Integer getId() {
       return id;
    }
    public void setId(Integer id) {
       this.id = id;
    }
    public String getTitle() {
       return title;
    }
    public void setTitle(String title) {
       this.title = title;
    }
    public String getContent() {
       return content;
    }
    public void setContent(String content) {
       this.content = content;
    }
    public String getAuthor() {
       return author;
    }
    public void setAuthor(String author) {
       this.author = author;  
    }
    public Date getPublishedAt() {
       return publishedAt;
    }
    public void setPublishedAt(Date publishedAt) {
       this.publishedAt = publishedAt;
    }
}