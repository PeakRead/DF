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

String Version = "V 7.1";

keyboard EYS;

public void settings(){
  size(700,700,P2D);
  PJOGL.setIcon("Misc/ICON.png");
}

PImage Secret;
PShape MenuBackground;

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
  resetALLassets();
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
      DebugDraw=tmp.getBoolean("DebugStart");
      HWeapon=append(HWeapon,PApplet.parseByte(5));
      curSDelay=append(curSDelay,PApplet.parseByte(0));
      curEDelay=append(curEDelay,PApplet.parseByte(0));
      HWeapon=append(HWeapon,PApplet.parseByte(6));
      curSDelay=append(curSDelay,PApplet.parseByte(0));
      curEDelay=append(curEDelay,PApplet.parseByte(0));
      Start(tmp.getString("DebugMap","arena_vent"));
      if(tmp.getBoolean("DebugTant",false)){
        tantactive=true;
        tantrest();
        round=PApplet.parseInt(tmp.getInt("DebugRound",1))-1;
        nextWave();
      }
    }
    Configs.set("DrawEffects",tmp.getInt("DrawEffects",1));
    Configs.set("GuiScale",tmp.getInt("GuiScale",100));
    Configs.set("Zoom",tmp.getInt("Zoom",100));
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
  
  Darken = loadShader("Misc/Darken.glsl");
  LightX = new float[128];
  LightY = new float[128];
  LightActive = new boolean[128];
  LightPower = new float[128];
  Darken.set("Width",(float)700);
  Darken.set("Height",(float)700);
  Darken.set("X",LightX);
  Darken.set("Y",LightY);
  Darken.set("Active",LightActive);
  Darken.set("Active",LightPower);
  Darken.set("Power",LightPower);
  
  
  Background=createGraphics(width,height,P3D);
  
  //dont remove this fixs the bluring
  textSize(12);
}

public void resetALLassets(){
  
  loadPacks();
  tantrest();
  setupKeys();
  resetCon();
  WeaponINIC();
  EnemyAINIC();
  ProAINIC();
  MenuSetup();
  PartINIC();
  ErrorImg = SloadImage("/Misc/Error.png");
  MenuBackground = loadPly("Misc/background.ply");
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
PShader Darken;
float[] LightX;
float[] LightY;
boolean[] LightActive;
float[] LightPower;
boolean DarkenActive = false;
int GlobalPhysTimer=0;

//make ent_timers and ent_textbox
int toturialTimer=0;
boolean toturialMode=false;

public void draw(){
  if(LW!=width || LH!=height){
    LW=width;
    LH=height;
    Background=createGraphics(width,height,P3D);
    Darken.set("Width",(float)width);
    Darken.set("Height",(float)height);
    if(WALLs!=null){
      for(int i=0;i<WALLs.length;i++){
        if(WALLs[i].IsShader){
          WALLs[i].shader.set("WIDTH",(float)width);
          WALLs[i].shader.set("HEIGHT",(float)height);
        }
      }
    }
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
    if(MenuBackground!=null){
      Background.beginDraw();
      Background.background(0);
      Background.ambientLight(100, 100, 100);
      Background.directionalLight(155, 155, 155, -0.5f, 0.5f, -1);
      //Background.ambientLight(255, 255, 255);
      Background.camera(0,-121,0,0,999,0, 0,0,-1);
      Background.perspective(PI/3.0f,PApplet.parseFloat(width)/PApplet.parseFloat(height),1,100000);
      Background.pushMatrix();
      //Background.scale(1,1,-1);
      Background.shape(MenuBackground);
      Background.popMatrix();
      Background.endDraw();
      image(Background, 0, 0);
    }else{
      background(0);
    }
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
    //DEBUGING
    println("BREAK"); //<>//
  }
  if(RunPhys){
    GlobalPhysTimer++;
    if(play.HP>0){HWeaponMATH();}
    try{
      play.Phy();
    }catch(Exception e){
      PrintCon("sorry for that");
      play.MT();
      PrintCon(e.toString());
      ErrorTimer=120;
    }
    for(int i=0;i<128;i++){
      LightActive[i]=false;
    }
    LightX[0] = play.X;
    LightY[0] = play.Y;
    LightActive[0] = true;
    LightPower[0] = 1;
    AIMath();
    ProjMath();
    MathEffects();
    trigger();
    Atrigger();
    propM();
    doorM();
    Darken.set("X",LightX);
    Darken.set("Y",LightY);
    Darken.set("Active",LightActive);
    Darken.set("Power",LightPower);
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
      if(TT[i]==8 && random(0,100)<60){
        NewPartic(new GravPoint(TX[i]+random(0,TW[i]),TY[i]+random(120,TH[i]),0,0,240,0xff30A018,-0.2f),true);
      }
    }
  }
  if(tantactive){
    tantmath();
  }
  //RENDER
  ZOOMER=PApplet.parseFloat(Configs.get("Zoom"))/100;
  Darken.set("OffX",play.X*ZOOMER-width/2);
  Darken.set("OffY",play.Y*ZOOMER-height/2);
  Darken.set("Zoom",ZOOMER);
  Darken.set("P",(float)0.004f/ZOOMER);
  for(int i=0;i<WALLs.length;i++){
    if(WALLs[i].IsShader){
      WALLs[i].shader.set("OFFX",-play.X*ZOOMER+width/2+WALLs[i].Offx);
      WALLs[i].shader.set("OFFY",-play.Y*ZOOMER+height/2+WALLs[i].Offy);
      WALLs[i].shader.set("SIZE",ZOOMER);
      WALLs[i].shader.set("TIME",GlobalPhysTimer);
    }
  }
  //why and how do colors work on this shit
  //fuck obj my new best friend is ply
  background(0);
  if(Mapinfo.IsBackTexture){
    if(Mapinfo.IsShader){
      shader(Mapinfo.Shader);
    }
    beginShape();
    if(!Mapinfo.IsShader){
      texture(Mapinfo.Texture);
    }
    vertex(0    ,0     ,0    ,0);
    vertex(width,0     ,width,0);
    vertex(width,height,width,height);
    vertex(0    ,height,0    ,height);
    endShape();
  }
  if(Mapinfo.IsBackModel && Mapinfo.Model!=null){
  Background.beginDraw();
  Background.background(0);
  Background.ambientLight(100, 100, 100);
  Background.directionalLight(155, 155, 155, -0.5f, 0.5f, -1);
  //Background.ambientLight(255, 255, 255);
  Background.camera(-play.X/5,-121/ZOOMER ,-play.Y/5, -play.X/5,999, -play.Y/5, 0,0,-1);
  Background.perspective(PI/3.0f,PApplet.parseFloat(width)/PApplet.parseFloat(height),1,100000);
  Background.pushMatrix();
  //Background.scale(1,1,-1);
  Background.shape(Mapinfo.Model);
  Background.popMatrix();
  Background.endDraw();
  image(Background, 0, 0);
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
  
  for(int i=0;i<WALLs.length;i++){
    if(WALLs[i].IsShader){
      shader(WALLs[i].shader);
    }
    textureWrap(REPEAT);
    beginShape();
    if(!WALLs[i].IsShader){
      texture(WALLs[i].img);
    }
    for(int u=0;u<WALLs[i].x.length;u++){
      vertex(WALLs[i].x[u],WALLs[i].y[u],WALLs[i].x[u],WALLs[i].y[u]);
    }
    endShape();
    if(WALLs[i].IsShader){
      resetShader();
    }
  }
  
  propD(); //<>//
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
  for(int i=0;i<TX.length;i++){
    if(TT[i]==8){
      stroke(0xff31DB46);
      fill(0xff31DB46,100);
      rect(TX[i],TY[i],TW[i],TH[i]);
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
  popMatrix();
  if(DarkenActive){
    noStroke();
    shader(Darken);
    rect(0,0,width,height);
    resetShader();
  }
  stroke(0);
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

String[] AINames={"Bug", "Fly", "Target", "Spewer", "testBoss", "Maze", "Laze", "Maze_Boss", "Laze_Boss", "tower", "napalm", "Spirit", "Guardian", "Crab", "Piller", "Supply", "Supply_Boss", "Electron", "Limbo","Lust","Gluttony","Greed","Anger","Heresy","Hatred","Violence","Fraud","Treachery","Zenith"};
boolean[] Sgroun={true ,false , true    , true    , true      , false , false , false      , false      , true   , true    , false   , true      , true  , true    , false   , false        , false     , false  ,false ,false     ,false  ,false  ,false   ,false   ,false     ,false  ,false      ,false};
String[] SupplySummon={"Fly", "Bug", "Spewer", "tower", "Maze", "Laze"};
String[] LimboSummoners = {"Limbo","Lust","Gluttony","Greed","Anger","Heresy","Violence","Fraud","Treachery"};
  
public void AIMath() {
  PMust=Must;
  for (int i=0; i<ListAi.size(); i++) {
    try {
      ListAi.get(i).math(i);
      AI tmp = ListAi.get(i);
      if (tmp.X>10000 | tmp.X<-10000 | tmp.Y>10000 | tmp.Y<-10000) {
        kill.append(i);
      }
    }
    catch(Exception e) {
      PrintCon("sorry for that");
      kill.append(i);
      AddPartic(1, ListAi.get(i).X, ListAi.get(i).Y, ListAi.get(i).X, -10000, 60, 0xffFFFFFF, false);
      AddPartic(5, ListAi.get(i).X, ListAi.get(i).Y, 128, 0, 60, 0xffFFFFFF, true);
      for (int ohno=0; ohno<50; ohno++) {
        AddPartic(2, ListAi.get(i).X, ListAi.get(i).Y, random(-10, 10), random(-10, 10), 60, 0xffFFFFFF, false);
      }
      PrintCon(e.toString());
      ErrorTimer=120;
    }
  }
  for (int i=0; i<ListAi.size(); i++) {
    try {
      if (ListAi.get(i).getClass()==Class.forName("ProjectDF$Spirit")) {
        Spirit tmp = (Spirit)ListAi.get(i);
        if (kill.hasValue(tmp.Connected)) {
          tmp.Con=false;
          tmp.hurte=true;
          ListAi.set(i, tmp);
        }
      }
    }
    catch(Exception e) {
    }
  }
  kill.reverse();
  for (int i=0; i<kill.size(); i++) {
    AI tmp = ListAi.get(kill.get(i));
    BOSSID.reverse();
    BOSSHP.reverse();
    for (int u=0; u<BOSSID.size(); u++) {
      if (kill.get(i)==BOSSID.get(u)) {
        BOSSID.remove(u);
        BOSSHP.remove(u);
        break;
      }
      if (kill.get(i)<BOSSID.get(u)) {
        BOSSID.set(u, BOSSID.get(u)-1);
      }
    }
    BOSSID.reverse();
    BOSSHP.reverse();
    if (dist(tmp.X, tmp.Y, play.X, play.Y)<=200 && play.HP>0) {
      AddPartic(1, play.X+random(-5, 5), play.Y-12+random(-5, 5), tmp.X+random(-5, 5), tmp.Y-tmp.H/2+random(-5, 5), 60, color(255, 0, 0), true);
      if (play.HP+5>100) {
        play.HP=100;
      } else {
        play.HP+=5;
      }
    }
    if (tmp.M) {
      Must--;
    }
  }
  for (int i=0; i<kill.size(); i++) {
    ListAi.remove(kill.get(i));
  }
  kill.clear();
}

public void AIR() {
  for (int i=0; i<ListAi.size(); i++) {
    ListAi.get(i).render();
    if (DebugDraw) {
      text(ListAi.get(i).HP, ListAi.get(i).X+10, ListAi.get(i).Y-10);
    }
  }
}

class Bug extends AI {
  Bug(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=10;
    H=14;
    HP=20;
    T=0;
    Animr = new SelfAnim(EAR.get("Bug"));
  }
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    Walk(0.3f, 0.5f, 6);
    Cont(W, H, 25);
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    pushMatrix();
    if (VX>0) {
      scale(-1, 1);
      translate(-X*2, 0);
    }
    Animr.Anim(true, OG>3);
    Animr.DIMG(X, Y, W, H, true, OG>3, 0xffFFFFFF);
    popMatrix();
  }
}

class Fly extends AI {
  Fly(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=6;
    H=12;
    HP=12;
    T=4;
    Animr = new SelfAnim(EAR.get("Fly"));
  }
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    VX+=cos(atan2(play.Y-Y, play.X-X))*0.4f;
    VY+=sin(atan2(play.Y-Y, play.X-X))*0.2f;
    VX=constrain(VX, -10, 10);
    VY=constrain(VY, -10, 10);
    Cont(W, H, 15);
    Phys(W, H, false);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    Animr.Anim(false, true);
    Animr.DIMG(X, Y, W, H, false, true, 0xffFFFFFF);
  }
}

class Target extends AI {
  Target(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=6;
    H=24;
    HP=9999;
    Animr = new SelfAnim(EAR.get("Target"));
  }
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    Fall();
    Cont(W, H, 1);
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
    NewPartic(new StandImg(X, Y, random(-12, 12), random(-12, 12), 15, 0xffFFFFFF, "uranium.png"), true);
    NewSPr(new hurtbox(X, Y, 20, 9000, -PI/4, 0, 15, 5));
    NewSPr(new hurtbox(X, Y, 20, 9000, PI/4, 0, 15, 5));
  }
  public void render() {
    stroke(0);
    fill(255);
    rect(X-W, Y-H, W*2, H);
    Animr.Anim(false, false);
    Animr.DIMG(X, Y, W, H, false, false, 0xffFFFFFF);
  }
  public void HURT(int dmg)
  {
    if (!hurte) {
      return;
    }
    HP-=dmg;
    Animr.Action(0);
    for (int B=0; B<5; B++) {
      AddPartic(4, X, Y, random(-1, 1), random(-8, -2), 50, color(255, 0, 0), true);
    }
    if (play.regenera==0) {
      if (dist(X, Y, play.X, play.Y)<=200 && play.HP>0) {
        AddPartic(1, play.X+random(-5, 5), play.Y-12+random(-5, 5), X+random(-5, 5), Y-H/2+random(-5, 5), 60, color(255, 0, 0), true);
        if (play.HP+dmg/4>100) {
          play.HP=100;
        } else {
          play.HP+=dmg/4;
        }
      }
    } else {
      if (random(1, 100)<50 && play.HP>0) {
        NewPR(X, Y-H/2, random(-5, 5), random(-5, 5), 10);
      }
    }
  }
}

class Spewer extends AI {
  int cooldown=80;
  Spewer(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=6;
    H=24;
    HP=34;
    T=5;
    Animr = new SelfAnim(EAR.get("Spewer"));
  }
  public void math(int SID) {
    if (dist(X, Y, play.X, play.Y)>150) {
      Walk(0.01f, 0.01f, 1);
    }
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    Fall();
    Cont(W, H, 1);
    Phys(W, H, true);
    if (cooldown==0) {

      NewPR(X+W, Y-H/2, cos(atan2(play.Y-Y+H/2-12, play.X+6-X-W))*6, sin(atan2(play.Y-Y+H/2-8, play.X+6-X-W))*6, 0);
      cooldown=80;
    } else {
      cooldown--;
    }
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    pushMatrix();
    if (VX<0) {
      scale(-1, 1);
      translate(-X*2, 0);
    }
    Animr.Anim(Gr && abs(VX)<0.5f, false);
    Animr.DIMG(X, Y, W, H, Gr && abs(VX)<0.5f, false, 0xffFFFFFF);
    popMatrix();
  }
}

class testBoss extends AI {
  testBoss(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=16;
    H=34;
    HP=600;
    BOSSHP.append(HP);
    BOSSID.append(ListAi.size());
  }
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    Fall();
    Cont(W, H, 1);
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    stroke(0);
    fill(255);
    rect(X-W, Y-H, W*2, H);
  }
}

