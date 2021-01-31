package com.hanker.dsp_scheduler.configuration;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.hanker.dsp_scheduler.proto.Building;
import com.hanker.dsp_scheduler.proto.Ingredient;
import com.hanker.dsp_scheduler.proto.Item;
import com.hanker.dsp_scheduler.proto.Recipe;
import org.junit.Test;

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
  public void initialize() {
    ImmutableMap<String, Item> itemMap = ImmutableMap.of(
        "item1", Item.newBuilder().setName("item1").build(),
        "item2", Item.newBuilder().setName("item2").build(),
        "item3", Item.newBuilder().setName("item3").build());
    Building workerBuilding = Building.newBuilder().setName("worker").build();
    Building building = Building.newBuilder().setName("building").build();
    ImmutableMap<String, Building> buildingMap =
        ImmutableMap.of("worker", workerBuilding, "building", building);
    Recipe recipe1 =
        Recipe.newBuilder()
            .addInputs(Ingredient.newBuilder().setItemName("item1").setQuantity(1).build())
            .addInputs(Ingredient.newBuilder().setItemName("item2").setQuantity(2).build())
            .addOutputs(Ingredient.newBuilder().setItemName("item3").setQuantity(3).build())
            .setProcessingTime(2.0F)
            .setBuildingName("worker").build();
    Recipe recipe2 =
        Recipe.newBuilder()
            .addInputs(Ingredient.newBuilder().setItemName("item2").setQuantity(2).build())
            .addInputs(Ingredient.newBuilder().setItemName("item3").setQuantity(3).build())
            .addOutputs(Ingredient.newBuilder().setBuildingName("building").setQuantity(1).build())
            .setProcessingTime(4.0F)
            .setBuildingName("worker").build();
    ImmutableList<Recipe> recipes = ImmutableList.of(recipe1, recipe2);

    DspSchedulerGlobalState state = DspSchedulerGlobalState.createFromLocalData(itemMap, buildingMap, recipes);

    assertThat(state.getItem("item1"))
        .ignoringRepeatedFieldOrder()
        .isEqualTo(Item.newBuilder().setName("item1").addProducedItems("item3").build());
    assertThat(state.getItem("item2"))
        .ignoringRepeatedFieldOrder()
        .isEqualTo(Item.newBuilder().setName("item2").addProducedItems("item3").addProducedBuildings("building")
            .build());
    assertThat(state.getItem("item3"))
        .ignoringRepeatedFieldOrder()
        .isEqualTo(Item.newBuilder().setName("item3").addProducedBuildings("building").build());
    assertThat(state.getBuilding("worker")).isEqualTo(workerBuilding);
    assertThat(state.getBuilding("building")).isEqualTo(building);
    assertThat(state.getRecipeByName("item1")).isEmpty();
    assertThat(state.getRecipeByName("item2")).isEmpty();
    assertThat(state.getRecipeByName("item3")).containsExactly(recipe1);
    assertThat(state.getRecipeByName("building")).containsExactly(recipe2);
  }
}