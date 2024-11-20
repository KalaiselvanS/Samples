package com.mycom.samples.webflux.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycom.samples.webflux.model.Article;
import com.mycom.samples.webflux.service.ArticleService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/findAll")
    public Flux<Article> getAllArticles() {
       return articleService.findAllArticles();
    }

    @PostMapping("/save")
    public Mono<ResponseEntity<Article>> createArticle(@RequestBody Article article) {
       return articleService.saveArticle(article)
             .map(savedArticle -> new ResponseEntity<>(savedArticle, HttpStatus.CREATED));
    }

    @GetMapping("/id/{id}")
    public Mono<ResponseEntity<Article>> getArticleById(@PathVariable Integer id) {
       return articleService.findOneArticle(id)
             .map(article -> ResponseEntity.ok(article))
             .defaultIfEmpty(ResponseEntity.notFound().build());
    }

//    @GetMapping("/author/{author}")
//    public Flux<ResponseEntity<Article>> getArticleByAuthor(@PathVariable String author) {
//       return articleService.findByAuthor(author)
//             .map(article -> ResponseEntity.ok(article))
//             .defaultIfEmpty(ResponseEntity.notFound().build());
//    }
//    The above method throws: java.lang.IllegalArgumentException: Only a single ResponseEntity supported
    @GetMapping("/author/{author}")
    public Flux<Article> getArticleByAuthor(@PathVariable String author) {
       return articleService.findByAuthor(author);
    }

    @PutMapping("/update/{articleId}")
    public Mono<ResponseEntity<Article>> updateArticle(@PathVariable Integer articleId,
                                   @RequestBody Article article) {
       return articleService.findOneArticle(articleId)
             .flatMap(existingArticle -> {
                  existingArticle.setTitle(article.getTitle());
                  existingArticle.setContent(article.getContent());
                  existingArticle.setAuthor(article.getAuthor());
                  existingArticle.setPublishedAt(article.getPublishedAt());
                  return articleService.saveArticle(existingArticle);
             })
            .map(updatedArticle -> new ResponseEntity<>(updatedArticle, HttpStatus.OK))
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/delete/{articleId}")
    public Mono<ResponseEntity<Void>> deleteArticle(@PathVariable Integer articleId) {
       return articleService.deleteArticle(articleId)
             .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
             .onErrorResume(error -> Mono.just(new ResponseEntity<Void>(HttpStatus.NOT_FOUND)));
    }
}