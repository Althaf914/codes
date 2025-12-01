import java.util.*;

public class dij {

    static class Edge{
        int v;
        int wt;

        public Edge(int v, int wt){
            this.v= v;
            this.wt= wt;
        }
    }

    static class Node implements Comparable<Node>{
            int v;
            int wt;

            public Node(int v, int w){
                this.v= v;
                this.wt= w;
            }

            public int compareTo(Node other){
                return Integer.compare(this.wt, other.wt);
            }
    }

    public static int[] dijkstra(List<List<Edge>> graph, int src){
        int n= graph.size();
        int dist[]= new int[n];
        boolean vis[]= new boolean[n];

        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src]= 0;

        PriorityQueue<Node> pq= new PriorityQueue<>();
        pq.add(new Node(src, 0));

        while(!pq.isEmpty()){
            Node curr= pq.poll();
            int u= curr.v;

            if(vis[u]) continue;
            vis[u]= true;

            for(Edge e: graph.get(u)){
                int v= e.v;
                int wt= e.wt;

                if(!vis[v] && dist[u]+wt< dist[v]){
                    dist[v]= dist[u]+wt;
                    pq.add(new Node(v, dist[v]));
                }
            }
        }

        return dist;
    }
    public static void main(String[] args) {
        Scanner sc= new Scanner(System.in);
        int n= sc.nextInt();
        int edges= sc.nextInt();

        List<List<Edge>> graph= new ArrayList<>();

        for(int i=0; i<n; i++){
            graph.add(new ArrayList<>());
        }

        for(int i=0; i<edges; i++){
            int u= sc.nextInt();
            int v= sc.nextInt();
            int wt= sc.nextInt();
            graph.get(u).add(new Edge(v, wt));
        }

        int src= sc.nextInt();
        int dist[]= dijkstra(graph, src);

        System.out.println("\nShortest distances from source " + src + ":");
        for (int i = 0; i < dist.length; i++) {
            System.out.println("To " + i + " = " + dist[i]);
        }
    }
}
