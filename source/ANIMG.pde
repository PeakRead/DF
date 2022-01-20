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
  
  void ANM(){
    timer--;
    if(timer<0){
      timer=delay;
      frame++;
    }
    if(frame==RFrames.length){
      frame=0;
    }
  }
  
  void ANR(float x,float y){
    image(Frames[RFrames[frame]],x,y);
  }
  
  int width(){
    return Frames[RFrames[frame]].width;
  }
  
  int height(){
    return Frames[RFrames[frame]].height;
  }
}

EnANIMG[] enANIM;
ProANIMG[] proANIM;
IntDict EAR;//me piss and shit

void EnemyAINIC(){
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

void ProAINIC(){
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
  
  void ANR(float x,float y,int frame){
    image(Frames[RFrames[frame]],x-Frames[RFrames[frame]].width/2,y-Frames[RFrames[frame]].height/2);
  }
  
  int width(){
    return Frames[RFrames[frame]].width;
  }
  
  int height(){
    return Frames[RFrames[frame]].height;
  }
  
  int Max(){
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
  void Anim(boolean move,boolean air){
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
  void Action(int todo){
    Action=true;
    Acting=todo;
    frame=0;
  }
  void DIMG(float X,float Y,float w,float h,boolean moveing,boolean Airborn,color C){
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
  void EIMG(float X,float Y,float w,float h,float r,int frame,color C){
    tint(C);
    pushMatrix();
    translate(X,Y);
    rotate(r);
    image(enANIM[ID].Extras[frame],-w/2,-h/2,w,h);
    popMatrix();
    noTint();
  }
  int getM(boolean mov,boolean air){
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

PImage SloadImage(String path){
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
