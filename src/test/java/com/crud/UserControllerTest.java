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

//@WebMvcTest(UserController.class)
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository mockRepository;

    @Before
    public void init() {
    	 User user = new User(1L, "Ronak","Mehta","ronak@abc.com",7878789878L,"Pune","India");
        when(mockRepository.findById(1L)).thenReturn(Optional.of(user));
    }

    @Test
    public void find_userId_OK() throws Exception {

        mockMvc.perform(get("/users/1"))
                /*.andDo(print())*/
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.firstName", is("Ronak")))
                .andExpect(jsonPath("$.lastName", is("Mehta")))
                .andExpect(jsonPath("$.email", is("ronak@abc.com")))
                .andExpect(jsonPath("$.phoneNumber", is(7878789878L)))
                .andExpect(jsonPath("$.addressLine1", is("Pune")))
                .andExpect(jsonPath("$.addressLine2", is("India")));

        verify(mockRepository, times(1)).findById(1L);

    }

    @Test
    public void find_allUser_OK() throws Exception {

        List<User> users = Arrays.asList(
                new User(1L, "Ronak","Mehta","ronak@abc.com",7878789878L,"Pune","India"),
                new User(2L, "Rohit","Mehta","rohit@abc.com",7878789878L,"Pune","India"));

        when(mockRepository.findAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("Ronak")))
                .andExpect(jsonPath("$[0].lastName", is("Mehta")))
                .andExpect(jsonPath("$[0].email", is("ronak@abc.com")))
                .andExpect(jsonPath("$[0].phoneNumber", is(7878789878L)))
                .andExpect(jsonPath("$[0].addressLine1", is("Pune")))
                .andExpect(jsonPath("$[0].addressLine2", is("India")))
        .andExpect(jsonPath("$[1].userId", is(2)))
        .andExpect(jsonPath("$[1].firstName", is("Rohit")))
        .andExpect(jsonPath("$[1].lastName", is("Mehta")))
        .andExpect(jsonPath("$[1].email", is("rohit@abc.com")))
        .andExpect(jsonPath("$[1].phoneNumber", is(7878789878L)))
        .andExpect(jsonPath("$[1].addressLine1", is("Pune")))
        .andExpect(jsonPath("$[1].addressLine2", is("India")));

        verify(mockRepository, times(1)).findAll();
    }

    @Test
    public void find_userIdNotFound_404() throws Exception {
        mockMvc.perform(get("/users/5")).andExpect(status().isNotFound());
    }

    @Test
    public void save_user_OK() throws Exception {

        User newUser = new User(1L, "Ronak","Mehta","ronak@abc.com",7878789878L,"Pune","India");
        when(mockRepository.save(any(User.class))).thenReturn(newUser);

        mockMvc.perform(post("/users")
                .content(om.writeValueAsString(newUser))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                /*.andDo(print())*/
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.firstName", is("Ronak")))
                .andExpect(jsonPath("$.lastName", is("Mehta")))
                .andExpect(jsonPath("$.email", is("ronak@abc.com")))
                .andExpect(jsonPath("$.phoneNumber", is(7878789878L)))
                .andExpect(jsonPath("$.addressLine1", is("Pune")))
                .andExpect(jsonPath("$.addressLine2", is("India")));

        verify(mockRepository, times(1)).save(any(User.class));

    }

    @Test
    public void update_user_OK() throws Exception {

        User updateUser = new User(1L, "Ronak","Mehta","ronak@gmail.com",7878789878L,"Mumbai","India");
        when(mockRepository.save(any(User.class))).thenReturn(updateUser);

        mockMvc.perform(put("/users/1")
                .content(om.writeValueAsString(updateUser))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Ronak")))
                .andExpect(jsonPath("$.lastName", is("Mehta")))
                .andExpect(jsonPath("$.email", is("ronak@gmail.com")))
                .andExpect(jsonPath("$.phoneNumber", is(7878789878L)))
                .andExpect(jsonPath("$.addressLine1", is("Mumbai")))
                .andExpect(jsonPath("$.addressLine2", is("India")));

    }

    @Test
    public void patch_userEmail_OK() throws Exception {

    	 User newUser = new User(1L, "Ronak","Mehta","ronak@abc.com",7878789878L,"Pune","India");
         when(mockRepository.save(any(User.class))).thenReturn(newUser);

//        when(mockRepository.save(any(User.class))).thenReturn(new User());
        String patchInJson = "{\"email\":\"ronak.mehta@gmail.com\"}";

        mockMvc.perform(patch("/users/1")
                .content(patchInJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(mockRepository, times(1)).findById(1L);
        verify(mockRepository, times(1)).save(any(User.class));

    }

    @Test
    public void patch_userSalary_405() throws Exception {

        String patchInJson = "{\"salary\":\"100000\"}";

        mockMvc.perform(patch("/users/1")
                .content(patchInJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());

        verify(mockRepository, times(1)).findById(1L);
        verify(mockRepository, times(0)).save(any(User.class));
    }

    @Test
    public void delete_user_OK() throws Exception {

        doNothing().when(mockRepository).deleteById(1L);

        mockMvc.perform(delete("/users/1"))
                /*.andDo(print())*/
                .andExpect(status().isOk());

        verify(mockRepository, times(1)).deleteById(1L);
    }

}
