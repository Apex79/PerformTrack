package com.performtrack.employee;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "employees",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_employee_email",
                columnNames = "email"
        )
)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 150)
    private String email;

    private String department;

    private String designation;

    private LocalDate joiningDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee reportingManager;

    @Column(nullable = false)
    private boolean active = true;

    public Employee() {
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public Employee getReportingManager() {
        return reportingManager;
    }

    public void setReportingManager(Employee reportingManager) {
        this.reportingManager = reportingManager;
    }

    // Convenience method to retrieve the reporting manager's ID
    public Long getManagerId() {
        return reportingManager == null
                ? null
                : reportingManager.getId();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
