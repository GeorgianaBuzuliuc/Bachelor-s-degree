import java.io.*;
import java.net.*;

public class DEM{
	public static String S = "n043e020 n043e021 n043e022 n043e023 n043e024 n043e025 n043e026 n043e027 n043e028";
	public static String N = "n048e020 n048e021 n048e022 n048e023 n048e024 n048e025 n048e026 n048e027 n048e028 n048e029";
	public static String W = "n043e020 n044e020 n045e020 n046e020 n047e020 n048e020";
	public static String E = "n043e028 n044e029 n045e029 n046e029 n047e029 n048e029";	

	public String name="";
	//public double minlon, maxlon, minlat, maxlat;
	public int minlon, maxlon, minlat, maxlat;
	RandomAccessFile raf;
	
	int DEM_type = 1;
	public static String dem_directory = "dem30"; 				// "dem30",  "dem90",  "dem90_L";
	
	//DEM resolution = 3" arc: grid = 1201, cellsize = 0.000833333333
	//DEM resolution = 1" arc: grid = 3600, cellsize = 0.00027777777777778
	
	public static int grid = dem_directory.startsWith("dem30") ? 3600 : 1200;
	public static double cellsize = 1./grid;
	
	int contor;				//debug contor
	
	
	public void selectDEM(int i){
		switch(i){
			case 0:
				dem_directory = "dem30";
				grid = 3600;
				break;
			case 1:
				dem_directory = "dem90";
				grid = 1200;
				break;
			case 2:
				dem_directory = "dem90_L";
				grid = 1200;
				break;				
		}
		cellsize = 1./grid;
		try{
			if(raf!=null)raf.close();
			raf = new RandomAccessFile(dem_directory+"/"+name+".dem","r");}
		catch(IOException e){}		
	}

	
	public DEM(String name){
		selectDEM(DEM_type);
		this.name = name.toLowerCase();
		Parse parse = getData(name, "n", "e");
		try{
			minlat = Integer.parseInt(parse.left.substring(1));
			minlon = Integer.parseInt(parse.right.substring(1));	
			maxlat = minlat + 1;
			maxlon = minlon + 1;
		}
		catch(NumberFormatException e){}
	}
	
static Parse getData(String s, String start, String end){	
	int index = s.indexOf(start);
	s=s.substring(index+1);
	index = s.indexOf(end);
	if(index==-1)return null;
	String s1=s.substring(0,index);
	s=s.substring(index+1);
	return new Parse(s1,s);
}	

public void close(){
	try{if(raf!=null)raf.close();}
	catch(IOException e){}
}


public double getElevation(double longitude, double latitude){
	double fail = -10000;
	selectDEM(DEM_type);
	if(raf!=null && minlon<=longitude && longitude<maxlon && minlat<=latitude && latitude<maxlat){
		double dlon = (longitude-minlon)*grid;
		double dlat = (latitude-minlat)*grid;
		int x = (int)(dlon+0.0000000001);
		int y = (int)(dlat+0.0000000001);	
		
		int[] ix = new int[4], iy = new int[4];
		double[] X = new double[4], Y = new double[4];
		double[][] H = new double[4][4];
		
		ix[0]=x-1; ix[1]=x; ix[2]=x+1; ix[3]=x+2;
		iy[0]=y+2; iy[1]=y+1; iy[2]=y; iy[3]=y-1;	
		for(int i=0; i<4; i++) X[i] = (double)ix[i];
		for(int j=0; j<4; j++) Y[j] = (double)iy[j];		
		
		//interior
		if(0 < x && x < grid-1 && 0 < y && y < grid-1){
			for(int i=0; i<4; i++){
				for(int j=0; j<4; j++){
					try{
						int pointer = 2*(grid+1)*(grid-iy[j])+2*ix[i];
						raf.seek(pointer);
						H[i][j] = (double)raf.readShort();
					}
					catch(Exception e){return fail;}	
				}			
			}
			BiCubicSpline bcs =new BiCubicSpline(X, Y, H);
			double elevation = bcs.interpolate(dlon, dlat);	
			//informatii_punct(longitude, latitude, elevation, x, y, H);
			return elevation;
		}
		
		//latura W
		if(x == 0 && 0 < y && y < grid-1){
			if(W.indexOf(name)>-1) return fail;
			for(int i=1; i<4; i++){
				for(int j=0; j<4; j++){
					try{
						int pointer = 2*(grid+1)*(grid-iy[j])+2*ix[i];
						raf.seek(pointer);
						H[i][j] = (double)raf.readShort();
					}
					catch(Exception e){return fail;}	
				}			
			}		
			RandomAccessFile RAF = null;
			try{
				RAF = new RandomAccessFile(dem_directory+"/n0"+(int)minlat+"e0"+(int)(minlon-1)+".dem","r");			
				for(int j=0; j<4; j++){
					int pointer = 2*(grid+1)*(grid-iy[j])+2*grid-2;
					RAF.seek(pointer);
					H[0][j] = (double)RAF.readShort();
				}	
				RAF.close();
			}catch(Exception e){return fail;}
			BiCubicSpline bcs =new BiCubicSpline(X, Y, H);
			double elevation = bcs.interpolate(dlon, dlat);	
			//informatii_punct(longitude, latitude, elevation, x, y, H);
			return elevation;			
		}
		
		//colt SW
		if(x == 0 && y == 0){
			if(W.indexOf(name)>-1 || S.indexOf(name)>-1) return fail;
			for(int i=1; i<4; i++){
				for(int j=0; j<3; j++){
					try{
						int pointer = 2*(grid+1)*(grid-iy[j])+2*ix[i];
						raf.seek(pointer);
						H[i][j] = (double)raf.readShort();
					}
					catch(Exception e){return fail;}	
				}			
			}		
			RandomAccessFile RAF = null;
			try{
				RAF = new RandomAccessFile(dem_directory+"/n0"+(int)minlat+"e0"+(int)(minlon-1)+".dem","r");			
				for(int j=0; j<3; j++){
					int pointer = 2*(grid+1)*(grid-iy[j])+2*grid-2;
					RAF.seek(pointer);
					H[0][j] = (double)RAF.readShort();
				}	
				RAF.close();
			}catch(Exception e){return fail;}
			try{
				RAF = new RandomAccessFile(dem_directory+"/n0"+(int)(minlat-1)+"e0"+(int)minlon+".dem","r");			
				for(int i=1; i<4; i++){
					int pointer = 2*(grid+1)+2*ix[i];
					RAF.seek(pointer);
					H[i][3] = (double)RAF.readShort();
				}	
				RAF.close();
			}catch(Exception e){return fail;}			
			try{
				RAF = new RandomAccessFile(dem_directory+"/n0"+(int)(minlat-1)+"e0"+(int)(minlon-1)+".dem","r");			
				int pointer =  2*(grid+1)+2*grid-2;
				RAF.seek(pointer);
				H[0][3] = (double)RAF.readShort();
				RAF.close();
			}catch(Exception e){return fail;}			
			BiCubicSpline bcs =new BiCubicSpline(X, Y, H);
			double elevation = bcs.interpolate(dlon, dlat);	
			//informatii_punct(longitude, latitude, elevation, x, y, H);
			return elevation;			
		}		

		//colt NW
		if(x == 0 && y == grid-1){
			if(W.indexOf(name)>-1 || N.indexOf(name)>-1) return fail;
			for(int i=1; i<4; i++){
				for(int j=1; j<4; j++){
					try{
						int pointer = 2*(grid+1)*(grid-iy[j])+2*ix[i];
						raf.seek(pointer);
						H[i][j] = (double)raf.readShort();
					}
					catch(Exception e){return fail;}	
				}			
			}		
			RandomAccessFile RAF = null;
			try{
				RAF = new RandomAccessFile(dem_directory+"/n0"+(int)minlat+"e0"+(int)(minlon-1)+".dem","r");			
				for(int j=1; j<4; j++){
					int pointer = 2*(grid+1)*(grid-iy[j])+2*grid-2;
					RAF.seek(pointer);
					H[0][j] = (double)RAF.readShort();
				}	
				RAF.close();
			}catch(Exception e){return fail;}
			try{
				RAF = new RandomAccessFile(dem_directory+"/n0"+(int)(minlat+1)+"e0"+(int)minlon+".dem","r");			
				for(int i=1; i<4; i++){
					int pointer = 2*(grid+1)*(grid-1)+2*ix[i];
					RAF.seek(pointer);
					H[i][0] = (double)RAF.readShort();
				}	
				RAF.close();
			}catch(Exception e){return fail;}			
			try{
				RAF = new RandomAccessFile(dem_directory+"/n0"+(int)(minlat+1)+"e0"+(int)(minlon-1)+".dem","r");			
				int pointer =  2*(grid+1)*(grid-1)+2*grid-2;;
				RAF.seek(pointer);
				H[0][0] = (double)RAF.readShort();
				RAF.close();
			}catch(Exception e){return fail;}			
			BiCubicSpline bcs =new BiCubicSpline(X, Y, H);
			double elevation = bcs.interpolate(dlon, dlat);	
			//informatii_punct(longitude, latitude, elevation, x, y, H);
			return elevation;			
		}		

		//latura S
		if(0 < x && x < grid-1 && y == 0){
			if(S.indexOf(name)>-1) return fail;
			for(int i=0; i<4; i++){
				for(int j=0; j<3; j++){
					try{
						int pointer = 2*(grid+1)*(grid-iy[j])+2*ix[i];
						raf.seek(pointer);
						H[i][j] = (double)raf.readShort();
					}
					catch(Exception e){return fail;}	
				}			
			}		
			RandomAccessFile RAF = null;
			try{
				RAF = new RandomAccessFile(dem_directory+"/n0"+(int)(minlat-1)+"e0"+(int)minlon+".dem","r");			
				for(int i=0; i<4; i++){
					int pointer = 2*(grid+1)+2*ix[i];
					RAF.seek(pointer);
					H[i][3] = (double)RAF.readShort();
				}	
				RAF.close();
			}catch(Exception e){return fail;}	
			BiCubicSpline bcs =new BiCubicSpline(X, Y, H);
			double elevation = bcs.interpolate(dlon, dlat);	
			//informatii_punct(longitude, latitude, elevation, x, y, H);
			return elevation;			
		}	

		//latura N
		if(0 < x && x < grid-1 && y == grid-1){
			if(N.indexOf(name)>-1) return fail;
			for(int i=0; i<4; i++){
				for(int j=1; j<4; j++){
					try{
						int pointer = 2*(grid+1)*(grid-iy[j])+2*ix[i];
						raf.seek(pointer);
						H[i][j] = (double)raf.readShort();
					}
					catch(Exception e){return fail;}	
				}			
			}		
			RandomAccessFile RAF = null;
			try{
				RAF = new RandomAccessFile(dem_directory+"/n0"+(int)(minlat+1)+"e0"+(int)minlon+".dem","r");			
				for(int i=0; i<4; i++){
					int pointer = 2*(grid+1)*(grid-1)+2*ix[i];
					RAF.seek(pointer);
					H[i][0] = (double)RAF.readShort();
				}	
				RAF.close();
			}catch(Exception e){return fail;}	
			BiCubicSpline bcs =new BiCubicSpline(X, Y, H);
			double elevation = bcs.interpolate(dlon, dlat);	
			//informatii_punct(longitude, latitude, elevation, x, y, H);
			return elevation;			
		}	

		//latura E
		if(x == grid-1 && 0 < y && y < grid-1){
			if(E.indexOf(name)>-1) return fail;
			for(int i=0; i<3; i++){
				for(int j=0; j<4; j++){
					try{
						int pointer = 2*(grid+1)*(grid-iy[j])+2*ix[i];
						raf.seek(pointer);
						H[i][j] = (double)raf.readShort();
					}
					catch(Exception e){return fail;}	
				}			
			}		
			RandomAccessFile RAF = null;
			try{
				RAF = new RandomAccessFile(dem_directory+"/n0"+(int)minlat+"e0"+(int)(minlon+1)+".dem","r");			
				for(int j=0; j<4; j++){
					int pointer = 2*(grid+1)*(grid-iy[j])+2;
					RAF.seek(pointer);
					H[3][j] = (double)RAF.readShort();
				}	
				RAF.close();
			}catch(Exception e){return fail;}
			BiCubicSpline bcs =new BiCubicSpline(X, Y, H);
			double elevation = bcs.interpolate(dlon, dlat);	
			//informatii_punct(longitude, latitude, elevation, x, y, H);
			return elevation;			
		}	

		//colt NE
		if(x == grid-1 && y == grid-1){
			if(E.indexOf(name)>-1 || N.indexOf(name)>-1) return fail;
			for(int i=0; i<3; i++){
				for(int j=1; j<4; j++){
					try{
						int pointer = 2*(grid+1)*(grid-iy[j])+2*ix[i];
						raf.seek(pointer);
						H[i][j] = (double)raf.readShort();
					}
					catch(Exception e){return fail;}	
				}			
			}		
			RandomAccessFile RAF = null;
			try{
				RAF = new RandomAccessFile(dem_directory+"/n0"+(int)minlat+"e0"+(int)(minlon+1)+".dem","r");			
				for(int j=1; j<4; j++){
					int pointer = 2*(grid+1)*(grid-iy[j])+2;
					RAF.seek(pointer);
					H[3][j] = (double)RAF.readShort();
				}	
				RAF.close();
			}catch(Exception e){return fail;}
			try{
				RAF = new RandomAccessFile(dem_directory+"/n0"+(int)(minlat+1)+"e0"+(int)minlon+".dem","r");			
				for(int i=0; i<3; i++){
					int pointer = 2*(grid+1)*(grid-1)+2*ix[i];
					RAF.seek(pointer);
					H[i][0] = (double)RAF.readShort();
				}	
				RAF.close();
			}catch(Exception e){return fail;}				
			try{
				RAF = new RandomAccessFile(dem_directory+"/n0"+(int)(minlat+1)+"e0"+(int)(minlon+1)+".dem","r");			
				int pointer = 2*(grid+1)*(grid-1)+2;
				RAF.seek(pointer);
				H[3][0] = (double)RAF.readShort();
				RAF.close();
			}catch(Exception e){return fail;}	
			BiCubicSpline bcs =new BiCubicSpline(X, Y, H);
			double elevation = bcs.interpolate(dlon, dlat);	
			//informatii_punct(longitude, latitude, elevation, x, y, H);
			return elevation;			
		}			
			
		//colt SE
		if(x == grid-1 && y == 0){
			if(S.indexOf(name)>-1 || E.indexOf(name)>-1) return fail;
			for(int i=0; i<3; i++){
				for(int j=0; j<3; j++){
					try{
						int pointer = 2*(grid+1)*(grid-iy[j])+2*ix[i];
						raf.seek(pointer);
						H[i][j] = (double)raf.readShort();
					}
					catch(Exception e){return fail;}	
				}			
			}		
			RandomAccessFile RAF = null;
			try{
				RAF = new RandomAccessFile(dem_directory+"/n0"+(int)(minlat-1)+"e0"+(int)minlon+".dem","r");			
				for(int i=0; i<3; i++){
					int pointer = 2*(grid+1)+2*ix[i];
					RAF.seek(pointer);
					H[i][3] = (double)RAF.readShort();
				}	
				RAF.close();
			}catch(Exception e){return fail;}	
			try{
				RAF = new RandomAccessFile(dem_directory+"/n0"+(int)minlat+"e0"+(int)(minlon+1)+".dem","r");			
				for(int j=0; j<3; j++){
					int pointer = 2*(grid+1)*(grid-iy[j])+2;
					RAF.seek(pointer);
					H[3][j] = (double)RAF.readShort();
				}	
				RAF.close();
			}catch(Exception e){return fail;}			
			try{
				RAF = new RandomAccessFile(dem_directory+"/n0"+(int)(minlat-1)+"e0"+(int)(minlon+1)+".dem","r");			
				int pointer = 2*(grid+1)+2;
				RAF.seek(pointer);
				H[3][3] = (double)RAF.readShort();			
				RAF.close();
			}catch(Exception e){return fail;}			
			BiCubicSpline bcs =new BiCubicSpline(X, Y, H);
			double elevation = bcs.interpolate(dlon, dlat);	
			//informatii_punct(longitude, latitude, elevation, x, y, H);
			return elevation;			
		}	
		
	}
	return fail;
}


public void informatii_punct(double longitude, double latitude, double elevation, int x, int y, double[][] H){
	System.out.println("=========  punct "+(++contor)+"  =========\n");
	System.out.println("long = " + longitude + ",  lat = " + latitude);
	System.out.println("x = " + x + ",   y = " + y);
	System.out.println("elev = " + elevation + "\n");	
	for(int j=0; j<4; j++){
		System.out.println((int)(H[0][j]+0.5)+"  "+(int)(H[1][j]+0.5)+"  "+(int)(H[2][j]+0.5)+"  "+(int)(H[3][j]+0.5)+"\n");
	}
}



public short getElevationTL(double longitude, double latitude){
	if(raf!=null && minlon<=longitude && longitude<maxlon && minlat<=latitude && latitude<maxlat){
		double dlon = (longitude-minlon)*grid;
		double dlat = (maxlat-latitude)*grid;
		int x = (int)(dlon);
		int y = (int)(dlat);	
		try{
			raf.seek(2*grid*y+2*x); 
			return raf.readShort();
		}
		catch(IOException e){e.printStackTrace();}		
	}else return -1;
	return -1;
}


public int getElevationR(double longitude, double latitude){
	if(raf!=null && minlon<=longitude && longitude<maxlon && minlat<=latitude && latitude<maxlat){
		double dlon = (longitude-minlon)*grid;
		double dlat = (maxlat-latitude)*grid;
		int x = (int)(dlon);
		int y = (int)(dlat);
		
		double dx = dlon-x;
		double dy = dlat-y;
		
		if(dx<=0.5)
			if(dy<=0.5) System.out.println("TL");
			else System.out.println("BL");
		else
			if(dy<=0.5) System.out.println("TR");
			else System.out.println("BR");
					
		
		try{
			int pointer = 2*grid*y+2*x;
			raf.seek(pointer);
			short TL = raf.readShort();
			System.out.println("TL: "+TL);

			pointer = 2*grid*y+2*(x+1);
			raf.seek(pointer);
			short TR = raf.readShort();	
			System.out.println("TR: "+TR);		
			
			pointer = 2*grid*(y+1)+2*x;
			raf.seek(pointer);
			short BL = raf.readShort();
			System.out.println("BL: "+BL);

			pointer = 2*grid*(y+1)+2*(x+1);
			raf.seek(pointer);
			short BR = raf.readShort();	
			System.out.println("BR: "+BR);	
			
			System.out.println("");					
			
			return TL;
		}
		catch(IOException e){}	

		
	}else return -1;
	return -1;
}



	
	
}