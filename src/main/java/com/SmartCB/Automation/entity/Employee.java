package com.SmartCB.Automation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "SMARTCB_EMPLOYEE")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String vmyCode;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;

    @PrePersist
    @PreUpdate
    private void normalizeVmyCode() {
        this.vmyCode = this.vmyCode.toLowerCase();
    }
}
