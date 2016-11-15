package uk.ac.coventry.bello.myinventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Freshollie on 14/11/2016.
 */

public class Inventory {
    private Map<InventoryItem, Integer> mInventory;
    private static final Inventory INSTANCE = new Inventory();

    private Inventory(){
        mInventory = new HashMap<>();
        makeTestItemsList(); // For testing only
    }
    public void makeTestItemsList(){
        mInventory.put(new InventoryItem("Bacon", 1.69), 1);
        mInventory.put(new InventoryItem("Mince", 1.50), 2);
        mInventory.put(new InventoryItem("Chips", 2.0), 1);
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

    public int getQuantity(InventoryItem inventoryItem){
        return mInventory.get(inventoryItem);
    }
}
