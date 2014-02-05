/**
 * @author Tom Bell
 *
 */
public class WordNet
{
    /**
     * Symbol table containing the nouns and their associated synsets in the WordNet.
     */
    private ST<String, Bag<Integer>> nouns;

    /**
     * Symbol table containing the synset id numbers and their associated nouns.
     */
    private ST<Integer, String> synid;

    /**
     * Digraph containing the synsets of the WordNet.
     */
    private Digraph G;

    /**
     * Shortest ancestral path object.
     */
    private SAP S;

    /**
     * Size (i.e., number of synsets) of the WordNet.
     */
    private int size;

    /**
     * Constructor takes the name of the two input files.
     * @param synsets - data file listing all the (noun) synsets in the WordNet
     * @param hypernyms - data file listing all the hypernym relationships
     */
    public WordNet(final String synsets, final String hypernyms)
    {
        nouns = new ST<String, Bag<Integer>>();
        synid = new ST<Integer, String>();
        In in = new In(synsets);
        while (!in.isEmpty())
        {
            String line = in.readLine();
            String[] token = line.split(",");
            int id = Integer.parseInt(token[0]);
            String[] synset = token[1].split(" ");
            for (String noun : synset)
            {
                Bag<Integer> list = new Bag<Integer>();
                if (nouns.contains(noun)) list = nouns.get(noun);
                list.add(id);
                nouns.put(noun, list);
            }
            synid.put(id, token[1]);
        }

        size = synid.size();
        G = new Digraph(size);
        in = new In(hypernyms);
        while (!in.isEmpty())
        {
            String line = in.readLine();
            String[] token = line.split(",");
            int v = Integer.parseInt(token[0]);
            for (int i = 1; i < token.length; i++)
            {
                int w = Integer.parseInt(token[i]);
                G.addEdge(v,  w);
            }
        }
        // Check that the WordNet is a rooted directed acyclic graph (DAG)
        checkGraph();

        S = new SAP(G);
    }

    /**
     * Returns all WordNet nouns.
     * @return iterator over all nouns
     */
    public Iterable<String> nouns()
    { return nouns.keys(); }

    /**
     * Is the word a WordNet noun?
     * @param word - word to check
     * @return <code>True</code> if the word is in the WordNet
     */
    public boolean isNoun(final String word)
    { return nouns.contains(word); }

    /**
     * Returns the distance between nounA and nounB.
     * @param nounA - a noun in the WordNet
     * @param nounB - a noun in the WordNet
     * @return distance between the nouns
     */
    public int distance(final String nounA, final String nounB)
    {
        if (!isNoun(nounA))
            throw new IllegalArgumentException("nounA is not a WordNet noun");
        if (!isNoun(nounB))
            throw new IllegalArgumentException("nounB is not a WordNet noun");
        Bag<Integer> v = getVertex(nounA);
        Bag<Integer> w = getVertex(nounB);
        return S.length(v, w);
    }

    /**
     * Returns a synset that is the common ancestor of nounA and nounB in a shortest
     * ancestral path.
     * @param nounA - a WordNet noun
     * @param nounB - a WordNet noun
     * @return the common ancestor synset
     */
    public String sap(final String nounA, final String nounB)
    {
        if (!isNoun(nounA))
            throw new IllegalArgumentException("nounA is not a WordNet noun");
        if (!isNoun(nounB))
            throw new IllegalArgumentException("nounB is not a WordNet noun");
        Bag<Integer> v = getVertex(nounA);
        Bag<Integer> w = getVertex(nounB);
        int id = S.ancestor(v, w);
        return synid.get(id);
    }

    /**
     * Checks that the WordNet is a rooted directed acyclic graph (DAG).
     */
    private void checkGraph()
    {
        int roots = 0;
        for (int v = 0; v < G.V(); v++)
        {
            if (!G.adj(v).iterator().hasNext()) roots++;
        }
        if (roots != 1) throw new IllegalArgumentException(
                "input does not correspond to a rooted DAG");
    }

    /**
     * Returns the synset id number(s) corresponding to a WordNet noun.
     * @param noun - a WordNet noun
     * @return vertex id
     */
    private Bag<Integer> getVertex(final String noun)
    { return nouns.get(noun); }

    /**
     * Unit tests.
     * @param args - not used
     */
    public static void main(final String[] args)
    {
    }
}
