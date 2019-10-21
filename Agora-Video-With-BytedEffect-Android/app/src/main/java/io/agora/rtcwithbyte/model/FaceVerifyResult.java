package io.agora.rtcwithbyte.model;

public class FaceVerifyResult {

    private double similarity;
    private long cost;
    private int validFaceNum;

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public int getValidFaceNum() {
        return validFaceNum;
    }

    public void setValidFaceNum(int validFaceNum) {
        this.validFaceNum = validFaceNum;
    }
}
