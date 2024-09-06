package com.ecstasy.hbmplus.Util;

public class Vector2 {
    public float x;
    public float y;

    protected float magnitude;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
        this.magnitude = MathUtil.sqrt(MathUtil.pow(x, 2) + MathUtil.pow(y, 2));
    }

    public Vector2(float x) {
        this(x, 0);
    }

    public Vector2() {
        this(0, 0);
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(x + other.x, y + other.y);
    }

    public Vector2 sub(Vector2 other) {
        return new Vector2(x - other.x, y - other.y);
    }

    public Vector2 mul(float other) {
        return new Vector2(x * other, y * other);
    }

    public Vector2 mul(Vector2 other) {
        return new Vector2(x * other.x, y * other.y);
    }

    public Vector2 div(float other) {

        if (other == 0) {
            throw new ArithmeticException("Cannon divide by zero.");
        }

        return new Vector2(x / other, y / other);
    }

    public Vector2 negate() {
        return new Vector2(-x, -y);
    }

    public float dot(Vector2 other) {
        return (x * other.x) + (y * other.y);
    }

    public float magnitude() {
        return magnitude;
    }

    public Vector2 unit() {
        if (magnitude == 1) {
            return new Vector2(x, y);
        }

        return this.div(this.magnitude);
    }

    @Override public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
