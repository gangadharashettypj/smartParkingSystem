package trial.example.com.smart_parking_system;

public class rowCars {
    public String mobile, name, vehicleno, url, slot;


    public rowCars(){

    }

    public rowCars(String mobile, String name, String vehicleno, String url, String slot) {
        this.mobile = mobile;
        this.name = name;
        this.vehicleno = vehicleno;
        this.url = url;
    }
    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVehicleno() {
        return vehicleno;
    }

    public void setVehicleno(String vehicleno) {
        this.vehicleno = vehicleno;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
