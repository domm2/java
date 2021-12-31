import java.util.Set;
import java.util.ArrayList;

public interface AirlineInterface {

  /**
  * reads the city names and the routes from a file
  * @param fileName the String file name
  * @return true if routes loaded successfully and false otherwise
  */
  public boolean loadRoutes(String fileName);

  /**
  * writes the city names and the routes into a file
  * @param fileName the String file name
  * @return true if routes saved successfully and false otherwise
  */
  public boolean saveRoutes(String fileName);


  /**
  * returns the set of city names in the Airline system
  * @return a (possibly empty) Set<String> of city names
  */
  public Set<String> retrieveCityNames();

  /**
  * returns the set of direct routes out of a given city
  * @param city the String city name
  * @return a (possibly empty) Set<Route> of Route objects representing the
  * direct routes out of city
  * @throws CityNotFoundException if the city is not found in the Airline
  * system
  */
  public Set<Route> retrieveDirectRoutesFrom(String city)
  throws CityNotFoundException;

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
  String destination) throws CityNotFoundException;


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
  String transit, String destination) throws CityNotFoundException;

  /**
   * finds one Minimum Spanning Tree (MST) for each connected component of
   * the graph
   * @return a (possibly empty) Set<Set<Route>> of MSTs. Each MST is a Set<Route>
   * of Route objects representing the MST edges.
   */
  public Set<Set<Route>> getMSTs();

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
    throws CityNotFoundException;

  /**
   * finds all itineraries within a given price regardless of the
   * starting city
   * @param  budget the double budget amount in dollars
   * @return a (possibly empty) Set<ArrayList<Route>> of paths with a total cost
   * less than or equal to the budget. Each path is an ArrayList<Route> of Route
   * objects.
   */
  public Set<ArrayList<Route>> tripsWithin(double budget);

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
    throws CityNotFoundException;

  /**
   * delete a given city and all non-stop routes out of and into the city from
   * the Airline schedule.
   * @param  city  the String city name
   * @throws CityNotFoundException if the city is not found in the Airline system
   */
  public void deleteCity(String city) throws CityNotFoundException;

}
