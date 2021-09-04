import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.zip.*; 
import java.util.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ProjectDF extends PApplet {

String Version = "V 5.3";

keyboard EYS;

public void settings(){
  size(700,700,P2D);
  PJOGL.setIcon("Misc/ICON.png");
}

PImage Secret;

public void setup(){
  if(args!=null){
    File tmp = new File(args[0]);
    if(tmp.isFile()){
      String[] tex =split(tmp.getName(),'.');
      if(tex[tex.length-1].equals("png")){
        Secret=loadImage(tmp.getAbsolutePath());
      }
    }
  }
  JSONObject tmp = loadJSONObject("Misc/config.json");
  tantrest();
  setupKeys();
  resetCon();
  WeaponINIC();
  EnemyAINIC();
  ProAINIC();
  MenuSetup();
  Configs = new IntDict();
  
  Configs.set("DrawEffects", 1);
  Configs.set("GuiScale", 100);
  Configs.set("ReversedWhell", 1);
  Configs.set("HurtScale", 1);
  Configs.set("ShouldPause", 1);
  Configs.set("SimpleExplosion", 1);
  Configs.set("Fullscreen", 0);
  Configs.set("Zoom", 100);
  
  if(tmp!=null){
    try{
    if(tmp.getBoolean("DebugStart",false)){
      DebugDraw=true;
      HWeapon=append(HWeapon,PApplet.parseByte(5));
      curSDelay=append(curSDelay,PApplet.parseByte(0));
      curEDelay=append(curEDelay,PApplet.parseByte(0));
      Start(tmp.getString("DebugMap","arena_vent"));
      if(tmp.getBoolean("DebugTant",false)){
        tantactive=true;
        tantrest();
        nextWave();
      }
    }
    Configs.set("DrawEffects",tmp.getInt("DrawEffects",1));
    Configs.set("GuiScale",tmp.getInt("GuiScale",100));
    JSONArray binds = tmp.getJSONArray("Binds");
    for(int i=0;i<binds.size();i++){
      JSONObject tmper = binds.getJSONObject(i);
      if(KeyRedirect.hasKey(tmper.getString("equa"))){
        int RE = KeyRedirect.get(tmper.getString("equa"));
        Keybinds[RE].code=PApplet.parseByte(tmper.getInt("code"));
        Keybinds[RE].iscode=tmper.getBoolean("iscode");
        Keybinds[RE].pri=tmper.getString("key").charAt(0);
      }
    }
    }catch(Exception e){
    PrintCon("sorry for that");
    PrintCon(e.toString());
    ErrorTimer=120;
    }
  }else{
    PrintCon("no config file detected");
    ErrorTimer=120;
  }
  
  byte[] saves = loadBytes("Misc/sav");
  if(saves!=null){
    CurrentSave=BgetI(saves,0,1);//yea thats it
  }else{
    PrintCon("MAJOR ERROR WHERE IS SAVE FILE?!");
    PrintCon("(supertinyfilebutimportanttinyfile.sav)");
    ErrorTimer=120;
  }
  
  background(0);
  LW=width;
  LW=height;
  ErrorImg = SloadImage("/Misc/Error.png");
}

public void Start(String Map){
  Gaming=true;
  openMap(Map);
  play = new Player();
  background(0);
}

Player play;
boolean DebugDraw=PApplet.parseBoolean(0);
boolean Gaming = false;
boolean RunPhys = true;
boolean MenuPaused = false;
int ErrorTimer = 0;
PImage ErrorImg;
IntDict Configs;
int LW=0;
int LH=0;
String TextString="";
int TextCurr=0;
boolean TextShow=false;
int hurtmepleanty=0;
float ZOOMER=1;

//make ent_timers and ent_textbox
int toturialTimer=0;
boolean toturialMode=false;

public void draw(){
  if(LW!=width || LH!=height){
    LW=width;
    LH=height;
    Background=createGraphics(width,height,P3D);
  }
  //if(Configs.get("Fullscreen")==1){
  //  surface.setSize(displayWidth, displayHeight);
  //  surface.setLocation(0,0); 
  //}
  if(ErrorTimer==0 & frameRate<40){
    PrintCon("less than 40 fps");
    ErrorTimer=120;
  }
  if(Gaming){
    if(Configs.get("ShouldPause")==1 && !focused){
      MenuTurnOffAll();
      MenuTurnOn("PAUSE_MENU");
      MenuPaused=true;
      RunPhys=false;
    }
    MAINLOOP();
    if(MenuPaused){
      fill(0,100);
      rect(0,0,width-1,height-1);
      Menu();
    }
    if(toturialMode && !MenuPaused){
      if(toturialTimer==120*0){
        texttoscren("hello");
      }
      if(toturialTimer==240*1){
        texttoscren("this is a game");
      }
      if(toturialTimer==240*2){
        texttoscren("so you might want to move too");
      }
      if(toturialTimer==240*3){
        texttoscren("A|D move you sideways");
      }
      if(toturialTimer==240*4){
        texttoscren("while W makes you jump");
      }
      if(toturialTimer==240*5){
        texttoscren("all of these binds can\nbe changed in the options menu");
      }
      if(toturialTimer==240*6){
        texttoscren("now there more");
      }
      if(toturialTimer==240*7){
        texttoscren("pressing S while in the air\nit makes you fall faster by 30%");
      }
      if(toturialTimer==240*8){
        texttoscren("SHIFT is dash\nworks both horizontal and vertical");
      }
      if(toturialTimer==240*9){
        texttoscren("doing a up dash on the ground\ngive you a lot of vertical speed");
      }
      if(toturialTimer==240*10){
        texttoscren("Hold SHIFT then W");
      }
      if(toturialTimer==240*11){
        texttoscren("now the guns");
      }
      if(toturialTimer==240*12){
        texttoscren("they all suck");
      }
      if(toturialTimer==240*13){
        texttoscren("you select one by either 1|2|3|4 or mouseWheel");
      }
      if(toturialTimer==240*14){
        texttoscren("1-garant\nis a fast acurate gun for the far away shit");
      }
      if(toturialTimer==240*15){
        texttoscren("2-rocket\nis a rocket lancher thing better used for swarms");
      }
      if(toturialTimer==240*16){
        texttoscren("3-railgun\na short high damage piercer weapon");
      }
      if(toturialTimer==240*17){
        texttoscren("4-shotgun\ncombo weapond has recoil but a very bad acuracy");
      }
      if(toturialTimer==240*18){
        texttoscren("the cooldown bar is the blue semicircle");
      }
      if(toturialTimer==240*19){
        texttoscren("now enemys");
      }
      if(toturialTimer==240*20){
        texttoscren("hurting them heals you");
      }
      if(toturialTimer==240*21){
        texttoscren("the health bar is the green semicircle");
      }
      if(toturialTimer==240*22){
        texttoscren("they also hurt you");
      }
      if(toturialTimer==240*23){
        texttoscren("1/3*2 of the damage will turn into HardDamage");
      }
      if(toturialTimer==240*24){
        texttoscren("you cannot heal above the harddamage");
      }
      if(toturialTimer==240*25){
        texttoscren("harddamage is the black bar semicircle");
      }
      if(toturialTimer==240*26){
        texttoscren("theres more but im lazy");
      }
      if(toturialTimer==240*27){
        texttoscren("you can stay and fuck around");
      }
      if(toturialTimer==240*28){
        texttoscren("bye");
        toturialMode=false;
      }
      toturialTimer++;
    }
  }else{
    background(0);
    text(Version,0,10);
    Menu();
  }
  if(ErrorTimer>0){
    tint(255,255,255,cos((float)ErrorTimer/20)*64+192);
    image(ErrorImg,width-ErrorImg.width,0);
    noTint();
    ErrorTimer--;
  }
  if(ConsoleUP){
    DrawConsole();
  }
}

public void MAINLOOP(){
  //background(0);
  play.PO=atan2(mouseY-height/2+12,mouseX-width/2);
  if(EYS.getkey('p')){
    //play.Force(cos(atan2(mouseY-height/2,mouseX-width/2))*3,sin(atan2(mouseY-height/2,mouseX-width/2))*3);
  }
  if(RunPhys){
    if(play.HP>0){HWeaponMATH();}
    try{
      play.Phy();
    }catch(Exception e){
      PrintCon("sorry for that");
      play.MT();
      PrintCon(e.toString());
      ErrorTimer=120;
    }
    AIMath();
    ProjMath();
    MathEffects();
    trigger();
    Atrigger();
    propM();
    doorM();
    for(int i=0;i<TX.length;i++){
      if(TT[i]==6){
        float vy=0;
        float vx=0;
        for(int u=0;u<ET.length;u++){
          if(ET[u].equals("playerC") && ES[u]==i){
            vx=(EX[u]-TX[i])/40;
            vy=(EY[u]-TY[i])/40;
            break;
          }
        }
        AddPartic(6,TX[i]+random(0,TW[i]),TY[i]+random(0,TH[i]),vx,vy,60,0xffF2F2F2,true);
      }
      if(TT[i]==7 && random(0,100)<60){
        AddPartic(4,TX[i]+random(0,TW[i]),TY[i]+random(0,TH[i]-240),0,2,60,0xff54D5DB,true);
      }
    }
  }
  if(tantactive){
    tantmath();
  }
  //RENDER
  //why and how do colors work on this shit
  //fuck obj my new best friend is ply
  ZOOMER=PApplet.parseFloat(Configs.get("Zoom"))/100;
  if(BACEXIST){
  Background.beginDraw();
  Background.background(0);
  Background.ambientLight(100, 100, 100);
  Background.directionalLight(155, 155, 155, -0.5f, 0.5f, -1);
  //Background.ambientLight(255, 255, 255);
  Background.camera(-play.X/5,-123/ZOOMER ,-play.Y/5, -play.X/5,999, -play.Y/5, 0,0,-1);
  Background.perspective(PI/3.0f,PApplet.parseFloat(width)/PApplet.parseFloat(height),1,100000);
  Background.pushMatrix();
  //Background.scale(1,1,-1);
  Background.shape(BACK);
  Background.popMatrix();
  Background.endDraw();
  image(Background, 0, 0);
  }else{
  background(0);
  }
  pushMatrix();
  scale(ZOOMER);
  translate(width/2/ZOOMER-play.X,height/2/ZOOMER-play.Y);
  
  //noStroke();
  //image(Hor,HorX+play.X/2,HorY+play.Y/2);
  //fill(0);
  //rect(-10000,-10000,20000,10000+BacY);
  //rect(-10000,Bac.height()+BacY,20000,10000-Bac.height());
  //rect(-10000,BacY,10000+BacX,Bac.height());
  //rect(Bac.width()+BacX,BacY,10000-Bac.width(),Bac.height());
  //Bac.ANR(BacX,BacY);
  //noFill();
  shape(WORLD);
  
  propD();
  AIR();
  PRR();
  DrawEffects();
  strokeWeight(1);
  for(int i=0;i<CSX.length;i++){
    stroke(255);
    line(CSX[i],CSY[i],CEX[i],CEY[i]);
    if(CT[i]==1){
      for(float u=0;u<10;u++){
        float X=CSX[i]*(u/10+0.05f)+CEX[i]*(1-u/10-0.05f);
        float Y=CSY[i]*(u/10+0.05f)+CEY[i]*(1-u/10-0.05f);
        line(X,Y,X,Y+10);
      }
    }
  }
  if(DebugDraw){
    stroke(255,0,0);
    line(0,play.Y+600,0,play.Y-600);
    stroke(0,0,255);
    line(play.X+600,0,play.X-600,0);
    for(int i=0;i<TX.length;i++){
      stroke(255,255,0);
      fill(255,255,0,100);
      rect(TX[i],TY[i],TW[i],TH[i]);
      text(str(TE[i]),TX[i],TY[i]);
    }
    for(int i=0;i<EX.length;i++){
      stroke(255,0,255);
      fill(255,0,255,100);
      line(EX[i],EY[i]-10,EX[i],EY[i]+10);
      line(EX[i]-10,EY[i],EX[i]+10,EY[i]);
      fill(255,0,255,255);
      text(ET[i],EX[i],EY[i]);
    }
  }
  stroke(0);
  popMatrix();
  for(int i=0;i<BOSSHP.size();i++){
    fill(100,100);
    rect(width/2-200,height-80-i*20,400,20);
    fill(200,0,0);
    rect(width/2-200,height-80-i*20,400*((float)ListAi.get(BOSSID.get(i)).HP/(float)BOSSHP.get(i)),20);
    fill(255);
    textAlign(CENTER,CENTER);
    text(ListAi.get(BOSSID.get(i)).getClass().toString(),width/2,height-70-i*20);
    textAlign(CENTER,CENTER);
  }
  stroke(120);
  fill(100);
  if(mousePressed && mouseButton==RIGHT && HWeapon[WeaponSellected]==5){
    for(int i=0;i<AINames.length;i++){
      textAlign(LEFT, TOP);
      fill(100);
      rect(100,40+i*15,80,15);
      fill(255);
      text(AINames[i],100,40+i*15);
      if(mouseX>100 && mouseX<140 && mouseY>40+i*15 && mouseY<55+i*15){
        DevGun=i;
      }
    }
  }
  fill(255);
  if(tantactive){
    text("round " + round,120,50);
    text(CurrentArena,120,60);
  }
  if(tantactive && Blurer>0){
    fill(255,sq(PApplet.parseFloat(Blurer)/60)*255);
    rect(0,0,width,height);
  }
  if(TextShow){
    textAlign(CENTER,CENTER);
    textSize(35);
    rectMode(CENTER);
    noStroke();
    fill(0,min(100,map(TextCurr,TextString.length()*2*15,TextString.length()*2*15+60,100,0)));
    rect(width/2,height/3*2,textWidth(TextString.substring(0,min(TextCurr,TextString.length())))+8,50);
    fill(255,map(TextCurr,TextString.length()*2*15,TextString.length()*2*15+60,255,0));
    text(TextString.substring(0,min(TextCurr,TextString.length())),width/2,height/3*2);
    TextCurr++;
    textAlign(LEFT, TOP);
    textSize(12);
    rectMode(CORNER);
    if(TextCurr==TextString.length()*2*15+60){
      TextShow=false;
    }
  }
  fill(255);
  play.drawM();
  if(play.HP>0){HWeaponDRAW();}
  if(hurtmepleanty>0){
    hurtmepleanty--;
    noFill();
    stroke(0xffFF0000,255);
    strokeWeight(hurtmepleanty*Configs.get("HurtScale"));
    rect(0,0,width,height);
  }
  strokeWeight(1);
  play.drawI();
  fill(0,255,0);
  if(DebugDraw){
  textAlign(LEFT, TOP);
  text("X:"+play.X,0,40);
  text("Y:"+play.Y,0,50);
  text("VX:"+play.VX,0,60);
  text("VY:"+play.VY,0,70);
  text("land:"+play.Gr,0,80);
  text("FPS:"+frameRate,0,90);
  text("AI:"+ListAi.size(),0,100);
  text("PO:"+play.PO,0,110);
  text("Effects:"+ListEffects.size(),0,120);
  text("Hp:"+play.HP,0,130);
  text("Hd:"+play.HD,0,140);
  text("Must:"+Must,0,150);
  text("PMust:"+PMust,0,160);
  text("PSX:"+PSX,0,170);
  text("PSY:"+PSY,0,180);
  text("Projectyls:"+ListPR.size(),0,190);
  text("DevGun:"+AINames[DevGun],0,200);
  text("Ignore:"+play.Ignore,0,210);
  text("cool:"+play.cooldownV,0,220);
  }
}

//behold the glue that holds all of this

public float TLineToLine(float x1,float y1,float x2,float y2,float x3,float y3,float x4,float y4)
{
  return ((x1-x3)*(y3-y4)-(y1-y3)*(x3-x4))/((x1-x2)*(y3-y4)-(y1-y2)*(x3-x4));
}
ArrayList<AI> ListAi;
IntList kill;
int Must=0;
int PMust=0;
IntList BOSSHP;
IntList BOSSID;

String[] AINames={"Bug","Fly","Target","Spewer","testBoss","Maze","Laze","Maze_Boss","Laze_Boss","tower","napalm","Spirit","Guardian"};
boolean[] Sgroun={true ,false,true    ,true    ,true      ,false ,false ,false      ,false      ,true   ,true    ,false   ,true};

public void AIMath(){
  PMust=Must;
  for(int i=0;i<ListAi.size();i++){
    try{
      ListAi.get(i).math(i);
      AI tmp = ListAi.get(i);
      if(tmp.X>10000 | tmp.X<-10000 | tmp.Y>10000 | tmp.Y<-10000){kill.append(i);}
    }catch(Exception e){
      PrintCon("sorry for that");
      kill.append(i);
      AddPartic(1,ListAi.get(i).X,ListAi.get(i).Y,ListAi.get(i).X,-10000,60,0xffFFFFFF,false);
      AddPartic(5,ListAi.get(i).X,ListAi.get(i).Y,128,0,60,0xffFFFFFF,true);
      for(int ohno=0;ohno<50;ohno++){
        AddPartic(2,ListAi.get(i).X,ListAi.get(i).Y,random(-10,10),random(-10,10),60,0xffFFFFFF,false);
      }
      PrintCon(e.toString());
      ErrorTimer=120;
    }
  }
  for(int i=0;i<ListAi.size();i++){
    try{
      if(ListAi.get(i).getClass()==Class.forName("ProjectDFTEST$Spirit")){
        Spirit tmp = (Spirit)ListAi.get(i);
        if(kill.hasValue(tmp.Connected)){
          tmp.Con=false;
          tmp.hurte=true;
          ListAi.set(i,tmp);
        }
      }
    }catch(Exception e){}
  }
  kill.reverse();
  for(int i=0;i<kill.size();i++){
    AI tmp = ListAi.get(kill.get(i));
    BOSSID.reverse();
    BOSSHP.reverse();
    for(int u=0;u<BOSSID.size();u++){
      if(kill.get(i)==BOSSID.get(u)){
        BOSSID.remove(u);
        BOSSHP.remove(u);
        break;
      }
      if(kill.get(i)<BOSSID.get(u)){
        BOSSID.set(u,BOSSID.get(u)-1);
      }
    }
    BOSSID.reverse();
    BOSSHP.reverse();
    if(dist(tmp.X,tmp.Y,play.X,play.Y)<=200 && play.HP>0){
      AddPartic(1,play.X+random(-5,5),play.Y-12+random(-5,5),tmp.X+random(-5,5),tmp.Y-tmp.H/2+random(-5,5),60,color(255,0,0),true);
      if(play.HP+5>100){
        play.HP=100;
      }else{
        play.HP+=5;
      }
    }
    if(tmp.M){Must--;}
  }
  for(int i=0;i<kill.size();i++){
    ListAi.remove(kill.get(i));
  }
  kill.clear();
}

public void AIR(){
  for(int i=0;i<ListAi.size();i++){
    ListAi.get(i).render();
    if(DebugDraw){
      text(ListAi.get(i).HP,ListAi.get(i).X+10,ListAi.get(i).Y-10);
    }
  }
}

class Bug extends AI{
  Bug(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=10;
    H=14;
    HP=20;
    T=0;
  }
  public void math(int SID){
      if(HP<=0){kill.append(SID);return;}
      Walk(0.3f,0.5f,6);
      Cont(W,H,35);
      Phys(W,H,true);
      X+=VX;
      Y+=VY;
  }
  public void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
      pushMatrix();
      if(VX>0){
        scale(-1,1);
        translate(-X*2,0);
      }
       Anim(EAR.get("Bug"),true,OG>3);
      enANIM[EAR.get("Bug")].DIMG(X,Y,W,H,frame,true,OG>3,0xffFFFFFF);
      popMatrix();
  }
}

class Fly extends AI{
  Fly(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=6;
    H=12;
    HP=12;
    T=4;
  }
  public void math(int SID){
    if(HP<=0){kill.append(SID);return;}
    VX+=cos(atan2(play.Y-Y,play.X-X))*0.4f;
    VY+=sin(atan2(play.Y-Y,play.X-X))*0.2f;
    VX=constrain(VX,-10,10);
    VY=constrain(VY,-10,10);
    Cont(W,H,15);
    Phys(W,H,false);
    X+=VX;
    Y+=VY;
  }
  public void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
     Anim(EAR.get("Fly"),false,true);
    enANIM[EAR.get("Fly")].DIMG(X,Y,W,H,frame,false,true,0xffFFFFFF);
  }
}

