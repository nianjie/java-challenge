package jp.co.axa.apidemo;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import org.hamcrest.CoreMatchers;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                .andExpect(jsonPath("$.message").value(CoreMatchers.containsString("does not exist")))
                ;
    }

    @Test
    public void testSaveEmployeeSuccess() throws Exception {
        Employee foo = new Employee("Foo", 1, "it");
        foo.setId(888l);
        when(employeeRepository.save(any(Employee.class))).thenReturn(foo);
        this.mockMvc.perform(post("/api/v1/employees")
                        .content("{\"name\": \"Foo\", \"salary\":1, \"department\":\"it\"}")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(foo.getId()))
                ;
        verify(employeeRepository).save(any());
    }

    @Test
    public void testSaveEmployeeWithSpecifiedId() throws Exception {
        this.mockMvc.perform(post("/api/v1/employees")
                        .content("{\"name\": \"Foo\", \"salary\":1, \"department\":\"it\", \"id\": 123}")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation error(s)"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.[0]").value(CoreMatchers.containsString("must not be specified")))
                ;
        verify(employeeRepository, never()).save(any());
    }

    @Test
    public void testSaveEmployeeWithJavaBeanValidationBroken() throws Exception {
        this.mockMvc.perform(post("/api/v1/employees")
                        .content("{\"name\": \"\", \"salary\":-1, \"department\":\"\"}")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").value(CoreMatchers.hasItem(CoreMatchers.endsWith("must not be empty"))))
                .andExpect(jsonPath("$.errors").value(CoreMatchers.hasItem(CoreMatchers.endsWith("must be greater than or equal to 0"))))
                ;
        verify(employeeRepository, never()).save(any());
    }

    @Test
    public void testDeleteEmployee() throws Exception {
        when(employeeRepository.existsById(1l)).thenReturn(true);
        this.mockMvc.perform(delete("/api/v1/employees/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                ;
        verify(employeeRepository).deleteById(1l);
    }

    @Test
    public void testDeleteEmployeeNotExist() throws Exception {
        this.mockMvc.perform(delete("/api/v1/employees/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(CoreMatchers.containsString("does not exist")))
        ;
        verify(employeeRepository, never()).deleteById(any());
    }

    @Test
    public void testUpdateEmployeeSuccess() throws Exception {
        Employee foo = new Employee("Foo", 1, "it");
        foo.setId(888l);
        when(employeeRepository.existsById(foo.getId())).thenReturn(true);
        this.mockMvc.perform(put("/api/v1/employees/888")
                        .content("{\"id\":888, \"name\": \"Bar\", \"salary\":1000, \"department\":\"hr\"}")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                ;
        foo.setDepartment("hr");
        foo.setName("Bar");
        foo.setSalary(1000);
        verify(employeeRepository).save(foo);
    }

    @Test
    public void testUpdateEmployeeWithNullId() throws Exception {
        Employee foo = new Employee("Foo", 1, "it");
        foo.setId(888l);
        when(employeeRepository.existsById(foo.getId())).thenReturn(true);
        this.mockMvc.perform(put("/api/v1/employees/888")
                        .content("{\"id\":null, \"name\": \"Bar\", \"salary\":1000, \"department\":\"hr\"}")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0]").value(CoreMatchers.containsString("must not be null")))
        ;
        verify(employeeRepository, never()).save(any());
    }

    @Test
    public void testUpdateEmployeeWithInconsistentIds() throws Exception {
        this.mockMvc.perform(put("/api/v1/employees/888")
                        .content("{\"id\":777, \"name\": \"Bar\", \"salary\":1000, \"department\":\"hr\"}")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0]").value(CoreMatchers.containsString("must be consistent")))
                ;
        verify(employeeRepository, never()).save(any());
    }

    @Test
    public void testUpdateEmployeeWithJavaBeanValidationBroken() throws Exception {
        this.mockMvc.perform(put("/api/v1/employees/888")
                        .content("{\"id\":888, \"name\": \"\", \"salary\":-1000, \"department\":\"\"}")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(CoreMatchers.hasItem(CoreMatchers.containsString("must not be empty"))))
                .andExpect(jsonPath("$.errors").value(CoreMatchers.hasItem(CoreMatchers.containsString("must be greater than or equal to 0"))))
        ;
        verify(employeeRepository, never()).save(any());
    }
}
