package com.crud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(AccountController.class)
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AccountControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountRepository mockRepository;

    @Before
    public void init() {
    	 Account account = new Account(123L,12345.00);
        when(mockRepository.findById(123L)).thenReturn(Optional.of(account));
    }

    @Test
    public void find_accountId_OK() throws Exception {

        mockMvc.perform(get("/accounts/123"))
                /*.andDo(print())*/
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber", is(123)))
                .andExpect(jsonPath("$.accountBalance", is(12345.00)));

        verify(mockRepository, times(1)).findById(123L);

    }

    @Test
    public void find_allAccount_OK() throws Exception {

        List<Account> accounts = Arrays.asList(
                new Account(123L,12345.00),
                new Account(456L,45678.00));

        when(mockRepository.findAll()).thenReturn(accounts);

        mockMvc.perform(get("/accounts"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].accountNumber", is(123)))
                .andExpect(jsonPath("$[0].accountBalance", is(12345.00)))
        		.andExpect(jsonPath("$[1].accountNumber", is(456)))
        		.andExpect(jsonPath("$[1].accountBalance", is(45678.00)));

        verify(mockRepository, times(1)).findAll();
    }

    @Test
    public void find_accountNumberNotFound_404() throws Exception {
        mockMvc.perform(get("/accounts/90")).andExpect(status().isNotFound());
    }

    @Test
    public void save_account_OK() throws Exception {

        Account newAccount = new Account(123L,12345.00);
        when(mockRepository.save(any(Account.class))).thenReturn(newAccount);

        mockMvc.perform(post("/accounts")
                .content(om.writeValueAsString(newAccount))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                /*.andDo(print())*/
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber", is(123)))
                .andExpect(jsonPath("$.accountBalance", is(12345.00)));

        verify(mockRepository, times(1)).save(any(Account.class));

    }

    @Test
    public void update_account_OK() throws Exception {

        Account updateAccount = new Account(123L,224466.00);
        when(mockRepository.save(any(Account.class))).thenReturn(updateAccount);

        mockMvc.perform(put("/accounts/123")
                .content(om.writeValueAsString(updateAccount))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber", is(123)))
                .andExpect(jsonPath("$.accountBalance", is(224466.00)));

    }

    @Test
    public void delete_account_OK() throws Exception {

        doNothing().when(mockRepository).deleteById(123L);

        mockMvc.perform(delete("/accounts/123"))
                /*.andDo(print())*/
                .andExpect(status().isOk());

        verify(mockRepository, times(1)).deleteById(123L);
    }

}