class Target extends AI{
  Target(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=6;
    H=24;
    HP=900;
  }
  public void math(int SID){
    if(HP<=0){kill.append(SID);return;}
    Fall();
    Cont(W,H,1);
    Phys(W,H,true);
    X+=VX;
    Y+=VY;
    NewPartic(new SubText(X,Y,random(-1,1),random(-3,-1),60,0xffFFFFFF,"test"),true);
  }
  public void render(){
    stroke(0);
    fill(255);
    rect(X-W,Y-H,W*2,H);
  }
}

class Spewer extends AI{
  int cooldown=80;
  Spewer(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=6;
    H=24;
    HP=34;
    T=5;
  }
  public void math(int SID){
    if(dist(X,Y,play.X,play.Y)>150){
      Walk(0.01f,0.01f,1);
    }
    if(HP<=0){kill.append(SID);return;}
    Fall();
    Cont(W,H,1);
    Phys(W,H,true);
    if(cooldown==0){
      
      NewPR(X+W,Y-H/2,cos(atan2(play.Y-Y+H/2-12,play.X+6-X-W))*6,sin(atan2(play.Y-Y+H/2-8,play.X+6-X-W))*6,0);
      cooldown=80;
    }else{
      cooldown--;
    }
    X+=VX;
    Y+=VY;
  }
  public void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
      pushMatrix();
      if(VX<0){
        scale(-1,1);
        translate(-X*2,0);
      }
      Anim(EAR.get("Spewer"),Gr && abs(VX)<0.5f,false);
      enANIM[EAR.get("Spewer")].DIMG(X,Y,W,H,frame,Gr && abs(VX)<0.5f,false,0xffFFFFFF);
      popMatrix();
  }
}

class testBoss extends AI{
  testBoss(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=16;
    H=34;
    HP=600;
    BOSSHP.append(HP);
    BOSSID.append(ListAi.size());
  }
  public void math(int SID){
    if(HP<=0){kill.append(SID);return;}
    Fall();
    Cont(W,H,1);
    Phys(W,H,true);
    X+=VX;
    Y+=VY;
  }
  public void render(){
    stroke(0);
    fill(255);
    rect(X-W,Y-H,W*2,H);
  }
}

class Maze extends AI{
  boolean Enraged=false;
  Maze(float nX,float nY,boolean nM,boolean Boss){
    X=nX;
    Y=nY;
    M=nM;
    W=16;
    H=32;
    T=1;
    if(Boss){
      HP=800;
      W=32;
      H=64;
      BOSSHP.append(HP);
      BOSSID.append(ListAi.size());
    }else{
      HP=200;
    }
  }
  int cooldown = 350;
  int attack = 1;
  public void math(int SID){
    if(HP<=0){
      if(Gr){
        AddPartic(5,X,Y,32,0,15,0xff00FF00,false);
        kill.append(SID);
        for(int all=0;all<ListAi.size();all++){
          try{
            if(ListAi.get(all).getClass()==Class.forName("ProjectDFTEST$Maze")){
              Maze tmp = (Maze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all,tmp);
            }
            if(ListAi.get(all).getClass()==Class.forName("ProjectDFTEST$Laze")){
              Laze tmp = (Laze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all,tmp);
            }
          }catch(Exception e){
            
          }
        }
        return;
      }
      VY+=0.2f;
    }else{
      Gr=false;
      if(cooldown>200){
        VX+=(play.X-X+cos((float)frameCount/20)*64)/10;
        VY+=(play.Y-Y+sin((float)frameCount/20)*64-200)/10;
        VX=VX/10*9;
        VY=VY/10*9;
      }else{
        VX=0;
        VY=0;
      }
      if(cooldown<200 && cooldown>80){
        float N=((float)cooldown-80)/120*8;
        if(attack==0){
        AddPartic(3,X,Y-H/2,cos(0*PI/2)*N,sin(0*PI/2)*N,15,0xff00FFFF,true);
        AddPartic(3,X,Y-H/2,cos(1*PI/2)*N,sin(1*PI/2)*N,15,0xff00FFFF,true);
        AddPartic(3,X,Y-H/2,cos(2*PI/2)*N,sin(2*PI/2)*N,15,0xff00FFFF,true);
        AddPartic(3,X,Y-H/2,cos(3*PI/2)*N,sin(3*PI/2)*N,15,0xff00FFFF,true);
        }else{
        AddPartic(3,X,Y-H/2,cos(0*PI/2+PI/4)*N,sin(0*PI/2+PI/4)*N,15,0xff00FFFF,true);
        AddPartic(3,X,Y-H/2,cos(1*PI/2+PI/4)*N,sin(1*PI/2+PI/4)*N,15,0xff00FFFF,true);
        AddPartic(3,X,Y-H/2,cos(2*PI/2+PI/4)*N,sin(2*PI/2+PI/4)*N,15,0xff00FFFF,true);
        AddPartic(3,X,Y-H/2,cos(3*PI/2+PI/4)*N,sin(3*PI/2+PI/4)*N,15,0xff00FFFF,true);
        }
      }
      if(!Enraged){
        if(cooldown<80){
          if(attack==0){
            if(cooldown%10==0){
              for(int i=0;i<4;i++){
                NewPR(X,Y-H/2,cos(i*PI/2+(float)cooldown/60*PI)*5,sin(i*PI/2+(float)cooldown/60*PI)*5,3);
              }
            }
          }else{
            if(cooldown%10==0){
              float target = atan2(play.Y-Y,play.X-X);
              for(int i=0;i<3;i++){
                NewPR(X,Y-H/2,cos(target+PI/8*(i-1))*5,sin(target+PI/8*(i-1))*5,3);
              }
            }
          }
        }
      }else{
        if(cooldown<80){
            if(cooldown%10==0){
              for(int i=0;i<8;i++){
                NewPR(X,Y-H/2,cos(i*PI/4+(float)cooldown/60)*5,sin(i*PI/4+(float)cooldown/60)*5,3);
              }
            }
        }
      }
      cooldown--;
      if(cooldown<0){cooldown=300;attack = (int)random(0,2);}
    }
    Cont(W,H,1);
    Phys(W,H,false);
    X+=VX;
    Y+=VY;
  }
  public void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
    Anim(EAR.get("Maze"),false,true);
    if(!Enraged){
      enANIM[EAR.get("Maze")].DIMG(X,Y,W,H,frame,false,true,0xffFFFFFF);
    }else{
      enANIM[EAR.get("Maze")].DIMG(X,Y,W,H,frame,false,true,0xffFFAAAA);
      stroke(255,0,0);
      noFill();
      circle(X+random(-2,2),Y-H/2+random(-2,2),36);
    }
  }
}

class Laze extends AI{
  boolean Enraged=false;
  Laze(float nX,float nY,boolean nM,boolean Boss){
    X=nX;
    Y=nY;
    M=nM;
    W=16;
    H=32;
    T=2;
    if(Boss){
      HP=800;
      W=32;
      H=64;
      BOSSHP.append(HP);
      BOSSID.append(ListAi.size());
    }else{
      HP=200;
    }
  }
  int cooldown = 300;
  int attack = 1;
  float LastPlayer = 0;
  float Tx;
  float Ty;
  public void math(int SID){
    if(HP<=0){
      if(Gr){
        AddPartic(5,X,Y,32,0,15,0xff00FF00,false);
        kill.append(SID);
        for(int all=0;all<ListAi.size();all++){
          try{
            if(ListAi.get(all).getClass()==Class.forName("ProjectDFTEST$Maze")){
              Maze tmp = (Maze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all,tmp);
            }
            if(ListAi.get(all).getClass()==Class.forName("ProjectDFTEST$Laze")){
              Laze tmp = (Laze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all,tmp);
            }
          }catch(Exception e){
            
          }
        }
        return;
      }
      VY+=0.2f;
    }else{
      Gr=false;
      if(cooldown>200){
        VX+=(play.X-X+cos(-(float)frameCount/20)*64)/10;
        VY+=(play.Y-Y+sin(-(float)frameCount/20)*64-200)/10;
        VX=VX/10*9;
        VY=VY/10*9;
      }else{
        VX=0;
        VY=0;
      }
      if(cooldown==200){
        if(attack==0){
          Tx=X;Ty=Y;
        }
        else{
          LastPlayer = atan2(play.Y-Y,play.X-X);
        }
      }
      if(cooldown<200 && cooldown>80){
        if(attack==0 || Enraged){
          Tx=(play.X+Tx)/2;
          Ty=(play.Y-12+Ty)/2;
        }else{
          float[] tmp=Enyscan(LastPlayer+PI/4,true,0,-H/2);
          AddPartic(1,X,Y-H/2,tmp[0],tmp[1],2,color(100,0,0),true);
                  tmp=Enyscan(LastPlayer-PI/4,true,0,-H/2);
          AddPartic(1,X,Y-H/2,tmp[0],tmp[1],2,color(100,0,0),true);
        }
      }
      if(cooldown<80){
        if(!Enraged){
          if(attack==0){
            if((cooldown+10)%20==0){
              Tx=play.X;
              Ty=play.Y;
            }
            if(cooldown%20==0){
              float[] tmp=Enyhitscan(atan2(Ty-Y,Tx-X),25,true,0,-H/2);
              AddPartic(1,X,Y-H/2,tmp[0],tmp[1],40,color(255,0,0),true);
            }
          }else{
            float[] tmp=Enyhitscan(LastPlayer+PI/4*((float)cooldown/80),15,true,0,-H/2);
            AddPartic(1,X,Y-H/2,tmp[0],tmp[1],40,color(255,0,0),true);
                    tmp=Enyhitscan(LastPlayer-PI/4*((float)cooldown/80),15,true,0,-H/2);
            AddPartic(1,X,Y-H/2,tmp[0],tmp[1],40,color(255,0,0),true);
          }
        }else{
          if(cooldown%8==0){
            Tx=(play.X+Tx*2)/3;
            Ty=(play.Y-12+Ty*2)/3;
            float[] tmp=Enyhitscan(atan2(Ty-Y,Tx-X),25,true,0,-H/2);
            AddPartic(1,X,Y-H/2,tmp[0],tmp[1],40,color(255,0,0),true);
          }
        }
      }
      cooldown--;
      if(cooldown<0){cooldown=300;attack = (int)random(0,2);}
    }
    Cont(W,H,1);
    Phys(W,H,false);
    X+=VX;
    Y+=VY;
  }
  public void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
    Anim(EAR.get("Laze"),false,true);
    if(!Enraged){
      enANIM[EAR.get("Laze")].DIMG(X,Y,W,H,frame,false,true,0xffFFFFFF);
    }else{
      enANIM[EAR.get("Laze")].DIMG(X,Y,W,H,frame,false,true,0xffFFAAAA);
      stroke(255,0,0);
      noFill();
      circle(X+random(-2,2),Y-H/2+random(-2,2),36);
    }
    if(cooldown<200 & (attack == 0 || Enraged)){
      stroke(0xffB703FF);
      noFill();
      circle(Tx,Ty,16);
      line(Tx-20,Ty,Tx+20,Ty);
      line(Tx,Ty-20,Tx,Ty+20);
    }
  }
}

class Tower extends AI{
  int cooldown=200;
  Tower(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=12;
    H=48;
    HP=34;
    T=3;
  }
  float Tx,Ty;
  public void math(int SID){
    if(HP<=0){kill.append(SID);return;}
    Fall();
      //Walk(0.3,0.5,6);
      //Phys(W,H,true);
      //Cont(W,H,35);
    if(dist(X,Y,play.X,play.Y)<150){
      Walk(-1,-1,1);
    }
    Cont(W,H,45);
    Phys(W,H,true);
    if(cooldown==0){
      float[] tmp=Enyhitscan(atan2(Ty-Y+12,Tx-X),25,true,0,-H/2);
      AddPartic(1,X,Y-H+6,tmp[0],tmp[1],40,color(255,0,0),true);
      cooldown=200;
    }
    if(cooldown>40 && cooldown<80){
      float[] tmp=Enyscan(atan2(Ty-Y+12,Tx-X)+PI/8*(((float)cooldown-40)/40),true,0,-H/2);
      AddPartic(1,X,Y-H+6,tmp[0],tmp[1],2,color(200,0,0),false);
              tmp=Enyscan(atan2(Ty-Y+12,Tx-X)-PI/8*(((float)cooldown-40)/40),true,0,-H/2);
      AddPartic(1,X,Y-H+6,tmp[0],tmp[1],2,color(200,0,0),false);
      Tx=play.X;
      Ty=play.Y;
    }
    if(cooldown<80){
      float[] tmp=Enyscan(atan2(Ty-Y+12,Tx-X),true,0,-H/2);
      AddPartic(1,X,Y-H+6,tmp[0],tmp[1],2,color(100,0,0),true);
    }
    cooldown--;
    X+=VX;
    Y+=VY;
  }
  public void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
    Anim(EAR.get("Tower"),abs(VX)>2,false);
    enANIM[EAR.get("Tower")].DIMG(X,Y,W,H,frame,abs(VX)>2,false,0xffFFFFFF);
  }
}

class Napalm extends AI{
  Napalm(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=1;
  }
  public void math(int SID){
    if(HP<=0){
      for(int i=0;i<30;i++){
        float R=random(-PI,0);
        float P=random(2,10);
        AddPartic(2,X,Y,cos(R)*P,sin(R)*P,40,color(0xffFF0000),false);
      }
      for(int i=0;i<9;i++){
        float R=-PI*(PApplet.parseFloat(i)/8);
        NewPR(X,Y-12,cos(R)*6,sin(R)*6,4);
      }
      AddPartic(5,X,Y,100,0,40,color(0xffFF0000),true);
      kill.append(SID);
      return;
    }
    Fall();
    Phys(W,H,true);
    X+=VX;
    Y+=VY;
  }
  public void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
    Anim(EAR.get("Tower"),abs(VX)>2,false);
    enANIM[EAR.get("Tower")].DIMG(X,Y,W,H,frame,abs(VX)>2,false,0xffFFFFFF);
  }
}

class Spirit extends AI{
  Spirit(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=24;
    H=48;
    HP=90;
  }
  int Cooldown=0;
  int Connected=0;
  boolean Con=false;
  public void math(int SID){
    if(HP<=0){
      kill.append(SID);
      return;
    }
    if(Cooldown==0 & !Con){
      int NUM=floor(random(0,ListAi.size()));
      try{
        if(!(ListAi.get(NUM).getClass()==Class.forName("ProjectDFTEST$Spirit")) && SID!=NUM){
          Connected=NUM;
          Con=true;
          hurte=false;
          Cooldown=240;
        }
      }catch(Exception e){}
    }
    if(Cooldown>0 & !Con){
      Cooldown--;
    }
    float R=atan2(Y-play.Y,X-play.X);
    NewPR(X,Y-H/2,-cos(R),-sin(R),5);
    if(dist(X,Y,play.X,play.Y)<150){
      VX+=cos(R);
      VY+=sin(R);
    }
    if(dist(X,Y,play.X,play.Y)>350){
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX,-2,2);
    VY=constrain(VY,-2,2);
    VY-=0.01f;
    Phys(W,H,true);
    X+=VX;
    Y+=VY;
  }
  public void render(){
    stroke(0xff00FFFF);
    fill(0xff00CCCC);
    rect(X-W,Y-H,W*2,H);
    if(Con){
      try{
      AI tmp = ListAi.get(Connected);
      AddPartic(1,X,Y-H/2,tmp.X,tmp.Y,1,color(0xff00FFFF,100),true);
      stroke(color(0xff00FFFF));
      noFill();
      quad(X-64,Y-H/2,X,Y-64-H/2,X+64,Y-H/2,X,Y+64-H/2);
      }catch(Exception e){}
    }
  }
}

class Guardian extends AI{
  int cooldown=360;
  int attack=0;
  int intro=240;
  Guardian(float nX,float nY,boolean nM){
    Bresistance=0;
    X=nX;
    Y=nY;
    M=nM;
    W=50;
    H=100;//1600
    HP=5000;
    T=3;
    hurte=false;
  }
  public void math(int SID){
    if(intro>0 && HP>0){
      intro--;
      if(intro<120){
        float R=random(-PI,PI);
        float D=random(64,128);
        NewPartic(new Wind(X-cos(R)*D,Y-H/2-sin(R)*D,cos(R)*D/10,sin(R)*D/10,10,0xffFFFFFF),true);
      }
    }
    if(intro==1 && HP>0){
      BOSSHP.append(HP);
      BOSSID.append(SID);
      hurte=true;
    }
    if(HP<=0){
      float R=random(-PI,PI);
      float D=random(intro/3,intro);
      NewPartic(new Wind(X,Y-H/2,cos(R)*D/3,sin(R)*D/3,10,0xffFFFFFF),true);
      intro++;
      cooldown=900;
      if(intro>120){
        NewPartic(new Explode(X,Y-H/2,128,0,60,0xffD80B0B),true);
        NewPartic(new Explode(X,Y-H/2,128+64,0,60,0xffD8560B),true);
        NewPartic(new Explode(X,Y-H/2,128+128,0,60,0xffD8C10B),true);
        kill.append(SID);return;
      }
    }
    cooldown--;
    if(cooldown<30 && attack==0){
      float R = random(-PI/4,PI/4)-PI/2;
      NewPR(X,Y-H/2,cos(R)*8,sin(R)*8,7);
    }
    if(cooldown%120==0 && attack==1){
      NewPR(X,Y,8,0,6);
      NewPR(X,Y,-8,0,6);
    }
    if(cooldown==0 && attack==2){
      NewPR(X,Y-H/2,0,0,8);
    }
    if(cooldown==0 && attack==3){
      for(int i=0;i<20;i++){
        NewPR(X,Y-H/2,i*PI/10,0,9);
      }
    }
    if(cooldown==0){
      attack=floor(random(0,4));
      cooldown=120;
    }
    Fall();
    Phys(W,H,true);
    X+=VX;
    Y+=VY;
  }
  public void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
    Anim(EAR.get("Guardian"),false,false);
    enANIM[EAR.get("Guardian")].DIMG(X,Y,W,H,frame,false,false,0xffFFFFFF);
    if(attack == 0){
      fill(255);
    }
    if(attack == 1){
      fill(0xffFFA600);
    }
    if(attack == 2){
      fill(0xffFF0000);
    }
    if(attack == 3){
      fill(0xff00C5FF);
    }
    circle(X,Y-H/2,max(0,map(intro,0,120,64,0)));
  }
}