class Maze extends AI {
  boolean Enraged=false;
  Maze(float nX, float nY, boolean nM, boolean Boss) {
    X=nX;
    Y=nY;
    M=nM;
    W=16;
    H=32;
    T=1;
    if (Boss) {
      HP=800;
      W=32;
      H=64;
      BOSSHP.append(HP);
      BOSSID.append(ListAi.size());
    } else {
      HP=200;
    };
    Animr = new SelfAnim(EAR.get("Maze"));
  }
  int cooldown = 370;
  int attack = 1;
  public void math(int SID) {
    if (HP<=0) {
      if (Gr || HP<=-1000) {
        AddPartic(5, X, Y, 32, 0, 15, 0xff00FF00, false);
        kill.append(SID);
        for (int all=0; all<ListAi.size(); all++) {
          try {
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Maze")) {
              Maze tmp = (Maze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Laze")) {
              Laze tmp = (Laze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Supply")) {
              Supply tmp = (Supply)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
          }
          catch(Exception e) {
          }
        }
        return;
      }
      VY+=0.2f;
    } else {
      Gr=false;
      if (cooldown>200 && cooldown<300) {
        VX+=(play.X-X+cos((float)frameCount/20)*64)/10;
        VY+=(play.Y-Y+sin((float)frameCount/20)*64-200)/10;
        VX=VX/10*9;
        VY=VY/10*9;
      } else {
        VX=0;
        VY=0;
      }
      if (cooldown<200 && cooldown>80) {
        float N=((float)cooldown-80)/120*8;
        if (attack==0) {
          AddPartic(3, X, Y-H/2, cos(0*PI/2)*N, sin(0*PI/2)*N, 15, 0xff00FFFF, true);
          AddPartic(3, X, Y-H/2, cos(1*PI/2)*N, sin(1*PI/2)*N, 15, 0xff00FFFF, true);
          AddPartic(3, X, Y-H/2, cos(2*PI/2)*N, sin(2*PI/2)*N, 15, 0xff00FFFF, true);
          AddPartic(3, X, Y-H/2, cos(3*PI/2)*N, sin(3*PI/2)*N, 15, 0xff00FFFF, true);
        } else {
          AddPartic(3, X, Y-H/2, cos(0*PI/2+PI/4)*N, sin(0*PI/2+PI/4)*N, 15, 0xff00FFFF, true);
          AddPartic(3, X, Y-H/2, cos(1*PI/2+PI/4)*N, sin(1*PI/2+PI/4)*N, 15, 0xff00FFFF, true);
          AddPartic(3, X, Y-H/2, cos(2*PI/2+PI/4)*N, sin(2*PI/2+PI/4)*N, 15, 0xff00FFFF, true);
          AddPartic(3, X, Y-H/2, cos(3*PI/2+PI/4)*N, sin(3*PI/2+PI/4)*N, 15, 0xff00FFFF, true);
        }
      }
      if (!Enraged) {
        if (cooldown<80) {
          if (attack==0) {
            if (cooldown%10==0) {
              for (int i=0; i<4; i++) {
                NewPR(X, Y-H/2, cos(i*PI/2+(float)cooldown/60*PI)*5, sin(i*PI/2+(float)cooldown/60*PI)*5, 3);
              }
            }
          } else {
            if (cooldown%10==0) {
              float target = atan2(play.Y-Y, play.X-X);
              for (int i=0; i<3; i++) {
                NewPR(X, Y-H/2, cos(target+PI/8*(i-1))*5, sin(target+PI/8*(i-1))*5, 3);
              }
            }
          }
        }
      } else {
        if (cooldown<80) {
          if (cooldown%10==0) {
            for (int i=0; i<8; i++) {
              NewPR(X, Y-H/2, cos(i*PI/4+(float)cooldown/60)*5, sin(i*PI/4+(float)cooldown/60)*5, 3);
            }
          }
        }
      }
      cooldown--;
      if (cooldown<0) {
        cooldown=370;
        attack = (int)random(0, 2);
      }
    }
    Cont(W, H, 1);
    Phys(W, H, false);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    Animr.Anim(false, true);
    if (!Enraged) {
      Animr.DIMG(X, Y, W, H, false, true, 0xffFFFFFF);
    } else {
      Animr.DIMG(X, Y, W, H, false, true, 0xffFFAAAA);
      stroke(255, 0, 0);
      noFill();
      circle(X+random(-2, 2), Y-H/2+random(-2, 2), 36);
    }
  }
}

class Laze extends AI {
  boolean Enraged=false;
  Laze(float nX, float nY, boolean nM, boolean Boss) {
    X=nX;
    Y=nY;
    M=nM;
    W=16;
    H=32;
    T=2;
    if (Boss) {
      HP=800;
      W=32;
      H=64;
      BOSSHP.append(HP);
      BOSSID.append(ListAi.size());
    } else {
      HP=200;
    }
    Animr = new SelfAnim(EAR.get("Laze"));
  }
  int cooldown = 420;
  int attack = 1;
  float LastPlayer = 0;
  float Tx;
  float Ty;
  public void math(int SID) {
    if (HP<=0) {
      if (Gr || HP<=-1000) {
        AddPartic(5, X, Y, 32, 0, 15, 0xff00FF00, false);
        kill.append(SID);
        for (int all=0; all<ListAi.size(); all++) {
          try {
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Maze")) {
              Maze tmp = (Maze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Laze")) {
              Laze tmp = (Laze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Supply")) {
              Supply tmp = (Supply)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
          }
          catch(Exception e) {
          }
        }
        return;
      }
      VY+=0.2f;
    } else {
      Gr=false;
      if (cooldown>200  && cooldown<350) {
        VX+=(play.X-X+cos(-(float)frameCount/20)*64)/10;
        VY+=(play.Y-Y+sin(-(float)frameCount/20)*64-200)/10;
        VX=VX/10*9;
        VY=VY/10*9;
      } else {
        VX=0;
        VY=0;
      }
      if (cooldown==200) {
        if (attack==0) {
          Tx=X;
          Ty=Y;
        } else {
          LastPlayer = atan2(play.Y-Y, play.X-X);
        }
      }
      if (cooldown<200 && cooldown>80) {
        if (attack==0 || Enraged) {
          Tx=(play.X+Tx)/2;
          Ty=(play.Y-12+Ty)/2;
        } else {
          float[] tmp=Enyscan(LastPlayer+PI/4, true, 0, -H/2);
          AddPartic(1, X, Y-H/2, tmp[0], tmp[1], 2, color(100, 0, 0), true);
          tmp=Enyscan(LastPlayer-PI/4, true, 0, -H/2);
          AddPartic(1, X, Y-H/2, tmp[0], tmp[1], 2, color(100, 0, 0), true);
        }
      }
      if (cooldown<80) {
        if (!Enraged) {
          if (attack==0) {
            if ((cooldown+10)%20==0) {
              Tx=play.X;
              Ty=play.Y;
            }
            if (cooldown%20==0) {
              float[] tmp=Enyhitscan(atan2(Ty-Y, Tx-X), 25, true, 0, -H/2);
              AddPartic(1, X, Y-H/2, tmp[0], tmp[1], 40, color(255, 0, 0), true);
            }
          } else {
            float[] tmp=Enyhitscan(LastPlayer+PI/4*((float)cooldown/80), 15, true, 0, -H/2);
            AddPartic(1, X, Y-H/2, tmp[0], tmp[1], 40, color(255, 0, 0), true);
            tmp=Enyhitscan(LastPlayer-PI/4*((float)cooldown/80), 15, true, 0, -H/2);
            AddPartic(1, X, Y-H/2, tmp[0], tmp[1], 40, color(255, 0, 0), true);
          }
        } else {
          if (cooldown%8==0) {
            Tx=(play.X+Tx*2)/3;
            Ty=(play.Y-12+Ty*2)/3;
            float[] tmp=Enyhitscan(atan2(Ty-Y, Tx-X), 25, true, 0, -H/2);
            AddPartic(1, X, Y-H/2, tmp[0], tmp[1], 40, color(255, 0, 0), true);
          }
        }
      }
      cooldown--;
      if (cooldown<0) {
        cooldown=420;
        attack = (int)random(0, 2);
      }
    }
    Cont(W, H, 1);
    Phys(W, H, false);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    Animr.Anim(false, true);
    if (!Enraged) {
      Animr.DIMG(X, Y, W, H, false, true, 0xffFFFFFF);
    } else {
      Animr.DIMG(X, Y, W, H, false, true, 0xffFFAAAA);
      stroke(255, 0, 0);
      noFill();
      circle(X+random(-2, 2), Y-H/2+random(-2, 2), 36);
    }
    if (cooldown<200 & (attack == 0 || Enraged)) {
      stroke(0xffB703FF);
      noFill();
      circle(Tx, Ty, 16);
      line(Tx-20, Ty, Tx+20, Ty);
      line(Tx, Ty-20, Tx, Ty+20);
      stroke(0xffFF0000, 100);
      line(X, Y-H/2, Tx, Ty);
    }
  }
}

class Tower extends AI {
  int cooldown=200;
  Tower(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=12;
    H=48;
    HP=34;
    T=3;
    Animr = new SelfAnim(EAR.get("Tower"));
  }
  float Tx, Ty;
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    Fall();
    //Walk(0.3,0.5,6);
    //Phys(W,H,true);
    //Cont(W,H,35);
    if (dist(X, Y, play.X, play.Y)<150) {
      Walk(-1, -1, 1);
    }
    Cont(W, H, 45);
    Phys(W, H, true);
    if (cooldown==0) {
      float[] tmp=Enyhitscan(atan2(Ty-Y+12, Tx-X), 25, true, 0, -H/2);
      AddPartic(1, X, Y-H+6, tmp[0], tmp[1], 40, color(255, 0, 0), true);
      cooldown=200;
    }
    if (cooldown>40 && cooldown<80) {
      float[] tmp=Enyscan(atan2(Ty-Y+12, Tx-X)+PI/8*(((float)cooldown-40)/40), true, 0, -H/2);
      AddPartic(1, X, Y-H+6, tmp[0], tmp[1], 2, color(200, 0, 0), false);
      tmp=Enyscan(atan2(Ty-Y+12, Tx-X)-PI/8*(((float)cooldown-40)/40), true, 0, -H/2);
      AddPartic(1, X, Y-H+6, tmp[0], tmp[1], 2, color(200, 0, 0), false);
      Tx=play.X;
      Ty=play.Y;
    }
    if (cooldown<80) {
      float[] tmp=Enyscan(atan2(Ty-Y+12, Tx-X), true, 0, -H/2);
      AddPartic(1, X, Y-H+6, tmp[0], tmp[1], 2, color(100, 0, 0), true);
    }
    cooldown--;
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    Animr.Anim(abs(VX)>2, false);
    Animr.DIMG(X, Y, W, H, abs(VX)>2, false, 0xffFFFFFF);
  }
}

class Napalm extends AI {
  Napalm(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=10;
    H=20;
    HP=1;
    Animr = new SelfAnim(EAR.get("Napalm"));
  }
  int dieng=0;
  public void math(int SID) {
    if (HP<=0 && dieng==0) {
      Animr.Action(0);
      dieng=1;
    }
    if (dieng>0) {
      dieng++;
      if (dieng==36) {
        for (int i=0; i<30; i++) {
          float T=random(5, 15);
          float R=random(-PI, PI);
          AddPartic(2, X, Y, cos(R)*T, sin(R)*T, 40, color(0xffFF0000), false);
        }
        for (int i=0; i<7; i++) {
          //float T=random(5,15);
          //float R=random(-PI,PI);
          //NewPartic(new Line(X,Y-12,X+sin((i-4)*PI/12)*999,Y-cos((i-4)*PI/12)*999,40,color(#FF0000)),false);
          NewPR(X, Y-12, sin((i-3)*PI/12)*3, -cos((i-3)*PI/12)*6, 4);
        }
        AddPartic(5, X, Y, 100, 0, 40, color(0xffFF0000), true);
        kill.append(SID);
        return;
      }
    }
    Fall();
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    Animr.Anim(false, false);
    Animr.DIMG(X, Y, W, H, false, false, 0xffFFFFFF);
  }
}

class Spirit extends AI {
  Spirit(float nX, float nY, boolean nM) {
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
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    if (!Con) {
      hurte=true;
    }
    if (Cooldown==0 & !Con) {
      int NUM=floor(random(0, ListAi.size()));
      try {
        if (SID!=NUM && !(ListAi.get(NUM).getClass()==Class.forName("ProjectDF$Spirit") || ListAi.get(NUM).getClass()==Class.forName("ProjectDF$Hatred"))) {
          Connected=NUM;
          Con=true;
          hurte=false;
          Cooldown=240;
        }
      }
      catch(Exception e) {
      }
    }
    try {
      if (ListAi.get(Connected).getClass()==Class.forName("ProjectDF$Spirit") || ListAi.get(Connected).getClass()==Class.forName("ProjectDF$Hatred")) {
        Con=false;
        hurte=true;
      }
    }
    catch(Exception e) {
    }
    if (Cooldown>0 & !Con) {
      Cooldown--;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if (frameCount%2==0) {
      NewPR(X, Y-H/2, -cos(R), -sin(R), 5);
    }
    if (dist(X, Y, play.X, play.Y)<150) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>350) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2);
    VY=constrain(VY, -2, 2);
    VY-=0.01f;
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    stroke(0xff00FFFF);
    fill(0xff00CCCC);
    rect(X-W, Y-H, W*2, H);
    if (Con) {
      try {
        AI tmp = ListAi.get(Connected);
        AddPartic(1, X, Y-H/2, tmp.X, tmp.Y, 1, color(0xff00FFFF, 100), true);
        stroke(color(0xff00FFFF));
        noFill();
        quad(X-64, Y-H/2, X, Y-64-H/2, X+64, Y-H/2, X, Y+64-H/2);
      }
      catch(Exception e) {
        hurte=true;
      }
    }
  }
}

class Guardian extends AI {
  int cooldown=360;
  int attack=0;
  int intro=240;
  Guardian(float nX, float nY, boolean nM) {
    Bresistance=0;
    X=nX;
    Y=nY;
    M=nM;
    W=50;
    H=100;//1600
    HP=5000;
    T=3;
    hurte=false;
    Animr = new SelfAnim(EAR.get("Guardian"));
  }
  public void math(int SID) {
    if (intro>0 && HP>0) {
      intro--;
      if (intro<120) {
        float R=random(-PI, PI);
        float D=random(64, 128);
        NewPartic(new Wind(X-cos(R)*D, Y-H/2-sin(R)*D, cos(R)*D/10, sin(R)*D/10, 10, 0xffFFFFFF), true);
      }
    }
    if (intro==1 && HP>0) {
      BOSSHP.append(HP);
      BOSSID.append(SID);
      hurte=true;
    }
    if (HP<=0) {
      float R=random(-PI, PI);
      float D=random(intro/3, intro);
      NewPartic(new Wind(X, Y-H/2, cos(R)*D/3, sin(R)*D/3, 10, 0xffFFFFFF), true);
      intro++;
      cooldown=900;
      if (intro>120) {
        NewPartic(new Explode(X, Y-H/2, 128, 0, 60, 0xffD80B0B), true);
        NewPartic(new Explode(X, Y-H/2, 128+64, 0, 60, 0xffD8560B), true);
        NewPartic(new Explode(X, Y-H/2, 128+128, 0, 60, 0xffD8C10B), true);
        kill.append(SID);
        return;
      }
    }
    cooldown--;
    if (cooldown<30 && attack==0) {
      float R = random(-PI/4, PI/4)-PI/2;
      NewPR(X, Y-H/2, cos(R)*8, sin(R)*8, 7);
    }
    if (cooldown%120==0 && attack==1) {
      NewPR(X, Y, 8, 0, 6);
      NewPR(X, Y, -8, 0, 6);
    }
    if (cooldown==0 && attack==2) {
      NewPR(X, Y-H/2, 0, 0, 8);
    }
    if (cooldown==0 && attack==3) {
      for (int i=0; i<10; i++) {
        NewPR(X, Y-H/2, i*PI/5, 0, 9);
      }
    }
    if (cooldown==0) {
      attack=floor(random(0, 4));
      cooldown=120;
    }
    Fall();
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    Animr.Anim(false, false);
    Animr.DIMG(X, Y, W, H, false, false, 0xffFFFFFF);
    if (attack == 0) {
      fill(255);
    }
    if (attack == 1) {
      fill(0xffFFA600);
    }
    if (attack == 2) {
      fill(0xffFF0000);
    }
    if (attack == 3) {
      fill(0xff00C5FF);
    }
    circle(X, Y-H/2, max(0, map(intro, 0, 120, 64, 0)));
  }
}

class Crab extends AI {
  int cooldown=400;
  float PX=0;
  float PY=0;
  Crab(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=32;
    H=48;//1600
    HP=400;//1000 is too much for you
    T=3;
    Animr = new SelfAnim(EAR.get("Crab"));
  }
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    cooldown--;
    if (cooldown==0) {
      cooldown=400;
      expd(PX, PY, 128, 30, 20, true);
      NewPartic(new Line(X, Y, X, Y-2000, 60, 0xffe8ff00, 5), false);
      NewPartic(new Line(PX, PY, PX, PY-2000, 60, 0xffe8ff00, 5), false);
    }
    Fall();
    if (dist(X, Y, play.X, play.Y)<128) {
      Walk(0.0f, -0.6f, 0.0f);
    }
    Cont(W, H, 15);
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    pushMatrix();
    if (VX<0) {
      scale(-1, 1);
      translate(-X*2, 0);
    }
    Animr.Anim(abs(VX)>0.3f, false);
    Animr.DIMG(X, Y, W, H, abs(VX)>0.3f, false, 0xffFFFFFF);
    popMatrix();
    if (cooldown<255) {
      stroke(0xffe8ff00, 255-cooldown);
      strokeWeight((255-cooldown)/25.5f);
      line(X, Y, X, Y-2000);
      line(PX, PY, PX, PY-2000);
      if (cooldown>=30) {
        PX=play.X;
        PY=play.Y;
      }
      strokeWeight(1);
    }
  }
}

class Piller extends AI {
  boolean Enranged=false;
  int AngyTimer = 300;
  Piller(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=30;
    H=30;
    HP=200;
    T=0;
    Animr = new SelfAnim(EAR.get("Piller"));
  }
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    if (!Enranged) {
      if (play.Gr) {
        if (AngyTimer<300) {
          AngyTimer+=3;
        }
      } else {
        AngyTimer--;
      }
      if (AngyTimer<=0) {
        Enranged=true;
        AngyTimer=100;
      }
    }
    if (Enranged) {
      if (AngyTimer<=0) {
        float R=atan2(play.Y-Y-5, play.X-X);
        for (int i=0; i<3; i++) {
          float Rand1=random(-PI/10, PI/10);
          float Rand2=random(-2, 2);
          NewPR(X, Y-8, cos(R+Rand1)*(12+Rand2), sin(R+Rand1)*(12+Rand2), 11);
        }
        AngyTimer=100;
      }
      AngyTimer--;
    }
    Walk(0.5f, 0.7f, 3);
    Cont(W, H, 35);
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
      fill(0);
      text(AngyTimer, X, Y+30);
    }
    pushMatrix();
    if (VX>0) {
      scale(-1, 1);
      translate(-X*2, 0);
    }
    Animr.Anim(true, false);
    if (Enranged) {
      Animr.DIMG(X, Y, W, H, true, false, 0xffFFAAAA);
      stroke(255, 0, 0);
      noFill();
      circle(X+random(-2, 2), Y-H/2+random(-2, 2), 60);
    } else {
      Animr.DIMG(X, Y, W, H, true, false, 0xffFFFFFF);
    }
    popMatrix();
  }
}

class Nucliy extends AI {
  //oh boy
  Nucliy(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=24;
    H=48;
    HP=90;
  }
  int Cooldown=0;
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
  }
}

