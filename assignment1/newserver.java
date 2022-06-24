import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Edge {
    int a, b;
    Double len;
}

class Node {
    int id;
    Double x, y;
}

class Block {
    protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    ArrayList<Node> internal_Node = new ArrayList<Node>();
    ArrayList<Node> external_Node = new ArrayList<Node>();
    ArrayList<Edge> internal_Edge = new ArrayList<Edge>();
    ArrayList<Edge> External_Edge = new ArrayList<Edge>();
}

class EdgeEntry {
    Double distance;
    int b;
}

class edgeComparator implements Comparator<EdgeEntry> {
    public int compare(EdgeEntry e1, EdgeEntry e2) {
        if (e1.distance > e2.distance) {
            return 1;
        } else {
            return -1;
        }
    }
}

class Data {
    // data
    public static ArrayList<Node> Nodes = new ArrayList<Node>();
    public static ArrayList<Edge> Edges = new ArrayList<Edge>();
    public static ArrayList<Block> Cells = new ArrayList<Block>();
    public static HashMap<Integer, Integer> nodeToCell = new HashMap<Integer, Integer>(); // <node_no, block>

    public static double x_min = Double.POSITIVE_INFINITY;
    public static double y_min = Double.POSITIVE_INFINITY;
    public static double x_max = Double.NEGATIVE_INFINITY;
    public static double y_max = Double.NEGATIVE_INFINITY;
    public static int x_scale, y_scale;
    public static int grids;

