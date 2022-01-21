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

void tantrest(){
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

void tantmath(){
  if(Must==0 && PMust!=0){
    waveEnd=true;
    delaytowave=60;
  }
  if(round==Indexs.length && waveEnd){
    if(Epilog==240){
      NewPartic(new SubText(play.X,play.Y-24,0,-1,60,#FFFFFF,"alright",32.0),true);
    }
    if(Epilog==240+120*1){
      NewPartic(new SubText(play.X,play.Y-24,0,-1,60,#FFFFFF,"i killed god.",32.0),true);
    }
    if(Epilog==240+120*2){
      NewPartic(new SubText(play.X,play.Y,0,-1,60,#FFFFFF,"now what?",32.0),true);
    }
    if(Epilog==240+120*4){
      NewPartic(new SubText(play.X,play.Y,0,-1,60,#FFFFFF,"...",32.0),true);
    }
    if(Epilog==240+120*6){
      AddPartic(1,play.X,play.Y,play.X,-10000,60,#FFFFFF,true);
      for(int ohno=0;ohno<50;ohno++){
        AddPartic(2,play.X,play.Y,random(-10,10),random(-10,10),60,#FFFFFF,true);
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

void nextWave(){
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

void ResartWave(){
  getWave(round);
  PMust=0;
  Must=0;
  enemyDelay=-60;
  waveEnd=false;
}

void arenaSpawn(String name){
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

void getFile(){
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

void getWave(int i){
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
