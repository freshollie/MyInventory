package uk.ac.coventry.bello.myinventory;

public class Item {
    private String name;
    private float price;
    private int totalGrams;
    private int grams;

    public Item(String name, float price, int totalGrams){
        this.name = name;
        this.price = price;
        this.totalGrams = totalGrams;
        this.grams = totalGrams;
    }

    public String getName(){
        return this.name;
    }

    public float getPrice(){
        return this.price;
    }

    public float getTotalGrams(){
        return this.totalGrams;
    }

    public int getGrams(){
        return this.grams;
    }


}
