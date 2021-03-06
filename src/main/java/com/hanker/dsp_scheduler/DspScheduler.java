package com.hanker.dsp_scheduler;

import com.google.common.collect.ImmutableList;
import com.hanker.dsp_scheduler.configuration.DspSchedulerGlobalState;
import com.hanker.dsp_scheduler.proto.Ingredient;
import com.hanker.dsp_scheduler.proto.Recipe;
import com.hanker.dsp_scheduler.proto.Requirement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DspScheduler {
  private DspSchedulerGlobalState state;

  public DspScheduler(DspSchedulerGlobalState state) {
    this.state = state;
  }

  public static DspScheduler createFromDefaultConfiguration() {
    try {
      DspSchedulerGlobalState state = DspSchedulerGlobalState.createFromDefaultFiles();
      return new DspScheduler(state);
    } catch (IOException e) {
      throw new RuntimeException("Failed to process configuration files.", e);
    }
  }

  public static void main(String[] args) {
    DspScheduler scheduler = createFromDefaultConfiguration();
    scheduler.state.validate();
    for (Requirement requirement : scheduler.getRequirementsToProduce("Electric_Motor", 100)) {
      System.out.printf("%s\n", requirement);
    }
  }

  /**
   * Finds the requirements to produce a product at a give yield rate with the default recipe.
   */
  public List<Requirement> getRequirementsToProduce(String name, float yieldRate) {
    Recipe recipe = getDefaultRecipe(name);
    return getRequirementsToProduce(name, yieldRate, recipe);
  }

  /**
   * Finds the requirements to produce a product at a give yield rate.
   *
   * @param name      The name of the output item or building.
   * @param yieldRate The number of products to be produced per minute.
   * @param recipe    The recipe to be used to produce this product.
   * @return The minimal ingredients required to achieve the goal. Returns an empty list if the input recipe is invalid.
   */
  public List<Requirement> getRequirementsToProduce(String name, float yieldRate, Recipe recipe) {
    Optional<Ingredient> ingredientOptional = getOutputIngredientFromRecipe(name, recipe);
    if (!ingredientOptional.isPresent()) {
      System.err.printf("The recipe [%s] is not used for generate item [%s]!", recipe, name);
      return ImmutableList.of();
    }

    List<Requirement> requirements = new ArrayList<>();
    for (Ingredient ingredient : recipe.getInputsList()) {
      Requirement.Builder requirementBuilder = Requirement.newBuilder();
      requirementBuilder.setName(ingredient.getName());
      requirementBuilder.setYieldRate(yieldRate * ingredient.getQuantity());
      requirements.add(requirementBuilder.build());
    }
    return requirements;
  }

  public Recipe getDefaultRecipe(String name) {
    return state.getRecipeByName(name)
        .stream()
        .filter(recipe -> !getInputIngredientFromRecipe(name, recipe).isPresent())
        .findFirst()
        .orElse(Recipe.getDefaultInstance());
  }

  private Optional<Ingredient> getOutputIngredientFromRecipe(String name, Recipe recipe) {
    for (Ingredient ingredient : recipe.getOutputsList()) {
      if (ingredient.getName().equals(name)) {
        return Optional.of(ingredient);
      }
    }
    return Optional.empty();
  }

  private Optional<Ingredient> getInputIngredientFromRecipe(String name, Recipe recipe) {
    for (Ingredient ingredient : recipe.getInputsList()) {
      if (ingredient.getName().equals(name)) {
        return Optional.of(ingredient);
      }
    }
    return Optional.empty();
  }

  public DspSchedulerGlobalState getState() {
    return state;
  }
}