class Supply extends AI {
  Supply(float nX, float nY, boolean nM, boolean Boss) {
    X=nX;
    Y=nY;
    M=nM;
    W=36;
    H=36;
    if (Boss) {
      HP=1900;
      W=48;
      H=48;
      BOSSHP.append(HP);
      BOSSID.append(ListAi.size());
    } else {
      HP=260;
    }
    Animr = new SelfAnim(EAR.get("Supply"));
  }
  boolean Enraged=false;
  int delay=300;
  int gun1mode=0;
  int gun1timer=0;
  int gun2mode=0;
  int gun2timer=0;
  public void math(int SID) {
    if (HP<=0) {
      if (Gr || HP<=-1000) {
        AddPartic(5, X, Y, 32, 0, 15, 0xff00FF00, false);
        kill.append(SID);
        for (int all=0; all<ListAi.size(); all++) {
          try {
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Maze")) {
              Maze tmp = (Maze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Laze")) {
              Laze tmp = (Laze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Supply")) {
              Supply tmp = (Supply)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
          }
          catch(Exception e) {
          }
        }
        return;
      }
      VY+=0.2f;
    } else {
      //GUN1
      if (gun1mode==0) {
        if (gun1timer>0) {
          if (gun1timer>10) {
            R1=atan2(play.Y-Y+H/3*2, play.X-X-W/3);
            float[] tmp=Enyscan(R1, true, +W/3, -H/3*2);
            AddPartic(1, X+W/3, Y-H/3*2, tmp[0], tmp[1], 2, color(255, 0, 0), true);
            tmp=Enyscan(R1+(gun1timer-10)/200.0f, true, +H/3, -H/3*2);
            AddPartic(1, X+W/3, Y-H/3*2, tmp[0], tmp[1], 2, color(255, 0, 0), true);
            tmp=Enyscan(R1-(gun1timer-10)/200.0f, true, +H/3, -H/3*2);
            AddPartic(1, X+W/3, Y-H/3*2, tmp[0], tmp[1], 2, color(255, 0, 0), true);
          }
        }
        if (gun1timer==0) {
          float[] tmp=Enyhitscan(R1, 0, true, +W/3, -H/3*2);
          AddPartic(1, X+W/3, Y-H/3*2, tmp[0], tmp[1], 40, color(255, 0, 0), true);
        }
      }
      if (gun1mode==1) {
        R1=atan2(play.Y-Y+H/3*2, play.X-X-W/3);
        if (gun1timer%100==0) {
          NewSPr(new MiniRocket(X+W/3, Y-H/3*2, cos(R1)*2, sin(R1)*2, 0));
          NewSPr(new MiniRocket(X+W/3, Y-H/3*2, cos(R1)*4, sin(R1)*4, 0));
        }
      }
      if (gun1mode==2) {
        R1=atan2(play.Y-Y+H/3*2, play.X-X-W/3);
        if (gun1timer%20==0) {
          NewSPr(new MiniBullet(X+W/3, Y-H/3*2, cos(R1)*4, sin(R1)*4, 0));
        }
      }
      if (gun1timer==0) {
        gun1timer=300;
        gun1mode=round(random(0, 2));
      }
      gun1timer--;
      //GUN2
      if (gun2mode==0) {
        if (gun2timer>0) {
          if (gun2timer>10) {
            R1=atan2(play.Y-Y+H/3*2, play.X-X+W/3);
            float[] tmp=Enyscan(R1, true, -W/3, -H/3*2);
            AddPartic(1, X-W/3, Y-H/3*2, tmp[0], tmp[1], 2, color(255, 0, 0), true);
            tmp=Enyscan(R1+(gun1timer-10)/200.0f, true, -W/3, -H/3*2);
            AddPartic(1, X-W/3, Y-H/3*2, tmp[0], tmp[1], 2, color(255, 0, 0), true);
            tmp=Enyscan(R1-(gun1timer-10)/200.0f, true, -W/3, -H/3*2);
            AddPartic(1, X-W/3, Y-H/3*2, tmp[0], tmp[1], 2, color(255, 0, 0), true);
          }
        }
        if (gun2timer==0) {
          float[] tmp=Enyhitscan(R1, 0, true, -W/3, -H/3*2);
          AddPartic(1, X-W/3, Y-H/3*2, tmp[0], tmp[1], 40, color(255, 0, 0), true);
        }
      }
      if (gun2mode==1) {
        R1=atan2(play.Y-Y+H/3*2, play.X-X+W/3);
        if (gun2timer%100==0) {
          NewSPr(new MiniRocket(X-W/3, Y-H/3*2, cos(R1)*2, sin(R1)*2, 0));
          NewSPr(new MiniRocket(X-W/3, Y-H/3*2, cos(R1)*4, sin(R1)*4, 0));
        }
      }
      if (gun2mode==2) {
        R1=atan2(play.Y-Y+H/3*2, play.X-X+W/3);
        if (gun2timer%20==0) {
          NewSPr(new MiniBullet(X-W/3, Y-H/3*2, cos(R1)*4, sin(R1)*4, 0));
        }
      }
      if (gun2timer==0) {
        gun2timer=300;
        gun2mode=round(random(0, 2));
      }
      gun2timer--;


      VX+=(play.X-X+cos((float)frameCount/40)*128)/10;
      VY+=(play.Y-Y+sin((float)frameCount/40)*32-200)/10;
      VX=VX/10*9;
      VY=VY/10*9;
      if (delay==0) {
        Animr.Action(0);
        Must++;
        NewAI(X, Y, SupplySummon[floor(constrain(abs(randomGaussian()), 0, 2)*2.5f)], true);
        for (int t=0; t<20; t++) {
          AddPartic(3, X, Y, random(-4, 4), random(-4, 4), 120, color(200, 0, 200), true);
        }
        delay=201;
      }
      delay--;
    }
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  float R1=0;
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
      Animr.Anim(false, true);
      Animr.DIMG(X, Y, W, H, false, true, 0xffFFFFFF);
      Animr.EIMG(X+H/3, Y-H/3*2, 16, 16, R1, gun1mode, 0xffFFFFFF);
      Animr.EIMG(X-H/3, Y-H/3*2, 16, 16, R1, gun2mode, 0xffFFFFFF);
    }
  }
}

class Electron extends AI {
  Electron(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=24;
    H=48;
    HP=6000;
    BOSSHP.append(HP);
    BOSSID.append(ListAi.size());
    Animr = new SelfAnim(EAR.get("Electron"));
    for (int i=0; i<16; i++) {
      NewPartic(new Wind(X, Y-H/2, random(-15, 15), random(-15, 15), 120, 0xffFFFFFF), true);
    }
  }
  int Cooldown=300;
  int Shield=1000;
  boolean downed=false;
  int attack=0;
  float attacking=0;
  public void math(int SID) {
    if (HP<=0) {
      NewPartic(new Explode(X, Y-H/2, 150, 0, 40, 0xffEA0C13), true);
      NewPartic(new Explode(X, Y-H/2, 300, 0, 40, 0xffEA0C13), true);
      for (int i=0; i<16; i++) {
        NewPartic(new Wind(X, Y-H/2, random(-15, 15), random(-15, 15), 120, 0xffFFFFFF), true);
      }
      kill.append(SID);
      return;
    } else {
      if (Shield<=0) {
        NewPartic(new Explode(X, Y-H/2, 100, 0, 40, 0xffEA0C13), true);
        for (int i=0; i<4; i++) {
          NewPartic(new Smoke(X, Y-H/2, random(-5, 5), random(-5, 5), 40, 0xff393030, -0.5f), true);
        }
        downed=true;
        Shield=0;
      }
      if (downed) {
        if (Shield==1000) {
          downed=false;
          Cooldown=50;
          attacking=0;
        }
        Shield+=2;
        VY+=0.5f;
      } else {
        if (Cooldown==0 && attacking<=0) {
          attacking=300;
          //attack=round(random(0, 3));
        }
        if (attacking>0) {
          if (attack==0) {
            if (attacking%30==0) {
              for (int i=0; i<8; i++) {
                //NewSPr(new SEletro(X,Y,3,PI/200,400,0,i*PI/4+attacking/20.0));//HARD
                //NewSPr(new SEletro(X,Y,3,-PI/200,400,0,i*PI/4-attacking/20.0));//HARD
                //NewSPr(new SEletro(X,Y,6,PI/200,400,0,i*PI/4+attacking/20.0));//HARD
                //NewSPr(new SEletro(X,Y,6,-PI/200,400,0,i*PI/4-attacking/20.0));//HARD
                NewSPr(new SEletro(X, Y-H/2, 3, PI/400, 400, 0, i*PI/4+attacking/15.0f));
              }
            }
            if ((attacking-15)%30==0) {
              for (int i=0; i<8; i++) {
                NewSPr(new SEletro(X, Y-H/2, 3, -PI/400, 400, 0, i*PI/4+attacking/15.0f));
              }
            }
          }
          if (attack==1) {
            if (attacking%30==0) {
              for (int i=0; i<1; i++) {
                //NewSPr(new Bross(play.X, play.Y-12, 0, 0, 60, round(random(0,7))*PI/8));//HARD
                //NewSPr(new Bross(play.X, play.Y-12, 0, 0, 60, round(random(0,7))*PI/8));//HARD
                //NewSPr(new Bross(play.X, play.Y-12, 0, 0, 60, round(random(0,7))*PI/8));//HARD
                //NewSPr(new Bross(play.X, play.Y-12, 0, 0, 60, round(random(0,7))*PI/8));//HARD
                if (random(0, 1)<0.5f) {
                  NewSPr(new Bross(play.X, play.Y-12, 0, 0, 60, PI/2));
                  NewSPr(new Bross(play.X, play.Y-12, 0, 0, 60, 0));
                } else {
                  NewSPr(new Bross(play.X, play.Y-12, 0, 0, 60, PI/4));
                  NewSPr(new Bross(play.X, play.Y-12, 0, 0, 60, PI/4*3));
                }
              }
            }
          }
          if (attack==2) {
            //if (attacking%5==0) {
            //  for (int i=0; i<3; i++) {
            //    NewSPr(new SEletro(X, Y-H/2, -6, random(-PI/400, PI/400), 400, 1200, random(-PI, PI)));
            //  }
            //}
            //if (attacking%25==0) {
            //    NewSPr(new Bross(play.X+random(-200,200),play.Y-12+random(-200,200),random(-2,2),random(-2,2),60,round(random(0,7))*PI/8));//HARD
            //}
            if (attacking%5==0) {
              for (int i=0; i<2; i++) {
                NewSPr(new SEletro(X, Y-H/2, -6, random(-PI/400, PI/400), 400, 1200, random(-PI, PI)));
              }
            }
          }
          if (attack==3) {
            if (attacking%30==0) {//TRUE 20
              for (int i=0; i<4; i++) {
                //NewSPr(new Bross(play.X+random(-200,200),play.Y-12+random(-200,200),random(-2,2),random(-2,2),60,round(random(0,7))*PI/8));//HARD
                NewSPr(new Bross(play.X+random(-400, 400), play.Y-12+random(-400, 400), 0, 0, 60, round(random(0, 3))*PI/4));
              }
            }
          }
          attacking--;
          if (attacking<=0) {
            Cooldown=300;
            attack++;
            if (attack==4) {
              attack=0;
            }
          }
        } else {
          Cooldown--;
        }
        VX+=(0-X)/10;
        VY+=(-500-Y)/10;
        VX=VX/10*9;
        VY=VY/10*9;
      }
    }
    Phys(W, H, false);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    Animr.Anim(false, !downed);
    Animr.DIMG(X, Y, W, H, false, !downed, 0xffFFFFFF);
    if (!downed) {
      for (int i=0; i<4; i++) {
        Animr.EIMG(X+cos(frameCount/60.0f+PI/2*i)*50, Y+sin(frameCount/60.0f+PI/2*i)*50-H/2, 21, 21, 0, attack, 0xffFFFFFF);
      }
    }
    noStroke();
    fill(0xff7ECCF0, 75);
    circle(X, Y-H/2, 100);
    arc(X, Y-H/2, 100, 100, -PI/2, PI*Shield/500-PI/2);
  }
  public void HURT(int dmg)
  {
    if (!hurte) {
      return;
    }
    if (downed) {
      HP-=dmg;
      for (int B=0; B<5; B++) {
        AddPartic(4, X, Y, random(-1, 1), random(-8, -2), 50, color(255, 0, 0), true);
      }
      if (play.regenera==0) {
        if (dist(X, Y, play.X, play.Y)<=200 && play.HP>0) {
          AddPartic(1, play.X+random(-5, 5), play.Y-12+random(-5, 5), X+random(-5, 5), Y-H/2+random(-5, 5), 60, color(255, 0, 0), true);
          if (play.HP+dmg/4>100) {
            play.HP=100;
          } else {
            play.HP+=dmg/4;
          }
        }
      } else {
        if (random(1, 100)<50 && play.HP>0) {
          NewPR(X, Y-H/2, random(-5, 5), random(-5, 5), 10);
        }
      }
    } else {
      Shield-=dmg;
      for (int B=0; B<5; B++) {
        AddPartic(4, X, Y, random(-8, 8), random(-8, 8), 50, 0xff7ECCF0, true);
      }
    }
  }
}


class Limbo extends AI {
  Limbo(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  int Cooldown=600+(int)random(0, 600);
  ;
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==0){
      NewAI(X+random(-5,5),Y+random(-5,5),LimboSummoners[floor(random(0,LimboSummoners.length))],true);
      Cooldown=600+(int)random(0, 600);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<180) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>180) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5f,0.5f);
    VY=constrain(VY, -2, 2)+random(-0.5f,0.5f);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, 0xff4B2705, -1), true);
    }
    VY-=0.01f;
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    noStroke();
    fill(0xff4B2705);
    circle(X, Y-H/2, H);
    stroke(color(155,0,155),255-Cooldown*0.5f);
    strokeWeight(3);
    if(Cooldown<240){
      line(X+cos( frameCount*1.0f/Cooldown)*24,Y+sin( frameCount*1.0f/Cooldown)*24-W,X-cos( frameCount*1.0f/Cooldown)*24,Y-sin( frameCount*1.0f/Cooldown)*24-W);
      line(X+cos(-frameCount*1.0f/Cooldown)*24,Y+sin(-frameCount*1.0f/Cooldown)*24-W,X-cos(-frameCount*1.0f/Cooldown)*24,Y-sin(-frameCount*1.0f/Cooldown)*24-W);
    }
    strokeWeight(1);
  }
}

class Lust extends AI {
  Lust(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  int Cooldown=200+(int)random(0, 200);
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==0){
      NewSPr(new WindBall(X,Y,-cos(R)*3,-sin(R)*3));
      Cooldown=200+(int)random(0, 200);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<175) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>175) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5f,0.5f);
    VY=constrain(VY, -2, 2)+random(-0.5f,0.5f);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, 0xffD0CFD1, -1), true);
    }
    VY-=0.01f;
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    noStroke();
    fill(0xffD0CFD1);
    circle(X, Y-H/2, H);
    stroke(color(155,0,155),255-Cooldown*0.5f);
  }
}

class Gluttony extends AI {
  Gluttony(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  int Cooldown=100+(int)random(0, 100);
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==0){
      NewSPr(new IceBall(X,Y,-cos(R)*0.5f,-sin(R)*0.5f));
      Cooldown=100+(int)random(0, 100);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<170) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>170) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5f,0.5f);
    VY=constrain(VY, -2, 2)+random(-0.5f,0.5f);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, 0xff57DECD, -1), true);
    }
    VY-=0.01f;
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    noStroke();
    fill(0xff57DECD);
    circle(X, Y-H/2, H);
    stroke(color(155,0,155),255-Cooldown*0.5f);
  }
}

class Greed extends AI {
  Greed(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  int Cooldown=100+(int)random(0, 100);
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==0){
      for(int i=0;i<3;i++){
        float Rand=random(-PI/20,PI/20)+atan2(Y-play.Y+50, X-play.X);
        NewSPr(new Melting(X,Y,-cos(Rand)*5,-sin(Rand)*5));
      }
      Cooldown=100+(int)random(0, 100);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<165) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>165) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5f,0.5f);
    VY=constrain(VY, -2, 2)+random(-0.5f,0.5f);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, 0xffFFE200, -1), true);
    }
    VY-=0.01f;
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    noStroke();
    fill(0xffFFE200);
    circle(X, Y-H/2, H);
    stroke(color(155,0,155),255-Cooldown*0.5f);
  }
}

class Anger extends AI {
  Anger(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  int Cooldown=200+(int)random(0, 200);
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==0){
      NewSPr(new Rage(X,Y,-cos(R)*5,-sin(R)*5));
      Cooldown=200+(int)random(0, 200);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<160) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>160) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5f,0.5f);
    VY=constrain(VY, -2, 2)+random(-0.5f,0.5f);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, 0xff176C0B, -1), true);
    }
    VY-=0.01f;
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    noStroke();
    fill(0xff176C0B);
    circle(X, Y-H/2, H);
    stroke(color(155,0,155),255-Cooldown*0.5f);
  }
}

  class Heresy extends AI {
  Heresy(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  int Cooldown=200+(int)random(0, 200);
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==0){
      NewSPr(new MiniFire(X,Y,0,0));
      Cooldown=200+(int)random(0, 200);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<155) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>155) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5f,0.5f);
    VY=constrain(VY, -2, 2)+random(-0.5f,0.5f);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, 0xffFF9008, -1), true);
    }
    VY-=0.01f;
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    noStroke();
    fill(0xffFF9008);
    circle(X, Y-H/2, H);
    stroke(color(155,0,155),255-Cooldown*0.5f);
  }
}

class Violence extends AI {
  Violence(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  int Cooldown=200+(int)random(0, 200);
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown<45){
      NewPartic(new Wind(X+random(-8, 8), Y-random(0, 16), random(-3, 3), random(-3, 3), 30, 0xffFF0000), true);
    }
    if(Cooldown==0){
      VX=-cos(R)*15;
      VY=-sin(R)*15;
      Cooldown=200+(int)random(0, 200);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<150) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>150) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -10, 10)+random(-0.5f,0.5f);
    VY=constrain(VY, -10, 10)+random(-0.5f,0.5f);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, 0xffFF0000, -1), true);
    }
    VY-=0.01f;
    Phys(W, H, true);
    Cont(W, H, 15);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    noStroke();
    fill(0xffFF0000);
    circle(X, Y-H/2, H);
    stroke(color(255,0,0),255-Cooldown*0.5f);
  }
}

class Hatred extends AI {
  Hatred(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=16;
    H=32;
    hurte=false;
    HP=2147483647;
    Animr = new SelfAnim(EAR.get("Hatred"));
  }
  int Cooldown=300;
  float R=0;
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    if(Cooldown==80){
      NewSPr(new hurtbox(X+cos(R)*1500,Y-H/2+sin(R)*1500,3000,25,R,40,70,5));
    }
    if(Cooldown==75){
      NewSPr(new hurtbox(X+cos(R)*1500,Y-H/2+sin(R)*1500,3000,35,R,40,75,5));
    }
    if(Cooldown==70){
      NewSPr(new hurtbox(X+cos(R)*1500,Y-H/2+sin(R)*1500,3000,45,R,40,80,15));
    }
    if(Cooldown<=0){
      Cooldown=300;
    }
    if(Cooldown>120){
      R=atan2(play.Y-12-Y+H/2, play.X-X);
    }
    Cooldown--;
    //VX=constrain(VX, -2, 2)+random(-0.1,0.1);
    //VY=constrain(VY, -2, 2)+random(-0.1,0.1);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, 0xffFF0000, 1), true);
    }
    //VY-=0.01;
    Phys(W, H, true);
    //X+=VX;
    //Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    Animr.Anim(false, true);
    Animr.DIMG(X, Y, W, H, false, true, 0xffFFFFFF);
    if (Cooldown>120 && Cooldown<300) {
      stroke(0xffFF0000);
      line(X,Y-H/2,X+cos( (Cooldown-120)/240.0f+R)*3000,Y-H/2+sin( (Cooldown-120)/240.0f+R)*3000);
      line(X,Y-H/2,X+cos(-(Cooldown-120)/240.0f+R)*3000,Y-H/2+sin(-(Cooldown-120)/240.0f+R)*3000);
      line(X,Y-H/2,X+cos(R)*3000,Y-H/2+sin(R)*3000);
    }
  }
}

class Fraud extends AI {
  Fraud(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  int Cooldown=200+(int)random(0, 200);
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==0){
      NewPartic(new Line(play.X,play.Y,X,Y,60,0xff222222,5),true);
      AThurt(10);
      HP+=20;
      Cooldown=200+(int)random(0, 200);
      for (int B=0; B<5; B++) {
        NewPartic(new VELLPoint(X, Y, random(-1, 1), random(-8, -2), 50, color(255, 0, 0)), true);
      }
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<145) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>145) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5f,0.5f);
    VY=constrain(VY, -2, 2)+random(-0.5f,0.5f);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, 0xff222222, -1), true);
    }
    VY-=0.01f;
    Phys(W, H, true);
    Cont(W, H, 15);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    noStroke();
    fill(0xff222222);
    circle(X, Y-H/2, H);
    stroke(color(255,0,0),255-Cooldown*0.5f);
  }
  public void HURT(int dmg)
  {
    if (!hurte) {
      return;
    }
    HP-=dmg;
    for (int B=0; B<5; B++) {
      AddPartic(4, X, Y, random(-1, 1), random(-8, -2), 50, color(255, 0, 0), true);
    }
    if (play.regenera==0) {
      if (dist(X, Y, play.X, play.Y)<=200 && play.HP>0) {
        AddPartic(1, play.X+random(-5, 5), play.Y-12+random(-5, 5), X+random(-5, 5), Y-H/2+random(-5, 5), 60, color(255, 0, 0), true);
        if (play.HP+dmg/2>100) {
          play.HP=100;
        } else {
          play.HP+=dmg/2;
        }
      }
    } else {
      if (random(1, 100)<50 && play.HP>0) {
        NewPR(X, Y-H/2, random(-5, 5), random(-5, 5), 10);
      }
    }
  }
}

