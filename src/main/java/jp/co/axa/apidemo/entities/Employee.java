package jp.co.axa.apidemo.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@ToString
@Entity
@Table(name="EMPLOYEE")
public class Employee {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(name="EMPLOYEE_NAME")
    @NotEmpty
    private String name;

    @Getter
    @Setter
    @Column(name="EMPLOYEE_SALARY")
    @Min(0)
    private Integer salary;

    @Getter
    @Setter
    @Column(name="DEPARTMENT")
    @NotEmpty
    private String department;

    public Employee() {
    }

    public Employee(String name, int salary, String department) {
        this.name = name;
        this.salary = salary;
        this.department = department;
    }

}
