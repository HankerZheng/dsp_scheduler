package com.hanker.dsp_scheduler;

import com.google.protobuf.util.JsonFormat;
import com.hanker.dsp_scheduler.proto.Building;
import com.hanker.dsp_scheduler.proto.Item;
import com.hanker.dsp_scheduler.proto.Recipe;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses the YAML configurations.
 */
public class ConfigParser {
  private static ClassLoader classLoader = ClassLoader.getSystemClassLoader();
  private static Yaml yamlLoader = new Yaml();

  public static List<Item> parseItems(String filename) throws IOException {
    return null;

  }

  public static List<Building> parseBuildings(String filename) throws IOException {
    return null;
  }

  public static List<Recipe> parseRecipes(String filename) throws IOException {
    List<Recipe> recipes = new ArrayList<>();
    for (Object data : loadYamlDataFileFile(filename)) {
      Recipe.Builder recipeBuilder = Recipe.newBuilder();
      JsonFormat.parser().merge(data.toString(), recipeBuilder);
      recipes.add(recipeBuilder.build());
    }
    return recipes;
  }

  private static List<Object> loadYamlDataFileFile(String filename) throws IOException {
    InputStream inputStream = classLoader.getResourceAsStream(filename);
    return yamlLoader.load(inputStream);
  }

  public static void main(String[] args) throws Exception {
    List<Recipe> recipes = parseRecipes("recipes.yml");
    System.out.println(recipes);
  }
}
