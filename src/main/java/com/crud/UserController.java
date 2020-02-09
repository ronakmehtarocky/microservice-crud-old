package com.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.crud.User;
import com.crud.UserRepository;
import com.crud.error.UserNotFoundException;
import com.crud.error.UserUnSupportedFieldPatchException;
@RestController
public class UserController {
	@Autowired
    private UserRepository repository;

    // Find
    @GetMapping("/users")
    List<User> findAll() {
        return repository.findAll();
    }

    // Save
    @PostMapping("/users")
    //return 201 instead of 200
    @ResponseStatus(HttpStatus.CREATED)
    User newUser(@RequestBody User newUser) {
        return repository.save(newUser);
    }

    // Find
    @GetMapping("/users/{userId}")
    User findOne(@PathVariable Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    // Save or update
    @PutMapping("/users/{userId}")
    User saveOrUpdate(@RequestBody User newUser, @PathVariable Long userId) {

         return repository.findById(userId)
                .map(x -> {
                    x.setFirstName(newUser.getFirstName());
                    x.setLastName(newUser.getLastName());
                    x.setEmail(newUser.getEmail());
                    x.setPhoneNumber(newUser.getPhoneNumber());
                    x.setAddressLine1(newUser.getAddressLine1());
                    x.setAddressLine2(newUser.getAddressLine2());
                    x.setAccount(newUser.getAccount());
                    return repository.save(x);
                })
                .orElseGet(() -> {
                    newUser.setUserId(userId);
                    return repository.save(newUser);
                });
    }

    // update email only
    @PatchMapping("/users/{userId}")
    User patch(@RequestBody Map<String, String> update, @PathVariable Long userId) {

        return repository.findById(userId)
                .map(x -> {

                    String email = update.get("email");
                    if (!StringUtils.isEmpty(email)) {
                        x.setFirstName(email);

                        // better create a custom method to update a value = :newValue where userId = :userId
                        return repository.save(x);
                    } else {
                        throw new UserUnSupportedFieldPatchException(update.keySet());
                    }

                })
                .orElseGet(() -> {
                    throw new UserNotFoundException(userId);
                });

    }

    @DeleteMapping("/users/{userId}")
    void deleteUser(@PathVariable Long userId) {
        repository.deleteById(userId);
    }

}
