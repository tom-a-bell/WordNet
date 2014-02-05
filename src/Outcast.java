/**
 * @author Tom Bell
 *
 */
public class Outcast
{
    /**
     * The WordNet.
     */
    private WordNet wn;

    /**
     * Constructor takes a WordNet object.
     * @param wordnet - a WordNet object
     */
    public Outcast(final WordNet wordnet)
    { wn = wordnet; }

    /**
     * Given an array of WordNet nouns, returns an outcast.
     * @param nouns - array of nouns
     * @return the outcast noun
     */
    public String outcast(final String[] nouns)
    {
        int[] distance = new int[nouns.length];
        for (int i = 0; i < nouns.length; i++)
        {
            distance[i] = 0;
        }

        for (int i = 0; i < nouns.length; i++)
        {
            for (int j = i + 1; j < nouns.length; j++)
            {
                int d = wn.distance(nouns[i], nouns[j]);
                distance[i] += d;
                distance[j] += d;
            }
        }

        String outcast = "";
        int maximum = 0;
        for (int i = 0; i < nouns.length; i++)
        {
            if (distance[i] > maximum)
            {
                outcast = nouns[i];
                maximum = distance[i];
            }
        }

        return outcast;
    }

    /**
     * Takes from the command line the name of a synset file, the name of a hypernym
     * file, followed by the names of outcast files, and prints out an outcast in
     * each file.
     * @param args - names of the synset file, hypernym file and outcast file(s)
     */
    public static void main(final String[] args)
    {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            String[] nouns = In.readStrings(args[t]);
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
