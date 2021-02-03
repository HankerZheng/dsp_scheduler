package com.hanker.dsp_scheduler;

import com.hanker.dsp_scheduler.configuration.ConfigParser;
import com.hanker.dsp_scheduler.configuration.DspSchedulerGlobalState;
import com.hanker.dsp_scheduler.proto.Building;
import com.hanker.dsp_scheduler.proto.Item;
import com.hanker.dsp_scheduler.proto.Recipe;
import com.hanker.dsp_scheduler.proto.Requirement;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.truth.extensions.proto.ProtoTruth.assertThat;

public class DspSchedulerTest {
  private DspScheduler dspScheduler;
  private DspSchedulerGlobalState state;

  @Before
  public void setup() throws Exception {
    Map<String, Item> itemMap = ConfigParser.parseItemMap("test_items.yml");
    Map<String, Building> buildingMap = ConfigParser.parseBuildingMap("test_buildings.yml");
    List<Recipe> recipeList = ConfigParser.parseRecipes("test_recipes.yml");
    state = DspSchedulerGlobalState.createFromLocalData(
        itemMap, buildingMap, recipeList);
    dspScheduler = new DspScheduler(state);
  }

  @Test
  public void getRequirementsToProduce_noRequirements() {
    assertThat(dspScheduler.getRequirementsToProduce("item2", 60)).isEmpty();
  }

  @Test
  public void getRequirementsToProduce_item1_specifiedRecipe() {
    Recipe recipe = state.getRecipeByName("item1").get(0);
    List<Requirement> requirements = dspScheduler.getRequirementsToProduce("item1", 60, recipe);

    assertThat(requirements).ignoringRepeatedFieldOrder().containsExactly(
        Requirement.newBuilder()
            .setItemName("item2").setYieldRate(60.0F).build(),
        Requirement.newBuilder()
            .setItemName("item3").setYieldRate(60.0F).build());
  }

  @Test
  public void getRequirementsToProduce_item3() {
    List<Requirement> requirements = dspScheduler.getRequirementsToProduce("item3", 60);
    assertThat(requirements).ignoringRepeatedFieldOrder().containsExactly(
        Requirement.newBuilder()
            .setItemName("item1").setYieldRate(120.0F).build(),
        Requirement.newBuilder()
            .setItemName("item2").setYieldRate(180.0F).build());
  }

  @Test
  public void getRequirementsToProduce_building() {
    List<Requirement> requirements = dspScheduler.getRequirementsToProduce("building", 60);
    assertThat(requirements).ignoringRepeatedFieldOrder().containsExactly(
        Requirement.newBuilder()
            .setItemName("item2").setYieldRate(120.0F).build(),
        Requirement.newBuilder()
            .setItemName("item3").setYieldRate(180.0F).build());
  }
}
