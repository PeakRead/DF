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
  void math(){
    timer++;
    if(timer==delay){
      timer=0;
      frame++;
    }
    if(frame==PROPR[Propredirect.get(T)].size()){
      frame=0;
    }
  }
  void render(){
    PROPR[Propredirect.get(T)].ANR(X,Y,frame);
  }
}

IntDict Propredirect;

void setupProps(){
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

void propM(){
  for(int i=0;i<PROPL.length;i++){
    PROPL[i].math();
  }
}

void propD(){
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
  
  void ANR(float x,float y,int frame){
    image(Frames[RFrames[frame]],x-Frames[RFrames[frame]].width/2,y-Frames[RFrames[frame]].height);
  }
  
  int width(int frame){
    return Frames[RFrames[frame]].width;
  }
  
  int height(int frame){
    return Frames[RFrames[frame]].height;
  }
  
  int size(){
    return RFrames.length;
  }
}
