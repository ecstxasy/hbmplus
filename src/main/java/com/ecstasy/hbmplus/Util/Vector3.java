package com.ecstasy.hbmplus.Util;

import net.minecraftforge.common.util.ForgeDirection;

public class Vector3 extends Vector2 {
    public float z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.magnitude = MathUtil.sqrt(MathUtil.pow(x, 2) + MathUtil.pow(y, 2) + MathUtil.pow(z, 2));
    }

    public Vector3(float x) {
        this(x, 0, 0);
    }

    public Vector3(float x, float y) {
        this(x, y, 0);
    }

    public Vector3() {
        this(0, 0, 0);
    }

    public static Vector3 fromDirection(ForgeDirection direction) {
        return new Vector3(direction.offsetX, direction.offsetY, direction.offsetZ);
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 sub(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    public Vector3 mul(float other) {
        return new Vector3(x * other, y * other, z * other);
    }

    public Vector3 mul(Vector3 other) {
        return new Vector3(x * other.x, y * other.y, z * other.z);
    }

    public float dot(Vector3 other) {
        return (x * other.x) + (y * other.y) + (z * other.z);
    }

    public Vector3 cross(Vector3 other) {
        float crossX = this.y * other.z - this.z * other.y;
        float crossY = this.z * other.x - this.x * other.z;
        float crossZ = this.x * other.y - this.y * other.x;
        return new Vector3(crossX, crossY, crossZ);
    }

    public Vector3 div(float other) {

        if (other == 0) {
            throw new ArithmeticException("Cannon divide by zero.");
        }

        return new Vector3(x / other, y / other, z / other);
    }

    public Vector3 negate() {
        return new Vector3(-x, -y, -z);
    }

    public Vector3 unit() {
        if (magnitude == 1) {
            return new Vector3(x, y, z);
        }

        return this.div(this.magnitude);
    }

    @Override public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
