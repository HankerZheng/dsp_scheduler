package com.hanker.dsp_scheduler.configuration;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.hanker.dsp_scheduler.proto.Building;
import com.hanker.dsp_scheduler.proto.Ingredient;
import com.hanker.dsp_scheduler.proto.Item;
import com.hanker.dsp_scheduler.proto.Recipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hanker.dsp_scheduler.proto.Ingredient.NameOneofCase.BUILDING_NAME;
import static com.hanker.dsp_scheduler.proto.Ingredient.NameOneofCase.ITEM_NAME;

public class DspSchedulerGlobalState {
  private ListMultimap<String, Recipe> recipeMultimap;
  private Map<String, Item> itemMap;
  private Map<String, Building> buildingMap;

  DspSchedulerGlobalState() {}

  public static DspSchedulerGlobalState createFromDefaultFiles() throws IOException {
    Map<String, Item> itemMap = ConfigParser.parseItemMap("items.yml");
    Map<String, Building> buildingMap = ConfigParser.parseBuildingMap("buildings.yml");
    List<Recipe> recipeList = ConfigParser.parseRecipes("recipes.yml");

    return new DspSchedulerGlobalState().initialize(itemMap, buildingMap, recipeList);
  }

  @VisibleForTesting
  static String getOutputItemOrBuildingName(Ingredient ingredient) {
    if (ingredient.getNameOneofCase() == ITEM_NAME) {
      return ingredient.getItemName();
    } else if (ingredient.getNameOneofCase() == BUILDING_NAME) {
      return ingredient.getBuildingName();
    }
    throw new RuntimeException(String.format("No name is set in the ingredient %s", ingredient));
  }

  @VisibleForTesting
  DspSchedulerGlobalState initialize(
      Map<String, Item> inputItemMap, Map<String, Building> buildingMap, List<Recipe> recipeList) {
    this.itemMap = new HashMap<>(inputItemMap);
    this.buildingMap = buildingMap;
    this.recipeMultimap = ArrayListMultimap.create();

    for (Recipe recipe : recipeList) {
      updateProducedItemsInItemMap(recipe);
      for (Ingredient outputIngredient : recipe.getOutputsList()) {
        this.recipeMultimap.put(getOutputItemOrBuildingName(outputIngredient), recipe);
      }
    }
    return this;
  }

  private void updateProducedItemsInItemMap(Recipe recipe) {
    List<String> producedItems = new ArrayList<>();
    List<String> producedBuildings = new ArrayList<>();
    for (Ingredient output : recipe.getOutputsList()) {
      if (!output.getItemName().isEmpty()) {
        producedItems.add(output.getItemName());
      } else if (!output.getBuildingName().isEmpty()) {
        producedBuildings.add(output.getBuildingName());
      }
    }
    for (Ingredient input : recipe.getInputsList()) {
      if (input.getItemName().isEmpty()) {
        throw new RuntimeException(
            String.format("The input ingredients' item of this recipe doesn't have a name!! %s", recipe));
      }
      Item inputItem = itemMap.get(input.getItemName());
      itemMap.put(
          input.getItemName(),
          inputItem.toBuilder()
              .addAllProducedItems(producedItems)
              .addAllProducedBuildings(producedBuildings)
              .build());
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
