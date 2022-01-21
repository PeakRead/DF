void loadPacks(){
  String[] Enabled = loadStrings(sketchPath()+"/data/Packs/Enabled.txt");
  Packs = new pack[0];
  File PackFolder = new File(sketchPath()+"\\Data\\Packs");
  File [] Packstoload = PackFolder.listFiles();
  for(int i=0;i<Packstoload.length;i++){
    File Pack = Packstoload[i];
    if(!Pack.isFile()){
      JSONObject Data = loadJSONObject(Pack.getPath()+"/Data.json");
      String name;
      String desc;
      String author;
      if(Data!=null){
        name = Data.getString("name","unknown");
        desc = Data.getString("desc","missing");
        author = Data.getString("author","someone");
      }else{
        name="unknown";
        desc="missing";
        author="someone";
      }
      Packs = (pack[])append(Packs,new pack(name,Pack.getName(),desc,author,Pack.getPath()));
      for(int u=0;u<Enabled.length;u++){
        if(Enabled[u].equals(Pack.getName())){
          Packs[Packs.length-1].Enabled=true;
        }
      }
    }
  }
}

pack[] Packs;

class pack{
  String Name;
  String Desc;
  String Author;
  String internalName;
  String Path;
  boolean Enabled=false;
  pack(String Name,String intername,String Desc,String Author,String Path){
    this.Name = Name;
    this.Desc = Desc;
    this.Author= Author;
    this.internalName=intername;
    this.Path=Path;
  }
}
