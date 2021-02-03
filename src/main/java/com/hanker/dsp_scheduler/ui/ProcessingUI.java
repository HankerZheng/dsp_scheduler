package com.hanker.dsp_scheduler.ui;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.HashMap;

public class ProcessingUI extends PApplet {
    int itemSize = 68;     // Diameter of rect
    int itemX = 12;
    int itemY = 7;
    int itemLiquidSize = 38;
    int itemLiquidX = 15;
    int recRad = 7;
    int fontSize = 15;
    int buttonColor, buttonHighlight;
    int marginTop, marginLeft;
    int returnButtonX, returnButtonY;
    int[] buttonOver = new int[2];
    HashMap<String, PImage> itemImgs = new HashMap<String, PImage>();
    String[][] itemNames = new String[itemX][itemY];
    String blank = "Blank";
    String iconPrefix = "icons/Icon_";
    String pngPostfix = ".png";
    String water = "Water";
    String sAcid = "Sulfuric_Acid";
    String rOil = "Refined_Oil";
    String cOil = "Crude_Oil";
    String back = "Back";
    boolean renderRecipe = false;
    boolean returnMenu = false;

    public void settings() {
        size(1280, 720);
    }

    @Override
    public void setup() {
        marginTop = (width - (itemSize * itemX)) / 2;
        marginLeft = (height - (itemSize * itemY)) / 2;
        returnButtonX = (width - 100);
        returnButtonY = (height - 100);
        String[] items = loadStrings("ui_items.yml");
        buttonHighlight = color(51);
        for (int i = 0; i < itemX; i++) {
            for (int j = 0; j < itemY; j++) {
                String item = items[i + (j * itemX)].trim();
                itemNames[i][j] = item;
                itemImgs.put(item, loadImage(iconPrefix + item + pngPostfix));
            }
        }
    }

    @Override
    public void draw() {
        update(mouseX, mouseY);
        background(0);
        if (renderRecipe) {
            renderRecipe(itemNames[buttonOver[0]][buttonOver[1]]);
            renderReturnButton();
        } else {
            renderHover();
            renderAllItems();
            renderCaption();
        }
    }

    void update(int x, int y) {
        buttonOver = getOverButtonNum();
        returnMenu = getOverReturnButton();
    }

    @Override
    public void mousePressed() {
        if (buttonOver[0] >= 0 && buttonOver[1] >= 0) {
            println("choose item:", buttonOver[0], buttonOver[1]);
            renderRecipe = true;
        }
        if (returnMenu) {
            renderRecipe = false;
        }
    }

    void renderRecipe(String itemName) {
        int treeDepth = 3;
        renderItem(itemName, height - treeDepth / 2, marginLeft);
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

    void renderCaption() {
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

    void renderHover() {
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

    void renderItem(String itemName, int x, int y) {
        // render button image
        if (itemName.equals(water) || itemName.equals(sAcid) || itemName.equals(rOil) || itemName.equals(cOil)) {
            image(itemImgs.get(itemName), x + itemLiquidX, y, itemLiquidSize, itemSize);
        } else {
            image(itemImgs.get(itemName), x, y, itemSize, itemSize);
        }
    }

    int[] getOverButtonNum() {
        if (renderRecipe) {
            return buttonOver;
        }
        for (int i = 0; i < itemX; i++) {
            for (int j = 0; j < itemY; j++) {
                int x = marginTop + (i * itemSize);
                int y = marginLeft + (j * itemSize);
                if (mouseX >= x && mouseX <= x + itemSize &&
                        mouseY >= y && mouseY <= y + itemSize && !itemNames[i][j].equals(blank)){
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

    boolean getOverReturnButton() {
        if (renderRecipe) {
            return mouseX >= returnButtonX && mouseX <= returnButtonX + itemSize &&
                    mouseY >= returnButtonY && mouseY <= returnButtonY + itemSize;
        }
        return false;
    }

    public static void main(String[] args) {
        PApplet.main(ProcessingUI.class.getCanonicalName(), args);
    }

}
