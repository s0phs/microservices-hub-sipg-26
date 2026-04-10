package com.github.s0phs.ms_pagamentos.service;

import com.github.s0phs.ms_pagamentos.entities.Pagamento;
import com.github.s0phs.ms_pagamentos.exceptions.ResourceNotFoundException;
import com.github.s0phs.ms_pagamentos.repositories.PagamentoRepository;
import com.github.s0phs.ms_pagamentos.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Long existingId;
    private Long nonExistingId;

    private Pagamento pagamento;

    @BeforeEach
    void setUp(){
        existingId = 1L;
        nonExistingId = Long.MAX_VALUE;

        pagamento = Factory.createPagamento();
    }

    @Test
    void deletePagamentoByIdShouldDeleteWhenIdExists(){

        Mockito.when(pagamentoRepository.existsById(existingId)).thenReturn(true);

        pagamentoService.deletePagamentoById(existingId);

        Mockito.verify(pagamentoRepository).existsById(existingId);

        Mockito.verify(pagamentoRepository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("deletePagamentoById deveria lançar ResourceNotFoundException quando o Id não existir")
    void deletePagamentoByIdShouldThrowResourseNotFoundExceptionWhenIdDoesNotExist(){

        Mockito.when(pagamentoRepository.existsById(nonExistingId)).thenReturn(false);

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> {
                    pagamentoService.deletePagamentoById(nonExistingId);
                });

        Mockito.verify(pagamentoRepository).existsById(nonExistingId);

        Mockito.verify(pagamentoRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }
}
