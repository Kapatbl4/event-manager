package vv.dev.event_manager.location.model;

public class EventLocation {

    private Long id;

    private String name;

    private String address;

    private Integer capacity;

    private String description;

    public EventLocation(String name, String address, Integer capacity, String description) {
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.description = description;
    }

    public EventLocation(Long id, String name, String address, Integer capacity, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.description = description;
    }

    public EventLocation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
