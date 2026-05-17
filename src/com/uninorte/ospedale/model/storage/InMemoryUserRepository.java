/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.model.storage;

import com.uninorte.ospedale.model.entity.User;
import com.uninorte.ospedale.model.repository.IUserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Samuel Ramirez
 */
public class InMemoryUserRepository implements IUserRepository {

    private final Map<Long, User> byId = new HashMap<>();
    private final Map<String, User> byUsername = new HashMap<>();

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(byUsername.get(username));
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public boolean existsById(long id) {
        return byId.containsKey(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return byUsername.containsKey(username);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(byId.values());
    }

    @Override
    public void save(User u) {
        User old = byId.put(u.getId(), u);
        if (old != null && !old.getUsername().equals(u.getUsername())) {
            byUsername.remove(old.getUsername());
        }
        byUsername.put(u.getUsername(), u);
    }
}
