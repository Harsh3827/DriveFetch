package Model;

// Model/entity for storing car information
public class CarInfo {
    // Attributes
    private String name;
    private double price;
    private int passengerCapacity;
    private String carGroup;
    private String transmissionType;
    private int largeBag;
    private int smallBag;
    private String rentalCompany;

    // Constructor
    public CarInfo(String name, double price, int passengerCapacity, String carGroup,
                   String transmissionType, int largeBag, int smallBag, String rentalCompany) {
        this.name = name;
        this.price = price;
        this.passengerCapacity = passengerCapacity;
        this.carGroup = carGroup;
        this.transmissionType = transmissionType;
        this.largeBag = largeBag;
        this.smallBag = smallBag;
        this.rentalCompany = rentalCompany;
    }

    // Getter and setter methods
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    public String getCarGroup() {
        return carGroup;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    public int getLargeBag() {
        return largeBag;
    }

    public int getSmallBag() {
        return smallBag;
    }

    public String getRentalCompany() {
        return rentalCompany;
    }

    public void setRentalCompany(String rentalCompany) {
        this.rentalCompany = rentalCompany;
    }

    // toString method
    @Override
    public String toString() {
        return "CarInfo [name=" + name + ", price=" + price + ", passengerCapacity=" + passengerCapacity
                + ", carGroup=" + carGroup + ", transmissionType=" + transmissionType + ", largeBag=" + largeBag
                + ", smallBag=" + smallBag + ", rentalCompany=" + rentalCompany + "]";
    }
}
