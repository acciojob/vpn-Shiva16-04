package com.driver.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userName;
    private String password;
    private String originalIp;
    private String maskedIp;
    private boolean connected;
    @ManyToMany
    private List<ServiceProvider> serviceProviderList;
    @OneToMany (mappedBy = "user", cascade = CascadeType.ALL)
    private List<Connection>connectionList;
    @OneToOne
    private Country country;

    public User() {
    }

    public User(int id, String userName, String password, String originalIp, String maskedIp, boolean connected1, List<ServiceProvider> serviceProviderList, List<Connection> connectionList, Country country) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.originalIp = originalIp;
        this.maskedIp = maskedIp;
        connected = connected1;
        this.serviceProviderList = serviceProviderList;
        this.connectionList = connectionList;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOriginalIp() {
        return originalIp;
    }

    public void setOriginalIp(String originalIp) {
        this.originalIp = originalIp;
    }

    public String getMaskedIp() {
        return maskedIp;
    }

    public void setMaskedIp(String maskedIp) {
        this.maskedIp = maskedIp;
    }

    public boolean getConnected() {
        return connected;
    }

    public void setConnected(boolean connected1) {
        connected = connected1;
    }

    public List<ServiceProvider> getServiceProviderList() {
        return serviceProviderList;
    }

    public void setServiceProviderList(List<ServiceProvider> serviceProviderList) {
        this.serviceProviderList = serviceProviderList;
    }

    public List<Connection> getConnectionList() {
        return connectionList;
    }

    public void setConnectionList(List<Connection> connectionList) {
        this.connectionList = connectionList;
    }

    public Country getOriginalCountry() {
        return country;
    }

    public void setOriginalCountry(Country country) {
        this.country = country;
    }
}