class AI{
  float Bresistance=1;
  float X;
  float Y;
  float VX;
  float VY;
  float W;
  float H;
  int T;
  int HP;
  boolean Gr;
  boolean M;
  int frame=0;
  int timer=0;
  int OG=0;
  boolean Ignore;
  boolean hurte=true;
  //AI(float nX,float nY,boolean nM){
  //}
  public void math(int SID){
  }
  public void render(){
  }
  public void Anim(int EI,boolean move,boolean air){
    timer++;
    if(timer>enANIM[EI].delay){
      timer=0;
      frame++;
    }
    if(frame==enANIM[EI].getM(move,air)){
      frame=0;
    }
    //println(frame);
  }
  public void Cont(float W,float H,int dmg){
    if(X-W<=play.X+6 && X+W>=play.X-6 && Y>=play.Y-24 && Y-H<=play.Y && play.IV==0 && play.HP>0){
      AThurt(dmg);
      if(X<=play.X){
        VX-=5;
      }else{
        VX+=5;
      }
    }
  }
  public void Fall(){
    if(Gr==false){
      if(VY<20){VY+=0.5f;}
      OG++;
    }else{
      VX/=2;
      OG=0;
    }
    Gr=false;
  }
  public void Walk(float GS,float AS,float JF){
    if(Gr==false){
      if(VY<20){VY+=0.5f;}
      if(play.X<X && VX>-7){VX-=AS;}
      if(play.X>X && VX<7 ){VX+=AS;}
      OG++;
    }else{
      if(play.Y+10<Y && random(0,100)<5){VY-=JF;}
      if(play.X<X && VX>-7){VX-=GS;}
      if(play.X>X && VX< 7){VX+=GS;}
      if(play.Y>Y){Ignore=true;}
      OG=0;
    }
    Gr=false;
  }
  public void Phys(float W,float H,boolean C){
    SPHYS(X-W,Y  ,X-W+VX+0.01f,Y+VY+0.01f,C);
    SPHYS(X+W,Y  ,X+W+VX+0.01f,Y+VY+0.01f,C);
    SPHYS(X-W,Y-H,X-W+VX+0.01f,Y-H+VY+0.01f,C);
    SPHYS(X+W,Y-H,X+W+VX+0.01f,Y-H+VY+0.01f,C);
    if(true){
      if(Ignore & C){Checkfor();}
      if(sphys(X-W+VX,Y+VY,X+W+VX,Y+VY) | sphys(X-W+VX,Y-H+VY,X+W+VX,Y-H+VY)){VY=0;}
      if(sphys(X-W+VX,Y+VY,X-W+VX,Y-H+VY) | sphys(X+W+VX,Y+VY,X+W+VX,Y-H+VY)){VX=0;}
    }
  }
  public void SPHYS(float T1,float T2,float T3,float T4,boolean C){
    float[] T;
    T=coll(T1,T2,T3,T4,Ignore || !C);
    if(T[0]>=0 && T[0]<=1){
      int i = (int)T[2];
      PVector TOplayer=new PVector(play.X-(CSX[i]+CEX[i])/2,play.Y-12-(CSY[i]+CEY[i])/2);
      PVector Normal=new PVector((CSX[i]-CEX[i]),(CSY[i]-CEY[i]));
      if(CSX[i]>CEX[i]){Normal.rotate(-PI/2);}else{Normal.rotate(PI/2);}
      if(Normal.dot(TOplayer)<0 && CT[i]==1){
        Ignore=true;
      }
      //float R=atan2(CSY[(int)T[2]]-CEY[(int)T[2]],CSX[(int)T[2]]-CEX[(int)T[2]]);
      float R=atan2(CSY[i]-CEY[i],CSX[i]-CEX[i]);
      float NV = VX * cos(R) + VY * sin(R);
      VX = cos(R) * NV * 0.99f;
      VY = sin(R) * NV * 0.99f;
      //float ISB=atan2(Y-H/2-(CSY[(int)T[2]]+CEY[(int)T[2]])/2,X-(CSX[(int)T[2]]+CEX[(int)T[2]])/2);
      R=atan2(CSY[i]-CEY[i],CSX[i]-CEX[i]);
      if(R<0){R+=PI;}
      if(Normal.dot(TOplayer)>0 && R>-PI/4){//its a feature fuck it
        Gr=true;
      }
      //Gr=true;
    }
  }
  public boolean sphys(float T1,float T2,float T3,float T4){
    float[] T;
    T=coll(T1,T2,T3,T4,Ignore);
    if(T[0]>0 && T[0]<1){
      return true;
    }
    return false;
  }
  public void HURT(int dmg)
  {
    if(!hurte){return;}
    HP-=dmg;
    for(int B=0;B<5;B++){
      AddPartic(4,X,Y,random(-1,1),random(-8,-2),50,color(255,0,0),true);
    }
    if(play.regenera==0){
      if(dist(X,Y,play.X,play.Y)<=200 && play.HP>0){
        AddPartic(1,play.X+random(-5,5),play.Y-12+random(-5,5),X+random(-5,5),Y-H/2+random(-5,5),60,color(255,0,0),true);
        if(play.HP+dmg/4>100){
          play.HP=100;
        }else{
          play.HP+=dmg/4;
        }
      }
    }else{
      if(random(1,100)<50 && play.HP>0){
        NewPR(X,Y-H/2,random(-5,5),random(-5,5),10);
      }
    }
  }

  public float[] Enyhitscan(float R, int dmg,boolean lazer,float Offx,float Offy) {
    float MT=9999;
    int T=-1;
    float OX=X+Offx;
    float OY=Y+Offy;
    float NX=X+Offx+cos(R);
    float NY=Y+Offy+sin(R);
    int[] list=CB(OX, OY, NX+cos(R)*1000, NY+sin(R)*1000);
    for (int p=0; p<list.length; p++) {
      int i=list[p];
      float r = TLineToLine(CSX[i], CSY[i], CEX[i], CEY[i], OX, OY, NX, NY);
      if (r>=0 && r<=1) {
        float t = TLineToLine(OX, OY, NX, NY, CSX[i], CSY[i], CEX[i], CEY[i]);
        if (t>0 && t<MT & r>=0 && r<=1) {
          MT=t;
        }
      }
    }
    float R1 = TLineToLine(play.X-6, play.Y-24, play.X+6, play.Y, X+Offx, Y+Offy, X+Offx+cos(R), Y+Offy+sin(R));
    float R2 = TLineToLine(play.X+6, play.Y-24, play.X-6, play.Y, X+Offx, Y+Offy, X+Offx+cos(R), Y+Offy+sin(R));
    if (R1>=0 && R1<=1 || R2>=0 && R2<=1) {
      float T1 = TLineToLine(X+Offx, Y+Offy, X+Offx+cos(R), Y+Offy+sin(R), play.X-6, play.Y-24, play.X+6, play.Y);
      float T2 = TLineToLine(X+Offx, Y+Offy, X+Offx+cos(R), Y+Offy+sin(R), play.X+6, play.Y-24, play.X-6, play.Y);
      if (min(T1, T2)>0 && min(T1, T2)<MT) {
        AThurt(dmg);
        if(!lazer){
          MT=min(MT, min(T1, T2));
          float[] tmp = {play.X+cos(R)*MT, play.Y-12+sin(R)*MT};
          return tmp;
        }
      }
    }
    float[] tmp = {X+Offx+cos(R)*MT,Y+Offy+sin(R)*MT};
    return tmp;
  }

  public float[] Enyscan(float R,boolean lazer,float Offx,float Offy) {
    float MT=9999;
    int T=-1;
    float OX=X+Offx;
    float OY=Y+Offy;
    float NX=X+Offx+cos(R);
    float NY=Y+Offy+sin(R);
    int[] list=CB(OX, OY, NX+cos(R)*1000, NY+sin(R)*1000);
    for (int p=0; p<list.length; p++) {
      int i=list[p];
      float r = TLineToLine(CSX[i], CSY[i], CEX[i], CEY[i], OX, OY, NX, NY);
      if (r>=0 && r<=1) {
        float t = TLineToLine(OX, OY, NX, NY, CSX[i], CSY[i], CEX[i], CEY[i]);
        if (t>0 && t<MT & r>=0 && r<=1) {
          MT=t;
        }
      }
    }
    float R1 = TLineToLine(play.X-6, play.Y-24, play.X+6, play.Y, X+Offx, Y+Offy, X+Offx+cos(R), Y+Offy+sin(R));
    float R2 = TLineToLine(play.X+6, play.Y-24, play.X-6, play.Y, X+Offx, Y+Offy, X+Offx+cos(R), Y+Offy+sin(R));
    if (R1>=0 && R1<=1 || R2>=0 && R2<=1) {
      float T1 = TLineToLine(X+Offx, Y+Offy, X+Offx+cos(R), Y+Offy+sin(R), play.X-6, play.Y-24, play.X+6, play.Y);
      float T2 = TLineToLine(X+Offx, Y+Offy, X+Offx+cos(R), Y+Offy+sin(R), play.X+6, play.Y-24, play.X-6, play.Y);
      if (min(T1, T2)>0 && min(T1, T2)<MT) {
        if(!lazer){
          MT=min(MT, min(T1, T2));
          float[] tmp = {play.X+cos(R)*MT, play.Y-12+sin(R)*MT};
          return tmp;
        }
      }
    }
    float[] tmp = {X+Offx+cos(R)*MT,Y+Offy+sin(R)*MT};
    return tmp;
  }
  public void Checkfor(){//find better sulucion
    int buffer=3;
    if(!(Checkforsub(X-W/2,Y+buffer,X-W/2,Y-H-buffer)||
    Checkforsub(X+W/2,Y+buffer,X+W/2,Y-H-buffer)||
    Checkforsub(X-W/2-buffer,Y,X+W/2+buffer,Y)||
    Checkforsub(X-W/2+buffer,Y-H,X+W/2+buffer,Y-H))){
      Ignore=false;
    }
  }
  public boolean Checkforsub(float SX,float SY,float EX,float EY){
    int[] list=CB(SX,SY,EX,EY);
    for(int e=0;e<list.length;e++){
      int i=list[e];
      float t;
      float r;
      t=TLineToLine(SX,SY,EX,EY,CSX[i],CSY[i],CEX[i],CEY[i]);
      r=TLineToLine(CSX[i],CSY[i],CEX[i],CEY[i],SX,SY,EX,EY);
      float T=1;
      if(t<T && t>0 && r<=1 && r>=0){
          T=t;
          return true;
      }
    }
    return false;
  }
}

public void NewAI(float X,float Y,String T,boolean M){
  //Need to figure a better way of doin this
  switch(T){//
    case "Bug":
      ListAi.add(new Bug(X,Y,M));
    break;
    case "Fly":
      ListAi.add(new Fly(X,Y,M));
    break;
    case "Target":
      ListAi.add(new Target(X,Y,M));
    break;
    case "Spewer":
      ListAi.add(new Spewer(X,Y,M));
    break;
    case "testBoss":
      ListAi.add(new testBoss(X,Y,M));
    break;
    case "Maze":
      ListAi.add(new Maze(X,Y,M,false));
    break;
    case "Laze":
      ListAi.add(new Laze(X,Y,M,false));
    break;
    case "Maze_Boss":
      ListAi.add(new Maze(X,Y,M,true));
    break;
    case "Laze_Boss":
      ListAi.add(new Laze(X,Y,M,true));
    break;
    case "tower":
      ListAi.add(new Tower(X,Y,M));
    break;
    case "napalm":
      ListAi.add(new Napalm(X,Y,M));
    break;
    case "Spirit":
      ListAi.add(new Spirit(X,Y,M));
    break;
    case "Guardian":
      ListAi.add(new Guardian(X,Y,M));
    break;
  }
}

public float[] coll(float OX,float OY,float NX,float NY,boolean Ignore){
  for(int i=0;i<CSX.length;i++){//OH god this is terrible
    float T=100;
    float N=0;
    float t;
    float r;
    
    t=TLineToLine(OX,OY,NX,NY,CSX[i],CSY[i],CEX[i],CEY[i]);
    r=TLineToLine(CSX[i],CSY[i],CEX[i],CEY[i],OX,OY,NX,NY);
    for(int u=0;u<4;u++){
      if(t<=T && t>=0 && r<=1 && r>=0 && !(Ignore && CT[i]==1)){
        {
          T=t;
          N=u;
        }
      }
    }
    if(T>=0 && T<=1){float[] tmp = {T,N,i};return tmp;}
  }
  float[] tmp = {-1};
  return tmp;
}
class ANIMG{
  PImage[] Frames;
  int[] RFrames;
  int delay=0;
  int timer=0;
  int frame=0;
  ANIMG(String file){
    Frames = new PImage[0];
    RFrames = new int[0];
    int[] RLOAD = new int[0];
    byte[] DATA = loadBytes(file);
    int header=0;
    int N = BgetI(DATA,header,2);
    header+=2;
    String sub = BgetS(DATA,header,N);
    header+=N;
        N = BgetI(DATA,header,2);
    header+=2;
    for(int i=0;i<N;i++){
      RFrames = append(RFrames,BgetI(DATA,header,2));
      boolean IS=true;
      for(int u=0;u<RLOAD.length;u++){
        if(RLOAD[u]==BgetI(DATA,header,2)){
          IS=false;
        }
      }
      if(IS){
        RLOAD = append(RLOAD,BgetI(DATA,header,2));
      }
      header+=2;
    }
    for(int u=0;u<RLOAD.length;u++){
      Frames = (PImage[])append(Frames,SloadImage("Maps/"+MAPname+"/"+(sub)+(u)+".png"));
      image(Frames[RFrames[u]],0,0);
    }
        delay = BgetI(DATA,header,2);
    header+=2;
  }
  
  public void ANM(){
    timer--;
    if(timer<0){
      timer=delay;
      frame++;
    }
    if(frame==RFrames.length){
      frame=0;
    }
  }
  
  public void ANR(float x,float y){
    image(Frames[RFrames[frame]],x,y);
  }
  
  public int width(){
    return Frames[RFrames[frame]].width;
  }
  
  public int height(){
    return Frames[RFrames[frame]].height;
  }
}

EnANIMG[] enANIM;
ProANIMG[] proANIM;
IntDict EAR;//me piss and shit

public void EnemyAINIC(){
  enANIM = new EnANIMG[0];
  EAR = new IntDict(0);
  File WATFFEA = new File(sketchPath()+"/data/Hostiles");
  for(int i=0;i<WATFFEA.list().length;i++){
    File tmper = new File(sketchPath()+"/data/Hostiles/"+WATFFEA.list()[i]);
    if(tmper.list().length>0){
      tmper = new File(sketchPath()+"/data/Hostiles/"+WATFFEA.list()[i]+"/file.EAF");
      if(tmper.exists()){
        enANIM = (EnANIMG[])append(enANIM,new EnANIMG(WATFFEA.list()[i]));
        EAR.add(WATFFEA.list()[i],EAR.size());
      }
    }
  }
  BOSSHP = new IntList();//wait why am i here?
  BOSSID = new IntList();
}

public void ProAINIC(){
  proANIM = new ProANIMG[0];
  proANIM = (ProANIMG[])append(proANIM,new ProANIMG("PlayerRocket"));
  proANIM = (ProANIMG[])append(proANIM,new ProANIMG("Spit"));
}

class ProANIMG{
  PImage[] Frames;
  int[] RFrames;
  int delay=0;
  int frame=0;
  ProANIMG(String file){
    Frames = new PImage[0];
    RFrames = new int[0];
    int[] RLOAD = new int[0];
    byte[] DATA = loadBytes("Pro/"+file+"/File.SFF");
    int header=0;
    int N = BgetI(DATA,header,2);
    header+=2;
    String sub = BgetS(DATA,header,N);
    header+=N;
        N = BgetI(DATA,header,2);
    header+=2;
    for(int i=0;i<N;i++){
      RFrames = append(RFrames,BgetI(DATA,header,2));
      boolean IS=true;
      for(int u=0;u<RLOAD.length;u++){
        if(RLOAD[u]==BgetI(DATA,header,2)){
          IS=false;
        }
      }
      if(IS){
        RLOAD = append(RLOAD,BgetI(DATA,header,2));
      }
      header+=2;
    }
    for(int u=0;u<RLOAD.length;u++){
      Frames = (PImage[])append(Frames,SloadImage("Pro/"+file+"/File"+(u)+".png"));
      image(Frames[RFrames[u]],0,0);
    }
        delay = BgetI(DATA,header,2);
    header+=2;
  }
  
  public void ANR(float x,float y,int frame){
    image(Frames[RFrames[frame]],x-Frames[RFrames[frame]].width/2,y-Frames[RFrames[frame]].height/2);
  }
  
  public int width(){
    return Frames[RFrames[frame]].width;
  }
  
  public int height(){
    return Frames[RFrames[frame]].height;
  }
  
  public int Max(){
    return Frames.length;
  }
}

class EnANIMG{
  int delay;
  PImage[] Air;
  PImage[] Move;
  PImage[] Stand;
  EnANIMG(String file){
    Air = new  PImage[0];
    Move = new  PImage[0];
    Stand = new  PImage[0];
    byte[] DATA = loadBytes(sketchPath()+"/data/Hostiles/"+file+"/file.EAF");
    int header=0;
    delay = BgetI(DATA,header,2);
    header+=2;
    int U = BgetI(DATA,header,2);
    header+=2;
    for(int i=0;i<U;i++){
      Air = (PImage[])append(Air,SloadImage("Hostiles/"+file+"/air"+i+".png"));
    }
        U = BgetI(DATA,header,2);
    header+=2;
    for(int i=0;i<U;i++){
      Move = (PImage[])append(Move,SloadImage("Hostiles/"+file+"/move"+i+".png"));
    }
        U = BgetI(DATA,header,2);
    header+=2;
    for(int i=0;i<U;i++){
      Stand = (PImage[])append(Stand,SloadImage("Hostiles/"+file+"/stand"+i+".png"));
    }
  }
  public void DIMG(float X,float Y,float w,float h,int frame,boolean moveing,boolean Airborn,int C){
    tint(C);
    if(Airborn){
      image(Air[frame],X-w,Y-h,w*2,h);
    }else if(moveing){
      image(Move[frame],X-w,Y-h,w*2,h);
    }else{
      image(Stand[frame],X-w,Y-h,w*2,h);
    }
    noTint();
  }
  public int getM(boolean mov,boolean air){
    if(air){
      return Air.length;
    }else if(mov){
      return Move.length;
    }else{
      return Stand.length;
    }
  }
}

public PImage SloadImage(String path){
  if(Secret!=null){
    return Secret;
  }
  PImage img=loadImage(path);
  if(img!=null){
    return img;
  }
  ErrorTimer=120;
  PrintCon("image " + path + " doesn't exist!");
  img=loadImage("Misc/mising.png");
  if(img!=null){
    return img;
  }
  PrintCon("bruh where the mising image?");
  PrintCon("now i have to make one!");
  PGraphics fuck = createGraphics(64,64);
  fuck.beginDraw();
  for(int x=0;x<64;x++){
    for(int y=0;y<64;y++){
      fuck.stroke(x^y*4);
      fuck.point(x,y);
    }
  }
  fuck.endDraw();
  return fuck;
}
public void setupBoxs(){
  boxs = new CBOX[0];
  for(int i=0;i<CSX.length;i++){
    cubes(CSX[i],CSY[i],CEX[i],CEY[i],i);
  }
}

CBOX[] boxs;

class CBOX{
  int x;
  int y;
  int[] c;
  CBOX(int X,int Y){
    x=X;
    y=Y;
    c = new int[0];
  }
  public boolean isH(int X,int Y){
    return X==x && Y==y;
  }
  public void A(int e){
    for(int i=0;i<c.length;i++){
      if(c[i]==e){
        return;
      }
    }
    c=append(c,e);
  }
  public void R(int e){
    boolean T=false;
    for(int i=0;i<c.length-1;i++){
      if(T){
        c[i]=c[i+1];
      }
      if(c[i]==e){T=true;}
    }
  }
}

float size = 256;

public void IC(int x,int y,int T){
  for(int i=0;i<boxs.length;i++){
    if(boxs[i].isH(x,y)){
      boxs[i].A(T);
      return;
    }
  }
  boxs = (CBOX[])append(boxs,new CBOX(x,y));
  boxs[boxs.length-1].A(T);
}

//int[] GA(int x,int y){
//  for(int i=0;i<boxs.length;i++){
//    if(boxs[i].isH(x,y)){
//      return boxs[i].c;
//    }
//  }
//  int[] tmp = {};
//  return tmp;
//}

public void cubes(float x1,float y1,float x2,float y2,int T){
  float minX=min(x1,x2);
  float maxX=max(x1,x2);
  float minY=min(y1,y2);
  float maxY=max(y1,y2);
  int bx=PApplet.parseInt(x1>x2)*-1;
  int by=PApplet.parseInt(y1>y2)*-1;
  IC(floor(x1/size),floor(y1/size),T);
  IC(floor(x2/size),floor(y2/size),T);
  for(float x=rev(minX,size);x<=rev(maxX-size,size)-bx+size;x+=size){
    float r = TLineToLine(x,0,x,1,x1,y1,x2,y2);
    circle(x,r,4);
    IC(floor(x/size+bx),floor(r/size),T);
  }
  for(float y=rev(minY,size);y<=rev(maxY-size,size)-by+size;y+=size){
    float r = TLineToLine(0,y,1,y,x1,y1,x2,y2);
    circle(r,y,4);
    IC(floor(r/size),floor(y/size+by),T);
  }
}

public float rev(float n,float v){
  return n-n%v;
}

public void updBx(int i){
  for(int u=0;u<boxs.length;u++){
      boxs[u].R(i);
    }
  cubes(CSX[i],CSY[i],CEX[i],CEY[i],i);
}

public int[] getBC(int x,int y){
  for(int u=0;u<boxs.length;u++){
    if(boxs[u].isH(x,y)){
      return boxs[u].c;
    }
  }
  int[] tmp = {};
  return tmp;
}

public int[] CB(float x1,float y1,float x2,float y2){
  int[] out = new int[0];
  float minX=min(x1,x2);
  float maxX=max(x1,x2);
  float minY=min(y1,y2);
  float maxY=max(y1,y2);
  int bx=PApplet.parseInt(x1>x2)*-1;
  int by=PApplet.parseInt(y1>y2)*-1;
  out = concat(out,getBC(floor(x1/size),floor(y1/size)));
  out = concat(out,getBC(floor(x2/size),floor(y2/size)));
  for(float x=rev(minX+size,size);x<rev(maxX,size)-bx+size;x+=size){
    float r = TLineToLine(x,0,x,1,x1,y1,x2,y2);
    out = concat(out,getBC(floor(x/size+bx),floor(r/size)));
  }
  for(float y=rev(minY+size,size);y<rev(maxY,size)-by+size;y+=size){
    float r = TLineToLine(0,y,1,y,x1,y1,x2,y2);
    out = concat(out,getBC(floor(r/size),floor(y/size+by)));
  }
  return out;
}

