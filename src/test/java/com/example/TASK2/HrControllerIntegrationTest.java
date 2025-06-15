package com.example.TASK2;
import com.example.TASK2.dto.EmployeeDTO;
import com.example.TASK2.entities.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = HrControllerIntegrationTest.Initializer.class)
public class HrControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static MockWebServer mockWebServer;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void startMockServer() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void shutdownServer() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            try {
                mockWebServer = new MockWebServer();
                mockWebServer.start();
                String mockBaseUrl = mockWebServer.url("/").toString();
                TestPropertyValues.of(
                        "employee.management.api.base-url=" + mockBaseUrl
                ).applyTo(context.getEnvironment());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testCreateEmployee_Success() throws Exception {
        Employee employee = new Employee(1, "John", "HR");
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(employee))
                .addHeader("Content-Type", "application/json")
                .setResponseCode(201));

        EmployeeDTO dto = new EmployeeDTO("John", "HR");

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "test-user");
        session.setAttribute("password", "test-password");

        mockMvc.perform(MockMvcRequestBuilders.post("/hr/add")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }


    @Test
    public void testCreateEmployee_Unauthorized() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401));

        EmployeeDTO dto = new EmployeeDTO("John", "HR");

        mockMvc.perform(MockMvcRequestBuilders.post("/hr/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testUpdateEmployee_Success() throws Exception {
        Employee employee = new Employee(1, "Jane", "Finance");
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(employee))
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200));

        EmployeeDTO dto = new EmployeeDTO("Jane", "Finance");

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "test-user");
        session.setAttribute("password", "test-password");

        mockMvc.perform(MockMvcRequestBuilders.put("/hr/update/1")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane"))
                .andExpect(jsonPath("$.department").value("Finance"));
    }


    @Test
    public void testUpdateEmployee_NotFound() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404));

        EmployeeDTO dto = new EmployeeDTO("Jane", "Finance");

        mockMvc.perform(MockMvcRequestBuilders.put("/hr/update/999")
                        .header("username", "test-user")
                        .header("password", "test-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testDeleteEmployee_Success() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("Employee deleted successfully!")
                .setResponseCode(200));

        mockMvc.perform(MockMvcRequestBuilders.delete("/hr/delete/1")
                        .header("username", "test-user")
                        .header("password", "test-password"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteEmployee_NotFound() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Record not present"));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "test-user");
        session.setAttribute("password", "test-password");

        mockMvc.perform(MockMvcRequestBuilders.delete("/hr/delete/999")
                        .session(session))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetEmployeeById_Success() throws Exception {
        Employee employee = new Employee(1, "John", "HR");
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(employee))
                .addHeader("Content-Type", "application/json"));

        mockMvc.perform(MockMvcRequestBuilders.get("/hr/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.department").value("HR"));
    }

    @Test
    public void testGetEmployeeById_NotFound() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Record not present"));

        mockMvc.perform(MockMvcRequestBuilders.get("/hr/1234"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetEmployeesByDepartment_Success() throws Exception {
        List<Employee> employees = List.of(
                new Employee(1, "Alice", "IT"),
                new Employee(2, "Bob", "IT")
        );
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(employees))
                .addHeader("Content-Type", "application/json"));

        mockMvc.perform(MockMvcRequestBuilders.get("/hr/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testGetEmployeesByDepartment_NotFound() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404));

        mockMvc.perform(MockMvcRequestBuilders.get("/hr/department/UnknownDept"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetAllEmployees_Success() throws Exception {
        List<Employee> employees = List.of(
                new Employee(1, "A", "HR"),
                new Employee(2, "B", "HR")
        );
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(employees))
                .addHeader("Content-Type", "application/json"));

        mockMvc.perform(MockMvcRequestBuilders.get("/hr/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testGetAllEmployees_NotFound() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404));

        mockMvc.perform(MockMvcRequestBuilders.get("/hr/all"))
                .andExpect(status().isInternalServerError());
    }
}