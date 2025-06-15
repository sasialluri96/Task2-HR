package com.example.TASK2.service;
import com.example.TASK2.dto.EmployeeDTO;
import com.example.TASK2.entities.Employee;
import com.example.TASK2.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@Service
public class HrService {
    @Autowired
    private WebClient webClient;
    private boolean isAuthenticated(HttpSession session) {
        String username = (String) session.getAttribute("username");
        String password = (String) session.getAttribute("password");
        return username != null && password != null;
    }
    public Employee createEmployee(EmployeeDTO employeeDTO, HttpSession session) {
        if (!isAuthenticated(session)) {
            throw new RuntimeException("Please provide credentials");
        }
        String username = (String) session.getAttribute("username");
        String password = (String) session.getAttribute("password");
        try {
            return webClient.post()
                    .uri("/add")
                    .headers(headers -> headers.setBasicAuth(username, password))
                    .bodyValue(employeeDTO)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw new RuntimeException("Failed to create employee", ex);
        }

    }
    public Employee updateEmployee(int id, EmployeeDTO employeeDTO, HttpSession session) {
        if (!isAuthenticated(session)) {
            throw new RuntimeException("Please provide credentials");
        }
        String username = (String) session.getAttribute("username");
        String password = (String) session.getAttribute("password");
        try {
            return webClient.put()
                    .uri("/update/{id}", id)
                    .headers(headers -> headers.setBasicAuth(username, password))
                    .bodyValue(employeeDTO)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new ResourceNotFoundException("Record not present");
        }
    }
    public String deleteEmployee(int id, HttpSession session) {
        if (!isAuthenticated(session)) {
            throw new RuntimeException("Please provide credentials");
        }
        String username = (String) session.getAttribute("username");
        String password = (String) session.getAttribute("password");
        try {
            return webClient.delete()
                    .uri("/delete/{id}", id)
                    .headers(headers -> headers.setBasicAuth(username, password))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new ResourceNotFoundException("Record not present");
        }
    }

    public Employee getEmployeeById(int id) {
        try {
            return webClient.get()
                    .uri("/employees/{id}", id)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new ResourceNotFoundException("Record not present");
            } else {
                throw new RuntimeException("Failed to retrieve employee", e);
            }
        }
    }

    public List<Employee> getEmployeesByDepartment(String department) {
         try {
             return webClient.get()
                    .uri("/employees/department/{department}", department)
                    .retrieve()
                    .bodyToFlux(Employee.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
             throw new RuntimeException("Error fetching employees", e);
         }
    }

    public List<Employee> getAllEmployees() {
        try {
            return webClient.get()
                    .uri("/employees")
                    .retrieve()
                    .bodyToFlux(Employee.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to retrieve employees", e);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all employees", e);
        }
    }

}