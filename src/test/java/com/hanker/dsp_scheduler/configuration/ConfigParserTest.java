package com.hanker.dsp_scheduler.configuration;

import com.hanker.dsp_scheduler.proto.Building;
import com.hanker.dsp_scheduler.proto.Ingredient;
import com.hanker.dsp_scheduler.proto.Item;
import com.hanker.dsp_scheduler.proto.Recipe;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.truth.extensions.proto.ProtoTruth.assertThat;


public class ConfigParserTest {

  @Test
  public void parseBuildings() throws Exception {
    Building worker = Building.newBuilder()
        .setName("worker")
        .setYieldMultiplier(1.5F)
        .build();
    Building building = Building.newBuilder()
        .setName("building")
        .build();

    Map<String, Building> buildingMap =
        ConfigParser.parseBuildingMap("test_buildings.yml");

    assertThat(buildingMap)
        .containsExactly("worker", worker, "building", building);
  }

  @Test
  public void parseItems() throws Exception {
    Item itemForParserTest = Item.newBuilder()
        .setName("itemForParserTest")
        .setDescription("description for one item.")
        .addProducedItems("A")
        .addProducedItems("B")
        .addProducedBuildings("C")
        .addProducedBuildings("D")
        .build();

    Map<String, Item> itemMap =
        ConfigParser.parseItemMap("test_items.yml");

    assertThat(itemMap).hasSize(4);
    assertThat(itemMap.get("itemForParserTest")).isEqualTo(itemForParserTest);
    assertThat(itemMap).containsKey("item1");
    assertThat(itemMap).containsKey("item2");
    assertThat(itemMap).containsKey("item3");
  }

  @Test
  public void parseRecipes() throws Exception {
    Recipe recipe1 = Recipe.newBuilder()
        .addOutputs(Ingredient.newBuilder().setName("item3").setQuantity(1))
        .addInputs(Ingredient.newBuilder().setName("item1").setQuantity(2))
        .addInputs(Ingredient.newBuilder().setName("item2").setQuantity(3))
        .setFactory("worker")
        .setCraftTime(4.0F)
        .build();
    Recipe recipe2 = Recipe.newBuilder()
        .addOutputs(Ingredient.newBuilder().setName("building").setQuantity(1))
        .addInputs(Ingredient.newBuilder().setName("item2").setQuantity(2))
        .addInputs(Ingredient.newBuilder().setName("item3").setQuantity(3))
        .setFactory("worker")
        .setCraftTime(3.0F)
        .build();
    Recipe recipe3 = Recipe.newBuilder()
        .addOutputs(Ingredient.newBuilder().setName("item3").setQuantity(3))
        .addOutputs(Ingredient.newBuilder().setName("item1").setQuantity(1))
        .addInputs(Ingredient.newBuilder().setName("item2").setQuantity(1))
        .addInputs(Ingredient.newBuilder().setName("item3").setQuantity(1))
        .setFactory("worker")
        .setCraftTime(2.0F)
        .build();

    List<Recipe> recipeList =
        ConfigParser.parseRecipes("test_recipes.yml");

    assertThat(recipeList).ignoringRepeatedFieldOrder().containsExactly(recipe1, recipe2, recipe3);
  }
}

