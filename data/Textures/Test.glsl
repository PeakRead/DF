precision mediump float;

uniform float WIDTH;
uniform float HEIGHT;
//uniform vec2 u_resolution;
uniform float OFFX;
uniform float OFFY;
uniform float SIZE;

float PI = 3.1413;
float Dsize = 30.0;
float Rsize = PI/45.0;

float dist(float x1,float y1,float x2,float y2){
	return sqrt(pow(x1-x2,2.0)+pow(y1-y2,2.0));
}

bool Close(float Num,float Target,float Delta){
  return Num<Target+Delta && Num>Target-Delta;
}

void main()
{
  float D = dist(gl_FragCoord.x-OFFX , gl_FragCoord.y+OFFY , 0.0 , HEIGHT) / SIZE * 0.5;
  float R = atan(gl_FragCoord.y+OFFY-HEIGHT,gl_FragCoord.x-OFFX);
  if(mod(D/2.0,Dsize)<15.0){
    R+=PI/45.0/2.0;
	  //gl_FragColor = vec4 (vec3(0.0) , 1.0);
    //return;
  }
  vec3 FCol=vec3(0.0, 0.0, 0.0);
  if(mod(D,Dsize)<2.0 * SIZE || Close(mod(R,Rsize),0.0,0.002 * SIZE)){
    FCol=vec3(0.5725, 0.5725, 0.5725);
  }else{
    //FCol=vec3(0.1882, 0.1882, 0.1882);
    float x=floor(R/Rsize)*Rsize;
    float y=floor(D/Dsize)*Dsize;
    float lerp = mod((y*0.01+x),1.0);
    FCol=vec3(0.2667, 0.2667, 0.2667)*lerp + vec3(0.5725, 0.6157, 0.6118)*(1.0-lerp);
  }
	gl_FragColor = vec4 (FCol , 1.0);
}