package jp.co.axa.apidemo;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeRepository employeeRepository;

    @Test
    public void testGetEmployees() throws Exception {
        Employee[] employees = {
                new Employee("Foo", 1, "it"),
                new Employee("Bar", 2, "it"),
        };
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employees));
        this.mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name").value(employees[0].getName()))
                .andExpect(jsonPath("$.[1].name").value(employees[1].getName()))
                ;
    }

    @Test
    public void testGetEmployeesEmpty() throws Exception {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());
        this.mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                ;
    }

    @Test
    public void testGetEmployee() throws Exception {
        Employee foo = new Employee("Foo", 1, "it");
        foo.setId(888l);
        when(employeeRepository.findById(1l)).thenReturn(Optional.of(foo));
        this.mockMvc.perform(get("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(foo.getName()))
                .andExpect(jsonPath("$.id").value(foo.getId()))
                ;
    }

    @Test
    public void testGetEmployeeNotExist() throws Exception {
        when(employeeRepository.findById(1l)).thenReturn(Optional.empty());
        this.mockMvc.perform(get("/api/v1/employees/1"))
                .andExpect(status().isNotFound())
                ;
    }

    @Test
    public void testSaveEmployee() throws Exception {
        Employee foo = new Employee("Foo", 1, "it");
        foo.setId(888l);
        when(employeeRepository.save(any(Employee.class))).thenReturn(foo);
        this.mockMvc.perform(post("/api/v1/employees")
                        .content("{\"name\": \"Foo\", \"salary\":1, \"department\":\"it\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(foo.getId()))
                ;
    }
}
