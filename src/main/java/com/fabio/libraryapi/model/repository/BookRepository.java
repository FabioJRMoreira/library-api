package com.fabio.libraryapi.model.repository;

import com.fabio.libraryapi.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Long> {

    //por padrao retorna false.
    boolean existsByIsbn(String isbn);
}
