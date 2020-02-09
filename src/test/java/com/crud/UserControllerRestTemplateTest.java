package com.crud;

import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // for restTemplate
@ActiveProfiles("test")
public class UserControllerRestTemplateTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private UserRepository mockRepository;

    @Before
    public void init() {
        User user = new User(1L, "Ronak","Mehta","ronak@abc.com",7878789878L,"Pune","India");
        when(mockRepository.findById(1L)).thenReturn(Optional.of(user));
    }

    @Test
    public void find_userId_OK() throws JSONException {

       
        String expected="{userId:1,firstName:\"Ronak\",lastName:\"Mehta\",email:\"ronak@abc.com\",phoneNumber:7878789878,"
        		+ "addressLine1:\"Pune\",addressLine2:\"India\"}";
        //String expected="{userId:1,firstName:\"Ronak\",lastName:\"Mehta\",email:\"ronak@abc.com\",phoneNumber:7878898967,"
        //		+ "addressLine1:\"Pune\",addressLine2:\"India\",account:[{accountNumber:123,accountBalance:12345}]}";
        
        ResponseEntity<String> response = restTemplate.getForEntity("/users/1", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        JSONAssert.assertEquals(expected, response.getBody(), false);

        verify(mockRepository, times(1)).findById(1L);

    }

    @Test
    public void find_allUser_OK() throws Exception {

        List<User> users = Arrays.asList(
                new User(1L, "Ronak","Mehta","ronak@abc.com",7878789878L,"Pune","India"),
                new User(2L, "Rohit","Mehta","rohit@abc.com",7878789878L,"Pune","India"));

        when(mockRepository.findAll()).thenReturn(users);

        String expected = om.writeValueAsString(users);

        ResponseEntity<String> response = restTemplate.getForEntity("/users", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);

        verify(mockRepository, times(1)).findAll();
    }

    @Test
    public void find_userIdNotFound_404() throws Exception {

        String expected = "{status:404,error:\"Not Found\",message:\"User id not found : 5\",path:\"/users/5\"}";

        ResponseEntity<String> response = restTemplate.getForEntity("/users/5", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);

    }

    @Test
    public void save_user_OK() throws Exception {

        User newUser = new User(1L, "Ronak","Mehta","ronak@abc.com",7878789878L,"Pune","India");
        when(mockRepository.save(any(User.class))).thenReturn(newUser);

        String expected = om.writeValueAsString(newUser);

        ResponseEntity<String> response = restTemplate.postForEntity("/users", newUser, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);

        verify(mockRepository, times(1)).save(any(User.class));

    }

    @Test
    public void update_user_OK() throws Exception {

        User updateUser = new User(1L, "Ronak","Mehta","ronak@abc.com",7878789878L,"Mumbai","India");
        when(mockRepository.save(any(User.class))).thenReturn(updateUser);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(updateUser), headers);

        ResponseEntity<String> response = restTemplate.exchange("/users/1", HttpMethod.PUT, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(om.writeValueAsString(updateUser), response.getBody(), false);

        verify(mockRepository, times(1)).findById(1L);
        verify(mockRepository, times(1)).save(any(User.class));

    }

    @Test
    public void patch_userEmail_OK() {

        when(mockRepository.save(any(User.class))).thenReturn(new User());
        String patchInJson = "{\"email\":\"ronak@gmail.com\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(patchInJson, headers);

        ResponseEntity<String> response = restTemplate.exchange("/users/1", HttpMethod.PUT, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(mockRepository, times(1)).findById(1L);
        verify(mockRepository, times(1)).save(any(User.class));

    }

    @Test
    public void patch_userSalary_405() throws JSONException {

        String expected = "{status:405,error:\"Method Not Allowed\",message:\"Field [salary] update is not allow.\"}";

        String patchInJson = "{\"salary\":\"10000\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(patchInJson, headers);

        ResponseEntity<String> response = restTemplate.exchange("/users/1", HttpMethod.PATCH, entity, String.class);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);

        verify(mockRepository, times(1)).findById(1L);
        verify(mockRepository, times(0)).save(any(User.class));
    }

    @Test
    public void delete_user_OK() {

        doNothing().when(mockRepository).deleteById(1L);

        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange("/users/1", HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(mockRepository, times(1)).deleteById(1L);
    }


}
