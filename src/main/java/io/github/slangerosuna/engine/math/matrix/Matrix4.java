package io.github.slangerosuna.engine.math.matrix;

import java.util.Arrays;

import io.github.slangerosuna.engine.math.vector.Vector3;

public class Matrix4 {
	public static final int SIZE = 4;
	private float[] elements = new float[SIZE * SIZE];

	public static Matrix4 transform(Vector3 pos, Vector3 rot, Vector3 scale) {
		Matrix4 translationMatrix = Matrix4.translation(pos);
		Matrix4 scaleMatrix = Matrix4.scale(scale);

		Matrix4 rotMatrix = rotation(rot);

		return Matrix4.Multiply(translationMatrix, Matrix4.Multiply(rotMatrix, scaleMatrix));
	}
	public static Matrix4 transform(Vector3 pos, Matrix4 rotMatrix, Vector3 scale) {
		Matrix4 translationMatrix = Matrix4.translation(pos);
		Matrix4 scaleMatrix = Matrix4.scale(scale);

		return Matrix4.Multiply(translationMatrix, Matrix4.Multiply(rotMatrix, scaleMatrix));
	}
	public static Matrix4 rotation(Vector3 rot){
		Matrix4 rotXMatrix = Matrix4.rotate(rot.x, new Vector3(1, 0, 0));
		Matrix4 rotYMatrix = Matrix4.rotate(rot.y, new Vector3(0, 1, 0));
		Matrix4 rotZMatrix = Matrix4.rotate(rot.z, new Vector3(0, 0, 1));

		return Matrix4.Multiply(rotXMatrix, Matrix4.Multiply(rotYMatrix, rotZMatrix));
	}
	public static Matrix4 identity() {
		Matrix4 result = new Matrix4();
		for(int x = 0; x < SIZE; x++) {
			for(int y = 0; y < SIZE; y++) {
				result.set(x, y, 0);
			}
		}

		result.set(0, 0, 1);
		result.set(1, 1, 1);
		result.set(2, 2, 1);
		result.set(3, 3, 1);

		return result;
	}

	public static Matrix4 translation(Vector3 translate) {
		Matrix4 result = Matrix4.identity();

		result.set(0, 3, translate.x);
		result.set(1, 3, translate.y);
		result.set(2, 3, translate.z);

		return result;
	}

	public static Matrix4 rotate(float angle, Vector3 axis) {
		Matrix4 result = Matrix4.identity();

		float cos = (float) Math.cos(Math.toRadians(angle));
		float sin = (float) Math.sin(Math.toRadians(angle));
		float C = 1 - cos;

		result.set(0, 0, cos + axis.x * axis.x * C);
		result.set(0, 1, axis.x * axis.y * C - axis.z * sin);
		result.set(0, 2, axis.x * axis.z * C + axis.y * sin);
		result.set(1, 0, axis.y * axis.x * C + axis.z * sin);
		result.set(1, 1, cos + axis.y * axis.y * C);
		result.set(1, 2, axis.y * axis.z * C - axis.x * sin);
		result.set(2, 0, axis.z * axis.x * C - axis.y * sin);
		result.set(2, 1, axis.z * axis.z * C + axis.x * sin);
		result.set(2, 2, cos + axis.z * axis.x * C);

		return result;
	}

	public static Matrix4 scale(Vector3 scalar) {
		Matrix4 result = Matrix4.identity();

		result.set(0, 0, scalar.x);
		result.set(1, 1, scalar.y);
		result.set(2, 2, scalar.z);

		return result;
	}

	public static Matrix4 projection(float fov, float aspect, float near, float far) {
		Matrix4 result = Matrix4.identity();

		float tanFOV = (float) Math.tan(Math.toRadians(fov / 2));
		float range = far - near;

		result.set(0, 0, 1.0f / (aspect * tanFOV));
		result.set(1, 1, 1.0f / tanFOV);
		result.set(2, 2, -((far + near) / range));
		result.set(3, 2, -1.0f);
		result.set(2, 3, -((2 * far * near) / range));
		result.set(3, 3, 0.0f);

		return result;
	}

