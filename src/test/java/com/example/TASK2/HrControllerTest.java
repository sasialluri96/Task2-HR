package com.example.TASK2;
import com.example.TASK2.controller.HrController;
import com.example.TASK2.dto.EmployeeDTO;
import com.example.TASK2.entities.Employee;
import com.example.TASK2.exception.ResourceNotFoundException;
import com.example.TASK2.service.HrService;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
@ExtendWith(SpringExtension.class)
@WebMvcTest(HrController.class)
public class HrControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private HrService hrService;
    private ObjectMapper objectMapper = new ObjectMapper();
    private EmployeeDTO employeeDTO;
    private Employee employee;
    @BeforeEach
    void setup() {
        employeeDTO = new EmployeeDTO();
        employeeDTO.setName("John");
        employeeDTO.setDepartment("HR");
        employee = new Employee();
        employee.setId(1);
        employee.setName("John");
        employee.setDepartment("HR");
    }


    @Test
    void testCreateEmployee_Success() throws Exception {
        Employee employee = new Employee();
        employee.setName("John");
        employee.setDepartment("HR");
        when(hrService.createEmployee(any(EmployeeDTO.class), any(HttpSession.class))).thenReturn(employee);
        mockMvc.perform(post("/hr/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeDTO))
                .header("username", "test")
                        .header("password", "test"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name").value("John"))
                        .andExpect(jsonPath("$.department").value("HR"));
    }
    @Test
    void testCreateEmployee_Unauthenticated() throws Exception {
        when(hrService.createEmployee(any(EmployeeDTO.class), any(HttpSession.class))).thenThrow(new RuntimeException("Please provide credentials"));
        mockMvc.perform(post("/hr/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
    @Test
    void testCreateEmployee_WebClientException() throws Exception {
        when(hrService.createEmployee(any(EmployeeDTO.class), any(HttpSession.class))).thenThrow(new RuntimeException("Failed to create employee"));
        mockMvc.perform(post("/hr/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO))
                        .header("username", "test")
                        .header("password", "test"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }




    @Test
    void testUpdateEmployee_Success() throws Exception {
        Employee employee = new Employee();
        employee.setName("John");
        employee.setDepartment("HR");
        when(hrService.updateEmployee(anyInt(), any(EmployeeDTO.class), any(HttpSession.class))).thenReturn(employee);
        mockMvc.perform(put("/hr/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.department").value("HR"));
    }
    @Test
    void testUpdateEmployee_RecordNotFound() throws Exception {
        when(hrService.updateEmployee(anyInt(), any(EmployeeDTO.class), any(HttpSession.class))).thenThrow(new ResourceNotFoundException("Record not present"));
        mockMvc.perform(put("/hr/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    void testUpdateEmployee_Unauthenticated() throws Exception {
        when(hrService.updateEmployee(anyInt(), any(EmployeeDTO.class), any(HttpSession.class))).thenThrow(new RuntimeException("Please provide credentials"));
        mockMvc.perform(put("/hr/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }




    @Test
    void testDeleteEmployee_Success() throws Exception {
        when(hrService.deleteEmployee(anyInt(), any(HttpSession.class))).thenReturn("Employee deleted successfully");
        mockMvc.perform(delete("/hr/delete/1")
                        .header("username", "test")
                        .header("password", "test"))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void testDeleteEmployee_RecordNotFound() throws Exception {
        when(hrService.deleteEmployee(anyInt(), any(HttpSession.class))).thenThrow(new ResourceNotFoundException("Record not present"));
        mockMvc.perform(delete("/hr/delete/1")
                        .header("username", "test")
                        .header("password", "test"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    void testDeleteEmployee_Unauthenticated() throws Exception {
        when(hrService.deleteEmployee(anyInt(), any(HttpSession.class))).thenThrow(new RuntimeException("Please provide credentials"));
        mockMvc.perform(delete("/hr/delete/1"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }




    @Test
    void testGetEmployeeById_Success() throws Exception {
        Employee employee = new Employee();
        employee.setName("John");
        employee.setDepartment("HR");
        when(hrService.getEmployeeById(anyInt())).thenReturn(employee);
        mockMvc.perform(get("/hr/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.department").value("HR"));
    }
    @Test
    void testGetEmployeeById_RecordNotFound() throws Exception {
        when(hrService.getEmployeeById(anyInt())).thenThrow(new ResourceNotFoundException("Record not present"));
        mockMvc.perform(get("/hr/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    void testGetEmployeeById_WebClientException() throws Exception {
        when(hrService.getEmployeeById(anyInt())).thenThrow(new RuntimeException("Failed to retrieve employee"));
        MvcResult result = mockMvc.perform(get("/hr/1"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Failed to retrieve employee"));
    }



    @Test
    void testGetEmployeesByDepartment_Success() throws Exception {
        List<Employee> employees = new ArrayList<>();
        Employee employee = new Employee();
        employee.setName("John");
        employee.setDepartment("HR");
        employees.add(employee);
        when(hrService.getEmployeesByDepartment(anyString())).thenReturn(employees);
        mockMvc.perform(get("/hr/department/HR"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    @Test
    void testGetEmployeesByDepartment_WebClientException() throws Exception {
        when(hrService.getEmployeesByDepartment(any())).thenThrow(new RuntimeException());
        mockMvc.perform(get("/hr/department/HR"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
    @Test
    void testGetEmployeesByDepartment_NullResponse() throws Exception {
        when(hrService.getEmployeesByDepartment(any())).thenThrow(new RuntimeException("Error fetching employees"));
        MvcResult result = mockMvc.perform(get("/hr/department/HR"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Error fetching employees"));
    }



    @Test
    void testGetAllEmployees_Success() throws Exception {
        List<Employee> employees = new ArrayList<>();
        employees.add(employee);
        when(hrService.getAllEmployees()).thenReturn(employees);
        mockMvc.perform(MockMvcRequestBuilders.get("/hr/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("John"));
    }
    @Test
    void testGetAllEmployees_WebClientException() throws Exception {
        when(hrService.getAllEmployees()).thenThrow(new RuntimeException());
        mockMvc.perform(get("/hr/all"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
    @Test
    void testGetAllEmployees_NullResponse() throws Exception {
        when(hrService.getAllEmployees()).thenThrow(new RuntimeException("Error fetching all employees"));
        MvcResult result = mockMvc.perform(get("/hr/all"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Error fetching all employees"));
    }
}