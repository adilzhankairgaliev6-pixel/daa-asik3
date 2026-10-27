import java.util.*;

/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] broken = new int[w.cells() - 1];
        int m = 0;
        for (int k = 0; k < w.count(); k++) {
            if (dsu.union(w.a()[k], w.b()[k])) broken[m++] = k;
        }
        return Arrays.copyOf(broken, m);
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        int m = w.count();
        // sort wall indexes by cost (primitive sort: cost * m + index fits in a long)
        long[] keys = new long[m];
        for (int k = 0; k < m; k++) keys[k] = (long) w.cost()[k] * m + k;
        Arrays.sort(keys);
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] broken = new int[w.cells() - 1];
        int cnt = 0;
        for (long key : keys) {
            int k = (int) (key % m);
            if (dsu.union(w.a()[k], w.b()[k])) {
                broken[cnt++] = k;
                if (cnt == broken.length) break;   // tree complete
            }
        }
        return Arrays.copyOf(broken, cnt);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells(), m = w.count();
        // adjacency lists of wall indexes for every cell
        int[] deg = new int[n];
        for (int k = 0; k < m; k++) { deg[w.a()[k]]++; deg[w.b()[k]]++; }
        int[][] adj = new int[n][];
        for (int i = 0; i < n; i++) adj[i] = new int[deg[i]];
        int[] fill = new int[n];
        for (int k = 0; k < m; k++) { adj[w.a()[k]][fill[w.a()[k]]++] = k; adj[w.b()[k]][fill[w.b()[k]]++] = k; }

        boolean[] inTree = new boolean[n];
        // queue entries: {cost, cell that would join the tree}
        PriorityQueue<long[]> pq = new PriorityQueue<>((x, y) -> Long.compare(x[0], y[0]));
        long total = 0;
        int joined = 0;
        inTree[0] = true; joined = 1;
        for (int k : adj[0]) pq.add(new long[]{w.cost()[k], w.a()[k] == 0 ? w.b()[k] : w.a()[k]});
        while (!pq.isEmpty() && joined < n) {
            long[] top = pq.poll();
            int v = (int) top[1];
            if (inTree[v]) continue;           // stale entry: this cell is already in the maze
            inTree[v] = true; joined++;
            total += top[0];
            for (int k : adj[v]) {
                int u = w.a()[k] == v ? w.b()[k] : w.a()[k];
                if (!inTree[u]) pq.add(new long[]{w.cost()[k], u});
            }
        }
        return total;
    }
}