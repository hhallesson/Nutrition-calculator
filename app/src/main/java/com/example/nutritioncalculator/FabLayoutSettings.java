package com.example.nutritioncalculator;

public class FabLayoutSettings {

    private float positionX = -1f;
    private float positionY = -1f;
    private int sizeDp = 56;
    private int baseLayoutWidth;
    private int baseLayoutHeight;

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public int getSizeDp() {
        return sizeDp;
    }

    public void setSizeDp(int sizeDp) {
        this.sizeDp = sizeDp;
    }

    public int getBaseLayoutWidth() {
        return baseLayoutWidth;
    }

    public void setBaseLayoutWidth(int baseLayoutWidth) {
        this.baseLayoutWidth = baseLayoutWidth;
    }

    public int getBaseLayoutHeight() {
        return baseLayoutHeight;
    }

    public void setBaseLayoutHeight(int baseLayoutHeight) {
        this.baseLayoutHeight = baseLayoutHeight;
    }

    public boolean hasCustomPosition() {
        return positionX >= 0f && positionY >= 0f;
    }

    public FabLayoutSettings copy() {
        FabLayoutSettings copy = new FabLayoutSettings();
        copy.positionX = positionX;
        copy.positionY = positionY;
        copy.sizeDp = sizeDp;
        copy.baseLayoutWidth = baseLayoutWidth;
        copy.baseLayoutHeight = baseLayoutHeight;
        return copy;
    }
}
