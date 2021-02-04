package com.hanker.dsp_scheduler;

import com.hanker.dsp_scheduler.proto.Ingredient;
import com.hanker.dsp_scheduler.proto.Recipe;
import com.hanker.dsp_scheduler.proto.Requirement;

import java.util.*;

public class RequirementTree {

  private static final int MAX_STACK_SIZE = 1000;
  String rootNodeName;
  Map<String, RequirementNode> requirementNodeMap;
  Map<String, Requirement> supplyMap;

  RequirementTree() {
    this.requirementNodeMap = new HashMap<>();
    this.supplyMap = new HashMap<>();
  }

  private static int getRequireQuantity(Recipe recipe, String name) {
    Ingredient ingredient =
    recipe.getOutputsList().stream().filter(i -> i.getName().equals(name)).findFirst().get();
    return ingredient.getQuantity();
  }
  public static RequirementTree getSolution(DspScheduler dspScheduler, Requirement requirement) {
    RequirementTree solution = new RequirementTree();
    solution.rootNodeName = requirement.getName();
    Stack<Requirement> requirementStack = new Stack<>();
    requirementStack.push(requirement);

    while (!requirementStack.isEmpty()) {
      Requirement newRequirement = requirementStack.pop();
      solution.updateSupplyMap(newRequirement);
      Recipe recipe = dspScheduler.getDefaultRecipe(newRequirement.getName());
      if (requirementStack.size() > MAX_STACK_SIZE) {
        throw new RuntimeException("MAX_STACK_SIZE reached!");
      }

      RequirementNode newNode = new RequirementNode();
      newNode.recipe = recipe;
      solution.requirementNodeMap.put(newRequirement.getName(), newNode);
      int requirementQuantity = getRequireQuantity(recipe, newRequirement.getName());
      for (Ingredient ingredient : recipe.getInputsList()) {
        newNode.resources.add(ingredient.getName());
        requirementStack.push(
            Requirement.newBuilder()
                .setName(ingredient.getName())
                .setYieldRate(newRequirement.getYieldRate() * ingredient.getQuantity() / requirementQuantity)
                .build());
      }
      for (Ingredient ingredient : recipe.getOutputsList()) {
        if (!ingredient.getName().equals(newRequirement.getName()))  {
          newNode.byProducts.add(ingredient.getName());
          solution.updateSupplyMap(
              Requirement.newBuilder()
                  .setName(ingredient.getName())
                  .setYieldRate(- ingredient.getQuantity() * newRequirement.getYieldRate() / requirementQuantity)
              .build());
        }
      }
    }
    solution.updateFactoryCount();
    return solution;
  }

  public static void main(String[] args) {
    DspScheduler dspScheduler = DspScheduler.createFromDefaultConfiguration();
    RequirementTree solution = RequirementTree.getSolution(
        dspScheduler,
        Requirement.newBuilder().setName("Processor").setYieldRate(10).build());
    System.out.println(solution);
  }

  public void updateSupplyMap(Requirement requirement) {
    String name = requirement.getName();
    Requirement existingSupply = supplyMap.getOrDefault(name, Requirement.newBuilder().setName(name).build());
    supplyMap.put(name,
        existingSupply.toBuilder()
            .setYieldRate(existingSupply.getYieldRate() + requirement.getYieldRate())
            .build());
  }

  public void updateFactoryCount() {
    for (String name : requirementNodeMap.keySet()) {
      float yieldRate = supplyMap.get(name).getYieldRate();
      RequirementNode node = requirementNodeMap.get(name);
      Optional<Ingredient> optionalIngredient =
          node.recipe.getOutputsList().stream().filter(ingredient -> ingredient.getName().equals(name)).findFirst();
      if (optionalIngredient.isPresent()) {
        node.factoryCount = yieldRate / (60.0F / node.recipe.getCraftTime() * optionalIngredient.get().getQuantity());
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (String name : requirementNodeMap.keySet()) {
      RequirementNode node = requirementNodeMap.get(name);
      Requirement requirement = supplyMap.get(name);
      sb.append(
          String.format("Produce: %s at %.2f per min in %f factory [%s].\n",
              requirement.getName(),
              requirement.getYieldRate(),
              node.factoryCount,
              node.recipe.getFactory()));
    }
    return sb.toString();
  }

  public static class RequirementNode {
    Recipe recipe;
    float factoryCount;
    List<String> byProducts = new ArrayList<>();
    List<String> resources = new ArrayList<>();
  }
}
