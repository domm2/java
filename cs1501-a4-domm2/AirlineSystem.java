import java.util.Set;
import java.util.ArrayList;
import java.util.*;
import java.io.*;

//----------------------
//account for all throws CityNotFoundExceptions 
//make helper methods
//----------------------

final public class AirlineSystem implements AirlineInterface {
  private ArrayList <String> cityNames = new ArrayList <String>();
  private Digraph G = null;
  private static final int INFINITY = Integer.MAX_VALUE;
  public int indexS = -1; public int indexD = -1; 
  //check its right file
  public boolean loadRoutes(String fileName){
    try{
      Scanner fileScan = new Scanner(new FileInputStream(fileName));
      int v = Integer.parseInt(fileScan.nextLine());
      G = new Digraph(v);

      //cityNames = new String[v];
      for(int i=0; i<v; i++){
        cityNames.add(fileScan.nextLine());
      }

      while(fileScan.hasNext()){
        String source = cityNames.get(fileScan.nextInt() - 1); 
        String destination = cityNames.get(fileScan.nextInt() - 1); 
        int distance = fileScan.nextInt();
        Double price = fileScan.nextDouble();

        G.addEdge(new Route(source, destination, distance, price));
        G.addEdge(new Route(destination, source, distance, price));
        if(fileScan.hasNextLine()) fileScan.nextLine();
      } 
      fileScan.close();
  }//end try
    catch(Exception e){
      return false;
    }

    return true;
  }//end load

  public Set<String> retrieveCityNames() {
    Set<String> citySet = new HashSet<String>();
    //System.out.println("v: "+G.v);
    if(cityNames != null){
      //iterate through the array of city names 
      //add the city String to the new Set
      for(String s : cityNames)
        citySet.add(s); 
  
      return citySet;
    }
    else return null; 
  }
 // !! still need to do exception !!!!!!! !!!!!
  public Set<Route> retrieveDirectRoutesFrom(String city)
    throws CityNotFoundException {
      
      Set<Route> directSet = new HashSet<Route>();
    
      if(!there(city)) throw new CityNotFoundException(city); 

      if(G.adj(indexS) == null) return directSet; 
      //adding each route in the adj list to the directSet
      for (Route e : G.adj(indexS)) {
        directSet.add(e);
        }
    return directSet;
  }

  public Set<ArrayList<String>> fewestStopsItinerary(String source,
    String destination) throws CityNotFoundException {

    Set<ArrayList<String>> hops = new HashSet<ArrayList<String>>();
    ArrayList<String> inHops = new ArrayList<String>();
    if(G == null) return hops; 

    String inThere = there(source, destination);
    if(!inThere.equals("true")) throw new CityNotFoundException(inThere); 
    else {
      //System.out.println("indexS: " + indexS + " indexD "+indexD);

      G.bfs(indexS);
      //no route
      if(!G.marked[indexD]) return hops;
        
      //intitialize a new stack<Integer> FILO 
      Stack<Integer> path = new Stack<>();
      //write a loop that starts at the end of the path moving to the parent vertex 
      //until we reach the source. 
      //System.out.println("indexS: " + indexS + " indexD "+indexD);
      for(int x = indexD; x != indexS; x = G.edgeTo[x]){
        //inside for loop push the vertex into the stack
        path.push(x);
      } 
      //push the source onto the stack
      path.push(indexS);
      int shortPath = path.size()-1; //# of hops
      
      while(!path.empty()){
        //turns Integer path.pop value into an int 
        //adds city to inHops LinkedList of string city names
        //this is list of one path          
        inHops.add(cityNames.get(path.pop().intValue()));
      }
    //adds this path to the Set of paths
    hops.add(inHops);
    return hops;
    }
  }

  public Set<ArrayList<Route>> shortestDistanceItinerary(String source,
    String destination) throws CityNotFoundException {

    Set<ArrayList<Route>> sdi = new HashSet<ArrayList<Route>>();
    ArrayList<Route> inSdi = new ArrayList<Route>();
    Set<Route> r1 = new HashSet<Route>();
    // for(int i = G.v; i < G.adj.length; i++)
    //     inSdi.(i) = new ArrayList<Route>();
    if(G == null) return sdi;

    String inThere = there(source, destination);
    if(!inThere.equals("true")) throw new CityNotFoundException(inThere); 
    
    //System.out.println("indexS: " + indexS + " indexD "+indexD);
    G.dijkstras(indexS, indexD);
    //no routes
    if(!G.marked[indexD]) return sdi;

    Stack<Integer> path = new Stack<>();
    //System.out.println("indexS: " + indexS + " indexD "+indexD);
    for (int x = indexD; x != indexS; x = G.edgeTo[x]){
      path.push(x);
      }
    int miles = G.distTo[indexD]; //# of miles 

    int prevVertex = indexS;
    while(!path.empty()){
      int v = path.pop();
      double pric = 0;
      String indexPV = cityNames.get(prevVertex);
      String indexV = cityNames.get(v);
      r1 = retrieveDirectRoutesFrom(indexPV);
      for(Route e : r1){
        if(e.destination.equals(indexV)){ pric = e.price; break;}
      }
      inSdi.add(new Route (indexPV, indexV, 
          G.distTo[v] - G.distTo[prevVertex], pric ));
      //System.out.print(G.distTo[v] - G.distTo[prevVertex] + " "
      //                  + cityNames[v] + " ");
      prevVertex = v;
    }
    sdi.add(inSdi);
    return sdi;
  }//end SDI

