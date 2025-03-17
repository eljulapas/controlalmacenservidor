package com.almacen.controlalmacen.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.almacen.controlalmacen.model.User;
import com.almacen.controlalmacen.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	 @Autowired
	    private UserService userService;
	 
	 /*@GetMapping("/")
	 public List<User> getAllUsers() {
		    System.out.println("Obteniendo todos los usuarios...");
		    List<User> users = userService.getAllUsers();
		    System.out.println("Usuarios encontrados: " + users.size());
		    return users;
		}*/


	    @GetMapping("/{id}")
	    public User getUserById(@PathVariable Long id) {
	        return userService.getUserById(id);
	    }

	    @PostMapping("/")
	    public User createUser(@RequestBody User user) {
	        return userService.saveUser(user);
	    }
	    
	    
	    @GetMapping("/")
	    public List<User> getAllUsers() {
	        System.out.println("Obteniendo todos los usuarios...");
	        return userService.getAllUsers();
	    }


}
