package co.nz.leonhardt.bpe.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * A directed graph.
 * 
 * @author Giampaolo Trapasso
 * @author freddy
 *
 * @param <V> type of the vertex
 * @apram <A> type of the edge attribute
 */
public class Digraph<V, A> {

	/**
	 * An edge.
	 * 
	 * @author freddy
	 *
	 * @param <V>
	 */
	public static class Edge<V, A> {
		private V vertex;
		private A attribute;

		/**
		 * Creates a new edge with given vertex and attribute.
		 * 
		 * @param v
		 * @param c
		 */
		public Edge(V v, A c) {
			vertex = v;
			attribute = c;
		}
		
		/**
		 * Creates a new edge with cost 0.
		 * 
		 * @param v
		 */
		public Edge(V v) {
			vertex = v;
			attribute = null;
		}

		public V getVertex() {
			return vertex;
		}

		public A getAttribute() {
			return attribute;
		}

		@Override
		public String toString() {
			return "Edge [vertex=" + vertex + ", attribute=" + attribute + "]";
		}

	}

	/**
	 * A Multimap is used to map each vertex to its list of adjacent vertices.
	 */
	private Multimap<V, Edge<V, A>> neighbours = ArrayListMultimap.create();

	/**
	 * String representation of graph.
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();
		for (V v : neighbours.keySet())
			s.append("\n    " + v + " -> " + neighbours.get(v));
		return s.toString();
	}

	public int getNumberOfEdges() {
		return neighbours.size();
	}

	/**
	 * True if graph contains vertex.
	 */
	public boolean contains(V vertex) {
		return neighbours.containsKey(vertex);
	}

	/**
	 * Add an edge to the graph; if either vertex does not exist, it's added.
	 * This implementation allows the creation of multi-edges and self-loops.
	 */
	public void add(V from, V to, A attribute) {
		neighbours.get(from).add(new Edge<V,A>(to, attribute));
	}
	
	public void add(V from, V to) {
		neighbours.get(from).add(new Edge<V,A>(to));
	}

	public int outDegree(V vertex) {
		return neighbours.get(vertex).size();
	}

	public int inDegree(V vertex) {
		return inboundNeighbors(vertex).size();
	}

	public List<V> outboundNeighbors(V vertex) {
		List<V> list = new ArrayList<V>();
		for (Edge<V,A> e : neighbours.get(vertex))
			list.add(e.vertex);
		return list;
	}

	public List<V> inboundNeighbors(V inboundVertex) {
		List<V> inList = new ArrayList<V>();
		for (V to : neighbours.keySet()) {
			for (Edge<V,A> e : neighbours.get(to))
				if (e.vertex.equals(inboundVertex))
					inList.add(to);
		}
		return inList;
	}

	public boolean isEdge(V from, V to) {
		for (Edge<V,A> e : neighbours.get(from)) {
			if (e.vertex.equals(to))
				return true;
		}
		return false;
	}

	public A getAttribute(V from, V to) {
		for (Edge<V,A> e : neighbours.get(from)) {
			if (e.vertex.equals(to))
				return e.attribute;
		}
		return null;
	}

	// Test
	public static void main(String[] args) throws IOException {

		Digraph<Integer, Integer> graph = new Digraph<Integer, Integer>();

		graph.add(0, 1, 1);
		graph.add(1, 2, 2);
		graph.add(2, 3, 2);
		graph.add(3, 0, 2);
		graph.add(1, 3, 1);
		graph.add(2, 1, 5);

		System.out.println("The nr. of vertices is: "
				+ graph.neighbours.keySet().size());
		System.out.println("The nr. of edges is: " + graph.getNumberOfEdges()); // to
																				// be
																				// fixed
		System.out.println("The current graph: " + graph);
		System.out.println("In-degrees for 0: " + graph.inDegree(0));
		System.out.println("Out-degrees for 0: " + graph.outDegree(0));
		System.out.println("In-degrees for 3: " + graph.inDegree(3));
		System.out.println("Out-degrees for 3: " + graph.outDegree(3));
		System.out.println("Outbounds for 1: " + graph.outboundNeighbors(1));
		System.out.println("Inbounds for 1: " + graph.inboundNeighbors(1));
		System.out.println("(0,2)? "
				+ (graph.isEdge(0, 2) ? "It's an edge" : "It's not an edge"));
		System.out.println("(1,3)? "
				+ (graph.isEdge(1, 3) ? "It's an edge" : "It's not an edge"));

		System.out.println("Cost for (1,3)? " + graph.getAttribute(1, 3));

	}
}