  public Set<ArrayList<Route>> shortestDistanceItinerary(String source,
    String transit, String destination) throws CityNotFoundException {

    Set<ArrayList<Route>> sdi = new HashSet<ArrayList<Route>>();
    ArrayList<Route> inSdi = new ArrayList<Route>();
    Set<ArrayList<Route>> sdi2 = new HashSet<ArrayList<Route>>();
    ArrayList<Route> inSdi2 = new ArrayList<Route>();
    Set<ArrayList<Route>> sdi3 = new HashSet<ArrayList<Route>>();

    sdi = shortestDistanceItinerary(source, transit);
    sdi2 = shortestDistanceItinerary(transit, destination);

    for(ArrayList<Route> e:sdi){
      inSdi = e; break;}
    for(ArrayList<Route> f:sdi2){
      inSdi2 =f; break;}
    
    for(Route g: inSdi2)
      inSdi.add(g);
    
    sdi3.add(inSdi);
    return sdi3;
  }

  public boolean addCity(String city){
    //checks to see if city already in list
    // for(String dup : cityNames){
    //   if(dup.equals(city)) return false;
    // }
    if(there(city)) return false;
    //resize adj list
    if(G.adj.length >= G.v){
      G.adj = Arrays.copyOf(G.adj, G.adj.length * 2);
      for(int i = G.v; i < G.adj.length; i++)
        G.adj[i] = new LinkedList<Route>();
    }
      //increments vertices
    G.v = G.v + 1;
    //adds city to original list 
    cityNames.add(city);
    return true;
  }

  public boolean addRoute(String source, String destination, int distance,
    double price) throws CityNotFoundException {
    String inThere = there(source, destination);
    if(!inThere.equals("true")) throw new CityNotFoundException(inThere); 
    else {
      //create bidirectional routes with param & Set for direct routes method
      Route other = new Route(source, destination, distance, price);
      Route other2 = new Route(destination, source, distance, price);
      Set<Route> rl = new HashSet<Route>();
      return addRouteHelper(other, other2, rl);
    }
  }
  private boolean addRouteHelper(Route other, Route other2, Set<Route> rl) throws CityNotFoundException{

    //call direct routes to get a list of the routes coming from this city
    rl = retrieveDirectRoutesFrom(other.source);
    for(Route e : rl){
      //if the e route and the inputted route are the same return false. already exists
      //also checks for bidirectional match
      if(e.equals(other)) return false;}
    
    //else add the bidirectional routes
    G.addEdge(other);
    G.addEdge(other2);
    return true;
  }

  public boolean updateRoute(String source, String destination, int distance,
  double price) throws CityNotFoundException {
    String inThere = there(source, destination);
    if(!inThere.equals("true")) throw new CityNotFoundException(inThere); 
    else {
      Route other = new Route(source, destination, distance, price);
      Set<Route> r1 = new HashSet<Route>();
      Set<Route> r2 = new HashSet<Route>();
      return updateRouteHelper(other, r1, r2);
    }
}

private boolean updateRouteHelper(Route other, Set<Route> r1, Set<Route> r2) 
  throws CityNotFoundException{
  boolean there = false;
  //call direct routes to get a list of the routes coming from this city
  r1 = retrieveDirectRoutesFrom(other.source);
  r2 = retrieveDirectRoutesFrom(other.destination);
  for(Route e : r1){
    //if the e route and the inputted route are the same 
    //two cities update info
    //equalEndPoints out of Route class
    if((e.source.equals(other.source) && e.destination.equals(other.destination))){
      e.distance = other.distance;
      e.price = other.price;
      there = true;
    }
  }
  for(Route d : r2){
    //if the e route and the inputted route are the same 
    //two cities update info
    //equalEndPoints out of Route class
    if((d.source.equals(other.destination) && d.destination.equals(other.source))){
      d.distance = other.distance;
      d.price = other.price;
      there = true;
    }
  }
  return there;
}

