package main.java;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.net.URL;


/**
 * Creates a model for the representation of a car.
 */
public class Car extends Rectangle {

    /**
     * Components of a car. Contribute to time
     * TODO: Create relationships between these components and calculation of time.
     */
    private int engine, suspension, boost, weight;

    /** TODO: Turbo boost?? */
    private boolean isBoosted;

    /** The total time the car has driven for. Proportional to the distance traveled and components. */
    private double time;

    /** Reference to the turn order and identification. TODO: Associate a car to a users name ?? */
    private int id;

    /**
     * Creates a car.
     * @param x The horizontal location
     * @param y The vertical location
     * @param offset The sizing relative to locations sizing
     * @param id The identifier
     */
    public Car(double x, double y, double offset, int id) {
        super(x, y, offset, offset);
        this.id = id;
        String[] names = { "bug", "blue", "black", "yellow","orange"};
        URL resource = getClass().getResource("/main/resources/images/" + names[id % names.length] + ".png");
        this.setFill(new ImagePattern(new Image(resource.toString())));
    }

    /**
     * @return The current time elapsed.
     */
    public double getTime() { return time; }

    /**
     * @return The car's identifier
     */
    public int getIdentifier() {
        return id;
    }

    /*
     * Moves a car from one location to another. Updates the time.
     * @param location The location to move to.
     */
    public void newLocation(double x, double y, double distanceIn) {
        this.setX(x);
        this.setY(y);
        // TODO: Here is where the components like engine could be calculated
        time += (distanceIn) * 0.32;
    }

    /**
     * @return The textual representation of a car.
     */
    @Override
    public String toString() {
        return "Car #:" + id + "\tTime traveled: " + time;
    }

}