package finitereality.treecipe.runtime.util;

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.GraphType;
import org.jgrapht.graph.AbstractGraph;

import java.util.Set;
import java.util.function.Supplier;

/**
 * A simple graph wrapper which "reverses" a given directed graph.
 * <p>
 * The implementation of this graph simply swaps source/target parameters where
 * appropriate; it does not actually "reverse" the underlying graph.
 *
 * @param <V> The vertex type
 * @param <E> The edge type
 */
public class AsReverseDirectedGraph<V, E>
    extends AbstractGraph<V, E>
{
    protected final Graph<V, E> base;

    public AsReverseDirectedGraph(Graph<V, E> base)
    {
        this.base = GraphTests.requireDirected(base);
    }

    @Override
    public Set<E> getAllEdges(final V sourceVertex, final V targetVertex)
    {
        return this.base.getAllEdges(targetVertex, sourceVertex);
    }

    @Override
    public E getEdge(final V sourceVertex, final V targetVertex)
    {
        return this.base.getEdge(targetVertex, sourceVertex);
    }

    @Override
    public Supplier<V> getVertexSupplier()
    {
        return this.base.getVertexSupplier();
    }

    @Override
    public Supplier<E> getEdgeSupplier()
    {
        return this.base.getEdgeSupplier();
    }

    @Override
    public E addEdge(final V sourceVertex, final V targetVertex)
    {
        return this.base.addEdge(targetVertex, sourceVertex);
    }

    @Override
    public boolean addEdge(final V sourceVertex, final V targetVertex, final E e)
    {
        return this.base.addEdge(targetVertex, sourceVertex, e);
    }

    @Override
    public V addVertex()
    {
        return this.base.addVertex();
    }

    @Override
    public boolean addVertex(final V v)
    {
        return this.base.addVertex(v);
    }

    @Override
    public boolean containsEdge(final E e)
    {
        return this.base.containsEdge(e);
    }

    @Override
    public boolean containsVertex(final V v)
    {
        return this.base.containsVertex(v);
    }

    @Override
    public Set<E> edgeSet()
    {
        return this.base.edgeSet();
    }

    @Override
    public int degreeOf(final V vertex)
    {
        return this.base.degreeOf(vertex);
    }

    @Override
    public Set<E> edgesOf(final V vertex)
    {
        return this.base.edgesOf(vertex);
    }

    @Override
    public int inDegreeOf(final V vertex)
    {
        return this.base.outDegreeOf(vertex);
    }

    @Override
    public Set<E> incomingEdgesOf(final V vertex)
    {
        return this.base.outgoingEdgesOf(vertex);
    }

    @Override
    public int outDegreeOf(final V vertex)
    {
        return this.base.inDegreeOf(vertex);
    }

    @Override
    public Set<E> outgoingEdgesOf(final V vertex)
    {
        return this.base.incomingEdgesOf(vertex);
    }

    @Override
    public E removeEdge(final V sourceVertex, final V targetVertex)
    {
        return this.base.removeEdge(targetVertex, sourceVertex);
    }

    @Override
    public boolean removeEdge(final E e)
    {
        return this.base.removeEdge(e);
    }

    @Override
    public boolean removeVertex(final V v)
    {
        return this.base.removeVertex(v);
    }

    @Override
    public Set<V> vertexSet()
    {
        return this.base.vertexSet();
    }

    @Override
    public V getEdgeSource(final E e)
    {
        return this.base.getEdgeTarget(e);
    }

    @Override
    public V getEdgeTarget(final E e)
    {
        return this.base.getEdgeSource(e);
    }

    @Override
    public GraphType getType()
    {
        return this.base.getType();
    }

    @Override
    public double getEdgeWeight(final E e)
    {
        return this.base.getEdgeWeight(e);
    }

    @Override
    public void setEdgeWeight(final E e, final double weight)
    {
        this.base.setEdgeWeight(e, weight);
    }
}
