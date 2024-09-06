package com.ecstasy.hbmplus.Util;

import net.minecraftforge.common.util.ForgeDirection;

public class CFrame {
    public Vector3 position = new Vector3();

    protected Vector3 lookVector = new Vector3(0, 0, 1);
    protected Vector3 rightVector = new Vector3(1, 0, 0);
    protected Vector3 upVector = new Vector3(0, 1, 0);

    protected float[] matrix = new float[] {
        1, 0, 0,
        0, 1, 0,
        0, 0, 1
    };

    private static int rightRow = 1;
    private static int upRow = 2;
    private static int lookRow = 3;

    public CFrame() {}

    public CFrame(Vector3 pos) {
        setPosition(pos);
    }

    public CFrame(float[] matrix) {
        setMatrix(matrix);
    }

    public CFrame(Vector3 pos, float[] matrix) {
        setCFrame(pos, matrix);
    }

    public CFrame(float x, float y, float z) {
        this(new Vector3(x, y, z));
    }

    public CFrame mul(CFrame other) {
        Vector3 newPos = this.position.add(
            this.rightVector.mul(other.position.x))
            .add(this.upVector.mul(other.position.y))
            .add(this.lookVector.mul(other.position.z));
    
        float[] newMatrix = new float[] {
            this.matrix[0] * other.matrix[0] + this.matrix[1] * other.matrix[3] + this.matrix[2] * other.matrix[6],
            this.matrix[0] * other.matrix[1] + this.matrix[1] * other.matrix[4] + this.matrix[2] * other.matrix[7],
            this.matrix[0] * other.matrix[2] + this.matrix[1] * other.matrix[5] + this.matrix[2] * other.matrix[8],
    
            this.matrix[3] * other.matrix[0] + this.matrix[4] * other.matrix[3] + this.matrix[5] * other.matrix[6],
            this.matrix[3] * other.matrix[1] + this.matrix[4] * other.matrix[4] + this.matrix[5] * other.matrix[7],
            this.matrix[3] * other.matrix[2] + this.matrix[4] * other.matrix[5] + this.matrix[5] * other.matrix[8],
    
            this.matrix[6] * other.matrix[0] + this.matrix[7] * other.matrix[3] + this.matrix[8] * other.matrix[6],
            this.matrix[6] * other.matrix[1] + this.matrix[7] * other.matrix[4] + this.matrix[8] * other.matrix[7],
            this.matrix[6] * other.matrix[2] + this.matrix[7] * other.matrix[5] + this.matrix[8] * other.matrix[8],
        };
    
        return new CFrame(newPos, newMatrix);
    }

    public CFrame inverse() {
        float[] inverse = new float[] {
            matrix[0], matrix[3], matrix[6],
            matrix[1], matrix[4], matrix[7],
            matrix[2], matrix[5], matrix[8]
        };

        float invPx = -(position.x * inverse[0] + position.y * inverse[1] + position.z * inverse[2]);
        float invPy = -(position.x * inverse[3] + position.y * inverse[4] + position.z * inverse[5]);
        float invPz = -(position.x * inverse[6] + position.y * inverse[7] + position.z * inverse[8]);

        Vector3 invP = new Vector3(invPx, invPy, invPz);

        return new CFrame(invP, inverse);
    }

    public static CFrame fromMatrix(Vector3 pos, Vector3 lookVector, Vector3 rightVector, Vector3 upVector) {
        return new CFrame(pos, new float[] {
            rightVector.x, upVector.x, lookVector.x,
            rightVector.y, upVector.y, lookVector.y,
            rightVector.z, upVector.z, lookVector.z
        });
    }

    public static CFrame fromMatrix(Vector3 pos, Vector3 lookVector, Vector3 rightVector) {
        Vector3 upVector = lookVector.cross(rightVector).unit();
        return fromMatrix(pos, lookVector, rightVector, upVector);
    }

    public static CFrame lookAt(Vector3 pos, Vector3 lookAt) {
        Vector3 lookVector = pos.sub(lookAt).unit();
        Vector3 rightVector = new Vector3(0, 1, 0).cross(lookVector).unit();
        Vector3 upVector = lookVector.cross(rightVector).unit();

        return fromMatrix(pos, lookVector, rightVector, upVector);
    }

    public static CFrame lookAlong(Vector3 pos, Vector3 dir) {
        return lookAt(pos, pos.add(dir.unit()));
    }

    public static CFrame lookAlong(Vector3 pos, ForgeDirection dir) {
        Vector3 lookAt = pos.add(Vector3.fromDirection(dir));
        return lookAt(pos, lookAt);
    }

    public static CFrame lookAlong(float x, float y, float z, ForgeDirection dir) {
        Vector3 lookAt = new Vector3(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
        return lookAt(new Vector3(x, y, z), lookAt);
    }

    private void setCFrame(Vector3 pos, float[] matrix) {
        setPosition(pos);
        setMatrix(matrix);
    }

    private void setMatrix(float[] matrix) {
        this.matrix = matrix;

        this.lookVector = new Vector3(matrix[lookRow * 3 - 3], matrix[lookRow * 3 - 2], matrix[lookRow * 3 - 1]);
        this.rightVector = new Vector3(matrix[rightRow * 3 - 3], matrix[rightRow * 3 - 2], matrix[rightRow * 3 - 1]);
        this.upVector = new Vector3(matrix[upRow * 3 - 3], matrix[upRow * 3 - 2], matrix[upRow * 3 - 1]);

    }

    private void setPosition(Vector3 pos) {
        position = pos;
    }
}
