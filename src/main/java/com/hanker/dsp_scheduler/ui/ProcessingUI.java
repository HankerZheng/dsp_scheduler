package com.hanker.dsp_scheduler.ui;

import com.hanker.dsp_scheduler.DspScheduler;
import com.hanker.dsp_scheduler.proto.Building;
import com.hanker.dsp_scheduler.proto.Ingredient;
import com.hanker.dsp_scheduler.proto.Item;
import com.hanker.dsp_scheduler.proto.Recipe;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.HashMap;
import java.util.List;

public class ProcessingUI extends PApplet {
    int itemSize = 55;     // Diameter of rect
    int itemLiquidSize = 27;
    int itemLiquidX = 11;
    int itemX = 12;
    int itemY = 7;
    int buildingX = 12;
    int buildingY = 4;
    int buildingMarginTop, buildingMarginLeft;
    int producedItemMarginTop, producedItemMarginLeft;
    int recRad = 7;
    int fontSize = 15;
    int flowLength = 160;
    int buttonColor, buttonHighlight;
    int marginTop = 30;
    int marginLeft = 30;
    int marginLeftRecipe = 50;
    int returnButtonX, returnButtonY;
    int[] buttonOver = new int[2];
    int[] buildingOver = new int[2];
    HashMap<String, PImage> itemImgs = new HashMap<String, PImage>();
    String[][] itemNames = new String[itemX][itemY];
    HashMap<String, PImage> buildingImgs = new HashMap<String, PImage>();
    String[][] buildingNames = new String[buildingX][buildingY];
    String blank = "Blank";
    String arrow = " --------> ";
    String iconPrefix = "icons/Icon_";
    String pngPostfix = ".png";
    String water = "Water";
    String sAcid = "Sulfuric_Acid";
    String rOil = "Refined_Oil";
    String cOil = "Crude_Oil";
    String back = "Back";
    boolean renderItemRecipe = false;
    boolean renderBuildingRecipe = false;
    boolean returnMenu = false;
    DspScheduler dspScheduler;

    public static void main(String[] args) {
        PApplet.main(ProcessingUI.class.getCanonicalName(), args);
    }

    public void settings() {
        size(1280, 720);
    }

    @Override
    public void setup() {
        dspScheduler = DspScheduler.createFromDefaultConfiguration();
        buildingMarginLeft = marginLeft;
        buildingMarginTop = marginTop + ((itemSize + 3) * itemY);
        producedItemMarginLeft = marginLeft + ((itemSize + 3) * itemX);
        producedItemMarginTop = marginTop;
        returnButtonX = (width - 100);
        returnButtonY = (height - 100);
        String[] items = loadStrings("ui_items.yml");
        String[] buildings = loadStrings("ui_buildings.yml");
        buttonHighlight = color(51);
        for (int i = 0; i < itemX; i++) {
            for (int j = 0; j < itemY; j++) {
                String item = items[i + (j * itemX)].trim();
                itemNames[i][j] = item;
                itemImgs.put(item, loadImage(iconPrefix + item + pngPostfix));
            }
        }
        for (int i = 0; i < buildingX; i++) {
            for (int j = 0; j < buildingY; j++) {
                String building = buildings[i + (j * buildingX)].trim();
                buildingNames[i][j] = building;
                buildingImgs.put(building, loadImage(iconPrefix + building + pngPostfix));
            }
        }
    }

    @Override
    public void draw() {
        update(mouseX, mouseY);
        background(0);
        if (renderItemRecipe) {
            renderItemRecipe(itemNames[buttonOver[0]][buttonOver[1]]);
            renderReturnButton();
        } else if (renderBuildingRecipe) {
            renderBuildingRecipe(buildingNames[buildingOver[0]][buildingOver[1]]);
            renderReturnButton();
        } else {
            renderItemHover();
            renderBuildingHover();
            renderAllItems();
            renderAllBuildings();
            renderItemCaption();
            renderBuildingCaption();
        }
    }

