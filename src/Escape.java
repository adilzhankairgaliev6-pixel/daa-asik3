/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells(), cols = w.cols();
        int exit = n - 1;
        int[][] g = Graphs.open(w, broken);

        // dead ends: cells with exactly one open door (start and exit count too)
        int deadEnds = 0;
        for (int i = 0; i < n; i++) if (g[i].length == 1) deadEnds++;

        // iterative DFS: stack[] holds the current path, next[c] is the next direction to try for cell c
        boolean[] seen = new boolean[n];
        int[] next = new int[n];          // 0 = up, 1 = right, 2 = down, 3 = left, 4 = all tried
        int[] stack = new int[n];
        int top = 0, visited = 0;
        stack[top++] = 0;
        seen[0] = true; visited = 1;
        while (top > 0 && stack[top - 1] != exit) {
            int cell = stack[top - 1];
            if (next[cell] == 4) { top--; continue; }   // dead end or all neighbours done: go back
            int dir = next[cell]++;
            int r = cell / cols, c = cell % cols;
            int nb;
            if (dir == 0) nb = r > 0 ? cell - cols : -1;
            else if (dir == 1) nb = c < cols - 1 ? cell + 1 : -1;
            else if (dir == 2) nb = r < w.rows() - 1 ? cell + cols : -1;
            else nb = c > 0 ? cell - 1 : -1;
            if (nb < 0 || seen[nb] || !isOpen(g[cell], nb)) continue;
            seen[nb] = true; visited++;
            stack[top++] = nb;
        }
        int[] path = java.util.Arrays.copyOf(stack, top);   // the stack is exactly the path from 0 to the exit
        return new Result(path, visited, deadEnds);
    }

    private static boolean isOpen(int[] neighbours, int cell) {
        for (int x : neighbours) if (x == cell) return true;
        return false;
    }
}