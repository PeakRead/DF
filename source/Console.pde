//weewoo wagon

String[] Console = new String[100];
boolean ConsoleUP = false;
String ConsoleInput = "";
int scrool=0;

void resetCon(){
  for(int i=0;i<Console.length;i++){
    Console[i] = "";
  }
}

void PrintCon(String text){
  for(int i=Console.length-2;i>=0;i--){
    Console[i+1] = Console[i]; 
  }
  Console[0]=text;
}

void DrawConsole(){
  noStroke();
  fill(#00FF00,200);
  rect(0,0,width,20*15);
  fill(100);
  rect(width-10,float(scrool)/80*20*13,10,20*2);
  fill(#FF00FF);
  for(int i=scrool;i<20+scrool;i++){
    try{
    text(Console[i],0,19*15-15*i+scrool*15);
    }catch(Exception e){/*ignore*/}
  }
  fill(#FFFF00,200);
  rect(0,20*15,width,15);
  fill(#0000FF);
  text(ConsoleInput,0,20*15);
}

String[] Confunc = {
"help : displays this",
"resurect : resurects the player if dead",
"noclip : noclip",
"map : load a map",
"maps : displays all files in the maps folder",
"clean : cleans the console",
"cum : cast a 500dmg lighting call on self",
"debugdraw : toggle the debugdrawing of stuff",
"god : invunrabylaty",
"notime : prevents the players HP from going below 0",
"phys : toggle the physics(player included)",
"restart : reset the player",
"error : fake a error",
"text : print shit to screen"};

void runConinput(){
  String[] args = split(ConsoleInput,' ');
  ConsoleInput="";
  switch(args[0]){
    case "":
      if(random(0,100)<5){
        PrintCon("nothing!");
      }else{
        PrintCon("]");
      }
      break;
      case "help":
      for(int i=0;i<Confunc.length;i++){
        PrintCon(Confunc[i]);
      }
    break;
    case "resurect":
      if(!Gaming){PrintCon("not gaming");break;}
      if(play.HP<=0){
        play.HP=100;
        PrintCon("get up!");
      }else{
        PrintCon("your not dead dumbass");
      }
    break;
    case "noclip":
      if(!Gaming){PrintCon("not gaming");break;}
      if(!play.Noclip){
        PrintCon("noclip on");
        play.Noclip=true;
      }else{
        PrintCon("noclip off");
        play.Noclip=false;
      }
    break;
    case "map":
      if(Gaming){
        if(args.length<2){
          PrintCon("expected more arguments");
          break;
        }
        PrintCon("loading Map:"+args[1]);
        openMap(args[1]);
        play.MT();
      }else{
        if(args.length<2){
          PrintCon("expected more arguments");
          break;
        }
        Start(args[1]);
      }
    break;
    case "clean":
      resetCon();
    break;
    case "uwu":
      PrintCon("uwu");
    break;
    case "cum":
      if(!Gaming){PrintCon("not gaming");break;}
      AddPartic(1,play.X,play.Y,play.X,-10000,60,#FFFFFF,true);
      for(int ohno=0;ohno<50;ohno++){
        AddPartic(2,play.X,play.Y,random(-10,10),random(-10,10),60,#FFFFFF,true);
      }
      expd(play.X, play.Y+8, 256, 500, 500, true);
      PrintCon("orh!");
    break;
    case "debugdraw":
      DebugDraw=!DebugDraw;
      PrintCon(DebugDraw+"");
    break;
    case "god":
      play.God=!play.God;
      PrintCon("god is now " + play.God);
    break;
    case "notime":
      play.notime=!play.notime;
      PrintCon("time to die? " + play.notime);
    break;
    case "tmp":
      NewAI(play.X-32,play.Y-128,"Maze_Boss",false);
      NewAI(play.X+32,play.Y-128,"Laze_Boss",false);
    break;
    case "phys":
      RunPhys=!RunPhys;
    break;
    case "maps":
      File pointer = new File(sketchPath()+"/data/Maps");
      String[] name = pointer.list();
      if(name.length==0){return;}
      for(int i=0;i<name.length;i++){
        PrintCon(name[i]);
      }
    break;
    case "nextwave":
      nextWave();
    break;
    case "gotowave":
      if(args.length<2){
        PrintCon("expected more arguments");
        break;
      }
      round=int(args[1])-1;
      nextWave();
    break;
    case "restart":
      play.MT();
    break;
    case "error":
      ErrorTimer=120;
    break;
    case "tantsumon":
      if(args.length<2){
        PrintCon("expected more arguments");
        break;
      }
      arenaSpawn(args[1]);
    break;
    case "text":
      if(args.length<2){
        PrintCon("expected more arguments");
        break;
      }
      String text="";
      for(int i=1;i<args.length;i++){
        text += args[i];
        if(i!=args.length-1){text+=' ';}
      }
      texttoscren(text);
    break;
    default:
      PrintCon("what?");
    break;
  }
}

void texttoscren(String text){
  TextString=text;
  TextCurr=0;
  TextShow=true;
}
