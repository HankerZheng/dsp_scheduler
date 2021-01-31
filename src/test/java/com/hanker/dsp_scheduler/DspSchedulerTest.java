package com.hanker.dsp_scheduler;

import com.hanker.dsp_scheduler.configuration.ConfigParser;
import com.hanker.dsp_scheduler.configuration.DspSchedulerGlobalState;
import com.hanker.dsp_scheduler.proto.Building;
import com.hanker.dsp_scheduler.proto.Ingredient;
import com.hanker.dsp_scheduler.proto.Item;
import com.hanker.dsp_scheduler.proto.Recipe;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class DspSchedulerTest {
  private DspScheduler dspScheduler;

  @Before
  public void setup() throws Exception {
    Map<String, Item> itemMap = ConfigParser.parseItemMap("test_items.yml");
    Map<String, Building> buildingMap = ConfigParser.parseBuildingMap("test_buildings.yml");
    List<Recipe> recipeList = ConfigParser.parseRecipes("test_recipes.yml");
    DspSchedulerGlobalState state = DspSchedulerGlobalState.createFromLocalData(
        itemMap, buildingMap, recipeList);
    dspScheduler = new DspScheduler(state);
  }

  @Test
  public void getRequirementToProduce_defaultRecipe() {
    List<Ingredient> ingredients = dspScheduler.getRequirementToProduce("item3", 60);
  }
}
