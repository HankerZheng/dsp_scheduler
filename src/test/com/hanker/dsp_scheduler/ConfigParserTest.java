package com.hanker.dsp_scheduler;

import com.hanker.dsp_scheduler.proto.Building;
import com.hanker.dsp_scheduler.proto.Item;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class ConfigParserTest {

  @Test
  public void parseItems() throws Exception {
    List<Item> items = ConfigParser.parseItems("items.yml");
    assertEquals(78, items.size());
  }

  @Test
  public void parseBuildings() throws Exception {
    List<Building>  buildings = ConfigParser.parseBuildings("buildings.yml");
    assertEquals(39, buildings.size());
  }

  @Test
  public void parseRecipes() throws Exception {
    List<Building>  buildings = ConfigParser.parseBuildings("recipes.yml");
    assertEquals(11, buildings.size());
  }
}