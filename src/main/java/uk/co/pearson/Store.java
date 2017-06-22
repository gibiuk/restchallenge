package uk.co.pearson;

public class Store {
    private String id;
    private String postCode;
    private String city;
    private String address;
    private String openDate;
    private String daysSinceOpen;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public String getDaysSinceOpen() {
        return daysSinceOpen;
    }

    public void setDaysSinceOpen(String daysSinceOpen) {
        this.daysSinceOpen = daysSinceOpen;
    }
}
