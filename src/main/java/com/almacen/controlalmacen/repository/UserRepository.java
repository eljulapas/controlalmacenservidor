package com.almacen.controlalmacen.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almacen.controlalmacen.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
   Optional<User> findByNombre(String nombre);
	


}