	public static Matrix4 view(Vector3 position, Vector3 rotation) {
		Matrix4 result = Matrix4.identity();

		Vector3 negative = new Vector3(-position.x, -position.y, -position.z);
		Vector3 negRot = new Vector3(-rotation.x, -rotation.y, -rotation.z);
		Matrix4 translationMatrix = Matrix4.translation(negative);
		Matrix4 rotXMatrix = Matrix4.rotate(negRot.x, new Vector3(1, 0, 0));
		Matrix4 rotYMatrix = Matrix4.rotate(negRot.y, new Vector3(0, 1, 0));
		Matrix4 rotZMatrix = Matrix4.rotate(negRot.z, new Vector3(0, 0, 1));

		Matrix4 rotationMatrix = Matrix4.Multiply(rotZMatrix, Matrix4.Multiply(rotXMatrix, rotYMatrix));

		result = Matrix4.Multiply(rotationMatrix, translationMatrix);

		return result;
	}

	public static Matrix4 Multiply(Matrix4 a, Matrix4 b) {
		Matrix4 result = Matrix4.identity();

		for(int x = 0; x < SIZE; x++) {
			for(int y = 0; y < SIZE; y++) {
				result.set(x, y, a.get(x, 0) * b.get(0, y) +
								 a.get(x, 1) * b.get(1, y) +
								 a.get(x, 2) * b.get(2, y) +
								 a.get(x, 3) * b.get(3, y));
			}
		}

		return result;
	}
	public Vector3 getRelativeAxis(Vector3 objectiveAxis){
		Matrix4 axis = Matrix4.translation(objectiveAxis);
		return Matrix4.Multiply(axis, inverted()).getTranslation();
	}
	public Vector3 getTranslation(){
		return new Vector3(get(0, 3), get(1, 3), get(2, 3));
	}
	public Vector3 getScale(){
		return new Vector3(get(0, 0), get(1, 1), get(2, 2));
	}
	public Matrix4 inverted(){
		//Create a matrix to receive inverse matrix data
    	Matrix4 reMatrix = Matrix4.identity();
        //Get the value of the determinant of the original matrix
        float value = determinant();
        //Judge whether the value of matrix determinant is zero
        if(Math.abs(value) <= 10e-6) {
            System.out.println("The matrix is irreversible!");
            return this;
        }
        //Primitive matrix mat Assignment divided by the value of the original determinant value Inverse matrix
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                reMatrix.set(i, j, getWithMatrix(this).get(i, j) / value);
            }
        }
        return reMatrix;
	}
	public float determinant(){
        float result = 0;

        for(int i = 0; i < SIZE; i++){
            result += get(i, 0) * subMatrix(i, 0).determinant();
        }

        return result;
    }
    public Matrix3 subMatrix(int i, int j){
        Matrix3 result = Matrix3.identity();
        for(int x = 0; x < SIZE; x++) { if(x != i) { for(int y = 0; y < SIZE; y++) { if(y != j) { result.set(x > i ? x - 1 : x, y > j ? y - 1 : y, get(x, y)); } } } }
        return result;
    }
	public static Matrix4 getWithMatrix(Matrix4 mat) {
		//Create a matrix to store the values of adjoint matrix
		Matrix4 withMatrix = Matrix4.identity();
		//ergodic withMatrix Store corresponding mat Value
	    for (int i = 0; i < SIZE; i++) {
	        for (int j = 0; j < SIZE; j++) {
	            float temp = (float)Math.pow(-1, i+j) * mat.subMatrix(j, i).determinant();
	            if(Math.abs(temp) <= 10e-6) temp = 0;
	            withMatrix.set(i, j,temp);
	        }
	    }
	    //Return result
		return withMatrix;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = result * prime + Arrays.hashCode(elements);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Matrix4 other = (Matrix4) obj;
		if (!Arrays.equals(elements, other.elements))
			return false;
		return true;
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

    @Override
	public String toString() {
        StringBuilder res = new StringBuilder();
		for(int y = 0; y < SIZE; y++){
            res.append("|");
			for(int x = 0; x < SIZE; x++){
                res.append(get(x, y)).append("|");
			}
            res.append("\n");
		}
        return res.toString();
	}
}
