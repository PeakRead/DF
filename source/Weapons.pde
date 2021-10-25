void WeaponINIC() {
  MOAG = new ArrayList<Weapon>();
  WeaponSprites = new PImage[0];
  //need to find a way of precedualy do this
  MOAG.add(new testgun());
  MOAG.add(new Railgun());
  MOAG.add(new Garant());
  MOAG.add(new Rocket());
  MOAG.add(new shoogun());
  MOAG.add(new devgun());
  MOAG.add(new KILLgun());
  //ADDWeapon(-12,-4,40,10,false,"Weapons/" + "testgun.png");
  //ADDWeapon(-12,-4,0,60,false,"Weapons/" + "Railgun.png");
  //ADDWeapon(-9,-4,0,9,false,"Weapons/" + "Garant.png");
  //ADDWeapon(-9,-2,0,25,false,"Weapons/" + "Rocket.png");
  //ADDWeapon(-9,-2,0,25,false,"Weapons/" + "shoogun.png");
  //ADDWeapon(-7,-2,0,25,false,"Weapons/" + "devgun.png");
}

//void ADDWeapon(int Offx,int Offy,int SDelay,int EDelay,boolean Const,String img){
//  MOAG.add(new Weapon((byte)Offx,(byte)Offy,(byte)SDelay,(byte)EDelay,Const));
//  WeaponSprites = (PImage[])append(WeaponSprites,loadImage(img));
//}

ArrayList<Weapon> MOAG;
PImage[] WeaponSprites;
byte WeaponSellected=0;
byte[] HWeapon = {2, 3, 1, 4};
byte[] curSDelay={0, 0, 0, 0};
byte[] curEDelay={0, 0, 0, 0};

class testgun extends Weapon {
  testgun() {
    Sprite=SloadImage("Weapons/testgun.png");
    WeaOffx=-12;
    WeaOffy=-4;
    WeapW=40;
    WeapH=11;
    SDelay=40;
    EDelay=10;
    Const=false;
  }
  void FIRE() {
    whack(play.X, play.Y, 35, play.PO, 30, true);
  }
}

class Railgun extends Weapon {
  Railgun() {
    Sprite=SloadImage("Weapons/Railgun.png");
    WeaOffx=-12;
    WeaOffy=-4;
    WeapW=35;
    WeapH=7;
    SDelay=0;
    EDelay=60;
    Const=false;
  }
  void FIRE() {
    Hitscan(0, 0, play.PO, true, 50, 70,256);
  }
}

class Garant extends Weapon {
  Garant() {
    Sprite=SloadImage("Weapons/Garant.png");
    WeaOffx=-9;
    WeaOffy=-4;
    WeapW=31;
    WeapH=6;
    SDelay=0;
    EDelay=12;
    Const=false;
  }
  void FIRE() {
    Hitscan(0, 0, play.PO, false, 8, 20,1000);
  }
}

class Rocket extends Weapon {
  Rocket() {
    Sprite=SloadImage("Weapons/Rocket.png");
    WeaOffx=-9;
    WeaOffy=-2;
    WeapW=28;
    WeapH=11;
    SDelay=0;
    EDelay=25;
    Const=false;
  }
  void FIRE() {
    NewPR(play.X, play.Y-12, cos(play.PO)*8, sin(play.PO)*8, 1);
  }
}

class shoogun extends Weapon {
  shoogun() {
    Sprite=SloadImage("Weapons/shoogun.png");
    WeaOffx=-9;
    WeaOffy=-2;
    WeapW=31;
    WeapH=6;
    SDelay=0;
    EDelay=60;
    Const=false;
  }
  void FIRE() {
    for (int i=0; i<5; i++) {
      NewPR(play.X, play.Y-12, cos(play.PO+PI/20-PI/40*i)*10, sin(play.PO+PI/20-PI/40*i)*10, 2);
    }
    for (int i=0; i<5; i++) {
      NewPR(play.X, play.Y-12, cos(play.PO+PI/20-PI/40*i)*8, sin(play.PO+PI/20-PI/40*i)*8, 2);
    }
    play.VX-=cos(play.PO)*8;
    play.VY-=sin(play.PO)*8;
  }
}

