package processing.test.quadcopter;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Quadcopter extends PApplet {

float circlePadX;
float circlePadY;
float padRadius;

float vertPadX;
float vertPadY;
float padWidth;
float padHeight;

CircleNub nub;
VertNub vNub;
QuadCopter quad;

public void setup(){
  
  
  circlePadX = width/5;
  circlePadY = height/2;
  padRadius = 200;
  
  vertPadX = width*4/5;
  vertPadY = height/2;
  padWidth = 50;
  padHeight = 450;
  
  nub = new CircleNub();
  vNub = new VertNub();
  quad = new QuadCopter();
}

class QuadCopter{
  float Px; float Py; float Pz;
  float Vx; float Vy; float Vz;
  
  QuadCopter(){
    Px = width/2;
    Py = height/2;
    Pz = 0;
    
    Vx = 0; Vy = 0; Vz = 0;
  }
  
  public float getX(){return Px-width/2;}
  public float getY(){return Py-height/2;}
  public float getZ(){return Pz;}
  
  public void Update(){
    Px += Vx; Py += Vy; Pz += Vz;
    Vx = nub.SpeedX();
    Vy = nub.SpeedY();
    Vz = vNub.SpeedZ();

    BTManager.handleVelocities(Vx, Vy, Vz);
  }
}

class CircleNub{
  float X; float Y;
  float nubRadius;
  
  CircleNub(){
    X = circlePadX; Y = circlePadY;
    nubRadius = 20;
  }
  
  float speedScale = 4;
  public float SpeedX(){
    return (X-circlePadX)/padRadius*speedScale;
  }
  public float SpeedY(){
    return (Y-circlePadY)/padRadius*speedScale;
  }
  
  public void Update(){
    if(mousePressed && distance(mouseX,mouseY,circlePadX,circlePadY) < padRadius-nubRadius){
      X = mouseX; Y = mouseY;
    } else{
      float falloff = .95f;
      X = (X-circlePadX)*falloff+circlePadX;
      Y = (Y-circlePadY)*falloff+circlePadY;
    }
  }
}

class VertNub{
  float Z;
  float maxDist;
  float nubRad;
  
  VertNub(){
    Z = vertPadY;
    maxDist = padHeight/2;
    nubRad = 20;
  }
  
  float speedScale = 2;
  public float SpeedZ(){
    return -(Z-vertPadY)/maxDist*speedScale;
  }
  
  public void Update(){
    if(mousePressed && (distance(0,mouseY,0,vertPadY) < padRadius-nubRad) && (abs(mouseX-vertPadX) < nubRad*1.5f)){
      Z = mouseY;
    } else{
      float falloff = .95f;
      Z = (Z-vertPadY)*falloff+vertPadY;
    }
  }
}

public void draw(){
  background(color(150));
  stroke(color(0));
  fill(color(250));
  strokeWeight(1);
  
  //Draws the circle pad
  ellipseMode(RADIUS);
  ellipse(circlePadX,circlePadY,padRadius,padRadius);
  
  //Draws the vert pad
  rect(vertPadX-padWidth/2,vertPadY-padHeight/2,padWidth,padHeight);
  
  //Draws the circle pad nub
  ellipse(nub.X,nub.Y,nub.nubRadius,nub.nubRadius);
  nub.Update();
  
  //Draws the vert pad nub
  ellipse(vertPadX,vNub.Z,vNub.nubRad,vNub.nubRad);
  vNub.Update();
  
  //Draws the quadcopter
  fill(color(256,0,0));
  float quadSize = 20;
  rect(quad.Px-quadSize/2,quad.Py-quadSize/2,quadSize,quadSize);
  quad.Update();
  
  //Draws Grid in the center
  
  stroke(color(200));
  strokeWeight(4);
  float gridHeight = 400;
  float gridWidth = 600;
  //horizontal grid line
  line(width/2-gridWidth/2,height/2,width/2+gridWidth/2,height/2);
  for(int i = 0; i <= 10; i++){
    float notchSize = 5;
    float Px = width/2-gridWidth/2+gridWidth/10*i;
    line(Px, height/2-notchSize, Px, height/2+notchSize);
    float tSize = 20;
    textSize(tSize);
    fill(200);
    text(i,Px-tSize/3,height/2+tSize+notchSize);
  }
  //vertical grid line
  //line(width/2,height/2-gridHeight/2,width/2,height/2+gridHeight/2);
  
  textSize(40);
  fill(0);
  text(
    "X: "+quad.getX()+
    "\nY: "+quad.getY()+
    "\nZ: "+quad.getZ()
  ,0,40);
  
}

public float distance(float Ax,float Ay,float Bx,float By){
  return sqrt(pow(Ax-Bx,2)+pow(Ay-By,2));
}
  public void settings() {  size(1920,1080); }
}
