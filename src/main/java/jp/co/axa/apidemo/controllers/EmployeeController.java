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
import java.util.NoSuchElementException;

/**
 * A controller provides REST service relating to {@link Employee}.
 * <p><ul>Following methods are supported:
 *   <li>/employees GET</li>
 *   <li>/employees/{id} GET</li>
 *   <li>/employees POST</li>
 *   <li>/employees/{id} DELETE</li>
 *   <li>/employees/{id} PUT</li>
 * </ul></p>
 */
@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * Get a list of employees available in the system.
     * @return List
     */
    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        List<Employee> employees = employeeService.retrieveEmployees();
        return employees;
    }

    /**
     * Get an employee which id is specified from URI path.
     * <p>If the specified employee not exist, an error will be responded.</p>
     * @param employeeId
     * @return Employee
     */
    @GetMapping("/employees/{employeeId}")
    public Employee getEmployee(@PathVariable(name="employeeId")Long employeeId) {
        return employeeService.getEmployee(employeeId);
    }

    /**
     * Create a new employee with information specified from client.
     * Before creation following validations are performed,  if failed detail message as response is returned.
     * <p>employee.id is empty and
     * <p>constraints declared within {@link Employee} definition.
     * @param employee
     * @param bindingResult
     * @return Employee
     * @throws BindException
     */
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

    /**
     * Delete an employee which id is specified from URI path.
     * <p>If the specified employee not exist, it will respond with an error.</p>
     * @param employeeId
     */
    @DeleteMapping("/employees/{employeeId}")
    public void deleteEmployee(@PathVariable(name="employeeId")Long employeeId){
        if (!employeeService.existsById(employeeId)) {
            throw new NoSuchElementException(String.format("The employee{id=%d} does not exist.", employeeId));
        }
        employeeService.deleteEmployee(employeeId);
    }

    /**
     * Update an employee which id as one segment is specified from URI path.
     * <p>Besides constraints declared within {@link Employee}, it also is not valid if the id included in the request body
     * differs the id in URI path.</p>
     * @param employee
     * @param bindingResult
     * @param employeeId
     * @throws BindException
     */
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
