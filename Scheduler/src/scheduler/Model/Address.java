package scheduler.Model;

public class Address {
    public Address(int id, String _addr, String _addr2, 
            String _city, String _country, String _pcode, String _phone)
    {
        addressId = id;
        address = _addr;
        address2 = _addr2;
        city = _city;
        country = _country;
        postalCode = _pcode;
        phone = _phone;
    }
    
    public int addressId;
    public String address;
    public String address2;
    public String city;    //This should be a reference to a City object later.
    public String country;
    public String postalCode;
    public String phone;
    
    
}
