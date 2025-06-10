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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
