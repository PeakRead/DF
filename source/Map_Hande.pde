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

void openMap(String MAP) {
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
    EX = append(EX, float  (BgetI(DATA, 0 +Header, 2)));
    EY = append(EY, float  (BgetI(DATA, 2 +Header, 2)));
    ET = append(ET, (yes[BgetI(DATA, 4 +Header, 2)]));
    ES = append(ES, int    (BgetI(DATA, 6 +Header, 2)));
    EM = (boolean[])append(EM, boolean(BgetI(DATA, 8 +Header, 1)));
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
    PROPL[i] = new PROP(float(BgetI(DATA, 0 +Header, 2)), float(BgetI(DATA, 2 +Header, 2)), yes[BgetI(DATA, 4 +Header, 2)]);
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

void Restart() { 
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

void trigger() {
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

void Atrigger() {
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
int BgetI(byte[] DATA, int index, int size) {
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

byte[] BsetI(int num, int size) {
  String Num = binary(num);
  byte[] DATA = new byte[size];
  for (int i=0; i<size; i++) {
    String subData="";
    for (int u=0; u<8; u++) {
      subData+=Num.charAt(i*8+u+(4-size)*8);
    }
    DATA[i]=byte(unbinary(subData));
  }
  return DATA;
}

String BgetS(byte[] DATA, int index, int size) {
  String num="";
  for (int i=0; i<size; i++) {
    num+=char(DATA[i+index]);
  }
  return num;
}

boolean DOORSOP=false;

//OPEN>><<CLOSE

door[] MAD;

void doorM() {
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
  void Math() {
    if (DOORSOP) {
      if (timer>0) {
        timer--;
      }
    } else {
      if (timer<Mimer) {
        timer++;
      }
    }
    CSX[Cool]=lerp(EX, SX, float(timer)/Mimer)-OCSX;
    CSY[Cool]=lerp(EY, SY, float(timer)/Mimer)-OCSY;
    CEX[Cool]=lerp(EX, SX, float(timer)/Mimer)-OCEX;
    CEY[Cool]=lerp(EY, SY, float(timer)/Mimer)-OCEY;
    PROPL[Prop].X=lerp(EX, SX, float(timer)/Mimer)-OPX;
    PROPL[Prop].Y=lerp(EY, SY, float(timer)/Mimer)-OPY;
    updBx(Cool);
  }
}

PShape loadPly(String filepath) {
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
  color[] colors = new color[0];
  while (!(info[header].equals("end_header"))) {
    String[] arg = split(info[header], ' ');
    if (arg[0].equals("element")) {
      if (arg[1].equals("vertex")) {
        vertexCount = int(arg[2]);
      }
      if (arg[1].equals("face")) {
        faceCount = int(arg[2]);
      }
    }
    header++;
  }
  header++;
  for (int i=0; i<vertexCount; i++) {
    String[] arg = split(info[header], ' ');
    vertexs = (PVector[])append(vertexs, new PVector(float(arg[0]), float(arg[1]), float(arg[2])));
    normal  = (PVector[])append(normal, new PVector(float(arg[3]), float(arg[4]), float(arg[5])));
    colors  = (color[]  )append(colors, color  (float(arg[6]), float(arg[7]), float(arg[8])));
    header++;
  }
  obj = createShape(GROUP);
  for (int i=0; i<faceCount; i++) {
    PShape tmp = createShape();
    String[] arg = split(info[header], ' ');
    tmp.beginShape();
    tmp.noStroke();
    for (int u=0; u<int(arg[0]); u++) {
      PVector V=vertexs[int(arg[u+1])];
      PVector N=normal[int(arg[u+1])];
      tmp.fill(colors[int(arg[u+1])]);
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
