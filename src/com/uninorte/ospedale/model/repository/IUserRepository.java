/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.uninorte.ospedale.model.repository;

import java.util.List;
import java.util.Optional;
import com.uninorte.ospedale.model.entity.User;
/**
 *
 * @author Samuel Ramirez
 */
public interface IUserRepository {
    Optional<User> findByUsername(String username);
    Optional<User> findById(long id);
    boolean existsById(long id);
    boolean existsByUsername(String username);
    List<User> findAll();
    void save(User u);
    
}
