package com.example.TASK2;
import com.example.TASK2.dto.EmployeeDTO;
import com.example.TASK2.entities.Employee;
import com.example.TASK2.exception.ResourceNotFoundException;
import com.example.TASK2.service.HrService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ContextConfiguration(initializers = HrServiceIntegrationTest.Initializer.class)
public class HrServiceIntegrationTest {
    @Autowired
    private HrService hrService;

    private static MockWebServer mockWebServer;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void startMockServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }
    @AfterAll
    static void shutdownServer() throws IOException {
        mockWebServer.shutdown();
    }

   
    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext context) {
            String mockBaseUrl = mockWebServer.url("/employees/").toString();
            TestPropertyValues.of(
                    "employee.management.api.base-url=" + mockBaseUrl
            ).applyTo(context.getEnvironment());
        }
    }

    
    @Test
    void testCreateEmployee_Success() throws Exception {
        Employee employee = new Employee(1, "John", "HR");
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(employee))
                .addHeader("Content-Type", "application/json")
                .setResponseCode(201));
        EmployeeDTO dto = new EmployeeDTO("John", "HR");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "test-user");
        session.setAttribute("password", "test-password");
        Employee response = hrService.createEmployee(dto, session);
        System.out.println("Response: " + objectMapper.writeValueAsString(response));
        assertNotNull(response);
        assertEquals("John", response.getName());
        assertEquals("HR", response.getDepartment());
    }
    @Test
    void testCreateEmployee_Duplicate() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("Employee already exists")
                .addHeader("Content-Type", "text/plain"));
        EmployeeDTO dto = new EmployeeDTO("John", "HR");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "test-user");
        session.setAttribute("password", "test-password");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            hrService.createEmployee(dto, session);
        });
        assertTrue(ex.getMessage().contains("Failed to create employee"));
    }

    
    @Test
    void testUpdateEmployee_Success() throws Exception {
        Employee updated = new Employee(1, "Jane", "Finance");
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(updated))
                .addHeader("Content-Type", "application/json"));
        EmployeeDTO dto = new EmployeeDTO("Jane", "Finance");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "test-user");
        session.setAttribute("password", "test-password");
        Employee result = hrService.updateEmployee(1, dto, session);
        assertNotNull(result);
        assertEquals("Jane", result.getName());
        assertEquals("Finance", result.getDepartment());
    }
    @Test
    void testUpdateEmployee_NotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Record not present"));
        EmployeeDTO dto = new EmployeeDTO("Jane", "Finance");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "test-user");
        session.setAttribute("password", "test-password");
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            hrService.updateEmployee(999, dto, session);
        });
        assertTrue(ex.getMessage().contains("Record not present"));
    }


    
    @Test
    void testDeleteEmployee_Success() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("Employee deleted successfully!")
                .setResponseCode(200));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "test-user");
        session.setAttribute("password", "test-password");
        String response = hrService.deleteEmployee(1, session);
        assertEquals("Employee deleted successfully!", response);
    }
    @Test
    void testDeleteEmployee_NotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Record not present"));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "test-user");
        session.setAttribute("password", "test-password");
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            hrService.deleteEmployee(999, session);
        });
        assertTrue(ex.getMessage().contains("Record not present"));
    }


    
    @Test
    void testGetEmployeeById_Success() throws Exception {
        Employee employee = new Employee(1, "John", "HR");
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(employee))
                .addHeader("Content-Type", "application/json"));
        Employee response = hrService.getEmployeeById(1);
        assertNotNull(response);
        assertEquals("John", response.getName());
        assertEquals("HR", response.getDepartment());
    }
    @Test
    void testGetEmployeeById_NotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Record not present"));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            hrService.getEmployeeById(1234);
        });
        assertTrue(ex.getMessage().contains("Record not present"));
    }

    
    @Test
    void testGetEmployeesByDepartment_Success() throws Exception {
        List<Employee> employees = List.of(
                new Employee(1, "Alice", "IT"),
                new Employee(2, "Bob", "IT")
        );
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(employees))
                .addHeader("Content-Type", "application/json"));
        List<Employee> result = hrService.getEmployeesByDepartment("IT");
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getName());
    }
    @Test
    void testGetEmployeesByDepartment_NotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("No employees found"));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            hrService.getEmployeesByDepartment("UnknownDept");
        });
        assertTrue(ex.getMessage().contains("Error fetching employees"));
    }


    @Test
    void testGetAllEmployees_Success() throws Exception {
        List<Employee> employees = List.of(
                new Employee(1, "A", "HR"),
                new Employee(2, "B", "HR")
        );
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(employees))
                .addHeader("Content-Type", "application/json"));
        List<Employee> result = hrService.getAllEmployees();
        assertEquals(2, result.size());
    }
    @Test
    void testGetAllEmployees_NotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("No employees found"));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            hrService.getAllEmployees();
        });
        assertTrue(ex.getMessage().contains("Failed to retrieve employees"));
    }
}
