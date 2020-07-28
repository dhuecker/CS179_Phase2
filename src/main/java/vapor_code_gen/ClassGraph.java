package vapor_code_gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;

public class ClassGraph {
    List<ClassGraphNode> nodes;
    public List< Tuple<ClassGraphNode, ClassGraphNode> > edges;

    public ClassGraph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void addEdge(String x, String y) {
        ClassGraphNode x_node = null;
        ClassGraphNode y_node = null;

        Iterator<ClassGraphNode> nodeIterator = nodes.iterator();
        while (nodeIterator.hasNext()) {
            ClassGraphNode currNode = nodeIterator.next();
            if (currNode.equals(x)) {
                x_node = currNode; // Node exists in list already
            }
            if (currNode.equals(y)) {
                y_node = currNode;
            }
        }

        // Node needs to be added to list
        if (x_node == null) {
            x_node = new ClassGraphNode(x);
            nodes.add(x_node);
        }

        if (y_node == null) {
            y_node = new ClassGraphNode(y);
            nodes.add(y_node);
        }

        edges.add(new Tuple<>(x_node, y_node));
    }

    // Modified DFS
    public List<String> topologicalSort() {
        List<String> orderedList = new ArrayList<>();

        while (hasUnmarkedPermNodes()) {
            visit(getUnmarkedPermNode(), orderedList);
        }

        // Prune for "null" nodes (ie nodes with no parent)
        orderedList.remove(null);

        Collections.reverse(orderedList);

        return orderedList;
    }

    public void print() {
        Iterator<Tuple<ClassGraphNode, ClassGraphNode> > edgeIterator = edges.iterator();
        while (edgeIterator.hasNext()) {
            Tuple<ClassGraphNode, ClassGraphNode> currEdge = edgeIterator.next();

            System.out.println(currEdge.x.name + " -> " + currEdge.y.name);
        }
    }

    // Helper functions for topological sort

    boolean hasUnmarkedPermNodes() {
        Iterator<ClassGraphNode> n =  nodes.iterator();
        while (n.hasNext()) {
            if (!n.next().pMark)
                return true;
        }

        return false;
    }

    ClassGraphNode getUnmarkedPermNode() {
        Iterator<ClassGraphNode> n =  nodes.iterator();
        while (n.hasNext()) {
            ClassGraphNode c = n.next();
            if (!c.pMark) return c;
        }

        return null;
    }

    // if edge n -> m exists, returns m
    ClassGraphNode getEdgePartner(ClassGraphNode n) {
        Iterator<Tuple<ClassGraphNode, ClassGraphNode> > edge = edges.iterator();
        while (edge.hasNext()) {
            Tuple<ClassGraphNode, ClassGraphNode> currEdge = edge.next();
            if (currEdge.x.equals(n.name))
                return currEdge.y;
        }

        return null;
    }

    void visit(ClassGraphNode node, List<String> list) {
        if (node.pMark)
            return;
        if (node.tMark)
            System.out.println("CLASS GRAPH NOT DAG");

        node.giveTempMark();

        ClassGraphNode edgeMate = getEdgePartner(node);
        if (edgeMate != null)
            visit(edgeMate, list);

        node.takeTempMark();
        node.givePermMark();

        list.add(0, node.name);
    }
}
