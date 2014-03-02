package org.bimserver.geometry;

public class Vector {
    // dot product (3D) which allows vector operations in arguments
    public static float dot(float[] u,float[] v) {
        return ((u[X] * v[X]) + (u[Y] * v[Y]) + (u[Z] * v[Z]));
    }
    public static float[] minus(float[] u, float[] v){
        return new float[]{u[X]-v[X],u[Y]-v[Y],u[Z]-v[Z]};
    }
    public static float[] addition(float[] u, float[] v){
        return new float[]{u[X]+v[X],u[Y]+v[Y],u[Z]+v[Z]};
    }
    //scalar product
    public static float[] scalarProduct(float r, float[] u){
        return new float[]{u[X]*r,u[Y]*r,u[Z]*r};
    }
    // (cross product)
    public static float[] crossProduct(float[] u, float[] v){
        return new float[]{(u[Y]*v[Z]) - (u[Z]*v[Y]),(u[Z]*v[X]) - (u[X]*v[Z]),(u[X]*v[Y]) - (u[Y]*v[X])};
    }
    //mangnatude or length
    public static float length(float[] u){
        return (float) Math.abs(Math.sqrt((u[X] *u[X]) + (u[Y] *u[Y]) + (u[Z] *u[Z])));
    }
 
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;
	public static void normalize(float[] rayDir) {
		float l = length(rayDir);
		rayDir[0] = rayDir[0] / l;
		rayDir[1] = rayDir[1] / l;
		rayDir[2] = rayDir[2] / l;
	}

	public static void dump(float[] v) {
		System.out.println(v[0] + ", " + v[1] + ", " + v[2] + (v.length == 4 ? (", " + v[3]) : ""));
	}

	public static void dump(String pre, float[] v) {
		System.out.println(pre + ": " + v[0] + ", " + v[1] + ", " + v[2] + (v.length == 4 ? (", " + v[3]) : ""));
	}
}