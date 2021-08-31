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
  void DIMG(float X,float Y,float w,float h,int frame,boolean moveing,boolean Airborn,color C){
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
  int getM(boolean mov,boolean air){
    if(air){
      return Air.length;
    }else if(mov){
      return Move.length;
    }else{
      return Stand.length;
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
