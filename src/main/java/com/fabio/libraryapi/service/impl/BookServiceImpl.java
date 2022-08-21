package com.fabio.libraryapi.service.impl;

import com.fabio.libraryapi.entity.Book;
import com.fabio.libraryapi.exception.BusinessException;
import com.fabio.libraryapi.model.repository.BookRepository;
import com.fabio.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {


    BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn()))
        {
            throw new BusinessException("Isbn ja cadastrado.");
        }
        return repository.save(book);
    }
}
