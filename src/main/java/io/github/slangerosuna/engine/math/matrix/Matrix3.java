package io.github.slangerosuna.engine.math.matrix;

public class Matrix3 {
    public static final int SIZE = 3;
    private float[] elements = new float[SIZE * SIZE];

    public static Matrix3 Multiply(Matrix3 a, Matrix3 b) {
		Matrix3 result = Matrix3.identity();

		for(int x = 0; x < SIZE; x++) {
			for(int y = 0; y < SIZE; y++) {
				result.set(x, y, a.get(x, 0) * b.get(0, y) +
								 a.get(x, 1) * b.get(1, y) +
								 a.get(x, 2) * b.get(2, y));
			}
		}

		return result;
	}

    public static Matrix3 identity() {
		Matrix3 result = new Matrix3();
		for(int x = 0; x < SIZE; x++) {
			for(int y = 0; y < SIZE; y++) {
				result.set(x, y, 0);
			}
		}

		result.set(0, 0, 1);
		result.set(1, 1, 1);
		result.set(2, 2, 1);

		return result;
	}
    public float get(int x, int y) {
		return elements[x + (y * SIZE)];
	}

	public void set(int x, int y, float value) {
		elements[x + (y * SIZE)] = value;
	}
	public float[] getAll(){
		return elements;
	}
    public float determinant(){
        float result = 0;

        for(int i = 0; i < SIZE; i++){
            result += get(i, 0) * subMatrix(i, 0).determinant();
        }

        return result;
    }
    public Matrix2 subMatrix(int i, int j){
        Matrix2 result = Matrix2.identity();
        for(int x = 0; x < SIZE; x++) { if(x != i) { for(int y = 0; y < SIZE; y++) { if(y != j) { result.set(x > i ? x - 1 : x, y > j ? y - 1 : y, get(x, y)); } } } }
        return result;
    }
}