    void update(int x, int y) {
        buttonOver = getOverButtonNum();
        buildingOver = getOverBuildingNum();
        returnMenu = getOverReturnButton();
    }

    @Override
    public void mousePressed() {
        if (buttonOver[0] >= 0 && buttonOver[1] >= 0) {
            println("choose item:", buttonOver[0], buttonOver[1]);
            renderItemRecipe = true;
            renderBuildingRecipe = false;
        }
        if (buildingOver[0] >= 0 && buildingOver[1] >= 0) {
            println("choose building:", buildingOver[0], buildingOver[1]);
            renderBuildingRecipe = true;
            renderItemRecipe = false;
        }
        if (returnMenu) {
            println("go back to main page:");
            renderItemRecipe = false;
            renderBuildingRecipe = false;
        }
    }

    void renderItemRecipe(String itemName) {
        int treeDepth = 3;
        renderItem(itemName, marginLeftRecipe, height / 2);
        renderItem("Iron_Ingot", marginLeftRecipe + (itemSize + flowLength), height / treeDepth);
        noFill();
        stroke(255, 255, 255);

        bezier(marginLeftRecipe + itemSize, height / 2 + itemSize / 2, marginLeftRecipe + itemSize + flowLength / 2, height / 2 + itemSize / 2,
                marginLeftRecipe + itemSize + flowLength - flowLength / 2, height / treeDepth + itemSize / 2, marginLeftRecipe + itemSize + flowLength, height / treeDepth + itemSize / 2);

        renderItem("Copper_Ingot", marginLeftRecipe + flowLength + itemSize, height / treeDepth * 2);
        renderItem("Iron_Ore", marginLeftRecipe + 2 * (itemSize + flowLength), height / treeDepth);
        renderItem("Copper_Ore", marginLeftRecipe + 2 * (itemSize + flowLength), height / treeDepth * 2);
    }

    void renderBuildingRecipe(String buildingName) {
        // TODO: reuse renderItemRecipe
        renderItem("Iron_Ingot", marginLeftRecipe + (itemSize + flowLength), height / 3);
    }

    void renderReturnButton() {
        noStroke();
        if (getOverReturnButton()) {
            fill(255, 255, 255);
        } else {
            fill(255, 255, 255, 90);
        }
        rect(returnButtonX, returnButtonY, textWidth(back) + fontSize, fontSize + 5, 10);
        fill(33, 48, 47);
        textSize(fontSize);
        text(back, returnButtonX + fontSize / 2, returnButtonY + fontSize);
    }

    void renderItemCaption() {
        int i = buttonOver[0];
        int j = buttonOver[1];
        if (i < 0 || j < 0) {
            return;
        }
        String itemName = itemNames[i][j];
        if (!itemName.equals(blank)) {
            textSize(fontSize);
            fill(255, 255, 255);
            rect(marginTop + (i * itemSize + itemSize / 2), marginLeft + ((j + 1) * itemSize), textWidth(itemName), fontSize + 5);
            fill(0, 0, 0);
            text(itemName.replace("_", " "), marginTop + (i * itemSize + itemSize / 2), marginLeft + ((j + 1) * itemSize) + fontSize);
        }
    }

    void renderBuildingCaption() {
        int i = buildingOver[0];
        int j = buildingOver[1];
        if (i < 0 || j < 0) {
            return;
        }
        String buildingName = buildingNames[i][j];
        if (!buildingName.equals(blank)) {
            textSize(fontSize);
            fill(255, 255, 255);
            rect(buildingMarginLeft + (i * itemSize + itemSize / 2), buildingMarginTop + ((j + 1) * itemSize), textWidth(buildingName), fontSize + 5);
            fill(0, 0, 0);
            text(buildingName.replace("_", " "), buildingMarginLeft + (i * itemSize + itemSize / 2), buildingMarginTop + ((j + 1) * itemSize) + fontSize);
        }
    }

