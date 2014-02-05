/**
 * @author Tom Bell
 *
 */
public class SAP
{
    /**
     * Private immutable copy of the digraph.
     */
    private final Digraph G;

    /**
     * Cache of previously determined ancestors and SAPs (speed optimisation).
     */
    private ST<String, int[]> cache;

    /**
     * Constructor takes a digraph (not necessarily a DAG).
     * @param digraph to analyse
     */
    public SAP(final Digraph digraph)
    {
        // Make a deep copy of the digraph
        G = new Digraph(digraph.V());
        for (int v = 0; v < digraph.V(); v++)
        {
            for (int w : digraph.adj(v))
            {
                G.addEdge(v, w);
            }
        }

        // Create the cache
        cache = new ST<String, int[]>();
    }

    /**
     * Returns the length of the shortest ancestral path between v and w;
     * -1 if no such path.
     * @param v - a vertex in the digraph
     * @param w - a vertex in the digraph
     * @return the shortest ancestral path length
     */
    public int length(final int v, final int w)
    { return findSAP(v, w)[1]; }

    /**
     * Returns a common ancestor of v and w that participates in a shortest ancestral
     * path; -1 if no such path.
     * @param v - a vertex in the digraph
     * @param w - a vertex in the digraph
     * @return the common ancestor of v and w, or -1 if no such path exists
     */
    public int ancestor(final int v, final int w)
    { return findSAP(v, w)[0]; }

    /**
     * Returns the length of the shortest ancestral path between any vertex in v and
     * any vertex in w; -1 if no such path.
     * @param v - list of vertices in the digraph
     * @param w - list of vertices in the digraph
     * @return the shortest ancestral path length between any vertex in v and w
     */
    public int length(final Iterable<Integer> v, final Iterable<Integer> w)
    { return findSAP(v, w)[1]; }

    /**
     * Returns a common ancestor that participates in the shortest ancestral path;
     * -1 if no such path.
     * @param v - list of vertices in the digraph
     * @param w - list of vertices in the digraph
     * @return the common ancestor with shortest path between any vertex in v and w
     */
    public int ancestor(final Iterable<Integer> v, final Iterable<Integer> w)
    { return findSAP(v, w)[0]; }

    /**
     * Finds the shortest ancestral path and common ancestor between v and w.
     * @param v - vertex in the digraph
     * @param w - vertex in the digraph
     * @return an integer array containing the common ancestor and the path length
     */
    private int[] findSAP(final int v, final int w)
    {
        // Check that v and w are valid
        checkBounds(v, w);

        // Return the cached common ancestor, if previously determined
        if (cache.contains(keyOf(v, w))) return cache.get(keyOf(v, w));

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);

        int ancestor = -1;
        int length = Integer.MAX_VALUE;

        for (int x = 0; x < G.V(); x++)
        {
            if (bfsV.hasPathTo(x) && bfsW.hasPathTo(x))
            {
                int l = bfsV.distTo(x) + bfsW.distTo(x);
                if (l < length)
                {
                    ancestor = x;
                    length = l;
                }
            }
        }

        if (ancestor == -1) length = -1;

        int[] value = new int[2];
        value[0] = ancestor;
        value[1] = length;

        // Cache the common ancestor and shortest ancestral path
        cache.put(keyOf(v, w), value);
        cache.put(keyOf(w, v), value);

        return value;
    }

    /**
     * Finds the shortest ancestral path and common ancestor between any vertex in v
     * and any vertex in w.
     * @param v - list of vertices in the digraph
     * @param w - list of vertices in the digraph
     * @return an integer array containing the common ancestor and the path length
     */
    private int[] findSAP(final Iterable<Integer> v, final Iterable<Integer> w)
    {
        // Check that v and w are valid
        checkBounds(v, w);

        // Return the cached common ancestor, if previously determined
        if (cache.contains(keyOf(v, w))) return cache.get(keyOf(v, w));

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);

        int ancestor = -1;
        int length = Integer.MAX_VALUE;

        for (int x = 0; x < G.V(); x++)
        {
            if (bfsV.hasPathTo(x) && bfsW.hasPathTo(x))
            {
                int l = bfsV.distTo(x) + bfsW.distTo(x);
                if (l < length)
                {
                    ancestor = x;
                    length = l;
                }
            }
        }

        if (ancestor == -1) length = -1;

        int[] value = new int[2];
        value[0] = ancestor;
        value[1] = length;

        // Cache the common ancestor and shortest ancestral path
        cache.put(keyOf(v, w), value);
        cache.put(keyOf(w, v), value);

        return value;
    }

    /**
     * Checks that the vertices v and w are valid; that is, they are both between
     * zero and the total number of vertices in the digraph.
     * @param v - a vertex in the digraph
     * @param w - a vertex in the digraph
     */
    private void checkBounds(final int v, final int w)
    {
        if (v < 0 || v >= G.V())
            throw new java.lang.IndexOutOfBoundsException("v must be >= 0 and < V");
        if (w < 0 || w >= G.V())
            throw new java.lang.IndexOutOfBoundsException("w must be >= 0 and < V");
    }

    /**
     * Checks that all vertices in the lists v and w are valid; that is, they are all
     * between zero and the total number of vertices in the digraph.
     * @param v - list of vertices in the digraph
     * @param w - list of vertices in the digraph
     */
    private void checkBounds(final Iterable<Integer> v, final Iterable<Integer> w)
    {
        if (!v.iterator().hasNext())
            throw new java.lang.IllegalArgumentException(
                    "v must contain one or more values");
        if (!w.iterator().hasNext())
            throw new java.lang.IllegalArgumentException(
                    "w must contain one or more values");
        for (int i : v)
            if (i < 0 || i >= G.V())
                throw new java.lang.IndexOutOfBoundsException(
                        "v[" + i + "] must be >= 0 and < V");
        for (int i : w)
            if (i < 0 || i >= G.V())
                throw new java.lang.IndexOutOfBoundsException(
                        "w[" + i + "] must be >= 0 and < V");
    }

    /**
     * Returns the corresponding cache key string for the two vertices v and w.
     * @param v - a vertex in the digraph
     * @param w - a vertex in the digraph
     * @return the key string for the two vertices
     */
    private String keyOf(final int v, final int w)
    { return String.valueOf(v) + "-" + String.valueOf(w); }

    /**
     * Returns the corresponding cache key string for two lists of vertices v and w.
     * @param v - list of vertices in the digraph
     * @param w - list of vertices in the digraph
     * @return the key string for the two lists of vertices
     */
    private String keyOf(final Iterable<Integer> v, final Iterable<Integer> w)
    {
        String key = "";
        for (int i: v) key += String.valueOf(i) + ",";
        key += "-";
        for (int i: w) key += String.valueOf(i) + ",";
        return key;
    }

    /**
     * Unit tests.
     * @param args - not used
     */
    public static void main(final String[] args)
    {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