//BOX TOO TINY
//ADD MORE




//yea it unpacks everything
//but it works

public void UPDATE(){
  saveBytes(sketchPath()+"/tmp.zip",loadBytes("https://github.com/PeakRead/DF/archive/refs/heads/main.zip"));
  File zipfile = new File(sketchPath()+"/tmp.zip");
  try{
    ZipFile opener = new ZipFile(zipfile);
    Enumeration files = opener.entries();
    while(files.hasMoreElements()){
      ZipEntry tmp = (ZipEntry)files.nextElement();
      InputStream open = opener.getInputStream(tmp);
      if(open.available()==0){continue;}
      byte[] shit = new byte[0];
      while(open.available()>0){
        byte[] out = new byte[1024];
        int readed = open.read(out);
        out = subset(out,0,readed);
        shit = concat(shit,out);
      }
      saveBytes(sketchPath()+"/"+removefirst(tmp.getName()),shit);
    }
  }catch(Exception e){
    e.printStackTrace();
  }
  launch(sketchPath()+"/ProjectDFTEST.exe");
  exit();
  //PrintCon(sketchPath());
  //ErrorTimer=120;
  
}

public String removefirst(String text){
  String[] texts = split(text,'/');
  String out="";
  for(int i=1;i<texts.length;i++){
    out+=texts[i];
    if(i!=texts.length-1){
      out+='/';
    }
  }
  return out;
}
//weewoo wagon

String[] Console = new String[100];
boolean ConsoleUP = false;
String ConsoleInput = "";
int scrool=0;

public void resetCon(){
  for(int i=0;i<Console.length;i++){
    Console[i] = "";
  }
}

public void PrintCon(String text){
  for(int i=Console.length-2;i>=0;i--){
    Console[i+1] = Console[i]; 
  }
  Console[0]=text;
}

public void DrawConsole(){
  noStroke();
  fill(0xff00FF00,200);
  rect(0,0,width,20*15);
  fill(100);
  rect(width-10,PApplet.parseFloat(scrool)/80*20*13,10,20*2);
  fill(0xffFF00FF);
  for(int i=scrool;i<20+scrool;i++){
    try{
    text(Console[i],0,19*15-15*i+scrool*15);
    }catch(Exception e){/*ignore*/}
  }
  fill(0xffFFFF00,200);
  rect(0,20*15,width,15);
  fill(0xff0000FF);
  text(ConsoleInput,0,20*15);
}

String[] Confunc = {
"help : displays this",
"resurect : resurects the player if dead",
"noclip : noclip",
"map : load a map",
"maps : displays all files in the maps folder",
"clean : cleans the console",
"cum : cast a 500dmg lighting call on self",
"debugdraw : toggle the debugdrawing of stuff",
"god : invunrabylaty",
"notime : prevents the players HP from going below 0",
"phys : toggle the physics(player included)",
"restart : reset the player",
"error : fake a error",
"text : print shit to screen"};

public void runConinput(){
  String[] args = split(ConsoleInput,' ');
  ConsoleInput="";
  switch(args[0]){
    case "":
      if(random(0,100)<5){
        PrintCon("nothing!");
      }else{
        PrintCon("]");
      }
      break;
      case "help":
      for(int i=0;i<Confunc.length;i++){
        PrintCon(Confunc[i]);
      }
    break;
    case "resurect":
      if(!Gaming){PrintCon("not gaming");break;}
      if(play.HP<=0){
        play.HP=100;
        PrintCon("get up!");
      }else{
        PrintCon("your not dead dumbass");
      }
    break;
    case "noclip":
      if(!Gaming){PrintCon("not gaming");break;}
      if(!play.Noclip){
        PrintCon("noclip on");
        play.Noclip=true;
      }else{
        PrintCon("noclip off");
        play.Noclip=false;
      }
    break;
    case "map":
      if(Gaming){
        if(args.length<2){
          PrintCon("expected more arguments");
          break;
        }
        PrintCon("loading Map:"+args[1]);
        openMap(args[1]);
        play.MT();
      }else{
        if(args.length<2){
          PrintCon("expected more arguments");
          break;
        }
        Start(args[1]);
      }
    break;
    case "clean":
      resetCon();
    break;
    case "uwu":
      PrintCon("uwu");
    break;
    case "cum":
      if(!Gaming){PrintCon("not gaming");break;}
      AddPartic(1,play.X,play.Y,play.X,-10000,60,0xffFFFFFF,true);
      for(int ohno=0;ohno<50;ohno++){
        AddPartic(2,play.X,play.Y,random(-10,10),random(-10,10),60,0xffFFFFFF,true);
      }
      expd(play.X, play.Y+8, 256, 500, 500, true);
      PrintCon("orh!");
    break;
    case "debugdraw":
      DebugDraw=!DebugDraw;
      PrintCon(DebugDraw+"");
    break;
    case "god":
      play.God=!play.God;
      PrintCon("god is now " + play.God);
    break;
    case "notime":
      play.notime=!play.notime;
      PrintCon("time to die? " + play.notime);
    break;
    case "tmp":
      NewAI(play.X-32,play.Y-128,"Maze_Boss",false);
      NewAI(play.X+32,play.Y-128,"Laze_Boss",false);
    break;
    case "phys":
      RunPhys=!RunPhys;
    break;
    case "maps":
      File pointer = new File(sketchPath()+"/data/Maps");
      String[] name = pointer.list();
      if(name.length==0){return;}
      for(int i=0;i<name.length;i++){
        PrintCon(name[i]);
      }
    break;
    case "nextwave":
      nextWave();
    break;
    case "gotowave":
      if(args.length<2){
        PrintCon("expected more arguments");
        break;
      }
      round=PApplet.parseInt(args[1])-1;
      nextWave();
    break;
    case "restart":
      play.MT();
    break;
    case "error":
      ErrorTimer=120;
    break;
    case "tantsumon":
      if(args.length<2){
        PrintCon("expected more arguments");
        break;
      }
      arenaSpawn(args[1]);
    break;
    case "text":
      if(args.length<2){
        PrintCon("expected more arguments");
        break;
      }
      String text="";
      for(int i=1;i<args.length;i++){
        text += args[i];
        if(i!=args.length-1){text+=' ';}
      }
      texttoscren(text);
    break;
    default:
      PrintCon("what?");
    break;
  }
}

public void texttoscren(String text){
  TextString=text;
  TextCurr=0;
  TextShow=true;
}
ArrayList<Effect> ListEffects;
IntList Ekill;

class Line extends Effect{
  Line(float nX,float nY,float nVX,float nVY,int ntime,int nC){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
  }
  public void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    //nothing!!
    time--;
  }
  public void drawE(){
    strokeWeight((float)time*(float)5/(float)Mtime);
    stroke(C,(float)time*(float)255/(float)Mtime);
    line(X,Y,VX,VY);
  }
}

class GravPoint extends Effect{
  GravPoint(float nX,float nY,float nVX,float nVY,int ntime,int nC){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
  }
  public void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    VY+=0.2f;
    X+=VX;
    Y+=VY;
    time--;
  }
  public void drawE(){
    strokeWeight((float)time*(float)5/(float)Mtime);
    stroke(C,(float)time*(float)255/(float)Mtime);
    point(X,Y);
  }
}

class VELLPoint extends Effect{
  VELLPoint(float nX,float nY,float nVX,float nVY,int ntime,int nC){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
  }
  public void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    X+=VX;
    Y+=VY;
    time--;
  }
  public void drawE(){
    strokeWeight((float)time*(float)5/(float)Mtime);
    stroke(C,(float)time*(float)255/(float)Mtime);
    point(X,Y);
  }
}

class LAGPoint extends Effect{
  float PX=0;
  float PY=0;
  LAGPoint(float nX,float nY,float nVX,float nVY,int ntime,int nC){
    PX=nX;
    PY=nY;
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
  }
  public void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    PX=X;
    PY=Y;
    VY+=0.2f;
    X+=VX;
    Y+=VY;
    time--;
  }
  public void drawE(){
    strokeWeight((float)time*(float)5/(float)Mtime);
    stroke(C,(float)time*(float)255/(float)Mtime);
    line(X,Y,PX,PY);
  }
}

class Explode extends Effect{
  Explode(float nX,float nY,float nVX,float nVY,int ntime,int nC){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
  }
  int bombtimer=0;
  public void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    //nothing!! but cooler!
    time--;
  }
  public void drawE(){
    if(Configs.get("SimpleExplosion")==0){
      strokeWeight(3);
      stroke(C,(float)time*(float)255/(float)Mtime);
    }else{
      noStroke();
    }
    fill(C,(float)time*(float)200/(float)Mtime);
    circle(X,Y,VX);
  }
}

class Wind extends Effect{
  Wind(float nX,float nY,float nVX,float nVY,int ntime,int nC){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
  }
  public void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    X+=VX;
    Y+=VY;
    time--;
  }
  public void drawE(){
    strokeWeight((float)time*(float)5/(float)Mtime);
    stroke(C,(float)time*(float)255/(float)Mtime);
    line(X-VX*2,Y-VY*2,X+VX*2,Y+VY*2);
  }
}

class SubText extends Effect{
  String text;
  SubText(float nX,float nY,float nVX,float nVY,int ntime,int nC,String ntext){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
    text=ntext;
  }
  public void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    X+=VX;
    Y+=VY;
    time--;
  }
  public void drawE(){
    fill(C);
    text(text,X,Y);
  }
}

class Effect{
  float X;
  float Y;
  int C;
  float VX;
  float VY;
  int time;
  int Mtime;
  //1line
  //2pointG
  //3pointV
  //4pointL
  //5EXP
  //Effect(float nX,float nY,float nVX,float nVY,int nType,int ntime,color nC){
  //  X=nX;
  //  Y=nY;
  //  PX=X;
  //  PY=Y;
  //  VX=nVX;
  //  VY=nVY;
  //  Type=nType;
  //  time=ntime;
  //  Mtime=ntime;
  //  C=nC;
  //}
  public void mathE(int T){
  }
  public void drawE(){
  }
}

public void NewPartic(Effect newthing,boolean Important){
  if(Configs.get("DrawEffects")==0 && !Important){return;}
  ListEffects.add(newthing);
}
  
//old version
public void AddPartic(int T,float X,float Y,float VX,float VY,int time,int C,boolean Important){
  if(Configs.get("DrawEffects")==0 && !Important){return;}
  switch(T){
    case 1:
      ListEffects.add(new Line(X,Y,VX,VY,time,C));
    break;
    case 2:
      ListEffects.add(new GravPoint(X,Y,VX,VY,time,C));
    break;
    case 3:
      ListEffects.add(new VELLPoint(X,Y,VX,VY,time,C));
    break;
    case 4:
      ListEffects.add(new LAGPoint(X,Y,VX,VY,time,C));
    break;
    case 5:
      ListEffects.add(new Explode(X,Y,VX,VY,time,C));
    break;
    case 6:
      ListEffects.add(new Wind(X,Y,VX,VY,time,C));
    break;
    case 7:
      ListEffects.add(new SubText(X,Y,VX,VY,time,C,""));
      //Text tmp=(Text)ListEffects.get(ListEffects.size()-1);
      //tmp.text="test";
      //ListEffects.set(ListEffects.size()-1,tmp);
      //eh it works
    break;
  }
}

public void MathEffects(){
  for(int i=0;i<ListEffects.size();i++){
    ListEffects.get(i).mathE(i);
  }
  Ekill.reverse();
  for(int i=0;i<Ekill.size();i++){
    ListEffects.remove(Ekill.get(i));
  }
  Ekill.clear();
}

public void DrawEffects(){
  for(int i=0;i<ListEffects.size();i++){
    ListEffects.get(i).drawE();
  }
}
boolean keys[];
boolean Skeys[];

IntDict KeyRedirect;//for convinence
KeyBind[] Keybinds;

class KeyBind{
  char pri;
  boolean iscode;
  byte code;
  KeyBind(char Pri,int num,String ID){
    if(num==-1){
      pri=Pri;
      iscode=false;
      code=0;
    }else{
      pri='?';
      iscode=true;
      code=PApplet.parseByte(num);
    }
    KeyRedirect.set(ID,KeyRedirect.size());
  }
  public boolean check(){
    if(iscode){
    return EYS.getSkey(code);
    }else{
    return EYS.getkey(pri);
    }
  }
}

public boolean GetKeyBind(String code){
  if(!KeyRedirect.hasKey(code)){
    PrintCon("sorry for that");
    PrintCon("it seams that "+code+" does not exist");
    ErrorTimer=120;
    return false;
  }
  return Keybinds[KeyRedirect.get(code)].check();
}

