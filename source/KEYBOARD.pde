boolean keys[];
boolean Skeys[];

IntDict KeyRedirect;//for convinence
KeyBind[] Keybinds;

class KeyBind{
  char pri;
  boolean iscode;
  byte code;
  KeyBind(char Pri,int num,String ID){
    if(num==-1){
      pri=Pri;
      iscode=false;
      code=0;
    }else{
      pri='?';
      iscode=true;
      code=byte(num);
    }
    KeyRedirect.set(ID,KeyRedirect.size());
  }
  boolean check(){
    if(iscode){
    return EYS.getSkey(code);
    }else{
    return EYS.getkey(pri);
    }
  }
}

boolean GetKeyBind(String code){
  if(!KeyRedirect.hasKey(code)){
    PrintCon("sorry for that");
    PrintCon("it seams that "+code+" does not exist");
    ErrorTimer=120;
    return false;
  }
  return Keybinds[KeyRedirect.get(code)].check();
}

void setupKeys(){
  EYS=new keyboard();
  KeyRedirect = new IntDict();
  Keybinds = new KeyBind[0];
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('w',-1,"Player_Move_Up"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('d',-1,"Player_Move_Right"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('a',-1,"Player_Move_Left"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('s',-1,"Player_Move_Down"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('r',-1,"Player_Restart"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('?',4,"Player_Boost"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('1',-1,"Weapon_1"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('2',-1,"Weapon_2"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('3',-1,"Weapon_3"));
  Keybinds = (KeyBind[])append(Keybinds,new KeyBind('4',-1,"Weapon_4"));
}

class keyboard
{
  keyboard()
  {
    keys = new boolean[128];
    Skeys = new boolean[8];
  }
  boolean getkey(char A)
  {
    return keys[int(A)];
  }
  boolean getSkey(int i)
  {
    return Skeys[i];
  }
}

void keyPressed()
{
  if(!WaitingUser){
    if(!ConsoleUP){
      if(key>=0&key<=128)
      {
        try{
          keys[int((key+"").toLowerCase().charAt(0))]=true;
        }catch(Exception E){
          PrintCon("TODO FIX THIS KEY");
          ErrorTimer=120;
        }
      }
      //if(key=='n'){play.Noclip){play.Noclip=false;}else{play.Noclip=true;}}
      if(keyCode==UP){Skeys[0]=true;}
      if(keyCode==RIGHT){Skeys[1]=true;}
      if(keyCode==LEFT){Skeys[2]=true;}
      if(keyCode==DOWN){Skeys[3]=true;}
      if(keyCode==SHIFT){Skeys[4]=true;}
      if(keyCode==CONTROL){Skeys[5]=true;}
      if(keyCode==ALT){Skeys[6]=true;}
      if(keyCode==TAB){Skeys[7]=true;}
      if(GetKeyBind("Weapon_1")){WeaponSellected=0;}
      if(GetKeyBind("Weapon_2")){WeaponSellected=1;}
      if(GetKeyBind("Weapon_3")){WeaponSellected=2;}
      if(GetKeyBind("Weapon_4")){WeaponSellected=3;}
      if(key=='\\'){ConsoleUP=!ConsoleUP;}
      if(key==ESC && Gaming){
        if(MenuPaused){
          MenuTurnOffAll();
          MenuPaused=false;
          RunPhys=true;
        }else{
          MenuTurnOffAll();
          MenuTurnOn("PAUSE_MENU");
          MenuPaused=true;
          RunPhys=false;
        }
      }
    }else{
      if(key=='\\'){ConsoleUP=!ConsoleUP;}
      switch(keyCode){
        case ENTER:
          runConinput();
        break;
        case BACKSPACE:
          if(ConsoleInput.length()>0){
            ConsoleInput=ConsoleInput.substring(0,ConsoleInput.length()-1);
          }
        break;
        case SHIFT:
        //ohno
        break;
        case ESC:
          ConsoleUP=false;
          key=0;
        break;
        default:
          ConsoleInput+=key;
        break;
      }
    }
  }else{
    if(key==CODED){
      Keybinds[ToWait].code=returnCODED(keyCode);
      Keybinds[ToWait].iscode=true;
      WaitingUser=false;
    }else{
      Keybinds[ToWait].pri=key;
      Keybinds[ToWait].iscode=false;
    }
    WaitingUser=false;
  }
  key=0;
}
void keyReleased()
{
  if(key>=0&key<=128)
  {
    try{
      keys[int((key+"").toLowerCase().charAt(0))]=false;
    }catch(Exception E){
      PrintCon("TODO FIX THIS KEY");
      ErrorTimer=120;
    }
  }
  if(keyCode==UP){Skeys[0]=false;}
  if(keyCode==RIGHT){Skeys[1]=false;}
  if(keyCode==LEFT){Skeys[2]=false;}
  if(keyCode==DOWN){Skeys[3]=false;}
  if(keyCode==SHIFT){Skeys[4]=false;}
  if(keyCode==CONTROL){Skeys[5]=false;}
  if(keyCode==ALT){Skeys[6]=false;}
  if(keyCode==TAB){Skeys[7]=false;}
}

void mouseWheel(MouseEvent event) {
  float e = event.getCount();
  if(Configs.get("ReversedWhell")==1){
    e*=-1;
  }
  if(!ConsoleUP){
    if(WeaponSellected<HWeapon.length-1 && e==-1){
      WeaponSellected++;
      return;
    }
    if(WeaponSellected>0 && e==1){
      WeaponSellected--;
      return;
    }
    if(WeaponSellected==HWeapon.length-1 && e==-1){
      WeaponSellected=0;
      return;
    }
    if(WeaponSellected==0 && e==1){
      WeaponSellected=byte(HWeapon.length-1);
      return;
    }
  }else{
    if(e==-1 && scrool>0){
      scrool--;
    }
    if(e== 1 && scrool<80){
      scrool++;
    }
  }
}

byte returnCODED(int c){
  if(c==UP){return 0;}
  if(c==RIGHT){return 1;}
  if(c==LEFT){return 2;}
  if(c==DOWN){return 3;}
  if(c==SHIFT){return 4;}
  if(c==CONTROL){return 5;}
  if(c==ALT){return 6;}
  if(c==TAB){return 7;}
  return 0;
}
String returnTEXE(int c){
  if(c==0){return "UP";}
  if(c==1){return "RIGHT";}
  if(c==2){return "LEFT";}
  if(c==3){return "DOWN";}
  if(c==4){return "SHIFT";}
  if(c==5){return "CONTROL";}
  if(c==6){return "ALT";}
  if(c==7){return "TAB";}
  return "????";
}
