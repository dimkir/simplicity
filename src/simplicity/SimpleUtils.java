package simplicity;

import processing.core.PApplet;

/**
* This class is a template which would allow you to add extra functionality.
*/
public class SimpleUtils
{
   private PApplet sketch;

   public SimpleUtils(PApplet callerSketch){
       sketch = callerSketch;
   }



  public void randomCircle(){
     float xx = sketch.random(sketch.width);
     float yy = sketch.random(sketch.height);
     float sz = sketch.random(10,20);
     //fill(#FF0000);
     sketch.pushStyle();
     sketch.fill(0xFFFF0000);
     sketch.noStroke();
     sketch.ellipse(xx, yy, sz, sz);
     sketch.popStyle();
  }
   
 
}
