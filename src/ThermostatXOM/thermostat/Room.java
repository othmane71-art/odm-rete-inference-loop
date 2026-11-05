package thermostat;

public class Room {
    private String name;
    private double temperature;
    private double targetTemperature;
    private boolean heatingOn;
    private int numberOfCycles;

    public Room(String name, double temperature, double target) {
        this.setName(name);
        this.setTemperature(temperature);
        this.setHeatingOn(false);
        this.setNumberOfCycles(0);
        this.setTargetTemperature(target);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getTargetTemperature() { return targetTemperature; }
    public void setTargetTemperature(double targetTemperature) { this.targetTemperature = targetTemperature; }

    public boolean isHeatingOn() { return heatingOn; }
    public void setHeatingOn(boolean heatingOn) { this.heatingOn = heatingOn; }

    public int getNumberOfCycles() { return numberOfCycles; }
    public void setNumberOfCycles(int numberOfCycles) { this.numberOfCycles = numberOfCycles; }
}
