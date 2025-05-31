package com.example.TASK2.entities;

import lombok.Data;
@Data
public class Employee {
    private int id;
    private String name;
    private String department;
    public Employee() {
   
    }
    public Employee(int id, String department, String name) {
        this.department = department;
        this.name = name;
        this.id = id;
    }
    
}
