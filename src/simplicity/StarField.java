package simplicity;
// uncomment ^^^ this line after finishing testing
// of the class in PDE and before compiling it into library.


import java.util.ArrayList;
import processing.core.PApplet;
/**
* This class allows simulation of the "starry night" when
* stars are flying along the screen
*
* First you have to initialize the StarNight class with
* screen dimensions and quantity of stars.
*/
public class StarField
{
   private final int C_STAR_SIZE = 8;
   private int fieldWidth;
   private int fieldHeight;
   private ArrayList<Star> stars = new ArrayList<Star>();
   private PApplet sketch;
  
   public StarField(int ww, int hh, int qty, PApplet ssketch){
     // init random stars with random velocities, directed to the right.
     fieldWidth = ww;
     fieldHeight = hh;
     sketch = ssketch;
     initRandomStars(qty, stars);  
     
   }  
   
   
   public int getStarCount(){
      return stars.size();
   }
   
   public Star getStar(int i ){
      return stars.get(i);
   }
   
   private void initRandomStars(int qty, ArrayList<Star> theStars){
      for(int i = 0 ; i < qty;  i++){
          // init random star
          float randX = sketch.random(fieldWidth);
          float randY = sketch.random(fieldHeight);
          float randSpeed = sketch.random(5,15);
          Star star = new Star(randX, randY, randSpeed);
          theStars.add(star);
      }
      
   }
    
    
   /**
   * Just draws all the stars
   */
   public void draw(){
      for(int i = 0 ; i<  stars.size(); i++){
         drawStar(stars.get(i));
      }
   }
   
   /**
   * This method is just responsible to draw the star
   */
   private void drawStar(Star star){
      sketch.pushStyle();
      sketch.noStroke();
      sketch.fill(255);
      sketch.ellipse(star.x, star.y, C_STAR_SIZE, C_STAR_SIZE);
      sketch.popStyle();
   }
   
   /**
   * Updates position of the stars
   */
   public void step(){
      // just loop through all the stars and add velocity to their locaiton.
      // if go off screen, make them enter from another side of the screen
      for(int i = 0; i < stars.size(); i++){
         Star star = stars.get(i);
         star.update(); // just updates location
         star.checkEdges(fieldWidth, fieldHeight); // makes sure star is within boundaries
      }
   } 
   
   

   
    
   public static class Star
   {
      public float x;
      public float y;
      public float speed;
      
      public Star(float xx, float yy, float sspeed){
         x = xx;
         y = yy;
         speed = sspeed;
      }
      
      void update(){
          x += speed;
      }
      
      
      /**
      * Just makes sure that x/y are within boundaries,
      * if they're not, just forces them into the boundaries.
      */
      void checkEdges(float maxWidth, float maxHeight){
          while ( x >= maxWidth) {
              x -= maxWidth;
          }
          
          while ( x < 0 ){
             x += maxWidth;
          }
          
          while ( y >= maxHeight ){
              y -= maxHeight;
          }
          
          while ( y < 0 ){
              y += maxHeight;
          }
      }
      
   }
  
}



