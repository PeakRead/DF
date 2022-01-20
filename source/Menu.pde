void Menu() {
  MATHUI();
  textAlign(LEFT, TOP);
}

void MenuSetup() {
  menuUI = new UI[0];

  //PAUSE_MENU

  menuUI = (UI[])append(menuUI, new Text      (20, 50,-1,-1, 100, 40, "PAUSE_MENU", "nothing", "PAUSED"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, -110-50,-1,1, 100, 40, "PAUSE_MENU", "restart", "restart"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, -110,-1,1, 100, 40, "PAUSE_MENU", "GotoOptions", "options"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, -110+50,-1,1, 100, 40, "PAUSE_MENU", "Exit", "Exit the thing"));
  menuUI = (UI[])append(menuUI, new Slot      (-210, 50,1,-1, 200, 20, "PAUSE_MENU", 0));
  menuUI = (UI[])append(menuUI, new Slot      (-210, 70,1,-1, 200, 20, "PAUSE_MENU", 1));
  menuUI = (UI[])append(menuUI, new Slot      (-210, 90,1,-1, 200, 20, "PAUSE_MENU", 2));
  menuUI = (UI[])append(menuUI, new Text      (-310, 50,1,-1, 100, 60, "PAUSE_MENU", "nothing", "testing this things"));

  //MAIN_MENU

  menuUI = (UI[])append(menuUI, new ButtonText(20, -160,-1,1, 100, 40, "MAIN_MENU", "RunGame", "go to the arena"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, -160-50,-1,1, 100, 40, "MAIN_MENU", "GotoTutorial", "tutorial"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, -160+50,-1,1, 100, 40, "MAIN_MENU", "GotoOptions", "options"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, -160+100,-1,1, 100, 40, "MAIN_MENU", "Quit", "Quit"));
  menuUI = (UI[])append(menuUI, new ButtonText(140, -160+100,-1,1, 100, 40, "MAIN_MENU", "UPDATE", "Update"));
  menuUI = (UI[])append(menuUI, new Image     (0,75,0,-1, 100, 40, "MAIN_MENU", "UPDATE", "Misc/Title.png"));
  
  //saves//
  
  menuUI = (UI[])append(menuUI, new SaveButton(140, -160,-1,1, 100, 40, "MAIN_MENU", "nothing" , 10));
  menuUI = (UI[])append(menuUI, new SaveButton(260, -160,-1,1, 100, 40, "MAIN_MENU", "nothing" , 20));
  menuUI = (UI[])append(menuUI, new SaveButton(320, -180,-1,1, 100, 10, "MAIN_MENU", "nothing" , 21));
  menuUI = (UI[])append(menuUI, new SaveButton(380, -160,-1,1, 100, 40, "MAIN_MENU", "nothing" , 30));
  menuUI = (UI[])append(menuUI, new SaveButton(500, -160,-1,1, 100, 40, "MAIN_MENU", "nothing" , 40));
  menuUI = (UI[])append(menuUI, new SaveButton(560, -180,-1,1, 100, 10, "MAIN_MENU", "nothing" , 41));
  menuUI = (UI[])append(menuUI, new SaveButton(620, -160,-1,1, 100, 40, "MAIN_MENU", "nothing" , 50));
  menuUI = (UI[])append(menuUI, new SaveButton(740, -160,-1,1, 100, 40, "MAIN_MENU", "nothing" , 60));
  
  //OPTIONS_MENU

  menuUI = (UI[])append(menuUI, new ButtonText(20, -110+50,-1,1, 100, 40, "OPTIONS_MENU", "GotoMain", "back"));
  menuUI = (UI[])append(menuUI, new Text      (20, 50,-1,-1, 100, 40, "OPTIONS_MENU", "nothing", "this is the options menu"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, 90,-1,-1, 100, 40, "OPTIONS_MENU", "GotoBinds", "KeyBinds"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, 130,-1,-1, 100, 40, "OPTIONS_MENU", "GotoNots", "Visuals"));
  menuUI = (UI[])append(menuUI, new ButtonText(20, 170,-1,-1, 100, 40, "OPTIONS_MENU", "Save_config", "save"));
  
  //Binds

  menuUI = (UI[])append(menuUI, new BindButton(140, 50,-1,-1, "Binds", "Player_Move_Up", "jump"));
  menuUI = (UI[])append(menuUI, new BindButton(140, 90,-1,-1, "Binds", "Player_Move_Down", ""));
  menuUI = (UI[])append(menuUI, new BindButton(140, 130,-1,-1, "Binds", "Player_Move_Left", "slide to the left"));
  menuUI = (UI[])append(menuUI, new BindButton(140, 170,-1,-1, "Binds", "Player_Move_Right", "slide to the right"));
  menuUI = (UI[])append(menuUI, new BindButton(140, 210,-1,-1, "Binds", "Player_Restart", "quick restart"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 50,-1,-1, "Binds", "Player_Boost", "special"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 90,-1,-1, "Binds", "Weapon_1", "slot garant"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 130,-1,-1, "Binds", "Weapon_2", "slot rocket"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 170,-1,-1, "Binds", "Weapon_3", "slot railgun"));
  menuUI = (UI[])append(menuUI, new BindButton(290, 210,-1,-1, "Binds", "Weapon_4", "slot shotgun"));
  
  //Nots

  menuUI = (UI[])append(menuUI, new ButtonToggle(140, 50,-1,-1, 400, 40, "Nots", "DrawEffects", "DrawEffects : Only important Effect will display"));
  menuUI = (UI[])append(menuUI, new ButtonSlider(140, 90,-1,-1, 400, 40, "Nots", "GuiScale", "GuiScale : Scale of the Hud Rings", 50, 500));
  menuUI = (UI[])append(menuUI, new ButtonToggle(140, 130,-1,-1, 400, 40, "Nots", "ReversedWhell", "ReversedWhell : reverse the weapon whell seletion"));
  menuUI = (UI[])append(menuUI, new ButtonSlider(140, 170,-1,-1, 400, 40, "Nots", "HurtScale", "HurtScale : scale the \"vinete\" that apears", 1, 50));
  menuUI = (UI[])append(menuUI, new ButtonToggle(140, 210,-1,-1, 400, 40, "Nots", "ShouldPause", "ShouldPause : autopause if unfocused"));
  menuUI = (UI[])append(menuUI, new ButtonToggle(140, 250,-1,-1, 400, 40, "Nots", "SimpleExplosion", "SimpleExplosion : makes explosions simpler goodFps boost for them"));
  //menuUI = (UI[])append(menuUI, new ButtonToggle(140,290, 400, 40, "Nots", "Fullscreen", "Fullscreen : Fullscreen"));
  menuUI = (UI[])append(menuUI, new ButtonSlider(140, 290,-1,-1, 400, 40, "Nots", "Zoom", "Zoom : How much zoom", 10, 200));

  MenuTurnOn("MAIN_MENU");
}