public void setupKeys(){
  EYS=new keyboard();
  KeyRedirect = new IntDict();
  Keybinds = new KeyBind[0];
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('w',-1,"Player_Move_Up"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('d',-1,"Player_Move_Right"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('a',-1,"Player_Move_Left"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('s',-1,"Player_Move_Down"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('r',-1,"Player_Restart"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('?',4,"Player_Boost"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('1',-1,"Weapon_1"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('2',-1,"Weapon_2"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('3',-1,"Weapon_3"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('4',-1,"Weapon_4"));
}

class keyboard
{
  keyboard()
  {
    keys = new boolean[128];
    Skeys = new boolean[8];
  }
  public boolean getkey(char A)
  {
    return keys[PApplet.parseInt(A)];
  }
  public boolean getSkey(int i)
  {
    return Skeys[i];
  }
}

public void keyPressed()
{
  if(!WaitingUser){
    if(!ConsoleUP){
      if(key>=0&key<=128)
      {
        try{
          keys[PApplet.parseInt((key+"").toLowerCase().charAt(0))]=true;
        }catch(Exception E){
          PrintCon("TODO FIX THIS KEY");
          ErrorTimer=120;
        }
      }
      //if(key=='n'){play.Noclip){play.Noclip=false;}else{play.Noclip=true;}}
      if(keyCode==UP){Skeys[0]=true;}
      if(keyCode==RIGHT){Skeys[1]=true;}
      if(keyCode==LEFT){Skeys[2]=true;}
      if(keyCode==DOWN){Skeys[3]=true;}
      if(keyCode==SHIFT){Skeys[4]=true;}
      if(keyCode==CONTROL){Skeys[5]=true;}
      if(keyCode==ALT){Skeys[6]=true;}
      if(keyCode==TAB){Skeys[7]=true;}
      if(GetKeyBind("Weapon_1")){WeaponSellected=0;}
      if(GetKeyBind("Weapon_2")){WeaponSellected=1;}
      if(GetKeyBind("Weapon_3")){WeaponSellected=2;}
      if(GetKeyBind("Weapon_4")){WeaponSellected=3;}
      if(key=='\\'){ConsoleUP=!ConsoleUP;}
      if(key==ESC && Gaming){
        if(MenuPaused){
          MenuTurnOffAll();
          MenuPaused=false;
          RunPhys=true;
        }else{
          MenuTurnOffAll();
          MenuTurnOn("PAUSE_MENU");
          MenuPaused=true;
          RunPhys=false;
        }
      }
    }else{
      if(key=='\\'){ConsoleUP=!ConsoleUP;}
      switch(keyCode){
        case ENTER:
          runConinput();
        break;
        case BACKSPACE:
          if(ConsoleInput.length()>0){
            ConsoleInput=ConsoleInput.substring(0,ConsoleInput.length()-1);
          }
        break;
        case SHIFT:
        //ohno
        break;
        case ESC:
          ConsoleUP=false;
          key=0;
        break;
        default:
          ConsoleInput+=key;
        break;
      }
    }
  }else{
    if(key==CODED){
      Keybinds[ToWait].code=returnCODED(keyCode);
      Keybinds[ToWait].iscode=true;
      WaitingUser=false;
    }else{
      Keybinds[ToWait].pri=key;
      Keybinds[ToWait].iscode=false;
    }
    WaitingUser=false;
  }
  key=0;
}
public void keyReleased()
{
  if(key>=0&key<=128)
  {
    try{
      keys[PApplet.parseInt((key+"").toLowerCase().charAt(0))]=false;
    }catch(Exception E){
      PrintCon("TODO FIX THIS KEY");
      ErrorTimer=120;
    }
  }
  if(keyCode==UP){Skeys[0]=false;}
  if(keyCode==RIGHT){Skeys[1]=false;}
  if(keyCode==LEFT){Skeys[2]=false;}
  if(keyCode==DOWN){Skeys[3]=false;}
  if(keyCode==SHIFT){Skeys[4]=false;}
  if(keyCode==CONTROL){Skeys[5]=false;}
  if(keyCode==ALT){Skeys[6]=false;}
  if(keyCode==TAB){Skeys[7]=false;}
}

public void mouseWheel(MouseEvent event) {
  float e = event.getCount();
  if(Configs.get("ReversedWhell")==1){
    e*=-1;
  }
  if(!ConsoleUP){
    if(WeaponSellected<HWeapon.length-1 && e==-1){
      WeaponSellected++;
      return;
    }
    if(WeaponSellected>0 && e==1){
      WeaponSellected--;
      return;
    }
    if(WeaponSellected==HWeapon.length-1 && e==-1){
      WeaponSellected=0;
      return;
    }
    if(WeaponSellected==0 && e==1){
      WeaponSellected=PApplet.parseByte(HWeapon.length-1);
      return;
    }
  }else{
    if(e==-1 && scrool>0){
      scrool--;
    }
    if(e== 1 && scrool<80){
      scrool++;
    }
  }
}

public byte returnCODED(int c){
  if(c==UP){return 0;}
  if(c==RIGHT){return 1;}
  if(c==LEFT){return 2;}
  if(c==DOWN){return 3;}
  if(c==SHIFT){return 4;}
  if(c==CONTROL){return 5;}
  if(c==ALT){return 6;}
  if(c==TAB){return 7;}
  return 0;
}
public String returnTEXE(int c){
  if(c==0){return "UP";}
  if(c==1){return "RIGHT";}
  if(c==2){return "LEFT";}
  if(c==3){return "DOWN";}
  if(c==4){return "SHIFT";}
  if(c==5){return "CONTROL";}
  if(c==6){return "ALT";}
  if(c==7){return "TAB";}
  return "????";
}
float[] CSX;
float[] CSY;
float[] CEX;
float[] CEY;
byte[] CT;
float[] TX;
float[] TY;
float[] TW;
float[] TH;
int[] TT;
boolean[] TE;
float[] EX;
float[] EY;
String[] ET;
int  [] ES;
int  [] ED;
boolean[] EM;
String MAPname;
float PSX;
float PSY;
float NMOX=0;
float NMOY=0;
PShape WORLD;
PShape BACK;
boolean BACEXIST;
PGraphics Background;

public void openMap(String MAP){
  ListAi = new ArrayList<AI>();
  ListEffects = new ArrayList<Effect>();
  ListPR = new ArrayList<PRO>();
  kill = new IntList();
  Ekill = new IntList();
  killPR = new IntList();
  Propredirect = new IntDict();
  try{
    BACK = loadPly("Maps/"+MAP+".ply");
    BACEXIST=true;
  }catch(Exception e){
    PrintCon("no background detected");
    PrintCon(e.getMessage());
    ErrorTimer=120;
    BACEXIST=false;
  }
  Background = createGraphics(width,height,P3D);
  byte[] DATA;
  try{
    DATA = loadBytes("Maps/"+MAP+".BM");
  }catch(Exception e){
    PrintCon("???");
    PrintCon(e.getMessage());
    ErrorTimer=120;
    Gaming=false;
    MenuTurnOffAll();
    MenuTurnOn("MAIN_MENU");
    return;
  }
  if(DATA == null){
    PrintCon("map " + MAP + " seams to not exist");
    ErrorTimer=120;
    Gaming=false;
    return;
  }
  MAPname = MAP;
  int num=BgetI(DATA,2,2);
  int Header=0;
  Header+=4;
  CSX = new float[num];
  CSY = new float[num];
  CEX = new float[num];
  CEY = new float[num];
  CT = new byte[num];
  for(int i=0;i<num;i++){
    CSX[i] = BgetI(DATA,0 +Header,2);
    CSY[i] = BgetI(DATA,2 +Header,2);
    CEX[i] = BgetI(DATA,4 +Header,2);
    CEY[i] = BgetI(DATA,6 +Header,2);
    CT[i] = (byte)BgetI(DATA,8 +Header,2);
    Header+=10;
  }
  
  num=BgetI(DATA,Header,2);
  Header+=2;
  TX = new float[num];
  TY = new float[num];
  TW = new float[num];
  TH = new float[num];
  TT = new int[num];
  TE = new boolean[num];
  for(int i=0;i<num;i++){
    TX[i] = BgetI(DATA,0 +Header,2);
    TY[i] = BgetI(DATA,2 +Header,2);
    TW[i] = BgetI(DATA,4 +Header,2);
    TH[i] = BgetI(DATA,6 +Header,2);
    TT[i] = BgetI(DATA,8 +Header,2);
    TE[i] = true;
    Header+=10;
  }
  EX = new float[0];
  EY = new float[0];
  ET = new String[0];
  ES = new int[0];
  EM = new boolean[0];
  String[] yes = new String[0];
  int NUM=BgetI(DATA, Header, 2);
  Header+=2;
  for (int i=0; i<NUM; i++) {
    num=BgetI(DATA, Header, 2);
    Header+=2;
    String tmper=BgetS(DATA, Header, num);
    Header+=num;
    yes = append(yes, tmper);
  }
  num=BgetI(DATA, Header, 2);
  Header+=2;
  for (int i=0; i<num; i++) {
    EX = append(EX, PApplet.parseFloat  (BgetI(DATA, 0 +Header, 2)));
    EY = append(EY, PApplet.parseFloat  (BgetI(DATA, 2 +Header, 2)));
    ET = append(ET,        (yes[BgetI(DATA,4 +Header,2)]));
    ES = append(ES, PApplet.parseInt    (BgetI(DATA, 6 +Header, 2)));
    EM = (boolean[])append(EM, PApplet.parseBoolean(BgetI(DATA, 8 +Header, 1)));
    Header+=9;
  }
  yes = new String[0];
  NUM=BgetI(DATA, Header, 2);
  Header+=2;
  for (int i=0; i<NUM; i++) {
    num=BgetI(DATA, Header, 2);
    Header+=2;
    String tmper=BgetS(DATA, Header, num);
    Header+=num;
    yes = append(yes, tmper);
  }
  num=BgetI(DATA,Header,2);
  Header+=2;
  PROPL = new PROP[num];
  for(int i=0;i<num;i++){
    PROPL[i] = new PROP(PApplet.parseFloat(BgetI(DATA,0 +Header,2)),PApplet.parseFloat(BgetI(DATA,2 +Header,2)),yes[BgetI(DATA,4 +Header,2)]);
    Header+=6;
  }
  num=BgetI(DATA,Header,2);
  Header+=2;
  MAD = new door[num];
  for(int i=0;i<num;i++){
    float SX=BgetI(DATA,0 +Header,2);
    float SY=BgetI(DATA,2 +Header,2);
    float EX=BgetI(DATA,4 +Header,2);
    float EY=BgetI(DATA,6 +Header,2);
    int delay=BgetI(DATA,8 +Header,2);
    int ATcol=BgetI(DATA,10+Header,2);
    int ATpro=BgetI(DATA,12+Header,2);
    MAD[i] = new door(SX,SY,EX,EY,delay,ATcol,ATpro);
    Header+=14;
  }
  yes = new String[0];
  NUM=BgetI(DATA,Header,2);
  Header+=2;
  for(int i=0;i<NUM;i++){
    num=BgetI(DATA,Header,2);
    Header+=2;
    String tmper=BgetS(DATA,Header,num);
    Header+=num;
    yes = append(yes,tmper);
  }
      NUM=BgetI(DATA,Header,2);
  Header+=2;
  TMPWALL = new Wall[0];
  for(int i=0;i<NUM;i++){
    int tmp = BgetI(DATA,Header,2);
    Header+=2;
    TMPWALL = (Wall[])append(TMPWALL,new Wall(yes[tmp]));
    TMPWALL[i].Offx = BgetI(DATA,Header+0,2);
    TMPWALL[i].Offx = BgetI(DATA,Header+2,2);
    Header+=4;
    num = BgetI(DATA,Header,2);
      Header+=2;
    for(int u=0;u<num;u++){
      TMPWALL[i].x = append(TMPWALL[i].x,BgetI(DATA,Header+0,2));
      TMPWALL[i].y = append(TMPWALL[i].y,BgetI(DATA,Header+2,2));
      Header+=4;
    }
  }
  WORLD = createShape(GROUP);
  for(int i=0;i<TMPWALL.length;i++){
    PShape tmp = createShape();
    tmp.beginShape();
    textureWrap(REPEAT); 
    tmp.texture(TMPWALL[i].img);
    for(int u=0;u<TMPWALL[i].x.length;u++){
      tmp.vertex(TMPWALL[i].x[u],TMPWALL[i].y[u],TMPWALL[i].x[u]-TMPWALL[i].Offx,TMPWALL[i].y[u]-TMPWALL[i].Offy);
    }
    tmp.endShape();
    WORLD.addChild(tmp);
  }
  DATA=null;
  TMPWALL = null;
  //
  for(int i=0;i<ET.length;i++){
    if(ET[i].equals("playerS")){
      PSX=EX[i];
      PSY=EY[i];
    }
  }
  int U = TX.length;
  CBE = new boolean[U];
  for(int i=0;i<U;i++){
    CBE[i] = true;
  }
  setupProps();
  setupBoxs();
}

Wall[] TMPWALL;

class Wall{
  PImage img;
  String imgName;
  float[] x;
  float[] y;
  float Offx;
  float Offy;
  Wall(String nimgName){
    img = SloadImage("Textures/"+nimgName);
    imgName=nimgName;
    x = new float[0];
    y = new float[0];
  }
  public void change(String nimgName){
    img = loadImage(nimgName);
    imgName=nimgName;
  }
}

boolean[] CBE;

public void Restart(){ 
  TE=CBE.clone();
  ListAi = new ArrayList<AI>();
  ListEffects = new ArrayList<Effect>();
  ListPR = new ArrayList<PRO>();
  kill = new IntList();
  Ekill = new IntList();
  killPR = new IntList();
  DOORSOP=false;
}

public void trigger(){
  if(play.HP>0){
  for(int i=0;i<TX.length;i++){
    if(play.X>TX[i] && play.X<TX[i]+TW[i] && play.Y>TY[i] && play.Y<TY[i]+TH[i] && TE[i]){
      switch(TT[i]){
        case 0:
        AThurt(25);
        break;
        case 1:
        for(int u=0;u<EX.length;u++){
          if(ET[u]=="playerE"){
            NMOX=play.X-EX[u];
            NMOY=play.Y-EY[u];
          }
        }
        openMap("Test");
        play.MT();
        DOORSOP=false;
        break;
        case 2:
        TE[i]=false;
        for(int u=0;u<EX.length;u++){
          if(ES[u]==i){
            NewAI(EX[u],EY[u],ET[u],EM[u]);
            if(EM[u]==true){Must++;}
            for(int t=0;t<10;t++){
              AddPartic(3,EX[u],EY[u],random(-2,2),random(-2,2),100,color(155,0,155),true);
            }
          }
        }
        break;
        case 3:
        TE[i]=false;
        CBE=TE.clone();
        for(int u=0;u<EX.length;u++){
          if(ES[u]==i){
            PSX=EX[u];
            PSY=EY[u]-1;
          }
        }
        break;
        case 4:
        TE[i]=false;
        DOORSOP=true;
        break;
        case 5:
        AThurt(999999999);
        break;
        case 6:
        float vx=0;
        float vy=0;
        for(int u=0;u<ET.length;u++){
          if(ET[u].equals("playerC") && ES[u]==i){
            vx=(EX[u]-TX[i])/40;
            vy=(EY[u]-TY[i])/40;
            break;
          }
        }
        play.VX+=vx;
        play.VY+=vy;
        break;
        case 7:
        play.frezzing=true;
        break;
        case 8:
        play.water=true;
        break;
      }
    }
  }
  }
  if(PMust!=Must && PMust!=0){
    DOORSOP=false;
  }
}

public void Atrigger(){
  for(int i=0;i<TX.length;i++){
    for(int u=0;u<ListAi.size();u++){
      AI tmp = ListAi.get(u);
      if(tmp.X>TX[i] && tmp.X<TX[i]+TW[i] && tmp.Y>TY[i] && tmp.Y<TY[i]+TH[i] && TE[i]){
        switch(TT[i]){
          case 0:
          ListAi.get(u).HURT(25);
          break;
          case 5:
          ListAi.get(u).HURT(999999999);
          break;
          case 6:
          float vx=0;
          float vy=0;
          for(int o=0;o<ET.length;o++){
            if(ET[o].equals("playerC") && ES[o]==i){
              vx=(EX[o]-TX[i])/40;
              vy=(EY[o]-TY[i])/40;
              break;
            }
          }
          ListAi.get(u).VX+=vx;
          ListAi.get(u).VY+=vy;
          break;
        }
      }
    }
  }
}
public int BgetI(byte[] DATA,int index,int size){
  String num="";
  int Num=0;
  for(int i=size-1;i>-1;i--){
    num+=binary(DATA[i+index]);
  }
  Num=0;
  if(num.charAt(0)=='1'){
    Num=(int)pow(-2,(size*8)-1);
  }
  for(int i=1;i<num.length();i++){
    if(num.charAt(i)=='1'){
      Num+=(int)pow(2,(size*8)-i-1);
    }
  }
  return Num;
}

public byte[] BsetI(int num,int size){
  String Num = binary(num);
  byte[] DATA = new byte[size];
  for(int i=0;i<size;i++){
    String subData="";
    for(int u=0;u<8;u++){
      subData+=Num.charAt(i*8+u+(4-size)*8);
    }
    DATA[i]=PApplet.parseByte(unbinary(subData));
  }
  return DATA;
}

public String BgetS(byte[] DATA,int index,int size){
  String num="";
  for(int i=0;i<size;i++){
    num+=PApplet.parseChar(DATA[i+index]);
  }
  return num;
}

boolean DOORSOP=false;

//OPEN>><<CLOSE

door[] MAD;

public void doorM(){
  for(int i=0;i<MAD.length;i++){
    MAD[i].Math();
  }
}

class door{
  float SX;
  float SY;
  float EX;
  float EY;
  float OCSX;
  float OCSY;
  float OCEX;
  float OCEY;
  float OPX;
  float OPY;
  int Cool;
  int Prop;
  int timer;
  int Mimer;
  door(float nSX,float nSY,float nEX,float nEY,int nMimer,int nCool,int nProp){
    SX=nSX;
    SY=nSY;
    EX=nEX;
    EY=nEY;
    Cool=nCool;
    Prop=nProp;
    timer=nMimer;
    Mimer=nMimer;
    OCSX=SX-CSX[Cool];
    OCSY=SY-CSY[Cool];
    OCEX=SX-CEX[Cool];
    OCEY=SY-CEY[Cool];
    OPX=SX-PROPL[Prop].X;
    OPY=SY-PROPL[Prop].Y;
  }
  public void Math(){
    if(DOORSOP){
      if(timer>0){
        timer--;
      }
    }else{
      if(timer<Mimer){
        timer++;
      }
    }
    CSX[Cool]=lerp(EX,SX,PApplet.parseFloat(timer)/Mimer)-OCSX;
    CSY[Cool]=lerp(EY,SY,PApplet.parseFloat(timer)/Mimer)-OCSY;
    CEX[Cool]=lerp(EX,SX,PApplet.parseFloat(timer)/Mimer)-OCEX;
    CEY[Cool]=lerp(EY,SY,PApplet.parseFloat(timer)/Mimer)-OCEY;
    PROPL[Prop].X=lerp(EX,SX,PApplet.parseFloat(timer)/Mimer)-OPX;
    PROPL[Prop].Y=lerp(EY,SY,PApplet.parseFloat(timer)/Mimer)-OPY;
    updBx(Cool);
  }
  
}

public PShape loadPly(String filepath){
  PShape obj;
  String[] info = loadStrings(filepath);
  int header=0;
  int vertexCount=0;
  int faceCount=0;
  PVector[] vertexs = new PVector[0];
  PVector[] normal = new PVector[0];
  int[] colors = new int[0];
  while(!(info[header].equals("end_header"))){
    String[] arg = split(info[header],' ');
    if(arg[0].equals("element")){
      if(arg[1].equals("vertex")){
        vertexCount = PApplet.parseInt(arg[2]);
      }
      if(arg[1].equals("face")){
        faceCount = PApplet.parseInt(arg[2]);
      }
    }
    header++;
  }
  header++;
  for(int i=0;i<vertexCount;i++){
    String[] arg = split(info[header],' ');
    vertexs = (PVector[])append(vertexs,new PVector(PApplet.parseFloat(arg[0]),PApplet.parseFloat(arg[1]),PApplet.parseFloat(arg[2])));
    normal  = (PVector[])append(normal ,new PVector(PApplet.parseFloat(arg[3]),PApplet.parseFloat(arg[4]),PApplet.parseFloat(arg[5])));
    colors  = (int[]  )append(colors ,    color  (PApplet.parseFloat(arg[6]),PApplet.parseFloat(arg[7]),PApplet.parseFloat(arg[8])));
    header++;
  }
  obj = createShape(GROUP);
  for(int i=0;i<faceCount;i++){
    PShape tmp = createShape();
    String[] arg = split(info[header],' ');
    tmp.beginShape();
    tmp.noStroke();
    for(int u=0;u<PApplet.parseInt(arg[0]);u++){
      PVector V=vertexs[PApplet.parseInt(arg[u+1])];
      PVector N=normal[PApplet.parseInt(arg[u+1])];
      tmp.fill(colors[PApplet.parseInt(arg[u+1])]);
      tmp.normal(N.x,N.y,N.z);
      tmp.vertex(V.x,V.y,V.z);
    }
    tmp.endShape(CLOSE);
    obj.addChild(tmp);
    header++;
  }
  return obj;
}
public void Menu() {
  MATHUI();
  textAlign(LEFT, TOP);
}

public void MenuSetup() {
  menuUI = new UI[0];

  //PAUSE_MENU

  menuUI = (UI[])append(menuUI, new Text      (20, 50, 100, 40, "PAUSE_MENU", "nothing", "PAUSED"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, height-110-50, 100, 40, "PAUSE_MENU", "restart", "restart"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, height-110, 100, 40, "PAUSE_MENU", "GotoOptions", "options"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, height-110+50, 100, 40, "PAUSE_MENU", "Exit", "Exit the thing"));
  menuUI = (UI[])append(menuUI, new Slot      (width-210, 50, 200, 20, "PAUSE_MENU", 0));
  menuUI = (UI[])append(menuUI, new Slot      (width-210, 70, 200, 20, "PAUSE_MENU", 1));
  menuUI = (UI[])append(menuUI, new Slot      (width-210, 90, 200, 20, "PAUSE_MENU", 2));
  menuUI = (UI[])append(menuUI, new Text      (width-310, 50, 100, 60, "PAUSE_MENU", "nothing", "testing this things"));

  //MAIN_MENU

  menuUI = (UI[])append(menuUI, new ButtonText(20, height-160, 100, 40, "MAIN_MENU", "RunGame", "go to the arena"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, height-160-50, 100, 40, "MAIN_MENU", "GotoTutorial", "tutorial"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, height-160+50, 100, 40, "MAIN_MENU", "GotoOptions", "options"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, height-160+100, 100, 40, "MAIN_MENU", "Quit", "Quit"));
  menuUI = (UI[])append(menuUI, new ButtonText(140, height-160+100, 100, 40, "MAIN_MENU", "UPDATE", "Update"));
  
  //saves//
  
  menuUI = (UI[])append(menuUI, new SaveButton(140, height-160, 100, 40, "MAIN_MENU", "nothing" , 10));
  menuUI = (UI[])append(menuUI, new SaveButton(260, height-160, 100, 40, "MAIN_MENU", "nothing" , 20));
  
  //OPTIONS_MENU

  menuUI = (UI[])append(menuUI, new ButtonText(20, height-110+50, 100, 40, "OPTIONS_MENU", "GotoMain", "back"));
  menuUI = (UI[])append(menuUI, new Text      (20, 50, 100, 40, "OPTIONS_MENU", "nothing", "this is the options menu"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, 90, 100, 40, "OPTIONS_MENU", "GotoBinds", "KeyBinds"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, 130, 100, 40, "OPTIONS_MENU", "GotoNots", "Visuals"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, 170, 100, 40, "OPTIONS_MENU", "Save_config", "save"));
  
  //Binds

  menuUI = (UI[])append(menuUI, new BindButton(140, 50, "Binds", "Player_Move_Up", "jump"));
  menuUI = (UI[])append(menuUI, new BindButton(140, 90, "Binds", "Player_Move_Down", ""));
  menuUI = (UI[])append(menuUI, new BindButton(140, 130, "Binds", "Player_Move_Left", "slide to the left"));
  menuUI = (UI[])append(menuUI, new BindButton(140, 170, "Binds", "Player_Move_Right", "slide to the right"));
  menuUI = (UI[])append(menuUI, new BindButton(140, 210, "Binds", "Player_Restart", "quick restart"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 50, "Binds", "Player_Boost", "special"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 90, "Binds", "Weapon_1", "slot garant"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 130, "Binds", "Weapon_2", "slot rocket"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 170, "Binds", "Weapon_3", "slot railgun"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 210, "Binds", "Weapon_4", "slot shotgun"));
  
  //Nots

  menuUI = (UI[])append(menuUI, new ButtonToggle(140, 50, 400, 40, "Nots", "DrawEffects", "DrawEffects : Only important Effect will display"));
  menuUI = (UI[])append(menuUI, new ButtonSlider(140, 90, 400, 40, "Nots", "GuiScale", "GuiScale : Scale of the Hud Rings", 50, 500));
  menuUI = (UI[])append(menuUI, new ButtonToggle(140, 130, 400, 40, "Nots", "ReversedWhell", "ReversedWhell : reverse the weapon whell seletion"));
  menuUI = (UI[])append(menuUI, new ButtonSlider(140, 170, 400, 40, "Nots", "HurtScale", "HurtScale : scale the \"vinete\" that apears", 1, 50));
  menuUI = (UI[])append(menuUI, new ButtonToggle(140, 210, 400, 40, "Nots", "ShouldPause", "ShouldPause : autopause if unfocused"));
  menuUI = (UI[])append(menuUI, new ButtonToggle(140, 250, 400, 40, "Nots", "SimpleExplosion", "SimpleExplosion : makes explosions simpler goodFps boost for them"));
  //menuUI = (UI[])append(menuUI, new ButtonToggle(140,290, 400, 40, "Nots", "Fullscreen", "Fullscreen : Fullscreen"));
  menuUI = (UI[])append(menuUI, new ButtonSlider(140, 290, 400, 40, "Nots", "Zoom", "Zoom : How much zoom", 10, 200));

  MenuTurnOn("MAIN_MENU");
}

public void GotoTutorial() {
  Start("tutorial");
  toturialTimer = 0;
  toturialMode = true;
}

public void nothing() {/*!nothing!*/
}

public void restart() {
  play.MT();
  if (tantactive) {
    ResartWave();
  }
}

public void MATHUI() {
  for (int i=0; i<menuUI.length; i++) {
    if (menuUI[i].Enable) {
      menuUI[i].Draw(i);
    }
  }
  if (mousePressed) {
    for (int i=0; i<menuUI.length; i++) {
      if (menuUI[i].Enable
        &&mouseX>menuUI[i].x
        &&mouseX<menuUI[i].x+menuUI[i].w
        &&mouseY>menuUI[i].y
        &&mouseY<menuUI[i].y+menuUI[i].h) {
        menuUI[i].CFunc(i);
      }
    }
  }
  MenuSwap=false;
}

int submenu=0;
boolean MenuSwap=false;//i exist to destroy the jank

UI[] menuUI;

public void RunGame() {
  //Start("AItest");
  tantactive=true;
  tantrest();
  nextWave();
  MenuPaused=false;
}

public void GotoOptions() {
  MenuTurnOffAll();
  MenuTurnOn("OPTIONS_MENU");
  MenuTurnOn("Binds");
  MenuSwap=true;
}

public void Save_config() {
  JSONObject tmp = loadJSONObject("Misc/config.json");
  JSONObject out = new JSONObject();
  out.setBoolean("DebugStart", tmp.getBoolean("DebugStart"));
  out.setString("DebugMap", tmp.getString("DebugMap"));
  out.setBoolean("DebugTant", tmp.getBoolean("DebugTant"));
  JSONArray binds = new JSONArray();
  for (int u=0; u<Keybinds.length; u++) {
    JSONObject bind = new JSONObject();
    bind.setString("equa", KeyRedirect.key(u));
    bind.setString("key", str(Keybinds[u].pri));
    bind.setBoolean("iscode", Keybinds[u].iscode);
    bind.setInt("code", Keybinds[u].code);
    binds.append(bind);
  }
  out.setJSONArray("Binds", binds);
  for (int u=0; u<Configs.size(); u++) {
    out.setInt(Configs.key(u), Configs.get(Configs.key(u)));
  }
  saveJSONObject(out, "data/Misc/config.json");
  println("test");
}

public void Quit() {
  exit();
}

public void Exit() {
  Gaming=false;
  MenuTurnOffAll();
  MenuTurnOn("MAIN_MENU");
  MenuSwap=true;
}

public void GotoMain() {
  if (Gaming) {
    MenuTurnOffAll();
    MenuTurnOn("PAUSE_MENU");
    MenuSwap=true;
  } else {
    MenuTurnOffAll();
    MenuTurnOn("MAIN_MENU");
    MenuSwap=true;
  }
}

public void GotoBinds() {
  MenuTurnOff("Nots");
  MenuTurnOn("Binds");
  MenuSwap=true;
}

public void GotoNots() {
  MenuTurnOff("Binds");
  MenuTurnOn("Nots");
  MenuSwap=true;
}

boolean WaitingUser=false;
int Waiter=0;
int ToWait=0;

//find better solucion
String[] slot1 = {"ESCAPE", "JET"};
String[] desc1 = {"updash", "constant up movement"};
String[] slot2 = {"DASH", "NO_U"};
String[] desc2 = {"dash", "null all movment + 30f of nodie"};
String[] slot3 = {"EXTRACT", "SHARPNEL"};
String[] desc3 = {"hurt to heal", "50% of frag to drop +5HP"};

class SaveButton extends UI {
  String Text;
  int save;
  SaveButton(float nx, float ny, float nw, float nh, String nCall,String nRun,int save) {
    x=nx;
    y=ny;
    w=nw;
    h=nh;
    Call=nCall;
    Run=nRun;
    this.save=save;
  }
  public void Draw(int i) {
    if(CurrentSave<save){return;}
    textAlign(CENTER, CENTER);
    stroke(0xff676767);
    fill(0xff404040);
    rect(x, y, w, h);
    fill(0xffBFBFBF);
    text("\"level\" "+(save), x+w/2, y+h/2);
  }
  public void CFunc(int i) {
    if(CurrentSave<save){return;}
    tantactive=true;
    tantrest();
    round=save-1;
    nextWave();
    MenuPaused=false;
  }
}


class Slot extends UI {
  int which=0;
  Slot(float nx, float ny, float nw, float nh, String nCall, int nwhich) {
    x=nx;
    y=ny;
    w=nw;
    h=nh;
    Call=nCall;
    which=nwhich;
  }
  public void Draw(int i) {
    textAlign(CENTER, CENTER);
    stroke(0xff676767);
    fill(0xff808080);
    rect(x, y, w, h);
    rect(x, y, 50, h);
    rect(x+w-50, y, 50, h);
    line(x+50, y, x, y+h/2);
    line(x+50, y+h, x, y+h/2);
    line(x+w-50, y, x+w, y+h/2);
    line(x+w-50, y+h, x+w, y+h/2);
    fill(0xff000000);
    if (which==0) {
      text(slot1[play.vertical], x+w/2, y+h/2);
    }
    if (which==1) {
      text(slot2[play.mobilaty], x+w/2, y+h/2);
    }
    if (which==2) {
      text(slot3[play.regenera], x+w/2, y+h/2);
    }
    if (mouseX>x+50 && mouseX<x+w-50 && mouseY>y && mouseY<y+h) {
      fill(0xff808080);
      rect(x+50, y, w-100, h);
      fill(0xff000000);
      if (which==0) {
        text(desc1[play.vertical], x+w/2, y+h/2);
      }
      if (which==1) {
        text(desc2[play.mobilaty], x+w/2, y+h/2);
      }
      if (which==2) {
        text(desc3[play.regenera], x+w/2, y+h/2);
      }
    }
  }
  public void CFunc(int i) {
    int u=0;
    if (mouseX<x+50) {
      u--;
    }
    if (mouseX>x+w-50) {
      u++;
    }
    if (which==0 && ((play.vertical>0 && u<0) || (play.vertical<slot1.length-1 && u>0))) {
      play.vertical+=u;
    }
    if (which==1 && ((play.mobilaty>0 && u<0) || (play.mobilaty<slot2.length-1 && u>0))) {
      play.mobilaty+=u;
    }
    if (which==2 && ((play.regenera>0 && u<0) || (play.regenera<slot2.length-1 && u>0))) {
      play.regenera+=u;
    }
  }
}

class ButtonSlider extends UI {
  String Text;
  String Var;
  int min;
  int max;
  ButtonSlider(float nx, float ny, float nw, float nh, String nCall, String nVar, String nText, int Min, int Max) {
    x=nx;
    y=ny;
    w=nw;
    h=nh;
    Call=nCall;
    Var=nVar;
    Text=nText;
    min=Min;
    max=Max;
  }
  public void Draw(int i) {
    textAlign(CENTER, CENTER);
    stroke(0xff676767);
    fill(0xff808080);
    rect(x, y, w, h);
    fill(0xff404040);
    rect(x, y, map(Configs.get(Var), min, max, 0, w), h);
    fill(0xffBFBFBF);
    text(Text, x+w/2, y+h/2-5);
    text(Configs.get(Var), x+w/2, y+h/2+5);
  }
  public void CFunc(int i) {
    Configs.set(Var, constrain((int)map(mouseX, x+5, x+w-5, min, max), min, max));
  }
}

class ButtonToggle extends UI {
  String Text;
  String Var;
  ButtonToggle(float nx, float ny, float nw, float nh, String nCall, String nVar, String nText) {
    x=nx;
    y=ny;
    w=nw;
    h=nh;
    Call=nCall;
    Var=nVar;
    Text=nText;
  }
  public void Draw(int i) {
    textAlign(CENTER, CENTER);
    stroke(0xff676767);
    if (Configs.get(Var)==1) {
      fill(0xff004000);
    } else {
      fill(0xff400000);
    }
    rect(x, y, w, h);
    fill(0xffBFBFBF);
    text(Text, x+w/2, y+h/2);
  }
  public void Func(int i) {
    Configs.set(Var, abs(Configs.get(Var)-1));
  }
}

class BindButton extends UI {
  String Text;
  String Code;
  int Connected;
  boolean Waiting;
  BindButton(float nx, float ny, String nCall, String nCode, String nText) {
    x=nx;
    y=ny;
    w=150;
    h=40;
    Call=nCall;
    Text=nText;
    Code = nCode;
    Connected = KeyRedirect.get(Code);
  }
  public void Draw(int i) {
    textAlign(LEFT, TOP);
    stroke(0xff676767);
    if (!(WaitingUser && Waiter == i)) {
      fill(0xff404040);
    } else {
      fill(0xff808080);
    }
    rect(x, y, w, h);
    fill(0xffBFBFBF);
    text(Text, x+2, y+2);
    if (Text.isEmpty()) {
      text(Code, x+2, y+2);
    }
    if (Keybinds[Connected].iscode) {
      text(returnTEXE(Keybinds[Connected].code), x+2, y+22);
    } else {
      text(Keybinds[Connected].pri, x+2, y+22);
    }
  }
  public void Func(int i) {
    WaitingUser=true;
    Waiter=i;
    ToWait=Connected;
  }
}

class Text extends UI {
  String Text;
  Text(float nx, float ny, float nw, float nh, String nCall, String nRun, String nText) {
    x=nx;
    y=ny;
    w=nw;
    h=nh;
    Call=nCall;
    Run=nRun;
    Text=nText;
  }
  public void Draw(int i) {
    textAlign(LEFT, TOP);
    stroke(0xff676767);
    fill(0xff000000);
    rect(x, y, w, h);
    fill(0xffBFBFBF);
    text(Text, x+2, y+2, w-4, 200);
  }
}

class ButtonText extends UI {
  String Text;
  ButtonText(float nx, float ny, float nw, float nh, String nCall, String nRun, String nText) {
    x=nx;
    y=ny;
    w=nw;
    h=nh;
    Call=nCall;
    Run=nRun;
    Text=nText;
  }
  public void Draw(int i) {
    textAlign(CENTER, CENTER);
    stroke(0xff676767);
    fill(0xff404040);
    rect(x, y, w, h);
    fill(0xffBFBFBF);
    text(Text, x+w/2, y+h/2);
  }
}

class UI {
  float x;
  float y;
  float w;
  float h;
  String Call;
  String Run;
  boolean Enable;
  public void turnOff(String Voice) {
    if (Voice.equals(Call)) {
      Enable=false;
    }
  }
  public void turnON(String Voice) {
    if (Voice.equals(Call)) {
      Enable=true;
    }
  }
  public void turnOffBut(String Voice) {
    if (!Voice.equals(Call)) {
      Enable=false;
    }
  }
  public void turnONBut(String Voice) {
    if (!Voice.equals(Call)) {
      Enable=true;
    }
  }
  public void Func(int i) {
    if (MenuSwap) {
      return;
    }
    try {
      method(Run);
    }
    catch(Exception e) {
      PrintCon("sorry for that");
      PrintCon(e.getMessage());
      ErrorTimer=120;
    }
  }
  public void CFunc(int i) {
    //much
  }
  public void Draw(int i) {
    //wow
  }
}

public void MenuTurnOn(String str) {
  for (int i=0; i<menuUI.length; i++) {
    menuUI[i].turnON(str);
  }
}

public void MenuTurnOff(String str) {
  for (int i=0; i<menuUI.length; i++) {
    menuUI[i].turnOff(str);
  }
}

public void MenuTurnOnAll() {//dont ever fucking use!
  for (int i=0; i<menuUI.length; i++) {
    menuUI[i].Enable=true;
  }
}

public void MenuTurnOffAll() {//turn Off
  for (int i=0; i<menuUI.length; i++) {
    menuUI[i].Enable=false;
  }
}

public void MenuTurnOnAllBut(String str) {//also dont use!
  for (int i=0; i<menuUI.length; i++) {
    menuUI[i].turnONBut(str);
  }
}

public void MenuTurnOffAllBut(String str) {//turn Off all but str
  for (int i=0; i<menuUI.length; i++) {
    menuUI[i].turnOffBut(str);
  }
}

public void mousePressed() {
  if (!Gaming || MenuPaused) {
    for (int i=0; i<menuUI.length; i++) {
      if (menuUI[i].Enable) {//ouch
        if (mouseX>menuUI[i].x
          &&mouseX<menuUI[i].x+menuUI[i].w
          &&mouseY>menuUI[i].y
          &&mouseY<menuUI[i].y+menuUI[i].h) {
          menuUI[i].Func(i);
        }
      }
    }
  }
}
ArrayList<PRO> ListPR;
IntList killPR;

public void ProjMath() {
  for (int i=0; i<ListPR.size(); i++) {
    ListPR.get(i).math(i);
  }
  killPR.reverse();
  for (int i=0; i<killPR.size(); i++) {
    ListPR.remove(killPR.get(i));
  }
  killPR.clear();
}

public void PRR () {
  for (int i=0; i<ListPR.size(); i++) {
    ListPR.get(i).render();
  }
}

class Spit extends PRO {
  Spit(float nX, float nY, float nVX, float nVY, int nT) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    T = nT;
    W=6;
    H=6;
  }
  public void math(int SID) {
    if (Coll(X-W, Y-W, X+W, Y+W)) {
      killPR.append(SID);
      return;
    }
    if (Coll(X+H, Y-H, X-H, Y+H)) {
      killPR.append(SID);
      return;
    }
    if (Coll(X, Y, X+VX, Y+VY)) {
      killPR.append(SID);
      return;
    }
    if (X>play.X-6 && X<play.X+6 && Y<play.Y && Y>play.Y-24) {
      AThurt(16);
      killPR.append(SID);
      return;
    }
    timer++;
    if (timer==proANIM[0].delay) {
      timer=0;
      cframe++;
    }
    if (cframe==proANIM[0].Max()) {
      cframe=0;
    }
    X+=VX;
    Y+=VY;
    if (random(0, 1)<0.2f) {
      AddPartic(4, X, Y, 0, 0, 40, color(0xff548454), false);
    }
  }
  public void render() {
    proANIM[1].ANR(X, Y, cframe);
  }
}

class Gran extends PRO {
  float Bombtimer=30;
  Gran(float nX, float nY, float nVX, float nVY, int nT) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    T = nT;
    W=6;
    H=6;
    timer=0;
    cframe=0;
  }
  public void math(int SID) {
    if (Coll(X-W, Y-W, X+W, Y+W)) {
      killPR.append(SID);
      expd(X, Y, 180, 15, 10, false);
      return;
    }
    if (Coll(X+H, Y-H, X-H, Y+H)) {
      killPR.append(SID);
      expd(X, Y, 180, 15, 10, false);
      return;
    }
    if (Coll(X, Y, X+VX, Y+VY)) {
      killPR.append(SID);
      expd(X, Y, 180, 15, 10, false);
      return;
    }
    if (Bombtimer==0) {
      killPR.append(SID);
      expd(X, Y, 180, 15, 10, false);
      return;
    }
    if (EnCo(X, Y, W, H, 0)) {
      killPR.append(SID);
      expd(X, Y, 180, 15, 10, false);
      return;
    }
    timer++;
    if (timer==proANIM[1].delay) {
      timer=0;
      cframe++;
    }
    if (cframe==proANIM[1].Max()) {
      cframe=0;
    }
    X+=VX;
    Y+=VY;
    Bombtimer--;
    //VY+=0.2;
  }
  public void render() {
    proANIM[0].ANR(X, Y, cframe);
  }
}

class Sharp extends PRO {
  Sharp(float nX, float nY, float nVX, float nVY, int nT) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    T = nT;
    W=6;
    H=6;
  }
  public void math(int SID) {
    if (Coll(X-W, Y-W, X+W, Y+W)) {
      killPR.append(SID);
      return;
    }
    if (Coll(X+H, Y-H, X-H, Y+H)) {
      killPR.append(SID);
      return;
    }
    if (Coll(X, Y, X+VX, Y+VY)) {
      killPR.append(SID);
      return;
    }
    if (EnCo(X, Y, W, H, 15)) {
      killPR.append(SID);
      return;
    }
    X+=VX;
    Y+=VY;
    VY+=0.2f;
    AddPartic(3, X, Y, 0, 0, 8, color(0xffAAAA00), false);
  }
  public void render() {
    stroke(0xffFFFF00);  
    fill(0xffFFFF00);
    triangle(X-3, Y+3, X, Y-3, X+3, Y+3);
  }
}

class MazeBullets extends PRO {
  MazeBullets(float nX, float nY, float nVX, float nVY, int nT) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    T = nT;
    W=8;
    H=8;
  }
  public void math(int SID) {
    if (Coll(X-W, Y-W, X+W, Y+W)) {
      killPR.append(SID);
      return;
    }
    if (Coll(X+H, Y-H, X-H, Y+H)) {
      killPR.append(SID);
      return;
    }
    if (Coll(X, Y, X+VX, Y+VY)) {
      killPR.append(SID);
      return;
    }
    if (Cont(W, H, 16)) {
      killPR.append(SID);
      return;
    }
    X+=VX;
    Y+=VY;
  }
  public void render() {
    fill(0xffFFFFFF);
    stroke(0xff00FFFF);
    circle(X, Y, 20);
    rect(X-W, Y-H, W*2, H*2);
  }
}

class NapalmFire extends PRO {
  NapalmFire(float nX, float nY, float nVX, float nVY, int nT) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    T = nT;
    W=8;
    H=8;
  }
  boolean Stuck=false;
  public void math(int SID) {
    if (!Stuck) {
      if (Coll(X-W, Y-W, X+W, Y+W)) {
        Stuck=true;
      }
      if (Coll(X+H, Y-H, X-H, Y+H)) {
        Stuck=true;
      }
      if (Coll(X, Y, X+VX, Y+VY)) {
        Stuck=true;
      }
    }
    if (Cont(W, H, 16)) {
    }
    if (EnCo(X, Y, W, H, 1)) {
    }
    if (!Stuck) {
      VY+=0.2f;
      X+=VX;
      Y+=VY;
    }
  }
  public void render() {
    noStroke();
    fill(0xffFF0000);
    circle(X, Y, 20);
    fill(0xffFF7C00);
    circle(X, Y, 10);
    //rect(X-W,Y-H,W*2,H*2);
  }
}

class SpirtShit extends PRO {
  int Bombtimer=60;
  SpirtShit(float nX, float nY, float nVX, float nVY, int nT) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    T = nT;
    W=6;
    H=6;
    timer=0;
    cframe=0;
  }
  public void math(int SID) {
    if (Coll(X-W, Y-W, X+W, Y+W)) {
      killPR.append(SID);
      return;
    }
    if (Coll(X+H, Y-H, X-H, Y+H)) {
      killPR.append(SID);
      return;
    }
    if (Coll(X, Y, X+VX, Y+VY)) {
      killPR.append(SID);
      return;
    }
    if (Cont(W, H, 16) & Bombtimer>20) {
      killPR.append(SID);
      return;
    }
    if (Bombtimer==0) {
      killPR.append(SID);
      return;
    }
    Bombtimer--;
    W=Bombtimer/4;
    H=Bombtimer/4;
    float R=atan2(Y-play.Y, X-play.X);
    VX-=cos(R)/10;
    VY-=sin(R)/10;
    X+=VX;
    Y+=VY;
    //VY+=0.2;
  }
  public void render() {
    noStroke();
    fill(0xff00FFFF);
    circle(X, Y, Bombtimer/2);
    fill(0xff00AAAA);
    circle(X, Y, Bombtimer/4);
  }
}

class Earth extends PRO {
  Earth(float nX, float nY, float nVX, float nVY, int nT) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    T = nT;
    W=2;
    H=2;
    timer=0;
    cframe=0;
  }
  int bombtimer=90;
  public void math(int SID) {
    if (bombtimer%5==0) {
      expd(X, Y-60,120, 16, 0, true);
    }
    if (bombtimer==0) {
      killPR.append(SID);
      return;
    }
    bombtimer--;
    X+=VX;
    Y+=VY;
    //VY+=0.2;
  }
  public void render() {
    if (DebugDraw) {
      noStroke();
      fill(255);
      rect(X-W, Y-H, W*2, H*2);
    }
  }
}

class Air extends PRO {
  Air(float nX, float nY, float nVX, float nVY, int nT) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    T = nT;
    W=6;
    H=6;
    timer=0;
    cframe=0;
  }
  float rotate=0;
  int fuel=30;
  public void math(int SID) {
    if (Coll(X-W, Y-W, X+W, Y+W)) {
      killPR.append(SID);
      return;
    }
    if (Coll(X+H, Y-H, X-H, Y+H)) {
      killPR.append(SID);
      return;
    }
    if (Coll(X, Y, X+VX, Y+VY)) {
      killPR.append(SID);
      return;
    }
    if (Cont(W, H, 16)) {
      killPR.append(SID);
      return;
    }
    if(fuel<=0 && fuel>-60){
    rotate = atan2(play.Y-Y, play.X-X);
    VX+=cos(rotate)*1;
    VY+=sin(rotate)*1;
    }
    AddPartic(1, X, Y, X+VX, Y+VY, 10, color(255), false);
    fuel--;
    X+=VX;
    Y+=VY;
    //VY+=0.2;
  }
  public void render() {
    noStroke();
    fill(255);
    rect(X-W, Y-H, W*2, H*2);
  }
}

