void setup()
{
     size(800,600);
   ball = new BouncingBall(width, height);

}

void draw(){
  background(0);
   randomShape();
   ball.update();
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
   



