//TO-DO Add necessary imports
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.security.auth.x500.X500Principal;
import java.io.FileInputStream;
import java.util.*;


public class AutoComplete {

  //TO-DO: Add instance variable: you should have at least the tree root
  private DLBNode root; 

  public AutoComplete(String dictFile) throws java.io.IOException {
    //TO-DO Initialize the instance variables  
    Scanner fileScan = new Scanner(new FileInputStream(dictFile));
    int len = 0;
    while(fileScan.hasNextLine()){
      StringBuilder word = new StringBuilder(fileScan.nextLine());
      if(word.length() > len) len = word.length();
      //TO-DO call the public add method or the private helper method if you have one
      add(word);
    }
    fileScan.close();
    //printTree(root, len);
  }

  /**
   * Part 1: add, increment score, and get score
   */

  //add word to the tree
  public void add(StringBuilder word){
    //TO-DO Implement this method
    if (word == null) throw new IllegalArgumentException("calls add() with a null word");
    root = add(root, word, 0);
  }

  /*Helper method for add. This method takes in the node, the word we are adding
  and the position. We traversed through the tree using recursion. When we are 
  calling the add method again on a child we increase the depth (pos) to move down
  the tree. For siblings we keep the depth the same to move to the right. Once we
  find the depth that equals the length of the word being added, we assign isWord 
  the true value and exit.*/ 
  private DLBNode add(DLBNode x, StringBuilder word, int pos) {
    DLBNode result = x;
    if (x == null){
        result = new DLBNode(word.charAt(pos), 0);
        if(pos < word.length()-1){
          result.child = add(result.child, word, pos+1); 
        } else {
          result.isWord = true;
         }
    } else if(x.data == word.charAt(pos)) {
        if(pos < word.length()-1){
          result.child = add(result.child, word, pos+1); 
        } else {
          result.isWord = true;
        }
    } else {
      result.sibling = add(result.sibling, word, pos);
    }
    return result;
  }

  //increment the score of word
  public void notifyWordSelected(StringBuilder word){
    //TO-DO Implement this method
    notifyWordSelected(root, word, 0);
  }
  
  /*helper method for notifyWordSelected. 
  notifyWordSelected takes in the root node, the word we are updating the score of and position
  we convert word into a string to pass it through the getNode method which returns the
  last node of the word. This allows us to then update the score of that node*/
  private void notifyWordSelected(DLBNode root, StringBuilder word, int pos){
    if (root == null) return;
    String letter = word.toString();
    root = getNode(root, letter, pos);
    root.score = root.score + 1;    
    return;
  }
  
  //get the score of word
  public int getScore(StringBuilder word){
    //TO-DO Implement this method
    return getScore(root, word, 0);
  }

  /*helper method for getScore. 
  getScore takes in the root node, the word we are getting the score of and position
  we convert word into a string to pass it through the getNode method which returns the
  last node of the word. This allows us to then get the score of that node and return it*/
  private int getScore(DLBNode root, StringBuilder word, int pos){
  int scoreF = 0;
  if (root == null)return 0;
  String letter = word.toString();
  root = getNode(root, letter, pos);
  scoreF = root.score;
  return scoreF;
  }

  /**
   * Part 2: retrieve word suggestions in sorted order.
   */
  
  /*retrieves a sorted list of autocomplete words for the given word. 
  Before calling the helper method it check to see if the given word is
  a valid word in the dictionary. if so it adds it to the list
  recursion then begins using the child to the last letter of word
  The list is sorted in descending order based on score.*/
  public ArrayList<Suggestion> retrieveWords(StringBuilder word){
    ArrayList<Suggestion> list = new ArrayList<>(); 
    //use getNode to find the node of the last letter of current                                       
    DLBNode end = getNode(root, word.toString(), 0);
    if (end == null) return list;
    //if the StringBuilder being passed through (current) is a word
    //then we add it onto the list before traversing it 
    if(end.isWord) list.add(new Suggestion(end.score, word));
    retrieveWords(end.child, list, word);
    Collections.sort(list, Collections.reverseOrder());
    return list;
  }
  /*helper method for retrieveWords. 
  retrieveWords takes in the child node of the last letter in the given word, 
  the list we keep the suggestions in, and the given word. We append the letter
  onto the given word. If that is a valid word we then add it to the list
  We go down the tree utnil x == null and thats when we go to siblings. */
  private void retrieveWords(DLBNode node, ArrayList<Suggestion> list, 
                              StringBuilder current){
    if(node==null) return;
    
    //traverse through DLB to find all words 
    DLBNode x = node; 
    if(x != null){
      current.append(x.data);
      if(x.isWord) {
        StringBuilder cur = new StringBuilder(current); 
        list.add(new Suggestion(x.score, cur));
      }
      if(x.child != null) retrieveWords(x.child, list, current); 
      current.deleteCharAt(current.length()-1);
      if(x.sibling != null) retrieveWords(x.sibling, list, current); 
    }
  }

  /**
   * Helper methods for debugging.
   */

  //Print the subtree after the start string
  public void printTree(String start){
    System.out.println("==================== START: DLB Tree Starting from "+ start + " ====================");
    DLBNode startNode = getNode(root, start, 0);
    if(startNode != null){
      printTree(startNode.child, 0);
    }
    System.out.println("==================== END: DLB Tree Starting from "+ start + " ====================");
  }

  //A helper method for printing the tree
  private void printTree(DLBNode node, int depth){
    if(node != null){
      for(int i=0; i<depth; i++){
        System.out.print(" ");
      }
      System.out.print(node.data);
      if(node.isWord){
        System.out.print(" *");
      }
        System.out.println(" (" + node.score + ")");
      printTree(node.child, depth+1);
      printTree(node.sibling, depth);
    }
  }

  //return a pointer to the node at the end of the start string. 
  //Called from printTree.
  private DLBNode getNode(DLBNode node, String start, int index){
    DLBNode result = node;
    if(node != null){
      if((index < start.length()-1) && (node.data.equals(start.charAt(index)))) {
          result = getNode(node.child, start, index+1);
      } else if((index == start.length()-1) && (node.data.equals(start.charAt(index)))) {
          result = node;
      } else {
          result = getNode(node.sibling, start, index);
      }
    }
    return result;
  }


  //A helper class to hold suggestions. 
  //Each suggestion is a (word, score) pair. 
  //This class is Comparable to itself.
  public class Suggestion implements Comparable<Suggestion>{
      public int score; 
      public StringBuilder word; 
    public Suggestion(int score, StringBuilder word){
      this.score = score;
      this.word = word;
    }

    @Override
    //-1 if this.score < s.score
    //1 if this.score > s.score
    //0 if this.score = s.score
    public int compareTo(Suggestion s){ 
      return Integer.compare(this.score, s.score); 
    }
  }

  //The node class.
  private class DLBNode{
    private Character data;
    private int score;
    private boolean isWord;
    private DLBNode sibling;
    private DLBNode child;

    private DLBNode(Character data, int score){
        this.data = data;
        this.score = score;
        isWord = false;
        sibling = child = null;
    }
  }
}