class Treachery extends AI {
  Treachery(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  float LPX = 0;
  float LPY = 0;
  int Cooldown=200+(int)random(0, 200);
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==30){
      LPX=play.X;
      LPY=play.Y;
    }
    if(Cooldown==0){
      expd(LPX,LPY,128,30,0,true);
      Cooldown=200+(int)random(0, 200);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<140) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>140) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5f,0.5f);
    VY=constrain(VY, -2, 2)+random(-0.5f,0.5f);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, 0xff004444, -1), true);
    }
    VY-=0.01f;
    Phys(W, H, true);
    Cont(W, H, 15);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    if(Cooldown<30){
      stroke(0xff0BD8B4);
      strokeWeight(3);
      noFill();
      circle(LPX,LPY,128);
      circle(LPX,LPY,Cooldown*128/30);
      if(Configs.get("DrawEffects")==1){
        for(int i=0;i<8;i++){
          float R = PI/4*i;
          line(LPX+cos(R)*64,LPY+sin(R)*64,LPX+cos(R)*80,LPY+sin(R)*80);
        }
      }
    }
    noStroke();
    fill(0xff0BD8B4);
    circle(X, Y-H/2, H);
    stroke(color(255,0,0),255-Cooldown*0.5f);
  }
}

class Zenith extends AI {
  Zenith(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY+50;
    M=nM;
    W=50;
    H=100;
    HP=4000;
    LX = new float[4];
    LY = new float[4];
    TX = new float[4];
    TY = new float[4];
    for(int i=0;i<4;i++){
      float R=PI/4+PI/2*i;
      LX[i]=cos(R)*500;
      LY[i]=sin(R)*500;
      TX[i]=cos(R)*500;
      TY[i]=sin(R)*500;
    }
    Animr = new SelfAnim(EAR.get("Zenith"));
    BOSSHP.append(HP);
    BOSSID.append(ListAi.size());
  }
  float[] LX;
  float[] LY;
  float[] TX;
  float[] TY;
  int DeathTimer=0;
  int AttackCooldown=0;
  int Attack=0;
  int lazerAce=0;
  int lazerSpeed=0;
  float lazerOff=0;
  boolean ShootOffset=false;
  float Spyral=0;
  float SpyralOff=0;
  int lastAttack=-1;
  float Deathspiral=0;
  Boolean enraged=false;
  int timealive=0;
  public void math(int SID) {
    //HP=0;//DEBUG
    if (HP<=0) {
      if(timealive>600){
        HP=0;
        DeathTimer++;
        if(DeathTimer>120 && DeathTimer<600){
          Deathspiral+=1;
          if(DeathTimer%30==15 && DeathTimer>300){
            for(int i=0;i<50;i++){
              float R=i*PI/25+Deathspiral*PI/200;
              NewSPr(new DeathShard(X,Y-50,R,10,0));
            }
          }
          if(DeathTimer%30==0){
            for(int i=0;i<50;i++){
              float R=i*PI/25+Deathspiral*PI/100;
              NewSPr(new DeathShard(X,Y-50,R,10,0));
            }
          }
        }
      }else{
        HP=4000;
        enraged=true;
        NewPartic(new ShockWave(X,Y-50,0,0,120,0xffFFFFFF),true);
      }
    }
    if(DeathTimer==660){
      for(int i=0;i<80;i++){
        float R=i*PI/40;
        NewSPr(new Shard(X,Y-50,R,12,0));
        NewSPr(new Shard(X,Y-50,R+PI/80/2,11,0));
        NewSPr(new Shard(X,Y-50,R+PI/80,10,0));
      }
      kill.append(SID);
      return;
    }
    float R=0;
    if(play.HP>0 && HP>0){
      R=atan2(play.Y+50-Y,play.X-X);
    }else{
      R=atan2(50-Y,-X);
    }
    if(AttackCooldown<-120 && HP>0){
      lastAttack = Attack;
      while(Attack==lastAttack){
        Attack = floor(random(0,3));
      }
      //Attack=2;
      if(Attack==0){
        lazerAce=0;
        lazerOff=atan2(Y-play.Y+50,X-play.X);
        AttackCooldown=480;

      }
      if(Attack==1){
        ShootOffset=true;
        AttackCooldown=480;
        for(int i=-2;i<3;i++){
          float Rotate = i*PI/8+R;
          NewPartic(new Line(X,Y-50,X+cos(Rotate)*2000,Y+sin(Rotate)*2000-50,120,0xffFF0000,4),true);
        }
      }
      if(Attack==2){
        Spyral=atan2(Y-play.Y+50,X-play.X);
        AttackCooldown=480;
        SpyralOff=0;
      }
    }
    if(AttackCooldown>=0){
    if(HP>0 && play.HP>0){
      if(Attack==0){
        if(enraged){        
          float Rotate = lazerOff-PI;
          if(AttackCooldown<480-100){
            NewSPr(new hurtbox(X+cos(Rotate+PI/2)*100,Y-50+sin(Rotate+PI/2)*100,4000,35,Rotate,80,10,3));
            NewSPr(new hurtbox(X+cos(Rotate-PI/2)*100,Y-50+sin(Rotate-PI/2)*100,4000,35,Rotate,80,10,3));
            if(AttackCooldown%30==0){
              if(AttackCooldown%60==0){
                NewSPr(new DeathShard(X+cos(Rotate-PI/2)*25,Y-50+sin(Rotate-PI/2)*25,Rotate,12,0));
                NewSPr(new DeathShard(X+cos(Rotate-PI/2)*50,Y-50+sin(Rotate-PI/2)*50,Rotate,12,0));
                NewSPr(new DeathShard(X+cos(Rotate-PI/2)*75,Y-50+sin(Rotate-PI/2)*75,Rotate,12,0));
              }else{
                NewSPr(new DeathShard(X+cos(Rotate+PI/2)*25,Y-50+sin(Rotate+PI/2)*25,Rotate,12,0));
                NewSPr(new DeathShard(X+cos(Rotate+PI/2)*50,Y-50+sin(Rotate+PI/2)*50,Rotate,12,0));
                NewSPr(new DeathShard(X+cos(Rotate+PI/2)*75,Y-50+sin(Rotate+PI/2)*75,Rotate,12,0));
              }
            }
          }else{
            lazerOff=atan2(Y-play.Y-50,X-play.X);
            NewPartic(new Line(X+cos(Rotate)*2000,Y+sin(Rotate)*2000-50,X-cos(Rotate)*2000,Y-sin(Rotate)*2000-50,120,0xffFF0000,4),true);
          }
        }else{
          if(AttackCooldown>240){
            lazerAce++;
          }
          if(AttackCooldown<240){
            lazerAce--;
          }
          lazerSpeed+=lazerAce;
          Float Rotate = lazerSpeed/500.0f*PI/30.0f+lazerOff;
          NewSPr(new hurtbox(X+cos(Rotate)*1000,Y-50+sin(Rotate)*1000,2000,35,Rotate,30,10,3));
          NewPartic(new Line(X-cos(Rotate)*2000,Y-sin(Rotate)*2000-50,X,Y-50,30,0xffFF0000,4),true);
        }
      }
      if(Attack==1 && AttackCooldown%20==0 && AttackCooldown<360){
        if(ShootOffset){
          for(int i=(enraged?-4:-2);i<(enraged?5:3);i++){
            NewSPr(new Shard(X,Y-50,R+i*PI/8,enraged?10:8,0));
          }
        }else{
          for(int i=(enraged?-4:-2);i<(enraged?4:2);i++){
            NewSPr(new Shard(X,Y-50,R+i*PI/8+PI/16,enraged?10:8,0));
          }
        }
        ShootOffset=!ShootOffset;
      }
      if(Attack==2){
        if(AttackCooldown<360){
          SpyralOff+=PI/180;
          NewSPr(new hurtbox(X,Y-50,1000,35,Spyral+SpyralOff+PI/4,30,10,3));
          NewSPr(new hurtbox(X,Y-50,1000,35,Spyral+SpyralOff-PI/4,30,10,3));
          NewSPr(new hurtbox(X+cos(Spyral-SpyralOff+PI/4)*750,Y-50+sin(Spyral-SpyralOff+PI/4)*750,500,35,Spyral-SpyralOff+PI/4,30,10,3));
          NewSPr(new hurtbox(X+cos(Spyral-SpyralOff-PI/4)*750,Y-50+sin(Spyral-SpyralOff-PI/4)*750,500,35,Spyral-SpyralOff-PI/4,30,10,3));
          NewSPr(new hurtbox(X+cos(Spyral-SpyralOff+PI/4*3)*750,Y-50+sin(Spyral-SpyralOff+PI/4*3)*750,500,35,Spyral-SpyralOff+PI/4*3,30,10,3));
          NewSPr(new hurtbox(X+cos(Spyral-SpyralOff-PI/4*3)*750,Y-50+sin(Spyral-SpyralOff-PI/4*3)*750,500,35,Spyral-SpyralOff-PI/4*3,30,10,3));
          if(enraged){
            NewSPr(new hurtbox(X+cos(Spyral+SpyralOff+PI/4)*850,Y-50+sin(Spyral+SpyralOff+PI/4)*850,300,35,Spyral+SpyralOff+PI/4,30,10,3));
            NewSPr(new hurtbox(X+cos(Spyral+SpyralOff-PI/4)*850,Y-50+sin(Spyral+SpyralOff-PI/4)*850,300,35,Spyral+SpyralOff-PI/4,30,10,3));
            NewSPr(new hurtbox(X+cos(Spyral+SpyralOff+PI/4*3)*850,Y-50+sin(Spyral+SpyralOff+PI/4*3)*850,300,35,Spyral+SpyralOff+PI/4*3,30,10,3));
            NewSPr(new hurtbox(X+cos(Spyral+SpyralOff-PI/4*3)*850,Y-50+sin(Spyral+SpyralOff-PI/4*3)*850,300,35,Spyral+SpyralOff-PI/4*3,30,10,3));
            
            NewSPr(new hurtbox(X,Y-50,600,35,Spyral-SpyralOff+PI/4,30,10,3));
            NewSPr(new hurtbox(X,Y-50,600,35,Spyral-SpyralOff-PI/4,30,10,3));
            
            NewSPr(new hurtbox(X,Y-50,2000,35,Spyral-SpyralOff/8+PI/4,30,10,3));
            NewSPr(new hurtbox(X,Y-50,2000,35,Spyral-SpyralOff/8-PI/4,30,10,3));
          }
        }else{
          NewPartic(new Line(X+cos(Spyral+PI/4)*1000,Y+sin(Spyral+PI/4)*1000-50,X-cos(Spyral+PI/4)*1000,Y-sin(Spyral+PI/4)*1000-50,5,0xffFF0000,5),true);
          NewPartic(new Line(X+cos(Spyral-PI/4)*1000,Y+sin(Spyral-PI/4)*1000-50,X-cos(Spyral-PI/4)*1000,Y-sin(Spyral-PI/4)*1000-50,5,0xffFF0000,5),true);
          NewPartic(new Circle(X,Y-50,1000,2,0xffFF0000,5),true);
          NewPartic(new Circle(X,Y-50,2000,2,0xffFF0000,5),true);
        }
      }
    }
    }
    AttackCooldown--;
    if(HP<2500 || enraged){
      if(HP>0){
        float Speed=0;
        if(enraged){
           Speed = map(HP,0,4000,14,6);
        }else{
           Speed = map(HP,0,2500,10,1);
        }
        if(Attack==2 && AttackCooldown<360 && AttackCooldown>0 || Attack==0 && enraged){
          Speed*=enraged?0.0f:0.6f;
        }
        X+=cos(R)*Speed;
        Y+=sin(R)*Speed;
      }else{
        X+=cos(R)*5;
        Y+=sin(R)*5;
      }
      for(int i=0;i<4;i++){
        if(enraged){
          if (LX[i]+20>play.X-6 && LX[i]-20<play.X+6 && LY[i]+20>play.Y-24 && LY[i]-20<play.Y+0) {
            AThurt(25);
          }
        }
        LX[i]=TX[i]*0.1f+LX[i]*0.9f;
        LY[i]=TY[i]*0.1f+LY[i]*0.9f;
        float D = dist(TX[i],TY[i],X,Y);
        if(D>500 || D<150){
          float Ran;
          if(enraged){
            Ran = R+random(-PI/16,PI/16);
          }else{
            Ran = R+random(-PI/4,PI/4);
          }
          TX[i]=X+cos(Ran)*400;
          TY[i]=Y+sin(Ran)*400;
        }
      }
    }
    Cont(W, H, 50);
    timealive++;
  }
  public void render() {
    float R=atan2(play.Y+50-Y,play.X-X);
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    for(int i=0;i<4;i++){
      float Rotate=atan2(LY[i]-Y+50,LX[i]-X);
      for(int u=0;u<18;u++){
        //circle(lerp(X,LX[i],u/10.0),lerp(Y-50,LY[i],u/10.0),8);
        int INDEX = u%2==0?1:0;
        Animr.EIMG(lerp(X,LX[i],u/18.0f),lerp(Y-50,LY[i],u/18.0f),32,32,Rotate,INDEX,0xffFFFFFF);
      }
      Animr.EIMG(LX[i],LY[i],40,40,0,2,enraged?0xffFF0000:0xffFFFFFF);
      if(enraged){
        stroke(0xffFF0000);
        strokeWeight(4);
        line(TX[i],TY[i],LX[i],LY[i]);
        Animr.EIMG(TX[i],TY[i],40,40,0,2,color(0xffFF0000,200));
      }
    }
    Animr.Anim(true, true);
    if(HP>0){
      Animr.DIMG(X, Y, W, H, true, true, enraged?0xffFF0000:0xffFFFFFF);
    }else{
      Animr.DIMG(X+random(-4,4), Y+random(-4,4), W, H, true, true, 0xffFFFFFF);
    }
    noStroke();
    fill(0xffFFFFFF);
    if(HP>0){
      circle(X+cos(R)*7, Y-H/2+sin(R)*7, 15);
    }else{
      circle(X+random(-4,4), Y+random(-4,4)-H/2, 15);
    }
  }
  int HealHits=0;
  public void HURT(int dmg)
  {
    if (!hurte) {
      return;
    }
    HealHits++;
    if(enraged){dmg/=5;}
    HP-=dmg;
    for (int B=0; B<5; B++) {
      AddPartic(4, X, Y, random(-1, 1), random(-8, -2), 50, color(255, 0, 0), true);
    }
    if(HealHits==2){
      NewSPr(new Heal(X,Y-50,0));
      HealHits=0;
    }
    if (play.regenera==0) {
      if (dist(X, Y, play.X, play.Y)<=200 && play.HP>0) {
        AddPartic(1, play.X+random(-5, 5), play.Y-12+random(-5, 5), X+random(-5, 5), Y-H/2+random(-5, 5), 60, color(255, 0, 0), true);
        if (play.HP+dmg/4>100) {
          play.HP=100;
        } else {
          play.HP+=dmg/4;
        }
      }
    } else {
      if (random(1, 100)<50 && play.HP>0) {
        NewPR(X, Y-H/2, random(-5, 5), random(-5, 5), 10);
      }
    }
  }
}

class Servant extends AI {
  Servant(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  float LPX = 0;
  float LPY = 0;
  int Cooldown=200+(int)random(0, 200);
  public void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==30){
      LPX=play.X;
      LPY=play.Y;
    }
    if(Cooldown==0){
      expd(LPX,LPY,128,30,0,true);
      Cooldown=200+(int)random(0, 200);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<140) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>140) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5f,0.5f);
    VY=constrain(VY, -2, 2)+random(-0.5f,0.5f);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, 0xff004444, -1), true);
    }
    VY-=0.01f;
    Phys(W, H, true);
    Cont(W, H, 15);
    X+=VX;
    Y+=VY;
  }
  public void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    if(Cooldown<30){
      stroke(0xff0BD8B4);
      strokeWeight(3);
      noFill();
      circle(LPX,LPY,128);
      circle(LPX,LPY,Cooldown*128/30);
      if(Configs.get("DrawEffects")==1){
        for(int i=0;i<8;i++){
          float R = PI/4*i;
          line(LPX+cos(R)*64,LPY+sin(R)*64,LPX+cos(R)*80,LPY+sin(R)*80);
        }
      }
    }
    noStroke();
    fill(0xff0BD8B4);
    circle(X, Y-H/2, H);
    stroke(color(255,0,0),255-Cooldown*0.5f);
  }
}

