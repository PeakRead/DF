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

void tantrest(){
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

void tantmath(){
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

void nextWave(){
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
