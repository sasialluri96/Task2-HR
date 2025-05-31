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
@Service
public class HrService {
    @Autowired
    private WebClient webClient;
    private String getUsername(HttpSession session) {
        Object username = session.getAttribute("username");
        if (username == null) throw new RuntimeException("Username not found in session. Please login first.");
        return username.toString();
    }
    private String getPassword(HttpSession session) {
        Object password = session.getAttribute("password");
        if (password == null) throw new RuntimeException("Password not found in session. Please login first.");
        return password.toString();
    }
    public Employee createEmployee(EmployeeDTO employeeDTO, HttpSession session) {
        return webClient.post()
                .uri("/add")
                .headers(headers -> headers.setBasicAuth(getUsername(session), getPassword(session)))
                .bodyValue(employeeDTO)
                .retrieve()
                .bodyToMono(Employee.class)
                .block();
    }

    public Employee updateEmployee(int id, EmployeeDTO employeeDTO, HttpSession session) {
        try {
            return webClient.put()
                    .uri("/update/{id}", id)
                    .headers(headers -> headers.setBasicAuth(getUsername(session), getPassword(session)))
                    .bodyValue(employeeDTO)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new ResourceNotFoundException("Record not present");
        }
    }

    public String deleteEmployee(int id, HttpSession session) {
        try {
            return webClient.delete()
                    .uri("/delete/{id}", id)
                    .headers(headers -> headers.setBasicAuth(getUsername(session), getPassword(session)))
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
                    .uri("/{id}", id)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new ResourceNotFoundException("Record not present");
        }
    }

    public List<Employee> getEmployeesByDepartment(String department) {
        return webClient.get()
                .uri("/department/{department}", department)
                .retrieve()
                .bodyToFlux(Employee.class)
                .collectList()
                .block();
    }

    public List<Employee> getAllEmployees() {
        return webClient.get()
                .uri("/all")
                .retrieve()
                .bodyToFlux(Employee.class)
                .collectList()
                .block();
    }
}
