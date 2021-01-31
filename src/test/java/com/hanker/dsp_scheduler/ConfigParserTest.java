package com.hanker.dsp_scheduler;

import com.hanker.dsp_scheduler.proto.Building;
import com.hanker.dsp_scheduler.proto.Ingredient;
import com.hanker.dsp_scheduler.proto.Item;
import com.hanker.dsp_scheduler.proto.Recipe;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.extensions.proto.ProtoTruth.assertThat;


public class ConfigParserTest {

  @Test
  public void parseBuildings() throws Exception {
    Building building1 = Building.newBuilder()
        .setName("building1")
        .build();
    Building building2 = Building.newBuilder()
        .setName("building2")
        .build();

    Map<String, Building> buildingMap =
        ConfigParser.parseBuildingMap("test_buildings.yml");

    assertThat(buildingMap)
        .containsExactly("building1", building1, "building2", building2);
  }
  @Test
  public void parseItems() throws Exception {
    Item item1 = Item.newBuilder()
        .setName("item1")
        .setDescription("description for item1.")
        .addUsedFor("item2")
        .addUsedFor("item3")
        .build();

    Map<String, Item.Builder> itemMap =
        ConfigParser.parseItemMap("test_items.yml");

    assertThat(itemMap).hasSize(3);
    assertThat(itemMap.get("item1").build()).isEqualTo(item1);
  }

  @Test
  public void parseRecipes() throws Exception {
    Recipe expectedRecipe = Recipe.newBuilder()
        .addOutputs(Ingredient.newBuilder().setItemName("item1").setQuantity(1))
        .addOutputs(Ingredient.newBuilder().setBuildingName("output_building").setQuantity(2))
        .addInputs(Ingredient.newBuilder().setItemName("item2").setQuantity(2))
        .addInputs(Ingredient.newBuilder().setItemName("item3").setQuantity(3))
        .setBuildingName("building")
        .setProcessingTime(1.5f)
        .build();

    List<Recipe> recipeList =
        ConfigParser.parseRecipes("test_recipes.yml");

    assertThat(recipeList).hasSize(2);
    assertThat(recipeList.get(0))
        .ignoringRepeatedFieldOrder()
        .isEqualTo(expectedRecipe);
  }
}