void GotoTutorial() {
  Start("tutorial");
  toturialTimer = 0;
  toturialMode = true;
}

void nothing() {/*!nothing!*/}

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
        &&mouseX>menuUI[i].x+menuUI[i].getAliningW()
        &&mouseX<menuUI[i].x+menuUI[i].getAliningW()+menuUI[i].w
        &&mouseY>menuUI[i].y+menuUI[i].getAliningH()
        &&mouseY<menuUI[i].y+menuUI[i].getAliningH()+menuUI[i].h) {
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
  toturialMode=false;
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
  out.setBoolean("Debugdraw", tmp.getBoolean("Debugdraw"));
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
  //println("test");
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
  SaveButton(float nx, float ny,int AllingW,int AllingH, float nw, float nh, String nCall,String nRun,int save) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
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
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    rect(0, 0, w, h);
    fill(#BFBFBF);
    text("\"level\" "+(save), w/2, h/2);
    popMatrix();
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
  Slot(float nx, float ny,int AllingW,int AllingH, float nw, float nh, String nCall, int nwhich) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
    w=nw;
    h=nh;
    Call=nCall;
    which=nwhich;
  }
  void Draw(int i) {
    textAlign(CENTER, CENTER);
    stroke(#676767);
    fill(#808080);
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    rect(0,0, w , h);
    rect(0,0, 50, h);
    rect(w-50, 0, 50, h);
    line(50, 0, 0, h/2);
    line(50, h, 0, h/2);
    line(w-50, 0, w, h/2);
    line(w-50, h, w, h/2);
    fill(#000000);
    if (which==0) {
      text(slot1[play.vertical], w/2, h/2);
    }
    if (which==1) {
      text(slot2[play.mobilaty], w/2, h/2);
    }
    if (which==2) {
      text(slot3[play.regenera], w/2, h/2);
    }
    if (mouseX>x+50+getAliningW() && mouseX<x+w-50+getAliningW() && mouseY>y+getAliningH() && mouseY<y+h+getAliningH()) {
      fill(#808080);
      rect(50, 0, w-100, h);
      fill(#000000);
      if (which==0) {
        text(desc1[play.vertical], w/2, h/2);
      }
      if (which==1) {
        text(desc2[play.mobilaty], w/2, h/2);
      }
      if (which==2) {
        text(desc3[play.regenera], w/2, h/2);
      }
    }
    popMatrix();
  }
  void CFunc(int i) {
    int u=0;
    if (mouseX<x+50+getAliningW()) {
      u--;
    }
    if (mouseX>x+w-50+getAliningW()) {
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
  ButtonSlider(float nx, float ny,int AllingW,int AllingH, float nw, float nh, String nCall, String nVar, String nText, int Min, int Max) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
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
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    rect(0, 0, w, h);
    fill(#404040);
    rect(0, 0, map(Configs.get(Var), min, max, 0, w), h);
    fill(#BFBFBF);
    text(Text, 0+w/2, 0+h/2-5);
    text(Configs.get(Var), 0+w/2, 0+h/2+5);
    popMatrix();
  }
  void CFunc(int i) {
    Configs.set(Var, constrain((int)map(mouseX, x+getAliningW()+5, x+getAliningW()+w-5, min, max), min, max));
  }
}

class ButtonToggle extends UI {
  String Text;
  String Var;
  ButtonToggle(float nx, float ny,int AllingW,int AllingH, float nw, float nh, String nCall, String nVar, String nText) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
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
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    rect(0, 0, w, h);
    fill(#BFBFBF);
    text(Text, w/2, h/2);
    popMatrix();
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
  BindButton(float nx, float ny,int AllingW,int AllingH, String nCall, String nCode, String nText) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
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
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    rect(0, 0, w, h);
    fill(#BFBFBF);
    text(Text,2,2);
    if (Text.isEmpty()) {
      text(Code,2,2);
    }
    if (Keybinds[Connected].iscode) {
      text(returnTEXE(Keybinds[Connected].code),2,22);
    } else {
      text(Keybinds[Connected].pri,2,22);
    }
    popMatrix();
  }
  void Func(int i) {
    WaitingUser=true;
    Waiter=i;
    ToWait=Connected;
  }
}

class Text extends UI {
  String Text;
  Text(float nx, float ny,int AllingW,int AllingH, float nw, float nh, String nCall, String nRun, String nText) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
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
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    rect(0, 0, w, h);
    fill(#BFBFBF);
    text(Text, 2, 2, w-4, 200);
    popMatrix();
  }
}

class Image extends UI {
  PImage img;
  Image(float nx, float ny,int AllingW,int AllingH, float nw, float nh, String nCall, String nRun, String ImgPath) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
    w=nw;
    h=nh;
    Call=nCall;
    Run=nRun;
    img=SloadImage(ImgPath);
  }
  void Draw(int i) {
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    image(img, -img.width/2, -img.height/2);
    popMatrix();
  }
}

class ButtonText extends UI {
  String Text;
  ButtonText(float nx, float ny,int AllingW,int AllingH, float nw, float nh, String nCall, String nRun, String nText) {
    x=nx;
    y=ny;
    this.AllingW=AllingW;
    this.AllingH=AllingH;
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
    pushMatrix();
    translate(x+getAliningW(),y+getAliningH());
    rect(0, 0, w, h);
    fill(#BFBFBF);
    text(Text, w/2, h/2);
    popMatrix();
  }
}

class UI {
  float x;
  float y;
  float w;
  float h;
  int AllingW;
  int AllingH;
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
      PrintCon(e.getMessage()+"");
      ErrorTimer=120;
    }
  }
  void CFunc(int i) {
    //much
  }
  void Draw(int i) {
    //wow
  }
  int getAliningW(){
    if(AllingW==-1){
      return 0;
    }else if(AllingW==0){
      return width/2;
    }else{
      return width;
    }
  }
  int getAliningH(){
    if(AllingH==-1){
      return 0;
    }else if(AllingH==0){
      return height/2;
    }else{
      return height;
    }
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
        if (mouseX>menuUI[i].x+menuUI[i].getAliningW()
          &&mouseX<menuUI[i].x+menuUI[i].getAliningW()+menuUI[i].w
          &&mouseY>menuUI[i].y+menuUI[i].getAliningH()
          &&mouseY<menuUI[i].y+menuUI[i].getAliningH()+menuUI[i].h) {
          menuUI[i].Func(i);
        }
      }
    }
  }
}
