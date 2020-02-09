/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Model;

/**
 *
 * @author Capsi
 */
public class Customer {
    public Customer(int id, String _name, 
            Address _address, boolean _active, String _phone)
    {
        customerId = id;
        name = _name;
        address = _address;
        active = _active;
        phone = _phone;
        addressSquash = address.address + " " + address.address2
                + ", " + address.city + " " + address.postalCode
                + " " + address.country;
    }
    
    private int customerId;
    private String name;
    private Address address;
    private boolean active;
    private String phone;
    private String addressSquash;

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressSquash() {
        return addressSquash;
    }

    public void setAddressSquash(String addressSquash) {
        this.addressSquash = addressSquash;
    }
}
