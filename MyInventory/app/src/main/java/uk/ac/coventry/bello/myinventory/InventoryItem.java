package uk.ac.coventry.bello.myinventory;
import org.json.JSONException;
import org.json.JSONObject;

public class InventoryItem {
    private String name;
    private double price;

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
