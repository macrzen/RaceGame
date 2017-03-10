package main.java;

import javafx.animation.PathTransition;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The track is the layer where the turn based sequence is controlled.
 * A group of graphical components, together creates the game aspect of the program
 */
public class Track extends Group {

    /**
     * A reference to all of the locations
     */
    private ArrayList<Location> locations;

    /**
     * Reference to all of the cars (proportional to the number of players)
     */
    private ArrayList<Car> cars;

    /**
     * Keeps track of which cars turn it is.
     */
    private Car activeCar;

    /**
     * gridPane contains a grid-pane of cars, and one of locations
     * Contains the information about the active car's current location,
     * the distances from the active car's current location,
     * as well as the time that each player has driven.
     */
    private GridPane gridPane, gpCars, gpLocations;

    /**
     * Reference to a car's start location.
     */
    private Map<Car, Location> carStartLocation;

    /**
     * Reference to a car's end location.
     */
    private Map<Car, Location> carEndLocation;

    /**
     * Reference to a car's current location;
     */
    private Map<Car, Location> carCurrentLocation;


    /**
     * Reference to the locations a car has visited.
     */
    private Map<Car, ArrayList<Location>> carVisitedLocations;


    /**
     * Initializes the collections for locations and cars.
     */
    public Track() {
        locations = new ArrayList<>();
        cars = new ArrayList<>();
        carVisitedLocations = new HashMap<>();
        carStartLocation = new HashMap<>();
        carEndLocation = new HashMap<>();
        carCurrentLocation = new HashMap<>();
    }

    /**
     * The way that the cars and locations are initialized.
     *
     * @param numPlayers The number of locations corresponds to combo-box selection from StartPrompt in View.
     * @param sceneX     The width of the main Screen from View.
     * @param sceneY     The height of the main Screen from View.
     */
    public void setTrack(int numPlayers, double offset, double sceneX, double sceneY) {
        Random rand = new Random();
        // for the number of players, number of locations changes, the addition of 2 or 3 extra locations is arbitrary
        int numLocation = (numPlayers & 1) == 1 ? numPlayers + 2 : numPlayers + 3;
        createLocations(numLocation, offset, sceneX, sceneY, rand);
        createCars(numPlayers, offset, rand);
        activeCar = cars.get(0);
        activeCar.setVisible(true);
        carStartLocation.get(activeCar).setActive(false, false);
        for (Location location : locations)
            if (carEndLocation.get(activeCar).equals(location))
                location.setActive(false, true);
        setGridPane(sceneX, sceneY);
    }

    /**
     * Creates the locations in relation to how many players selected. Called in setTrack, separated for clarity.
     *
     * @param numLocation Number of locations.
     * @param offset      Relative sizing.
     * @param sceneX      Scene's width.
     * @param sceneY      Scene's Height.
     * @param rand        Random number generator.
     */
    private void createLocations(int numLocation, double offset, double sceneX, double sceneY, Random rand) {
        for (int k = 0; k < numLocation; k++) {
            int tmpOffSetX = (int) sceneX - ((int) offset * 2);
            int tmpOffsetY = (int) sceneY - ((int) offset * 2);
            double x = rand.nextInt(tmpOffSetX - 300);
            double y = rand.nextInt(tmpOffsetY);
            for (Location lo : locations) {
                if (lo.intersects(new BoundingBox(x, y, offset * 2, offset * 2))) {
                    x = rand.nextInt(tmpOffSetX - 300);
                    y = rand.nextInt(tmpOffsetY);
                }
            }
            if (x < offset) x += (offset + 10); // Attempts to buffer location from being placed off-screen.
            if (y < offset) y += (offset + 10);
            if (y > sceneY - offset) y -= (offset + 10);
            if (x > sceneX - offset) x -= (offset + 10);
            locations.add(new Location(x, y, offset, "Location " + k));            // adds new locations
            locations.get(k).setOnMouseClicked(locationEvent);

        }
        for (int i = 0; i < locations.size(); i++) {
            Location toAdd = locations.get(i);
            this.getChildren().add(toAdd);
            this.getChildren().add(new Text(toAdd.getCenterX(), toAdd.getCenterY(), i + ""));
        }
    }

