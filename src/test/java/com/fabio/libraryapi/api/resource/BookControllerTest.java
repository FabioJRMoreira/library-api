package com.fabio.libraryapi.api.resource;

import com.fabio.libraryapi.api.dto.BookDTO;
import com.fabio.libraryapi.entity.Book;
import com.fabio.libraryapi.exception.BusinessException;
import com.fabio.libraryapi.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    //simula as requisicoes para a api.
    MockMvc mvc;
    @MockBean
    //cria uma isntancia mocada do service.
    BookService service;


    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBookTest() throws Exception {

        BookDTO dto = createNewBook();

        Book saveBook = Book.builder().id(10l).author("Artur").title("As aventuras").isbn("001").build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(saveBook);
        //recebe um ojeto e trasforma em json
        String json = new ObjectMapper().writeValueAsString(dto);

        //criando uma requisicao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                //metodo a ser validado nesse caso post
                .post(BOOK_API)
                //passa o conteudo do tipo joson
                .contentType(MediaType.APPLICATION_JSON)
                //servidor aceita requisicoes do tipo json
                .accept(MediaType.APPLICATION_JSON)
                //envia o json criado para a requisicao
                .content(json);
        //fazer a requisicao
        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));
    }

    //teste de integridade do obj
    @Test
    @DisplayName("Deve lancar erro de validacao quando nai houver dados suficientes")
    public void createInvalidBookTest() throws Exception {
        //recebe um ojeto e trasforma em json
        //passa um book vazio
        String json = new ObjectMapper().writeValueAsString(new BookDTO());
        //cria a requisicao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                //metodo a ser validado
                .post(BOOK_API)
                //passa o conteudo do tipo joson
                .contentType(MediaType.APPLICATION_JSON)
                //servidor aceita requisicoes do tipo json
                .accept(MediaType.APPLICATION_JSON)
                //envia o json criado para a requisicao
                .content(json);
        //valida a requisico
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

    //nao deve salvar com isbn repetido
    @Test
    @DisplayName("Deve lancar erro ao tentar cadastrar um livro com isbn ja utilizadado por outro")
    public void createBookWithDuplicateIsbn() throws Exception {
        //CRIA UM DTO valido
        BookDTO bookDTO = createNewBook();
        //recebe um ojeto e trasforma em json
        String json = new ObjectMapper().writeValueAsString(bookDTO);

        //quando o serve for salvar o book vai lancar um bussines exption
        BDDMockito
                .given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException("Isbn ja cadastrado."));

        //cria a requisicao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                //metodo a ser validado
                .post(BOOK_API)
                //passa o conteudo do tipo joson
                .contentType(MediaType.APPLICATION_JSON)
                //servidor aceita requisicoes do tipo json
                .accept(MediaType.APPLICATION_JSON)
                //envia o json criado para a requisicao
                .content(json);

        //executa a request
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Isbn ja cadastrado."));
    }

    //obter as informacoes de um livro

    @Test
    @DisplayName("Deve obiter informacoes de um livro")
    public void getBookDetailsTest() throws Exception {
        //cenario
        long id = 1l;
        Book book = Book.builder()
                .id(id)
                .author("Artur")
                .title("As aventuras")
                .isbn("001")
                .build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        //execucao

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        //verificacao
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()));
    }

    // get lancar erro quando o book nao tiver na base

    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado nao existir ")
    public void bookNotFoundTest() throws Exception {
        //cenario
        //quando for no ser passando qualquer long deve retornar um opt vazio
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
        //execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        //verificacao
        mvc.perform(request)
                .andExpect(status().isNotFound());


    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBook() throws Exception {
        //cenario
        //cria um book com id
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1l).build()));
        //execucao
        //faz a requisicao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(request)
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Deve retornar resource note foud quando nao encontrar um livro para deletar")
    public void deleteInesistenteBook() throws Exception {
        //cenario
        //cria um book com id
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
        //execucao
        //faz a requisicao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(request)
                .andExpect(status().isNotFound());

    }

    //TESTA UPDATE BOOK
    @Test
    @DisplayName("Deve Atualizar um livro")
    public void updateBookTest() throws Exception {
        //cenario
        Long id = 1l;
        String json  = new ObjectMapper().writeValueAsString(createNewBook());
        //cria um book com id
        Book book = Book.builder().id(1l).title("some title").author("some author").isbn("321").build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        Book atualizado = Book.builder().id(id).author("Artur").title("As aventuras").isbn("321").build();
        //moca o retorno do obj
        BDDMockito.given(service.update(book)).willReturn(atualizado);

        //execucao
        //faz a requisicao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        //verificacao
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value("321"));
                ;
    }

    @Test
    @DisplayName("Deve retornar  404 ao tentar atualizar um livro inexixtente ")
    public void updateInexistenteBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        //retorna um obj vazio
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
        //faz a requisicao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/"+1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        //Verifica
        mvc.perform(request).andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("Deve filtrar livros")
    public void findBookTeste()throws Exception{
        //ccenario
        Long id = 1l;
        Book book = Book.builder()
                .id(id)
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .build();
        BDDMockito.given(service.find(Mockito.any(Book.class),Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0,100),1));
        //execuao
        String query  = String.format("?title=%s&author=%s&page0&size=100",book.getTitle(),book.getAuthor());
        //faz a requisicao com a paginacao
        MockHttpServletRequestBuilder request=MockMvcRequestBuilders
                .get(BOOK_API.concat(query))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                //verifica o tamanho da lista que rotonou
                .andExpect(jsonPath("content",Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }


    //cria um novo BOOK
    private BookDTO createNewBook() {
        return BookDTO.builder().author("Artur").title("As aventuras").isbn("001").build();
    }
}
