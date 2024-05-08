package io.github.slangerosuna.engine.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import io.github.slangerosuna.engine.render.Mesh;
import io.github.slangerosuna.engine.render.Vertex;
import io.github.slangerosuna.engine.math.vector.Vector2;
import io.github.slangerosuna.engine.math.vector.Vector3;

public class ObjLoader {
    private static HashMap<String, Mesh> idMap = new HashMap<String, Mesh>();
    public static void removeMesh(String path) {
        idMap.remove(path);
    }

    public static Mesh loadObj(String path) {
        if (idMap.containsKey(path)) {
            var mesh = idMap.get(path);
            mesh.incrementRefCount();
            return mesh;
        }
        var file = FileUtils.loadAsString(path);
        var lines = file.split("\n");

        Vector3[] positions = Arrays.stream(lines).filter(line -> line.startsWith("v ")).map(x -> getVertex(x)).toArray(Vector3[]::new);
        Vector2[] uvs = Arrays.stream(lines).filter(line -> line.startsWith("vt ")).map(x -> getUV(x)).toArray(Vector2[]::new);
        Vector3[] normals = Arrays.stream(lines).filter(line -> line.startsWith("vn ")).map(x -> getNormal(x)).toArray(Vector3[]::new);
        Face[] faces = Arrays.stream(lines).filter(line -> line.startsWith("f ")).map(x -> getFace(x)).toArray(Face[]::new);

        var vertices = new ArrayList<Vertex>();
        var indices = new ArrayList<Integer>();

        var vertexIndexMap = new HashMap<Vertex, Integer>();

        for (int i = 0; i < faces.length; i++) {
            var face = faces[i];
            if (face.vertexIndices.length == 3) {
                for (int j = 0; j < face.vertexIndices.length; j++) {
                    var vertexIndex = face.vertexIndices[j] - 1;
                    var uvIndex = face.uvIndices[j] - 1;
                    var normalIndex = face.normalIndices[j] - 1;

                    System.out.println(vertexIndex + " " + uvIndex + " " + normalIndex);

                    var vertex = new Vertex(positions[vertexIndex], uvs[uvIndex], normals[normalIndex]);

                    if (vertexIndexMap.get(vertex) != null) {
                        indices.add(vertexIndexMap.get(vertex));
                    } else {
                        vertices.add(vertex);
                        vertexIndexMap.put(vertex, vertices.size() - 1);
                        indices.add(vertices.size() - 1);
                    }
                }
            } else if (face.vertexIndices.length == 4) {
                // First triangle
                for (int j = 0; j < 3; j++) {
                    var vertexIndex = face.vertexIndices[j] - 1;
                    var uvIndex = face.uvIndices[j] - 1;
                    var normalIndex = face.normalIndices[j] - 1;

                    var vertex = new Vertex(positions[vertexIndex], uvs[uvIndex], normals[normalIndex]);

                    if (vertexIndexMap.get(vertex) != null) {
                        indices.add(vertexIndexMap.get(vertex));
                    } else {
                        vertices.add(vertex);
                        vertexIndexMap.put(vertex, vertices.size() - 1);
                        indices.add(vertices.size() - 1);
                    }
                }

                // Second triangle
                for (int j = 0; j < 3; j++) {
                    var vertexIndex = face.vertexIndices[j == 0 ? 0 : j + 1] - 1;
                    var uvIndex = face.uvIndices[j == 0 ? 0 : j + 1] - 1;
                    var normalIndex = face.normalIndices[j == 0 ? 0 : j + 1] - 1;

                    var vertex = new Vertex(positions[vertexIndex], uvs[uvIndex], normals[normalIndex]);

                    if (vertexIndexMap.get(vertex) != null) {
                        indices.add(vertexIndexMap.get(vertex));
                    } else {
                        vertices.add(vertex);
                        vertexIndexMap.put(vertex, vertices.size() - 1);
                        indices.add(vertices.size() - 1);
                    }
                }
            } else {
                throw new RuntimeException("Face is not a triangle or a quad");
            }
        }

        var mesh = new Mesh(vertices.toArray(Vertex[]::new), indices.stream().mapToInt(x -> x).toArray(), path);
        mesh.create();
        idMap.put(path, mesh);
        return mesh;
    }

    private static Vector3 getVertex(String line) {
        var tokens = line.split(" ");
        var x = Float.parseFloat(tokens[1]);
        var y = Float.parseFloat(tokens[2]);
        var z = Float.parseFloat(tokens[3]);
    
        return new Vector3(x, y, z);
    }

    private static Vector2 getUV(String line) {
        var tokens = line.split(" ");
        var x = Float.parseFloat(tokens[1]);
        var y = Float.parseFloat(tokens[2]);
    
        return new Vector2(x, y);
    }

    private static Vector3 getNormal(String line) {
        var tokens = line.split(" ");
        var x = Float.parseFloat(tokens[1]);
        var y = Float.parseFloat(tokens[2]);
        var z = Float.parseFloat(tokens[3]);
    
        return new Vector3(x, y, z);
    }

    private static Face getFace(String line) {
        var vertices = Arrays.stream(line.split(" ")).map(vertex -> vertex.split("/")).toArray(String[][]::new);
        var vertexIndices = new int[vertices.length - 1];
        var uvIndices = new int[vertices.length - 1];
        var normalIndices = new int[vertices.length - 1];
 
        for (int i = 1; i < vertices.length; i++) {
            vertexIndices[i - 1] = Integer.parseInt(vertices[i][0]);
            uvIndices[i - 1] = Integer.parseInt(vertices[i][1]);
            normalIndices[i - 1] = Integer.parseInt(vertices[i][2]);
        }

        return new Face(vertexIndices, uvIndices, normalIndices);
    }
}

class Face {
    public int[] vertexIndices;
    public int[] uvIndices;
    public int[] normalIndices;

    public Face(int[] vertexIndices, int[] uvIndices, int[] normalIndices) {
        this.vertexIndices = vertexIndices;
        this.uvIndices = uvIndices;
        this.normalIndices = normalIndices;
    }
}