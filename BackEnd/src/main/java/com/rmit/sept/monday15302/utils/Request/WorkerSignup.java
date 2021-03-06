package com.rmit.sept.monday15302.utils.Request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class WorkerSignup {

    @NotBlank(message = "User name is required")
    @Size(min = 3, max = 21)
    private String userName;

    @NotBlank(message = "Password is required")
    @Size(min = 6)
    private String password;

    @NotBlank(message = "Worker's first name is required")
    private String fName;

    @NotBlank(message = "Worker's last name is required")
    private String lName;

    @NotBlank(message = "Admin Id is required")
    private String adminId;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp="(^$|[0-9]{10})")
    private String phoneNumber;

    public WorkerSignup(String username, String password, String fName, String lName,
                String adminId, String phoneNumber) {
        this.userName = username;
        this.password = password;
        this.fName = fName;
        this.lName = lName;
        this.adminId = adminId;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return userName;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
