void Menu() {
  MATHUI();
  textAlign(LEFT, TOP);
}

void MenuSetup() {
  menuUI = new UI[0];

  //PAUSE_MENU

  menuUI = (UI[])append(menuUI, new Text      (20, 50, 100, 40, "PAUSE_MENU", "nothing", "PAUSED"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, height-110-50, 100, 40, "PAUSE_MENU", "restart", "restart"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, height-110, 100, 40, "PAUSE_MENU", "GotoOptions", "options"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, height-110+50, 100, 40, "PAUSE_MENU", "Exit", "Exit the thing"));
  menuUI = (UI[])append(menuUI, new Slot      (width-210, 50, 200, 20, "PAUSE_MENU", 0));
  menuUI = (UI[])append(menuUI, new Slot      (width-210, 70, 200, 20, "PAUSE_MENU", 1));
  menuUI = (UI[])append(menuUI, new Slot      (width-210, 90, 200, 20, "PAUSE_MENU", 2));
  menuUI = (UI[])append(menuUI, new Text      (width-310, 50, 100, 60, "PAUSE_MENU", "nothing", "testing this things"));

  //MAIN_MENU

  menuUI = (UI[])append(menuUI, new ButtonText(20, height-160, 100, 40, "MAIN_MENU", "RunGame", "go to the arena"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, height-160-50, 100, 40, "MAIN_MENU", "GotoTutorial", "tutorial"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, height-160+50, 100, 40, "MAIN_MENU", "GotoOptions", "options"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, height-160+100, 100, 40, "MAIN_MENU", "Quit", "Quit"));
  menuUI = (UI[])append(menuUI, new ButtonText(140, height-160+100, 100, 40, "MAIN_MENU", "UPDATE", "Update"));
  
  //saves//
  
  menuUI = (UI[])append(menuUI, new SaveButton(140, height-160, 100, 40, "MAIN_MENU", "nothing" , 10));
  menuUI = (UI[])append(menuUI, new SaveButton(260, height-160, 100, 40, "MAIN_MENU", "nothing" , 20));
  
  //OPTIONS_MENU

  menuUI = (UI[])append(menuUI, new ButtonText(20, height-110+50, 100, 40, "OPTIONS_MENU", "GotoMain", "back"));
  menuUI = (UI[])append(menuUI, new Text      (20, 50, 100, 40, "OPTIONS_MENU", "nothing", "this is the options menu"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, 90, 100, 40, "OPTIONS_MENU", "GotoBinds", "KeyBinds"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, 130, 100, 40, "OPTIONS_MENU", "GotoNots", "Visuals"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, 170, 100, 40, "OPTIONS_MENU", "Save_config", "save"));
  
  //Binds

  menuUI = (UI[])append(menuUI, new BindButton(140, 50, "Binds", "Player_Move_Up", "jump"));
  menuUI = (UI[])append(menuUI, new BindButton(140, 90, "Binds", "Player_Move_Down", ""));
  menuUI = (UI[])append(menuUI, new BindButton(140, 130, "Binds", "Player_Move_Left", "slide to the left"));
  menuUI = (UI[])append(menuUI, new BindButton(140, 170, "Binds", "Player_Move_Right", "slide to the right"));
  menuUI = (UI[])append(menuUI, new BindButton(140, 210, "Binds", "Player_Restart", "quick restart"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 50, "Binds", "Player_Boost", "special"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 90, "Binds", "Weapon_1", "slot garant"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 130, "Binds", "Weapon_2", "slot rocket"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 170, "Binds", "Weapon_3", "slot railgun"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 210, "Binds", "Weapon_4", "slot shotgun"));
  
  //Nots

  menuUI = (UI[])append(menuUI, new ButtonToggle(140, 50, 400, 40, "Nots", "DrawEffects", "DrawEffects : Only important Effect will display"));
  menuUI = (UI[])append(menuUI, new ButtonSlider(140, 90, 400, 40, "Nots", "GuiScale", "GuiScale : Scale of the Hud Rings", 50, 500));
  menuUI = (UI[])append(menuUI, new ButtonToggle(140, 130, 400, 40, "Nots", "ReversedWhell", "ReversedWhell : reverse the weapon whell seletion"));
  menuUI = (UI[])append(menuUI, new ButtonSlider(140, 170, 400, 40, "Nots", "HurtScale", "HurtScale : scale the \"vinete\" that apears", 1, 50));
  menuUI = (UI[])append(menuUI, new ButtonToggle(140, 210, 400, 40, "Nots", "ShouldPause", "ShouldPause : autopause if unfocused"));
  menuUI = (UI[])append(menuUI, new ButtonToggle(140, 250, 400, 40, "Nots", "SimpleExplosion", "SimpleExplosion : makes explosions simpler goodFps boost for them"));
  //menuUI = (UI[])append(menuUI, new ButtonToggle(140,290, 400, 40, "Nots", "Fullscreen", "Fullscreen : Fullscreen"));
  menuUI = (UI[])append(menuUI, new ButtonSlider(140, 290, 400, 40, "Nots", "Zoom", "Zoom : How much zoom", 10, 200));

  MenuTurnOn("MAIN_MENU");
}

