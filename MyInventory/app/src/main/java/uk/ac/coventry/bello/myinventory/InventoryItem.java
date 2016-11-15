package uk.ac.coventry.bello.myinventory;

public class InventoryItem {
    private String name;
    private double price;
    private int totalGrams;
    private int grams;

    public InventoryItem(String name, double price){
        this.name = name;
        this.price = price;
    }

    public String getName(){
        return this.name;
    }

    public double getPrice(){
        return this.price;
    }

    public float getTotalGrams(){
        return this.totalGrams;
    }

    public int getGrams(){
        return this.grams;
    }


}
