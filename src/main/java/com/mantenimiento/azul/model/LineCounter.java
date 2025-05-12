package com.mantenimiento.azul.model;

public class LineCounter {
    private int added;
    private int removed;

    public LineCounter() {
        this.added = 0;
        this.removed = 0;
    }

    public int getAdded() {
        return added;
    }

    public void setAdded(int added) {
        this.added = added;
    }

    public int getRemoved() {
        return removed;
    }

    public void setRemoved(int removed) {
        this.removed = removed;
    }

    public void incrementAdded() {
        this.added++;
    }

    public void incrementRemoved() {
        this.removed++;
    }

    public void addAdded(int added) {
        this.added += added;
    }

    public void addRemoved(int removed) {
        this.removed += removed;
    }
}