    void renderItemHover() {
        int i = buttonOver[0];
        int j = buttonOver[1];
        if (i < 0 || j < 0) {
            return;
        }
        String itemName = itemNames[i][j];
        if (!itemName.equals(blank)) {
            fill(buttonHighlight);
        } else {
            fill(buttonColor);
        }
        noStroke();
        rect(marginTop + (i * itemSize), marginLeft + (j * itemSize), itemSize, itemSize, recRad);
        Item item = dspScheduler.getState().getItem(itemName);
        renderProductList(item.getProducedItemsList(), item.getProducedBuildingsList());
        renderRecipes(dspScheduler.getState().getRecipeByName(item.getName()));
    }

    void renderRecipes(List<Recipe> recipes) {
        int indexY = 0;
        fill(255, 255, 255);
        text("Recipe:", producedItemMarginLeft, height / 2 - textWidth("1"));
        for (Recipe recipe : recipes) {
            int indexX = 0;
            for (Ingredient input : recipe.getInputsList()) {
                if (itemImgs.containsKey(input.getName())) {
                    renderItem(input.getName(), producedItemMarginLeft + indexX * itemSize, height / 2 + indexY * itemSize);
                } else if (buildingImgs.containsKey(input.getName())) {
                    renderBuilding(input.getName(), producedItemMarginLeft + indexX * itemSize, height / 2 + indexY * itemSize);
                }
                text(input.getQuantity(), producedItemMarginLeft + (indexX + 1) * itemSize, height / 2 + (indexY + 1) * itemSize);
                indexX++;
            }
            String craftTime = recipe.getCraftTime() + "s";
            text(craftTime, producedItemMarginLeft + indexX * itemSize + (int) ((textWidth(arrow) - textWidth(craftTime)) / 2.0), (int) (height / 2 + (indexY + 0.5) * itemSize));
            text(arrow, producedItemMarginLeft + indexX * itemSize, (int) (height / 2 + (indexY + 0.7) * itemSize));
            int outputMarginLeft = (int) (producedItemMarginLeft + textWidth(arrow));
            for (Ingredient output : recipe.getOutputsList()) {
                if (itemImgs.containsKey(output.getName())) {
                    renderItem(output.getName(), outputMarginLeft + indexX * itemSize, height / 2 + indexY * itemSize);
                } else if (buildingImgs.containsKey(output.getName())) {
                    renderBuilding(output.getName(), outputMarginLeft + indexX * itemSize, height / 2 + indexY * itemSize);
                }
                text(output.getQuantity(), outputMarginLeft + (indexX + 1) * itemSize, height / 2 + (indexY + 1) * itemSize);
                indexX++;
            }
            indexY++;
        }
    }

    void renderProductList(List<String> itemList, List<String> buildingList) {
        int indexColumn = 0;
        int indexRow = 0;
        fill(255, 255, 255);
        text("Produced Items:", producedItemMarginLeft, producedItemMarginTop + itemSize / 4);
        for (String producedItemName : itemList) {
            renderItem(producedItemName, producedItemMarginLeft + indexColumn * itemSize, itemSize * indexRow + producedItemMarginTop + itemSize / 2);
            indexColumn++;
            if (producedItemMarginLeft + indexColumn * itemSize + itemSize > width - marginLeft) {
                indexColumn = 0;
                indexRow++;
            }
        }
        indexColumn = 0;
        indexRow++;

        text("Produced Buildings:", producedItemMarginLeft, height / 4);
        for (String producedBuildingName : buildingList) {
            renderBuilding(producedBuildingName, producedItemMarginLeft + indexColumn * itemSize, (int) (itemSize * (indexRow - 0.75) + height / 4));
            indexColumn++;
            if (producedItemMarginLeft + indexColumn * itemSize + itemSize > width - marginLeft) {
                indexColumn = 0;
                indexRow++;
            }
        }
    }