void GotoTutorial() {
  Start("tutorial");
  toturialTimer = 0;
  toturialMode = true;
}

void nothing() {/*!nothing!*/
}

void restart() {
  play.MT();
  if (tantactive) {
    ResartWave();
  }
}

void MATHUI() {
  for (int i=0; i<menuUI.length; i++) {
    if (menuUI[i].Enable) {
      menuUI[i].Draw(i);
    }
  }
  if (mousePressed) {
    for (int i=0; i<menuUI.length; i++) {
      if (menuUI[i].Enable
        &&mouseX>menuUI[i].x
        &&mouseX<menuUI[i].x+menuUI[i].w
        &&mouseY>menuUI[i].y
        &&mouseY<menuUI[i].y+menuUI[i].h) {
        menuUI[i].CFunc(i);
      }
    }
  }
  MenuSwap=false;
}

int submenu=0;
boolean MenuSwap=false;//i exist to destroy the jank

UI[] menuUI;

void RunGame() {
  //Start("AItest");
  tantactive=true;
  tantrest();
  nextWave();
  MenuPaused=false;
}

void GotoOptions() {
  MenuTurnOffAll();
  MenuTurnOn("OPTIONS_MENU");
  MenuTurnOn("Binds");
  MenuSwap=true;
}

void Save_config() {
  JSONObject tmp = loadJSONObject("Misc/config.json");
  JSONObject out = new JSONObject();
  out.setBoolean("DebugStart", tmp.getBoolean("DebugStart"));
  out.setString("DebugMap", tmp.getString("DebugMap"));
  out.setBoolean("DebugTant", tmp.getBoolean("DebugTant"));
  JSONArray binds = new JSONArray();
  for (int u=0; u<Keybinds.length; u++) {
    JSONObject bind = new JSONObject();
    bind.setString("equa", KeyRedirect.key(u));
    bind.setString("key", str(Keybinds[u].pri));
    bind.setBoolean("iscode", Keybinds[u].iscode);
    bind.setInt("code", Keybinds[u].code);
    binds.append(bind);
  }
  out.setJSONArray("Binds", binds);
  for (int u=0; u<Configs.size(); u++) {
    out.setInt(Configs.key(u), Configs.get(Configs.key(u)));
  }
  saveJSONObject(out, "data/Misc/config.json");
  println("test");
}

void Quit() {
  exit();
}

void Exit() {
  Gaming=false;
  MenuTurnOffAll();
  MenuTurnOn("MAIN_MENU");
  MenuSwap=true;
}

void GotoMain() {
  if (Gaming) {
    MenuTurnOffAll();
    MenuTurnOn("PAUSE_MENU");
    MenuSwap=true;
  } else {
    MenuTurnOffAll();
    MenuTurnOn("MAIN_MENU");
    MenuSwap=true;
  }
}

void GotoBinds() {
  MenuTurnOff("Nots");
  MenuTurnOn("Binds");
  MenuSwap=true;
}

void GotoNots() {
  MenuTurnOff("Binds");
  MenuTurnOn("Nots");
  MenuSwap=true;
}

boolean WaitingUser=false;
int Waiter=0;
int ToWait=0;

//find better solucion
String[] slot1 = {"ESCAPE", "JET"};
String[] desc1 = {"updash", "constant up movement"};
String[] slot2 = {"DASH", "NO_U"};
String[] desc2 = {"dash", "null all movment + 30f of nodie"};
String[] slot3 = {"EXTRACT", "SHARPNEL"};
String[] desc3 = {"hurt to heal", "50% of frag to drop +5HP"};

