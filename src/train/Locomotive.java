package train;

public class Locomotive extends Vehicle {
    
    public double maxTractiveEffort;

    public Locomotive (double length, double distanceToFrontWheel, double distanceToBackWheel, double weight, double maxTractiveEffort) {
        super(length, distanceToFrontWheel, distanceToBackWheel, weight);
        this.maxTractiveEffort = maxTractiveEffort;
    }
}
