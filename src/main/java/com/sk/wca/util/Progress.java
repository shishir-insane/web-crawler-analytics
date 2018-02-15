/**
 *
 * Progress.java
 * com.sk.wca.util
 * web-crawler-analytics
 *
 * Copyright 2018 - Shishir Kumar
 */
package com.sk.wca.util;

public class Progress {

    private double progressMade;
    private int total;
    private int completed;

    /**
     * Instantiates a new progress.
     *
     * @param total
     *            the total
     */
    public Progress(final int total) {
        progressMade = 0.0;
        completed = 0;
        this.total = total;
    }

    /**
     * Gets the progress made.
     *
     * @return the progress made
     */
    public double getProgressMade() {
        return progressMade;
    }

    /**
     * Sets the progress made.
     *
     * @param progressMade
     *            the new progress made
     */
    public void setProgressMade(final double progressMade) {
        this.progressMade = progressMade;
    }

    /**
     * Gets the total.
     *
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * Sets the total.
     *
     * @param total
     *            the new total
     */
    public void setTotal(final int total) {
        this.total = total;
    }

    /**
     * Gets the completed.
     *
     * @return the completed
     */
    public int getCompleted() {
        return completed;
    }

    /**
     * Sets the completed.
     *
     * @param completed
     *            the new completed
     */
    public void setCompleted(final int completed) {
        this.completed = completed;
    }

}
