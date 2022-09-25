package com.fabio.libraryapi.model.repository;


import com.fabio.libraryapi.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.OpenOption;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.setAllowComparingPrivateFields;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
//indica que vou fazer teste com jpa
@DataJpaTest
public class BookRepositoryTest {

    //simula o entityManager
    //criado apenas para fazer teste com jpa
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com isbn informado")
    public void returnTrueWhenIsbnExists(){
        //cenario
        String isbn ="123";
        //pesistir um book na base
        entityManager.persist(createNewBook());
        //execucao
        boolean exists = repository.existsByIsbn(isbn);
        //verificacao
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando nao existir um livro na base com isbn informado")
    public void returnFalseWhenIsbnDoesntExists(){
        //cenario
        String isbn ="123";
        //execucao
        boolean exists = repository.existsByIsbn(isbn);
        //verificacao
        assertThat(exists).isFalse();
    }


    @Test
    @DisplayName("Deve obter um livro por id")
    public void findByIdTest(){
        Book book = createNewBook();
        entityManager.persist(book);
        //cenario
        Optional<Book> foundBook = repository.findById(book.getId());
        //verificacao
        assertThat(foundBook.isPresent()).isTrue();
     }
    @Test
    @DisplayName("Dev Salvar um livro")
    public void saveBookTest(){
        //cenario
        Book book = createNewBook();
        //execucao
        Book saveBook= repository.save(book);
        //verificacao
        assertThat(saveBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deletBook(){
        //cenarrio
        Book book = createNewBook();
        entityManager.persist(book);
        //execucao
        //busca o livro persistido
        Book bookFound = entityManager.find(Book.class,book.getId());
        //Deleta o livro
        repository.delete(bookFound);
        //procura novamente para ter certeza que o livro foi deletado
        Book deleteBook = entityManager.find(Book.class,book.getId());
        //verifica se o obj é nulo mesmo.
        assertThat(deleteBook).isNull();
    }

    private Book createNewBook() {
        return Book.builder().isbn("123").title("As aventuras").author("Fulano").build();
    }
}
