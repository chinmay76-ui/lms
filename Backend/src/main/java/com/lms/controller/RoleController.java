package com.lms.controller;

import com.lms.entity.Role;
import com.lms.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService service;

    @PostMapping("/add")
    public Role addRole(@RequestBody Role role) {
        return service.saveRole(role);
    }

    @GetMapping("/all")
    public List<Role> getAllRoles() {
        return service.getAllRoles();
    }
}
