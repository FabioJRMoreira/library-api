package com.fabio.libraryapi.api.exception;

import com.fabio.libraryapi.exception.BusinessException;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiErros {
    private List<String> errors;
    public ApiErros(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach(erro-> this.errors.add(erro.getDefaultMessage()));
    }

    public ApiErros(BusinessException ex) {
        this.errors= Arrays.asList(ex.getMessage());
    }

    public List<String> getErrors() {
        return errors;
    }
}