class SaveButton extends UI {
  String Text;
  int save;
  SaveButton(float nx, float ny, float nw, float nh, String nCall,String nRun,int save) {
    x=nx;
    y=ny;
    w=nw;
    h=nh;
    Call=nCall;
    Run=nRun;
    this.save=save;
  }
  void Draw(int i) {
    if(CurrentSave<save){return;}
    textAlign(CENTER, CENTER);
    stroke(#676767);
    fill(#404040);
    rect(x, y, w, h);
    fill(#BFBFBF);
    text("\"level\" "+(save), x+w/2, y+h/2);
  }
  void CFunc(int i) {
    if(CurrentSave<save){return;}
    tantactive=true;
    tantrest();
    round=save-1;
    nextWave();
    MenuPaused=false;
  }
}


class Slot extends UI {
  int which=0;
  Slot(float nx, float ny, float nw, float nh, String nCall, int nwhich) {
    x=nx;
    y=ny;
    w=nw;
    h=nh;
    Call=nCall;
    which=nwhich;
  }
  void Draw(int i) {
    textAlign(CENTER, CENTER);
    stroke(#676767);
    fill(#808080);
    rect(x, y, w, h);
    rect(x, y, 50, h);
    rect(x+w-50, y, 50, h);
    line(x+50, y, x, y+h/2);
    line(x+50, y+h, x, y+h/2);
    line(x+w-50, y, x+w, y+h/2);
    line(x+w-50, y+h, x+w, y+h/2);
    fill(#000000);
    if (which==0) {
      text(slot1[play.vertical], x+w/2, y+h/2);
    }
    if (which==1) {
      text(slot2[play.mobilaty], x+w/2, y+h/2);
    }
    if (which==2) {
      text(slot3[play.regenera], x+w/2, y+h/2);
    }
    if (mouseX>x+50 && mouseX<x+w-50 && mouseY>y && mouseY<y+h) {
      fill(#808080);
      rect(x+50, y, w-100, h);
      fill(#000000);
      if (which==0) {
        text(desc1[play.vertical], x+w/2, y+h/2);
      }
      if (which==1) {
        text(desc2[play.mobilaty], x+w/2, y+h/2);
      }
      if (which==2) {
        text(desc3[play.regenera], x+w/2, y+h/2);
      }
    }
  }
  void CFunc(int i) {
    int u=0;
    if (mouseX<x+50) {
      u--;
    }
    if (mouseX>x+w-50) {
      u++;
    }
    if (which==0 && ((play.vertical>0 && u<0) || (play.vertical<slot1.length-1 && u>0))) {
      play.vertical+=u;
    }
    if (which==1 && ((play.mobilaty>0 && u<0) || (play.mobilaty<slot2.length-1 && u>0))) {
      play.mobilaty+=u;
    }
    if (which==2 && ((play.regenera>0 && u<0) || (play.regenera<slot2.length-1 && u>0))) {
      play.regenera+=u;
    }
  }
}

class ButtonSlider extends UI {
  String Text;
  String Var;
  int min;
  int max;
  ButtonSlider(float nx, float ny, float nw, float nh, String nCall, String nVar, String nText, int Min, int Max) {
    x=nx;
    y=ny;
    w=nw;
    h=nh;
    Call=nCall;
    Var=nVar;
    Text=nText;
    min=Min;
    max=Max;
  }
  void Draw(int i) {
    textAlign(CENTER, CENTER);
    stroke(#676767);
    fill(#808080);
    rect(x, y, w, h);
    fill(#404040);
    rect(x, y, map(Configs.get(Var), min, max, 0, w), h);
    fill(#BFBFBF);
    text(Text, x+w/2, y+h/2-5);
    text(Configs.get(Var), x+w/2, y+h/2+5);
  }
  void CFunc(int i) {
    Configs.set(Var, constrain((int)map(mouseX, x+5, x+w-5, min, max), min, max));
  }
}

class ButtonToggle extends UI {
  String Text;
  String Var;
  ButtonToggle(float nx, float ny, float nw, float nh, String nCall, String nVar, String nText) {
    x=nx;
    y=ny;
    w=nw;
    h=nh;
    Call=nCall;
    Var=nVar;
    Text=nText;
  }
  void Draw(int i) {
    textAlign(CENTER, CENTER);
    stroke(#676767);
    if (Configs.get(Var)==1) {
      fill(#004000);
    } else {
      fill(#400000);
    }
    rect(x, y, w, h);
    fill(#BFBFBF);
    text(Text, x+w/2, y+h/2);
  }
  void Func(int i) {
    Configs.set(Var, abs(Configs.get(Var)-1));
  }
}

class BindButton extends UI {
  String Text;
  String Code;
  int Connected;
  boolean Waiting;
  BindButton(float nx, float ny, String nCall, String nCode, String nText) {
    x=nx;
    y=ny;
    w=150;
    h=40;
    Call=nCall;
    Text=nText;
    Code = nCode;
    Connected = KeyRedirect.get(Code);
  }
  void Draw(int i) {
    textAlign(LEFT, TOP);
    stroke(#676767);
    if (!(WaitingUser && Waiter == i)) {
      fill(#404040);
    } else {
      fill(#808080);
    }
    rect(x, y, w, h);
    fill(#BFBFBF);
    text(Text, x+2, y+2);
    if (Text.isEmpty()) {
      text(Code, x+2, y+2);
    }
    if (Keybinds[Connected].iscode) {
      text(returnTEXE(Keybinds[Connected].code), x+2, y+22);
    } else {
      text(Keybinds[Connected].pri, x+2, y+22);
    }
  }
  void Func(int i) {
    WaitingUser=true;
    Waiter=i;
    ToWait=Connected;
  }
}

class Text extends UI {
  String Text;
  Text(float nx, float ny, float nw, float nh, String nCall, String nRun, String nText) {
    x=nx;
    y=ny;
    w=nw;
    h=nh;
    Call=nCall;
    Run=nRun;
    Text=nText;
  }
  void Draw(int i) {
    textAlign(LEFT, TOP);
    stroke(#676767);
    fill(#000000);
    rect(x, y, w, h);
    fill(#BFBFBF);
    text(Text, x+2, y+2, w-4, 200);
  }
}

class ButtonText extends UI {
  String Text;
  ButtonText(float nx, float ny, float nw, float nh, String nCall, String nRun, String nText) {
    x=nx;
    y=ny;
    w=nw;
    h=nh;
    Call=nCall;
    Run=nRun;
    Text=nText;
  }
  void Draw(int i) {
    textAlign(CENTER, CENTER);
    stroke(#676767);
    fill(#404040);
    rect(x, y, w, h);
    fill(#BFBFBF);
    text(Text, x+w/2, y+h/2);
  }
}

class UI {
  float x;
  float y;
  float w;
  float h;
  String Call;
  String Run;
  boolean Enable;
  void turnOff(String Voice) {
    if (Voice.equals(Call)) {
      Enable=false;
    }
  }
  void turnON(String Voice) {
    if (Voice.equals(Call)) {
      Enable=true;
    }
  }
  void turnOffBut(String Voice) {
    if (!Voice.equals(Call)) {
      Enable=false;
    }
  }
  void turnONBut(String Voice) {
    if (!Voice.equals(Call)) {
      Enable=true;
    }
  }
  void Func(int i) {
    if (MenuSwap) {
      return;
    }
    try {
      method(Run);
    }
    catch(Exception e) {
      PrintCon("sorry for that");
      PrintCon(e.getMessage());
      ErrorTimer=120;
    }
  }
  void CFunc(int i) {
    //much
  }
  void Draw(int i) {
    //wow
  }
}

void MenuTurnOn(String str) {
  for (int i=0; i<menuUI.length; i++) {
    menuUI[i].turnON(str);
  }
}

void MenuTurnOff(String str) {
  for (int i=0; i<menuUI.length; i++) {
    menuUI[i].turnOff(str);
  }
}

void MenuTurnOnAll() {//dont ever fucking use!
  for (int i=0; i<menuUI.length; i++) {
    menuUI[i].Enable=true;
  }
}

void MenuTurnOffAll() {//turn Off
  for (int i=0; i<menuUI.length; i++) {
    menuUI[i].Enable=false;
  }
}

void MenuTurnOnAllBut(String str) {//also dont use!
  for (int i=0; i<menuUI.length; i++) {
    menuUI[i].turnONBut(str);
  }
}

void MenuTurnOffAllBut(String str) {//turn Off all but str
  for (int i=0; i<menuUI.length; i++) {
    menuUI[i].turnOffBut(str);
  }
}

void mousePressed() {
  if (!Gaming || MenuPaused) {
    for (int i=0; i<menuUI.length; i++) {
      if (menuUI[i].Enable) {//ouch
        if (mouseX>menuUI[i].x
          &&mouseX<menuUI[i].x+menuUI[i].w
          &&mouseY>menuUI[i].y
          &&mouseY<menuUI[i].y+menuUI[i].h) {
          menuUI[i].Func(i);
        }
      }
    }
  }
}
