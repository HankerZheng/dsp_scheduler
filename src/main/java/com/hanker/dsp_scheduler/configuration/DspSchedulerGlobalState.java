package com.hanker.dsp_scheduler.configuration;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;
import com.hanker.dsp_scheduler.proto.Building;
import com.hanker.dsp_scheduler.proto.Ingredient;
import com.hanker.dsp_scheduler.proto.Item;
import com.hanker.dsp_scheduler.proto.Recipe;

import java.io.IOException;
import java.util.*;


public class DspSchedulerGlobalState {
  private ListMultimap<String, Recipe> recipeMultimap;
  private Map<String, Item> itemMap;
  private Map<String, Building> buildingMap;

  DspSchedulerGlobalState(Map<String, Item> itemMap, Map<String, Building> buildingMap, List<Recipe> recipeList) {
    this.itemMap = new HashMap<>(itemMap);
    this.buildingMap = new HashMap(buildingMap);
    this.recipeMultimap = ArrayListMultimap.create();
    initialize(recipeList);
  }

  public static DspSchedulerGlobalState createFromDefaultFiles() throws IOException {
    Map<String, Item> itemMap = ConfigParser.parseItemMap("items.yml");
    Map<String, Building> buildingMap = ConfigParser.parseBuildingMap("buildings.yml");
    List<Recipe> recipeList = ConfigParser.parseRecipes("recipes.yml");

    return createFromLocalData(itemMap, buildingMap, recipeList);
  }

  public static DspSchedulerGlobalState createFromLocalData(
      Map<String, Item> itemMap, Map<String, Building> buildingMap, List<Recipe> recipeList) {
    return new DspSchedulerGlobalState(itemMap, buildingMap, recipeList);
  }



  private DspSchedulerGlobalState initialize(List<Recipe> recipeList) {
    for (Recipe recipe : recipeList) {
      updateProducedInItemMap(recipe);
      for (Ingredient outputIngredient : recipe.getOutputsList()) {
        this.recipeMultimap.put(outputIngredient.getName(), recipe);
      }
    }
    return this;
  }

  public void validate() {
    for (String itemName : itemMap.keySet()) {
      Preconditions.checkArgument(
          recipeMultimap.containsKey(itemName),
          "Item %s is not in the recipe map.",
          itemName);
    }
    for (String buildingName : buildingMap.keySet()) {
      if (buildingName.equals("Full_Accumulator")) {
        // Full accumulator is the fully charged accumulator.
        continue;
      }
      Preconditions.checkArgument(
          recipeMultimap.containsKey(buildingName),
          "Building %s is not in the recipe map.",
          buildingName);
    }
  }

  private void updateProducedInItemMap(Recipe recipe) {
    Set<String> producedItems = new HashSet<>();
    Set<String> producedBuildings = new HashSet<>();
    for (Ingredient output : recipe.getOutputsList()) {
      String name = output.getName();
      if (itemMap.containsKey(name)) {
        producedItems.add(output.getName());
      } else if (buildingMap.containsKey(name)) {
        producedBuildings.add(output.getName());
      }
    }
    for (Ingredient input : recipe.getInputsList()) {
      String name = input.getName();
      if (itemMap.containsKey(name)) {
        Item inputItem = itemMap.get(name);
        Preconditions.checkNotNull(inputItem, "Item %s not in the map.", name);
        itemMap.put(
            name,
            inputItem.toBuilder()
                .clearProducedItems()
                .clearProducedBuildings()
                .addAllProducedItems(
                    Sets.union(producedItems, new HashSet<>(inputItem.getProducedItemsList())))
                .addAllProducedBuildings(
                    Sets.union(producedBuildings, new HashSet<>(inputItem.getProducedBuildingsList())))
                .build());
      } else if (buildingMap.containsKey(name)) {
        Building inputBuilding = buildingMap.get(name);
        Preconditions.checkNotNull(inputBuilding, "Building %s is not in the map.", name);
        buildingMap.put(
            name,
            inputBuilding.toBuilder()
                .clearProducedItems()
                .clearProducedBuildings()
                .addAllProducedItems(
                    Sets.union(producedItems, new HashSet<>(inputBuilding.getProducedItemsList())))
                .addAllProducedBuildings(
                    Sets.union(producedBuildings, new HashSet<>(inputBuilding.getProducedBuildingsList())))
                .build());
      }
    }
  }

  public Item getItem(String itemName) {
    return itemMap.get(itemName);
  }

  public Building getBuilding(String buildingName) {
    return buildingMap.get(buildingName);
  }

  public List<Recipe> getRecipeByName(String name) {
    return recipeMultimap.get(name);
  }
}