  //helper method
  //if the city is found in the * Airline system return true
  //also sets the index of the city
  private boolean there(String city){
    //checks to see if city already in list
    //has to be more efficient way????????????????
    indexS = -1;
    boolean there = false;
    for(int s = 0; s < G.v; s++){
      if(cityNames.get(s).equals(city)) { there = true; indexS = s;}
    }
    return there;    
  }
  //helper method
  //if any of the two cities are not found in the * Airline system return false
  //also sets the index of the cities
  private String there(String city, String city2){
    indexS = -1; indexD =-1;
    //checks to see if city already in list
    //has to be more efficient way????????????????
    String there = "true";
    for(int s = 0; s < G.v; s++){
      if(cityNames.get(s).equals(city)){ 
      indexS = s; break;
      }
    }
    for(int d = 0; d < G.v; d++){
      if(cityNames.get(d).equals(city2)){ 
        indexD = d; break;
      }
    }
    if(indexS == -1) there = city;
    else if(indexD == -1) there = city2;
    return there;
    // for(int s = 0; s < G.v; s++){
    //   if(cityNames.get(s).equals(city)){ 
    //     indexS = s;
    //     for(int d = 0; d < G.v; d++){
    //       if(cityNames.get(d).equals(city2)){
    //         indexD = d;
    //         return there;
    //       }
    //     }
    //     return city2;
    //   }
    // }
    // return city;
  }

  private class Digraph {
    private int v;
    private int e;
    private LinkedList<Route>[] adj;
    private boolean[] marked;  // marked[v] = is there an s-v path
    private int[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
    private int[] distTo;      // distTo[v] = number of edges shortest s-v path

    /**
    * Create an empty digraph with v vertices.
    */
    public Digraph(int v) {
      if (v < 0) throw new RuntimeException("Number of vertices must be nonnegative");
      this.v = v;
      this.e = 0;
      @SuppressWarnings("unchecked")
      LinkedList<Route>[] temp =
      (LinkedList<Route>[]) new LinkedList[v];
      adj = temp;
      for (int i = 0; i < v; i++)
        adj[i] = new LinkedList<Route>();
    }

    /**
    * Add the edge e to this digraph.
    */
    public void addEdge(Route edge) {
      //find int value for the source city name
      int s = 0;
      for (int i = 0; i < G.v; i++) {
        if(cityNames.get(i).equals(edge.source)){ 
          s = i; 
          break;
        }
      }
      adj[s].add(edge);
      e++; //doubled does it need to be divided by 2?
    }

    /**
    * Return the edges leaving vertex v as an Iterable.
    * To iterate over the edges leaving vertex v, use foreach notation:
    * <tt>for (DirectedEdge e : graph.adj(v))</tt>.
    */
    public Iterable<Route> adj(int v) {
      if(adj[v]==null) return null;
      return adj[v];
    }

    public void bfs(int source) {
      marked = new boolean[this.v];
      distTo = new int[this.v];
      edgeTo = new int[this.v];

      Queue<Integer> q = new LinkedList<Integer>();
      for (int i = 0; i < v; i++){
        distTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0; // dist from start to start 
      marked[source] = true; //mark as has been visited
      q.add(source); //add starting point to q. source is entered in

      while (!q.isEmpty()) {
        //pops the root node off and adds that q to v
        int v = q.remove(); 
        for (Route e : adj(v)) { //iterates through the edges in the adj list 
          int d = -1;
          for (int m = 0; m < G.v; m++) {
            if(cityNames.get(m).equals(e.destination)){ 
            d = m; 
            break;
            }
          }
          if (!marked[d]) {
            //update edgeTo of w.to to parent of w.to
            edgeTo[d] = v;
            //update distTo of w.to to parent + 1
            distTo[d] = distTo[v] + 1; 
            //mark the edge destination as visited
            marked[d] = true;
            //add w.to to the q
            q.add(d);
          }
        }
      }
    }//end bfs

    public void dijkstras(int source, int destination) {
      marked = new boolean[this.v];
      distTo = new int[this.v];
      edgeTo = new int[this.v];
      //int dest = -1;
      //System.out.println("d indexS: " + indexS + " indexD "+indexD);


      for (int i = 0; i < v; i++){
        distTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      marked[source] = true;
      int nMarked = 1;

      int current = source;
      
      //while marked vertices is less than vertices in the graph
      while (nMarked < this.v) {
        for (Route e : adj(current)) {
          // for(int d = 0; d < G.v; d++){
          //   if(cityNames.get(d).equals(e.destination))
          //     dest = d;
          // }
          //if distance so far + distance to e route < dist to destination
          if (distTo[current]+e.distance < distTo[cityNames.indexOf(e.destination)]) {
	          //updates edgeTo and distTo
            edgeTo[cityNames.indexOf(e.destination)] = current;
            distTo[cityNames.indexOf(e.destination)] = distTo[current] + e.distance;
          }
        }
        //Find the vertex with minimim path distance
        //This can be done more effiently using a priority queue!
        int min = INFINITY;
        current = -1;

        //finds vertex with smallest distTo from source vertex
        for(int i=0; i<distTo.length; i++){
          if(marked[i])
            continue;
          if(distTo[i] < min){
            min = distTo[i];
            current = i;
          }
        }
	      //Update marked[] and nMarked. Check for disconnected graph.
        if(current >= 0){
          //mark visited
          marked[current] = true;
          //increment marked vertices
          nMarked++;
        }
        else break;
      }
    }//end dijkstras 
  }//end digraph class
}//end Airline class
