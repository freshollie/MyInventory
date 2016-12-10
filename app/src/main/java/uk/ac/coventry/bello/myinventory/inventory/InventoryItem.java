package uk.ac.coventry.bello.myinventory.inventory;

import org.json.JSONException;
import org.json.JSONObject;

public class InventoryItem {
    private String name;
    private double price;

    public InventoryItem(String itemName, double itemPrice){
        name = itemName;
        price = itemPrice;
    }

    public String getName(){
        return name;
    }

    public void setName(String itemName) {
        name = itemName;
    }

    public double getPrice(){
        return price;
    }

    public void setPrice(double itemPrice) {
        price = itemPrice;
    }

    public JSONObject getJson(){
        JSONObject jsonItem = new JSONObject();

        try{
            jsonItem.put("name", getName());
            jsonItem.put("price", getPrice());

        } catch(JSONException e){
            e.printStackTrace();
        }

        return jsonItem;
    }




}
