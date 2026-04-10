package com.example.charcuteria.dto.address;

public class AddressDtoRequest {

    private Integer user_id;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String zip_code;

    public void setUserId(Integer user_id) { this.user_id = user_id; }
    public void setStreet(String street) { this.street = street; }
    public void setNumber(String number) { this.number = number; }
    public void setComplement(String complement) { this.complement = complement; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setZipCode(String zip_code) { this.zip_code = zip_code; }
    
    public Integer getUserId() { return this.user_id; }
    public String getStreet() { return this.street; }
    public String getNumber() { return this.number; }
    public String getComplement() { return this.complement; }
    public String getNeighborhood() { return this.neighborhood; }
    public String getCity() { return this.city; }
    public String getState() { return this.state; }
    public String getZipCode() { return this.zip_code; }
}
