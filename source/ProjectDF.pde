String Version = "V 7.1";

keyboard EYS;

void settings(){
  size(700,700,P2D);
  PJOGL.setIcon("Misc/ICON.png");
}

PImage Secret;
PShape MenuBackground;

void setup(){
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
      HWeapon=append(HWeapon,byte(5));
      curSDelay=append(curSDelay,byte(0));
      curEDelay=append(curEDelay,byte(0));
      HWeapon=append(HWeapon,byte(6));
      curSDelay=append(curSDelay,byte(0));
      curEDelay=append(curEDelay,byte(0));
      Start(tmp.getString("DebugMap","arena_vent"));
      if(tmp.getBoolean("DebugTant",false)){
        tantactive=true;
        tantrest();
        round=int(tmp.getInt("DebugRound",1))-1;
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
        Keybinds[RE].code=byte(tmper.getInt("code"));
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

void resetALLassets(){
  
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

void Start(String Map){
  Gaming=true;
  openMap(Map);
  play = new Player();
  background(0);
}

Player play;
boolean DebugDraw=boolean(0);
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

void draw(){
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
      Background.directionalLight(155, 155, 155, -0.5, 0.5, -1);
      //Background.ambientLight(255, 255, 255);
      Background.camera(0,-121,0,0,999,0, 0,0,-1);
      Background.perspective(PI/3.0,float(width)/float(height),1,100000);
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

void MAINLOOP(){
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
        AddPartic(6,TX[i]+random(0,TW[i]),TY[i]+random(0,TH[i]),vx,vy,60,#F2F2F2,true);
      }
      if(TT[i]==7 && random(0,100)<60){
        AddPartic(4,TX[i]+random(0,TW[i]),TY[i]+random(0,TH[i]-240),0,2,60,#54D5DB,true);
      }
      if(TT[i]==8 && random(0,100)<60){
        NewPartic(new GravPoint(TX[i]+random(0,TW[i]),TY[i]+random(120,TH[i]),0,0,240,#30A018,-0.2),true);
      }
    }
  }
  if(tantactive){
    tantmath();
  }
  //RENDER
  ZOOMER=float(Configs.get("Zoom"))/100;
  Darken.set("OffX",play.X*ZOOMER-width/2);
  Darken.set("OffY",play.Y*ZOOMER-height/2);
  Darken.set("Zoom",ZOOMER);
  Darken.set("P",(float)0.004/ZOOMER);
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
  Background.directionalLight(155, 155, 155, -0.5, 0.5, -1);
  //Background.ambientLight(255, 255, 255);
  Background.camera(-play.X/5,-121/ZOOMER ,-play.Y/5, -play.X/5,999, -play.Y/5, 0,0,-1);
  Background.perspective(PI/3.0,float(width)/float(height),1,100000);
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
        float X=CSX[i]*(u/10+0.05)+CEX[i]*(1-u/10-0.05);
        float Y=CSY[i]*(u/10+0.05)+CEY[i]*(1-u/10-0.05);
        line(X,Y,X,Y+10);
      }
    }
  }
  for(int i=0;i<TX.length;i++){
    if(TT[i]==8){
      stroke(#31DB46);
      fill(#31DB46,100);
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
    fill(255,sq(float(Blurer)/60)*255);
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
    stroke(#FF0000,255);
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

float TLineToLine(float x1,float y1,float x2,float y2,float x3,float y3,float x4,float y4)
{
  return ((x1-x3)*(y3-y4)-(y1-y3)*(x3-x4))/((x1-x2)*(y3-y4)-(y1-y2)*(x3-x4));
}
