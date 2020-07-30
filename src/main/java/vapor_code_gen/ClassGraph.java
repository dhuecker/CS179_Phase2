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
        ClassGraphNode a_node = null;
        ClassGraphNode b_node = null;

        Iterator<ClassGraphNode> nodeIt = nodes.iterator();
        while (nodeIt.hasNext()) {
            ClassGraphNode currentNode = nodeIt.next();
            if (currentNode.equals(x)) {
                a_node = currentNode;
            }
            if (currentNode.equals(y)) {
                b_node = currentNode;
            }
        }

        if (a_node == null) {
            a_node = new ClassGraphNode(x);
            nodes.add(a_node);
        }

        if (b_node == null) {
            b_node = new ClassGraphNode(y);
            nodes.add(b_node);
        }

        edges.add(new Tuple<>(a_node, b_node));
    }

    public List<String> topologicalSort() {
        List<String> oList = new ArrayList<>();

        while (hasUnmarkedPermNodes()) {
            visit(getUnmarkedPermNode(), oList);
        }

        oList.remove(null);

        Collections.reverse(oList);

        return oList;
    }

    public void print() {
        Iterator<Tuple<ClassGraphNode, ClassGraphNode> > edgeIt = edges.iterator();
        while (edgeIt.hasNext()) {
            Tuple<ClassGraphNode, ClassGraphNode> currentEdge = edgeIt.next();

            System.out.println(currentEdge.x.name + " -> " + currentEdge.y.name);
        }
    }

    boolean hasUnmarkedPermNodes() {
        Iterator<ClassGraphNode> x =  nodes.iterator();
        while (x.hasNext()) {
            if (!x.next().pMark)
                return true;
        }

        return false;
    }

    ClassGraphNode getUnmarkedPermNode() {
        Iterator<ClassGraphNode> x =  nodes.iterator();
        while (x.hasNext()) {
            ClassGraphNode temp = x.next();
            if (!temp.pMark) return temp;
        }

        return null;
    }

    ClassGraphNode getEdgePartner(ClassGraphNode n) {
        Iterator<Tuple<ClassGraphNode, ClassGraphNode> > edge = edges.iterator();
        while (edge.hasNext()) {
            Tuple<ClassGraphNode, ClassGraphNode> currentEdge = edge.next();
            if (currentEdge.x.equals(n.name))
                return currentEdge.y;
        }

        return null;
    }

    void visit(ClassGraphNode node, List<String> list) {
        if (node.pMark)
            return;
        if (node.tMark)
            System.out.println("CLASS GRAPH NOT DAG");

        node.giveTempMark();

        ClassGraphNode edgePartner = getEdgePartner(node);
        if (edgePartner != null)
            visit(edgePartner, list);

        node.takeTempMark();
        node.givePermMark();

        list.add(0, node.name);
    }
}
