package jp.co.axa.apidemo.controllers;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        List<Employee> employees = employeeService.retrieveEmployees();
        return employees;
    }

    @GetMapping("/employees/{employeeId}")
    public Employee getEmployee(@PathVariable(name="employeeId")Long employeeId) {
        return employeeService.getEmployee(employeeId);
    }

    @PostMapping("/employees")
    public Employee saveEmployee(@Valid @RequestBody Employee employee, BindingResult bindingResult) throws BindException {
        if (employee.getId() != null) {
            bindingResult.rejectValue("id", "MustBeEmpty");
        }
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return employeeService.saveEmployee(employee);
    }

    @DeleteMapping("/employees/{employeeId}")
    public void deleteEmployee(@PathVariable(name="employeeId")Long employeeId){
        employeeService.deleteEmployee(employeeId);
        System.out.println("Employee Deleted Successfully");
    }

    @PutMapping("/employees/{employeeId}")
    public void updateEmployee(@RequestBody @Valid Employee employee,
                               BindingResult bindingResult,
                               @PathVariable(name="employeeId")Long employeeId) throws BindException {
        ValidationUtils.rejectIfEmpty(bindingResult, "id", "MustNotNull");
        if (employee.getId() != null && !employee.getId().equals(employeeId)) {
            bindingResult.rejectValue("id", "MustBeConsistent");
        }
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        if (employeeService.existsById(employeeId)) {
            employeeService.updateEmployee(employee);
        }
    }

}