class devgun extends Weapon {
  //haha devgun spawn 6*10^7 bosses
  devgun() {
    Sprite=SloadImage("Weapons/devgun.png");
    WeaOffx=-7;
    WeaOffy=-2;
    WeapW=19;
    WeapH=3;
    SDelay=0;
    EDelay=15;
    Const=false;
  }
  void FIRE() {
    AddPartic(1,play.X, play.Y-12, play.X+mouseX-width/2, play.Y+mouseY-height/2, 40, color(255),true);
    NewAI(play.X+mouseX-width/2,play.Y+mouseY-height/2,AINames[DevGun],false);
  }
}

class KILLgun extends Weapon {
  KILLgun() {
    Sprite=SloadImage("Weapons/devgun.png");
    WeaOffx=-7;
    WeaOffy=-2;
    WeapW=19;
    WeapH=3;
    SDelay=15;
    EDelay=1;
    Const=true;
  }
  void FIRE() {
    Hitscan(0, 0, play.PO, true, 8, 99999999,1000);
  }
}

class Weapon {
  byte WeaOffx, WeaOffy;
  byte WeapW,WeapH;
  byte SDelay, EDelay;
  boolean Const;
  PImage Sprite;
  void FIRE() {
  }
  void DRAW() {
    pushMatrix();
    scale(ZOOMER);
    translate(width/2/ZOOMER, height/2/ZOOMER-10);
    rotate(play.PO);
    if (play.PO<-PI/2 || play.PO>PI/2) {
      scale(1, -1);
      image(Sprite, WeaOffx, WeaOffy,WeapW,WeapH);
    } else {
      image(Sprite, WeaOffx, WeaOffy,WeapW,WeapH);
    }
    popMatrix();
  }
}

void Hitscan(int Ofx, int Ofy, float R, boolean Pierce, int lineD, int dmg,float range) {
  float[] tmp = hitscan(R, dmg, Pierce,range);
  AddPartic(1,play.X+Ofx, play.Y-12+Ofy, tmp[0], tmp[1], lineD, color(255),true);
  for (int i=0; i<5; i++) {
    AddPartic(2,tmp[0], tmp[1], random(-1, 1), random(-1, 1), 40, color(255, 255, 0),false);
  }
}

int DevGun=0;

void HWeaponMATH() {
  if (mousePressed && mouseButton==LEFT && curEDelay[WeaponSellected]<1 && !MenuPaused) {
    if (curSDelay[WeaponSellected]>=MOAG.get(HWeapon[WeaponSellected]).SDelay) {
      MOAG.get(HWeapon[WeaponSellected]).FIRE();
      if (!MOAG.get(HWeapon[WeaponSellected]).Const) {
        curEDelay[WeaponSellected]=MOAG.get(HWeapon[WeaponSellected]).EDelay;
        curSDelay[WeaponSellected]=0;
      }
    } else {
      curSDelay[WeaponSellected]++;
    }
  } else {
    if (MOAG.get(HWeapon[WeaponSellected]).Const && curSDelay[WeaponSellected]>=MOAG.get(HWeapon[WeaponSellected]).SDelay) {
      curEDelay[WeaponSellected]=MOAG.get(HWeapon[WeaponSellected]).EDelay;
      curSDelay[WeaponSellected]=0;
    } else {
      curSDelay[WeaponSellected]=0;
    }
  }
  for (int i=0; i<HWeapon.length; i++) {
    if (curEDelay[i]>=1) {
      curEDelay[i]--;
    }
  }
}

