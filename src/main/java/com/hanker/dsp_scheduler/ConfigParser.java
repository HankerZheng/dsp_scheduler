package com.hanker.dsp_scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.ArrayList;

/**
 * Parses the YAML configurations.
 */
public class ConfigParser {
  ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  public static List<Item> parseItems(String filename) {
    return new ArrayList<>();
  }

  public static List<Recipe> parseRecipe(String filename) {
    return new ArrayList<>();
  }
}