    // functions
    public static void GetNodesandEdges() {
        // Reading Nodes from files
        File node = new File("Dataset/sample_nodes.txt"); // File("London/nodes.txt"); //

        try {
            Scanner nodeSc = new Scanner(node);
            while (nodeSc.hasNextLine()) {
                // read lines and store in arraylist nodes
                Node n = new Node();

                String line = nodeSc.nextLine();
                // System.out.println(line);
                String[] sp = line.split(" ", 3);

                n.id = Integer.parseInt(sp[0]);
                n.x = Double.parseDouble(sp[1]);
                n.y = Double.parseDouble(sp[2]);

                Data.Nodes.add(n);

            }
            nodeSc.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Reading edges from files
        File edge = new File("Dataset/sample_edges.txt"); // File("London/edges.txt");//

        try {
            Scanner edgeSc = new Scanner(edge);
            while (edgeSc.hasNextLine()) {
                // read lines and store in arraylist Edges
                Edge e = new Edge();

                String line = edgeSc.nextLine();
                // System.out.println(line);
                String[] sp = line.split(" ", 3);

                e.a = Integer.parseInt(sp[0]);
                e.b = Integer.parseInt(sp[1]);
                e.len = Double.parseDouble(sp[2]);

                Data.Edges.add(e);

            }
            edgeSc.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void min_and_max(int k) {
        for (Node n : Data.Nodes) {
            double x = n.x;
            double y = n.y;

            if (x < x_min) {
                x_min = x;
            } else if (x > x_max) {
                x_max = x;
            }

            if (y < y_min) {
                y_min = y;
            } else if (y > y_max) {
                y_max = y;
            }
        }

        // scaling xmax and ymax
        if (!isInteger((y_max - y_min) / k)) {
            y_scale = (int) ((y_max - y_min) / k) + 1; // k + 1
            y_max = y_min + k * y_scale;
        } else {
            y_scale = (int) ((y_max - y_min) / k);
        }

        if (!isInteger((x_max - x_min) / k)) {
            x_scale = (int) ((x_max - x_min) / k) + 1; // k + 1
            x_max = x_min + k * x_scale;
        } else {
            x_scale = (int) ((x_max - x_min) / k);
        }
    }

    public static void CellInsert(int k) {
        // internal nodes
        for (int i = 0; i < Nodes.size(); i++) {
            Cells.add(null);
        }
        for (Node n : Data.Nodes) {
            double x = n.x;
            double y = n.y;
            int xloc, yloc;

            xloc = (int) ((x - x_min) / k);
            yloc = (int) ((y - y_min) / k);
            int x_scale = (int) ((x_max - x_min) / k);
            int index = xloc + yloc * x_scale;
            // System.out.println("xloc:"+xloc + " yloc:"+yloc + " x_scale:"+x_scale);
            if (Cells.get(index) == null) {
                Cells.set(index, new Block());

            }
            Cells.get(index).internal_Node.add(n);
            nodeToCell.put(n.id, index);// nodeId to cell no
        }

        // add internal and external edges
        for (Edge e : Data.Edges) {
            int a = e.a;
            int b = e.b;
            if (nodeToCell.get(a) == nodeToCell.get(b)) {
                Cells.get(nodeToCell.get(a)).internal_Edge.add(e);
            } else {
                Cells.get(nodeToCell.get(a)).External_Edge.add(e);
                Cells.get(nodeToCell.get(b)).External_Edge.add(e);
            }
        }

        // add external nodes
        for (Block c : Cells) {
            if (c != null) {
                Set<Integer> IntNode = new HashSet<Integer>();
                Set<Integer> ExtNode = new HashSet<Integer>();

                for (Node n : c.internal_Node) {
                    IntNode.add(n.id);
                }

                for (Edge e : c.External_Edge) {
                    ExtNode.add(e.a);
                    ExtNode.add(e.b);
                }

                ExtNode.removeAll(IntNode);

                for (int id : ExtNode) {
                    c.external_Node.add(Data.Nodes.get(id));
                }
            }
        }
    }

    static boolean isInteger(Double n) {
        return (int) (Math.ceil(n)) == (int) (Math.floor(n));
    }

}

class newserver {
    public static void main(String[] args) throws IOException, InterruptedException {
        // input scaling factor
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the value of K:");
        int k = sc.nextInt();

        // get nodes and edges from the files
        Data.GetNodesandEdges();

        // finding xmin xmax ymin ymax xscale yscale
        Data.min_and_max(k);

        //
        Data.grids = Data.x_scale * Data.y_scale;
        System.out.println("Grids = " + Data.grids);
        System.out.println("xscale = " + Data.x_scale + " yscale = " + Data.y_scale);

        // inserting data into cells
        Data.CellInsert(k);
        System.out.println("no of cells = " + Data.Cells.size());

        sc.close();

        ExecutorService executorService = Executors.newFixedThreadPool(100);

        ServerSocket ss = new ServerSocket(4999);
        // ss.setReuseAddress(true);
        while (true) {

            Socket s = ss.accept();
            // Demo x=new Demo(s);
            System.out.println("Client connected");

            Runnable t1 = new Demo(s);
            executorService.submit(t1);
            // Update(Cells,nodeToCell);

        }
    }
}//newserver end

class Demo implements Runnable {

    protected Socket s;

    public Demo(Socket s1) {
        this.s = s1;
    }

    public void run() // throws IOException,InterruptedException
    {

        try {

            InputStreamReader in = new InputStreamReader(s.getInputStream());

            BufferedReader bf = new BufferedReader(in);

            // String str=bf.readLine();

            PrintWriter pr = new PrintWriter(s.getOutputStream());
           /* pr.println("Select option: \n1. Querry\n2. Update Dataset");
            pr.flush();
	   */
            String str = bf.readLine();
            int n = Integer.parseInt(str);
            String strx = "";
            if (n == 1) {
                // System.out.println("Client : "+ str);
                str = bf.readLine();
                String[] sp = str.split(" ", 2);
                int source = Integer.parseInt(sp[0]);

                int destination = Integer.parseInt(sp[1]);

                strx = dijkstra.PartitionDijkstras(source, destination);

            } else {
                str = bf.readLine();
                System.out.println("str = " + str);
                String[] sp = str.split(" ", 3);
                double weight = Double.parseDouble(sp[2]);
                int end = Integer.parseInt(sp[1]);
                int start = Integer.parseInt(sp[0]);
                dijkstra.update(start, end, weight);
            }

            pr.println(strx);
            pr.flush();

            System.out.println("Close thread");
            s.close();
        } catch (IOException e) {
            return;
        }

    }
}//demo end

class dijkstra {

    public static void update(int start, int end, Double weight) {

        int x = Data.nodeToCell.get(start);

        Block c = Data.Cells.get(x);
        c.lock.writeLock().lock();
        try {
            int ind = 0;

            for (ListIterator<Edge> iterator = c.internal_Edge.listIterator(); iterator.hasNext();) {
                Edge value = iterator.next();
                if (value.a == start && value.b == end) {
                    value.len = weight;
                    iterator.set(value);
                    Data.Cells.get(x).internal_Edge.set(ind, value);
                }
                // System.out.println("1");
                ind++;
            }
            ind = 0;
            for (ListIterator<Edge> iterator = c.External_Edge.listIterator(); iterator.hasNext();) {
                Edge value = iterator.next();
                if (value.a == start && value.b == end) {
                    value.len = weight;
                    iterator.set(value);
                    Data.Cells.get(x).External_Edge.set(ind, value);

                }
                // System.out.println("2");
                ind++;
            }
        } finally {
            c.lock.writeLock().unlock();
        }

        // System.out.println("Close thread");
        // s.close();

    }

    public static String PartitionDijkstras(int source, int destination) {

        // graph seen till now
        int N = Data.Nodes.size();
        ArrayList<ArrayList<EdgeEntry>> seenGraph = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            seenGraph.add(i, new ArrayList<EdgeEntry>());
        }

        // shrotest distance from source
        Double shortesDist[] = new Double[N];
        for (int i = 0; i < N; i++) {
            shortesDist[i] = Double.POSITIVE_INFINITY;
        }
        shortesDist[source] = 0.0;// source dist = 0

        ArrayList<Edge> openFileEdges = new ArrayList<>();

        Set<Integer> openCells = new HashSet<Integer>();

        int cellCount = 0;

        // priority queue with minimum disance pop first
        PriorityQueue<EdgeEntry> pq = new PriorityQueue<>(new edgeComparator());// queue is working fine

        // nodes done with shortest distance
        Set<Integer> closedNode = new HashSet<Integer>();

        // parent of node
        Integer parentNode[] = new Integer[N];// all null by default

        // path from source to destination
        ArrayList<Integer> path = new ArrayList<Integer>();

        // shortest distance from source to destination
        Double minDist = Double.POSITIVE_INFINITY;

        
        EdgeEntry ent = new EdgeEntry();
        ent.b = source;
        ent.distance = 0.0;

        pq.add(ent);

        while (pq.size() > 0) {
            EdgeEntry current = pq.poll();
            double dist = current.distance;
            int curr_node = current.b;

            if (!closedNode.contains(curr_node)) { // already calculated shortest dist then skip node
                closedNode.add(curr_node);

                // if destination reach then calculate path
                if (curr_node == destination) {
                    minDist = dist;
                    int temp = destination;
                    while (temp != source) {
                        path.add(0, temp);
                        if (parentNode[temp] == null)
                            break;
                        temp = parentNode[temp];
                    }
                    path.add(0, source);
                    break;
                }

                int index = Data.nodeToCell.get(curr_node);

                if (!openCells.contains(index)) {
                    openCells.add(index);
                    cellCount++;

                    Block c = Data.Cells.get(index);
                    c.lock.readLock().lock();
                    try {
                        for (Edge e : c.internal_Edge) {
                            openFileEdges.add(e);
                        }
                        for (Edge e : c.External_Edge) {
                            openFileEdges.add(e);
                        }
                    } finally {
                        c.lock.readLock().unlock();
                    }

                }

                for (Iterator<Edge> iterator = openFileEdges.iterator(); iterator.hasNext();) {
                    Edge value = iterator.next();
                    if (value.a == curr_node) {
                        EdgeEntry temp = new EdgeEntry();
                        temp.b = value.b;
                        temp.distance = value.len;
                        seenGraph.get(curr_node).add(temp);
                        iterator.remove();
                    }
                }

                // System.out.println("curr node = " + curr_node);

                for (EdgeEntry e : seenGraph.get(curr_node)) {
                    // System.out.println("dist = " + dist);
                    // System.out.println("e = " + e.b + " dist = " + e.distance);
                    if (e != null && (shortesDist[e.b] > dist + e.distance)) { // shortesDist[e.b] == 0.0 ||
                        shortesDist[e.b] = (dist + e.distance);
                        parentNode[e.b] = curr_node;
                    }
                    EdgeEntry temp = new EdgeEntry();
                    temp.b = e.b;
                    temp.distance = (dist + e.distance);

                    pq.add(temp);
                }

            }
            //
            // System.out.println("mindist = " + minDist);
            // System.out.println("current node = " + curr_node);
            // System.out.println("closedNodes = " + closedNode);

        }
        String s = "";
        if (pq.size() == 0) {
            s = "destination not reachable";
        } else {
            // System.out.print("Path:");
            // for(int i:path){
            // System.out.print("->"+i);
            // }
            // System.out.println();
            s = "minimum distance between " + source + " and " + destination + " is " + minDist;

            // System.out.println("closed nodes min dist");
            // for(int i: closedNode){
            // System.out.println(i + " " + shortesDist[i]);
            // }
        }
        return s;
    }
}
