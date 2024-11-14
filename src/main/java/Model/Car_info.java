package Model;

// model or entity for storing data
public class Car_info {
	// attributes
    private String namee;
    private double pricee;
    private int passenger_Capacityy;
    private String ccar_Group;
    private String ttransmission_Type;
    private int largee_Bag;
    private int ssmall_Bag;
    private String rental_Company;
 // constructor
    public Car_info(String name, double price, int passenger_Capacity, String car_Group, String transmission_Type, int large_Bag, int small_Bag, String rental_Company) {
        this.namee = name;
        this.pricee = price;
        this.passenger_Capacityy = passenger_Capacity;
        this.ccar_Group = car_Group;
        this.ttransmission_Type = transmission_Type;
        this.largee_Bag = large_Bag;
        this.ssmall_Bag = small_Bag;
        this.rental_Company = rental_Company;
    }
// getters methods
    public String getRentalCompany() {
        return rental_Company;
    }
// setter method for rentalcompnay
    public void setRentalCompany(String rentalCompany) {
        this.rental_Company = rentalCompany;
    }

    public String getName() {
        return namee;
    }

    public double getPrice() {
        return pricee;
    }

    public int getPassengerCapacity() {
        return passenger_Capacityy;
    }

    public String getTransmissionType() {
        return ttransmission_Type;
    }

    public String getCarGroup() {
        return ccar_Group;
    }

    public int getLargeBag() {
        return largee_Bag;
    }

    public int getSmallBag() {
        return ssmall_Bag;
    }
    
// toString methods
	@Override
	public String toString() {
		return "Car_Info [name=" + namee + ", price=" + pricee + ", passenger_Capacity=" + passenger_Capacityy
				+ ", car_Group=" + ccar_Group + ", transmission_Type=" + ttransmission_Type + ", large_Bag=" + largee_Bag
				+ ", small_Bag=" + ssmall_Bag + ", rental_Company=" + rental_Company + "]";
	}
    
}
