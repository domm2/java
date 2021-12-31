import java.util.*;
import java.io.*;
import java.util.Set;
import java.util.ArrayList;


final public class AirlineSystem implements AirlineInterface {
  private ArrayList <String> cityNames = new ArrayList <String>();
  private Digraph G = null;
  private static final int INFINITY = Integer.MAX_VALUE;
  private boolean[] almarked = null;
  public int[] component = null;
  public int indexS = -1; public int indexD = -1; 
  public int cpt = 1;
  /**
  * reads the city names and the routes from a file
  * @param fileName the String file name
  * @return true if routes loaded successfully and false otherwise
  */
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
  }

  /**
  * writes the city names and the routes into a file
  * @param fileName the String file name
  * @return true if routes saved successfully and false otherwise
  */
  public boolean saveRoutes(String fileName){
    try{
      FileWriter myWriter = new FileWriter(fileName);
      myWriter.write(Integer.toString(G.v));

      for(String b:cityNames)
        myWriter.write("\n" + b);
      
      for(String n:cityNames){
        Set<Route> directSet = retrieveDirectRoutesFrom(n);
        
        for(Route e: directSet){
          if(cityNames.indexOf(n)<cityNames.indexOf(e.destination)){
            int s = cityNames.indexOf(e.source)+1;
            int d = cityNames.indexOf(e.destination)+1;
            myWriter.write("\n" + s + " " + d + " " + e.distance + " " + e.price); 
          }
        }
      }
      myWriter.close();    
    }catch(Exception e){ 
      return false;
    }
    return true;
  }


  /**
  * returns the set of city names in the Airline system
  * @return a (possibly empty) Set<String> of city names
  */
  public Set<String> retrieveCityNames(){
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

  /**
  * returns the set of direct routes out of a given city
  * @param city the String city name
  * @return a (possibly empty) Set<Route> of Route objects representing the
  * direct routes out of city
  * @throws CityNotFoundException if the city is not found in the Airline
  * system
  */
  public Set<Route> retrieveDirectRoutesFrom(String city)
    throws CityNotFoundException{

      Set<Route> directSet = new HashSet<Route>();
    
      if(!there(city)) throw new CityNotFoundException(city); 

      if(G.adj(indexS) == null) return directSet; 
      //adding each route in the adj list to the directSet
      for (Route e : G.adj(indexS)) {
        directSet.add(e);
        }
      return directSet;
  }

  /**
  * finds cheapest path(s) between two cities
  * @param source the String source city name
  * @param destination the String destination city name
  * @return a (possibly empty) Set<ArrayList<Route>> of cheapest
  * paths. Each path is an ArrayList<Route> of Route objects that includes a
  * Route out of the source and a Route into the destination.
  * @throws CityNotFoundException if any of the two cities are not found in the
  * Airline system
  */
  public Set<ArrayList<Route>> cheapestItinerary(String source,
    String destination) throws CityNotFoundException{
    
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
      //int miles = G.priceTo[indexD]; //# of miles 
  
      int prevVertex = indexS;
      while(!path.empty()){
        int v = path.pop();
        int dis = 0;
        String indexPV = cityNames.get(prevVertex);
        String indexV = cityNames.get(v);
        r1 = retrieveDirectRoutesFrom(indexPV);
        for(Route e : r1){
          if(e.destination.equals(indexV)) dis = e.distance;
        }
        inSdi.add(new Route (indexPV, indexV, dis,
            G.priceTo[v] - G.priceTo[prevVertex]));
        
        prevVertex = v;
      }
      sdi.add(inSdi);
      return sdi;
  }


  /**
  * finds cheapest path(s) between two cities going through a third city
  * @param source the String source city name
  * @param transit the String transit city name
  * @param destination the String destination city name
  * @return a (possibly empty) Set<ArrayList<Route>> of cheapest
  * paths. Each path is an ArrayList<Route> of city names that includes
  * a Route out of source, into and out of transit, and into destination.
  * @throws CityNotFoundException if any of the three cities are not found in
  * the Airline system
  */
  public Set<ArrayList<Route>> cheapestItinerary(String source,
    String transit, String destination) throws CityNotFoundException{

    Set<ArrayList<Route>> sdi = new HashSet<ArrayList<Route>>();
    ArrayList<Route> inSdi = new ArrayList<Route>();
    Set<ArrayList<Route>> sdi2 = new HashSet<ArrayList<Route>>();
    ArrayList<Route> inSdi2 = new ArrayList<Route>();
    Set<ArrayList<Route>> sdi3 = new HashSet<ArrayList<Route>>();

    sdi = cheapestItinerary(source, transit);
    sdi2 = cheapestItinerary(transit, destination);

    for(ArrayList<Route> e:sdi){
      inSdi = e; break;}
    for(ArrayList<Route> f:sdi2){
      inSdi2 =f; break;}
    
    for(Route g: inSdi2)
      inSdi.add(g);
    
    sdi3.add(inSdi);
    return sdi3;
    }

  /**
   * finds one Minimum Spanning Tree (MST) for each connected component of
   * the graph
   * @return a (possibly empty) Set<Set<Route>> of MSTs. Each MST is a Set<Route>
   * of Route objects representing the MST edges.
   */
  public Set<Set<Route>> getMSTs(){
    Set<Set<Route>> mst = new HashSet<Set<Route>>();
    Set<Route> inMst = new HashSet<Route>();
    Set<Route> r1 = new HashSet<Route>();
    component = new int[G.v];

    //dij if dist curr vert + edge < dis neighbor update 
    //if edge distance < best edge value
    //bfs to find components start prims then add to set 
    //component array and global var 
    //visit vert comp = comp global
    //increment global once u call bfs again
    for(int i=0; i<G.v; i++)
      component[i] = -1;
    for(String s : cityNames){
      int indexOfs = cityNames.indexOf(s);
      int indexOfd = G.v-1;
      G.dijkstras(indexOfs, indexOfd);

      if(!G.marked[indexOfd]) return mst;
  
      Stack<Integer> path = new Stack<>();
      //System.out.println("indexS: " + indexS + " indexD "+indexD);
      for (int x = indexOfd; x != indexOfs; x = G.edgeTo[x]){
        path.push(x);
        }
      //int miles = G.priceTo[indexD]; //# of miles 
  
      int prevVertex = indexOfs;
      while(!path.empty()){
        int v = path.pop();
        int dis = 0;
        String indexPV = cityNames.get(prevVertex);
        String indexV = cityNames.get(v);
        try{r1 = retrieveDirectRoutesFrom(indexPV);}catch(Exception e){ return mst;}
        for(Route e : r1){
          if(e.destination.equals(indexV)) dis = e.distance;
        }
        inMst.add(new Route (indexPV, indexV, dis,
            G.priceTo[v] - G.priceTo[prevVertex]));
        
        prevVertex = v;
      }
      mst.add(inMst);
      cpt++;
      return mst;
    }

    // if(G == null) return mst; 
    // G.mstAlg(0);
    // //call prims until all cities are marked
    // if(!G.marked[G.v-1]) return mst;//returning null marked is null

    // for(String n:cityNames){
    //   try{inMst = retrieveDirectRoutesFrom(n);} catch(Exception e){return mst;}
    //   mst.add(inMst);
    // }
    return mst;
      //intitialize a new stack<Integer> FILO 
      //Stack<Integer> path = new Stack<>();
      // //write a loop that starts at the end of the path moving to the parent vertex 
      // //until we reach the source. 
      // for(int x = G.v-1; x != 0; x = G.edgeTo[x]){
      //   //inside for loop push the vertex into the stack
      //   path.push(x);
      // } 
      // //push the source onto the stack
      // path.push(0);
      
      // int prevVertex = 0; 
      // while(!path.empty()){
      //   //turns Integer path.pop value into an int 
      //   //adds city to inMst Set of string city names
      //   //this is set of one path  
      //   //inMst.add(cityNames.get(path.pop().intValue()));
      //   int v = path.pop();
      //   double pric = 0;
      //   int dis = 0;
      //   String indexPV = cityNames.get(prevVertex);
      //   String indexV = cityNames.get(v);
      //   try{ r1 = retrieveDirectRoutesFrom(indexPV);}
      //   catch(Exception w){return mst;}
      //   for(Route e : r1){
      //     if(e.destination.equals(indexV)){ pric = e.price; dis = e.distance; break;}
      //   }
      //   inMst.add(new Route(indexPV, indexV, dis, pric)); 
      //   prevVertex = v;        
      // }
    //adds this path to the Set of paths
  }

  /**
   * finds all itineraries starting out of a source city and within a given
   * price
   * @param city the String city name
   * @param budget the double budget amount in dollars
   * @return a (possibly empty) Set<ArrayList<Route>> of paths with a total cost
   * less than or equal to the budget. Each path is an ArrayList<Route> of Route
   * objects starting with a Route object out of the source city.
   */
  public Set<ArrayList<Route>> tripsWithin(String city, double budget)
    throws CityNotFoundException {

    if(!there(city)) throw new CityNotFoundException(city); 
    else {
      double priceSoFar = 0;
      almarked = new boolean[G.v];
      for(int i=0; i<G.v; i++) almarked[i] = false;
      Set<ArrayList<Route>> setOfPaths = new HashSet<ArrayList<Route>>();
      ArrayList<Route> curPath = new ArrayList<Route>();
      System.out.println();
      return solve(city, priceSoFar, budget, setOfPaths, curPath);
    }//end else
  }

  /**
   * finds all itineraries within a given price regardless of the
   * starting city
   * @param  budget the double budget amount in dollars
   * @return a (possibly empty) Set<ArrayList<Route>> of paths with a total cost
   * less than or equal to the budget. Each path is an ArrayList<Route> of Route
   * objects.
   */
  public Set<ArrayList<Route>> tripsWithin(double budget){
    Set<ArrayList<Route>> setOfPaths = new HashSet<ArrayList<Route>>();

    try{
      for(String c : cityNames){
        setOfPaths.addAll(tripsWithin(c, budget));
      }//endfor
    }catch(Exception e) {return setOfPaths;}
    return setOfPaths;
  }

  private Set<ArrayList<Route>> solve(String city, double priceSoFar, double budget,
    Set<ArrayList<Route>> setOfPaths, ArrayList<Route> curPath) throws CityNotFoundException{

      //can aslo go backwards because previous city is in this city routes list too
      //goes back n forth from city to city
      //set marked and check if neighbor is marked then dont recurse on it 
      Set<Route> from = retrieveDirectRoutesFrom(city);
      //System.out.println("printing setofpaths " + setOfPaths);
      if(from==null) return setOfPaths;
      //System.out.println(cityNames.indexOf(city));
      for(Route a : from){
        //if(!almarked[cityNames.indexOf(a.destination)]){
          almarked[cityNames.indexOf(a.source)] = true;
          //if priceSoFar + a.price <= budget
          if(priceSoFar + a.price <= budget&&!almarked[cityNames.indexOf(a.destination)]){
            //apply choice. add a to path. add path to setOfPaths
            curPath.add(a); setOfPaths.add(new ArrayList<Route>(curPath));
            //almarked[cityNames.indexOf(city)] = true;
            //almarked[cityNames.indexOf(a.destination)] = true;
            //increase priceSoFar
            priceSoFar += a.price;
            if(priceSoFar<budget)
              //recurse using next city as start 
              solve(a.destination, priceSoFar, budget, setOfPaths, curPath);
            //undo choice
            int index = curPath.size() -1;
            curPath.remove(index);
            //decrease priceSoFar
            priceSoFar -= a.price;
          }//endif
          almarked[cityNames.indexOf(a.source)] = false;
        //}
      }//endfor
    return setOfPaths;
  }
  
  
  /**
   * delete a given non-stop route from the Airline's schedule. Both directions
   * of the route have to be deleted.
   * @param  source the String source city name
   * @param  destination the String destination city name
   * @return true if the route is deleted successfully and false if no route
   * existed between the two cities
   * @throws CityNotFoundException if any of the two cities are not found in the
   * Airline system
   */
  public boolean deleteRoute(String source, String destination)
    throws CityNotFoundException{
    String inThere = there(source, destination);
    if(!inThere.equals("true")) throw new CityNotFoundException(inThere); 
    else {
      return deleteRouteHelper(source, destination, indexS, indexD);
    }
    }

  private boolean deleteRouteHelper(String source, String destination, int indexS, int indexD) 
  throws CityNotFoundException{
  boolean there = false;
  //Route del = null;
  //call direct routes to get a list of the routes coming from this city
  for(Route e : G.adj[indexS]){
    //if the e route and the inputted route are the same, delete
    //equalEndPoints out of Route class
    if(e.source.equals(source) && e.destination.equals(destination)){
      //System.out.print("removing " + e.destination);
      //del = e;
      G.adj[indexS].remove(e);
      G.e--; 
      there = true;
      break;
    }
  }
  // G.adj[indexS].remove(del); G.adj[indexD].remove(del);
  // G.e--;   G.e--;
  // there = true;
 //System.out.print("removing " + indexD);
  for(Route d : G.adj[indexD]){
    //if the e route and the inputted route are the same, delete
    //equalEndPoints out of Route class
    if(d.source.equals(destination) && d.destination.equals(source)){
      //System.out.print("removing " + d.destination);
      G.adj[indexD].remove(d);
      G.e--;
      there = true;
      break;
    }
  }
  return there;
}

  /**
   * delete a given city and all non-stop routes out of and into the city from
   * the Airline schedule.
   * @param  city  the String city name
   * @throws CityNotFoundException if the city is not found in the Airline system
   */
  public void deleteCity(String city) throws CityNotFoundException{
    if(!there(city)) throw new CityNotFoundException(city); 
    ArrayList<String> shold = new ArrayList<>();
    int copIndexS = indexS; 
    //System.out.println("word: "+ cityNames.get(copIndexS) + " indexS: " + copIndexS);

    //deletes routes to city from adj lists
    for(Route e : G.adj[copIndexS]){
      shold.add(e.destination);
     // deleteRoute(e.destination, city);
    }
    for(String s : shold)
      deleteRoute(s, city);
    // //deletes city's adj list
    @SuppressWarnings("unchecked")
    LinkedList<Route>[] copy = (LinkedList<Route>[]) new LinkedList[copIndexS];
    @SuppressWarnings("unchecked")
    LinkedList<Route>[] copy2 = (LinkedList<Route>[]) new LinkedList[G.v-(copIndexS+1)];
    int len = copy.length + copy2.length;
    @SuppressWarnings("unchecked")
    LinkedList<Route>[] copy3 = (LinkedList<Route>[]) new LinkedList[len];
   
    if(copIndexS != 0) 
      copy = Arrays.copyOfRange(G.adj, 0, copIndexS);
    copy2 = Arrays.copyOfRange(G.adj, copIndexS+1, G.v);
    //System.out.println("word: "+ cityNames.get(copIndexS) + " indexS: " + copIndexS);
    
    for(int i=0; i< copy.length; i++){
      copy3[i] = copy[i];
      //System.out.println("copy from 1 into 3: " + copy3[i]);
    }
    for(int i=copy.length, j=0; i< copy3.length; i++){
      copy3[i] = copy2[j]; j++;
      //System.out.println("copy from 2 into 3: " + copy3[i]);

    }

    G.adj = copy3;
    //deletes city from cityNames
    cityNames.remove(copIndexS); 
    G.v--;
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
  }

  private class Digraph {
    private int v;
    private int e;
    private LinkedList<Route>[] adj;
    private boolean[] marked;  // marked[v] = is there an s-v path
    private int[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
    private int[] distTo;      // distTo[v] = number of edges shortest s-v path
    private double[] priceTo;
    // private int small;
    // private int smIndex;
    // private int visited;
    // private int unconnected;
    // private Route hold;
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

    // public void mstAlg(int source) {
    //   marked = new boolean[this.v];
    //   distTo = new int[this.v];
    //   edgeTo = new int[this.v];
    //   moreAdj = new int[v];
    //   small = Integer.MAX_VALUE;
    //   smIndex = 0;
    //   visited = 0;
    //   unconnected =0;
    //   Digraph mst = new Digraph(v);

    //   //Queue<Integer> q = new LinkedList<Integer>();
    //   for (int j = 0; j < v; j++){
    //     distTo[j] = INFINITY;
    //     marked[j] = false;
    //     moreAdj[j] = adj[j].size();
    //   }

    //   visited = 1;
    //   distTo[source] = 0; // dist from start to start 
    //   marked[source] = true; //mark as has been visited
    //  // q.add(source); //add starting point to q. source is entered in
     
    //  //while there are more cities to visit
    //   while (visited<v) {
    //     for(int i = 0; i<v; i++){
    //       //if city is marked but has more unmarked neighbors
    //       if(marked[i] && moreAdj[i] > 0){
    //         for(Route e: adj[i]){
    //           if(!marked[cityNames.indexOf(e.destination)] && e.distance < small){
    //             small = e.distance; smIndex = i; hold = new Route(e.source, e.destination, e.distance, e.price);
    //           }
    //         }
    //       }


    //       if (hold!=null) {
    //         moreAdj[smIndex]--;
    //         moreAdj[cityNames.indexOf(hold.source)]--;

    //         marked[cityNames.indexOf(hold.source)] = true;
    //         mst.addEdge(hold);
    //         visited++;
    //       }
    //       else{
    //         int k = 0;
    //         while(true){
    //           if(marked[k]==false){
    //             marked[k] = true; break;
    //           }
    //           k++;
    //         }
    //         unconnected++; visited++;
    //       }
    //     }
    //   }
    //   G = mst;
    // }//end mst

    public void dijkstras(int source, int destination) {
      marked = new boolean[this.v];
      priceTo = new double[this.v];
      edgeTo = new int[this.v];
      //int dest = -1;
      //System.out.println("d indexS: " + indexS + " indexD "+indexD);


      for (int i = 0; i < v; i++){
        priceTo[i] = INFINITY;
        marked[i] = false;
      }
      priceTo[source] = 0;
      marked[source] = true;
      component[source] = cpt;
      int nMarked = 1;

      int current = source;
      
      //while marked vertices is less than vertices in the graph
      while (nMarked < this.v) {
        for (Route e : adj(current)) {
          // for(int d = 0; d < G.v; d++){
          //   if(cityNames.get(d).equals(e.destination))
          //     dest = d;
          // }
          //if price so far + price to e route < dist to destination
          if (priceTo[current]+e.price < priceTo[cityNames.indexOf(e.destination)]) {
	          //updates edgeTo and distTo
            edgeTo[cityNames.indexOf(e.destination)] = current;
            priceTo[cityNames.indexOf(e.destination)] = priceTo[current] + e.price;
          }
        }
        //Find the vertex with minimim path price
        double min = INFINITY;
        current = -1;

        //finds vertex with smallest distTo from source vertex
        for(int i=0; i<priceTo.length; i++){
          if(marked[i])
            continue;
          if(priceTo[i] < min){
            min = priceTo[i];
            current = i;
          }
        }
	      //Update marked[] and nMarked. Check for disconnected graph.
        if(current >= 0){
          //mark visited
          marked[current] = true;
          if(component[current]==-1)
            component[current] = cpt;
          //increment marked vertices
          nMarked++;
        }
        else break;
      }
    }//end dijkstras 
  }//end digraph class
}
