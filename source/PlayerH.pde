class Player{
  float X;
  float Y;
  float VX;
  float VY;
  float PVY;
  float PX;
  float PY;
  float PO;
  int HP=100;
  int HD=0;
  int HDdelay=0;
  boolean Noclip=false;
  boolean God=false;
  boolean notime=false;
  int IV=0;
  boolean Gr=false;
  boolean Ignore=false;
  int cooldownV = 0;
  int cooldownH = 0;
  boolean frezzing=false;
  boolean water=false;
  int coolmeter=0;
  byte vertical=0;
  byte mobilaty=0;
  byte regenera=0;
  Player(){
    X=PSX;
    Y=PSY;
    VX=0;
    VY=0;
    PO=0;
  }
  
  void MT(){
    X=PSX+NMOX;
    Y=PSY+NMOY;
    PX=X;
    PY=Y;
    play.HP=100;
    play.HD=0;
    play.HDdelay=0;
    Restart();
  }
  
  void drawM(){
    pushMatrix();
    scale(ZOOMER);
    stroke(0);
    fill(0,0,200);
    rect(width/2/ZOOMER-6,height/2/ZOOMER-24,12,24);
    line(width/2/ZOOMER,height/2/ZOOMER-8,width/2/ZOOMER+play.VX,height/2/ZOOMER+play.VY-8);
    popMatrix();
  }
  
  void drawI(){
    float rescale=(float)Configs.get("GuiScale")/100;
    pushMatrix();
    scale(rescale);
    fill(#00FFEE,100);
    noStroke();
    arc(width/2*(1/rescale)-5,(height/2+24*rescale)*(1/rescale),10,10, -PI/2, (2*PI*cooldownV/80)-PI/2);
    arc(width/2*(1/rescale)+5,(height/2+24*rescale)*(1/rescale),10,10, -PI/2, (2*PI*cooldownH/80)-PI/2);
    noFill();
    stroke(0,255,0,100);
    strokeWeight(5);
    strokeCap(SQUARE);
    arc(width/2*(1/rescale),(height/2-12)*(1/rescale),36,36,PI/2,PI/2+PI*play.HP/100);
    noFill();
    stroke(200,100);
    strokeWeight(5);
    strokeCap(SQUARE);
    arc(width/2*(1/rescale),(height/2-12)*(1/rescale),36,36,PI/2+PI*(100-play.HD)/100,PI/2+PI);
    noFill();
    stroke(#75E8ED,150);
    strokeWeight(2);
    strokeCap(SQUARE);
    arc(width/2*(1/rescale),(height/2-12)*(1/rescale),36,36,PI/2,PI/2+PI*play.coolmeter/180);
    strokeWeight(1);
    strokeCap(ROUND);
    stroke(0);
    stroke(100,100);
    strokeWeight(5);
    strokeCap(SQUARE);
    arc(width/2*(1/rescale),(height/2-12)*(1/rescale),45,45,PI/2,PI/2+PI*IV/20);
    popMatrix();
    pushMatrix();
    scale(ZOOMER);
    stroke(100,0,0);
    strokeWeight(2);
    circle(width/2/ZOOMER,height/2/ZOOMER,400);
    strokeWeight(1);
    strokeCap(ROUND);
    popMatrix();
  }
  
  void Force(float NVX,float NVY){
    VX += NVX;
    VY += NVY;
  }

  void dash(int m){
    switch(mobilaty){
      case 0:
      if(cooldownH==0){
        if(Gr==false){
          VX=m*15;
          VY=-2;
        }else{
          VX=m*18;
          VY=0;
        }
        cooldownH=80;
        for(int i=0;i<5;i++){
          AddPartic(6,X+random(-6,6),Y+random(-24,0),VX/2,VY/2,10,#F2F2F2,false);
        }
      }
      break;
      case 1:
      if(cooldownH==0){
        if(Gr==false){
          VX=0;
          VY=0;
        }else{
          VX=0;
          VY=0;
        }
        cooldownH=80;
        IV=30;
        for(int i=0;i<15;i++){
          AddPartic(6,X,Y-12,random(-5,5),random(-5,5),30,#F2F2F2,false);
        }
      }
      break;
    }
  }

  void updash(){
    switch(vertical){
      case 0:
      if(cooldownV==0){
        if(Gr==false){
          VX=0;
          VY=-15;
        }else{
          VX=0;
          VY=-20;
        }
        cooldownV=80;
        for(int i=0;i<5;i++){
          AddPartic(6,X+random(-6,6),Y+random(-24,0),VX/2,VY/2,10,#F2F2F2,false);
        }
      }
      break;
      case 1:
      if(cooldownV<80){
        VY=min(VY-0.8,0);
        cooldownV+=2;
      }
      break;
    }
  }

  void Phy(){
    PX=X;
    PY=Y;
    HP=min(HP,100-HD);
    if(!Noclip){
    if(Gr){VY=0.1;}
    if(Gr==false){
      if(!water){
        if(VY<20){VY+=0.5;}
        if(GetKeyBind("Player_Move_Down") && VY<20 && HP>0 && !ConsoleUP){VY+=0.15;}
        if(GetKeyBind("Player_Move_Left") && VX>-7 && HP>0 && !ConsoleUP){VX-=0.6;}
        if(GetKeyBind("Player_Move_Right") && VX<7 && HP>0 && !ConsoleUP){VX+=0.6;}
        if(GetKeyBind("Player_Move_Up") && GetKeyBind("Player_Boost") && HP>0 && !ConsoleUP){updash();}
        if(GetKeyBind("Player_Move_Left") && GetKeyBind("Player_Boost") && HP>0 && !ConsoleUP){dash(-1);}
        if(GetKeyBind("Player_Move_Right") && GetKeyBind("Player_Boost") && HP>0 && !ConsoleUP){dash(1);}
      }else{
        if(HP<=0){VY=-0.5;}
        if(!(GetKeyBind("Player_Move_Down") | GetKeyBind("Player_Move_Up"))){VY/=2;}
        if(!(GetKeyBind("Player_Move_Left") | GetKeyBind("Player_Move_Right"))){VX/=2;}
        if(GetKeyBind("Player_Move_Down") && VY<5 && HP>0 && !ConsoleUP){VY+=2.5;}
        if(GetKeyBind("Player_Move_Up") && VY>-5  && HP>0 && !ConsoleUP){VY-=2.5;}
        if(GetKeyBind("Player_Move_Left") && VX>-5  && HP>0 && !ConsoleUP){VX-=2.5;}
        if(GetKeyBind("Player_Move_Right") && VX<5  && HP>0 && !ConsoleUP){VX+=2.5;}
      }
    }else{
      if(PVY>=5){
        for(int i=0;i<5;i++){
          AddPartic(2,X,Y,random(-5,5)+VX/2,-random(5,PVY)/3,15,color(100),false);
        }
      }
      if(GetKeyBind("Player_Move_Up") && HP>0 && !ConsoleUP){VY=-12;}
      if(!GetKeyBind("Player_Move_Right") && !GetKeyBind("Player_Move_Left")){VX/=2;}
      if(GetKeyBind("Player_Move_Left") && VX>-7 && HP>0 && !ConsoleUP){VX-=0.7;}
      if(GetKeyBind("Player_Move_Right") && VX< 7 && HP>0 && !ConsoleUP){VX+=0.7;}
      if(GetKeyBind("Player_Move_Up") && GetKeyBind("Player_Boost") && HP>0 && !ConsoleUP){updash();}
      if(GetKeyBind("Player_Move_Left") && GetKeyBind("Player_Boost") && HP>0 && !ConsoleUP){dash(-1);}
      if(GetKeyBind("Player_Move_Right") && GetKeyBind("Player_Boost") && HP>0 && !ConsoleUP){dash(1);}
      if(GetKeyBind("Player_Move_Down") && HP>0 && !ConsoleUP){Ignore=true;}
    }
    PVY=VY;
    if(Gr){cooldownV=0;}
    Gr=false;
    if((GetKeyBind("Player_Restart") && HP<=0 && !ConsoleUP)| X>10000 | X<-10000 | Y>10000 | Y<-10000){
      if(X>10000 | X<-10000 | Y>10000 | Y<-10000){
        ErrorTimer=120;
      }
      MT();
      VX=0;
      VY=0;
      BOSSID.clear();
      BOSSHP.clear();
      if(tantactive){
        ResartWave();
      }
    }
    if(cooldownH>0){cooldownH--;}
    if(!frezzing){
      if(coolmeter>0){
        coolmeter--;
      }
    }else{
      if(coolmeter<180){
        coolmeter++;
      }else{
        AThurt(15);
      }
    }
    frezzing=false;
    water=false;
    if(Ignore){Checkfor();}
    coll(X-6,Y   ,X-6+VX+0.01,Y+VY+0.01);
    coll(X+6,Y   ,X+6+VX+0.01,Y+VY+0.01);
    coll(X-6,Y-24,X-6+VX+0.01,Y-24+VY+0.01);
    coll(X+6,Y-24,X+6+VX+0.01,Y-24+VY+0.01);
    if(cill(-6+X+VX,Y+VY- 0, 6+X+VX,Y+VY- 0) | cill(-6+X+VX,Y+VY-24, 6+X+VX,Y+VY-24)){VY=0;}
    if(cill(-6+X+VX,Y+VY- 0,-6+X+VX,Y+VY-24) | cill( 6+X+VX,Y+VY- 0, 6+X+VX,Y+VY-24)){VX=0;}
    if(play.IV>0){
      play.IV--;
    }
    if(HP<=0){
      play.IV=0;
      HP=0;
    }
    if(HP<=1 && notime){
      HP=1;
    }
    if(HD>0 && HDdelay==0){HD--;}
    if(HDdelay>0){HDdelay--;}
    X+=VX;
    Y+=VY;
    }else{
      if(( GetKeyBind("Player_Move_Up") ||  EYS.getSkey(0)) && !ConsoleUP){Y-=10;}
      if(( GetKeyBind("Player_Move_Left") ||  EYS.getSkey(2)) && !ConsoleUP){X-=10;}
      if(( GetKeyBind("Player_Move_Right") ||  EYS.getSkey(1)) && !ConsoleUP){X+=10;}
      if(( GetKeyBind("Player_Move_Down") ||  EYS.getSkey(3)) && !ConsoleUP){Y+=10;}
    }
  }
  
  void coll(float OX,float OY,float NX,float NY){
    int[] list=CB(OX,OY,NX,NY);
    for(int u=0;u<list.length;u++){//god is here!
      float T=100;
      float N=0;
      float t;
      float r;
      int i = list[u];
      
      t=TLineToLine(OX,OY,NX,NY  ,CSX[i],CSY[i],CEX[i],CEY[i]);
      r=TLineToLine(CSX[i],CSY[i],CEX[i],CEY[i],OX,OY,NX,NY);
      for(int y=0;y<4;y++){
        if(t<=T && t>=0 && r<=1 && r>=0 && !(Ignore && CT[i]==1)){
          {
            T=t;
          }
        }
      }
      if(T>=0 && T<=1){
        PVector TOplayer=new PVector(play.X-(CSX[i]+CEX[i])/2,play.Y-12-(CSY[i]+CEY[i])/2);
        PVector Normal=new PVector((CSX[i]-CEX[i]),(CSY[i]-CEY[i]));
        if(CSX[i]>CEX[i]){Normal.rotate(-PI/2);}else{Normal.rotate(PI/2);}
        if(Normal.dot(TOplayer)<0 && CT[i]==1){
          Ignore=true;
        }
        //TODO fix this!!! why is it working? odd
        float R=atan2(CSY[i]-CEY[i],CSX[i]-CEX[i]);
        float NV = VX * cos(R) + VY * sin(R);
        if(!(Ignore && CT[i]==1)){
        VX = cos(R) * NV * 0.99;
        VY = sin(R) * NV * 0.99;
        }
        //PVector TOplayer=atan2(play.Y-12-(CSY[i]+CEY[i])/2,play.X-(CSX[i]+CEX[i])/2);
        //PVector TOplayer=atan2(play.Y-12-(CSY[i]+CEY[i])/2,play.X-(CSX[i]+CEX[i])/2);
        R=atan2(CSY[i]-CEY[i],CSX[i]-CEX[i]);
        if(R<0){R+=PI;}
        if(Normal.dot(TOplayer)>0 && R>-PI/4){//its a feature fuck it
          Gr=true;
        }
        break;
      }
    }
  }
  
  boolean cill(float OX,float OY,float NX,float NY){
    int[] list=CB(OX,OY,NX,NY);
    for(int e=0;e<list.length;e++){//thaank youu doom
      int i=list[e];
      float t;
      float r;
      t=TLineToLine(OX,OY,NX,NY,CSX[i],CSY[i],CEX[i],CEY[i]);
      r=TLineToLine(CSX[i],CSY[i],CEX[i],CEY[i],OX,OY,NX,NY);
      float T=100;
      if(t<T && t>0 && r<1 && r>0){
          T=t;
        }
      //println(T+"|"+R);
      if(T>0 && T<1 && !(Ignore && CT[i]==1)){
        //X -= sin(N*PI/2+CR[i])*2;
        //Y -= cos(N*PI/2+CR[i])*2;
        return true;
      }
    }
    return false;
  }
  void Checkfor(){//find better sulucion
    int buffer=3;
    if(!(Checkforsub(X-6,Y+buffer,X-6,Y-24-buffer)||
    Checkforsub(X+6,Y+buffer,X+6,Y-24-buffer)||
    Checkforsub(X-6-buffer,Y,X+6+buffer,Y)||
    Checkforsub(X-6+buffer,Y-24,X+6+buffer,Y-24))){
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

void AThurt(int dmg){
  if(play.IV==0 && !play.God){
    play.HP-=dmg;
    play.HD+=dmg/3*2;
    play.HDdelay=120;
    play.IV=40;
    hurtmepleanty=10;
  }
}
