ArrayList<Effect> ListEffects;
IntList Ekill;
IntDict PartTextName;
PImage[] PartImgs;

void PartINIC(){
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
  Line(float nX,float nY,float nVX,float nVY,int ntime,color nC,float Weight){
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
  void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    //nothing!!
    time--;
  }
  void drawE(){
    strokeWeight((float)time*(float)Weight/(float)Mtime);
    stroke(C,(float)time*(float)255/(float)Mtime);
    line(X,Y,VX,VY);
  }
}

class GravPoint extends Effect{
  float gravmult=1;
  GravPoint(float nX,float nY,float nVX,float nVY,int ntime,color nC,float gravmult){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
    this.gravmult=gravmult;
  }
  void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    VY+=0.2*gravmult;
    X+=VX;
    Y+=VY;
    time--;
  }
  void drawE(){
    strokeWeight((float)time*(float)5/(float)Mtime);
    stroke(C,(float)time*(float)255/(float)Mtime);
    point(X,Y);
  }
}

class VELLPoint extends Effect{
  VELLPoint(float nX,float nY,float nVX,float nVY,int ntime,color nC){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
  }
  void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    X+=VX;
    Y+=VY;
    time--;
  }
  void drawE(){
    strokeWeight((float)time*(float)5/(float)Mtime);
    stroke(C,(float)time*(float)255/(float)Mtime);
    point(X,Y);
  }
}

class LAGPoint extends Effect{
  float PX=0;
  float PY=0;
  LAGPoint(float nX,float nY,float nVX,float nVY,int ntime,color nC){
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
  void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    PX=X;
    PY=Y;
    VY+=0.2;
    X+=VX;
    Y+=VY;
    time--;
  }
  void drawE(){
    strokeWeight((float)time*(float)5/(float)Mtime);
    stroke(C,(float)time*(float)255/(float)Mtime);
    line(X,Y,PX,PY);
  }
}

class Explode extends Effect{
  Explode(float nX,float nY,float nVX,float nVY,int ntime,color nC){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
  }
  int bombtimer=0;
  void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    //nothing!! but cooler!
    time--;
  }
  void drawE(){
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
  Wind(float nX,float nY,float nVX,float nVY,int ntime,color nC){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
  }
  void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    X+=VX;
    Y+=VY;
    time--;
  }
  void drawE(){
    strokeWeight((float)time*(float)5/(float)Mtime);
    stroke(C,(float)time*(float)255/(float)Mtime);
    line(X-VX*2,Y-VY*2,X+VX*2,Y+VY*2);
  }
}

class SubText extends Effect{
  String text;
  SubText(float nX,float nY,float nVX,float nVY,int ntime,color nC,String ntext){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
    text=ntext;
  }
  void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    X+=VX;
    Y+=VY;
    time--;
  }
  void drawE(){
    fill(C);
    text(text,X,Y);
  }
}

class StandImg extends Effect{
  StandImg(float nX,float nY,float nVX,float nVY,int ntime,color nC,String ImgName){
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
  void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    X+=VX;
    Y+=VY;
    time--;
  }
  void drawE(){
    //fill(C);
    tint(C,float(time*255)/Mtime);
    image(PartImgs[theImgID],X,Y);
    noTint();
  }
}

class Smoke extends Effect{
  float gravmult=1;
  Smoke(float nX,float nY,float nVX,float nVY,int ntime,color nC,float gravmult){
    X=nX;
    Y=nY;
    VX=nVX;
    VY=nVY;
    time=ntime;
    Mtime=ntime;
    C=nC;
    this.gravmult=gravmult;
  }
  void mathE(int T){
    if(time==0){
      Ekill.append(T);
    }
    NewPartic(new GravPoint(X,Y,0,0,Mtime,C,-1),true);
    VY+=1*gravmult;
    X+=VX;
    Y+=VY;
    time--;
  }
  void drawE(){
  }
}

class Effect{
  float X;
  float Y;
  color C;
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
  void mathE(int T){
  }
  void drawE(){
  }
}

void NewPartic(Effect newthing,boolean Important){
  if(Configs.get("DrawEffects")==0 && !Important){return;}
  ListEffects.add(newthing);
}
  
//old version
void AddPartic(int T,float X,float Y,float VX,float VY,int time,color C,boolean Important){
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
      ListEffects.add(new SubText(X,Y,VX,VY,time,C,""));
      //Text tmp=(Text)ListEffects.get(ListEffects.size()-1);
      //tmp.text="test";
      //ListEffects.set(ListEffects.size()-1,tmp);
      //eh it works
    break;
  }
}

void MathEffects(){
  for(int i=0;i<ListEffects.size();i++){
    ListEffects.get(i).mathE(i);
  }
  Ekill.reverse();
  for(int i=0;i<Ekill.size();i++){
    ListEffects.remove(Ekill.get(i));
  }
  Ekill.clear();
}

void DrawEffects(){
  for(int i=0;i<ListEffects.size();i++){
    ListEffects.get(i).drawE();
  }
}
