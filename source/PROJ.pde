ArrayList<PRO> ListPR;
IntList killPR;

void ProjMath() {
  for (int i=0; i<ListPR.size(); i++) {
    ListPR.get(i).math(i);
  }
  killPR.reverse();
  for (int i=0; i<killPR.size(); i++) {
    ListPR.remove(killPR.get(i));
  }
  killPR.clear();
}

void PRR () {
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
  }
  void math(int SID) {
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
    if (random(0, 1)<0.2) {
      AddPartic(4, X, Y, 0, 0, 40, color(#548454), false);
    }
  }
  void render() {
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
  }
  void math(int SID) {
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
    //VY+=0.2;
  }
  void render() {
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
  }
  void math(int SID) {
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
    VY+=0.2;
    AddPartic(3, X, Y, 0, 0, 8, color(#AAAA00), false);
  }
  void render() {
    stroke(#FFFF00);  
    fill(#FFFF00);
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
  }
  void math(int SID) {
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
  }
  void render() {
    fill(#FFFFFF);
    stroke(#00FFFF);
    circle(X, Y, 20);
    rect(X-W, Y-H, W*2, H*2);
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
  }
  boolean Stuck=false;
  void math(int SID) {
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
      VY+=0.2;
      X+=VX;
      Y+=VY;
    }
  }
  void render() {
    noStroke();
    fill(#FF0000);
    circle(X, Y, 20);
    fill(#FF7C00);
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
  }
  void math(int SID) {
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
  }
  void render() {
    noStroke();
    fill(#00FFFF);
    circle(X, Y, Bombtimer/2);
    fill(#00AAAA);
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
  }
  int bombtimer=90;
  void math(int SID) {
    if (bombtimer%5==0) {
      expd(X, Y-60,120, 16, 0, true);
    }
    if (bombtimer==0) {
      killPR.append(SID);
      return;
    }
    bombtimer--;
    X+=VX;
    Y+=VY;
    //VY+=0.2;
  }
  void render() {
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
  }
  float rotate=0;
  int fuel=30;
  void math(int SID) {
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
  }
  void render() {
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
  }
  float fuel=280;
  float rotate=0;
  void math(int SID) {
    Cont(W, H, 12);
    rotate = atan2(play.Y-Y, play.X-X);
    AddPartic(3, X+random(-W/2, W/2), Y+random(-H/2, H/2), 0, -3, 40, color(#FF0000), false);
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
  }
  void render() {
    if (DebugDraw) {
      noStroke();
      fill(255);
      rect(X-W, Y-H, W*2, H*2);
    }
    if (fuel>240) {
    fill(#FF0000,map(fuel,280,240,0,255));
    circle(X, Y, W*2.5);
    fill(#FF8D00,map(fuel,280,240,0,255));
    circle(X, Y, W*1.5);
    }else{
    fill(#FF0000, fuel/120*255);
    circle(X, Y, W*2.5);
    fill(#FF8D00, fuel/120*255);
    circle(X, Y, W*1.5);
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
  }
  int fuel=240;
  float rotate=0;
  void math(int SID) {
    Cont(W, H, 12);
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
  }
  void render() {
    if (DebugDraw) {
      noStroke();
      fill(255);
      rect(X-W, Y-H, W*2, H*2);
    }
    fill(#00A8FF,map(fuel,240,200,0,255));
    circle(X, Y, W*2.5);
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
  }
  int fuel=240;
  void math(int SID) {
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
  }
  void render() {
    if (DebugDraw) {
      noStroke();
      fill(255);
      rect(X-W, Y-H, W*2, H*2);
    }
    fill(#FF0000,map(fuel,240,200,0,255));
    circle(X, Y,map(fuel,240,0,16,0));
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
  boolean Cont(float W, float H, int dmg) {
    if (X+W>play.X-6 && X-W<play.X+6 && Y+H>play.Y-24 && Y-H<play.Y+0) {
      AThurt(dmg);
      return true;
    }
    return false;
  }
  void math(int SID) {/*MATH GOES HERE*/
  }
  void render() {
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
  boolean EnCo(float X, float Y, float W, float H, int dmg) {
    for (int i=0; i<ListAi.size(); i++) {
      AI tmp = ListAi.get(i);
      if (X>tmp.X-tmp.W && X<tmp.X+tmp.W && Y>tmp.Y-tmp.H && Y<tmp.Y) {
        ListAi.get(i).HURT(dmg);
        return true;
      }
    }
    return false;
  }
}

void expd(float x, float y, float r, int d, float f, boolean player) {
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
      ListAi.get(i).Y -= 0.1;
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

void NewPR(float X, float Y, float VX, float VY, int T) {
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
  }
}

boolean Coll(float OX, float OY, float NX, float NY) {
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