class Fire extends PRO {
  Fire(float nX, float nY, float nVX, float nVY, int nT) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    T = nT;
    W=32;
    H=32;
    timer=0;
    cframe=0;
  }
  float fuel=280;
  float rotate=0;
  public void math(int SID) {
    Cont(W, H, 12);
    rotate = atan2(play.Y-Y, play.X-X);
    AddPartic(3, X+random(-W/2, W/2), Y+random(-H/2, H/2), 0, -3, 40, color(0xffFF0000), false);
    if(fuel<240){
    X+=cos(rotate)*3;
    Y+=sin(rotate)*3;
    }
    fuel--;
    if (fuel==0) {
      killPR.append(SID);
      return;
    }
    //VY+=0.2;
  }
  public void render() {
    if (DebugDraw) {
      noStroke();
      fill(255);
      rect(X-W, Y-H, W*2, H*2);
    }
    if (fuel>240) {
    fill(0xffFF0000,map(fuel,280,240,0,255));
    circle(X, Y, W*2.5f);
    fill(0xffFF8D00,map(fuel,280,240,0,255));
    circle(X, Y, W*1.5f);
    }else{
    fill(0xffFF0000, fuel/120*255);
    circle(X, Y, W*2.5f);
    fill(0xffFF8D00, fuel/120*255);
    circle(X, Y, W*1.5f);
    }
  }
}

