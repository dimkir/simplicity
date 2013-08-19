/**
* Remember when you develop your library using this template and
* this specific sketch, you need to have line "package simplicity;" be commented off
* on top of any of the *.java files you're working on.
*
* Whereas when you finished testing your library here in this Sketch
* and want to compile it to standalone library, you must have 
* as the first line of each of the java file:
package simpilicity;
*
* If you don't do that, other sketches won't be able to see your classes
* eg. BouncingBall and will be telling you that they can't find class named "BouncingBall".
*/


BouncingBall ball;
StarField sfield;
void setup()
{
     size(800,600);
   ball = new BouncingBall(width, height);
   sfield = new StarField(width, height, 100, this);

}

void draw(){
   background(0);

   randomShape();
   ball.update();
   
   sfield.draw();
   sfield.step();
}



void randomShape(){
   PVector loc = randomLoc();
   drawRandomShape(loc); 
  
}  

PVector randomLoc(){
//   return new PVector(random(width), random(height));
   return new PVector(ball.x, ball.y);
}

void drawRandomShape(PVector location){
  
   if ( random(1) > 0.1  ){
      drawRandomRect(location);
   }
   else{
      drawRandomEllipse(location);
   }
   
}


void drawRandomRect(PVector loc){
   float wwidth = getRandomWidth("rect");
   float hheight = getRandomHeight("rect");
   rect(loc.x, loc.y, wwidth, hheight);
}

void drawRandomEllipse(PVector loc){
   float wwidth = getRandomWidth("ellipse");
   float hheight = getRandomHeight("ellipse");
   
   ellipse(loc.x, loc.y, wwidth, hheight);
}


float getRandomWidth(String shapeName){
   return random(10,20);
}

float getRandomHeight(String shapeName){
   return random(10,20);
}
   



