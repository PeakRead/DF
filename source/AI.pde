ArrayList<AI> ListAi;
IntList kill;
int Must=0;
int PMust=0;
IntList BOSSHP;
IntList BOSSID;

String[] AINames={"Bug", "Fly", "Target", "Spewer", "testBoss", "Maze", "Laze", "Maze_Boss", "Laze_Boss", "tower", "napalm", "Spirit", "Guardian", "Crab", "Piller", "Supply", "Supply_Boss", "Electron", "Limbo","Lust","Gluttony","Greed","Anger","Heresy","Hatred","Violence","Fraud","Treachery","Zenith"};
boolean[] Sgroun={true ,false , true    , true    , true      , false , false , false      , false      , true   , true    , false   , true      , true  , true    , false   , false        , false     , false  ,false ,false     ,false  ,false  ,false   ,false   ,false     ,false  ,false      ,false};
String[] SupplySummon={"Fly", "Bug", "Spewer", "tower", "Maze", "Laze"};
String[] LimboSummoners = {"Limbo","Lust","Gluttony","Greed","Anger","Heresy","Violence","Fraud","Treachery"};
  
void AIMath() {
  PMust=Must;
  for (int i=0; i<ListAi.size(); i++) {
    try {
      ListAi.get(i).math(i);
      AI tmp = ListAi.get(i);
      if (tmp.X>10000 | tmp.X<-10000 | tmp.Y>10000 | tmp.Y<-10000) {
        kill.append(i);
      }
    }
    catch(Exception e) {
      PrintCon("sorry for that");
      kill.append(i);
      AddPartic(1, ListAi.get(i).X, ListAi.get(i).Y, ListAi.get(i).X, -10000, 60, #FFFFFF, false);
      AddPartic(5, ListAi.get(i).X, ListAi.get(i).Y, 128, 0, 60, #FFFFFF, true);
      for (int ohno=0; ohno<50; ohno++) {
        AddPartic(2, ListAi.get(i).X, ListAi.get(i).Y, random(-10, 10), random(-10, 10), 60, #FFFFFF, false);
      }
      PrintCon(e.toString());
      ErrorTimer=120;
    }
  }
  for (int i=0; i<ListAi.size(); i++) {
    try {
      if (ListAi.get(i).getClass()==Class.forName("ProjectDF$Spirit")) {
        Spirit tmp = (Spirit)ListAi.get(i);
        if (kill.hasValue(tmp.Connected)) {
          tmp.Con=false;
          tmp.hurte=true;
          ListAi.set(i, tmp);
        }
      }
    }
    catch(Exception e) {
    }
  }
  kill.reverse();
  for (int i=0; i<kill.size(); i++) {
    AI tmp = ListAi.get(kill.get(i));
    BOSSID.reverse();
    BOSSHP.reverse();
    for (int u=0; u<BOSSID.size(); u++) {
      if (kill.get(i)==BOSSID.get(u)) {
        BOSSID.remove(u);
        BOSSHP.remove(u);
        break;
      }
      if (kill.get(i)<BOSSID.get(u)) {
        BOSSID.set(u, BOSSID.get(u)-1);
      }
    }
    BOSSID.reverse();
    BOSSHP.reverse();
    if (dist(tmp.X, tmp.Y, play.X, play.Y)<=200 && play.HP>0) {
      AddPartic(1, play.X+random(-5, 5), play.Y-12+random(-5, 5), tmp.X+random(-5, 5), tmp.Y-tmp.H/2+random(-5, 5), 60, color(255, 0, 0), true);
      if (play.HP+5>100) {
        play.HP=100;
      } else {
        play.HP+=5;
      }
    }
    if (tmp.M) {
      Must--;
    }
  }
  for (int i=0; i<kill.size(); i++) {
    ListAi.remove(kill.get(i));
  }
  kill.clear();
}

void AIR() {
  for (int i=0; i<ListAi.size(); i++) {
    ListAi.get(i).render();
    if (DebugDraw) {
      text(ListAi.get(i).HP, ListAi.get(i).X+10, ListAi.get(i).Y-10);
    }
  }
}

class Bug extends AI {
  Bug(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=10;
    H=14;
    HP=20;
    T=0;
    Animr = new SelfAnim(EAR.get("Bug"));
  }
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    Walk(0.3, 0.5, 6);
    Cont(W, H, 25);
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    pushMatrix();
    if (VX>0) {
      scale(-1, 1);
      translate(-X*2, 0);
    }
    Animr.Anim(true, OG>3);
    Animr.DIMG(X, Y, W, H, true, OG>3, #FFFFFF);
    popMatrix();
  }
}

class Fly extends AI {
  Fly(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=6;
    H=12;
    HP=12;
    T=4;
    Animr = new SelfAnim(EAR.get("Fly"));
  }
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    VX+=cos(atan2(play.Y-Y, play.X-X))*0.4;
    VY+=sin(atan2(play.Y-Y, play.X-X))*0.2;
    VX=constrain(VX, -10, 10);
    VY=constrain(VY, -10, 10);
    Cont(W, H, 15);
    Phys(W, H, false);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    Animr.Anim(false, true);
    Animr.DIMG(X, Y, W, H, false, true, #FFFFFF);
  }
}

class Target extends AI {
  Target(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=6;
    H=24;
    HP=9999;
    Animr = new SelfAnim(EAR.get("Target"));
  }
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    Fall();
    Cont(W, H, 1);
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
    NewPartic(new StandImg(X, Y, random(-12, 12), random(-12, 12), 15, #FFFFFF, "uranium.png"), true);
    NewSPr(new hurtbox(X, Y, 20, 9000, -PI/4, 0, 15, 5));
    NewSPr(new hurtbox(X, Y, 20, 9000, PI/4, 0, 15, 5));
  }
  void render() {
    stroke(0);
    fill(255);
    rect(X-W, Y-H, W*2, H);
    Animr.Anim(false, false);
    Animr.DIMG(X, Y, W, H, false, false, #FFFFFF);
  }
  void HURT(int dmg)
  {
    if (!hurte) {
      return;
    }
    HP-=dmg;
    Animr.Action(0);
    for (int B=0; B<5; B++) {
      AddPartic(4, X, Y, random(-1, 1), random(-8, -2), 50, color(255, 0, 0), true);
    }
    if (play.regenera==0) {
      if (dist(X, Y, play.X, play.Y)<=200 && play.HP>0) {
        AddPartic(1, play.X+random(-5, 5), play.Y-12+random(-5, 5), X+random(-5, 5), Y-H/2+random(-5, 5), 60, color(255, 0, 0), true);
        if (play.HP+dmg/4>100) {
          play.HP=100;
        } else {
          play.HP+=dmg/4;
        }
      }
    } else {
      if (random(1, 100)<50 && play.HP>0) {
        NewPR(X, Y-H/2, random(-5, 5), random(-5, 5), 10);
      }
    }
  }
}

class Spewer extends AI {
  int cooldown=80;
  Spewer(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=6;
    H=24;
    HP=34;
    T=5;
    Animr = new SelfAnim(EAR.get("Spewer"));
  }
  void math(int SID) {
    if (dist(X, Y, play.X, play.Y)>150) {
      Walk(0.01, 0.01, 1);
    }
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    Fall();
    Cont(W, H, 1);
    Phys(W, H, true);
    if (cooldown==0) {

      NewPR(X+W, Y-H/2, cos(atan2(play.Y-Y+H/2-12, play.X+6-X-W))*6, sin(atan2(play.Y-Y+H/2-8, play.X+6-X-W))*6, 0);
      cooldown=80;
    } else {
      cooldown--;
    }
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    pushMatrix();
    if (VX<0) {
      scale(-1, 1);
      translate(-X*2, 0);
    }
    Animr.Anim(Gr && abs(VX)<0.5, false);
    Animr.DIMG(X, Y, W, H, Gr && abs(VX)<0.5, false, #FFFFFF);
    popMatrix();
  }
}

class testBoss extends AI {
  testBoss(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=16;
    H=34;
    HP=600;
    BOSSHP.append(HP);
    BOSSID.append(ListAi.size());
  }
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    Fall();
    Cont(W, H, 1);
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  void render() {
    stroke(0);
    fill(255);
    rect(X-W, Y-H, W*2, H);
  }
}

class Maze extends AI {
  boolean Enraged=false;
  Maze(float nX, float nY, boolean nM, boolean Boss) {
    X=nX;
    Y=nY;
    M=nM;
    W=16;
    H=32;
    T=1;
    if (Boss) {
      HP=800;
      W=32;
      H=64;
      BOSSHP.append(HP);
      BOSSID.append(ListAi.size());
    } else {
      HP=200;
    };
    Animr = new SelfAnim(EAR.get("Maze"));
  }
  int cooldown = 370;
  int attack = 1;
  void math(int SID) {
    if (HP<=0) {
      if (Gr || HP<=-1000) {
        AddPartic(5, X, Y, 32, 0, 15, #00FF00, false);
        kill.append(SID);
        for (int all=0; all<ListAi.size(); all++) {
          try {
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Maze")) {
              Maze tmp = (Maze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Laze")) {
              Laze tmp = (Laze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Supply")) {
              Supply tmp = (Supply)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
          }
          catch(Exception e) {
          }
        }
        return;
      }
      VY+=0.2;
    } else {
      Gr=false;
      if (cooldown>200 && cooldown<300) {
        VX+=(play.X-X+cos((float)frameCount/20)*64)/10;
        VY+=(play.Y-Y+sin((float)frameCount/20)*64-200)/10;
        VX=VX/10*9;
        VY=VY/10*9;
      } else {
        VX=0;
        VY=0;
      }
      if (cooldown<200 && cooldown>80) {
        float N=((float)cooldown-80)/120*8;
        if (attack==0) {
          AddPartic(3, X, Y-H/2, cos(0*PI/2)*N, sin(0*PI/2)*N, 15, #00FFFF, true);
          AddPartic(3, X, Y-H/2, cos(1*PI/2)*N, sin(1*PI/2)*N, 15, #00FFFF, true);
          AddPartic(3, X, Y-H/2, cos(2*PI/2)*N, sin(2*PI/2)*N, 15, #00FFFF, true);
          AddPartic(3, X, Y-H/2, cos(3*PI/2)*N, sin(3*PI/2)*N, 15, #00FFFF, true);
        } else {
          AddPartic(3, X, Y-H/2, cos(0*PI/2+PI/4)*N, sin(0*PI/2+PI/4)*N, 15, #00FFFF, true);
          AddPartic(3, X, Y-H/2, cos(1*PI/2+PI/4)*N, sin(1*PI/2+PI/4)*N, 15, #00FFFF, true);
          AddPartic(3, X, Y-H/2, cos(2*PI/2+PI/4)*N, sin(2*PI/2+PI/4)*N, 15, #00FFFF, true);
          AddPartic(3, X, Y-H/2, cos(3*PI/2+PI/4)*N, sin(3*PI/2+PI/4)*N, 15, #00FFFF, true);
        }
      }
      if (!Enraged) {
        if (cooldown<80) {
          if (attack==0) {
            if (cooldown%10==0) {
              for (int i=0; i<4; i++) {
                NewPR(X, Y-H/2, cos(i*PI/2+(float)cooldown/60*PI)*5, sin(i*PI/2+(float)cooldown/60*PI)*5, 3);
              }
            }
          } else {
            if (cooldown%10==0) {
              float target = atan2(play.Y-Y, play.X-X);
              for (int i=0; i<3; i++) {
                NewPR(X, Y-H/2, cos(target+PI/8*(i-1))*5, sin(target+PI/8*(i-1))*5, 3);
              }
            }
          }
        }
      } else {
        if (cooldown<80) {
          if (cooldown%10==0) {
            for (int i=0; i<8; i++) {
              NewPR(X, Y-H/2, cos(i*PI/4+(float)cooldown/60)*5, sin(i*PI/4+(float)cooldown/60)*5, 3);
            }
          }
        }
      }
      cooldown--;
      if (cooldown<0) {
        cooldown=370;
        attack = (int)random(0, 2);
      }
    }
    Cont(W, H, 1);
    Phys(W, H, false);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    Animr.Anim(false, true);
    if (!Enraged) {
      Animr.DIMG(X, Y, W, H, false, true, #FFFFFF);
    } else {
      Animr.DIMG(X, Y, W, H, false, true, #FFAAAA);
      stroke(255, 0, 0);
      noFill();
      circle(X+random(-2, 2), Y-H/2+random(-2, 2), 36);
    }
  }
}

class Laze extends AI {
  boolean Enraged=false;
  Laze(float nX, float nY, boolean nM, boolean Boss) {
    X=nX;
    Y=nY;
    M=nM;
    W=16;
    H=32;
    T=2;
    if (Boss) {
      HP=800;
      W=32;
      H=64;
      BOSSHP.append(HP);
      BOSSID.append(ListAi.size());
    } else {
      HP=200;
    }
    Animr = new SelfAnim(EAR.get("Laze"));
  }
  int cooldown = 420;
  int attack = 1;
  float LastPlayer = 0;
  float Tx;
  float Ty;
  void math(int SID) {
    if (HP<=0) {
      if (Gr || HP<=-1000) {
        AddPartic(5, X, Y, 32, 0, 15, #00FF00, false);
        kill.append(SID);
        for (int all=0; all<ListAi.size(); all++) {
          try {
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Maze")) {
              Maze tmp = (Maze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Laze")) {
              Laze tmp = (Laze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Supply")) {
              Supply tmp = (Supply)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
          }
          catch(Exception e) {
          }
        }
        return;
      }
      VY+=0.2;
    } else {
      Gr=false;
      if (cooldown>200  && cooldown<350) {
        VX+=(play.X-X+cos(-(float)frameCount/20)*64)/10;
        VY+=(play.Y-Y+sin(-(float)frameCount/20)*64-200)/10;
        VX=VX/10*9;
        VY=VY/10*9;
      } else {
        VX=0;
        VY=0;
      }
      if (cooldown==200) {
        if (attack==0) {
          Tx=X;
          Ty=Y;
        } else {
          LastPlayer = atan2(play.Y-Y, play.X-X);
        }
      }
      if (cooldown<200 && cooldown>80) {
        if (attack==0 || Enraged) {
          Tx=(play.X+Tx)/2;
          Ty=(play.Y-12+Ty)/2;
        } else {
          float[] tmp=Enyscan(LastPlayer+PI/4, true, 0, -H/2);
          AddPartic(1, X, Y-H/2, tmp[0], tmp[1], 2, color(100, 0, 0), true);
          tmp=Enyscan(LastPlayer-PI/4, true, 0, -H/2);
          AddPartic(1, X, Y-H/2, tmp[0], tmp[1], 2, color(100, 0, 0), true);
        }
      }
      if (cooldown<80) {
        if (!Enraged) {
          if (attack==0) {
            if ((cooldown+10)%20==0) {
              Tx=play.X;
              Ty=play.Y;
            }
            if (cooldown%20==0) {
              float[] tmp=Enyhitscan(atan2(Ty-Y, Tx-X), 25, true, 0, -H/2);
              AddPartic(1, X, Y-H/2, tmp[0], tmp[1], 40, color(255, 0, 0), true);
            }
          } else {
            float[] tmp=Enyhitscan(LastPlayer+PI/4*((float)cooldown/80), 15, true, 0, -H/2);
            AddPartic(1, X, Y-H/2, tmp[0], tmp[1], 40, color(255, 0, 0), true);
            tmp=Enyhitscan(LastPlayer-PI/4*((float)cooldown/80), 15, true, 0, -H/2);
            AddPartic(1, X, Y-H/2, tmp[0], tmp[1], 40, color(255, 0, 0), true);
          }
        } else {
          if (cooldown%8==0) {
            Tx=(play.X+Tx*2)/3;
            Ty=(play.Y-12+Ty*2)/3;
            float[] tmp=Enyhitscan(atan2(Ty-Y, Tx-X), 25, true, 0, -H/2);
            AddPartic(1, X, Y-H/2, tmp[0], tmp[1], 40, color(255, 0, 0), true);
          }
        }
      }
      cooldown--;
      if (cooldown<0) {
        cooldown=420;
        attack = (int)random(0, 2);
      }
    }
    Cont(W, H, 1);
    Phys(W, H, false);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    Animr.Anim(false, true);
    if (!Enraged) {
      Animr.DIMG(X, Y, W, H, false, true, #FFFFFF);
    } else {
      Animr.DIMG(X, Y, W, H, false, true, #FFAAAA);
      stroke(255, 0, 0);
      noFill();
      circle(X+random(-2, 2), Y-H/2+random(-2, 2), 36);
    }
    if (cooldown<200 & (attack == 0 || Enraged)) {
      stroke(#B703FF);
      noFill();
      circle(Tx, Ty, 16);
      line(Tx-20, Ty, Tx+20, Ty);
      line(Tx, Ty-20, Tx, Ty+20);
      stroke(#FF0000, 100);
      line(X, Y-H/2, Tx, Ty);
    }
  }
}

class Tower extends AI {
  int cooldown=200;
  Tower(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=12;
    H=48;
    HP=34;
    T=3;
    Animr = new SelfAnim(EAR.get("Tower"));
  }
  float Tx, Ty;
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    Fall();
    //Walk(0.3,0.5,6);
    //Phys(W,H,true);
    //Cont(W,H,35);
    if (dist(X, Y, play.X, play.Y)<150) {
      Walk(-1, -1, 1);
    }
    Cont(W, H, 45);
    Phys(W, H, true);
    if (cooldown==0) {
      float[] tmp=Enyhitscan(atan2(Ty-Y+12, Tx-X), 25, true, 0, -H/2);
      AddPartic(1, X, Y-H+6, tmp[0], tmp[1], 40, color(255, 0, 0), true);
      cooldown=200;
    }
    if (cooldown>40 && cooldown<80) {
      float[] tmp=Enyscan(atan2(Ty-Y+12, Tx-X)+PI/8*(((float)cooldown-40)/40), true, 0, -H/2);
      AddPartic(1, X, Y-H+6, tmp[0], tmp[1], 2, color(200, 0, 0), false);
      tmp=Enyscan(atan2(Ty-Y+12, Tx-X)-PI/8*(((float)cooldown-40)/40), true, 0, -H/2);
      AddPartic(1, X, Y-H+6, tmp[0], tmp[1], 2, color(200, 0, 0), false);
      Tx=play.X;
      Ty=play.Y;
    }
    if (cooldown<80) {
      float[] tmp=Enyscan(atan2(Ty-Y+12, Tx-X), true, 0, -H/2);
      AddPartic(1, X, Y-H+6, tmp[0], tmp[1], 2, color(100, 0, 0), true);
    }
    cooldown--;
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    Animr.Anim(abs(VX)>2, false);
    Animr.DIMG(X, Y, W, H, abs(VX)>2, false, #FFFFFF);
  }
}

class Napalm extends AI {
  Napalm(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=10;
    H=20;
    HP=1;
    Animr = new SelfAnim(EAR.get("Napalm"));
  }
  int dieng=0;
  void math(int SID) {
    if (HP<=0 && dieng==0) {
      Animr.Action(0);
      dieng=1;
    }
    if (dieng>0) {
      dieng++;
      if (dieng==36) {
        for (int i=0; i<30; i++) {
          float T=random(5, 15);
          float R=random(-PI, PI);
          AddPartic(2, X, Y, cos(R)*T, sin(R)*T, 40, color(#FF0000), false);
        }
        for (int i=0; i<7; i++) {
          //float T=random(5,15);
          //float R=random(-PI,PI);
          //NewPartic(new Line(X,Y-12,X+sin((i-4)*PI/12)*999,Y-cos((i-4)*PI/12)*999,40,color(#FF0000)),false);
          NewPR(X, Y-12, sin((i-3)*PI/12)*3, -cos((i-3)*PI/12)*6, 4);
        }
        AddPartic(5, X, Y, 100, 0, 40, color(#FF0000), true);
        kill.append(SID);
        return;
      }
    }
    Fall();
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    Animr.Anim(false, false);
    Animr.DIMG(X, Y, W, H, false, false, #FFFFFF);
  }
}

class Spirit extends AI {
  Spirit(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=24;
    H=48;
    HP=90;
  }
  int Cooldown=0;
  int Connected=0;
  boolean Con=false;
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    if (!Con) {
      hurte=true;
    }
    if (Cooldown==0 & !Con) {
      int NUM=floor(random(0, ListAi.size()));
      try {
        if (SID!=NUM && !(ListAi.get(NUM).getClass()==Class.forName("ProjectDF$Spirit") || ListAi.get(NUM).getClass()==Class.forName("ProjectDF$Hatred"))) {
          Connected=NUM;
          Con=true;
          hurte=false;
          Cooldown=240;
        }
      }
      catch(Exception e) {
      }
    }
    try {
      if (ListAi.get(Connected).getClass()==Class.forName("ProjectDF$Spirit") || ListAi.get(Connected).getClass()==Class.forName("ProjectDF$Hatred")) {
        Con=false;
        hurte=true;
      }
    }
    catch(Exception e) {
    }
    if (Cooldown>0 & !Con) {
      Cooldown--;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if (frameCount%2==0) {
      NewPR(X, Y-H/2, -cos(R), -sin(R), 5);
    }
    if (dist(X, Y, play.X, play.Y)<150) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>350) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2);
    VY=constrain(VY, -2, 2);
    VY-=0.01;
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  void render() {
    stroke(#00FFFF);
    fill(#00CCCC);
    rect(X-W, Y-H, W*2, H);
    if (Con) {
      try {
        AI tmp = ListAi.get(Connected);
        AddPartic(1, X, Y-H/2, tmp.X, tmp.Y, 1, color(#00FFFF, 100), true);
        stroke(color(#00FFFF));
        noFill();
        quad(X-64, Y-H/2, X, Y-64-H/2, X+64, Y-H/2, X, Y+64-H/2);
      }
      catch(Exception e) {
        hurte=true;
      }
    }
  }
}

class Guardian extends AI {
  int cooldown=360;
  int attack=0;
  int intro=240;
  Guardian(float nX, float nY, boolean nM) {
    Bresistance=0;
    X=nX;
    Y=nY;
    M=nM;
    W=50;
    H=100;//1600
    HP=5000;
    T=3;
    hurte=false;
    Animr = new SelfAnim(EAR.get("Guardian"));
  }
  void math(int SID) {
    if (intro>0 && HP>0) {
      intro--;
      if (intro<120) {
        float R=random(-PI, PI);
        float D=random(64, 128);
        NewPartic(new Wind(X-cos(R)*D, Y-H/2-sin(R)*D, cos(R)*D/10, sin(R)*D/10, 10, #FFFFFF), true);
      }
    }
    if (intro==1 && HP>0) {
      BOSSHP.append(HP);
      BOSSID.append(SID);
      hurte=true;
    }
    if (HP<=0) {
      float R=random(-PI, PI);
      float D=random(intro/3, intro);
      NewPartic(new Wind(X, Y-H/2, cos(R)*D/3, sin(R)*D/3, 10, #FFFFFF), true);
      intro++;
      cooldown=900;
      if (intro>120) {
        NewPartic(new Explode(X, Y-H/2, 128, 0, 60, #D80B0B), true);
        NewPartic(new Explode(X, Y-H/2, 128+64, 0, 60, #D8560B), true);
        NewPartic(new Explode(X, Y-H/2, 128+128, 0, 60, #D8C10B), true);
        kill.append(SID);
        return;
      }
    }
    cooldown--;
    if (cooldown<30 && attack==0) {
      float R = random(-PI/4, PI/4)-PI/2;
      NewPR(X, Y-H/2, cos(R)*8, sin(R)*8, 7);
    }
    if (cooldown%120==0 && attack==1) {
      NewPR(X, Y, 8, 0, 6);
      NewPR(X, Y, -8, 0, 6);
    }
    if (cooldown==0 && attack==2) {
      NewPR(X, Y-H/2, 0, 0, 8);
    }
    if (cooldown==0 && attack==3) {
      for (int i=0; i<10; i++) {
        NewPR(X, Y-H/2, i*PI/5, 0, 9);
      }
    }
    if (cooldown==0) {
      attack=floor(random(0, 4));
      cooldown=120;
    }
    Fall();
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    Animr.Anim(false, false);
    Animr.DIMG(X, Y, W, H, false, false, #FFFFFF);
    if (attack == 0) {
      fill(255);
    }
    if (attack == 1) {
      fill(#FFA600);
    }
    if (attack == 2) {
      fill(#FF0000);
    }
    if (attack == 3) {
      fill(#00C5FF);
    }
    circle(X, Y-H/2, max(0, map(intro, 0, 120, 64, 0)));
  }
}

class Crab extends AI {
  int cooldown=400;
  float PX=0;
  float PY=0;
  Crab(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=32;
    H=48;//1600
    HP=400;//1000 is too much for you
    T=3;
    Animr = new SelfAnim(EAR.get("Crab"));
  }
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    cooldown--;
    if (cooldown==0) {
      cooldown=400;
      expd(PX, PY, 128, 30, 20, true);
      NewPartic(new Line(X, Y, X, Y-2000, 60, #e8ff00, 5), false);
      NewPartic(new Line(PX, PY, PX, PY-2000, 60, #e8ff00, 5), false);
    }
    Fall();
    if (dist(X, Y, play.X, play.Y)<128) {
      Walk(0.0, -0.6, 0.0);
    }
    Cont(W, H, 15);
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    pushMatrix();
    if (VX<0) {
      scale(-1, 1);
      translate(-X*2, 0);
    }
    Animr.Anim(abs(VX)>0.3, false);
    Animr.DIMG(X, Y, W, H, abs(VX)>0.3, false, #FFFFFF);
    popMatrix();
    if (cooldown<255) {
      stroke(#e8ff00, 255-cooldown);
      strokeWeight((255-cooldown)/25.5);
      line(X, Y, X, Y-2000);
      line(PX, PY, PX, PY-2000);
      if (cooldown>=30) {
        PX=play.X;
        PY=play.Y;
      }
      strokeWeight(1);
    }
  }
}

class Piller extends AI {
  boolean Enranged=false;
  int AngyTimer = 300;
  Piller(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=30;
    H=30;
    HP=200;
    T=0;
    Animr = new SelfAnim(EAR.get("Piller"));
  }
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    if (!Enranged) {
      if (play.Gr) {
        if (AngyTimer<300) {
          AngyTimer+=3;
        }
      } else {
        AngyTimer--;
      }
      if (AngyTimer<=0) {
        Enranged=true;
        AngyTimer=100;
      }
    }
    if (Enranged) {
      if (AngyTimer<=0) {
        float R=atan2(play.Y-Y-5, play.X-X);
        for (int i=0; i<3; i++) {
          float Rand1=random(-PI/10, PI/10);
          float Rand2=random(-2, 2);
          NewPR(X, Y-8, cos(R+Rand1)*(12+Rand2), sin(R+Rand1)*(12+Rand2), 11);
        }
        AngyTimer=100;
      }
      AngyTimer--;
    }
    Walk(0.5, 0.7, 3);
    Cont(W, H, 35);
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
      fill(0);
      text(AngyTimer, X, Y+30);
    }
    pushMatrix();
    if (VX>0) {
      scale(-1, 1);
      translate(-X*2, 0);
    }
    Animr.Anim(true, false);
    if (Enranged) {
      Animr.DIMG(X, Y, W, H, true, false, #FFAAAA);
      stroke(255, 0, 0);
      noFill();
      circle(X+random(-2, 2), Y-H/2+random(-2, 2), 60);
    } else {
      Animr.DIMG(X, Y, W, H, true, false, #FFFFFF);
    }
    popMatrix();
  }
}

class Nucliy extends AI {
  //oh boy
  Nucliy(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=24;
    H=48;
    HP=90;
  }
  int Cooldown=0;
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
  }
}

class Supply extends AI {
  Supply(float nX, float nY, boolean nM, boolean Boss) {
    X=nX;
    Y=nY;
    M=nM;
    W=36;
    H=36;
    if (Boss) {
      HP=1900;
      W=48;
      H=48;
      BOSSHP.append(HP);
      BOSSID.append(ListAi.size());
    } else {
      HP=260;
    }
    Animr = new SelfAnim(EAR.get("Supply"));
  }
  boolean Enraged=false;
  int delay=300;
  int gun1mode=0;
  int gun1timer=0;
  int gun2mode=0;
  int gun2timer=0;
  void math(int SID) {
    if (HP<=0) {
      if (Gr || HP<=-1000) {
        AddPartic(5, X, Y, 32, 0, 15, #00FF00, false);
        kill.append(SID);
        for (int all=0; all<ListAi.size(); all++) {
          try {
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Maze")) {
              Maze tmp = (Maze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Laze")) {
              Laze tmp = (Laze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
            if (ListAi.get(all).getClass()==Class.forName("ProjectDF$Supply")) {
              Supply tmp = (Supply)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all, tmp);
            }
          }
          catch(Exception e) {
          }
        }
        return;
      }
      VY+=0.2;
    } else {
      //GUN1
      if (gun1mode==0) {
        if (gun1timer>0) {
          if (gun1timer>10) {
            R1=atan2(play.Y-Y+H/3*2, play.X-X-W/3);
            float[] tmp=Enyscan(R1, true, +W/3, -H/3*2);
            AddPartic(1, X+W/3, Y-H/3*2, tmp[0], tmp[1], 2, color(255, 0, 0), true);
            tmp=Enyscan(R1+(gun1timer-10)/200.0, true, +H/3, -H/3*2);
            AddPartic(1, X+W/3, Y-H/3*2, tmp[0], tmp[1], 2, color(255, 0, 0), true);
            tmp=Enyscan(R1-(gun1timer-10)/200.0, true, +H/3, -H/3*2);
            AddPartic(1, X+W/3, Y-H/3*2, tmp[0], tmp[1], 2, color(255, 0, 0), true);
          }
        }
        if (gun1timer==0) {
          float[] tmp=Enyhitscan(R1, 0, true, +W/3, -H/3*2);
          AddPartic(1, X+W/3, Y-H/3*2, tmp[0], tmp[1], 40, color(255, 0, 0), true);
        }
      }
      if (gun1mode==1) {
        R1=atan2(play.Y-Y+H/3*2, play.X-X-W/3);
        if (gun1timer%100==0) {
          NewSPr(new MiniRocket(X+W/3, Y-H/3*2, cos(R1)*2, sin(R1)*2, 0));
          NewSPr(new MiniRocket(X+W/3, Y-H/3*2, cos(R1)*4, sin(R1)*4, 0));
        }
      }
      if (gun1mode==2) {
        R1=atan2(play.Y-Y+H/3*2, play.X-X-W/3);
        if (gun1timer%20==0) {
          NewSPr(new MiniBullet(X+W/3, Y-H/3*2, cos(R1)*4, sin(R1)*4, 0));
        }
      }
      if (gun1timer==0) {
        gun1timer=300;
        gun1mode=round(random(0, 2));
      }
      gun1timer--;
      //GUN2
      if (gun2mode==0) {
        if (gun2timer>0) {
          if (gun2timer>10) {
            R1=atan2(play.Y-Y+H/3*2, play.X-X+W/3);
            float[] tmp=Enyscan(R1, true, -W/3, -H/3*2);
            AddPartic(1, X-W/3, Y-H/3*2, tmp[0], tmp[1], 2, color(255, 0, 0), true);
            tmp=Enyscan(R1+(gun1timer-10)/200.0, true, -W/3, -H/3*2);
            AddPartic(1, X-W/3, Y-H/3*2, tmp[0], tmp[1], 2, color(255, 0, 0), true);
            tmp=Enyscan(R1-(gun1timer-10)/200.0, true, -W/3, -H/3*2);
            AddPartic(1, X-W/3, Y-H/3*2, tmp[0], tmp[1], 2, color(255, 0, 0), true);
          }
        }
        if (gun2timer==0) {
          float[] tmp=Enyhitscan(R1, 0, true, -W/3, -H/3*2);
          AddPartic(1, X-W/3, Y-H/3*2, tmp[0], tmp[1], 40, color(255, 0, 0), true);
        }
      }
      if (gun2mode==1) {
        R1=atan2(play.Y-Y+H/3*2, play.X-X+W/3);
        if (gun2timer%100==0) {
          NewSPr(new MiniRocket(X-W/3, Y-H/3*2, cos(R1)*2, sin(R1)*2, 0));
          NewSPr(new MiniRocket(X-W/3, Y-H/3*2, cos(R1)*4, sin(R1)*4, 0));
        }
      }
      if (gun2mode==2) {
        R1=atan2(play.Y-Y+H/3*2, play.X-X+W/3);
        if (gun2timer%20==0) {
          NewSPr(new MiniBullet(X-W/3, Y-H/3*2, cos(R1)*4, sin(R1)*4, 0));
        }
      }
      if (gun2timer==0) {
        gun2timer=300;
        gun2mode=round(random(0, 2));
      }
      gun2timer--;


      VX+=(play.X-X+cos((float)frameCount/40)*128)/10;
      VY+=(play.Y-Y+sin((float)frameCount/40)*32-200)/10;
      VX=VX/10*9;
      VY=VY/10*9;
      if (delay==0) {
        Animr.Action(0);
        Must++;
        NewAI(X, Y, SupplySummon[floor(constrain(abs(randomGaussian()), 0, 2)*2.5)], true);
        for (int t=0; t<20; t++) {
          AddPartic(3, X, Y, random(-4, 4), random(-4, 4), 120, color(200, 0, 200), true);
        }
        delay=201;
      }
      delay--;
    }
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  float R1=0;
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
      Animr.Anim(false, true);
      Animr.DIMG(X, Y, W, H, false, true, #FFFFFF);
      Animr.EIMG(X+H/3, Y-H/3*2, 16, 16, R1, gun1mode, #FFFFFF);
      Animr.EIMG(X-H/3, Y-H/3*2, 16, 16, R1, gun2mode, #FFFFFF);
    }
  }
}

class Electron extends AI {
  Electron(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=24;
    H=48;
    HP=6000;
    BOSSHP.append(HP);
    BOSSID.append(ListAi.size());
    Animr = new SelfAnim(EAR.get("Electron"));
    for (int i=0; i<16; i++) {
      NewPartic(new Wind(X, Y-H/2, random(-15, 15), random(-15, 15), 120, #FFFFFF), true);
    }
  }
  int Cooldown=300;
  int Shield=1000;
  boolean downed=false;
  int attack=0;
  float attacking=0;
  void math(int SID) {
    if (HP<=0) {
      NewPartic(new Explode(X, Y-H/2, 150, 0, 40, #EA0C13), true);
      NewPartic(new Explode(X, Y-H/2, 300, 0, 40, #EA0C13), true);
      for (int i=0; i<16; i++) {
        NewPartic(new Wind(X, Y-H/2, random(-15, 15), random(-15, 15), 120, #FFFFFF), true);
      }
      kill.append(SID);
      return;
    } else {
      if (Shield<=0) {
        NewPartic(new Explode(X, Y-H/2, 100, 0, 40, #EA0C13), true);
        for (int i=0; i<4; i++) {
          NewPartic(new Smoke(X, Y-H/2, random(-5, 5), random(-5, 5), 40, #393030, -0.5), true);
        }
        downed=true;
        Shield=0;
      }
      if (downed) {
        if (Shield==1000) {
          downed=false;
          Cooldown=50;
          attacking=0;
        }
        Shield+=2;
        VY+=0.5;
      } else {
        if (Cooldown==0 && attacking<=0) {
          attacking=300;
          //attack=round(random(0, 3));
        }
        if (attacking>0) {
          if (attack==0) {
            if (attacking%30==0) {
              for (int i=0; i<8; i++) {
                //NewSPr(new SEletro(X,Y,3,PI/200,400,0,i*PI/4+attacking/20.0));//HARD
                //NewSPr(new SEletro(X,Y,3,-PI/200,400,0,i*PI/4-attacking/20.0));//HARD
                //NewSPr(new SEletro(X,Y,6,PI/200,400,0,i*PI/4+attacking/20.0));//HARD
                //NewSPr(new SEletro(X,Y,6,-PI/200,400,0,i*PI/4-attacking/20.0));//HARD
                NewSPr(new SEletro(X, Y-H/2, 3, PI/400, 400, 0, i*PI/4+attacking/15.0));
              }
            }
            if ((attacking-15)%30==0) {
              for (int i=0; i<8; i++) {
                NewSPr(new SEletro(X, Y-H/2, 3, -PI/400, 400, 0, i*PI/4+attacking/15.0));
              }
            }
          }
          if (attack==1) {
            if (attacking%30==0) {
              for (int i=0; i<1; i++) {
                //NewSPr(new Bross(play.X, play.Y-12, 0, 0, 60, round(random(0,7))*PI/8));//HARD
                //NewSPr(new Bross(play.X, play.Y-12, 0, 0, 60, round(random(0,7))*PI/8));//HARD
                //NewSPr(new Bross(play.X, play.Y-12, 0, 0, 60, round(random(0,7))*PI/8));//HARD
                //NewSPr(new Bross(play.X, play.Y-12, 0, 0, 60, round(random(0,7))*PI/8));//HARD
                if (random(0, 1)<0.5) {
                  NewSPr(new Bross(play.X, play.Y-12, 0, 0, 60, PI/2));
                  NewSPr(new Bross(play.X, play.Y-12, 0, 0, 60, 0));
                } else {
                  NewSPr(new Bross(play.X, play.Y-12, 0, 0, 60, PI/4));
                  NewSPr(new Bross(play.X, play.Y-12, 0, 0, 60, PI/4*3));
                }
              }
            }
          }
          if (attack==2) {
            //if (attacking%5==0) {
            //  for (int i=0; i<3; i++) {
            //    NewSPr(new SEletro(X, Y-H/2, -6, random(-PI/400, PI/400), 400, 1200, random(-PI, PI)));
            //  }
            //}
            //if (attacking%25==0) {
            //    NewSPr(new Bross(play.X+random(-200,200),play.Y-12+random(-200,200),random(-2,2),random(-2,2),60,round(random(0,7))*PI/8));//HARD
            //}
            if (attacking%5==0) {
              for (int i=0; i<2; i++) {
                NewSPr(new SEletro(X, Y-H/2, -6, random(-PI/400, PI/400), 400, 1200, random(-PI, PI)));
              }
            }
          }
          if (attack==3) {
            if (attacking%30==0) {//TRUE 20
              for (int i=0; i<4; i++) {
                //NewSPr(new Bross(play.X+random(-200,200),play.Y-12+random(-200,200),random(-2,2),random(-2,2),60,round(random(0,7))*PI/8));//HARD
                NewSPr(new Bross(play.X+random(-400, 400), play.Y-12+random(-400, 400), 0, 0, 60, round(random(0, 3))*PI/4));
              }
            }
          }
          attacking--;
          if (attacking<=0) {
            Cooldown=300;
            attack++;
            if (attack==4) {
              attack=0;
            }
          }
        } else {
          Cooldown--;
        }
        VX+=(0-X)/10;
        VY+=(-500-Y)/10;
        VX=VX/10*9;
        VY=VY/10*9;
      }
    }
    Phys(W, H, false);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    Animr.Anim(false, !downed);
    Animr.DIMG(X, Y, W, H, false, !downed, #FFFFFF);
    if (!downed) {
      for (int i=0; i<4; i++) {
        Animr.EIMG(X+cos(frameCount/60.0+PI/2*i)*50, Y+sin(frameCount/60.0+PI/2*i)*50-H/2, 21, 21, 0, attack, #FFFFFF);
      }
    }
    noStroke();
    fill(#7ECCF0, 75);
    circle(X, Y-H/2, 100);
    arc(X, Y-H/2, 100, 100, -PI/2, PI*Shield/500-PI/2);
  }
  void HURT(int dmg)
  {
    if (!hurte) {
      return;
    }
    if (downed) {
      HP-=dmg;
      for (int B=0; B<5; B++) {
        AddPartic(4, X, Y, random(-1, 1), random(-8, -2), 50, color(255, 0, 0), true);
      }
      if (play.regenera==0) {
        if (dist(X, Y, play.X, play.Y)<=200 && play.HP>0) {
          AddPartic(1, play.X+random(-5, 5), play.Y-12+random(-5, 5), X+random(-5, 5), Y-H/2+random(-5, 5), 60, color(255, 0, 0), true);
          if (play.HP+dmg/4>100) {
            play.HP=100;
          } else {
            play.HP+=dmg/4;
          }
        }
      } else {
        if (random(1, 100)<50 && play.HP>0) {
          NewPR(X, Y-H/2, random(-5, 5), random(-5, 5), 10);
        }
      }
    } else {
      Shield-=dmg;
      for (int B=0; B<5; B++) {
        AddPartic(4, X, Y, random(-8, 8), random(-8, 8), 50, #7ECCF0, true);
      }
    }
  }
}


class Limbo extends AI {
  Limbo(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  int Cooldown=600+(int)random(0, 600);
  ;
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==0){
      NewAI(X+random(-5,5),Y+random(-5,5),LimboSummoners[floor(random(0,LimboSummoners.length))],true);
      Cooldown=600+(int)random(0, 600);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<180) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>180) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5,0.5);
    VY=constrain(VY, -2, 2)+random(-0.5,0.5);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, #4B2705, -1), true);
    }
    VY-=0.01;
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    noStroke();
    fill(#4B2705);
    circle(X, Y-H/2, H);
    stroke(color(155,0,155),255-Cooldown*0.5);
    strokeWeight(3);
    if(Cooldown<240){
      line(X+cos( frameCount*1.0/Cooldown)*24,Y+sin( frameCount*1.0/Cooldown)*24-W,X-cos( frameCount*1.0/Cooldown)*24,Y-sin( frameCount*1.0/Cooldown)*24-W);
      line(X+cos(-frameCount*1.0/Cooldown)*24,Y+sin(-frameCount*1.0/Cooldown)*24-W,X-cos(-frameCount*1.0/Cooldown)*24,Y-sin(-frameCount*1.0/Cooldown)*24-W);
    }
    strokeWeight(1);
  }
}

class Lust extends AI {
  Lust(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  int Cooldown=200+(int)random(0, 200);
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==0){
      NewSPr(new WindBall(X,Y,-cos(R)*3,-sin(R)*3));
      Cooldown=200+(int)random(0, 200);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<175) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>175) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5,0.5);
    VY=constrain(VY, -2, 2)+random(-0.5,0.5);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, #D0CFD1, -1), true);
    }
    VY-=0.01;
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    noStroke();
    fill(#D0CFD1);
    circle(X, Y-H/2, H);
    stroke(color(155,0,155),255-Cooldown*0.5);
  }
}

class Gluttony extends AI {
  Gluttony(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  int Cooldown=100+(int)random(0, 100);
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==0){
      NewSPr(new IceBall(X,Y,-cos(R)*0.5,-sin(R)*0.5));
      Cooldown=100+(int)random(0, 100);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<170) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>170) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5,0.5);
    VY=constrain(VY, -2, 2)+random(-0.5,0.5);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, #57DECD, -1), true);
    }
    VY-=0.01;
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    noStroke();
    fill(#57DECD);
    circle(X, Y-H/2, H);
    stroke(color(155,0,155),255-Cooldown*0.5);
  }
}

class Greed extends AI {
  Greed(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  int Cooldown=100+(int)random(0, 100);
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==0){
      for(int i=0;i<3;i++){
        float Rand=random(-PI/20,PI/20)+atan2(Y-play.Y+50, X-play.X);
        NewSPr(new Melting(X,Y,-cos(Rand)*5,-sin(Rand)*5));
      }
      Cooldown=100+(int)random(0, 100);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<165) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>165) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5,0.5);
    VY=constrain(VY, -2, 2)+random(-0.5,0.5);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, #FFE200, -1), true);
    }
    VY-=0.01;
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    noStroke();
    fill(#FFE200);
    circle(X, Y-H/2, H);
    stroke(color(155,0,155),255-Cooldown*0.5);
  }
}

class Anger extends AI {
  Anger(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  int Cooldown=200+(int)random(0, 200);
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==0){
      NewSPr(new Rage(X,Y,-cos(R)*5,-sin(R)*5));
      Cooldown=200+(int)random(0, 200);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<160) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>160) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5,0.5);
    VY=constrain(VY, -2, 2)+random(-0.5,0.5);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, #176C0B, -1), true);
    }
    VY-=0.01;
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    noStroke();
    fill(#176C0B);
    circle(X, Y-H/2, H);
    stroke(color(155,0,155),255-Cooldown*0.5);
  }
}

  class Heresy extends AI {
  Heresy(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  int Cooldown=200+(int)random(0, 200);
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==0){
      NewSPr(new MiniFire(X,Y,0,0));
      Cooldown=200+(int)random(0, 200);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<155) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>155) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5,0.5);
    VY=constrain(VY, -2, 2)+random(-0.5,0.5);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, #FF9008, -1), true);
    }
    VY-=0.01;
    Phys(W, H, true);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    noStroke();
    fill(#FF9008);
    circle(X, Y-H/2, H);
    stroke(color(155,0,155),255-Cooldown*0.5);
  }
}

class Violence extends AI {
  Violence(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  int Cooldown=200+(int)random(0, 200);
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown<45){
      NewPartic(new Wind(X+random(-8, 8), Y-random(0, 16), random(-3, 3), random(-3, 3), 30, #FF0000), true);
    }
    if(Cooldown==0){
      VX=-cos(R)*15;
      VY=-sin(R)*15;
      Cooldown=200+(int)random(0, 200);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<150) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>150) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -10, 10)+random(-0.5,0.5);
    VY=constrain(VY, -10, 10)+random(-0.5,0.5);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, #FF0000, -1), true);
    }
    VY-=0.01;
    Phys(W, H, true);
    Cont(W, H, 15);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    noStroke();
    fill(#FF0000);
    circle(X, Y-H/2, H);
    stroke(color(255,0,0),255-Cooldown*0.5);
  }
}

class Hatred extends AI {
  Hatred(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=16;
    H=32;
    hurte=false;
    HP=2147483647;
    Animr = new SelfAnim(EAR.get("Hatred"));
  }
  int Cooldown=300;
  float R=0;
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    if(Cooldown==80){
      NewSPr(new hurtbox(X+cos(R)*1500,Y-H/2+sin(R)*1500,3000,25,R,40,70,5));
    }
    if(Cooldown==75){
      NewSPr(new hurtbox(X+cos(R)*1500,Y-H/2+sin(R)*1500,3000,35,R,40,75,5));
    }
    if(Cooldown==70){
      NewSPr(new hurtbox(X+cos(R)*1500,Y-H/2+sin(R)*1500,3000,45,R,40,80,15));
    }
    if(Cooldown<=0){
      Cooldown=300;
    }
    if(Cooldown>120){
      R=atan2(play.Y-12-Y+H/2, play.X-X);
    }
    Cooldown--;
    //VX=constrain(VX, -2, 2)+random(-0.1,0.1);
    //VY=constrain(VY, -2, 2)+random(-0.1,0.1);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, #FF0000, 1), true);
    }
    //VY-=0.01;
    Phys(W, H, true);
    //X+=VX;
    //Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    Animr.Anim(false, true);
    Animr.DIMG(X, Y, W, H, false, true, #FFFFFF);
    if (Cooldown>120 && Cooldown<300) {
      stroke(#FF0000);
      line(X,Y-H/2,X+cos( (Cooldown-120)/240.0+R)*3000,Y-H/2+sin( (Cooldown-120)/240.0+R)*3000);
      line(X,Y-H/2,X+cos(-(Cooldown-120)/240.0+R)*3000,Y-H/2+sin(-(Cooldown-120)/240.0+R)*3000);
      line(X,Y-H/2,X+cos(R)*3000,Y-H/2+sin(R)*3000);
    }
  }
}

class Fraud extends AI {
  Fraud(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  int Cooldown=200+(int)random(0, 200);
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==0){
      NewPartic(new Line(play.X,play.Y,X,Y,60,#222222,5),true);
      AThurt(10);
      HP+=20;
      Cooldown=200+(int)random(0, 200);
      for (int B=0; B<5; B++) {
        NewPartic(new VELLPoint(X, Y, random(-1, 1), random(-8, -2), 50, color(255, 0, 0)), true);
      }
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<145) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>145) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5,0.5);
    VY=constrain(VY, -2, 2)+random(-0.5,0.5);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, #222222, -1), true);
    }
    VY-=0.01;
    Phys(W, H, true);
    Cont(W, H, 15);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    noStroke();
    fill(#222222);
    circle(X, Y-H/2, H);
    stroke(color(255,0,0),255-Cooldown*0.5);
  }
  void HURT(int dmg)
  {
    if (!hurte) {
      return;
    }
    HP-=dmg;
    for (int B=0; B<5; B++) {
      AddPartic(4, X, Y, random(-1, 1), random(-8, -2), 50, color(255, 0, 0), true);
    }
    if (play.regenera==0) {
      if (dist(X, Y, play.X, play.Y)<=200 && play.HP>0) {
        AddPartic(1, play.X+random(-5, 5), play.Y-12+random(-5, 5), X+random(-5, 5), Y-H/2+random(-5, 5), 60, color(255, 0, 0), true);
        if (play.HP+dmg/2>100) {
          play.HP=100;
        } else {
          play.HP+=dmg/2;
        }
      }
    } else {
      if (random(1, 100)<50 && play.HP>0) {
        NewPR(X, Y-H/2, random(-5, 5), random(-5, 5), 10);
      }
    }
  }
}

class Treachery extends AI {
  Treachery(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  float LPX = 0;
  float LPY = 0;
  int Cooldown=200+(int)random(0, 200);
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==30){
      LPX=play.X;
      LPY=play.Y;
    }
    if(Cooldown==0){
      expd(LPX,LPY,128,30,0,true);
      Cooldown=200+(int)random(0, 200);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<140) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>140) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5,0.5);
    VY=constrain(VY, -2, 2)+random(-0.5,0.5);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, #004444, -1), true);
    }
    VY-=0.01;
    Phys(W, H, true);
    Cont(W, H, 15);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    if(Cooldown<30){
      stroke(#0BD8B4);
      strokeWeight(3);
      noFill();
      circle(LPX,LPY,128);
      circle(LPX,LPY,Cooldown*128/30);
      if(Configs.get("DrawEffects")==1){
        for(int i=0;i<8;i++){
          float R = PI/4*i;
          line(LPX+cos(R)*64,LPY+sin(R)*64,LPX+cos(R)*80,LPY+sin(R)*80);
        }
      }
    }
    noStroke();
    fill(#0BD8B4);
    circle(X, Y-H/2, H);
    stroke(color(255,0,0),255-Cooldown*0.5);
  }
}

class Zenith extends AI {
  Zenith(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY+50;
    M=nM;
    W=50;
    H=100;
    HP=4000;
    LX = new float[4];
    LY = new float[4];
    TX = new float[4];
    TY = new float[4];
    for(int i=0;i<4;i++){
      float R=PI/4+PI/2*i;
      LX[i]=cos(R)*500;
      LY[i]=sin(R)*500;
      TX[i]=cos(R)*500;
      TY[i]=sin(R)*500;
    }
    Animr = new SelfAnim(EAR.get("Zenith"));
    BOSSHP.append(HP);
    BOSSID.append(ListAi.size());
  }
  float[] LX;
  float[] LY;
  float[] TX;
  float[] TY;
  int DeathTimer=0;
  int AttackCooldown=0;
  int Attack=0;
  int lazerAce=0;
  int lazerSpeed=0;
  float lazerOff=0;
  boolean ShootOffset=false;
  float Spyral=0;
  float SpyralOff=0;
  int lastAttack=-1;
  float Deathspiral=0;
  Boolean enraged=false;
  int timealive=0;
  void math(int SID) {
    //HP=0;//DEBUG
    if (HP<=0) {
      if(timealive>600){
        HP=0;
        DeathTimer++;
        if(DeathTimer>120 && DeathTimer<600){
          Deathspiral+=1;
          if(DeathTimer%30==15 && DeathTimer>300){
            for(int i=0;i<50;i++){
              float R=i*PI/25+Deathspiral*PI/200;
              NewSPr(new DeathShard(X,Y-50,R,10,0));
            }
          }
          if(DeathTimer%30==0){
            for(int i=0;i<50;i++){
              float R=i*PI/25+Deathspiral*PI/100;
              NewSPr(new DeathShard(X,Y-50,R,10,0));
            }
          }
        }
      }else{
        HP=4000;
        enraged=true;
        NewPartic(new ShockWave(X,Y-50,0,0,120,#FFFFFF),true);
      }
    }
    if(DeathTimer==660){
      for(int i=0;i<80;i++){
        float R=i*PI/40;
        NewSPr(new Shard(X,Y-50,R,12,0));
        NewSPr(new Shard(X,Y-50,R+PI/80/2,11,0));
        NewSPr(new Shard(X,Y-50,R+PI/80,10,0));
      }
      kill.append(SID);
      return;
    }
    float R=0;
    if(play.HP>0 && HP>0){
      R=atan2(play.Y+50-Y,play.X-X);
    }else{
      R=atan2(50-Y,-X);
    }
    if(AttackCooldown<-120 && HP>0){
      lastAttack = Attack;
      while(Attack==lastAttack){
        Attack = floor(random(0,3));
      }
      //Attack=2;
      if(Attack==0){
        lazerAce=0;
        lazerOff=atan2(Y-play.Y+50,X-play.X);
        AttackCooldown=480;

      }
      if(Attack==1){
        ShootOffset=true;
        AttackCooldown=480;
        for(int i=-2;i<3;i++){
          float Rotate = i*PI/8+R;
          NewPartic(new Line(X,Y-50,X+cos(Rotate)*2000,Y+sin(Rotate)*2000-50,120,#FF0000,4),true);
        }
      }
      if(Attack==2){
        Spyral=atan2(Y-play.Y+50,X-play.X);
        AttackCooldown=480;
        SpyralOff=0;
      }
    }
    if(AttackCooldown>=0){
    if(HP>0 && play.HP>0){
      if(Attack==0){
        if(enraged){        
          float Rotate = lazerOff-PI;
          if(AttackCooldown<480-100){
            NewSPr(new hurtbox(X+cos(Rotate+PI/2)*100,Y-50+sin(Rotate+PI/2)*100,4000,35,Rotate,80,10,3));
            NewSPr(new hurtbox(X+cos(Rotate-PI/2)*100,Y-50+sin(Rotate-PI/2)*100,4000,35,Rotate,80,10,3));
            if(AttackCooldown%30==0){
              if(AttackCooldown%60==0){
                NewSPr(new DeathShard(X+cos(Rotate-PI/2)*25,Y-50+sin(Rotate-PI/2)*25,Rotate,12,0));
                NewSPr(new DeathShard(X+cos(Rotate-PI/2)*50,Y-50+sin(Rotate-PI/2)*50,Rotate,12,0));
                NewSPr(new DeathShard(X+cos(Rotate-PI/2)*75,Y-50+sin(Rotate-PI/2)*75,Rotate,12,0));
              }else{
                NewSPr(new DeathShard(X+cos(Rotate+PI/2)*25,Y-50+sin(Rotate+PI/2)*25,Rotate,12,0));
                NewSPr(new DeathShard(X+cos(Rotate+PI/2)*50,Y-50+sin(Rotate+PI/2)*50,Rotate,12,0));
                NewSPr(new DeathShard(X+cos(Rotate+PI/2)*75,Y-50+sin(Rotate+PI/2)*75,Rotate,12,0));
              }
            }
          }else{
            lazerOff=atan2(Y-play.Y-50,X-play.X);
            NewPartic(new Line(X+cos(Rotate)*2000,Y+sin(Rotate)*2000-50,X-cos(Rotate)*2000,Y-sin(Rotate)*2000-50,120,#FF0000,4),true);
          }
        }else{
          if(AttackCooldown>240){
            lazerAce++;
          }
          if(AttackCooldown<240){
            lazerAce--;
          }
          lazerSpeed+=lazerAce;
          Float Rotate = lazerSpeed/500.0*PI/30.0+lazerOff;
          NewSPr(new hurtbox(X+cos(Rotate)*1000,Y-50+sin(Rotate)*1000,2000,35,Rotate,30,10,3));
          NewPartic(new Line(X-cos(Rotate)*2000,Y-sin(Rotate)*2000-50,X,Y-50,30,#FF0000,4),true);
        }
      }
      if(Attack==1 && AttackCooldown%20==0 && AttackCooldown<360){
        if(ShootOffset){
          for(int i=(enraged?-4:-2);i<(enraged?5:3);i++){
            NewSPr(new Shard(X,Y-50,R+i*PI/8,enraged?10:8,0));
          }
        }else{
          for(int i=(enraged?-4:-2);i<(enraged?4:2);i++){
            NewSPr(new Shard(X,Y-50,R+i*PI/8+PI/16,enraged?10:8,0));
          }
        }
        ShootOffset=!ShootOffset;
      }
      if(Attack==2){
        if(AttackCooldown<360){
          SpyralOff+=PI/180;
          NewSPr(new hurtbox(X,Y-50,1000,35,Spyral+SpyralOff+PI/4,30,10,3));
          NewSPr(new hurtbox(X,Y-50,1000,35,Spyral+SpyralOff-PI/4,30,10,3));
          NewSPr(new hurtbox(X+cos(Spyral-SpyralOff+PI/4)*750,Y-50+sin(Spyral-SpyralOff+PI/4)*750,500,35,Spyral-SpyralOff+PI/4,30,10,3));
          NewSPr(new hurtbox(X+cos(Spyral-SpyralOff-PI/4)*750,Y-50+sin(Spyral-SpyralOff-PI/4)*750,500,35,Spyral-SpyralOff-PI/4,30,10,3));
          NewSPr(new hurtbox(X+cos(Spyral-SpyralOff+PI/4*3)*750,Y-50+sin(Spyral-SpyralOff+PI/4*3)*750,500,35,Spyral-SpyralOff+PI/4*3,30,10,3));
          NewSPr(new hurtbox(X+cos(Spyral-SpyralOff-PI/4*3)*750,Y-50+sin(Spyral-SpyralOff-PI/4*3)*750,500,35,Spyral-SpyralOff-PI/4*3,30,10,3));
          if(enraged){
            NewSPr(new hurtbox(X+cos(Spyral+SpyralOff+PI/4)*850,Y-50+sin(Spyral+SpyralOff+PI/4)*850,300,35,Spyral+SpyralOff+PI/4,30,10,3));
            NewSPr(new hurtbox(X+cos(Spyral+SpyralOff-PI/4)*850,Y-50+sin(Spyral+SpyralOff-PI/4)*850,300,35,Spyral+SpyralOff-PI/4,30,10,3));
            NewSPr(new hurtbox(X+cos(Spyral+SpyralOff+PI/4*3)*850,Y-50+sin(Spyral+SpyralOff+PI/4*3)*850,300,35,Spyral+SpyralOff+PI/4*3,30,10,3));
            NewSPr(new hurtbox(X+cos(Spyral+SpyralOff-PI/4*3)*850,Y-50+sin(Spyral+SpyralOff-PI/4*3)*850,300,35,Spyral+SpyralOff-PI/4*3,30,10,3));
            
            NewSPr(new hurtbox(X,Y-50,600,35,Spyral-SpyralOff+PI/4,30,10,3));
            NewSPr(new hurtbox(X,Y-50,600,35,Spyral-SpyralOff-PI/4,30,10,3));
            
            NewSPr(new hurtbox(X,Y-50,2000,35,Spyral-SpyralOff/8+PI/4,30,10,3));
            NewSPr(new hurtbox(X,Y-50,2000,35,Spyral-SpyralOff/8-PI/4,30,10,3));
          }
        }else{
          NewPartic(new Line(X+cos(Spyral+PI/4)*1000,Y+sin(Spyral+PI/4)*1000-50,X-cos(Spyral+PI/4)*1000,Y-sin(Spyral+PI/4)*1000-50,5,#FF0000,5),true);
          NewPartic(new Line(X+cos(Spyral-PI/4)*1000,Y+sin(Spyral-PI/4)*1000-50,X-cos(Spyral-PI/4)*1000,Y-sin(Spyral-PI/4)*1000-50,5,#FF0000,5),true);
          NewPartic(new Circle(X,Y-50,1000,2,#FF0000,5),true);
          NewPartic(new Circle(X,Y-50,2000,2,#FF0000,5),true);
        }
      }
    }
    }
    AttackCooldown--;
    if(HP<2500 || enraged){
      if(HP>0){
        float Speed=0;
        if(enraged){
           Speed = map(HP,0,4000,14,6);
        }else{
           Speed = map(HP,0,2500,10,1);
        }
        if(Attack==2 && AttackCooldown<360 && AttackCooldown>0 || Attack==0 && enraged){
          Speed*=enraged?0.0:0.6;
        }
        X+=cos(R)*Speed;
        Y+=sin(R)*Speed;
      }else{
        X+=cos(R)*5;
        Y+=sin(R)*5;
      }
      for(int i=0;i<4;i++){
        if(enraged){
          if (LX[i]+20>play.X-6 && LX[i]-20<play.X+6 && LY[i]+20>play.Y-24 && LY[i]-20<play.Y+0) {
            AThurt(25);
          }
        }
        LX[i]=TX[i]*0.1+LX[i]*0.9;
        LY[i]=TY[i]*0.1+LY[i]*0.9;
        float D = dist(TX[i],TY[i],X,Y);
        if(D>500 || D<150){
          float Ran;
          if(enraged){
            Ran = R+random(-PI/16,PI/16);
          }else{
            Ran = R+random(-PI/4,PI/4);
          }
          TX[i]=X+cos(Ran)*400;
          TY[i]=Y+sin(Ran)*400;
        }
      }
    }
    Cont(W, H, 50);
    timealive++;
  }
  void render() {
    float R=atan2(play.Y+50-Y,play.X-X);
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    for(int i=0;i<4;i++){
      float Rotate=atan2(LY[i]-Y+50,LX[i]-X);
      for(int u=0;u<18;u++){
        //circle(lerp(X,LX[i],u/10.0),lerp(Y-50,LY[i],u/10.0),8);
        int INDEX = u%2==0?1:0;
        Animr.EIMG(lerp(X,LX[i],u/18.0),lerp(Y-50,LY[i],u/18.0),32,32,Rotate,INDEX,#FFFFFF);
      }
      Animr.EIMG(LX[i],LY[i],40,40,0,2,enraged?#FF0000:#FFFFFF);
      if(enraged){
        stroke(#FF0000);
        strokeWeight(4);
        line(TX[i],TY[i],LX[i],LY[i]);
        Animr.EIMG(TX[i],TY[i],40,40,0,2,color(#FF0000,200));
      }
    }
    Animr.Anim(true, true);
    if(HP>0){
      Animr.DIMG(X, Y, W, H, true, true, enraged?#FF0000:#FFFFFF);
    }else{
      Animr.DIMG(X+random(-4,4), Y+random(-4,4), W, H, true, true, #FFFFFF);
    }
    noStroke();
    fill(#FFFFFF);
    if(HP>0){
      circle(X+cos(R)*7, Y-H/2+sin(R)*7, 15);
    }else{
      circle(X+random(-4,4), Y+random(-4,4)-H/2, 15);
    }
  }
  int HealHits=0;
  void HURT(int dmg)
  {
    if (!hurte) {
      return;
    }
    HealHits++;
    if(enraged){dmg/=5;}
    HP-=dmg;
    for (int B=0; B<5; B++) {
      AddPartic(4, X, Y, random(-1, 1), random(-8, -2), 50, color(255, 0, 0), true);
    }
    if(HealHits==2){
      NewSPr(new Heal(X,Y-50,0));
      HealHits=0;
    }
    if (play.regenera==0) {
      if (dist(X, Y, play.X, play.Y)<=200 && play.HP>0) {
        AddPartic(1, play.X+random(-5, 5), play.Y-12+random(-5, 5), X+random(-5, 5), Y-H/2+random(-5, 5), 60, color(255, 0, 0), true);
        if (play.HP+dmg/4>100) {
          play.HP=100;
        } else {
          play.HP+=dmg/4;
        }
      }
    } else {
      if (random(1, 100)<50 && play.HP>0) {
        NewPR(X, Y-H/2, random(-5, 5), random(-5, 5), 10);
      }
    }
  }
}

class Servant extends AI {
  Servant(float nX, float nY, boolean nM) {
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=80;
  }
  float LPX = 0;
  float LPY = 0;
  int Cooldown=200+(int)random(0, 200);
  void math(int SID) {
    if (HP<=0) {
      kill.append(SID);
      return;
    }
    float R=atan2(Y-play.Y, X-play.X);
    if(Cooldown==30){
      LPX=play.X;
      LPY=play.Y;
    }
    if(Cooldown==0){
      expd(LPX,LPY,128,30,0,true);
      Cooldown=200+(int)random(0, 200);
    }
    Cooldown--;
    if (dist(X, Y, play.X, play.Y)<140) {
      VX+=cos(R);
      VY+=sin(R);
    }
    if (dist(X, Y, play.X, play.Y)>140) {
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX, -2, 2)+random(-0.5,0.5);
    VY=constrain(VY, -2, 2)+random(-0.5,0.5);
    if(random(0,100)<20){
      NewPartic(new GravPoint(X+random(-8, 8), Y-random(0, 16), 0, 0, 30, #004444, -1), true);
    }
    VY-=0.01;
    Phys(W, H, true);
    Cont(W, H, 15);
    X+=VX;
    Y+=VY;
  }
  void render() {
    if (DebugDraw) {
      stroke(0);
      fill(255);
      rect(X-W, Y-H, W*2, H);
    }
    if(Cooldown<30){
      stroke(#0BD8B4);
      strokeWeight(3);
      noFill();
      circle(LPX,LPY,128);
      circle(LPX,LPY,Cooldown*128/30);
      if(Configs.get("DrawEffects")==1){
        for(int i=0;i<8;i++){
          float R = PI/4*i;
          line(LPX+cos(R)*64,LPY+sin(R)*64,LPX+cos(R)*80,LPY+sin(R)*80);
        }
      }
    }
    noStroke();
    fill(#0BD8B4);
    circle(X, Y-H/2, H);
    stroke(color(255,0,0),255-Cooldown*0.5);
  }
}

class AI {
  float Bresistance=1;
  float X;
  float Y;
  float VX;
  float VY;
  float W;
  float H;
  int T;
  int HP;
  boolean Gr;
  boolean M;
  SelfAnim Animr;
  ;
  int OG=0;
  boolean Ignore;
  boolean hurte=true;
  //AI(float nX,float nY,boolean nM){
  //}
  void math(int SID) {
  }
  void render() {
  }
  void Cont(float W, float H, int dmg) {
    if (X-W<=play.X+6 && X+W>=play.X-6 && Y>=play.Y-24 && Y-H<=play.Y && play.IV==0 && play.HP>0) {
      AThurt(dmg);
      if (X<=play.X) {
        VX-=5;
      } else {
        VX+=5;
      }
    }
  }
  void Fall() {
    if (Gr==false) {
      if (VY<20) {
        VY+=0.5;
      }
      OG++;
    } else {
      VX/=2;
      OG=0;
    }
    Gr=false;
  }
  void Walk(float GS, float AS, float JF) {
    if (Gr==false) {
      if (VY<20) {
        VY+=0.5;
      }
      if (play.X<X && VX>-7) {
        VX-=AS;
      }
      if (play.X>X && VX<7 ) {
        VX+=AS;
      }
      OG++;
    } else {
      if (play.Y+10<Y && random(0, 100)<5) {
        VY-=JF;
      }
      if (play.X<X && VX>-7) {
        VX-=GS;
      }
      if (play.X>X && VX< 7) {
        VX+=GS;
      }
      if (play.Y>Y) {
        Ignore=true;
      }
      OG=0;
    }
    Gr=false;
  }
  void Phys(float W, float H, boolean C) {
    SPHYS(X-W+0.01, Y-0.01, X-W+VX, Y+VY, C);
    SPHYS(X+W-0.01, Y-0.01, X+W+VX, Y+VY, C);
    SPHYS(X-W+0.01, Y-H+0.01, X-W+VX, Y-H+VY, C);
    SPHYS(X+W-0.01, Y-H+0.01, X+W+VX, Y-H+VY, C);
    if (true) {
      if (Ignore & C) {
        Checkfor();
      }
      if (sphys(X-W+VX, Y+VY, X+W+VX, Y+VY) | sphys(X-W+VX, Y-H+VY, X+W+VX, Y-H+VY)) {
        VY=0;
      }
      if (sphys(X-W+VX, Y+VY, X-W+VX, Y-H+VY) | sphys(X+W+VX, Y+VY, X+W+VX, Y-H+VY)) {
        VX=0;
      }
    }
  }
  void SPHYS(float T1, float T2, float T3, float T4, boolean C) {
    float[] T;
    T=coll(T1, T2, T3, T4, Ignore || !C);
    if (T[0]>=0 && T[0]<=1) {
      int i = (int)T[2];
      PVector TOplayer=new PVector(play.X-(CSX[i]+CEX[i])/2, play.Y-12-(CSY[i]+CEY[i])/2);
      PVector Normal=new PVector((CSX[i]-CEX[i]), (CSY[i]-CEY[i]));
      if (CSX[i]>CEX[i]) {
        Normal.rotate(-PI/2);
      } else {
        Normal.rotate(PI/2);
      }
      if (Normal.dot(TOplayer)<0 && CT[i]==1) {
        Ignore=true;
      }
      //float R=atan2(CSY[(int)T[2]]-CEY[(int)T[2]],CSX[(int)T[2]]-CEX[(int)T[2]]);
      float R=atan2(CSY[i]-CEY[i], CSX[i]-CEX[i]);
      float NV = VX * cos(R) + VY * sin(R);
      VX = cos(R) * NV * 0.99;
      VY = sin(R) * NV * 0.99;
      //float ISB=atan2(Y-H/2-(CSY[(int)T[2]]+CEY[(int)T[2]])/2,X-(CSX[(int)T[2]]+CEX[(int)T[2]])/2);
      R=atan2(CSY[i]-CEY[i], CSX[i]-CEX[i]);
      if (R<0) {
        R+=PI;
      }
      if (Normal.dot(TOplayer)>0 && R>-PI/4) {//its a feature fuck it
        Gr=true;
      }
      //Gr=true;
    }
  }
  boolean sphys(float T1, float T2, float T3, float T4) {
    float[] T;
    T=coll(T1, T2, T3, T4, Ignore);
    if (T[0]>0 && T[0]<1) {
      return true;
    }
    return false;
  }
  void HURT(int dmg)
  {
    if (!hurte) {
      return;
    }
    HP-=dmg;
    for (int B=0; B<5; B++) {
      AddPartic(4, X, Y, random(-1, 1), random(-8, -2), 50, color(255, 0, 0), true);
    }
    if (play.regenera==0) {
      if (dist(X, Y, play.X, play.Y)<=200 && play.HP>0) {
        AddPartic(1, play.X+random(-5, 5), play.Y-12+random(-5, 5), X+random(-5, 5), Y-H/2+random(-5, 5), 60, color(255, 0, 0), true);
        if (play.HP+dmg/4>100) {
          play.HP=100;
        } else {
          play.HP+=dmg/4;
        }
      }
    } else {
      if (random(1, 100)<50 && play.HP>0) {
        NewPR(X, Y-H/2, random(-5, 5), random(-5, 5), 10);
      }
    }
  }

  float[] Enyhitscan(float R, int dmg, boolean lazer, float Offx, float Offy) {
    float MT=9999;
    int T=-1;
    float OX=X+Offx;
    float OY=Y+Offy;
    float NX=X+Offx+cos(R);
    float NY=Y+Offy+sin(R);
    int[] list=CB(OX, OY, NX+cos(R)*1000, NY+sin(R)*1000);
    for (int p=0; p<list.length; p++) {
      int i=list[p];
      float r = TLineToLine(CSX[i], CSY[i], CEX[i], CEY[i], OX, OY, NX, NY);
      if (r>=0 && r<=1) {
        float t = TLineToLine(OX, OY, NX, NY, CSX[i], CSY[i], CEX[i], CEY[i]);
        if (t>0 && t<MT & r>=0 && r<=1) {
          MT=t;
        }
      }
    }
    float R1 = TLineToLine(play.X-6, play.Y-24, play.X+6, play.Y, X+Offx, Y+Offy, X+Offx+cos(R), Y+Offy+sin(R));
    float R2 = TLineToLine(play.X+6, play.Y-24, play.X-6, play.Y, X+Offx, Y+Offy, X+Offx+cos(R), Y+Offy+sin(R));
    if (R1>=0 && R1<=1 || R2>=0 && R2<=1) {
      float T1 = TLineToLine(X+Offx, Y+Offy, X+Offx+cos(R), Y+Offy+sin(R), play.X-6, play.Y-24, play.X+6, play.Y);
      float T2 = TLineToLine(X+Offx, Y+Offy, X+Offx+cos(R), Y+Offy+sin(R), play.X+6, play.Y-24, play.X-6, play.Y);
      if (min(T1, T2)>0 && min(T1, T2)<MT) {
        AThurt(dmg);
        if (!lazer) {
          MT=min(MT, min(T1, T2));
          float[] tmp = {play.X+cos(R)*MT, play.Y-12+sin(R)*MT};
          return tmp;
        }
      }
    }
    float[] tmp = {X+Offx+cos(R)*MT, Y+Offy+sin(R)*MT};
    return tmp;
  }

  float[] Enyscan(float R, boolean lazer, float Offx, float Offy) {
    float MT=9999;
    int T=-1;
    float OX=X+Offx;
    float OY=Y+Offy;
    float NX=X+Offx+cos(R);
    float NY=Y+Offy+sin(R);
    int[] list=CB(OX, OY, NX+cos(R)*1000, NY+sin(R)*1000);
    for (int p=0; p<list.length; p++) {
      int i=list[p];
      float r = TLineToLine(CSX[i], CSY[i], CEX[i], CEY[i], OX, OY, NX, NY);
      if (r>=0 && r<=1) {
        float t = TLineToLine(OX, OY, NX, NY, CSX[i], CSY[i], CEX[i], CEY[i]);
        if (t>0 && t<MT & r>=0 && r<=1) {
          MT=t;
        }
      }
    }
    float R1 = TLineToLine(play.X-6, play.Y-24, play.X+6, play.Y, X+Offx, Y+Offy, X+Offx+cos(R), Y+Offy+sin(R));
    float R2 = TLineToLine(play.X+6, play.Y-24, play.X-6, play.Y, X+Offx, Y+Offy, X+Offx+cos(R), Y+Offy+sin(R));
    if (R1>=0 && R1<=1 || R2>=0 && R2<=1) {
      float T1 = TLineToLine(X+Offx, Y+Offy, X+Offx+cos(R), Y+Offy+sin(R), play.X-6, play.Y-24, play.X+6, play.Y);
      float T2 = TLineToLine(X+Offx, Y+Offy, X+Offx+cos(R), Y+Offy+sin(R), play.X+6, play.Y-24, play.X-6, play.Y);
      if (min(T1, T2)>0 && min(T1, T2)<MT) {
        if (!lazer) {
          MT=min(MT, min(T1, T2));
          float[] tmp = {play.X+cos(R)*MT, play.Y-12+sin(R)*MT};
          return tmp;
        }
      }
    }
    float[] tmp = {X+Offx+cos(R)*MT, Y+Offy+sin(R)*MT};
    return tmp;
  }
  void Checkfor() {//find better sulucion
    int buffer=3;
    if (!(Checkforsub(X-W/2, Y+buffer, X-W/2, Y-H-buffer)||
      Checkforsub(X+W/2, Y+buffer, X+W/2, Y-H-buffer)||
      Checkforsub(X-W/2-buffer, Y, X+W/2+buffer, Y)||
      Checkforsub(X-W/2+buffer, Y-H, X+W/2+buffer, Y-H))) {
      Ignore=false;
    }
  }
  boolean Checkforsub(float SX, float SY, float EX, float EY) {
    int[] list=CB(SX, SY, EX, EY);
    for (int e=0; e<list.length; e++) {
      int i=list[e];
      float t;
      float r;
      t=TLineToLine(SX, SY, EX, EY, CSX[i], CSY[i], CEX[i], CEY[i]);
      r=TLineToLine(CSX[i], CSY[i], CEX[i], CEY[i], SX, SY, EX, EY);
      float T=1;
      if (t<T && t>0 && r<=1 && r>=0) {
        T=t;
        return true;
      }
    }
    return false;
  }
}

boolean NewAI(float X, float Y, String T, boolean M) {
  //Need to figure a better way of doin this
  switch(T) {//
  case "Bug":
    ListAi.add(new Bug(X, Y, M));
    break;
  case "Fly":
    ListAi.add(new Fly(X, Y, M));
    break;
  case "Target":
    ListAi.add(new Target(X, Y, M));
    break;
  case "Spewer":
    ListAi.add(new Spewer(X, Y, M));
    break;
  case "testBoss":
    ListAi.add(new testBoss(X, Y, M));
    break;
  case "Maze":
    ListAi.add(new Maze(X, Y, M, false));
    break;
  case "Laze":
    ListAi.add(new Laze(X, Y, M, false));
    break;
  case "Maze_Boss":
    ListAi.add(new Maze(X, Y, M, true));
    break;
  case "Laze_Boss":
    ListAi.add(new Laze(X, Y, M, true));
    break;
  case "tower":
    ListAi.add(new Tower(X, Y, M));
    break;
  case "napalm":
    ListAi.add(new Napalm(X, Y, M));
    break;
  case "Spirit":
    ListAi.add(new Spirit(X, Y, M));
    break;
  case "Guardian":
    ListAi.add(new Guardian(X, Y, M));
    break;
  case "Crab":
    ListAi.add(new Crab(X, Y, M));
    break;
  case "Piller":
    ListAi.add(new Piller(X, Y, M));
    break;
  case "Supply":
    ListAi.add(new Supply(X, Y, M, false));
    break;
  case "Supply_Boss":
    ListAi.add(new Supply(X, Y, M, true));
    break;
  case "Electron":
    ListAi.add(new Electron(X, Y, M));
    break;
  case "Limbo":
    ListAi.add(new Limbo(X, Y, M));
    break;
  case "Lust":
    ListAi.add(new Lust(X, Y, M));
    break;
  case "Gluttony":
    ListAi.add(new Gluttony(X, Y, M));
    break;
  case "Greed":
    ListAi.add(new Greed(X, Y, M));
    break;
  case "Anger":
    ListAi.add(new Anger(X, Y, M));
    break;
  case "Heresy":
    ListAi.add(new Heresy(X, Y, M));
    break;
  case "Hatred":
    ListAi.add(new Hatred(X, Y, M));
    break;
  case "Violence":
    ListAi.add(new Violence(X, Y, M));
    break;
  case "Fraud":
    ListAi.add(new Fraud(X, Y, M));
    break;
  case "Treachery":
    ListAi.add(new Treachery(X, Y, M));
    break;
  case "Zenith":
    ListAi.add(new Zenith(X, Y, M));
    break;
  default:
    return false;
  }
  return true;
}

float[] coll(float OX, float OY, float NX, float NY, boolean Ignore) {
  for (int i=0; i<CSX.length; i++) {//OH god this is terrible
    float T=100;
    float N=0;
    float t;
    float r;

    t=TLineToLine(OX, OY, NX, NY, CSX[i], CSY[i], CEX[i], CEY[i]);
    r=TLineToLine(CSX[i], CSY[i], CEX[i], CEY[i], OX, OY, NX, NY);
    for (int u=0; u<4; u++) {
      if (t<=T && t>=0 && r<=1 && r>=0 && !(Ignore && CT[i]==1)) {
        {
          T=t;
          N=u;
        }
      }
    }
    if (T>=0 && T<=1) {
      float[] tmp = {T, N, i};
      return tmp;
    }
  }
  float[] tmp = {-1};
  return tmp;
}