    /**
     * Creates the cars. Called in setTrack, separated for clarity.
     *
     * @param numPlayers The number of cars to be created in relation to the number of players
     * @param offset     The sizing offset.
     * @param rand       Random number generator.
     */
    private void createCars(int numPlayers, double offset, Random rand) {
        ArrayList<Location> forStart = new ArrayList<>();
        ArrayList<Location> forEnd = new ArrayList<>();
        for (int j = 0; j < numPlayers; j++) {
            Location start = locations.get(rand.nextInt(locations.size()));
            Location end = locations.get(rand.nextInt(locations.size()));
            while (forStart.contains(start)) start = locations.get(rand.nextInt(locations.size()));
            while (forEnd.contains(end) || end.equals(start)) end = locations.get(rand.nextInt(locations.size()));
            forStart.add(start);
            forEnd.add(end);
            Car tempCar = new Car(start.getCenterX() - offset, start.getCenterY() - offset, offset + 10, j);
            carVisitedLocations.put(tempCar, new ArrayList<>());
            cars.add(tempCar);
            carStartLocation.put(tempCar, start);
            carEndLocation.put(tempCar, end);
            carCurrentLocation.put(tempCar, start);
            cars.get(j).setVisible(false);
            carVisitedLocations.get(tempCar).add(start);
            this.getChildren().add(cars.get(j));
        }


    }

    /**
     * Creates the grid-pane for the game stats.
     *
     * @param sceneX Reference to the scene's width. Needed for alignment.
     * @param sceneY Reference to the scene's height.
     */
    private void setGridPane(double sceneX, double sceneY) {
        gridPane = new GridPane();
        gridPane.setMinSize(200, sceneY);
        gridPane.setMaxSize(200, sceneY);
        gridPane.setAlignment(Pos.CENTER);
        gpLocations = new GridPane();
        gpLocations.setMaxSize(200, sceneY / 3);
        gpLocations.add(new Text("Location"), 0, 0);
        gpLocations.add(new Text("\tDistance (km)"), 1, 0);

        for (int q = 0; q < locations.size(); q++) {
            gpLocations.add(new Text(locations.get(q).getName()), 0, q + 1);
            String dist = String.format("%.1f", carCurrentLocation.get(activeCar).getDistanceToLocation(locations.get(q)));
            gpLocations.add(new Text("\t\t" + dist), 1, q + 1);
        }

        gpCars = new GridPane();
        gpCars.setMaxSize(200, sceneY / 3);
        gpCars.add(new Text("Car"), 0, 0);
        gpCars.add(new Text("\tTime (hr)"), 1, 0);

        for (int nums = 0; nums < cars.size(); nums++) {
            gpCars.add(new Text(Integer.toString(cars.get(nums).getIdentifier())), 0, nums + 1);
            String dist = String.format("%.1f", cars.get(nums).getTime());
            gpCars.add(new Text("\t\t" + dist), 1, nums + 1);
        }

        gridPane.add(gpLocations, 0, 1);
        gridPane.add(new Rectangle(200, 200, Color.TRANSPARENT), 0, 2);
        gridPane.add(gpCars, 0, 3);
        gridPane.add(new Rectangle(200, 200, Color.TRANSPARENT), 0, 3);
        gridPane.add(new Text("Active Car\t" + activeCar.getIdentifier() + "\t" + carEndLocation.get(activeCar).getName()), 0, 5);
        gridPane.setLayoutX(sceneX - 300);
        this.getChildren().add(gridPane);
    }

    /**
     * TODO: Refactor.
     * Updates the cars statistics within the gridPane
     */
    public void updateStats() {
        ObservableList<Node> children = gpLocations.getChildren();
        for (int i = 0; i < gpLocations.getChildren().size() / 2; i++)
            for (Node n : children)
                if (GridPane.getRowIndex(n) == i + 1 && GridPane.getColumnIndex(n) == 1) {
                    Text t = (Text) n;
                    t.setText(String.format(
                            "\t\t%.1f", carCurrentLocation.get(activeCar).getDistanceToLocation(locations.get(i))));
                }
        children = gpCars.getChildren();
        for (int j = 0; j < gpCars.getChildren().size() / 2; j++)
            for (Node m : children)
                if (GridPane.getRowIndex(m) == j + 1 && GridPane.getColumnIndex(m) == 1) {
                    Text tmpM = (Text) m;
                    tmpM.setText(String.format("\t\t%.1f", cars.get(j).getTime()));
                }
        children = gridPane.getChildren();
        for (Node o : children)
            if (GridPane.getRowIndex(o) == 5 && GridPane.getColumnIndex(o) == 0) {
                Text tmpO = (Text) o;
                tmpO.setText("Active Car\t" + activeCar.getIdentifier() + " " + carEndLocation.get(activeCar).getName());
            }
    }