class AI {
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
  SelfAnim Animr;
  ;
  int OG=0;
  boolean Ignore;
  boolean hurte=true;
  //AI(float nX,float nY,boolean nM){
  //}
  public void math(int SID) {
  }
  public void render() {
  }
  public void Cont(float W, float H, int dmg) {
    if (X-W<=play.X+6 && X+W>=play.X-6 && Y>=play.Y-24 && Y-H<=play.Y && play.IV==0 && play.HP>0) {
      AThurt(dmg);
      if (X<=play.X) {
        VX-=5;
      } else {
        VX+=5;
      }
    }
  }
  public void Fall() {
    if (Gr==false) {
      if (VY<20) {
        VY+=0.5f;
      }
      OG++;
    } else {
      VX/=2;
      OG=0;
    }
    Gr=false;
  }
  public void Walk(float GS, float AS, float JF) {
    if (Gr==false) {
      if (VY<20) {
        VY+=0.5f;
      }
      if (play.X<X && VX>-7) {
        VX-=AS;
      }
      if (play.X>X && VX<7 ) {
        VX+=AS;
      }
      OG++;
    } else {
      if (play.Y+10<Y && random(0, 100)<5) {
        VY-=JF;
      }
      if (play.X<X && VX>-7) {
        VX-=GS;
      }
      if (play.X>X && VX< 7) {
        VX+=GS;
      }
      if (play.Y>Y) {
        Ignore=true;
      }
      OG=0;
    }
    Gr=false;
  }
  public void Phys(float W, float H, boolean C) {
    SPHYS(X-W+0.01f, Y-0.01f, X-W+VX, Y+VY, C);
    SPHYS(X+W-0.01f, Y-0.01f, X+W+VX, Y+VY, C);
    SPHYS(X-W+0.01f, Y-H+0.01f, X-W+VX, Y-H+VY, C);
    SPHYS(X+W-0.01f, Y-H+0.01f, X+W+VX, Y-H+VY, C);
    if (true) {
      if (Ignore & C) {
        Checkfor();
      }
      if (sphys(X-W+VX, Y+VY, X+W+VX, Y+VY) | sphys(X-W+VX, Y-H+VY, X+W+VX, Y-H+VY)) {
        VY=0;
      }
      if (sphys(X-W+VX, Y+VY, X-W+VX, Y-H+VY) | sphys(X+W+VX, Y+VY, X+W+VX, Y-H+VY)) {
        VX=0;
      }
    }
  }
  public void SPHYS(float T1, float T2, float T3, float T4, boolean C) {
    float[] T;
    T=coll(T1, T2, T3, T4, Ignore || !C);
    if (T[0]>=0 && T[0]<=1) {
      int i = (int)T[2];
      PVector TOplayer=new PVector(play.X-(CSX[i]+CEX[i])/2, play.Y-12-(CSY[i]+CEY[i])/2);
      PVector Normal=new PVector((CSX[i]-CEX[i]), (CSY[i]-CEY[i]));
      if (CSX[i]>CEX[i]) {
        Normal.rotate(-PI/2);
      } else {
        Normal.rotate(PI/2);
      }
      if (Normal.dot(TOplayer)<0 && CT[i]==1) {
        Ignore=true;
      }
      //float R=atan2(CSY[(int)T[2]]-CEY[(int)T[2]],CSX[(int)T[2]]-CEX[(int)T[2]]);
      float R=atan2(CSY[i]-CEY[i], CSX[i]-CEX[i]);
      float NV = VX * cos(R) + VY * sin(R);
      VX = cos(R) * NV * 0.99f;
      VY = sin(R) * NV * 0.99f;
      //float ISB=atan2(Y-H/2-(CSY[(int)T[2]]+CEY[(int)T[2]])/2,X-(CSX[(int)T[2]]+CEX[(int)T[2]])/2);
      R=atan2(CSY[i]-CEY[i], CSX[i]-CEX[i]);
      if (R<0) {
        R+=PI;
      }
      if (Normal.dot(TOplayer)>0 && R>-PI/4) {//its a feature fuck it
        Gr=true;
      }
      //Gr=true;
    }
  }
  public boolean sphys(float T1, float T2, float T3, float T4) {
    float[] T;
    T=coll(T1, T2, T3, T4, Ignore);
    if (T[0]>0 && T[0]<1) {
      return true;
    }
    return false;
  }
  public void HURT(int dmg)
  {
    if (!hurte) {
      return;
    }
    HP-=dmg;
    for (int B=0; B<5; B++) {
      AddPartic(4, X, Y, random(-1, 1), random(-8, -2), 50, color(255, 0, 0), true);
    }
    if (play.regenera==0) {
      if (dist(X, Y, play.X, play.Y)<=200 && play.HP>0) {
        AddPartic(1, play.X+random(-5, 5), play.Y-12+random(-5, 5), X+random(-5, 5), Y-H/2+random(-5, 5), 60, color(255, 0, 0), true);
        if (play.HP+dmg/4>100) {
          play.HP=100;
        } else {
          play.HP+=dmg/4;
        }
      }
    } else {
      if (random(1, 100)<50 && play.HP>0) {
        NewPR(X, Y-H/2, random(-5, 5), random(-5, 5), 10);
      }
    }
  }

  public float[] Enyhitscan(float R, int dmg, boolean lazer, float Offx, float Offy) {
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
        if (!lazer) {
          MT=min(MT, min(T1, T2));
          float[] tmp = {play.X+cos(R)*MT, play.Y-12+sin(R)*MT};
          return tmp;
        }
      }
    }
    float[] tmp = {X+Offx+cos(R)*MT, Y+Offy+sin(R)*MT};
    return tmp;
  }

  public float[] Enyscan(float R, boolean lazer, float Offx, float Offy) {
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
        if (!lazer) {
          MT=min(MT, min(T1, T2));
          float[] tmp = {play.X+cos(R)*MT, play.Y-12+sin(R)*MT};
          return tmp;
        }
      }
    }
    float[] tmp = {X+Offx+cos(R)*MT, Y+Offy+sin(R)*MT};
    return tmp;
  }
  public void Checkfor() {//find better sulucion
    int buffer=3;
    if (!(Checkforsub(X-W/2, Y+buffer, X-W/2, Y-H-buffer)||
      Checkforsub(X+W/2, Y+buffer, X+W/2, Y-H-buffer)||
      Checkforsub(X-W/2-buffer, Y, X+W/2+buffer, Y)||
      Checkforsub(X-W/2+buffer, Y-H, X+W/2+buffer, Y-H))) {
      Ignore=false;
    }
  }
  public boolean Checkforsub(float SX, float SY, float EX, float EY) {
    int[] list=CB(SX, SY, EX, EY);
    for (int e=0; e<list.length; e++) {
      int i=list[e];
      float t;
      float r;
      t=TLineToLine(SX, SY, EX, EY, CSX[i], CSY[i], CEX[i], CEY[i]);
      r=TLineToLine(CSX[i], CSY[i], CEX[i], CEY[i], SX, SY, EX, EY);
      float T=1;
      if (t<T && t>0 && r<=1 && r>=0) {
        T=t;
        return true;
      }
    }
    return false;
  }
}

public boolean NewAI(float X, float Y, String T, boolean M) {
  //Need to figure a better way of doin this
  switch(T) {//
  case "Bug":
    ListAi.add(new Bug(X, Y, M));
    break;
  case "Fly":
    ListAi.add(new Fly(X, Y, M));
    break;
  case "Target":
    ListAi.add(new Target(X, Y, M));
    break;
  case "Spewer":
    ListAi.add(new Spewer(X, Y, M));
    break;
  case "testBoss":
    ListAi.add(new testBoss(X, Y, M));
    break;
  case "Maze":
    ListAi.add(new Maze(X, Y, M, false));
    break;
  case "Laze":
    ListAi.add(new Laze(X, Y, M, false));
    break;
  case "Maze_Boss":
    ListAi.add(new Maze(X, Y, M, true));
    break;
  case "Laze_Boss":
    ListAi.add(new Laze(X, Y, M, true));
    break;
  case "tower":
    ListAi.add(new Tower(X, Y, M));
    break;
  case "napalm":
    ListAi.add(new Napalm(X, Y, M));
    break;
  case "Spirit":
    ListAi.add(new Spirit(X, Y, M));
    break;
  case "Guardian":
    ListAi.add(new Guardian(X, Y, M));
    break;
  case "Crab":
    ListAi.add(new Crab(X, Y, M));
    break;
  case "Piller":
    ListAi.add(new Piller(X, Y, M));
    break;
  case "Supply":
    ListAi.add(new Supply(X, Y, M, false));
    break;
  case "Supply_Boss":
    ListAi.add(new Supply(X, Y, M, true));
    break;
  case "Electron":
    ListAi.add(new Electron(X, Y, M));
    break;
  case "Limbo":
    ListAi.add(new Limbo(X, Y, M));
    break;
  case "Lust":
    ListAi.add(new Lust(X, Y, M));
    break;
  case "Gluttony":
    ListAi.add(new Gluttony(X, Y, M));
    break;
  case "Greed":
    ListAi.add(new Greed(X, Y, M));
    break;
  case "Anger":
    ListAi.add(new Anger(X, Y, M));
    break;
  case "Heresy":
    ListAi.add(new Heresy(X, Y, M));
    break;
  case "Hatred":
    ListAi.add(new Hatred(X, Y, M));
    break;
  case "Violence":
    ListAi.add(new Violence(X, Y, M));
    break;
  case "Fraud":
    ListAi.add(new Fraud(X, Y, M));
    break;
  case "Treachery":
    ListAi.add(new Treachery(X, Y, M));
    break;
  case "Zenith":
    ListAi.add(new Zenith(X, Y, M));
    break;
  default:
    return false;
  }
  return true;
}

public float[] coll(float OX, float OY, float NX, float NY, boolean Ignore) {
  for (int i=0; i<CSX.length; i++) {//OH god this is terrible
    float T=100;
    float N=0;
    float t;
    float r;

    t=TLineToLine(OX, OY, NX, NY, CSX[i], CSY[i], CEX[i], CEY[i]);
    r=TLineToLine(CSX[i], CSY[i], CEX[i], CEY[i], OX, OY, NX, NY);
    for (int u=0; u<4; u++) {
      if (t<=T && t>=0 && r<=1 && r>=0 && !(Ignore && CT[i]==1)) {
        {
          T=t;
          N=u;
        }
      }
    }
    if (T>=0 && T<=1) {
      float[] tmp = {T, N, i};
      return tmp;
    }
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
      Frames = (PImage[])append(Frames,SloadImage("Maps/"+Mapinfo.Name+"/"+(sub)+(u)+".png"));
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
  File WATFFEA = new File(sketchPath()+"/data/Pro");
  for(int i=0;i<WATFFEA.list().length;i++){
    File tmper = new File(sketchPath()+"/data/Pro/"+WATFFEA.list()[i]);
    if(tmper.list().length>0){
      tmper = new File(sketchPath()+"/data/Pro/"+WATFFEA.list()[i]+"/file.SFF");
      if(tmper.exists()){
        proANIM = (ProANIMG[])append(proANIM,new ProANIMG(WATFFEA.list()[i]));
      }
    }
  }
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

class SelfAnim{
  int frame=0;
  int timer=0;
  int delay=0;
  boolean Action=false;
  int Acting=0;
  int ID=0;
  SelfAnim(int ID){
    this.ID=ID;
    delay=enANIM[ID].delay;
  }
  public void Anim(boolean move,boolean air){
    timer++;
    if(timer>delay){
      timer=0;
      frame++;
    }
    if(!Action){
      if(frame>=getM(move,air)){
        frame=0;
      }
    }else{
      if(frame>=enANIM[ID].Actions[Acting].length){
        frame=0;
        Action=false;
      }
    }
    //println(frame);
  }
  public void Action(int todo){
    Action=true;
    Acting=todo;
    frame=0;
  }
  public void DIMG(float X,float Y,float w,float h,boolean moveing,boolean Airborn,int C){
    tint(C);
    if(Action){
      image(enANIM[ID].Actions[Acting][frame],X-w,Y-h,w*2,h);
    }else{
      if(Airborn){
        image(enANIM[ID].Air[frame],X-w,Y-h,w*2,h);
      }else if(moveing){
        image(enANIM[ID].Move[frame],X-w,Y-h,w*2,h);
      }else{
        image(enANIM[ID].Stand[frame],X-w,Y-h,w*2,h);
      }
    }
    noTint();
  }
  public void EIMG(float X,float Y,float w,float h,float r,int frame,int C){
    tint(C);
    pushMatrix();
    translate(X,Y);
    rotate(r);
    image(enANIM[ID].Extras[frame],-w/2,-h/2,w,h);
    popMatrix();
    noTint();
  }
  public int getM(boolean mov,boolean air){
    if(air){
      return enANIM[ID].Air.length;
    }else if(mov){
      return enANIM[ID].Move.length;
    }else{
      return enANIM[ID].Stand.length;
    }
  }
}

class EnANIMG{
  int delay;
  PImage[] Air;
  PImage[] Move;
  PImage[] Stand;
  PImage[][] Actions;
  PImage[] Extras;
  EnANIMG(String file){
    Air = new  PImage[0];
    Move = new  PImage[0];
    Stand = new  PImage[0];
    Actions = new PImage[0][0];//Oh no
    Extras = new PImage[0];
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
        U = BgetI(DATA,header,2);
    header+=2;
    for(int i=0;i<U;i++){
      Extras = (PImage[])append(Extras,SloadImage("Hostiles/"+file+"/extra"+i+".png"));
    }
        U = BgetI(DATA,header,2);
    header+=2;
    for(int i=0;i<U;i++){
      int UU = BgetI(DATA,header,2);
      header+=2;
      Actions = (PImage[][])append(Actions,new PImage[0]);
      for(int ii=0;ii<UU;ii++){
        Actions[i] = (PImage[])append(Actions[i],SloadImage("Hostiles/"+file+"/action"+ii+i+".png"));
      }
    }
  }
}

public PImage SloadImage(String path){
  if(Secret!=null){
    return Secret;
  }
  for(int i=0;i<Packs.length;i++){
    if(!Packs[i].Enabled){continue;}
    File IS = new File(sketchPath()+"/Data/Packs/"+Packs[i].internalName+"/"+path);
    if(IS.exists()){
      PImage img=loadImage(IS.getAbsolutePath());
      if(img!=null){
        return img;
      }
    }
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
      switch(removefirst(tmp.getName())){
        case "data/Misc/sav":
        case "data/Misc/config.json":
        continue;
      }
      byte[] shit = new byte[0];
      while(open.available()>0){
        byte[] out = new byte[1024];
        int readed = open.read(out);
        out = subset(out,0,readed);
        shit = concat(shit,out);
      }
      //saveBytes(sketchPath()+"/"+removefirst(tmp.getName()),shit);
    }
    opener.close();
  }catch(Exception e){
    e.printStackTrace();
  }
  zipfile.delete();
  launch(sketchPath()+"/ProjectDF.exe");
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
int consoleSell=-1;

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
  textAlign(LEFT,TOP);
  noStroke();
  fill(0xff00FF00,200);
  rect(0,0,width,20*15);
  fill(100);
  rect(width-10,PApplet.parseFloat(scrool)/80*20*13,10,20*2);
  fill(0xffFF00FF);
  for(int i=scrool;i<20+scrool;i++){
    try{
    text(Console[i],10,19*15-15*i+scrool*15);
    }catch(Exception e){/*ignore*/}
  }
  fill(0xffFFFF00,200);
  rect(0,20*15,width,15);
  fill(0xff0000FF);
  text(ConsoleInput,10,20*15);
  float textmax=textWidth(ConsoleInput);
  stroke(0xff0000FF);
  line(textmax+10,20*15,textmax+10,21*15);
  rect(2,20*15+2-consoleSell*15+scrool*15-15,6,10);
}

String[] Confunc = {
"help:posts this",
"resurect:resurect the player hopefully...",
"noclip:noclip",
"map:switch maps",
"clean:cleans the console",
"cum:roll the d20",
"debugdraw:toggles the visabilaty of many stuff",
"god:god",
"notime:its buddah the thing were you take damage but dont die",
"tmp:mhhhh",
"phys:toggle phys",
"maps:lists all maps in the maps folder",
"nextwave:nextwave",
"gotowave:gotowave",
"restart:restarts the player",
"error:fake error maker",
"tantsumon:summon a enemy in a tant summon point",
"text:make a funny text",
"dark:enable disable Darken"
};

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
        play.HD=0;
        play.IV=60;
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
    case "dark":
      DarkenActive = !DarkenActive;
    break;
    case "hurt":
      if(args.length<2){
        PrintCon("expected more arguments");
        break;
      }
      AThurt(PApplet.parseInt(args[1]));
      //play.HP -= int();
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
IntDict PartTextName;
PImage[] PartImgs;

public void PartINIC(){
  PartImgs = new PImage[0];
  PartTextName = new IntDict(0);
  File WATFFEA = new File(sketchPath()+"/data/Particles");
  PartImgs = (PImage[])append(PartImgs,SloadImage("Misc/mising.png"));
  PartTextName.add("mising.png",0);
  for(int i=0;i<WATFFEA.list().length;i++){
    //File tmper = new File(sketchPath()+"/data/Particles/"+WATFFEA.list()[i]);
    PartImgs = (PImage[])append(PartImgs,SloadImage(sketchPath()+"/data/Particles/"+WATFFEA.list()[i]));
    PartTextName.add(WATFFEA.list()[i],PartTextName.size());
  }
}

class Line extends Effect{
  Line(float nX,float nY,float nVX,float nVY,int ntime,int nC,float Weight){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
    this.Weight=Weight;
  }
  float Weight=5;
  public void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    //nothing!!
    time--;
  }
  public void drawE(){
    strokeWeight((float)time*(float)Weight/(float)Mtime);
    stroke(C,(float)time*(float)255/(float)Mtime);
    line(X,Y,VX,VY);
  }
}

class GravPoint extends Effect{
  float gravmult=1;
  GravPoint(float nX,float nY,float nVX,float nVY,int ntime,int nC,float gravmult){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
    this.gravmult=gravmult;
  }
  public void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    VY+=0.2f*gravmult;
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
  float scale;
  SubText(float nX,float nY,float nVX,float nVY,int ntime,int nC,String ntext,float scale){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
    text=ntext;
    this.scale=scale;
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
    textSize(scale);
    text(text,X,Y);
    textSize(12);
  }
}

class StandImg extends Effect{
  StandImg(float nX,float nY,float nVX,float nVY,int ntime,int nC,String ImgName){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
    theImgID=PartTextName.get(ImgName,0);
  }
  int theImgID=0;
  public void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    X+=VX;
    Y+=VY;
    time--;
  }
  public void drawE(){
    //fill(C);
    tint(C,PApplet.parseFloat(time*255)/Mtime);
    image(PartImgs[theImgID],X,Y);
    noTint();
  }
}

class ShockWave extends Effect{
  ShockWave(float nX,float nY,float nVX,float nVY,int ntime,int nC){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
    theImgID=PartTextName.get("Shockwave.png",0);
  }
  int theImgID=0;
  float scale=0;
  public void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    X+=VX;
    Y+=VY;
    scale+=20;
    time--;
  }
  public void drawE(){
    //fill(C);
    tint(C,PApplet.parseFloat(time*255)/Mtime);
    image(PartImgs[theImgID],X-scale/2,Y-scale/2,scale,scale);
    noTint();
  }
}

class Smoke extends Effect{
  float gravmult=1;
  Smoke(float nX,float nY,float nVX,float nVY,int ntime,int nC,float gravmult){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
    this.gravmult=gravmult;
  }
  public void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    NewPartic(new GravPoint(X,Y,0,0,Mtime,C,-1),true);
    VY+=1*gravmult;
    X+=VX;
    Y+=VY;
    time--;
  }
  public void drawE(){
  }
}

