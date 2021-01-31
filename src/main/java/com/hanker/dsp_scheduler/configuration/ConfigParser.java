package com.hanker.dsp_scheduler.configuration;

import com.google.gson.Gson;
import com.google.protobuf.util.JsonFormat;
import com.hanker.dsp_scheduler.proto.Building;
import com.hanker.dsp_scheduler.proto.Item;
import com.hanker.dsp_scheduler.proto.Recipe;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses the YAML configurations.
 */
public class ConfigParser {
  private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
  private static final Yaml yamlLoader = new Yaml();
  private static final Gson gson = new Gson();

  public static Map<String, Item> parseItemMap(String filename) throws IOException {
    Map<String, Item> itemMap = new HashMap<>();
    for (Object data : loadYamlDataFileFile(filename)) {
      Item.Builder itemBuilder = Item.newBuilder();
      JsonFormat.parser().merge(gson.toJson(data), itemBuilder);
      itemMap.put(itemBuilder.getName(), itemBuilder.build());
    }
    return itemMap;
  }

  public static Map<String, Building> parseBuildingMap(String filename) throws IOException {
    Map<String, Building> buildingMap = new HashMap<>();
    for (Object data : loadYamlDataFileFile(filename)) {
      Building.Builder buildingBuilder = Building.newBuilder();
      JsonFormat.parser().merge(gson.toJson(data), buildingBuilder);
      buildingMap.put(buildingBuilder.getName(), buildingBuilder.build());
    }
    return buildingMap;
  }

  public static List<Recipe> parseRecipes(String filename) throws IOException {
    List<Recipe> recipes = new ArrayList<>();
    for (Object data : loadYamlDataFileFile(filename)) {
      Recipe.Builder recipeBuilder = Recipe.newBuilder();
      JsonFormat.parser().merge(gson.toJson(data), recipeBuilder);
      recipes.add(recipeBuilder.build());
    }
    return recipes;
  }

  private static List<String> loadYamlDataFileFile(String filename) throws IOException {
    InputStream inputStream = classLoader.getResourceAsStream(filename);
    return yamlLoader.load(inputStream);
  }
}
