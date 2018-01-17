package com.company;

import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.static_data.constant.ChampionListTags;
import net.rithms.riot.api.endpoints.static_data.constant.ItemListTags;
import net.rithms.riot.api.endpoints.static_data.constant.Locale;
import net.rithms.riot.api.endpoints.static_data.dto.Champion;
import net.rithms.riot.api.endpoints.static_data.dto.Item;
import net.rithms.riot.api.endpoints.static_data.dto.ItemList;
import net.rithms.riot.constant.Platform;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Engine {
    static private RiotApi riotApi;
    static private String KEY = "RGAPI-b23d0f39-7485-4e88-a80a-660b3e3ba809";
    public static HashMap<String, Champion> championsMap = new HashMap<>();
    public static  Map<String, Item> itemList;
    private static Champion currentChampion;
    private static Platform currentPlatform;

    Engine() {
        ApiConfig config = new ApiConfig().setKey(KEY);
        riotApi = new RiotApi(config);
        try {
            loadChampionList();
            loadItemList();
        } catch (RiotApiException e) {
            System.out.println("FAILED");
            System.out.println(e.getMessage());
        }
    }

    public static Platform getCurrentPlatform() {
        if(currentPlatform == null){
            currentPlatform = Platform.NA;
        }
        return currentPlatform;
    }

    public static void setCurrentPlatform(String platformString) {
        currentPlatform = Platform.getPlatformByName(platformString);
    }
    public static List<String> platformNamesList = Arrays.stream(Platform.values())
            .map(platform -> platform.getName().toUpperCase())
            .collect(Collectors.toList());

    public static Champion getCurrentChampion() {
        return currentChampion;
    }

    public static void setCurrentChampionFromName(String name) {
        Engine.currentChampion = championsMap.get(name);
    }

    private void loadChampionList() throws RiotApiException {
        readChampionsFromFile();
        if(championsMap != null)
            return;
        championsMap = new HashMap<>();
        System.out.println("Calling service for champions");
        Collection<Champion> championCollection = riotApi.getDataChampionList(getCurrentPlatform(), Locale.EN_US, null, true, ChampionListTags.STATS)
                .getData()
                .values();
        championCollection
                .forEach(champion -> championsMap.put(champion.getName(), champion));
        writeChampionsToFile();
    }

    private void loadItemList() throws RiotApiException {
        readItemsFromFile();
        if(itemList != null)
            return;
        itemList = new HashMap<>();
        System.out.println("Calling service for items");
        ArrayList<Item> itemCollection = new ArrayList<>(riotApi.getDataItemList(getCurrentPlatform(), Locale.EN_US, null, ItemListTags.STATS, ItemListTags.MAPS, ItemListTags.IMAGE)
                .getData()
                .values());
        itemCollection.removeIf(item -> !item.getMaps().get(String.valueOf(11)));
        itemCollection.forEach((item -> itemList.put(item.getName(), item)));
        writeItemsToFile();
    }

    private void readChampionsFromFile(){
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("championList.ser"))){
            championsMap = (HashMap<String, Champion>) objectInputStream.readObject();
            System.out.println("Read champions from file");
        } catch (FileNotFoundException e) {
            System.out.println("No stats saved");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void writeChampionsToFile(){
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("championList.ser"))){
            objectOutputStream.writeObject(championsMap);
            System.out.println("Wrote champions to file");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readItemsFromFile(){
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("itemList.ser"))){
            itemList = (Map<String, Item>) objectInputStream.readObject();
            System.out.println("Read items from file");
        } catch (FileNotFoundException e) {
            System.out.println("No stats saved");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void writeItemsToFile(){
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("itemList.ser"))){
            objectOutputStream.writeObject(itemList);
            System.out.println("Wrote items to file");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
