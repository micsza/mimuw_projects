import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

class Vertex {
    private static int INFTY = 2147483647;
    private int id;
    private ArrayList<Vertex> adj;
    private boolean visited;
    private int dist;

    public Vertex(int id) {
        this.id = id;
        adj = new ArrayList<Vertex>();
        visited = false;
        dist = INFTY;
    }

    public int id() {
        return id;
    }

    public int dist() {
        return dist;
    }

    public boolean isVisited() {
        return visited;
    }

    public void visit(int distFromSource) {
        visited = true;
        dist = distFromSource;
    }

    public void setUnvisited() {
        visited = false;
        dist = INFTY;
    }

    public boolean isAdjecentTo(Vertex u) {
        return adj.contains(u);
    }

    public void visitAllNeighbours(ArrayDeque<Vertex> Q) {
        for (Vertex v: this.adj) {
            if (!v.isVisited()) {
                v.visit(this.dist + 1);
                Q.addLast(v);
            }
        }
    }

    public void visitAllNeighbours(ArrayDeque<Vertex> Q, HashMap<Vertex, Vertex> M) {
        for (Vertex v: this.adj) {
            if (!v.isVisited()) {
                v.visit(this.dist + 1);
                M.put(v, this);
                Q.addLast(v);
            }
        }
    }

    public void addNeighbour(Vertex u) {
        assert(u != this);
        assert(!this.isAdjecentTo(u));
        adj.add(u);
    }

    public String toString() {
        return "" + id; // + "(visited = " + visited + ")";
    }

    public String adj() {
        return adj.toString();
    }
}

public class Graph {
    private ArrayList<Vertex> vertexList;
    private int size;

    public Graph() {
        vertexList = new ArrayList<Vertex>();
        size = 0;
    }

    public int size() {
        return size;
    }

    public Vertex vertex(int i) {
        return vertexList.get(i);
    }

    public void resetVertices() {
        for (Vertex v: vertexList)
            v.setUnvisited();
    }

    public void addVertex() {
        vertexList.add(new Vertex(size++));
    }

    public boolean edgeExists(int i, int j) {
        if (vertex(i).isAdjecentTo(vertex(j)) && vertex(j).isAdjecentTo(vertex(i)))
            return true;

        if (!(vertex(i).isAdjecentTo(vertex(j)) || vertex(j).isAdjecentTo(vertex(i))))
            return false;

        if ((vertex(i).isAdjecentTo(vertex(j)) && !vertex(j).isAdjecentTo(vertex(i))) || (!vertex(i).isAdjecentTo(vertex(j))
                && vertex(j).isAdjecentTo(vertex(i))))
            System.err.println("Error: one way adjacent");

        return false;
    }

    public void addEdge(int i, int j) {
        assert((0 <= i) && (i < size) && (0 <= j) && (j < size));
        assert(!edgeExists(i, j));

        vertex(i).addNeighbour(vertex(j));
        vertex(j).addNeighbour(vertex(i));
    }

    public int distance(int i, int j) {
        assert((0 <= i) && (i < size) && (0 <= j) && (j < size));
        if (i == j)
            return 0;

        resetVertices();
        ArrayDeque<Vertex> Q = new ArrayDeque<Vertex>();
        Vertex source = vertex(i);
        source.visit(0);
        Q.addLast(source);
        while (!Q.isEmpty()) {
            Vertex u = Q.getFirst();
            Q.removeFirst();
            if (u == vertex(j))
                return u.dist();
            u.visitAllNeighbours(Q);
        }

        return -1;
    }

    public ArrayList<Vertex> shortestPath(int i, int j) {
        assert((0 <= i) && (i < size) && (0 <= j) && (j < size));
        HashMap<Vertex, Vertex> M = new HashMap<>();

        resetVertices();
        ArrayDeque<Vertex> Q = new ArrayDeque<Vertex>();
        Vertex source = vertex(i);
        Vertex target = vertex(j);

        source.visit(0);
        M.put(source, null);
        Q.addLast(source);
        while (!Q.isEmpty()) {
            Vertex u = Q.getFirst();
            Q.removeFirst();
            if (u == target)
                return path(M, source, target);
            u.visitAllNeighbours(Q, M);
        }

        return new ArrayList<>();
    }

    public ArrayList<Vertex> path(HashMap<Vertex, Vertex> M, Vertex source, Vertex target) {
        ArrayList<Vertex> list = new ArrayList<>();
        Vertex v = target;
        while (v != null) {
            list.add(v);
            v = M.get(v);
        }

        return list;
    }

    public void print() {
        for (Vertex v: vertexList)
            System.out.println(v.toString() + ": " + v.adj());
    }

    public void printDist(int i, int j) {
        System.out.println("dist(" + i + ", " + j + ") = " + this.distance(i,j));
    }

    public void printShortestPath(int i, int j) {
        System.out.println("shortestPath(" + i + ", " + j + ") = " + this.shortestPath(i,j).toString());
    }

    /*
     *********** MAIN ***********
     */
    public static void main(String[] argv) {
        System.out.println(" ****** G1 ******");
        Graph G1 = new Graph();
        for (int i = 0; i < 7; i++)
            G1.addVertex();
        G1.addEdge(1, 3);
        G1.addEdge(2,5);
        G1.addEdge(3, 5);
        G1.addEdge(0, 1);
        G1.addEdge(0, 2);
        G1.print();

        G1.printDist(1, 2);
        G1.printShortestPath(1, 2);

        G1.printDist(2, 1);
        G1.printShortestPath(2, 1);

        G1.printDist(5, 6);
        G1.printShortestPath(5, 6);


        /* G2 = lista */
        System.out.println("\n ****** G2 ******");
        Graph G2 = new Graph();
        for (int l = 0; l < 10; l++)
            G2.addVertex();
        for (int l = 0; l < 9; l++)
            G2.addEdge(l, l + 1);
        G2.print();
        G2.printDist(0, 9);
        G2.printShortestPath(0, 9);
        G2.printDist(3,8);
        G2.printShortestPath(3, 8);

        /* G4 = Petersen */

        System.out.println("\n ****** G4 ******");
        Graph G4 = new Graph();
        for (int l = 0; l < 10; l++)
            G4.addVertex();
        G4.addEdge(0,1);
        G4.addEdge(0,4);
        G4.addEdge(0,5);
        G4.addEdge(6,1);
        G4.addEdge(2,1);
        G4.addEdge(2, 7);
        G4.addEdge(2, 3);
        G4.addEdge(3, 8);
        G4.addEdge(3, 4);
        G4.addEdge(4, 9);

        G4.addEdge(5, 8);
        G4.addEdge(5, 7);
        G4.addEdge(6, 8);
        G4.addEdge(6, 9);
        G4.addEdge(7, 9);
        G4.print();

        G4.printDist(2, 8);
        G4.printShortestPath(2, 8);
        G4.printDist(3, 6);
        G4.printShortestPath(3, 6);
        G4.printDist(2, 4);
        G4.printShortestPath(2, 4);


        System.out.println("\n ****** G5 ******");
        Graph G5 = new Graph();
        for (int l = 0; l < 6; l++)
            G5.addVertex();
        G5.addEdge(0,1);
        G5.addEdge(0, 5);
        G5.addEdge(1, 2);
        G5.addEdge(2,3);
        G5.addEdge(3,5);
        G5.addEdge(2,4);
        G5.addEdge(4, 5);
        G5.printShortestPath(1,5);
        G5.printShortestPath(0,4);
    }
}
