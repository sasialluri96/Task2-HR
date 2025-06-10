package com.example.TASK2;
import com.example.TASK2.dto.EmployeeDTO;
import com.example.TASK2.entities.Employee;
import com.example.TASK2.exception.ResourceNotFoundException;
import com.example.TASK2.service.HrService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.function.Consumer;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class HrServiceTest {
    @InjectMocks
    private HrService hrService;
    @Mock
    private WebClient webClient;
    @Mock
    private HttpSession session;
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
        session = mock(HttpSession.class);
    }



    @Test
    void testCreateEmployee_Success() {
        when(session.getAttribute("username")).thenReturn("username");
        when(session.getAttribute("password")).thenReturn("password");
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any(Consumer.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Employee.class)).thenReturn(Mono.just(employee));
        Employee createdEmployee = hrService.createEmployee(employeeDTO, session);
        assertNotNull(createdEmployee);
        assertEquals("John", createdEmployee.getName());
    }
    @Test
    void testCreateEmployee_Unauthenticated() {
        when(session.getAttribute("username")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> hrService.createEmployee(employeeDTO, session));
        System.out.println("Exception Message: " + ex.getMessage());
        assertEquals("Please provide credentials", ex.getMessage());
    }
    @Test
    void testCreateEmployee_WebClientException() {
        when(session.getAttribute("username")).thenReturn("username");
        when(session.getAttribute("password")).thenReturn("password");
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any(Consumer.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Employee.class)).thenThrow(WebClientResponseException.class);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> hrService.createEmployee(employeeDTO, session));
        System.out.println("Exception Message: " + ex.getMessage());
        assertEquals("Failed to create employee", ex.getMessage());
    }




    @Test
    void testUpdateEmployee_Success() {
        when(session.getAttribute("username")).thenReturn("username");
        when(session.getAttribute("password")).thenReturn("password");
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), anyInt())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any(Consumer.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Employee.class)).thenReturn(Mono.just(employee));
        Employee updatedEmployee = hrService.updateEmployee(1, employeeDTO, session);
        assertNotNull(updatedEmployee);
        assertEquals("John", updatedEmployee.getName());
    }
    @Test
    void testUpdateEmployee_RecordNotFound() {
        when(session.getAttribute("username")).thenReturn("username");
        when(session.getAttribute("password")).thenReturn("password");
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), anyInt())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any(Consumer.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Employee.class)).thenThrow(WebClientResponseException.NotFound.class);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> hrService.updateEmployee(1, employeeDTO, session));
        System.out.println("Exception Message: " + ex.getMessage());
        assertEquals("Record not present", ex.getMessage());
    }
    @Test
    void testUpdateEmployee_Unauthenticated() {
        when(session.getAttribute("username")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> hrService.updateEmployee(1, employeeDTO, session));
        System.out.println("Exception Message: " + ex.getMessage());
        assertEquals("Please provide credentials", ex.getMessage());
    }




    @Test
    void testDeleteEmployee_Success() {
        when(session.getAttribute("username")).thenReturn("username");
        when(session.getAttribute("password")).thenReturn("password");
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any(Consumer.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Employee deleted successfully"));
        String result = hrService.deleteEmployee(1, session);
        assertNotNull(result);
        assertEquals("Employee deleted successfully", result);
    }
    @Test
    void testDeleteEmployee_RecordNotFound() {
        when(session.getAttribute("username")).thenReturn("username");
        when(session.getAttribute("password")).thenReturn("password");
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any(Consumer.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenThrow(WebClientResponseException.NotFound.class);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> hrService.deleteEmployee(1, session));
        System.out.println("Exception Message: " + ex.getMessage());
        assertEquals("Record not present", ex.getMessage());
    }
    @Test
    void testDeleteEmployee_Unauthenticated() {
        when(session.getAttribute("username")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> hrService.deleteEmployee(1, session));
        System.out.println("Exception Message: " + ex.getMessage());
        assertEquals("Please provide credentials", ex.getMessage());
    }



    @Test
    void testGetEmployeeById_Success() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Employee.class)).thenReturn(Mono.just(employee));
        Employee result = hrService.getEmployeeById(1);
        assertNotNull(result);
        assertEquals("John", result.getName());
    }
    @Test
    void testGetEmployeeById_NotFound() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Employee.class)).thenThrow(new WebClientResponseException(404, "Not Found", null, null, null));
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> hrService.getEmployeeById(1));
        System.out.println("Exception Message: " + ex.getMessage());
        assertEquals("Record not present", ex.getMessage());
    }
    @Test
    void testGetEmployeeById_WebClientException() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        WebClientResponseException exception = WebClientResponseException.create(500, "Internal Server Error", null, null, null);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Employee.class)).thenThrow(exception);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> hrService.getEmployeeById(1));
        assertNotNull(ex);
        System.out.println("Exception Message: " + ex.getMessage());
        assertEquals("Failed to retrieve employee", ex.getMessage());
    }



    @Test
    void testGetEmployeesByDepartment_Success() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(Employee.class)).thenReturn(Flux.just(employee));
        List<Employee> result = hrService.getEmployeesByDepartment("HR");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
    }
    @Test
    void testGetEmployeesByDepartment_WebClientException() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        WebClientResponseException exception = WebClientResponseException.create(500, "Internal Server Error", null, null, null);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(Employee.class)).thenThrow(exception);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> hrService.getEmployeesByDepartment("HR"));
        assertNotNull(ex);
    }
    @Test
    void testGetEmployeesByDepartment_NullResponse() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(Employee.class)).thenReturn(Flux.error(new RuntimeException("Error fetching employees")));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> hrService.getEmployeesByDepartment("HR"));
        assertNotNull(ex);
        assertNotNull(ex.getCause());
        System.out.println("Exception Message: " + ex.getMessage());
        assertEquals("Error fetching employees", ex.getCause().getMessage());
    }




    @Test
    void testGetAllEmployees_Success() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(Employee.class)).thenReturn(Flux.just(employee));
        List<Employee> result = hrService.getAllEmployees();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
    }
    @Test
    void testGetAllEmployees_WebClientException() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        WebClientResponseException exception = WebClientResponseException.create(500, "Failed to retrieve employees due to server error", null, null, null);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(Employee.class)).thenThrow(exception);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> hrService.getAllEmployees());
        assertNotNull(ex);
        assertTrue(ex.getCause() instanceof WebClientResponseException || ((Exception)ex.getCause()).getCause() instanceof WebClientResponseException);
    }
    @Test
    void testGetAllEmployees_NullResponse() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(Employee.class)).thenReturn(Flux.error(new RuntimeException("Error fetching all employees")));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> hrService.getAllEmployees());
        assertNotNull(ex);
        assertNotNull(ex.getCause());
        System.out.println("Exception Message: " + ex.getMessage());
        assertEquals("Error fetching all employees", ex.getCause().getMessage());
    }
}