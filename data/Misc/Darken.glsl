uniform float X[128];
uniform float Y[128];
uniform bool Active[128];
uniform float Power[128];
uniform float OffX;
uniform float OffY;
uniform float P;
uniform float Width;
uniform float Height;
uniform float Zoom;

void main()
{
	float D = 99999999;
	for(int l=0;l<128;l++){
		if(Active[l]==true){
			D = min(sqrt(pow((gl_FragCoord.x-X[l]*Zoom+OffX),2)+pow(((Height-gl_FragCoord.y)-Y[l]*Zoom+OffY),2))*(P/Power[l]/(pow(Zoom,0.1))),D);
		}
	}
	vec2 p;
	gl_FragColor = vec4 (vec3(0.0,0.0,0.0) , (min(D,1)));
}
