package com.mycom.samples.webflux.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.mycom.samples.webflux.model.Article;

import reactor.core.publisher.Flux;

@Repository
public interface ArticleRepository extends ReactiveMongoRepository<Article, Integer> {

	@Query("{'author': ?0}")
	Flux<Article> findByAuthor(String author);

}