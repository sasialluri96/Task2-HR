package com.example.TASK2.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public class EmployeeDTO {
    @NotBlank(message = "Name must not be blank")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Name must contain only letters")
    private String name;
    @NotBlank(message = "Department must not be blank")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Department must contain only letters")
    private String department;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        if (name == null || name.trim().equalsIgnoreCase("null")) {
            this.name = null;
        } else {
            this.name = name.trim();
        }
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        if (department == null || department.trim().equalsIgnoreCase("null") || department.trim().isEmpty()) {
            this.department = null;
        } else {
            this.department = department.trim();
        }
    }
}

