package uk.ac.coventry.bello.myvehicle;

/**
 * Created by bello on 10/10/2016.
 */
public class Vehicle {
    private String make;
    private int year;
    private String message;
    public static int counter = 0;

    public Vehicle(){
        this.make = "Renault";
        this.year = 2006;
        this.message = "This is the default message";
        count();
    }

    public Vehicle(String make, int Year){
        this.make = make;
        this.year = year;
        this.message = "You car is a " + make + " built in " + year;
        count();
    }

    public Vehicle(String make){
        this();
        this.make = make;
        this.message = "You didn't type in a year value";
    }

    public Vehicle(int year){
        this();
        this.year = year;
        this.message = "You didn't type in a make";

    }

    public String getMessage(){
        return message;
    }

    @Override
    public String toString(){
        return message;
    }

    private void count(){
        this.counter ++;
    }

}
