package com.example.demo.Login.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;


@Entity
@Table(name = "user_info", uniqueConstraints = @UniqueConstraint(columnNames = {"externalId", "alias"}))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String externalId;

    private String phoneNumber;
    private String email;
    private String postcode;
    private String address;
    private String detailAddress;
    private String extraAddress;
    private String fullAddress;

    @Column(nullable = false)
    private String alias;



    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getExtraAddress() {
        return extraAddress;
    }

    public void setExtraAddress(String extraAddress) {
        this.extraAddress = extraAddress;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    // 전체 주소 가공
    public void constructFullAddress() {
        if (detailAddress != null && !detailAddress.isEmpty()) {
            this.fullAddress = address + " " + detailAddress;
        } else {
            this.fullAddress = address;
        }
    }
}