ArrayList<AI> ListAi;
IntList kill;
int Must=0;
int PMust=0;
IntList BOSSHP;
IntList BOSSID;

String[] AINames={"Bug","Fly","Target","Spewer","testBoss","Maze","Laze","Maze_Boss","Laze_Boss","tower","napalm","Spirit","Guardian","Crab","Piller"};
boolean[] Sgroun={true ,false,true    ,true    ,true      ,false ,false ,false      ,false      ,true   ,true    ,false   ,true      ,true  ,true};

void AIMath(){
  PMust=Must;
  for(int i=0;i<ListAi.size();i++){
    try{
      ListAi.get(i).math(i);
      AI tmp = ListAi.get(i);
      if(tmp.X>10000 | tmp.X<-10000 | tmp.Y>10000 | tmp.Y<-10000){kill.append(i);}
    }catch(Exception e){
      PrintCon("sorry for that");
      kill.append(i);
      AddPartic(1,ListAi.get(i).X,ListAi.get(i).Y,ListAi.get(i).X,-10000,60,#FFFFFF,false);
      AddPartic(5,ListAi.get(i).X,ListAi.get(i).Y,128,0,60,#FFFFFF,true);
      for(int ohno=0;ohno<50;ohno++){
        AddPartic(2,ListAi.get(i).X,ListAi.get(i).Y,random(-10,10),random(-10,10),60,#FFFFFF,false);
      }
      PrintCon(e.toString());
      ErrorTimer=120;
    }
  }
  for(int i=0;i<ListAi.size();i++){
    try{
      if(ListAi.get(i).getClass()==Class.forName("ProjectDFTEST$Spirit")){
        Spirit tmp = (Spirit)ListAi.get(i);
        if(kill.hasValue(tmp.Connected)){
          tmp.Con=false;
          tmp.hurte=true;
          ListAi.set(i,tmp);
        }
      }
    }catch(Exception e){}
  }
  kill.reverse();
  for(int i=0;i<kill.size();i++){
    AI tmp = ListAi.get(kill.get(i));
    BOSSID.reverse();
    BOSSHP.reverse();
    for(int u=0;u<BOSSID.size();u++){
      if(kill.get(i)==BOSSID.get(u)){
        BOSSID.remove(u);
        BOSSHP.remove(u);
        break;
      }
      if(kill.get(i)<BOSSID.get(u)){
        BOSSID.set(u,BOSSID.get(u)-1);
      }
    }
    BOSSID.reverse();
    BOSSHP.reverse();
    if(dist(tmp.X,tmp.Y,play.X,play.Y)<=200 && play.HP>0){
      AddPartic(1,play.X+random(-5,5),play.Y-12+random(-5,5),tmp.X+random(-5,5),tmp.Y-tmp.H/2+random(-5,5),60,color(255,0,0),true);
      if(play.HP+5>100){
        play.HP=100;
      }else{
        play.HP+=5;
      }
    }
    if(tmp.M){Must--;}
  }
  for(int i=0;i<kill.size();i++){
    ListAi.remove(kill.get(i));
  }
  kill.clear();
}

void AIR(){
  for(int i=0;i<ListAi.size();i++){
    ListAi.get(i).render();
    if(DebugDraw){
      text(ListAi.get(i).HP,ListAi.get(i).X+10,ListAi.get(i).Y-10);
    }
  }
}

class Bug extends AI{
  Bug(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=10;
    H=14;
    HP=20;
    T=0;
    Animr.ID=EAR.get("Bug");
  }
  void math(int SID){
      if(HP<=0){kill.append(SID);return;}
      Walk(0.3,0.5,6);
      Cont(W,H,25);
      Phys(W,H,true);
      X+=VX;
      Y+=VY;
  }
  void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
      pushMatrix();
      if(VX>0){
        scale(-1,1);
        translate(-X*2,0);
      }
      Animr.Anim(true,OG>3);
      Animr.DIMG(X,Y,W,H,true,OG>3,#FFFFFF);
      popMatrix();
  }
}

class Fly extends AI{
  Fly(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=6;
    H=12;
    HP=12;
    T=4;
    Animr.ID=EAR.get("Fly");
  }
  void math(int SID){
    if(HP<=0){kill.append(SID);return;}
    VX+=cos(atan2(play.Y-Y,play.X-X))*0.4;
    VY+=sin(atan2(play.Y-Y,play.X-X))*0.2;
    VX=constrain(VX,-10,10);
    VY=constrain(VY,-10,10);
    Cont(W,H,15);
    Phys(W,H,false);
    X+=VX;
    Y+=VY;
  }
  void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
    Animr.Anim(false,true);
    Animr.DIMG(X,Y,W,H,false,true,#FFFFFF);
  }
}

class Target extends AI{
  Target(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=6;
    H=24;
    HP=9999;
    Animr.ID=EAR.get("Target");
  }
  void math(int SID){
    if(HP<=0){kill.append(SID);return;}
    Fall();
    Cont(W,H,1);
    Phys(W,H,true);
    X+=VX;
    Y+=VY;
    NewPartic(new StandImg(X,Y,random(-12,12),random(-12,12),15,#FFFFFF,"uranium.png"),true);
    NewSPr(new hurtbox(X,Y,20,9000,-PI/4,0,15,5));
    NewSPr(new hurtbox(X,Y,20,9000,PI/4,0,15,5));
  }
  void render(){
    stroke(0);
    fill(255);
    rect(X-W,Y-H,W*2,H);
    Animr.Anim(false,false);
    Animr.DIMG(X,Y,W,H,false,false,#FFFFFF);
    Animr.EIMG(cos(frameCount/20.0)*32+X,sin(frameCount/20.0)*32+Y,8,8,0,#FFFFFF);
  }
  void HURT(int dmg)
  {
    if(!hurte){return;}
    HP-=dmg;
    Animr.Action(0);
    for(int B=0;B<5;B++){
      AddPartic(4,X,Y,random(-1,1),random(-8,-2),50,color(255,0,0),true);
    }
    if(play.regenera==0){
      if(dist(X,Y,play.X,play.Y)<=200 && play.HP>0){
        AddPartic(1,play.X+random(-5,5),play.Y-12+random(-5,5),X+random(-5,5),Y-H/2+random(-5,5),60,color(255,0,0),true);
        if(play.HP+dmg/4>100){
          play.HP=100;
        }else{
          play.HP+=dmg/4;
        }
      }
    }else{
      if(random(1,100)<50 && play.HP>0){
        NewPR(X,Y-H/2,random(-5,5),random(-5,5),10);
      }
    }
  }
}

class Spewer extends AI{
  int cooldown=80;
  Spewer(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=6;
    H=24;
    HP=34;
    T=5;
    Animr.ID=EAR.get("Spewer");
  }
  void math(int SID){
    if(dist(X,Y,play.X,play.Y)>150){
      Walk(0.01,0.01,1);
    }
    if(HP<=0){kill.append(SID);return;}
    Fall();
    Cont(W,H,1);
    Phys(W,H,true);
    if(cooldown==0){
      
      NewPR(X+W,Y-H/2,cos(atan2(play.Y-Y+H/2-12,play.X+6-X-W))*6,sin(atan2(play.Y-Y+H/2-8,play.X+6-X-W))*6,0);
      cooldown=80;
    }else{
      cooldown--;
    }
    X+=VX;
    Y+=VY;
  }
  void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
      pushMatrix();
      if(VX<0){
        scale(-1,1);
        translate(-X*2,0);
      }
      Animr.Anim(Gr && abs(VX)<0.5,false);
      Animr.DIMG(X,Y,W,H,Gr && abs(VX)<0.5,false,#FFFFFF);
      popMatrix();
  }
}

class testBoss extends AI{
  testBoss(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=16;
    H=34;
    HP=600;
    BOSSHP.append(HP);
    BOSSID.append(ListAi.size());
  }
  void math(int SID){
    if(HP<=0){kill.append(SID);return;}
    Fall();
    Cont(W,H,1);
    Phys(W,H,true);
    X+=VX;
    Y+=VY;
  }
  void render(){
    stroke(0);
    fill(255);
    rect(X-W,Y-H,W*2,H);
  }
}

class Maze extends AI{
  boolean Enraged=false;
  Maze(float nX,float nY,boolean nM,boolean Boss){
    X=nX;
    Y=nY;
    M=nM;
    W=16;
    H=32;
    T=1;
    if(Boss){
      HP=800;
      W=32;
      H=64;
      BOSSHP.append(HP);
      BOSSID.append(ListAi.size());
    }else{
      HP=200;
    };
    Animr.ID=EAR.get("Maze");
  }
  int cooldown = 370;
  int attack = 1;
  void math(int SID){
    if(HP<=0){
      if(Gr){
        AddPartic(5,X,Y,32,0,15,#00FF00,false);
        kill.append(SID);
        for(int all=0;all<ListAi.size();all++){
          try{
            if(ListAi.get(all).getClass()==Class.forName("ProjectDFTEST$Maze")){
              Maze tmp = (Maze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all,tmp);
            }
            if(ListAi.get(all).getClass()==Class.forName("ProjectDFTEST$Laze")){
              Laze tmp = (Laze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all,tmp);
            }
          }catch(Exception e){
            
          }
        }
        return;
      }
      VY+=0.2;
    }else{
      Gr=false;
      if(cooldown>200 && cooldown<300){
        VX+=(play.X-X+cos((float)frameCount/20)*64)/10;
        VY+=(play.Y-Y+sin((float)frameCount/20)*64-200)/10;
        VX=VX/10*9;
        VY=VY/10*9;
      }else{
        VX=0;
        VY=0;
      }
      if(cooldown<200 && cooldown>80){
        float N=((float)cooldown-80)/120*8;
        if(attack==0){
        AddPartic(3,X,Y-H/2,cos(0*PI/2)*N,sin(0*PI/2)*N,15,#00FFFF,true);
        AddPartic(3,X,Y-H/2,cos(1*PI/2)*N,sin(1*PI/2)*N,15,#00FFFF,true);
        AddPartic(3,X,Y-H/2,cos(2*PI/2)*N,sin(2*PI/2)*N,15,#00FFFF,true);
        AddPartic(3,X,Y-H/2,cos(3*PI/2)*N,sin(3*PI/2)*N,15,#00FFFF,true);
        }else{
        AddPartic(3,X,Y-H/2,cos(0*PI/2+PI/4)*N,sin(0*PI/2+PI/4)*N,15,#00FFFF,true);
        AddPartic(3,X,Y-H/2,cos(1*PI/2+PI/4)*N,sin(1*PI/2+PI/4)*N,15,#00FFFF,true);
        AddPartic(3,X,Y-H/2,cos(2*PI/2+PI/4)*N,sin(2*PI/2+PI/4)*N,15,#00FFFF,true);
        AddPartic(3,X,Y-H/2,cos(3*PI/2+PI/4)*N,sin(3*PI/2+PI/4)*N,15,#00FFFF,true);
        }
      }
      if(!Enraged){
        if(cooldown<80){
          if(attack==0){
            if(cooldown%10==0){
              for(int i=0;i<4;i++){
                NewPR(X,Y-H/2,cos(i*PI/2+(float)cooldown/60*PI)*5,sin(i*PI/2+(float)cooldown/60*PI)*5,3);
              }
            }
          }else{
            if(cooldown%10==0){
              float target = atan2(play.Y-Y,play.X-X);
              for(int i=0;i<3;i++){
                NewPR(X,Y-H/2,cos(target+PI/8*(i-1))*5,sin(target+PI/8*(i-1))*5,3);
              }
            }
          }
        }
      }else{
        if(cooldown<80){
            if(cooldown%10==0){
              for(int i=0;i<8;i++){
                NewPR(X,Y-H/2,cos(i*PI/4+(float)cooldown/60)*5,sin(i*PI/4+(float)cooldown/60)*5,3);
              }
            }
        }
      }
      cooldown--;
      if(cooldown<0){cooldown=370;attack = (int)random(0,2);}
    }
    Cont(W,H,1);
    Phys(W,H,false);
    X+=VX;
    Y+=VY;
  }
  void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
    Animr.Anim(false,true);
    if(!Enraged){
      Animr.DIMG(X,Y,W,H,false,true,#FFFFFF);
    }else{
      Animr.DIMG(X,Y,W,H,false,true,#FFAAAA);
      stroke(255,0,0);
      noFill();
      circle(X+random(-2,2),Y-H/2+random(-2,2),36);
    }
  }
}

class Laze extends AI{
  boolean Enraged=false;
  Laze(float nX,float nY,boolean nM,boolean Boss){
    X=nX;
    Y=nY;
    M=nM;
    W=16;
    H=32;
    T=2;
    if(Boss){
      HP=800;
      W=32;
      H=64;
      BOSSHP.append(HP);
      BOSSID.append(ListAi.size());
    }else{
      HP=200;
    }
    Animr.ID=EAR.get("Laze");
  }
  int cooldown = 420;
  int attack = 1;
  float LastPlayer = 0;
  float Tx;
  float Ty;
  void math(int SID){
    if(HP<=0){
      if(Gr){
        AddPartic(5,X,Y,32,0,15,#00FF00,false);
        kill.append(SID);
        for(int all=0;all<ListAi.size();all++){
          try{
            if(ListAi.get(all).getClass()==Class.forName("ProjectDFTEST$Maze")){
              Maze tmp = (Maze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all,tmp);
            }
            if(ListAi.get(all).getClass()==Class.forName("ProjectDFTEST$Laze")){
              Laze tmp = (Laze)ListAi.get(all);
              tmp.Enraged=true;
              ListAi.set(all,tmp);
            }
          }catch(Exception e){
            
          }
        }
        return;
      }
      VY+=0.2;
    }else{
      Gr=false;
      if(cooldown>200  && cooldown<350){
        VX+=(play.X-X+cos(-(float)frameCount/20)*64)/10;
        VY+=(play.Y-Y+sin(-(float)frameCount/20)*64-200)/10;
        VX=VX/10*9;
        VY=VY/10*9;
      }else{
        VX=0;
        VY=0;
      }
      if(cooldown==200){
        if(attack==0){
          Tx=X;Ty=Y;
        }
        else{
          LastPlayer = atan2(play.Y-Y,play.X-X);
        }
      }
      if(cooldown<200 && cooldown>80){
        if(attack==0 || Enraged){
          Tx=(play.X+Tx)/2;
          Ty=(play.Y-12+Ty)/2;
        }else{
          float[] tmp=Enyscan(LastPlayer+PI/4,true,0,-H/2);
          AddPartic(1,X,Y-H/2,tmp[0],tmp[1],2,color(100,0,0),true);
                  tmp=Enyscan(LastPlayer-PI/4,true,0,-H/2);
          AddPartic(1,X,Y-H/2,tmp[0],tmp[1],2,color(100,0,0),true);
        }
      }
      if(cooldown<80){
        if(!Enraged){
          if(attack==0){
            if((cooldown+10)%20==0){
              Tx=play.X;
              Ty=play.Y;
            }
            if(cooldown%20==0){
              float[] tmp=Enyhitscan(atan2(Ty-Y,Tx-X),25,true,0,-H/2);
              AddPartic(1,X,Y-H/2,tmp[0],tmp[1],40,color(255,0,0),true);
            }
          }else{
            float[] tmp=Enyhitscan(LastPlayer+PI/4*((float)cooldown/80),15,true,0,-H/2);
            AddPartic(1,X,Y-H/2,tmp[0],tmp[1],40,color(255,0,0),true);
                    tmp=Enyhitscan(LastPlayer-PI/4*((float)cooldown/80),15,true,0,-H/2);
            AddPartic(1,X,Y-H/2,tmp[0],tmp[1],40,color(255,0,0),true);
          }
        }else{
          if(cooldown%8==0){
            Tx=(play.X+Tx*2)/3;
            Ty=(play.Y-12+Ty*2)/3;
            float[] tmp=Enyhitscan(atan2(Ty-Y,Tx-X),25,true,0,-H/2);
            AddPartic(1,X,Y-H/2,tmp[0],tmp[1],40,color(255,0,0),true);
          }
        }
      }
      cooldown--;
      if(cooldown<0){cooldown=420;attack = (int)random(0,2);}
    }
    Cont(W,H,1);
    Phys(W,H,false);
    X+=VX;
    Y+=VY;
  }
  void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
    Animr.Anim(false,true);
    if(!Enraged){
      Animr.DIMG(X,Y,W,H,false,true,#FFFFFF);
    }else{
      Animr.DIMG(X,Y,W,H,false,true,#FFAAAA);
      stroke(255,0,0);
      noFill();
      circle(X+random(-2,2),Y-H/2+random(-2,2),36);
    }
    if(cooldown<200 & (attack == 0 || Enraged)){
      stroke(#B703FF);
      noFill();
      circle(Tx,Ty,16);
      line(Tx-20,Ty,Tx+20,Ty);
      line(Tx,Ty-20,Tx,Ty+20);
    }
  }
}

class Tower extends AI{
  int cooldown=200;
  Tower(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=12;
    H=48;
    HP=34;
    T=3;
    Animr.ID=EAR.get("Tower");
  }
  float Tx,Ty;
  void math(int SID){
    if(HP<=0){kill.append(SID);return;}
    Fall();
      //Walk(0.3,0.5,6);
      //Phys(W,H,true);
      //Cont(W,H,35);
    if(dist(X,Y,play.X,play.Y)<150){
      Walk(-1,-1,1);
    }
    Cont(W,H,45);
    Phys(W,H,true);
    if(cooldown==0){
      float[] tmp=Enyhitscan(atan2(Ty-Y+12,Tx-X),25,true,0,-H/2);
      AddPartic(1,X,Y-H+6,tmp[0],tmp[1],40,color(255,0,0),true);
      cooldown=200;
    }
    if(cooldown>40 && cooldown<80){
      float[] tmp=Enyscan(atan2(Ty-Y+12,Tx-X)+PI/8*(((float)cooldown-40)/40),true,0,-H/2);
      AddPartic(1,X,Y-H+6,tmp[0],tmp[1],2,color(200,0,0),false);
              tmp=Enyscan(atan2(Ty-Y+12,Tx-X)-PI/8*(((float)cooldown-40)/40),true,0,-H/2);
      AddPartic(1,X,Y-H+6,tmp[0],tmp[1],2,color(200,0,0),false);
      Tx=play.X;
      Ty=play.Y;
    }
    if(cooldown<80){
      float[] tmp=Enyscan(atan2(Ty-Y+12,Tx-X),true,0,-H/2);
      AddPartic(1,X,Y-H+6,tmp[0],tmp[1],2,color(100,0,0),true);
    }
    cooldown--;
    X+=VX;
    Y+=VY;
  }
  void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
    Animr.Anim(abs(VX)>2,false);
    Animr.DIMG(X,Y,W,H,abs(VX)>2,false,#FFFFFF);
  }
}

class Napalm extends AI{
  Napalm(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=8;
    H=16;
    HP=1;
    Animr.ID=EAR.get("Tower");
  }
  void math(int SID){
    if(HP<=0){
      for(int i=0;i<30;i++){
        float R=random(-PI,0);
        float P=random(2,10);
        AddPartic(2,X,Y,cos(R)*P,sin(R)*P,40,color(#FF0000),false);
      }
      for(int i=0;i<9;i++){
        float R=-PI*(float(i)/8);
        NewPR(X,Y-12,cos(R)*6,sin(R)*6,4);
      }
      AddPartic(5,X,Y,100,0,40,color(#FF0000),true);
      kill.append(SID);
      return;
    }
    Fall();
    Phys(W,H,true);
    X+=VX;
    Y+=VY;
  }
  void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
    Animr.Anim(abs(VX)>2,false);
    Animr.DIMG(X,Y,W,H,abs(VX)>2,false,#FFFFFF);
  }
}

class Spirit extends AI{
  Spirit(float nX,float nY,boolean nM){
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
  void math(int SID){
    if(HP<=0){
      kill.append(SID);
      return;
    }
    if(Cooldown==0 & !Con){
      int NUM=floor(random(0,ListAi.size()));
      try{
        if(!(ListAi.get(NUM).getClass()==Class.forName("ProjectDFTEST$Spirit")) && SID!=NUM){
          Connected=NUM;
          Con=true;
          hurte=false;
          Cooldown=240;
        }
      }catch(Exception e){}
    }
    if(Cooldown>0 & !Con){
      Cooldown--;
    }
    float R=atan2(Y-play.Y,X-play.X);
    NewPR(X,Y-H/2,-cos(R),-sin(R),5);
    if(dist(X,Y,play.X,play.Y)<150){
      VX+=cos(R);
      VY+=sin(R);
    }
    if(dist(X,Y,play.X,play.Y)>350){
      VX-=cos(R);
      VY-=sin(R);
    }
    VX=constrain(VX,-2,2);
    VY=constrain(VY,-2,2);
    VY-=0.01;
    Phys(W,H,true);
    X+=VX;
    Y+=VY;
  }
  void render(){
    stroke(#00FFFF);
    fill(#00CCCC);
    rect(X-W,Y-H,W*2,H);
    if(Con){
      try{
      AI tmp = ListAi.get(Connected);
      AddPartic(1,X,Y-H/2,tmp.X,tmp.Y,1,color(#00FFFF,100),true);
      stroke(color(#00FFFF));
      noFill();
      quad(X-64,Y-H/2,X,Y-64-H/2,X+64,Y-H/2,X,Y+64-H/2);
      }catch(Exception e){}
    }
  }
}

class Guardian extends AI{
  int cooldown=360;
  int attack=0;
  int intro=240;
  Guardian(float nX,float nY,boolean nM){
    Bresistance=0;
    X=nX;
    Y=nY;
    M=nM;
    W=50;
    H=100;//1600
    HP=5000;
    T=3;
    hurte=false;
    Animr.ID=EAR.get("Guardian");
  }
  void math(int SID){
    if(intro>0 && HP>0){
      intro--;
      if(intro<120){
        float R=random(-PI,PI);
        float D=random(64,128);
        NewPartic(new Wind(X-cos(R)*D,Y-H/2-sin(R)*D,cos(R)*D/10,sin(R)*D/10,10,#FFFFFF),true);
      }
    }
    if(intro==1 && HP>0){
      BOSSHP.append(HP);
      BOSSID.append(SID);
      hurte=true;
    }
    if(HP<=0){
      float R=random(-PI,PI);
      float D=random(intro/3,intro);
      NewPartic(new Wind(X,Y-H/2,cos(R)*D/3,sin(R)*D/3,10,#FFFFFF),true);
      intro++;
      cooldown=900;
      if(intro>120){
        NewPartic(new Explode(X,Y-H/2,128,0,60,#D80B0B),true);
        NewPartic(new Explode(X,Y-H/2,128+64,0,60,#D8560B),true);
        NewPartic(new Explode(X,Y-H/2,128+128,0,60,#D8C10B),true);
        kill.append(SID);return;
      }
    }
    cooldown--;
    if(cooldown<30 && attack==0){
      float R = random(-PI/4,PI/4)-PI/2;
      NewPR(X,Y-H/2,cos(R)*8,sin(R)*8,7);
    }
    if(cooldown%120==0 && attack==1){
      NewPR(X,Y,8,0,6);
      NewPR(X,Y,-8,0,6);
    }
    if(cooldown==0 && attack==2){
      NewPR(X,Y-H/2,0,0,8);
    }
    if(cooldown==0 && attack==3){
      for(int i=0;i<10;i++){
        NewPR(X,Y-H/2,i*PI/5,0,9);
      }
    }
    if(cooldown==0){
      attack=floor(random(0,4));
      cooldown=120;
    }
    Fall();
    Phys(W,H,true);
    X+=VX;
    Y+=VY;
  }
  void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
    Animr.Anim(false,false);
    Animr.DIMG(X,Y,W,H,false,false,#FFFFFF);
    if(attack == 0){
      fill(255);
    }
    if(attack == 1){
      fill(#FFA600);
    }
    if(attack == 2){
      fill(#FF0000);
    }
    if(attack == 3){
      fill(#00C5FF);
    }
    circle(X,Y-H/2,max(0,map(intro,0,120,64,0)));
  }
}

class Crab extends AI{
  int cooldown=400;
  float PX=0;
  float PY=0;
  Crab(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=32;
    H=48;//1600
    HP=1000;
    T=3;
    Animr.ID=EAR.get("Crab");
  }
  void math(int SID){
    if(HP<=0){
      kill.append(SID);return;
    }
    cooldown--;
    if(cooldown==0){
      cooldown=400;
      expd(PX,PY,128,30,20,true);
      NewPartic(new Line(X,Y,X,Y-2000,60,#e8ff00),false);
      NewPartic(new Line(PX,PY,PX,PY-2000,60,#e8ff00),false);
    }
    Fall();
    if(dist(X,Y,play.X,play.Y)<128){
      Walk(0.0,-0.6,0.0);
    }
    Cont(W,H,15);
    Phys(W,H,true);
    X+=VX;
    Y+=VY;
  }
  void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
    pushMatrix();
    if(VX<0){
      scale(-1,1);
      translate(-X*2,0);
    }
    Animr.Anim(abs(VX)>0.3,false);
    Animr.DIMG(X,Y,W,H,abs(VX)>0.3,false,#FFFFFF);
    popMatrix();
    if(cooldown<255){
      stroke(#e8ff00,255-cooldown);
      strokeWeight((255-cooldown)/25.5);
      line(X,Y,X,Y-2000);
      line(PX,PY,PX,PY-2000);
      if(cooldown>=30){
        PX=play.X;
        PY=play.Y;
      }
      strokeWeight(1);
    }
  }
}

class Piller extends AI{
  boolean Enranged=false;
  int AngyTimer = 300;
  Piller(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=30;
    H=30;
    HP=200;
    T=0;
    Animr.ID=EAR.get("Bug");
  }
  void math(int SID){
      if(HP<=0){kill.append(SID);return;}
      if(play.Gr){
        if(AngyTimer<300){
          AngyTimer+=3;
        }
      }else{
        AngyTimer--;
      }
      if(AngyTimer<=0){
        Enranged=true;
      }
      if(Enranged){
        float R=atan2(play.Y-Y-5,play.X-X);
        for(int i=0;i<3;i++){
          float Rand1=random(-PI/10,PI/10);
          float Rand2=random(-2,2);
          NewPR(X,Y-5,cos(R+Rand1)*(12+Rand2),sin(R+Rand1)*(12+Rand2),11);
        }
      }
      Walk(0.5,0.7,3);
      Cont(W,H,35);
      Phys(W,H,true);
      X+=VX;
      Y+=VY;
  }
  void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
      fill(0);
      text(AngyTimer,X,Y+30);
    }
      pushMatrix();
      if(VX>0){
        scale(-1,1);
        translate(-X*2,0);
      }
       Animr.Anim(true,OG>3);
      if(Enranged){
        Animr.DIMG(X,Y,W,H,true,OG>3,#FFAAAA);
        stroke(255,0,0);
        noFill();
        circle(X+random(-2,2),Y-H/2+random(-2,2),60);
      }else{
        Animr.DIMG(X,Y,W,H,true,OG>3,#FFFFFF);
      }
      popMatrix();
  }
}

class Nucliy extends AI{
  //oh boy
  Nucliy(float nX,float nY,boolean nM){
    X=nX;
    Y=nY;
    M=nM;
    W=24;
    H=48;
    HP=90;
  }
  int Cooldown=0;
  void math(int SID){
    if(HP<=0){
      kill.append(SID);
      return;
    }
    Phys(W,H,true);
    X+=VX;
    Y+=VY;
  }
  void render(){
    if(DebugDraw){
      stroke(0);
      fill(255);
      rect(X-W,Y-H,W*2,H);
    }
  }
}

class AI{
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
  SelfAnim Animr = new SelfAnim();
  int OG=0;
  boolean Ignore;
  boolean hurte=true;
  //AI(float nX,float nY,boolean nM){
  //}
  void math(int SID){
  }
  void render(){
  }
  void Cont(float W,float H,int dmg){
    if(X-W<=play.X+6 && X+W>=play.X-6 && Y>=play.Y-24 && Y-H<=play.Y && play.IV==0 && play.HP>0){
      AThurt(dmg);
      if(X<=play.X){
        VX-=5;
      }else{
        VX+=5;
      }
    }
  }
  void Fall(){
    if(Gr==false){
      if(VY<20){VY+=0.5;}
      OG++;
    }else{
      VX/=2;
      OG=0;
    }
    Gr=false;
  }
  void Walk(float GS,float AS,float JF){
    if(Gr==false){
      if(VY<20){VY+=0.5;}
      if(play.X<X && VX>-7){VX-=AS;}
      if(play.X>X && VX<7 ){VX+=AS;}
      OG++;
    }else{
      if(play.Y+10<Y && random(0,100)<5){VY-=JF;}
      if(play.X<X && VX>-7){VX-=GS;}
      if(play.X>X && VX< 7){VX+=GS;}
      if(play.Y>Y){Ignore=true;}
      OG=0;
    }
    Gr=false;
  }
  void Phys(float W,float H,boolean C){
    SPHYS(X-W,Y  ,X-W+VX+0.01,Y+VY-0.01,C);
    SPHYS(X+W,Y  ,X+W+VX-0.01,Y+VY-0.01,C);
    SPHYS(X-W,Y-H,X-W+VX+0.01,Y-H+VY+0.01,C);
    SPHYS(X+W,Y-H,X+W+VX-0.01,Y-H+VY+0.01,C);
    if(true){
      if(Ignore & C){Checkfor();}
      if(sphys(X-W+VX,Y+VY,X+W+VX,Y+VY) | sphys(X-W+VX,Y-H+VY,X+W+VX,Y-H+VY)){VY=0;}
      if(sphys(X-W+VX,Y+VY,X-W+VX,Y-H+VY) | sphys(X+W+VX,Y+VY,X+W+VX,Y-H+VY)){VX=0;}
    }
  }
  void SPHYS(float T1,float T2,float T3,float T4,boolean C){
    float[] T;
    T=coll(T1,T2,T3,T4,Ignore || !C);
    if(T[0]>=0 && T[0]<=1){
      int i = (int)T[2];
      PVector TOplayer=new PVector(play.X-(CSX[i]+CEX[i])/2,play.Y-12-(CSY[i]+CEY[i])/2);
      PVector Normal=new PVector((CSX[i]-CEX[i]),(CSY[i]-CEY[i]));
      if(CSX[i]>CEX[i]){Normal.rotate(-PI/2);}else{Normal.rotate(PI/2);}
      if(Normal.dot(TOplayer)<0 && CT[i]==1){
        Ignore=true;
      }
      //float R=atan2(CSY[(int)T[2]]-CEY[(int)T[2]],CSX[(int)T[2]]-CEX[(int)T[2]]);
      float R=atan2(CSY[i]-CEY[i],CSX[i]-CEX[i]);
      float NV = VX * cos(R) + VY * sin(R);
      VX = cos(R) * NV * 0.99;
      VY = sin(R) * NV * 0.99;
      //float ISB=atan2(Y-H/2-(CSY[(int)T[2]]+CEY[(int)T[2]])/2,X-(CSX[(int)T[2]]+CEX[(int)T[2]])/2);
      R=atan2(CSY[i]-CEY[i],CSX[i]-CEX[i]);
      if(R<0){R+=PI;}
      if(Normal.dot(TOplayer)>0 && R>-PI/4){//its a feature fuck it
        Gr=true;
      }
      //Gr=true;
    }
  }
  boolean sphys(float T1,float T2,float T3,float T4){
    float[] T;
    T=coll(T1,T2,T3,T4,Ignore);
    if(T[0]>0 && T[0]<1){
      return true;
    }
    return false;
  }
  void HURT(int dmg)
  {
    if(!hurte){return;}
    HP-=dmg;
    for(int B=0;B<5;B++){
      AddPartic(4,X,Y,random(-1,1),random(-8,-2),50,color(255,0,0),true);
    }
    if(play.regenera==0){
      if(dist(X,Y,play.X,play.Y)<=200 && play.HP>0){
        AddPartic(1,play.X+random(-5,5),play.Y-12+random(-5,5),X+random(-5,5),Y-H/2+random(-5,5),60,color(255,0,0),true);
        if(play.HP+dmg/4>100){
          play.HP=100;
        }else{
          play.HP+=dmg/4;
        }
      }
    }else{
      if(random(1,100)<50 && play.HP>0){
        NewPR(X,Y-H/2,random(-5,5),random(-5,5),10);
      }
    }
  }

  float[] Enyhitscan(float R, int dmg,boolean lazer,float Offx,float Offy) {
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
        if(!lazer){
          MT=min(MT, min(T1, T2));
          float[] tmp = {play.X+cos(R)*MT, play.Y-12+sin(R)*MT};
          return tmp;
        }
      }
    }
    float[] tmp = {X+Offx+cos(R)*MT,Y+Offy+sin(R)*MT};
    return tmp;
  }

  float[] Enyscan(float R,boolean lazer,float Offx,float Offy) {
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
        if(!lazer){
          MT=min(MT, min(T1, T2));
          float[] tmp = {play.X+cos(R)*MT, play.Y-12+sin(R)*MT};
          return tmp;
        }
      }
    }
    float[] tmp = {X+Offx+cos(R)*MT,Y+Offy+sin(R)*MT};
    return tmp;
  }
  void Checkfor(){//find better sulucion
    int buffer=3;
    if(!(Checkforsub(X-W/2,Y+buffer,X-W/2,Y-H-buffer)||
    Checkforsub(X+W/2,Y+buffer,X+W/2,Y-H-buffer)||
    Checkforsub(X-W/2-buffer,Y,X+W/2+buffer,Y)||
    Checkforsub(X-W/2+buffer,Y-H,X+W/2+buffer,Y-H))){
      Ignore=false;
    }
  }
  boolean Checkforsub(float SX,float SY,float EX,float EY){
    int[] list=CB(SX,SY,EX,EY);
    for(int e=0;e<list.length;e++){
      int i=list[e];
      float t;
      float r;
      t=TLineToLine(SX,SY,EX,EY,CSX[i],CSY[i],CEX[i],CEY[i]);
      r=TLineToLine(CSX[i],CSY[i],CEX[i],CEY[i],SX,SY,EX,EY);
      float T=1;
      if(t<T && t>0 && r<=1 && r>=0){
          T=t;
          return true;
      }
    }
    return false;
  }
}

void NewAI(float X,float Y,String T,boolean M){
  //Need to figure a better way of doin this
  switch(T){//
    case "Bug":
      ListAi.add(new Bug(X,Y,M));
    break;
    case "Fly":
      ListAi.add(new Fly(X,Y,M));
    break;
    case "Target":
      ListAi.add(new Target(X,Y,M));
    break;
    case "Spewer":
      ListAi.add(new Spewer(X,Y,M));
    break;
    case "testBoss":
      ListAi.add(new testBoss(X,Y,M));
    break;
    case "Maze":
      ListAi.add(new Maze(X,Y,M,false));
    break;
    case "Laze":
      ListAi.add(new Laze(X,Y,M,false));
    break;
    case "Maze_Boss":
      ListAi.add(new Maze(X,Y,M,true));
    break;
    case "Laze_Boss":
      ListAi.add(new Laze(X,Y,M,true));
    break;
    case "tower":
      ListAi.add(new Tower(X,Y,M));
    break;
    case "napalm":
      ListAi.add(new Napalm(X,Y,M));
    break;
    case "Spirit":
      ListAi.add(new Spirit(X,Y,M));
    break;
    case "Guardian":
      ListAi.add(new Guardian(X,Y,M));
    break;
    case "Crab":
      ListAi.add(new Crab(X,Y,M));
    break;
    case "Piller":
      ListAi.add(new Piller(X,Y,M));
    break;
  }
}

float[] coll(float OX,float OY,float NX,float NY,boolean Ignore){
  for(int i=0;i<CSX.length;i++){//OH god this is terrible
    float T=100;
    float N=0;
    float t;
    float r;
    
    t=TLineToLine(OX,OY,NX,NY,CSX[i],CSY[i],CEX[i],CEY[i]);
    r=TLineToLine(CSX[i],CSY[i],CEX[i],CEY[i],OX,OY,NX,NY);
    for(int u=0;u<4;u++){
      if(t<=T && t>=0 && r<=1 && r>=0 && !(Ignore && CT[i]==1)){
        {
          T=t;
          N=u;
        }
      }
    }
    if(T>=0 && T<=1){float[] tmp = {T,N,i};return tmp;}
  }
  float[] tmp = {-1};
  return tmp;
}