    /**
     * Animates the transition of the active car.
     *
     * @param x1 The current location x
     * @param y1 The current location y
     * @param x2 The destination location x
     * @param y2 The destination location y
     */
    private void move(double x1, double y1, double x2, double y2) {
        Path p = new Path();
        p.getElements().add(new MoveTo(x1, y1));
        p.getElements().add(new LineTo(x1, y1));
        p.getElements().add(new LineTo(x2, y2));
        p.setOpacity(0);
        this.getChildren().add(p);
        PathTransition pt = new PathTransition();
        pt.setNode(activeCar);
        pt.setDuration(Duration.seconds(.5));
        pt.setDelay(Duration.seconds(0));
        pt.setPath(p);
        pt.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pt.play();
    }

    /**
     * Handles changing the active car's location.
     */
    private EventHandler<MouseEvent> locationEvent = mouseEvent -> {
        Location l = (Location) mouseEvent.getSource();
        if (!carVisitedLocations.get(activeCar).contains(l)) {
            double x = carCurrentLocation.get(activeCar).getCenterX();
            double y = carCurrentLocation.get(activeCar).getCenterY();
            if (!carEndLocation.get(activeCar).equals(l) || carEndLocation.get(activeCar).isActive()) {
                boolean carOneMoreLocation = carVisitedLocations.get(activeCar).size() + 1 == locations.size();
                l.setClicked(true);
                l.setActive(false, false);
                double distance = carCurrentLocation.get(activeCar).getDistanceToLocation(l);
                Location carLocation = carCurrentLocation.get(activeCar);
                carCurrentLocation.put(activeCar, l); // updates activeCar current location
                carVisitedLocations.get(activeCar).add(l); // updates visited locations

                Location local = carCurrentLocation.get(activeCar);

                Line line = new Line(carLocation.getCenterX(), carLocation.getCenterY(), local.getCenterX(), local.getCenterY());

                Color[] color = {Color.RED, Color.BLUE, Color.BLACK, Color.YELLOW, Color.ORANGE};

                line.setStroke(color[activeCar.getIdentifier() % color.length]);
                this.getChildren().add(line);

                activeCar.newLocation(l.getCenterX() - l.getRadius(), l.getCenterY() - l.getRadius(), distance);

                move(x, y, l.getCenterX(), l.getCenterY());

                activeCar = cars.get((cars.indexOf(activeCar) + 1) % cars.size());
                updateStats();
                carOneMoreLocation = carVisitedLocations.get(activeCar).size() + 1 >= locations.size();
                for (Location location : locations) {
                    if (carVisitedLocations.get(activeCar).contains(location))
                        location.setActive(false, false);
                    if (!carVisitedLocations.get(activeCar).contains(location))
                        location.setActive(true, false);
                    if (carEndLocation.get(activeCar).equals(location))
                        carEndLocation.get(activeCar).setActive(false, true);
                    if (carOneMoreLocation && carEndLocation.get(activeCar).equals(location)) {
                        location.setActive(true, false);
                        location.setLastColor();
                    }
                }
                activeCar.setVisible(true);

            }
        }
        int finished = 0;
        for (Car c : cars) if (carVisitedLocations.get(c).size() == locations.size()) finished++;
        if (finished == cars.size()) {

            for (Location local : locations) {
                URL green = getClass().getResource("/main/resources/images/redgif.gif");
                local.setFill(new ImagePattern(new Image(green.toString())));
            }

            Car car = cars.get(0);

            for (Car aCar : cars) {
                aCar.setVisible(true);
                if (car.getTime() > aCar.getTime()) car = aCar;
            }

            Text t = new Text("Car #" + car.getIdentifier() + "WINS!!");

            t.setFont(Font.font(50));
            t.setFill(Color.GREEN);
            t.setEffect(new Glow());
            t.setTextAlignment(TextAlignment.CENTER);
            t.setTranslateY(100);
            t.setTranslateX(200);
            t.setTranslateZ(300);

            this.getChildren().add(t);

            car.setHeight(500);
            car.setWidth(500);
        }
    };

}