class Water extends PRO {
  Water(float nX, float nY, float nVX, float nVY, int nT) {
    X = nX;
    Y = nY;
    rotate = nVX;
    VX = 0;
    VY = 0;
    T = nT;
    W=32;
    H=32;
    timer=0;
    cframe=0;
  }
  int fuel=240;
  float rotate=0;
  public void math(int SID) {
    Cont(W, H, 12);
    if (fuel<200) {
      X+=cos(rotate)*9;
      Y+=sin(rotate)*9;
    }
    fuel--;
    if (fuel==0) {
      killPR.append(SID);
      return;
    }
    //VY+=0.2;
  }
  public void render() {
    if (DebugDraw) {
      noStroke();
      fill(255);
      rect(X-W, Y-H, W*2, H*2);
    }
    fill(0xff00A8FF,map(fuel,240,200,0,255));
    circle(X, Y, W*2.5f);
  }
}

class Soul extends PRO {
  Soul(float nX, float nY, float nVX, float nVY, int nT) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    T = nT;
    W=8;
    H=8;
    timer=0;
    cframe=0;
  }
  int fuel=240;
  public void math(int SID) {
    if (dist(play.X,play.Y-12,X,Y)<120) {
      float R = atan2(Y-play.Y+12,X-play.X);
      VX-=cos(R)*9;
      VY-=sin(R)*9;
    }
    if (dist(play.X,play.Y-12,X,Y)<20) {
      play.HP+=5;
      killPR.append(SID);
      return;
    }
    fuel--;
    if (fuel==0) {
      killPR.append(SID);
      return;
    }
    X+=VX;
    Y+=VY;
    VX=VX*4/5;
    VY=VY*4/5;
  }
  public void render() {
    if (DebugDraw) {
      noStroke();
      fill(255);
      rect(X-W, Y-H, W*2, H*2);
    }
    fill(0xffFF0000,map(fuel,240,200,0,255));
    circle(X, Y,map(fuel,240,0,16,0));
  }
}

class PRO {
  float X;
  float Y;
  float VX;
  float VY;
  float W;
  float H;
  int cframe;
  int timer;
  int T;
  public boolean Cont(float W, float H, int dmg) {
    if (X+W>play.X-6 && X-W<play.X+6 && Y+H>play.Y-24 && Y-H<play.Y+0) {
      AThurt(dmg);
      return true;
    }
    return false;
  }
  public void math(int SID) {/*MATH GOES HERE*/
  }
  public void render() {
    switch(T) {
    case 0:
      break;
    case 1:
      break;
    case 2:
      break;
    case 3:
      break;
    }
  }
  public boolean EnCo(float X, float Y, float W, float H, int dmg) {
    for (int i=0; i<ListAi.size(); i++) {
      AI tmp = ListAi.get(i);
      if (X>tmp.X-tmp.W && X<tmp.X+tmp.W && Y>tmp.Y-tmp.H && Y<tmp.Y) {
        ListAi.get(i).HURT(dmg);
        return true;
      }
    }
    return false;
  }
}

public void expd(float x, float y, float r, int d, float f, boolean player) {
  AddPartic(5, x, y, r, 0, 25, color(255, 255, 0), true);
  r=r/2;
  int num = ListAi.size();
  if (!player) {
  for (int i=0; i<num; i++) {
    float X = ListAi.get(i).X;
    float Y = ListAi.get(i).Y-ListAi.get(i).H/2;
    if (dist(X, Y, x, y)<=r) {
      float R = atan2(Y-ListAi.get(i).H/2-y, X-x);
      float V = ListAi.get(i).Bresistance/100;
      ListAi.get(i).VX += cos(R)*f*V;
      ListAi.get(i).VY += sin(R)*f*V;
      ListAi.get(i).Y -= 0.1f;
      ListAi.get(i).HP -= d;
    }
  }
  }
  if (player) {
    if (dist(play.X, play.Y-12, x, y)<=r) {
      float R = atan2(play.Y-12-y, play.X-x);
      play.VX += cos(R)*f;
      play.VY += sin(R)*f;
      AThurt(d);
    }
  }
}

public void NewPR(float X, float Y, float VX, float VY, int T) {
  switch(T) {
  case 0:
    ListPR.add(new Spit(X, Y, VX, VY, T));
    break;
  case 1:
    ListPR.add(new Gran(X, Y, VX, VY, T));
    break;
  case 2:
    ListPR.add(new Sharp(X, Y, VX, VY, T));
    break;
  case 3:
    ListPR.add(new MazeBullets(X, Y, VX, VY, T));
    break;
  case 4:
    ListPR.add(new NapalmFire(X, Y, VX, VY, T));
    break;
  case 5:
    ListPR.add(new SpirtShit(X, Y, VX, VY, T));
    break;
  case 6:
    ListPR.add(new Earth(X, Y, VX, VY, T));
    break;
  case 7:
    ListPR.add(new Air(X, Y, VX, VY, T));
    break;
  case 8:
    ListPR.add(new Fire(X, Y, VX, VY, T));
    break;
  case 9:
    ListPR.add(new Water(X, Y, VX, VY, T));
    break;
  case 10:
    ListPR.add(new Soul(X, Y, VX, VY, T));
    break;
  }
}

public boolean Coll(float OX, float OY, float NX, float NY) {
  int[] list=CB(OX, OY, NX, NY);
  for (int p=0; p<list.length; p++) {//OH god this is terrible
    int i=list[p];
    float T=100;
    float t;
    float r;

    t=TLineToLine(OX, OY, NX, NY, CSX[i], CSY[i], CEX[i], CEY[i]);
    r=TLineToLine(CSX[i], CSY[i], CEX[i], CEY[i], OX, OY, NX, NY);
    if (t<=T && t>=0 && r<=1 && r>=0 && CT[i]!=1) {
      T=t;
    }
    if (T>=0 && T<=1) {
      return true;
    }
  }
  return false;
}
class PROP{
  float X;
  float Y;
  String T;
  int timer=0;
  int delay=0;
  int frame=0;
  PROP(float nX,float nY,String nT){
    X=nX;
    Y=nY;
    T=nT;
  }
  public void math(){
    timer++;
    if(timer==delay){
      timer=0;
      frame++;
    }
    if(frame==PROPR[Propredirect.get(T)].size()){
      frame=0;
    }
  }
  public void render(){
    PROPR[Propredirect.get(T)].ANR(X,Y,frame);
  }
}

IntDict Propredirect;

public void setupProps(){
  //File pointer = new File(sketchPath()+"/data/props");
  //String[] Props = pointer.list();
  //String[] Props = {"test","campfire","testdoor","wip"};//dran i need to find a better way for this
  //int[] G = new int[0];
  PROPR = new ANIPR[0];
  for(int i=0;i<PROPL.length;i++){
    PROP tmp = PROPL[i];
    boolean S=false;
    for(int u=0;u<Propredirect.size();u++){
      if(Propredirect.hasKey(tmp.T)){
        PROPL[i].delay = PROPR[u].delay;
        S=true;
        break;
      }
    }
    if(!S){
      Propredirect.set(tmp.T,PROPR.length);
      PROPR = (ANIPR[])append(PROPR,new ANIPR(tmp.T,0));
      PROPL[i].delay = PROPR[PROPR.length-1].delay;
    }
  }
}

public void propM(){
  for(int i=0;i<PROPL.length;i++){
    PROPL[i].math();
  }
}

public void propD(){
  for(int i=0;i<PROPL.length;i++){
    PROPL[i].render();
  }
}
PROP[] PROPL;
ANIPR[] PROPR;

class ANIPR{
  PImage[] Frames;
  int[] RFrames;
  int delay=0;
  int G;
  ANIPR(String file,int nG){
    G=nG;
    Frames = new PImage[0];
    RFrames = new int[0];
    int[] RLOAD = new int[0];
    byte[] DATA = loadBytes("props/"+file+"/file.SFF");
    int header=0;
    int N = BgetI(DATA,header,2);
    header+=2;
    String sub = BgetS(DATA,header,N);
    header+=N;
        N = BgetI(DATA,header,2);
    header+=2;
    for(int i=0;i<N;i++){
      RFrames = append(RFrames,BgetI(DATA,header,2));
      boolean IS=true;
      for(int u=0;u<RLOAD.length;u++){
        if(RLOAD[u]==BgetI(DATA,header,2)){
          IS=false;
        }
      }
      if(IS){
        RLOAD = append(RLOAD,BgetI(DATA,header,2));
      }
      header+=2;
    }
    for(int u=0;u<RLOAD.length;u++){
      Frames = (PImage[])append(Frames,SloadImage("props/"+file+"/"+(sub)+(u)+".png"));
      image(Frames[RFrames[u]],0,0);
    }
        delay = BgetI(DATA,header,2);
    header+=2;
  }
  
  public void ANR(float x,float y,int frame){
    image(Frames[RFrames[frame]],x-Frames[RFrames[frame]].width/2,y-Frames[RFrames[frame]].height);
  }
  
  public int width(int frame){
    return Frames[RFrames[frame]].width;
  }
  
  public int height(int frame){
    return Frames[RFrames[frame]].height;
  }
  
  public int size(){
    return RFrames.length;
  }
}
class Player{
  float X;
  float Y;
  float VX;
  float VY;
  float PVY;
  float PX;
  float PY;
  float PO;
  int HP=100;
  int HD=0;
  int HDdelay=0;
  boolean Noclip=false;
  boolean God=false;
  boolean notime=false;
  int IV=0;
  boolean Gr=false;
  boolean Ignore=false;
  int cooldownV = 0;
  int cooldownH = 0;
  boolean frezzing=false;
  boolean water=false;
  int coolmeter=0;
  byte vertical=0;
  byte mobilaty=0;
  byte regenera=0;
  Player(){
    X=PSX;
    Y=PSY;
    VX=0;
    VY=0;
    PO=0;
  }
  
  public void MT(){
    X=PSX+NMOX;
    Y=PSY+NMOY;
    PX=X;
    PY=Y;
    play.HP=100;
    play.HD=0;
    play.HDdelay=0;
    Restart();
  }
  
  public void drawM(){
    pushMatrix();
    scale(ZOOMER);
    stroke(0);
    fill(0,0,200);
    rect(width/2/ZOOMER-6,height/2/ZOOMER-24,12,24);
    line(width/2/ZOOMER,height/2/ZOOMER-8,width/2/ZOOMER+play.VX,height/2/ZOOMER+play.VY-8);
    popMatrix();
  }
  
  public void drawI(){
    float rescale=(float)Configs.get("GuiScale")/100;
    pushMatrix();
    scale(rescale);
    fill(0xff00FFEE,100);
    noStroke();
    arc(width/2*(1/rescale)-5,(height/2+24*rescale)*(1/rescale),10,10, -PI/2, (2*PI*cooldownV/80)-PI/2);
    arc(width/2*(1/rescale)+5,(height/2+24*rescale)*(1/rescale),10,10, -PI/2, (2*PI*cooldownH/80)-PI/2);
    noFill();
    stroke(0,255,0,100);
    strokeWeight(5);
    strokeCap(SQUARE);
    arc(width/2*(1/rescale),(height/2-12)*(1/rescale),36,36,PI/2,PI/2+PI*play.HP/100);
    noFill();
    stroke(200,100);
    strokeWeight(5);
    strokeCap(SQUARE);
    arc(width/2*(1/rescale),(height/2-12)*(1/rescale),36,36,PI/2+PI*(100-play.HD)/100,PI/2+PI);
    noFill();
    stroke(0xff75E8ED,150);
    strokeWeight(2);
    strokeCap(SQUARE);
    arc(width/2*(1/rescale),(height/2-12)*(1/rescale),36,36,PI/2,PI/2+PI*play.coolmeter/180);
    strokeWeight(1);
    strokeCap(ROUND);
    stroke(0);
    stroke(100,100);
    strokeWeight(5);
    strokeCap(SQUARE);
    arc(width/2*(1/rescale),(height/2-12)*(1/rescale),45,45,PI/2,PI/2+PI*IV/20);
    popMatrix();
    pushMatrix();
    scale(ZOOMER);
    stroke(100,0,0);
    strokeWeight(2);
    circle(width/2/ZOOMER,height/2/ZOOMER,400);
    strokeWeight(1);
    strokeCap(ROUND);
    popMatrix();
  }
  
  public void Force(float NVX,float NVY){
    VX += NVX;
    VY += NVY;
  }

  public void dash(int m){
    switch(mobilaty){
      case 0:
      if(cooldownH==0){
        if(Gr==false){
          VX=m*15;
          VY=-2;
        }else{
          VX=m*18;
          VY=0;
        }
        cooldownH=80;
        for(int i=0;i<5;i++){
          AddPartic(6,X+random(-6,6),Y+random(-24,0),VX/2,VY/2,10,0xffF2F2F2,false);
        }
      }
      break;
      case 1:
      if(cooldownH==0){
        if(Gr==false){
          VX=0;
          VY=0;
        }else{
          VX=0;
          VY=0;
        }
        cooldownH=80;
        IV=30;
        for(int i=0;i<15;i++){
          AddPartic(6,X,Y-12,random(-5,5),random(-5,5),30,0xffF2F2F2,false);
        }
      }
      break;
    }
  }

  public void updash(){
    switch(vertical){
      case 0:
      if(cooldownV==0){
        if(Gr==false){
          VX=0;
          VY=-15;
        }else{
          VX=0;
          VY=-20;
        }
        cooldownV=80;
        for(int i=0;i<5;i++){
          AddPartic(6,X+random(-6,6),Y+random(-24,0),VX/2,VY/2,10,0xffF2F2F2,false);
        }
      }
      break;
      case 1:
      if(cooldownV<80){
        VY=min(VY-0.8f,0);
        cooldownV+=2;
      }
      break;
    }
  }

  public void Phy(){
    PX=X;
    PY=Y;
    HP=min(HP,100-HD);
    if(!Noclip){
    if(Gr){VY=0.1f;}
    if(Gr==false){
      if(!water){
        if(VY<20){VY+=0.5f;}
        if(GetKeyBind("Player_Move_Down") && VY<20 && HP>0 && !ConsoleUP){VY+=0.15f;}
        if(GetKeyBind("Player_Move_Left") && VX>-7 && HP>0 && !ConsoleUP){VX-=0.6f;}
        if(GetKeyBind("Player_Move_Right") && VX<7 && HP>0 && !ConsoleUP){VX+=0.6f;}
        if(GetKeyBind("Player_Move_Up") && GetKeyBind("Player_Boost") && HP>0 && !ConsoleUP){updash();}
        if(GetKeyBind("Player_Move_Left") && GetKeyBind("Player_Boost") && HP>0 && !ConsoleUP){dash(-1);}
        if(GetKeyBind("Player_Move_Right") && GetKeyBind("Player_Boost") && HP>0 && !ConsoleUP){dash(1);}
      }else{
        if(HP<=0){VY=-0.5f;}
        if(!(GetKeyBind("Player_Move_Down") | GetKeyBind("Player_Move_Up"))){VY/=2;}
        if(!(GetKeyBind("Player_Move_Left") | GetKeyBind("Player_Move_Right"))){VX/=2;}
        if(GetKeyBind("Player_Move_Down") && VY<5 && HP>0 && !ConsoleUP){VY+=2.5f;}
        if(GetKeyBind("Player_Move_Up") && VY>-5  && HP>0 && !ConsoleUP){VY-=2.5f;}
        if(GetKeyBind("Player_Move_Left") && VX>-5  && HP>0 && !ConsoleUP){VX-=2.5f;}
        if(GetKeyBind("Player_Move_Right") && VX<5  && HP>0 && !ConsoleUP){VX+=2.5f;}
      }
    }else{
      if(PVY>=5){
        for(int i=0;i<5;i++){
          AddPartic(2,X,Y,random(-5,5)+VX/2,-random(5,PVY)/3,15,color(100),false);
        }
      }
      if(GetKeyBind("Player_Move_Up") && HP>0 && !ConsoleUP){VY=-12;}
      if(!GetKeyBind("Player_Move_Right") && !GetKeyBind("Player_Move_Left")){VX/=2;}
      if(GetKeyBind("Player_Move_Left") && VX>-7 && HP>0 && !ConsoleUP){VX-=0.7f;}
      if(GetKeyBind("Player_Move_Right") && VX< 7 && HP>0 && !ConsoleUP){VX+=0.7f;}
      if(GetKeyBind("Player_Move_Up") && GetKeyBind("Player_Boost") && HP>0 && !ConsoleUP){updash();}
      if(GetKeyBind("Player_Move_Left") && GetKeyBind("Player_Boost") && HP>0 && !ConsoleUP){dash(-1);}
      if(GetKeyBind("Player_Move_Right") && GetKeyBind("Player_Boost") && HP>0 && !ConsoleUP){dash(1);}
      if(GetKeyBind("Player_Move_Down") && HP>0 && !ConsoleUP){Ignore=true;}
    }
    PVY=VY;
    if(Gr){cooldownV=0;}
    Gr=false;
    if((GetKeyBind("Player_Restart") && HP<=0 && !ConsoleUP)| X>10000 | X<-10000 | Y>10000 | Y<-10000){
      if(X>10000 | X<-10000 | Y>10000 | Y<-10000){
        ErrorTimer=120;
      }
      MT();
      VX=0;
      VY=0;
      BOSSID.clear();
      BOSSHP.clear();
      if(tantactive){
        ResartWave();
      }
    }
    if(cooldownH>0){cooldownH--;}
    if(!frezzing){
      if(coolmeter>0){
        coolmeter--;
      }
    }else{
      if(coolmeter<180){
        coolmeter++;
      }else{
        AThurt(15);
      }
    }
    frezzing=false;
    water=false;
    if(Ignore){Checkfor();}
    coll(X-6,Y   ,X-6+VX+0.01f,Y+VY+0.01f);
    coll(X+6,Y   ,X+6+VX+0.01f,Y+VY+0.01f);
    coll(X-6,Y-24,X-6+VX+0.01f,Y-24+VY+0.01f);
    coll(X+6,Y-24,X+6+VX+0.01f,Y-24+VY+0.01f);
    if(cill(-6+X+VX,Y+VY- 0, 6+X+VX,Y+VY- 0) | cill(-6+X+VX,Y+VY-24, 6+X+VX,Y+VY-24)){VY=0;}
    if(cill(-6+X+VX,Y+VY- 0,-6+X+VX,Y+VY-24) | cill( 6+X+VX,Y+VY- 0, 6+X+VX,Y+VY-24)){VX=0;}
    if(play.IV>0){
      play.IV--;
    }
    if(HP<=0){
      play.IV=0;
      HP=0;
    }
    if(HP<=1 && notime){
      HP=1;
    }
    if(HD>0 && HDdelay==0){HD--;}
    if(HDdelay>0){HDdelay--;}
    X+=VX;
    Y+=VY;
    }else{
      if(( GetKeyBind("Player_Move_Up") ||  EYS.getSkey(0)) && !ConsoleUP){Y-=10;}
      if(( GetKeyBind("Player_Move_Left") ||  EYS.getSkey(2)) && !ConsoleUP){X-=10;}
      if(( GetKeyBind("Player_Move_Right") ||  EYS.getSkey(1)) && !ConsoleUP){X+=10;}
      if(( GetKeyBind("Player_Move_Down") ||  EYS.getSkey(3)) && !ConsoleUP){Y+=10;}
    }
  }
  
  public void coll(float OX,float OY,float NX,float NY){
    int[] list=CB(OX,OY,NX,NY);
    for(int u=0;u<list.length;u++){//god is here!
      float T=100;
      float N=0;
      float t;
      float r;
      int i = list[u];
      
      t=TLineToLine(OX,OY,NX,NY  ,CSX[i],CSY[i],CEX[i],CEY[i]);
      r=TLineToLine(CSX[i],CSY[i],CEX[i],CEY[i],OX,OY,NX,NY);
      for(int y=0;y<4;y++){
        if(t<=T && t>=0 && r<=1 && r>=0 && !(Ignore && CT[i]==1)){
          {
            T=t;
          }
        }
      }
      if(T>=0 && T<=1){
        PVector TOplayer=new PVector(play.X-(CSX[i]+CEX[i])/2,play.Y-12-(CSY[i]+CEY[i])/2);
        PVector Normal=new PVector((CSX[i]-CEX[i]),(CSY[i]-CEY[i]));
        if(CSX[i]>CEX[i]){Normal.rotate(-PI/2);}else{Normal.rotate(PI/2);}
        if(Normal.dot(TOplayer)<0 && CT[i]==1){
          Ignore=true;
        }
        //TODO fix this!!! why is it working? odd
        float R=atan2(CSY[i]-CEY[i],CSX[i]-CEX[i]);
        float NV = VX * cos(R) + VY * sin(R);
        if(!(Ignore && CT[i]==1)){
        VX = cos(R) * NV * 0.99f;
        VY = sin(R) * NV * 0.99f;
        }
        //PVector TOplayer=atan2(play.Y-12-(CSY[i]+CEY[i])/2,play.X-(CSX[i]+CEX[i])/2);
        //PVector TOplayer=atan2(play.Y-12-(CSY[i]+CEY[i])/2,play.X-(CSX[i]+CEX[i])/2);
        R=atan2(CSY[i]-CEY[i],CSX[i]-CEX[i]);
        if(R<0){R+=PI;}
        if(Normal.dot(TOplayer)>0 && R>-PI/4){//its a feature fuck it
          Gr=true;
        }
        break;
      }
    }
  }
  