class Circle extends Effect{
  Circle(float nX,float nY,float nR,int ntime,int nC,float Weight){
    X=nX;
    Y=nY;
    VX=nR;
    VY=0;
    time=ntime;
    Mtime=ntime;
    C=nC;
    this.Weight=Weight;
  }
  float Weight=5;
  public void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    //nothing!!
    time--;
  }
  public void drawE(){
    strokeWeight((float)time*(float)Weight/(float)Mtime);
    stroke(C,(float)time*(float)255/(float)Mtime);
    noFill();
    circle(X,Y,VX);
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
      ListEffects.add(new Line(X,Y,VX,VY,time,C,5));
    break;
    case 2:
      ListEffects.add(new GravPoint(X,Y,VX,VY,time,C,1));
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
      ListEffects.add(new SubText(X,Y,VX,VY,time,C,"",24));
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
          consoleSell=-1;
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
        case UP:
          consoleSell++;
          consoleSell=min(consoleSell,99);
          ConsoleInput=Console[consoleSell];
          key=0;
        break;
        case DOWN:
          consoleSell--;
          consoleSell=max(consoleSell,0);
          ConsoleInput=Console[consoleSell];
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
float PSX;
float PSY;
float NMOX=0;
float NMOY=0;
PGraphics Background;
_Mapinfo Mapinfo;

class _Mapinfo{
  _Mapinfo(){
   IsBackModel=true;
   IsBackTexture=false;
   ModelName = null;
   Name = "";
   Model=null;
  }
  //VERSION 1
  String Name;
  PShape Model;
  //VERSION 2
  boolean IsBackModel=true;
  boolean IsBackTexture=false;
  String ModelName;
  String TextureName;
  boolean IsShader;
  PImage Texture;
  PShader Shader;
}

public void openMap(String MAP) {
  ListAi = new ArrayList<AI>();
  ListEffects = new ArrayList<Effect>();
  ListPR = new ArrayList<PRO>();
  kill = new IntList();
  Ekill = new IntList();
  killPR = new IntList();
  Propredirect = new IntDict();
  Background = createGraphics(width, height, P3D);
  byte[] DATA;
  try {
    DATA = loadBytes("Maps/"+MAP+".BM");
  }
  catch(Exception e) {
    PrintCon("???");
    PrintCon(e.getMessage()+"");
    ErrorTimer=120;
    Gaming=false;
    MenuTurnOffAll();
    MenuTurnOn("MAIN_MENU");
    return;
  }
  if (DATA == null) {
    PrintCon("map " + MAP + " seams to not exist");
    ErrorTimer=120;
    Gaming=false;
    return;
  }
  int Version=BgetI(DATA, 0, 2);
  int Header=2;
  Mapinfo = new _Mapinfo();
  if(Version>1){
    //CUSTOM NAME
    int StringSize=BgetI(DATA, Header, 2);
    Header+=2;
    String tmper=BgetS(DATA, Header, StringSize);
    if(tmper!=""){
      Mapinfo.Name = tmper;
    }else{
      Mapinfo.Name = MAP;
    }
    Header+=StringSize;
    //ISBACK
    int num=BgetI(DATA, Header, 1);
    Header+=1;
    Mapinfo.IsBackModel = num==1;
    if(num==1){
      StringSize=BgetI(DATA, Header, 2);
      Header+=2;
      tmper=BgetS(DATA, Header, StringSize);
      Header+=StringSize;
      if(tmper!=""){
        try {
          Mapinfo.Model = loadPly("Maps/"+tmper+".ply");
        }
        catch(Exception e) {
          PrintCon("no background detected");
          PrintCon(e.getMessage()+"");
          ErrorTimer=120;
          Mapinfo.IsBackModel=false;
        }
      }else{
        try {
          Mapinfo.Model = loadPly("Maps/"+MAP+".ply");
        }
        catch(Exception e) {
          PrintCon("no background detected");
          PrintCon(e.getMessage()+"");
          ErrorTimer=120;
          Mapinfo.IsBackModel=false;
        }
      }
    }
    //ISTEXTURE
    num=BgetI(DATA, Header, 1);
    Header+=1;
    Mapinfo.IsBackTexture = num==1;
    if(num==1){
      StringSize=BgetI(DATA, Header, 2);
      Header+=2;
      tmper=BgetS(DATA, Header, StringSize);
      Header+=StringSize;
      Mapinfo.TextureName=tmper;
      if(tmper!=""){
        if (!tmper.endsWith(".glsl")) {
          Mapinfo.Texture = SloadImage("Textures/"+tmper);
        } else {
          Mapinfo.Shader = loadShader("Textures/"+tmper);
          Mapinfo.IsShader=true;
          Mapinfo.Shader.set("WIDTH",width);
          Mapinfo.Shader.set("HEIGHT",height);
        }
      }else{
        Mapinfo.IsBackTexture=false;
      }
    }
  }else{
    Mapinfo.Name = MAP;
    try {
      Mapinfo.Model = loadPly("Maps/"+MAP+".ply");
      Mapinfo.IsBackModel=true;
    }
    catch(Exception e) {
      PrintCon("no background detected");
      PrintCon(e.getMessage()+"");
      ErrorTimer=120;
      Mapinfo.IsBackModel=false;
    }
  }
  int num=BgetI(DATA, Header, 2);
  Header+=2;
  CSX = new float[num];
  CSY = new float[num];
  CEX = new float[num];
  CEY = new float[num];
  CT = new byte[num];
  for (int i=0; i<num; i++) {
    CSX[i] = BgetI(DATA, 0 +Header, 2);
    CSY[i] = BgetI(DATA, 2 +Header, 2);
    CEX[i] = BgetI(DATA, 4 +Header, 2);
    CEY[i] = BgetI(DATA, 6 +Header, 2);
    CT[i] = (byte)BgetI(DATA, 8 +Header, 2);
    Header+=10;
  }

  num=BgetI(DATA, Header, 2);
  Header+=2;
  TX = new float[num];
  TY = new float[num];
  TW = new float[num];
  TH = new float[num];
  TT = new int[num];
  TE = new boolean[num];
  for (int i=0; i<num; i++) {
    TX[i] = BgetI(DATA, 0 +Header, 2);
    TY[i] = BgetI(DATA, 2 +Header, 2);
    TW[i] = BgetI(DATA, 4 +Header, 2);
    TH[i] = BgetI(DATA, 6 +Header, 2);
    TT[i] = BgetI(DATA, 8 +Header, 2);
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
    ET = append(ET, (yes[BgetI(DATA, 4 +Header, 2)]));
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
  num=BgetI(DATA, Header, 2);
  Header+=2;
  PROPL = new PROP[num];
  for (int i=0; i<num; i++) {
    PROPL[i] = new PROP(PApplet.parseFloat(BgetI(DATA, 0 +Header, 2)), PApplet.parseFloat(BgetI(DATA, 2 +Header, 2)), yes[BgetI(DATA, 4 +Header, 2)]);
    Header+=6;
  }
  num=BgetI(DATA, Header, 2);
  Header+=2;
  MAD = new door[num];
  for (int i=0; i<num; i++) {
    float SX=BgetI(DATA, 0 +Header, 2);
    float SY=BgetI(DATA, 2 +Header, 2);
    float EX=BgetI(DATA, 4 +Header, 2);
    float EY=BgetI(DATA, 6 +Header, 2);
    int delay=BgetI(DATA, 8 +Header, 2);
    int ATcol=BgetI(DATA, 10+Header, 2);
    int ATpro=BgetI(DATA, 12+Header, 2);
    MAD[i] = new door(SX, SY, EX, EY, delay, ATcol, ATpro);
    Header+=14;
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
  NUM=BgetI(DATA, Header, 2);
  Header+=2;
  WALLs = new Wall[0];
  for (int i=0; i<NUM; i++) {
    int tmp = BgetI(DATA, Header, 2);
    Header+=2;
    WALLs = (Wall[])append(WALLs, new Wall(yes[tmp]));
    WALLs[i].Offx = BgetI(DATA, Header+0, 2);
    WALLs[i].Offx = BgetI(DATA, Header+2, 2);
    Header+=4;
    num = BgetI(DATA, Header, 2);
    Header+=2;
    for (int u=0; u<num; u++) {
      WALLs[i].x = append(WALLs[i].x, BgetI(DATA, Header+0, 2));
      WALLs[i].y = append(WALLs[i].y, BgetI(DATA, Header+2, 2));
      Header+=4;
    }
  }
  DATA=null;
  //
  for (int i=0; i<ET.length; i++) {
    if (ET[i].equals("playerS")) {
      PSX=EX[i];
      PSY=EY[i];
    }
  }
  int U = TX.length;
  CBE = new boolean[U];
  for (int i=0; i<U; i++) {
    CBE[i] = true;
  }
  setupProps();
  setupBoxs();
}

Wall[] WALLs;

class Wall {
  PImage img;
  PShader shader;
  String Name;
  boolean IsShader=false;
  float[] x;
  float[] y;
  float Offx;
  float Offy;
  Wall(String FileName) {
    Name=FileName;
    if (!FileName.endsWith(".glsl")) {
      img = SloadImage("Textures/"+FileName);
    } else {
      shader = loadShader("Textures/"+FileName);
      IsShader=true;
      shader.set("WIDTH",(float)width);
      shader.set("HEIGHT",(float)height);
    }
    x = new float[0];
    y = new float[0];
  }
  //void change(String FileName){
  //  img = loadImage(FileName);
  //  Name=FileName;
  //}
}

boolean[] CBE;

public void Restart() { 
  TE=CBE.clone();
  ListAi = new ArrayList<AI>();
  ListEffects = new ArrayList<Effect>();
  ListPR = new ArrayList<PRO>();
  kill = new IntList();
  Ekill = new IntList();
  killPR = new IntList();
  BOSSHP.clear();
  BOSSID.clear();
  DOORSOP=false;
}

public void trigger() {
  if (play.HP>0) {
    for (int i=0; i<TX.length; i++) {
      if (play.X>TX[i] && play.X<TX[i]+TW[i] && play.Y>TY[i] && play.Y<TY[i]+TH[i] && TE[i]) {
        switch(TT[i]) {
        case 0:
          AThurt(25);
          break;
        case 1:
          for (int u=0; u<EX.length; u++) {
            if (ET[u]=="playerE") {
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
          for (int u=0; u<EX.length; u++) {
            if (ES[u]==i) {
              NewAI(EX[u], EY[u], ET[u], EM[u]);
              if (EM[u]==true) {
                Must++;
              }
              for (int t=0; t<10; t++) {
                AddPartic(3, EX[u], EY[u], random(-2, 2), random(-2, 2), 100, color(155, 0, 155), true);
              }
            }
          }
          break;
        case 3:
          TE[i]=false;
          CBE=TE.clone();
          for (int u=0; u<EX.length; u++) {
            if (ES[u]==i) {
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
          for (int u=0; u<ET.length; u++) {
            if (ET[u].equals("playerC") && ES[u]==i) {
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
  if (PMust!=Must && PMust!=0) {
    DOORSOP=false;
  }
}

public void Atrigger() {
  for (int i=0; i<TX.length; i++) {
    for (int u=0; u<ListAi.size(); u++) {
      AI tmp = ListAi.get(u);
      if (tmp.X>TX[i] && tmp.X<TX[i]+TW[i] && tmp.Y>TY[i] && tmp.Y<TY[i]+TH[i] && TE[i]) {
        switch(TT[i]) {
        case 0:
          ListAi.get(u).HURT(25);
          break;
        case 5:
          ListAi.get(u).HURT(999999999);
          break;
        case 6:
          float vx=0;
          float vy=0;
          for (int o=0; o<ET.length; o++) {
            if (ET[o].equals("playerC") && ES[o]==i) {
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
public int BgetI(byte[] DATA, int index, int size) {
  String num="";
  int Num=0;
  for (int i=size-1; i>-1; i--) {
    num+=binary(DATA[i+index]);
  }
  Num=0;
  if (num.charAt(0)=='1') {
    Num=(int)pow(-2, (size*8)-1);
  }
  for (int i=1; i<num.length(); i++) {
    if (num.charAt(i)=='1') {
      Num+=(int)pow(2, (size*8)-i-1);
    }
  }
  return Num;
}

public byte[] BsetI(int num, int size) {
  String Num = binary(num);
  byte[] DATA = new byte[size];
  for (int i=0; i<size; i++) {
    String subData="";
    for (int u=0; u<8; u++) {
      subData+=Num.charAt(i*8+u+(4-size)*8);
    }
    DATA[i]=PApplet.parseByte(unbinary(subData));
  }
  return DATA;
}

public String BgetS(byte[] DATA, int index, int size) {
  String num="";
  for (int i=0; i<size; i++) {
    num+=PApplet.parseChar(DATA[i+index]);
  }
  return num;
}

boolean DOORSOP=false;

//OPEN>><<CLOSE

door[] MAD;

public void doorM() {
  for (int i=0; i<MAD.length; i++) {
    MAD[i].Math();
  }
}

class door {
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
  door(float nSX, float nSY, float nEX, float nEY, int nMimer, int nCool, int nProp) {
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
  public void Math() {
    if (DOORSOP) {
      if (timer>0) {
        timer--;
      }
    } else {
      if (timer<Mimer) {
        timer++;
      }
    }
    CSX[Cool]=lerp(EX, SX, PApplet.parseFloat(timer)/Mimer)-OCSX;
    CSY[Cool]=lerp(EY, SY, PApplet.parseFloat(timer)/Mimer)-OCSY;
    CEX[Cool]=lerp(EX, SX, PApplet.parseFloat(timer)/Mimer)-OCEX;
    CEY[Cool]=lerp(EY, SY, PApplet.parseFloat(timer)/Mimer)-OCEY;
    PROPL[Prop].X=lerp(EX, SX, PApplet.parseFloat(timer)/Mimer)-OPX;
    PROPL[Prop].Y=lerp(EY, SY, PApplet.parseFloat(timer)/Mimer)-OPY;
    updBx(Cool);
  }
}

public PShape loadPly(String filepath) {
  try{
  PShape obj;
  String[] info=null;
  for(int i=0;i<Packs.length;i++){
    if(!Packs[i].Enabled){continue;}
    File IS = new File(sketchPath()+"/Data/Packs/"+Packs[i].internalName+"/"+filepath);
    if(IS.exists()){
      info = loadStrings(IS.getAbsolutePath());
    }
  }
  if(info==null){
    info = loadStrings(filepath);
  }
  if(info==null){
    PrintCon("error opening model");
    PrintCon("model is empty");
    ErrorTimer=120;
    return null;
  }
  int header=0;
  int vertexCount=0;
  int faceCount=0;
  PVector[] vertexs = new PVector[0];
  PVector[] normal = new PVector[0];
  int[] colors = new int[0];
  while (!(info[header].equals("end_header"))) {
    String[] arg = split(info[header], ' ');
    if (arg[0].equals("element")) {
      if (arg[1].equals("vertex")) {
        vertexCount = PApplet.parseInt(arg[2]);
      }
      if (arg[1].equals("face")) {
        faceCount = PApplet.parseInt(arg[2]);
      }
    }
    header++;
  }
  header++;
  for (int i=0; i<vertexCount; i++) {
    String[] arg = split(info[header], ' ');
    vertexs = (PVector[])append(vertexs, new PVector(PApplet.parseFloat(arg[0]), PApplet.parseFloat(arg[1]), PApplet.parseFloat(arg[2])));
    normal  = (PVector[])append(normal, new PVector(PApplet.parseFloat(arg[3]), PApplet.parseFloat(arg[4]), PApplet.parseFloat(arg[5])));
    colors  = (int[]  )append(colors, color  (PApplet.parseFloat(arg[6]), PApplet.parseFloat(arg[7]), PApplet.parseFloat(arg[8])));
    header++;
  }
  obj = createShape(GROUP);
  for (int i=0; i<faceCount; i++) {
    PShape tmp = createShape();
    String[] arg = split(info[header], ' ');
    tmp.beginShape();
    tmp.noStroke();
    for (int u=0; u<PApplet.parseInt(arg[0]); u++) {
      PVector V=vertexs[PApplet.parseInt(arg[u+1])];
      PVector N=normal[PApplet.parseInt(arg[u+1])];
      tmp.fill(colors[PApplet.parseInt(arg[u+1])]);
      tmp.normal(N.x, N.y, N.z);
      tmp.vertex(V.x, V.y, V.z);
    }
    tmp.endShape(CLOSE);
    obj.addChild(tmp);
    header++;
  }
  return obj;
  }catch(Exception e){
    PrintCon("error opening model");
    PrintCon(e.toString());
    ErrorTimer=120;
    return null;
  }
}
public void Menu() {
  MATHUI();
  textAlign(LEFT, TOP);
}

public void MenuSetup() {
  menuUI = new UI[0];

  //PAUSE_MENU

  menuUI = (UI[])append(menuUI, new Text      (20, 50,-1,-1, 100, 40, "PAUSE_MENU", "nothing", "PAUSED"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, -110-50,-1,1, 100, 40, "PAUSE_MENU", "restart", "restart"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, -110,-1,1, 100, 40, "PAUSE_MENU", "GotoOptions", "options"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, -110+50,-1,1, 100, 40, "PAUSE_MENU", "Exit", "Exit the thing"));
  menuUI = (UI[])append(menuUI, new Slot      (-210, 50,1,-1, 200, 20, "PAUSE_MENU", 0));
  menuUI = (UI[])append(menuUI, new Slot      (-210, 70,1,-1, 200, 20, "PAUSE_MENU", 1));
  menuUI = (UI[])append(menuUI, new Slot      (-210, 90,1,-1, 200, 20, "PAUSE_MENU", 2));
  menuUI = (UI[])append(menuUI, new Text      (-310, 50,1,-1, 100, 60, "PAUSE_MENU", "nothing", "testing this things"));

  //MAIN_MENU

  menuUI = (UI[])append(menuUI, new ButtonText(20, -160,-1,1, 100, 40, "MAIN_MENU", "RunGame", "go to the arena"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, -160-50,-1,1, 100, 40, "MAIN_MENU", "GotoTutorial", "tutorial"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, -160+50,-1,1, 100, 40, "MAIN_MENU", "GotoOptions", "options"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, -160+100,-1,1, 100, 40, "MAIN_MENU", "Quit", "Quit"));
  menuUI = (UI[])append(menuUI, new ButtonText(140, -160+100,-1,1, 100, 40, "MAIN_MENU", "UPDATE", "Update"));
  menuUI = (UI[])append(menuUI, new ButtonText(140, -160+50,-1,1, 100, 40, "MAIN_MENU", "GotoPack", "Packs"));
  menuUI = (UI[])append(menuUI, new Image     (0,75,0,-1, 100, 40, "MAIN_MENU", "UPDATE", "Misc/Title.png"));
  
  //PACKS

  menuUI = (UI[])append(menuUI, new ButtonText(20,50,-1,-1, 100, 40, "PACKS", "GotoMainFromPack", "back"));
  menuUI = (UI[])append(menuUI, new PACKS(20, 100,-1,-1, 300, 500, "PACKS", "nothing"));

  //saves//
  
  menuUI = (UI[])append(menuUI, new SaveButton(140, -160,-1,1, 100, 40, "MAIN_MENU", "nothing" , 10));
  menuUI = (UI[])append(menuUI, new SaveButton(260, -160,-1,1, 100, 40, "MAIN_MENU", "nothing" , 20));
  menuUI = (UI[])append(menuUI, new SaveButton(320, -180,-1,1, 100, 10, "MAIN_MENU", "nothing" , 21));
  menuUI = (UI[])append(menuUI, new SaveButton(380, -160,-1,1, 100, 40, "MAIN_MENU", "nothing" , 30));
  menuUI = (UI[])append(menuUI, new SaveButton(500, -160,-1,1, 100, 40, "MAIN_MENU", "nothing" , 40));
  menuUI = (UI[])append(menuUI, new SaveButton(560, -180,-1,1, 100, 10, "MAIN_MENU", "nothing" , 41));
  menuUI = (UI[])append(menuUI, new SaveButton(620, -160,-1,1, 100, 40, "MAIN_MENU", "nothing" , 50));
  menuUI = (UI[])append(menuUI, new SaveButton(740, -160,-1,1, 100, 40, "MAIN_MENU", "nothing" , 60));
  
  //OPTIONS_MENU

  menuUI = (UI[])append(menuUI, new ButtonText(20, -110+50,-1,1, 100, 40, "OPTIONS_MENU", "GotoMain", "back"));
  menuUI = (UI[])append(menuUI, new Text      (20, 50,-1,-1, 100, 40, "OPTIONS_MENU", "nothing", "this is the options menu"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, 90,-1,-1, 100, 40, "OPTIONS_MENU", "GotoBinds", "KeyBinds"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, 130,-1,-1, 100, 40, "OPTIONS_MENU", "GotoNots", "Visuals"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, 170,-1,-1, 100, 40, "OPTIONS_MENU", "Save_config", "save"));
  
  //Binds

  menuUI = (UI[])append(menuUI, new BindButton(140, 50,-1,-1, "Binds", "Player_Move_Up", "jump"));
  menuUI = (UI[])append(menuUI, new BindButton(140, 90,-1,-1, "Binds", "Player_Move_Down", ""));
  menuUI = (UI[])append(menuUI, new BindButton(140, 130,-1,-1, "Binds", "Player_Move_Left", "slide to the left"));
  menuUI = (UI[])append(menuUI, new BindButton(140, 170,-1,-1, "Binds", "Player_Move_Right", "slide to the right"));
  menuUI = (UI[])append(menuUI, new BindButton(140, 210,-1,-1, "Binds", "Player_Restart", "quick restart"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 50,-1,-1, "Binds", "Player_Boost", "special"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 90,-1,-1, "Binds", "Weapon_1", "slot garant"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 130,-1,-1, "Binds", "Weapon_2", "slot rocket"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 170,-1,-1, "Binds", "Weapon_3", "slot railgun"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 210,-1,-1, "Binds", "Weapon_4", "slot shotgun"));
  
  //Nots

  menuUI = (UI[])append(menuUI, new ButtonToggle(140, 50,-1,-1, 400, 40, "Nots", "DrawEffects", "DrawEffects : Only important Effect will display"));
  menuUI = (UI[])append(menuUI, new ButtonSlider(140, 90,-1,-1, 400, 40, "Nots", "GuiScale", "GuiScale : Scale of the Hud Rings", 50, 500));
  menuUI = (UI[])append(menuUI, new ButtonToggle(140, 130,-1,-1, 400, 40, "Nots", "ReversedWhell", "ReversedWhell : reverse the weapon whell seletion"));
  menuUI = (UI[])append(menuUI, new ButtonSlider(140, 170,-1,-1, 400, 40, "Nots", "HurtScale", "HurtScale : scale the \"vinete\" that apears", 1, 50));
  menuUI = (UI[])append(menuUI, new ButtonToggle(140, 210,-1,-1, 400, 40, "Nots", "ShouldPause", "ShouldPause : autopause if unfocused"));
  menuUI = (UI[])append(menuUI, new ButtonToggle(140, 250,-1,-1, 400, 40, "Nots", "SimpleExplosion", "SimpleExplosion : makes explosions simpler goodFps boost for them"));
  //menuUI = (UI[])append(menuUI, new ButtonToggle(140,290, 400, 40, "Nots", "Fullscreen", "Fullscreen : Fullscreen"));
  menuUI = (UI[])append(menuUI, new ButtonSlider(140, 290,-1,-1, 400, 40, "Nots", "Zoom", "Zoom : How much zoom", 10, 200));

  MenuTurnOn("MAIN_MENU");
}

public void GotoTutorial() {
  Start("tutorial");
  toturialTimer = 0;
  toturialMode = true;
}

public void nothing() {/*!nothing!*/}

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
        &&mouseX>menuUI[i].x+menuUI[i].getAliningW()
        &&mouseX<menuUI[i].x+menuUI[i].getAliningW()+menuUI[i].w
        &&mouseY>menuUI[i].y+menuUI[i].getAliningH()
        &&mouseY<menuUI[i].y+menuUI[i].getAliningH()+menuUI[i].h) {
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
  toturialMode=false;
  tantactive=true;
  tantrest();
  nextWave();
  MenuPaused=false;
  RunPhys=true;
}

public void GotoOptions() {
  MenuTurnOffAll();
  MenuTurnOn("OPTIONS_MENU");
  MenuTurnOn("Binds");
  MenuSwap=true;
}

public void GotoPack() {
  MenuTurnOffAll();
  MenuTurnOn("PACKS");
  MenuSwap=true;
}

public void Save_config() {
  JSONObject tmp = loadJSONObject("Misc/config.json");
  JSONObject out = new JSONObject();
  out.setBoolean("DebugStart", tmp.getBoolean("DebugStart"));
  out.setString("DebugMap", tmp.getString("DebugMap"));
  out.setBoolean("Debugdraw", tmp.getBoolean("Debugdraw"));
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
  //println("test");
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

public void GotoMainFromPack() {
  String[] Enabled = {};
  for(int i=0;i<Packs.length;i++){
    if(Packs[i].Enabled){
      Enabled = append(Enabled,Packs[i].internalName);
    }
  }
  saveStrings(sketchPath()+"/data/Packs/Enabled.txt",Enabled);
  printArray(Enabled);
  resetALLassets();
  MenuTurnOffAll();
  MenuTurnOn("MAIN_MENU");
  MenuSwap=true;
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
  SaveButton(float nx, float ny,int AllingW,int AllingH, float nw, float nh, String nCall,String nRun,int save) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
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
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    rect(0, 0, w, h);
    fill(0xffBFBFBF);
    text("\"level\" "+(save), w/2, h/2);
    popMatrix();
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
  Slot(float nx, float ny,int AllingW,int AllingH, float nw, float nh, String nCall, int nwhich) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
    w=nw;
    h=nh;
    Call=nCall;
    which=nwhich;
  }
  public void Draw(int i) {
    textAlign(CENTER, CENTER);
    stroke(0xff676767);
    fill(0xff808080);
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    rect(0,0, w , h);
    rect(0,0, 50, h);
    rect(w-50, 0, 50, h);
    line(50, 0, 0, h/2);
    line(50, h, 0, h/2);
    line(w-50, 0, w, h/2);
    line(w-50, h, w, h/2);
    fill(0xff000000);
    if (which==0) {
      text(slot1[play.vertical], w/2, h/2);
    }
    if (which==1) {
      text(slot2[play.mobilaty], w/2, h/2);
    }
    if (which==2) {
      text(slot3[play.regenera], w/2, h/2);
    }
    if (mouseX>x+50+getAliningW() && mouseX<x+w-50+getAliningW() && mouseY>y+getAliningH() && mouseY<y+h+getAliningH()) {
      fill(0xff808080);
      rect(50, 0, w-100, h);
      fill(0xff000000);
      if (which==0) {
        text(desc1[play.vertical], w/2, h/2);
      }
      if (which==1) {
        text(desc2[play.mobilaty], w/2, h/2);
      }
      if (which==2) {
        text(desc3[play.regenera], w/2, h/2);
      }
    }
    popMatrix();
  }
  public void CFunc(int i) {
    int u=0;
    if (mouseX<x+50+getAliningW()) {
      u--;
    }
    if (mouseX>x+w-50+getAliningW()) {
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
  ButtonSlider(float nx, float ny,int AllingW,int AllingH, float nw, float nh, String nCall, String nVar, String nText, int Min, int Max) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
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
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    rect(0, 0, w, h);
    fill(0xff404040);
    rect(0, 0, map(Configs.get(Var), min, max, 0, w), h);
    fill(0xffBFBFBF);
    text(Text, 0+w/2, 0+h/2-5);
    text(Configs.get(Var), 0+w/2, 0+h/2+5);
    popMatrix();
  }
  public void CFunc(int i) {
    Configs.set(Var, constrain((int)map(mouseX, x+getAliningW()+5, x+getAliningW()+w-5, min, max), min, max));
  }
}

class ButtonToggle extends UI {
  String Text;
  String Var;
  ButtonToggle(float nx, float ny,int AllingW,int AllingH, float nw, float nh, String nCall, String nVar, String nText) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
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
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    rect(0, 0, w, h);
    fill(0xffBFBFBF);
    text(Text, w/2, h/2);
    popMatrix();
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
  BindButton(float nx, float ny,int AllingW,int AllingH, String nCall, String nCode, String nText) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
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
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    rect(0, 0, w, h);
    fill(0xffBFBFBF);
    text(Text,2,2);
    if (Text.isEmpty()) {
      text(Code,2,2);
    }
    if (Keybinds[Connected].iscode) {
      text(returnTEXE(Keybinds[Connected].code),2,22);
    } else {
      text(Keybinds[Connected].pri,2,22);
    }
    popMatrix();
  }
  public void Func(int i) {
    WaitingUser=true;
    Waiter=i;
    ToWait=Connected;
  }
}

class Text extends UI {
  String Text;
  Text(float nx, float ny,int AllingW,int AllingH, float nw, float nh, String nCall, String nRun, String nText) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
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
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    rect(0, 0, w, h);
    fill(0xffBFBFBF);
    text(Text, 2, 2, w-4, 200);
    popMatrix();
  }
}

class Image extends UI {
  PImage img;
  Image(float nx, float ny,int AllingW,int AllingH, float nw, float nh, String nCall, String nRun, String ImgPath) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
    w=nw;
    h=nh;
    Call=nCall;
    Run=nRun;
    img=SloadImage(ImgPath);
  }
  public void Draw(int i) {
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    image(img, -img.width/2, -img.height/2);
    popMatrix();
  }
}

class ButtonText extends UI {
  String Text;
  ButtonText(float nx, float ny,int AllingW,int AllingH, float nw, float nh, String nCall, String nRun, String nText) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
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
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    rect(0, 0, w, h);
    fill(0xffBFBFBF);
    text(Text, w/2, h/2);
    popMatrix();
  }
}

class PACKS extends UI {
  PACKS(float nx, float ny,int AllingW,int AllingH, float nw, float nh, String nCall, String nRun) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
    w=nw;
    h=nh;
    Call=nCall;
    Run=nRun;
  }
  int lookingat=0;
  public void Draw(int i) {
    textAlign(LEFT, TOP);
    stroke(0xff676767);
    fill(0xff404040);
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    rect(0, 0, 300, 500);
    line(100,0,100,500);
    for(int u=0;u<Packs.length;u++){
      if(mouseX>=x+getAliningW() && mouseX<=x+getAliningW()+100 && mouseY>=y+getAliningH()+20*u && mouseY<=y+getAliningH()+20*(u+1)){
        lookingat=u;
      }
      fill(0xffFFFFFF);
      text(Packs[u].Name,2,2+20*u);
      line(0,20*(u+1),100,20*(u+1));
      fill(Packs[u].Enabled?0xff00FF00:0xffFF0000);
      rect(81,20*u+1,18,18);
    }
    if(Packs.length>0){
      fill(0xffFFFFFF);
      text(Packs[lookingat].Name,102,2);
      text(Packs[lookingat].Author,102,24);
      text(Packs[lookingat].Desc,102,46,200,300);
    }
    popMatrix();
  }
  public void Func(int i) {
    for(int u=0;u<Packs.length;u++){
      if(mouseX>=x+getAliningW() && mouseX<=x+getAliningW()+100 && mouseY>=y+getAliningH()+20*u && mouseY<=y+getAliningH()+20*(u+1)){
        Packs[u].Enabled=!Packs[u].Enabled;
      }
    }
  }
}

class UI {
  float x;
  float y;
  float w;
  float h;
  int AllingW;
  int AllingH;
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
      PrintCon(e.getMessage()+"");
      ErrorTimer=120;
    }
  }
  public void CFunc(int i) {
    //much
  }
  public void Draw(int i) {
    //wow
  }
  public int getAliningW(){
    if(AllingW==-1){
      return 0;
    }else if(AllingW==0){
      return width/2;
    }else{
      return width;
    }
  }
  public int getAliningH(){
    if(AllingH==-1){
      return 0;
    }else if(AllingH==0){
      return height/2;
    }else{
      return height;
    }
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
        if (mouseX>menuUI[i].x+menuUI[i].getAliningW()
          &&mouseX<menuUI[i].x+menuUI[i].getAliningW()+menuUI[i].w
          &&mouseY>menuUI[i].y+menuUI[i].getAliningH()
          &&mouseY<menuUI[i].y+menuUI[i].getAliningH()+menuUI[i].h) {
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
    Light = FindLight();
    Power = 1;
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
    UpdateLight();
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
    Light = FindLight();
    Power = 0.8f;
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
    UpdateLight();
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
    Light = FindLight();
    Power = 0.2f;
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
    UpdateLight();
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
    Light = FindLight();
    Power = 1;
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
    UpdateLight();
  }
  public void render() {
    fill(0xffFFFFFF);
    stroke(0xff00FFFF);
    circle(X, Y, 20);
    //rect(X-W, Y-H, W*2, H*2);
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
    Light = FindLight();
    Power = 1;
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
    UpdateLight();
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
    Light = FindLight();
    Power = 0.1f;
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
    UpdateLight();
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
    Light = FindLight();
    Power = 0.2f;
  }
  int bombtimer=90;
  public void math(int SID) {
    if (bombtimer%5==0) {
      expd(X, Y-60,120, 42, 0, true);
    }
    if (bombtimer==0) {
      killPR.append(SID);
      return;
    }
    bombtimer--;
    X+=VX;
    Y+=VY;
    //VY+=0.2;
    UpdateLight();
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
    Light = FindLight();
    Power = 0.2f;
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
    if (Cont(W, H, 32)) {
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
    UpdateLight();
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
    Light = FindLight();
    Power = 0.2f;
  }
  float fuel=280;
  float rotate=0;
  public void math(int SID) {
    Cont(W, H, 32);
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
    UpdateLight();
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
    Light = FindLight();
    Power = 0.2f;
  }
  int fuel=240;
  float rotate=0;
  public void math(int SID) {
    Cont(W, H, 42);
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
    UpdateLight();
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
    Light = FindLight();
    Power = 0.3f;
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
    UpdateLight();
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

class Rock extends PRO {
  Rock(float nX, float nY, float nVX, float nVY, int nT) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    T = nT;
    W=8;
    H=8;
    timer=0;
    cframe=0;
    Light = FindLight();
    Power = 1;
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
    NewPartic(new GravPoint(X,Y,0,0,5,0xff222222,1),false);
    X+=VX;
    Y+=VY;
    VY+=0.2f;
    UpdateLight();
  }
  public void render() {
    if (DebugDraw) {
      noStroke();
      fill(255);
      rect(X-W, Y-H, W*2, H*2);
    }
    noStroke();
    fill(0xff676767);
    circle(X,Y,W*2);
  }
}

class hurtbox extends PRO {
  hurtbox(float X, float Y, float W, float H, float R,int hurtme,int timer,int hurttimer) {
    this.X = X;
    this.Y = Y;
    this.W = W;
    this.H = H;
    this.R = R;
    this.timer=timer;
    this.maxtimer=timer;
    this.hurttimer=hurttimer;
    ouch=hurtme;//POV you are looking at my code
  }
  int maxtimer=15;
  int ouch=0;
  int timer=15;
  int hurttimer=15;
  float R=0;
  public void math(int SID) {
    if(timer==0){
      killPR.append(SID);
      return;
    }
    timer--;
    hurttimer--;
    if(hurttimer>=0){
      //even if not the best way of doing this
      //the processes is fast
      float[] Offx = {-6,-6,6,6};
      float[] Offy = {0,-24,-24,0};
      pushMatrix();
      rotate(-R);
      translate(-X,-Y);
      for(int looper=0;looper<4;looper++){
        float nx=screenX(play.X+Offx[looper],play.Y+Offy[looper]);
        float ny=screenY(play.X+Offx[looper],play.Y+Offy[looper]);
        if(nx>-W/2 && nx<W/2  &&  ny>-H/2 && ny<H/2){
          AThurt(ouch);
          break;
        }
      }
      popMatrix();
    }
  }
  public void render() {
    pushMatrix();
    translate(X,Y);
    rotate(R);
    noStroke();
    fill(255,timer*255/maxtimer);
    rect(-W/2, -H/2, W, H);
    popMatrix();
  }
}

class MiniRocket extends PRO {
  float Bombtimer=60;
  MiniRocket(float nX, float nY, float nVX, float nVY, int nT) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    T = nT;
    W=12;
    H=12;
    Light = FindLight();
    Power = 0.2f;
  }
  public void math(int SID) {
    if (Coll(X-W, Y-W, X+W, Y+W)) {
      killPR.append(SID);
      expd(X, Y, 120, 15, 5, true);
      return;
    }
    if (Coll(X+H, Y-H, X-H, Y+H)) {
      killPR.append(SID);
      expd(X, Y, 120, 15, 5, true);
      return;
    }
    if (Coll(X, Y, X+VX, Y+VY)) {
      killPR.append(SID);
      expd(X, Y, 120, 15, 5, true);
      return;
    }
    if (Bombtimer==0) {
      killPR.append(SID);
      expd(X, Y, 120, 15, 5, true);
      return;
    }
    X+=VX;
    Y+=VY;
    Bombtimer--;
    NewPartic(new GravPoint(X,Y,-VX,-VY,40,0xffCCCCCC,-0.4f),true);
    UpdateLight();
    //VY+=0.2;
  }
  public void render() {
    if (DebugDraw) {
      noStroke();
      fill(255);
      rect(X-W, Y-H, W*2, H*2);
    }
    noStroke();
    fill(0xffFA0000);
    circle(X,Y,12);
  }
}

class MiniBullet extends PRO {
  MiniBullet(float nX, float nY, float nVX, float nVY, int nT) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    T = nT;
    W=6;
    H=6;
    Light = FindLight();
    Power = 0.2f;
  }
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
    if (Cont(W, H, 32)) {
      killPR.append(SID);
      return;
    }
    AddPartic(1, X, Y, X+VX, Y+VY, 10, color(0xffFFFF00), true);
    fuel--;
    X+=VX;
    Y+=VY;
    UpdateLight();
    //VY+=0.2;
  }
  public void render() {
    if (DebugDraw) {
      noStroke();
      fill(255);
      rect(X-W, Y-H, W*2, H*2);
    }
    fill(0xffFFFF00);
    circle(X,Y,W);
  }
}

class Eletro extends PRO {
  Eletro(float nX, float nY, float nVX, float nVY,int timer) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    W=8;
    H=8;
    this.timer=timer;
  }
  int timer=0;
  public void math(int SID) {
    if (timer==0) {
      killPR.append(SID);
      return;
    }
    timer--;
    if (Cont(W, H, 16)) {
      killPR.append(SID);
      return;
    }
    X+=VX;
    Y+=VY;
  }
  public void render() {
    fill(0xffEAF9FF);
    stroke(0xff05ACF7);
    circle(X, Y, 20);
    //rect(X-W, Y-H, W*2, H*2);
  }
}

class SEletro extends PRO {
  SEletro(float nX, float nY, float nVX, float nVY,int timer,float Doff,float Roff) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    W=8;
    H=8;
    this.Doff=Doff;
    this.Roff=Roff;
    this.timer=timer;
  }
  float Doff=0;
  float Roff=0;
  public void math(int SID) {
    if (timer==0) {
      killPR.append(SID);
      return;
    }
    timer--;
    if (Cont(W, H, 16)) {
      killPR.append(SID);
      return;
    }
    Doff+=VX;
    Roff+=VY;
  }
  public void render() {
    fill(0xffEAF9FF);
    stroke(0xff05ACF7);
    circle(X+(cos(Roff)*Doff), Y+(sin(Roff)*Doff), 20);
    //rect(X-W, Y-H, W*2, H*2);
  }
  public boolean Cont(float W, float H, int dmg) {
    if (X+(cos(Roff)*Doff)+W>play.X-6 && X+(cos(Roff)*Doff)-W<play.X+6 && Y+(sin(Roff)*Doff)+H>play.Y-24 && Y+(sin(Roff)*Doff)-H<play.Y+0) {
      AThurt(dmg);
      return true;
    }
    return false;
  }
}

class Bross extends PRO {
  Bross(float nX, float nY, float nVX, float nVY,int timer,float R) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    W=8;
    H=8;
    this.timer=timer;
    this.R=R;
    Light = FindLight();
    Power = 1;
  }
  float R=0;
  public void math(int SID) {
    if (timer==0) {
      killPR.append(SID);
      NewSPr(new hurtbox(X,Y,3000,20,R,35,10,1));
      return;
    }
    NewPartic(new Line(X-cos(R)*1500,Y-sin(R)*1500,X+cos(R)*1500,Y+sin(R)*1500,2,0xffFF0000,timer),true);
    timer--;
    UpdateLight();
  }
  public void render() {
    fill(0xffEAF9FF);
    stroke(0xff05ACF7);
    circle(X, Y, 20);
    //rect(X-W, Y-H, W*2, H*2);
  }
}

class WindBall extends PRO {
  WindBall(float nX, float nY, float nVX, float nVY) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    W=32;
    H=32;
    Light = FindLight();
    Power = 0.3f;
  }
  float R=random(-PI,PI);
  int timer=240;
  public void math(int SID) {
    if (timer==0) {
      killPR.append(SID);
      return;
    }
    if (X+W>play.X-6 && X-W<play.X+6 && Y+H>play.Y-24 && Y-H<play.Y+0) {
      play.VX+=cos(R);
      play.VY+=sin(R);
    }
    NewPartic(new Wind(X+random(-16,16),Y+random(-16,16),cos(R)*5,sin(R)*5,15,0xffD0CFD1),true);
    timer--;
    X+=VX;
    Y+=VY;
    UpdateLight();
  }
  public void render() {
    fill(0xffD0CFD1,100);
    stroke(0xffD0CFD1,100);
    circle(X, Y, 64);
    //rect(X-W, Y-H, W*2, H*2);
  }
}

class IceBall extends PRO {
  IceBall(float nX, float nY, float nVX, float nVY) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    W=32;
    H=32;
    Light = FindLight();
    Power = 0.3f;
  }
  float R=random(-PI,PI);
  int timer=480;
  public void math(int SID) {
    if (timer==0) {
      killPR.append(SID);
      return;
    }
    if (X+W>play.X-6 && X-W<play.X+6 && Y+H>play.Y-24 && Y-H<play.Y+0) {
      play.frezzing=true;
    }
    NewPartic(new GravPoint(X+random(-32,32),Y+random(-32,32)-H/2,0,0,15,0xffD0CFD1,0),true);
    timer--;
    X+=VX;
    Y+=VY;
    UpdateLight();
  }
  public void render() {
    fill(0xff75DBD2,100);
    stroke(0xff75DBD2,100);
    circle(X, Y, 64);
    //rect(X-W, Y-H, W*2, H*2);
  }
}

class Melting extends PRO {
  Melting(float nX, float nY, float nVX, float nVY) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    W=16;
    H=16;
    Light = FindLight();
    Power = 0.3f;
  }
  int timer=480;
  boolean Grounded=false;
  public void math(int SID) {
    if (timer==0) {
      killPR.append(SID);
      return;
    }
    if(!Grounded){
      if (Coll(X-W, Y-W, X+W, Y+W)) {
        Grounded=true;
      }
      if (Coll(X+H, Y-H, X-H, Y+H)) {
        Grounded=true;
      }
      if (Coll(X, Y, X+VX, Y+VY)) {
        Grounded=true;
      }
    }
    if (Cont(W,H,32)) {
      killPR.append(SID);
      return;
    }
    timer--;
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-10, 10), Y-random(0, 20), random(-1,1), 0, 60, 0xffD8C004, -0.2f), true);
    }
    if(!Grounded){
      VY+=0.2f;
      X+=VX;
      Y+=VY;
    }
    UpdateLight();
  }
  public void render() {
    fill(0xffD8C004);
    stroke(0xffD8C004);
    circle(X, Y, 32);
    fill(0xffF5EA6F,100);
    stroke(0xffF5EA6F,100);
    circle(X, Y, 16);
    //rect(X-W, Y-H, W*2, H*2);
  }
}

class Rage extends PRO {
  Rage(float nX, float nY, float nVX, float nVY) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    W=8;
    H=8;
    Light = FindLight();
    Power = 0.3f;
  }
  int timer=480;
  public void math(int SID) {
    if (timer==0) {
      killPR.append(SID);
      expd(X,Y,128,32,0,true);
      return;
    }
    if (Coll(X-W, Y-W, X+W, Y+W)) {
      killPR.append(SID);
      expd(X,Y,128,32,0,true);
      return;
    }
    if (Coll(X+H, Y-H, X-H, Y+H)) {
      killPR.append(SID);
      expd(X,Y,128,32,0,true);
      return;
    }
    if (Coll(X, Y, X+VX, Y+VY)) {
      killPR.append(SID);
      expd(X,Y,128,32,0,true);
      return;
    }
    if (Cont(W,H,32)) {
      killPR.append(SID);
      expd(X,Y,128,32,0,true);
      return;
    }
    timer--;
    NewPartic(new GravPoint(X+random(-10, 10), Y-random(0, 20), 0  , 0, 60, 0xff155A0B, 0), true);
    X+=VX;
    Y+=VY;
    UpdateLight();
  }
  public void render() {
    fill(0xff155A0B,100);
    stroke(0xff155A0B,100);
    circle(X, Y, 16);
    //rect(X-W, Y-H, W*2, H*2);
  }
}

class MiniFire extends PRO {
  MiniFire(float nX, float nY, float nVX, float nVY) {
    X = nX;
    Y = nY;
    VX = nVX;
    VY = nVY;
    W=16;
    H=16;
    timer=0;
    cframe=0;
    Light = FindLight();
    Power = 0.3f;
  }
  float fuel=140;
  float rotate=0;
  public void math(int SID) {
    Cont(W, H, 32);
    rotate = atan2(play.Y-Y, play.X-X);
    AddPartic(3, X+random(-W/2, W/2), Y+random(-H/2, H/2), 0, -3, 40, color(0xffFF0000), false);
    VX+=cos(rotate)*0.3f;
    VY+=sin(rotate)*0.3f;
    fuel--;
    if (fuel==0) {
      killPR.append(SID);
      return;
    }
    X+=VX;
    Y+=VY;
    UpdateLight();
    //VY+=0.2;
  }
  public void render() {
    if (DebugDraw) {
      noStroke();
      fill(255);
      rect(X-W, Y-H, W*2, H*2);
    }
    fill(0xffFF0000, fuel/120*255);
    circle(X, Y, W*2.5f);
    fill(0xffFF8D00, fuel/120*255);
    circle(X, Y, W*1.5f);
  }
}

class Shard extends PRO {
  Shard(float nX, float nY, float nR, float nV, int nT) {
    X = nX;
    Y = nY;
    VX = cos(nR)*nV;
    VY = sin(nR)*nV;
    T = nT;
    W=6;
    H=6;
  }
  int fuel=60;
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
    if (Cont(W, H, 32)) {
      killPR.append(SID);
      return;
    }
    AddPartic(1, X, Y, X+VX, Y+VY, 6, color(0xffFFFFFF), true);
    fuel--;
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
    fill(0xffFFFFFF);
    circle(X,Y,W);
  }
}

class DeathShard extends PRO {
  DeathShard(float nX, float nY, float nR, float nV, int nT) {
    X = nX;
    Y = nY;
    VX = cos(nR)*nV;
    VY = sin(nR)*nV;
    T = nT;
    W=12;
    H=12;
  }
  int fuel=60;
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
    if (Cont(W, H, 32)) {
      killPR.append(SID);
      return;
    }
    AddPartic(1, X, Y, X+VX, Y+VY, 6, color(0xffFFFFFF), true);
    fuel--;
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
    fill(0xffFFFFFF);
    circle(X,Y,W*2);
  }
}

class Heal extends PRO {
  Heal(float nX, float nY, int nT) {
    X = nX;
    Y = nY;
    VX = 0;
    VY = 0;
    T = nT;
    W=12;
    H=12;
  }
  public void math(int SID) {
    if (play.HP<=0) {
      killPR.append(SID);
      NewPartic(new LAGPoint(X,Y,random(-5,5),random(-5,5),30,0xffFF0000),true);
      return;
    }
    if (dist(play.X,play.Y-12,X,Y)<12) {
      killPR.append(SID);
      play.HP=min(100,play.HP+1);
      return;
    }
    AddPartic(1, X, Y, X+VX, Y+VY, 10, color(0xffFF0000), true);
    float R=atan2(play.Y-Y-12,play.X-X);
    X+=cos(R)*12;
    Y+=sin(R)*12;
    //VY+=0.2;
  }
  public void render() {
    if (DebugDraw) {
      noStroke();
      fill(255);
      rect(X-W, Y-H, W*2, H*2);
    }
    fill(0xffFF0000);
    circle(X,Y,W);
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
  int Light;
  float Power;
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
  public void UpdateLight(){
    LightActive[Light] = true;
    LightPower[Light] = Power;
    LightX[Light] = X;
    LightY[Light] = Y;
  }
}

public int FindLight(){
  for(int i=0;i<128;i++){
    if(!LightActive[i]){
      LightActive[i]=true;
      return i;
    }
  }
  return 0;
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

public void NewSPr(PRO newthing){
  ListPR.add(newthing);
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
  case 11:
    ListPR.add(new Rock(X, Y, VX, VY, T));
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
  int light=0;
  PROP(float nX,float nY,String nT){
    X=nX;
    Y=nY;
    T=nT;
    if(T.equals("lamp")){
      light=FindLight();
    }
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
    if(T.equals("lamp")){
      LightX[light] = X;
      LightY[light] = Y-60;
      LightPower[light] = 1;
      LightActive[light] = true;
    }
  }
  public void render(){
    PROPR[Propredirect.get(T)].ANR(X,Y,frame);
    if(T.equals("lamp")){
      
    }
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
public void loadPacks(){
  String[] Enabled = loadStrings(sketchPath()+"/data/Packs/Enabled.txt");
  Packs = new pack[0];
  File PackFolder = new File(sketchPath()+"\\Data\\Packs");
  File [] Packstoload = PackFolder.listFiles();
  for(int i=0;i<Packstoload.length;i++){
    File Pack = Packstoload[i];
    if(!Pack.isFile()){
      JSONObject Data = loadJSONObject(Pack.getPath()+"/Data.json");
      String name;
      String desc;
      String author;
      if(Data!=null){
        name = Data.getString("name","unknown");
        desc = Data.getString("desc","missing");
        author = Data.getString("author","someone");
      }else{
        name="unknown";
        desc="missing";
        author="someone";
      }
      Packs = (pack[])append(Packs,new pack(name,Pack.getName(),desc,author,Pack.getPath()));
      for(int u=0;u<Enabled.length;u++){
        if(Enabled[u].equals(Pack.getName())){
          Packs[Packs.length-1].Enabled=true;
        }
      }
    }
  }
}

pack[] Packs;

class pack{
  String Name;
  String Desc;
  String Author;
  String internalName;
  String Path;
  boolean Enabled=false;
  pack(String Name,String intername,String Desc,String Author,String Path){
    this.Name = Name;
    this.Desc = Desc;
    this.Author= Author;
    this.internalName=intername;
    this.Path=Path;
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
  boolean Friction=false;
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
        if(!(GetKeyBind("Player_Move_Down") | GetKeyBind("Player_Move_Up"))){VY/=1.4f;}
        if(!(GetKeyBind("Player_Move_Left") | GetKeyBind("Player_Move_Right"))){VX/=1.4f;}
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
      if(!GetKeyBind("Player_Move_Right") && !GetKeyBind("Player_Move_Left") && Friction){VX/=2;}
      if(GetKeyBind("Player_Move_Left") && VX>-7 && HP>0 && !ConsoleUP){VX-=0.7f;}
      if(GetKeyBind("Player_Move_Right") && VX< 7 && HP>0 && !ConsoleUP){VX+=0.7f;}
      if(GetKeyBind("Player_Move_Up") && GetKeyBind("Player_Boost") && HP>0 && !ConsoleUP){updash();}
      if(GetKeyBind("Player_Move_Left") && GetKeyBind("Player_Boost") && HP>0 && !ConsoleUP){dash(-1);}
      if(GetKeyBind("Player_Move_Right") && GetKeyBind("Player_Boost") && HP>0 && !ConsoleUP){dash(1);}
    }
    if(GetKeyBind("Player_Move_Down") && HP>0 && !ConsoleUP){Ignore=true;}
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
    if(Ignore && !GetKeyBind("Player_Move_Down")){Checkfor();}
    coll(X-6,Y   ,X-6+VX+0.01f,Y+VY+0.01f);
    coll(X+6,Y   ,X+6+VX-0.01f,Y+VY+0.01f);
    coll(X-6,Y-24,X-6+VX+0.01f,Y-24+VY-0.01f);
    coll(X+6,Y-24,X+6+VX-0.01f,Y-24+VY-0.01f);
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
        PVector Normal=new PVector((CEX[i]-CSX[i]),(CEY[i]-CSY[i]));
        Normal.rotate(-PI/2);
        //if(CSX[i]>CEX[i]){Normal.rotate(-PI/2);}else{Normal.rotate(PI/2);}
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
        R=atan2(CSY[i]-CEY[i],CSX[i]-CEX[i])%PI;
        if(R<0){R+=PI;}
        //println(R);
        if(Normal.dot(TOplayer)>0){//its a feature fuck it
          Gr=true;
          if(CT[i]!=2){
            Friction=true;
          }else{
            Friction=false;
          }
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
JSONArray[] arenas;
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
  DarkenActive=false;
  round=0;
  getFile();
  JSONArray BigFile = loadJSONArray(sketchPath()+"/data/Maps/RAN.json");
  arenas = new JSONArray[BigFile.size()];
  for(int i=0;i<BigFile.size();i++){
    arenas[i]=BigFile.getJSONArray(i);
  }
}

int Epilog=0;

public void tantmath(){
  if(Must==0 && PMust!=0){
    waveEnd=true;
    delaytowave=60;
  }
  if(round==Indexs.length && waveEnd){
    if(Epilog==240){
      NewPartic(new SubText(play.X,play.Y-24,0,-1,60,0xffFFFFFF,"alright",32.0f),true);
    }
    if(Epilog==240+120*1){
      NewPartic(new SubText(play.X,play.Y-24,0,-1,60,0xffFFFFFF,"i killed god.",32.0f),true);
    }
    if(Epilog==240+120*2){
      NewPartic(new SubText(play.X,play.Y,0,-1,60,0xffFFFFFF,"now what?",32.0f),true);
    }
    if(Epilog==240+120*4){
      NewPartic(new SubText(play.X,play.Y,0,-1,60,0xffFFFFFF,"...",32.0f),true);
    }
    if(Epilog==240+120*6){
      AddPartic(1,play.X,play.Y,play.X,-10000,60,0xffFFFFFF,true);
      for(int ohno=0;ohno<50;ohno++){
        AddPartic(2,play.X,play.Y,random(-10,10),random(-10,10),60,0xffFFFFFF,true);
      }
      expd(play.X, play.Y+8, 256, 500, 500, true);
      play.HP=-500;
    }
    if(Epilog==240+120*7){
      texttoscren("sorry theres no good ending");
    }
    if(Epilog==240+120*9){
      exit();
    }
    Epilog++;
    return;
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
  Must=0;
  PMust=0;
  if(round==Indexs.length){
    Blurer=90000;
    round++;
    return;
  }
  Blurer=60;
  round++;
  switch(round){
    case 10:
    case 20:
    case 21:
    case 30:
    case 40:
    case 41:
    case 50:
    case 60:
    CurrentSave=round;
    byte[] out = new byte[1];
    byte[] tmp = BsetI(round,1);
    out[0]=tmp[0];
    saveBytes("data/Misc/sav",out);
    break;
    default:
    break;
  }
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
    case 30:
    Start("arena_caves");
    CurrentArena=split("arena_caves",'_')[1];
    break;
    case 40:
    Start("Boss2");
    CurrentArena="light";
    break;
    case 60:
    Start("Boss3");
    CurrentArena="end";
    break;
    default:
    String[] names = arenas[round/20].getStringArray();
    int map=(int)random(0,names.length-1);
    Start(names[map]);
    CurrentArena=split(names[map],'_')[1];
    break;
  }
        Grspawn=0;
        Arspawn=0;
  GrspawnID = new int[0];
  ArspawnID = new int[0];
  DarkenActive=false;
  for(int i=0;i<ET.length;i++){
    if(ET[i].equals("air")){
      ArspawnID = append(ArspawnID,i);
      //println("air");
    }
    if(ET[i].equals("any")){
      GrspawnID = append(GrspawnID,i);
      //println("any");
    }
    if(ET[i].equals("dark")){
      DarkenActive=true;
      //println("any");
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
  //println(name + "!" + round);
  if(gr){
    float X=EX[GrspawnID[Grspawn]];
    float Y=EY[GrspawnID[Grspawn]];
    Grspawn++;
    if(Grspawn==GrspawnID.length){
      Grspawn=0;
    }
    if(NewAI(X,Y+-11,name,true)){
      Must++;
    }
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
    if(NewAI(X,Y+-11,name,true)){
      Must++;
    }
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
  MOAG.add(new KILLgun());
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
    EDelay=12;
    Const=false;
  }
  public void FIRE() {
    Hitscan(0, 0, play.PO, false, 8, 20,1000);
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
      NewPR(play.X, play.Y-12, cos(play.PO+PI/20-PI/40*i)*10, sin(play.PO+PI/20-PI/40*i)*10, 2);
    }
    for (int i=0; i<5; i++) {
      NewPR(play.X, play.Y-12, cos(play.PO+PI/20-PI/40*i)*8, sin(play.PO+PI/20-PI/40*i)*8, 2);
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
    EDelay=15;
    Const=false;
  }
  public void FIRE() {
    AddPartic(1,play.X, play.Y-12, play.X+mouseX-width/2, play.Y+mouseY-height/2, 40, color(255),true);
    NewAI(play.X+mouseX-width/2,play.Y+mouseY-height/2,AINames[DevGun],false);
  }
}

class KILLgun extends Weapon {
  KILLgun() {
    Sprite=SloadImage("Weapons/devgun.png");
    WeaOffx=-7;
    WeaOffy=-2;
    WeapW=19;
    WeapH=3;
    SDelay=15;
    EDelay=1;
    Const=true;
  }
  public void FIRE() {
    for(int u=1;u<3;u++){
    for(int i=0;i<4;i++){
      float R=i*PI/2;
      Hitscan(PApplet.parseInt(cos(R+frameCount/10.0f)*(30+cos(frameCount/30.0f)*15)*u), PApplet.parseInt(sin(R+frameCount/10.0f)*(30+cos(frameCount/30.0f)*15)*u), play.PO, true, 8, 5,1000);
    }
    }
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
