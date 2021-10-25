void setupBoxs(){
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
  boolean isH(int X,int Y){
    return X==x && Y==y;
  }
  void A(int e){
    for(int i=0;i<c.length;i++){
      if(c[i]==e){
        return;
      }
    }
    c=append(c,e);
  }
  void R(int e){
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

void IC(int x,int y,int T){
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

void cubes(float x1,float y1,float x2,float y2,int T){
  float minX=min(x1,x2);
  float maxX=max(x1,x2);
  float minY=min(y1,y2);
  float maxY=max(y1,y2);
  int bx=int(x1>x2)*-1;
  int by=int(y1>y2)*-1;
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

float rev(float n,float v){
  return n-n%v;
}

void updBx(int i){
  for(int u=0;u<boxs.length;u++){
      boxs[u].R(i);
    }
  cubes(CSX[i],CSY[i],CEX[i],CEY[i],i);
}

int[] getBC(int x,int y){
  for(int u=0;u<boxs.length;u++){
    if(boxs[u].isH(x,y)){
      return boxs[u].c;
    }
  }
  int[] tmp = {};
  return tmp;
}

int[] CB(float x1,float y1,float x2,float y2){
  int[] out = new int[0];
  float minX=min(x1,x2);
  float maxX=max(x1,x2);
  float minY=min(y1,y2);
  float maxY=max(y1,y2);
  int bx=int(x1>x2)*-1;
  int by=int(y1>y2)*-1;
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

import java.util.zip.*;
import java.util.*;

//yea it unpacks everything
//but it works

void UPDATE(){
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
      println(sketchPath()+"/"+removefirst(tmp.getName()));
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

String removefirst(String text){
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
