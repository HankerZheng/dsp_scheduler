package com.hanker.dsp_scheduler.configuration;

import com.hanker.dsp_scheduler.proto.Building;
import com.hanker.dsp_scheduler.proto.Ingredient;
import com.hanker.dsp_scheduler.proto.Item;
import com.hanker.dsp_scheduler.proto.Recipe;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.extensions.proto.ProtoTruth.assertThat;
import static org.junit.Assert.assertThrows;


public class DspSchedulerGlobalStateTest {

  @Test
  public void getOutputItemOrBuildingName_buildingName() {
    Ingredient ingredient = Ingredient.newBuilder().setBuildingName("building").build();
    assertThat(DspSchedulerGlobalState.getItemOrBuildingName(ingredient)).isEqualTo("building");
  }

  @Test
  public void getOutputItemOrBuildingName_itemName() {
    Ingredient ingredient = Ingredient.newBuilder().setItemName("item").build();
    assertThat(DspSchedulerGlobalState.getItemOrBuildingName(ingredient)).isEqualTo("item");
  }

  @Test
  public void getOutputItemOrBuildingName_noName() {
    Ingredient ingredient = Ingredient.getDefaultInstance();
    RuntimeException e = assertThrows(RuntimeException.class,
        () -> DspSchedulerGlobalState.getItemOrBuildingName(ingredient));
    assertThat(e).hasMessageThat().contains("No name is set in the ingredient");
  }

  @Test
  public void initialize() throws Exception {
    Map<String, Item> itemMap = ConfigParser.parseItemMap("test_items.yml");
    Map<String, Building> buildingMap = ConfigParser.parseBuildingMap("test_buildings.yml");
    List<Recipe> recipes = ConfigParser.parseRecipes("test_recipes.yml");

    DspSchedulerGlobalState state = DspSchedulerGlobalState.createFromLocalData(itemMap, buildingMap, recipes);

    assertThat(state.getItem("itemForParserTest")).isNotNull();
    assertThat(state.getItem("item1"))
        .ignoringRepeatedFieldOrder()
        .isEqualTo(Item.newBuilder()
            .setName("item1")
            .setDescription("description for item1.")
            .addProducedItems("item3")
            .build());
    assertThat(state.getItem("item2"))
        .ignoringRepeatedFieldOrder()
        .isEqualTo(Item.newBuilder().setName("item2")
            .setDescription("description for item2.")
            .addProducedItems("item1")
            .addProducedItems("item3")
            .addProducedBuildings("building")
            .build());
    assertThat(state.getItem("item3"))
        .ignoringRepeatedFieldOrder()
        .isEqualTo(Item.newBuilder().setName("item3")
            .setDescription("description for item3.")
            .addProducedItems("item1")
            .addProducedItems("item3")
            .addProducedBuildings("building")
            .build());
    assertThat(state.getBuilding("worker")).isEqualTo(
        Building.newBuilder().setName("worker").setYieldMultiplier(1.5F).build());
    assertThat(state.getBuilding("building")).isEqualTo(Building.newBuilder().setName("building").build());
    assertThat(state.getRecipeByName("item1")).ignoringRepeatedFieldOrder()
        .containsExactly(recipes.get(2));
    assertThat(state.getRecipeByName("item2"))
        .isEmpty();
    assertThat(state.getRecipeByName("item3")).ignoringRepeatedFieldOrder()
        .containsExactly(recipes.get(0), recipes.get(2));
    assertThat(state.getRecipeByName("building")).containsExactly(recipes.get(1));
  }
}