package com.example.ormdemo.repository;

import com.example.ormdemo.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Query có JOIN FETCH - giải quyết N+1
    @Query("SELECT b FROM Book b JOIN FETCH b.publisher")
    List<Book> findAllWithPublisher();
}