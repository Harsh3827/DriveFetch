package Model;

public class CarInfo {
    // Attributes
    private String name;
    private double price;
    private int passengerCapacity;
    private String carGroup;
    private String transmissionType;
    private String carCompany;
    private String link;

    // No-argument constructor (needed by Jackson for deserialization)
    public CarInfo() {
    }

    // Constructor with parameters
    public CarInfo(String name, double price, int passengerCapacity, String carGroup,
                   String transmissionType, String carCompany, String link) {
        this.name = name;
        this.price = price;
        this.passengerCapacity = passengerCapacity;
        this.carGroup = carGroup;
        this.transmissionType = transmissionType;
        this.carCompany = carCompany;
        this.link = link;
    }

    // Getter and setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(int passengerCapacity) {
        this.passengerCapacity = passengerCapacity;
    }

    public String getCarGroup() {
        return carGroup;
    }

    public void setCarGroup(String carGroup) {
        this.carGroup = carGroup;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    public String getCarCompany() {
        return carCompany;
    }

    public void setCarCompany(String carCompany) {
        this.carCompany = carCompany;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    // toString method
    @Override
    public String toString() {
        return "CarInfo [name=" + name + ", price=" + price + ", passengerCapacity=" + passengerCapacity
                + ", carGroup=" + carGroup + ", transmissionType=" + transmissionType + ", carCompany=" + carCompany
                + ", link=" + link + "]";
    }
}
