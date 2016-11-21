package uk.ac.coventry.bello.myinventory;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.JsonWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Freshollie on 14/11/2016.
 */

public class Inventory {
    private Map<InventoryItem, Integer> mInventory;
    private static final Inventory INSTANCE = new Inventory();

    private Inventory() {
        resetInventory();
    }

    public void resetInventory(){
        mInventory = new HashMap<>();
    }

    public static Inventory getInstance(){
        return INSTANCE;
    }

    public Map<InventoryItem, Integer> getInventory(){
        return mInventory;
    }

    public void setInventory(Map<InventoryItem, Integer> inventory){
        mInventory = inventory;
    }

    public int getInventoryLength(){
        return mInventory.size();
    }

    public boolean isItem(String name){
        Iterator items = mInventory.keySet().iterator();
        while(items.hasNext()){
            if(name.toLowerCase().equals(((InventoryItem)items.next()).getName().toLowerCase())){
                return true;
            }
        }
        return false;
    }

    public List<InventoryItem> getItems(){
        return new ArrayList<InventoryItem>(mInventory.keySet());
    }

    public void setItem(InventoryItem inventoryItem, int quantity){
        mInventory.put(inventoryItem, quantity);
    }

    public int getQuantity(InventoryItem item){
        if(mInventory.containsKey(item)){
            return mInventory.get(item);
        } else {
            return 0;
        }
    }

    public Set<String> getSaveStringSet(){
        Iterator<InventoryItem> items = mInventory.keySet().iterator();

        Set<String> stringSet = new HashSet<String>();
        while(items.hasNext()){
            JSONObject finalJsonObject = new JSONObject();
            InventoryItem item = items.next();

            try {
                finalJsonObject.put("item", item.getJson());
                finalJsonObject.put("quantity", getQuantity(item));
            } catch (JSONException e){
                e.printStackTrace();
            }

            stringSet.add(finalJsonObject.toString());
        }
        return stringSet;
    }

    public void save(Context context){
        SharedPreferences mPrefs = context.getSharedPreferences(context.getString(R.string.saved_items_key), 0);
        SharedPreferences.Editor editor = mPrefs.edit();


        editor.putStringSet(context.getString(R.string.saved_items_key), getSaveStringSet());
        editor.commit();
    }

    public void load(Context context){
        resetInventory();

        SharedPreferences mPrefs = context.getSharedPreferences(context.getString(R.string.saved_items_key), 0);

        Set<String> set = mPrefs.getStringSet(context.getString(R.string.saved_items_key), null);
        if (set != null) {
            for (String s : set) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject jsonItem = jsonObject.getJSONObject("item");
                    String name = jsonItem.getString("name");
                    double price = jsonItem.getDouble("price");

                    int quantity = jsonObject.getInt("quantity");

                    setItem(new InventoryItem(name, price), quantity);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
