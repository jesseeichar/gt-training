package _7_extra_tasks;

import org.geotools.graph.structure.Graph;
import org.geotools.graph.structure.GraphVisitor;
import org.geotools.graph.structure.Graphable;
import org.geotools.graph.traverse.GraphTraversal;
import org.geotools.graph.traverse.basic.BasicGraphTraversal;
import org.geotools.graph.traverse.basic.SimpleGraphWalker;
import org.geotools.graph.traverse.standard.BreadthFirstIterator;

import java.util.Iterator;

/**
 * @author Jesse on 3/25/2015.
 */
public final class GraphUtil {
    public static void printOrphans(Graph graph) {
        final OrphanVisitor graphVisitor = new OrphanVisitor();
        final SimpleGraphWalker sgv = new SimpleGraphWalker(graphVisitor);
        final BreadthFirstIterator iterator = new BreadthFirstIterator();
        final BasicGraphTraversal bgt = new BasicGraphTraversal(graph, sgv, iterator);
        iterator.setSource((Graphable) graph.getNodes().iterator().next());
        bgt.traverse();

        System.out.println("Total nodes: " + graph.getNodes().size());
        System.out.println("Found orphans: " + graphVisitor.getCount());
    }

    static final class OrphanVisitor implements GraphVisitor {
        private int count = 0;

        public int getCount() {
            return count;
        }

        public int visit(Graphable component) {
            Iterator related = component.getRelated();
            if (!related.hasNext()) {
                // no related components makes this an orphan
                count++;
            }
            return GraphTraversal.CONTINUE;
        }

    }

}
