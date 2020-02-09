package com.crud;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // for restTemplate
@ActiveProfiles("test")
public class AccountControllerRestTemplateTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private AccountRepository mockRepository;

    @Before
    public void init() {
        Account account = new Account(123L,12345.00);
        when(mockRepository.findById(123L)).thenReturn(Optional.of(account));
    }

    @Test
    public void find_accountNumber_OK() throws JSONException {

       
        String expected="{accountNumber:123,accountBalance:12345.00}";

        ResponseEntity<String> response = restTemplate.getForEntity("/accounts/123", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        JSONAssert.assertEquals(expected, response.getBody(), false);

        verify(mockRepository, times(1)).findById(123L);

    }

    @Test
    public void find_allAccount_OK() throws Exception {

        List<Account> accounts = Arrays.asList(
        		new Account(123L,12345.00),
                new Account(456L,45678.00));
        when(mockRepository.findAll()).thenReturn(accounts);

        String expected = om.writeValueAsString(accounts);

        ResponseEntity<String> response = restTemplate.getForEntity("/accounts", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);

        verify(mockRepository, times(1)).findAll();
    }
    @Test
    public void find_accountNumberNotFound_404() throws Exception {

        String expected = "{status:404,error:\"Not Found\",message:\"Account Number not found : 7878\",path:\"/accounts/7878\"}";

        ResponseEntity<String> response = restTemplate.getForEntity("/accounts/7878", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);

    }

    @Test
    public void save_account_OK() throws Exception {

        Account newAccount = new Account(123L,12345.00);
        when(mockRepository.save(any(Account.class))).thenReturn(newAccount);

        String expected = om.writeValueAsString(newAccount);

        ResponseEntity<String> response = restTemplate.postForEntity("/accounts", newAccount, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);

        verify(mockRepository, times(1)).save(any(Account.class));

    }

    @Test
    public void update_account_OK() throws Exception {

        Account updateAccount = new Account(123L,78989.00);
        when(mockRepository.save(any(Account.class))).thenReturn(updateAccount);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(updateAccount), headers);

        ResponseEntity<String> response = restTemplate.exchange("/accounts/123", HttpMethod.PUT, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(om.writeValueAsString(updateAccount), response.getBody(), false);

        verify(mockRepository, times(1)).findById(123L);
        verify(mockRepository, times(1)).save(any(Account.class));

    }

    @Test
    public void delete_account_OK() {

        doNothing().when(mockRepository).deleteById(123L);

        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange("/accounts/123", HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(mockRepository, times(1)).deleteById(123L);
    }


}
