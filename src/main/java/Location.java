package main.java;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.net.URL;

/**
 * Creates a destination for use within the track.
 */
public class Location extends Circle {

    /**
     * The name identifier of the location
     */
    private String name;

    /**
     * Keeps track within the running loop of each turn whether
     * a location has been clicked
     * a location can be clicked (active)
     */
    private boolean clicked, active;

    /**
     * @param x      The x coordinate of the location
     * @param y      The y coordinate of the location
     * @param offset The width and height of the location
     * @param name   THe name identifier of the location
     */
    public Location(double x, double y, double offset, String name) { //, Map<Location, Integer> neighbors) {
        super(x, y, offset);
        this.name = name;
        this.setFill(Color.LIMEGREEN);
        clicked = false;
        active = true;
    }

    /**
     * @return the name of the location
     */
    public String getName() {
        return name;
    }

    /**
     * @return True if the location can be selected, otherwise false
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @return True if the location has been clicked, otherwise false
     */
    public boolean getClicked() {
        return clicked;
    }

    /**
     * @param in The value for "clicked" to be changed to.
     */
    public void setClicked(boolean in) {
        clicked = in;
    }

    /**
     * @param in     The value for "active" to be changed to.
     * @param orange True if the location to be set is the end location for a car.
     */
    public void setActive(boolean in, boolean orange) {
        active = in;
        if (orange) this.setFill(Color.ORANGE); // TODO: Find image for this representation ?
        else {
            if (!active) this.setFill(Color.CRIMSON);
            else this.setFill(Color.LIMEGREEN);
        }
    }

    /**
     * Indicates the last location.
     */
    public void setLastColor() {
        URL green = getClass().getResource("/main/resources/images/greengif.gif");
        this.setFill(new ImagePattern(new Image(green.toString())));
    }

    /**
     * Computes the distance on demand from "this" location to,
     *
     * @param location The location to compute distance to
     * @return the hypotenuse of the difference of x and y values.
     */
    public double getDistanceToLocation(Location location) {
        double tmpX = location.getCenterX();
        double tmpY = location.getCenterY();
        double xThis = this.getCenterX();
        double yThis = this.getCenterY();
        double a = (tmpY - yThis) > 0 ? tmpY - yThis : yThis - tmpY;
        double b = (tmpX - xThis) > 0 ? tmpX - xThis : xThis - tmpX;
        return Math.hypot(a, b) / 100;
    }

    /**
     * @return The string representation of the car.
     */
    public String toString() {
        return name + "\tActive: " + active + "\tClicked: " + clicked;
    }

}