void HWeaponDRAW() {
  MOAG.get(HWeapon[WeaponSellected]).DRAW();
  float rescale=(float)Configs.get("GuiScale")/100;
  pushMatrix();
  scale(rescale);
  noFill();
  stroke(0, 0, 255, 100);
  strokeWeight(5);
  strokeCap(SQUARE);
  arc(width/2*(1/rescale), (height/2-12)*(1/rescale), 36, 36, PI/2-PI*curSDelay[WeaponSellected]/MOAG.get(HWeapon[WeaponSellected]).SDelay, PI/2);
  arc(width/2*(1/rescale), (height/2-12)*(1/rescale), 36, 36, PI/2-PI*curEDelay[WeaponSellected]/MOAG.get(HWeapon[WeaponSellected]).EDelay, PI/2);
  strokeWeight(2);
  for (int i=0; i<HWeapon.length; i++) {
    arc(width/2*(1/rescale), (height/2-12)*(1/rescale), 44+i*6, 44+i*6, PI/2-PI*curEDelay[i]/MOAG.get(HWeapon[i]).EDelay, PI/2);
  }
  popMatrix();
  strokeWeight(3);
  stroke(50);
  fill(100);
  for (int i=0; i<HWeapon.length; i++) {
    rect(i*80, 0, 80, 40);
  }
  stroke(50);
  fill(120);
  rect(WeaponSellected*80,0,80,40);
  for (int i=0; i<HWeapon.length; i++) {
    PImage tmp=MOAG.get(HWeapon[i]).Sprite;
    byte Width = MOAG.get(HWeapon[i]).WeapW;
    byte Height = MOAG.get(HWeapon[i]).WeapH;
    image(tmp, i*80+40-Width/2, 20-Height/2 ,Width,Height);
  }
  noStroke();
  fill(200, 200, 200);
  for (int i=0; i<HWeapon.length; i++) {
    rect(i*80, 30, 80*curEDelay[i]/MOAG.get(HWeapon[i]).EDelay, 10);
  }
  strokeWeight(1);
  strokeCap(ROUND);
  stroke(0);
  noFill();
}

void whack(float x, float y, float r, float d, float f, boolean player) {
  for (int i=0; i<ListAi.size(); i++) {
    float X = ListAi.get(i).X;
    float Y = ListAi.get(i).Y;
    if (dist(X, Y, x, y)<=r) {
      ListAi.get(i).VX = cos(d)*f;
      ListAi.get(i).VY = sin(d)*f;
    }
  }
  if (player) {
    if (dist(play.X, play.Y, x, y)<=r) {
      play.VX = cos(d)*f;
      play.VY = sin(d)*f;
    }
  }
}

float[] hitscan(float R, int dmg, boolean Pierce,float range) {
  float MT=range;
  int T=-1;
  float OX=play.X;
  float OY=play.Y-12;
  float NX=play.X+cos(R);
  float NY=play.Y-12+sin(R);
  int[] list=CB(OX, OY, NX+cos(R)*range, NY+sin(R)*range);
  for (int p=0; p<list.length; p++) {
    int i=list[p];
    float r = TLineToLine(CSX[i], CSY[i], CEX[i], CEY[i], OX, OY, NX, NY);
    if (r>=0 && r<=1 && CT[i]!=1) {
      float t = TLineToLine(OX, OY, NX, NY, CSX[i], CSY[i], CEX[i], CEY[i]);
      if (t>0 && t<MT & r>=0 && r<=1) {
        MT=t;
      }
    }
  }
  for (int i=0; i<ListAi.size(); i++) {
    AI tmp = ListAi.get(i);
    float R1 = TLineToLine(tmp.X-tmp.W, tmp.Y-tmp.H, tmp.X+tmp.W, tmp.Y, play.X, play.Y-12, play.X+cos(R), play.Y-12+sin(R));
    float R2 = TLineToLine(tmp.X+tmp.W, tmp.Y-tmp.H, tmp.X-tmp.W, tmp.Y, play.X, play.Y-12, play.X+cos(R), play.Y-12+sin(R));
    if (R1>=0 && R1<=1 || R2>=0 && R2<=1) {
      float T1 = TLineToLine(play.X, play.Y-12, play.X+cos(R), play.Y-12+sin(R), tmp.X-tmp.W, tmp.Y-tmp.H, tmp.X+tmp.W, tmp.Y);
      float T2 = TLineToLine(play.X, play.Y-12, play.X+cos(R), play.Y-12+sin(R), tmp.X+tmp.W, tmp.Y-tmp.H, tmp.X-tmp.W, tmp.Y);
      if (min(T1, T2)>0 && min(T1, T2)<MT & !Pierce) {
        MT=min(MT, min(T1, T2));
        T=i;
      }
      if (min(T1, T2)>0 && min(T1, T2)<MT & Pierce) {
        ListAi.get(i).HURT(dmg);
      }
    }
  }
  if (T>=0 && !Pierce) {
    ListAi.get(T).HURT(dmg);
  }
  float[] tmp = {play.X+cos(R)*MT, play.Y-12+sin(R)*MT};
  return tmp;
}
