package com.fabio.libraryapi.api.resource;

import com.fabio.libraryapi.api.dto.BookDTO;
import com.fabio.libraryapi.entity.Book;
import com.fabio.libraryapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API="/api/books";

    @Autowired
    //simula as requisicoes para a api.
    MockMvc mvc;
    @MockBean
            //cria uma isntancia mocada do service.
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBookTest()throws  Exception{

        BookDTO dto = BookDTO.builder().author("Artur").title("As aventuras").isbn("001").build();

        Book saveBook = Book.builder().id(10l).author("Artur").title("As aventuras").isbn("001").build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(saveBook);
        //recebe um ojeto e trasforma em json
        String json = new ObjectMapper().writeValueAsString(dto);

        //criando uma requisicao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
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
                .andExpect(jsonPath("isbn").value(dto.getIsbn()))
        ;

    }

    @Test
    @DisplayName("Deve lancar erro de validacao quando nai houver dados suficientes")
    public void createInvalidBookTest(){

    }


}