  public boolean cill(float OX,float OY,float NX,float NY){
    int[] list=CB(OX,OY,NX,NY);
    for(int e=0;e<list.length;e++){//thaank youu doom
      int i=list[e];
      float t;
      float r;
      t=TLineToLine(OX,OY,NX,NY,CSX[i],CSY[i],CEX[i],CEY[i]);
      r=TLineToLine(CSX[i],CSY[i],CEX[i],CEY[i],OX,OY,NX,NY);
      float T=100;
      if(t<T && t>0 && r<1 && r>0){
          T=t;
        }
      //println(T+"|"+R);
      if(T>0 && T<1 && !(Ignore && CT[i]==1)){
        //X -= sin(N*PI/2+CR[i])*2;
        //Y -= cos(N*PI/2+CR[i])*2;
        return true;
      }
    }
    return false;
  }
  public void Checkfor(){//find better sulucion
    int buffer=3;
    if(!(Checkforsub(X-6,Y+buffer,X-6,Y-24-buffer)||
    Checkforsub(X+6,Y+buffer,X+6,Y-24-buffer)||
    Checkforsub(X-6-buffer,Y,X+6+buffer,Y)||
    Checkforsub(X-6+buffer,Y-24,X+6+buffer,Y-24))){
      Ignore=false;
    }
  }
  public boolean Checkforsub(float SX,float SY,float EX,float EY){
    int[] list=CB(SX,SY,EX,EY);
    for(int e=0;e<list.length;e++){
      int i=list[e];
      float t;
      float r;
      t=TLineToLine(SX,SY,EX,EY,CSX[i],CSY[i],CEX[i],CEY[i]);
      r=TLineToLine(CSX[i],CSY[i],CEX[i],CEY[i],SX,SY,EX,EY);
      float T=1;
      if(t<T && t>0 && r<=1 && r>=0){
          T=t;
          return true;
      }
    }
    return false;
  }
}

public void AThurt(int dmg){
  if(play.IV==0 && !play.God){
    play.HP-=dmg;
    play.HD+=dmg/3*2;
    play.HDdelay=120;
    play.IV=40;
    hurtmepleanty=10;
  }
}
int round=0;
boolean tantactive=false;
String[] arenas;
int enemyDelay=0;
int Grspawn=0;
int[] GrspawnID;
int Arspawn=0;
int[] ArspawnID;
boolean waveEnd=false;
int delaytowave=0;
int Blurer=0;
String CurrentArena="";
int CurrentSave;

public void tantrest(){
  round=0;
  getFile();
  arenas = new String[0];
  File tmp = new File(sketchPath()+"/data/Maps");
  String[] maps = tmp.list();
  if(maps != null){
  for(int i=0;i<maps.length;i++){
    if(split(maps[i],'_')[0].equals("arena")){
      arenas = append(arenas,split(maps[i],'.')[0]);
    }
  }
  }
}

public void tantmath(){
  if(round-1==Indexs.length){
    if(Blurer==90000){
    texttoscren("thats all that exists");
    }
    if(Blurer==90000-240){
    texttoscren("but there might be more");
    }
    if(Blurer==90000-480){
    texttoscren("but now...");
    }
    if(Blurer==90000-480-240){//mathhard
    TextCurr=7;
    TextString="fuckoff";
    TextShow=true;
    }
    if(Blurer==90000-480-240-60){
    exit();
    }
    play.VX=0;
    play.VY=0;
    Blurer--;
    return;
  }
  if(Must==0 && PMust!=0 && enemyDelay>=0){
    waveEnd=true;
    delaytowave=60;
  }
  if(Blurer>0 && !waveEnd){
    Blurer--;
  }
  if(waveEnd){
    delaytowave--;
    Blurer++;
  }
  if(delaytowave==0 && waveEnd){
    nextWave();
  }
  if(round>enemyDelay){
    enemyDelay++;
    if(WAVENUMS.size()>0){
      arenaSpawn(WaveEn[WAVENUMS.remove(0)]);
    }
  }
}

public void nextWave(){
  BOSSHP.clear();
  BOSSID.clear();
  if(round==Indexs.length){
    Blurer=90000;
    round++;
    return;
  }
  Blurer=60;
  int map=(int)random(0,arenas.length);
  switch(round){
    case 10:
    case 20:
    CurrentSave=round;
    byte[] out = new byte[1];
    byte[] tmp = BsetI(round,1);
    out[0]=tmp[0];
    saveBytes("data/Misc/sav",out);
    break;
    default:
    break;
  }
  round++;
  getWave(round);
  enemyDelay=-120;
  waveEnd=false;
  switch(round){
    case 10:
    Start("arena_caves");
    CurrentArena=split("arena_caves",'_')[1];
    break;
    case 20:
    Start("Boss1");
    CurrentArena="gate";
    break;
    default:
    Start(arenas[map]);
    CurrentArena=split(arenas[map],'_')[1];
    break;
  }
        Grspawn=0;
        Arspawn=0;
  GrspawnID = new int[0];
  ArspawnID = new int[0];
  for(int i=0;i<ET.length;i++){
    if(ET[i].equals("air")){
      ArspawnID = append(ArspawnID,i);
      println("air");
    }
    if(ET[i].equals("any")){
      GrspawnID = append(GrspawnID,i);
      println("any");
    }
  }
}

public void ResartWave(){
  getWave(round);
  PMust=0;
  Must=0;
  enemyDelay=-60;
  waveEnd=false;
}

public void arenaSpawn(String name){
  boolean gr=false;
  for(int i=0;i<AINames.length;i++){
    if(AINames[i].equals(name)){
      gr=Sgroun[i];
      break;
    }
  }
  if(gr){
    float X=EX[GrspawnID[Grspawn]];
    float Y=EY[GrspawnID[Grspawn]];
    Grspawn++;
    if(Grspawn==GrspawnID.length){
      Grspawn=0;
    }
    Must++;
    NewAI(X,Y+-11,name,true);
    for(int t=0;t<10;t++){
      AddPartic(3,X,Y,random(-2,2),random(-2,2),100,color(155,0,155),true);
    }
  }else{
    float X=EX[ArspawnID[Arspawn]];
    float Y=EY[ArspawnID[Arspawn]];
    Arspawn++;
    if(Arspawn==ArspawnID.length){
      Arspawn=0;
    }
    Must++;
    NewAI(X,Y+-11,name,true);
    for(int t=0;t<10;t++){
      AddPartic(3,X,Y,random(-2,2),random(-2,2),100,color(155,0,155),true);
    }
  }
}

String[] WaveEn;
int[] Indexs;
byte[] DATAtant;
IntList WAVENUMS;

public void getFile(){
  WaveEn = new String[0];
  Indexs = new int[0];
  DATAtant = loadBytes("/Misc/TantInfo.tyd");
  int PointerZone = BgetI(DATAtant,0,4);
  int NUM = BgetI(DATAtant,4,2);
  int header = 6;
  for(int i=0;i<NUM;i++){
    int size = BgetI(DATAtant,header,2);
    header+=2;
    WaveEn=append(WaveEn,BgetS(DATAtant,header,size));
    header+=size;
  }
  NUM = BgetI(DATAtant,PointerZone,2);
  header=PointerZone;
  header+=2;
  for(int i=0;i<NUM;i++){
    Indexs=append(Indexs,BgetI(DATAtant,header,2));
    header+=2;
  }
}

public void getWave(int i){
  WAVENUMS = new IntList();
  int header=0;
  header=Indexs[i-1];
  int size = BgetI(DATAtant,header,2);
  header+=2;
  for(int u=0;u<size;u++){
    int id = BgetI(DATAtant,header,2);
    header+=2;
    int num = BgetI(DATAtant,header,2);
    for(int o=0;o<num;o++){
      WAVENUMS.append(id);
    }
    header+=2;
  }
  WAVENUMS.shuffle();
}
public void WeaponINIC() {
  MOAG = new ArrayList<Weapon>();
  WeaponSprites = new PImage[0];
  //need to find a way of precedualy do this
  MOAG.add(new testgun());
  MOAG.add(new Railgun());
  MOAG.add(new Garant());
  MOAG.add(new Rocket());
  MOAG.add(new shoogun());
  MOAG.add(new devgun());
  //ADDWeapon(-12,-4,40,10,false,"Weapons/" + "testgun.png");
  //ADDWeapon(-12,-4,0,60,false,"Weapons/" + "Railgun.png");
  //ADDWeapon(-9,-4,0,9,false,"Weapons/" + "Garant.png");
  //ADDWeapon(-9,-2,0,25,false,"Weapons/" + "Rocket.png");
  //ADDWeapon(-9,-2,0,25,false,"Weapons/" + "shoogun.png");
  //ADDWeapon(-7,-2,0,25,false,"Weapons/" + "devgun.png");
}

//void ADDWeapon(int Offx,int Offy,int SDelay,int EDelay,boolean Const,String img){
//  MOAG.add(new Weapon((byte)Offx,(byte)Offy,(byte)SDelay,(byte)EDelay,Const));
//  WeaponSprites = (PImage[])append(WeaponSprites,loadImage(img));
//}

ArrayList<Weapon> MOAG;
PImage[] WeaponSprites;
byte WeaponSellected=0;
byte[] HWeapon = {2, 3, 1, 4};
byte[] curSDelay={0, 0, 0, 0};
byte[] curEDelay={0, 0, 0, 0};

class testgun extends Weapon {
  testgun() {
    Sprite=SloadImage("Weapons/testgun.png");
    WeaOffx=-12;
    WeaOffy=-4;
    WeapW=40;
    WeapH=11;
    SDelay=40;
    EDelay=10;
    Const=false;
  }
  public void FIRE() {
    whack(play.X, play.Y, 35, play.PO, 30, true);
  }
}

class Railgun extends Weapon {
  Railgun() {
    Sprite=SloadImage("Weapons/Railgun.png");
    WeaOffx=-12;
    WeaOffy=-4;
    WeapW=35;
    WeapH=7;
    SDelay=0;
    EDelay=60;
    Const=false;
  }
  public void FIRE() {
    Hitscan(0, 0, play.PO, true, 50, 70,256);
  }
}

class Garant extends Weapon {
  Garant() {
    Sprite=SloadImage("Weapons/Garant.png");
    WeaOffx=-9;
    WeaOffy=-4;
    WeapW=31;
    WeapH=6;
    SDelay=0;
    EDelay=9;
    Const=false;
  }
  public void FIRE() {
    Hitscan(0, 0, play.PO, false, 8, 12,1000);
  }
}

class Rocket extends Weapon {
  Rocket() {
    Sprite=SloadImage("Weapons/Rocket.png");
    WeaOffx=-9;
    WeaOffy=-2;
    WeapW=28;
    WeapH=11;
    SDelay=0;
    EDelay=25;
    Const=false;
  }
  public void FIRE() {
    NewPR(play.X, play.Y-12, cos(play.PO)*8, sin(play.PO)*8, 1);
  }
}

class shoogun extends Weapon {
  shoogun() {
    Sprite=SloadImage("Weapons/shoogun.png");
    WeaOffx=-9;
    WeaOffy=-2;
    WeapW=31;
    WeapH=6;
    SDelay=0;
    EDelay=60;
    Const=false;
  }
  public void FIRE() {
    for (int i=0; i<5; i++) {
      NewPR(play.X, play.Y-12, cos(play.PO+PI/10-PI/20*i)*10, sin(play.PO+PI/10-PI/20*i)*10, 2);
    }
    for (int i=0; i<5; i++) {
      NewPR(play.X, play.Y-12, cos(play.PO+PI/10-PI/20*i)*8, sin(play.PO+PI/10-PI/20*i)*8, 2);
    }
    play.VX-=cos(play.PO)*8;
    play.VY-=sin(play.PO)*8;
  }
}

class devgun extends Weapon {
  //haha devgun spawn 6*10^7 bosses
  devgun() {
    Sprite=SloadImage("Weapons/devgun.png");
    WeaOffx=-7;
    WeaOffy=-2;
    WeapW=19;
    WeapH=3;
    SDelay=0;
    EDelay=25;
    Const=false;
  }
  public void FIRE() {
    AddPartic(1,play.X, play.Y-12, play.X+mouseX-width/2, play.Y+mouseY-height/2, 40, color(255),true);
    NewAI(play.X+mouseX-width/2,play.Y+mouseY-height/2,AINames[DevGun],false);
  }
}

class Weapon {
  byte WeaOffx, WeaOffy;
  byte WeapW,WeapH;
  byte SDelay, EDelay;
  boolean Const;
  PImage Sprite;
  public void FIRE() {
  }
  public void DRAW() {
    pushMatrix();
    scale(ZOOMER);
    translate(width/2/ZOOMER, height/2/ZOOMER-10);
    rotate(play.PO);
    if (play.PO<-PI/2 || play.PO>PI/2) {
      scale(1, -1);
      image(Sprite, WeaOffx, WeaOffy,WeapW,WeapH);
    } else {
      image(Sprite, WeaOffx, WeaOffy,WeapW,WeapH);
    }
    popMatrix();
  }
}

public void Hitscan(int Ofx, int Ofy, float R, boolean Pierce, int lineD, int dmg,float range) {
  float[] tmp = hitscan(R, dmg, Pierce,range);
  AddPartic(1,play.X+Ofx, play.Y-12+Ofy, tmp[0], tmp[1], lineD, color(255),true);
  for (int i=0; i<5; i++) {
    AddPartic(2,tmp[0], tmp[1], random(-1, 1), random(-1, 1), 40, color(255, 255, 0),false);
  }
}

int DevGun=0;

public void HWeaponMATH() {
  if (mousePressed && mouseButton==LEFT && curEDelay[WeaponSellected]<1 && !MenuPaused) {
    if (curSDelay[WeaponSellected]>=MOAG.get(HWeapon[WeaponSellected]).SDelay) {
      MOAG.get(HWeapon[WeaponSellected]).FIRE();
      if (!MOAG.get(HWeapon[WeaponSellected]).Const) {
        curEDelay[WeaponSellected]=MOAG.get(HWeapon[WeaponSellected]).EDelay;
        curSDelay[WeaponSellected]=0;
      }
    } else {
      curSDelay[WeaponSellected]++;
    }
  } else {
    if (MOAG.get(HWeapon[WeaponSellected]).Const && curSDelay[WeaponSellected]>=MOAG.get(HWeapon[WeaponSellected]).SDelay) {
      curEDelay[WeaponSellected]=MOAG.get(HWeapon[WeaponSellected]).EDelay;
      curSDelay[WeaponSellected]=0;
    } else {
      curSDelay[WeaponSellected]=0;
    }
  }
  for (int i=0; i<HWeapon.length; i++) {
    if (curEDelay[i]>=1) {
      curEDelay[i]--;
    }
  }
}

public void HWeaponDRAW() {
  MOAG.get(HWeapon[WeaponSellected]).DRAW();
  float rescale=(float)Configs.get("GuiScale")/100;
  pushMatrix();
  scale(rescale);
  noFill();
  stroke(0, 0, 255, 100);
  strokeWeight(5);
  strokeCap(SQUARE);
  arc(width/2*(1/rescale), (height/2-12)*(1/rescale), 36, 36, PI/2-PI*curSDelay[WeaponSellected]/MOAG.get(HWeapon[WeaponSellected]).SDelay, PI/2);
  arc(width/2*(1/rescale), (height/2-12)*(1/rescale), 36, 36, PI/2-PI*curEDelay[WeaponSellected]/MOAG.get(HWeapon[WeaponSellected]).EDelay, PI/2);
  strokeWeight(2);
  for (int i=0; i<HWeapon.length; i++) {
    arc(width/2*(1/rescale), (height/2-12)*(1/rescale), 44+i*6, 44+i*6, PI/2-PI*curEDelay[i]/MOAG.get(HWeapon[i]).EDelay, PI/2);
  }
  popMatrix();
  strokeWeight(3);
  stroke(50);
  fill(100);
  for (int i=0; i<HWeapon.length; i++) {
    rect(i*80, 0, 80, 40);
  }
  stroke(50);
  fill(120);
  rect(WeaponSellected*80,0,80,40);
  for (int i=0; i<HWeapon.length; i++) {
    PImage tmp=MOAG.get(HWeapon[i]).Sprite;
    byte Width = MOAG.get(HWeapon[i]).WeapW;
    byte Height = MOAG.get(HWeapon[i]).WeapH;
    image(tmp, i*80+40-Width/2, 20-Height/2 ,Width,Height);
  }
  noStroke();
  fill(200, 200, 200);
  for (int i=0; i<HWeapon.length; i++) {
    rect(i*80, 30, 80*curEDelay[i]/MOAG.get(HWeapon[i]).EDelay, 10);
  }
  strokeWeight(1);
  strokeCap(ROUND);
  stroke(0);
  noFill();
}

public void whack(float x, float y, float r, float d, float f, boolean player) {
  for (int i=0; i<ListAi.size(); i++) {
    float X = ListAi.get(i).X;
    float Y = ListAi.get(i).Y;
    if (dist(X, Y, x, y)<=r) {
      ListAi.get(i).VX = cos(d)*f;
      ListAi.get(i).VY = sin(d)*f;
    }
  }
  if (player) {
    if (dist(play.X, play.Y, x, y)<=r) {
      play.VX = cos(d)*f;
      play.VY = sin(d)*f;
    }
  }
}

public float[] hitscan(float R, int dmg, boolean Pierce,float range) {
  float MT=range;
  int T=-1;
  float OX=play.X;
  float OY=play.Y-12;
  float NX=play.X+cos(R);
  float NY=play.Y-12+sin(R);
  int[] list=CB(OX, OY, NX+cos(R)*range, NY+sin(R)*range);
  for (int p=0; p<list.length; p++) {
    int i=list[p];
    float r = TLineToLine(CSX[i], CSY[i], CEX[i], CEY[i], OX, OY, NX, NY);
    if (r>=0 && r<=1 && CT[i]!=1) {
      float t = TLineToLine(OX, OY, NX, NY, CSX[i], CSY[i], CEX[i], CEY[i]);
      if (t>0 && t<MT & r>=0 && r<=1) {
        MT=t;
      }
    }
  }
  for (int i=0; i<ListAi.size(); i++) {
    AI tmp = ListAi.get(i);
    float R1 = TLineToLine(tmp.X-tmp.W, tmp.Y-tmp.H, tmp.X+tmp.W, tmp.Y, play.X, play.Y-12, play.X+cos(R), play.Y-12+sin(R));
    float R2 = TLineToLine(tmp.X+tmp.W, tmp.Y-tmp.H, tmp.X-tmp.W, tmp.Y, play.X, play.Y-12, play.X+cos(R), play.Y-12+sin(R));
    if (R1>=0 && R1<=1 || R2>=0 && R2<=1) {
      float T1 = TLineToLine(play.X, play.Y-12, play.X+cos(R), play.Y-12+sin(R), tmp.X-tmp.W, tmp.Y-tmp.H, tmp.X+tmp.W, tmp.Y);
      float T2 = TLineToLine(play.X, play.Y-12, play.X+cos(R), play.Y-12+sin(R), tmp.X+tmp.W, tmp.Y-tmp.H, tmp.X-tmp.W, tmp.Y);
      if (min(T1, T2)>0 && min(T1, T2)<MT & !Pierce) {
        MT=min(MT, min(T1, T2));
        T=i;
      }
      if (min(T1, T2)>0 && min(T1, T2)<MT & Pierce) {
        ListAi.get(i).HURT(dmg);
      }
    }
  }
  if (T>=0 && !Pierce) {
    ListAi.get(T).HURT(dmg);
  }
  float[] tmp = {play.X+cos(R)*MT, play.Y-12+sin(R)*MT};
  return tmp;
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ProjectDF" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
