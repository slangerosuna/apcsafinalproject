package io.github.slangerosuna.engine.utils;

import io.github.slangerosuna.engine.render.Mesh;
import io.github.slangerosuna.engine.render.Vertex;
import io.github.slangerosuna.engine.math.vector.Vector3;

public class ObjLoader {
    public static Mesh loadObj(String path) {
        var file = FileUtils.loadAsString(path);
        var lines = file.split("\n");

        var vertices = lines.stream().filter(line -> line.startsWith("v ")).map(getVertex).toArray(Vector3[]::new);
        var uvs = lines.stream().filter(line -> line.startsWith("vt ")).map(getUV).toArray(Vector3[]::new);
        var normals = lines.stream().filter(line -> line.startsWith("vn ")).map(getNormal).toArray(Vector3[]::new);
        var faces = lines.stream().filter(line -> line.startsWith("f ")).map(getFace).toArray(String[]::new);

        var vertices = new ArrayList<Vertex>();
        var indices = new int[faces.length * 3]

        for (int i = 0; i < faces.length; i++) {
            var face = faces[i];
            for (int j = 0; j < face.vertexIndices.length; j++) {
                var vertexIndex = face.vertexIndices[j] - 1;
                var uvIndex = face.uvIndices[j] - 1;
                var normalIndex = face.normalIndices[j] - 1;

                var vertex = new Vertex(vertices[vertexIndex], uvs[uvIndex], normals[normalIndex]);

                vertices.stream().filter(vertex.equals).findFirst().ifPresentOrElse(
                    existingVertex -> indices[i * 3 + j] = vertices.indexOf(existingVertex),
                    () -> {
                        vertices.add(vertex);
                        indices[i * 3 + j] = vertices.size() - 1;
                    }
                );
            }
        }

        return new Mesh(vertices.toArray(Vertex[]::new), indices);
    }

    private static Vector3 getVertex(String line) {
        var tokens = line.split(" ");
        var x = Float.parseFloat(tokens[1]);
        var y = Float.parseFloat(tokens[2]);
        var z = Float.parseFloat(tokens[3]);
    
        return new Vector3(x, y, z);
    }

    private static Vector3 getUV(String line) {
        var tokens = line.split(" ");
        var x = Float.parseFloat(tokens[1]);
        var y = Float.parseFloat(tokens[2]);
    
        return new Vector3(x, y, 0);
    }

    private static Vector3 getNormal(String line) {
        var tokens = line.split(" ");
        var x = Float.parseFloat(tokens[1]);
        var y = Float.parseFloat(tokens[2]);
        var z = Float.parseFloat(tokens[3]);
    
        return new Vector3(x, y, z);
    }

    private static Face getFace(String line) {
        var vertices = line.split(" ").stream().map(vertex -> vertex.split("/")).toArray(String[][]::new);
        var vertexIndices = new int[vertices.length];
        var uvIndices = new int[vertices.length];
        var normalIndices = new int[vertices.length];
 
        for (int i = 0; i < vertices.length; i++) {
            vertexIndices[i] = Integer.parseInt(vertices[i][0]);
            uvIndices[i] = Integer.parseInt(vertices[i][1]);
            normalIndices[i] = Integer.parseInt(vertices[i][2]);
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