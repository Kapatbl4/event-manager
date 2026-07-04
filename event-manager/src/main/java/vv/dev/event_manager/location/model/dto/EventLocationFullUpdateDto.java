package vv.dev.event_manager.location.model.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EventLocationFullUpdateDto {
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @NotBlank(message = "Address cannot be empty")
    private String address;

    @NotNull
    @Min(value = 5, message = "Capacity must be greater than 4")
    private Integer capacity;

    private String description;

    public EventLocationFullUpdateDto(String name, String address, Integer capacity, String description) {
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.description = description;
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