    void renderBuildingHover() {
        int i = buildingOver[0];
        int j = buildingOver[1];
        if (i < 0 || j < 0) {
            return;
        }
        String buildingName = buildingNames[i][j];
        if (!buildingName.equals(blank)) {
            fill(buttonHighlight);
        } else {
            fill(buttonColor);
        }
        noStroke();
        rect(buildingMarginLeft + (i * itemSize), buildingMarginTop + (j * itemSize), itemSize, itemSize, recRad);
        Building building = dspScheduler.getState().getBuilding(buildingName);
        renderProductList(building.getProducedItemsList(), building.getProducedBuildingsList());
        renderRecipes(dspScheduler.getState().getRecipeByName(building.getName()));
    }

    void renderAllItems() {
        for (int i = 0; i < itemX; i++) {
            for (int j = 0; j < itemY; j++) {
                String itemName = itemNames[i][j];
                // render button image
                if (itemName.equals(water) || itemName.equals(sAcid) || itemName.equals(rOil) || itemName.equals(cOil)) {
                    image(itemImgs.get(itemName), marginTop + (i * itemSize) + itemLiquidX, marginLeft + (j * itemSize), itemLiquidSize, itemSize);
                } else {
                    image(itemImgs.get(itemName), marginTop + (i * itemSize), marginLeft + (j * itemSize), itemSize, itemSize);
                }
            }
        }
    }

    void renderAllBuildings() {
        for (int i = 0; i < buildingX; i++) {
            for (int j = 0; j < buildingY; j++) {
                String buildingName = buildingNames[i][j];
                // render button image
                image(buildingImgs.get(buildingName), buildingMarginLeft + (i * itemSize), buildingMarginTop + (j * itemSize), itemSize, itemSize);
            }
        }
    }

    void renderItem(String itemName, int x, int y) {
        // render button image
        if (itemName.equals(water) || itemName.equals(sAcid) || itemName.equals(rOil) || itemName.equals(cOil)) {
            image(itemImgs.get(itemName), x + itemLiquidX, y, itemLiquidSize, itemSize);
        } else {
            image(itemImgs.get(itemName), x, y, itemSize, itemSize);
        }
    }

    void renderBuilding(String buildingName, int x, int y) {
        image(buildingImgs.get(buildingName), x, y, itemSize, itemSize);
    }

    int[] getOverButtonNum() {
        if (renderItemRecipe || renderBuildingRecipe) {
            return buttonOver;
        }
        if (mouseX < marginLeft && mouseX > marginLeft + itemX * itemSize &&
                mouseY < marginTop && mouseY <= marginTop + itemY * itemSize) {
            return new int[]{-1, -1};
        }
        for (int i = 0; i < itemX; i++) {
            for (int j = 0; j < itemY; j++) {
                int x = marginTop + (i * itemSize);
                int y = marginLeft + (j * itemSize);
                if (mouseX >= x && mouseX <= x + itemSize &&
                        mouseY >= y && mouseY <= y + itemSize && !itemNames[i][j].equals(blank)) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

    int[] getOverBuildingNum() {
        if (renderBuildingRecipe || renderItemRecipe) {
            return buildingOver;
        }
        if (mouseX < buildingMarginLeft && mouseX > buildingMarginLeft + buildingX * itemSize &&
                mouseY < buildingMarginTop && mouseY <= buildingMarginTop + buildingY * itemSize) {
            return new int[]{-1, -1};
        }
        for (int i = 0; i < buildingX; i++) {
            for (int j = 0; j < buildingY; j++) {
                int x = buildingMarginLeft + (i * itemSize);
                int y = buildingMarginTop + (j * itemSize);
                if (mouseX >= x && mouseX <= x + itemSize &&
                        mouseY >= y && mouseY <= y + itemSize && !buildingNames[i][j].equals(blank)) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

    boolean getOverReturnButton() {
        if (renderItemRecipe || renderBuildingRecipe) {
            return mouseX >= returnButtonX && mouseX <= returnButtonX + itemSize &&
                    mouseY >= returnButtonY && mouseY <= returnButtonY + itemSize;
        }
        return false;
    }

}
