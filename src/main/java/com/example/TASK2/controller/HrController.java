package com.example.TASK2.controller;
import com.example.TASK2.dto.EmployeeDTO;
import com.example.TASK2.entities.Employee;
import com.example.TASK2.service.HrService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/hr")
public class HrController {
    @Autowired
    private HrService hrService;
    @PostMapping("/login")
    public String login(@RequestHeader("username") String username,
                        @RequestHeader("password") String password,
                        HttpSession session) {
        session.setAttribute("username", username);
        session.setAttribute("password", password);
        return "Login successful. Credentials stored in session.";
    }

    @PostMapping("/add")
    public Employee createEmployee(@RequestBody @Valid EmployeeDTO employeeDTO, HttpSession session) {
        return hrService.createEmployee(employeeDTO, session);
    }

    @PutMapping("/update/{id}")
    public Employee updateEmployee(@PathVariable int id,
                                   @RequestBody @Valid EmployeeDTO employeeDTO,
                                   HttpSession session) {
        return hrService.updateEmployee(id, employeeDTO, session);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable int id, HttpSession session) {
        return hrService.deleteEmployee(id, session);
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable int id) {
        return hrService.getEmployeeById(id);
    }

    @GetMapping("/department/{department}")
    public List<Employee> getEmployeesByDepartment(@PathVariable String department) {
        return hrService.getEmployeesByDepartment(department);
    }

    @GetMapping("/all")
    public List<Employee> getAllEmployees() {
        return hrService.getAllEmployees();
    }
}
