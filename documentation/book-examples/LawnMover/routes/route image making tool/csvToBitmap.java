import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;




public class csvToBitmap{

	static List<String> xCoords;
	static List<String> yCoords;


	static BufferedImage img = null;
	static int lineWidth = 4;
	static int colour = 0;

	static int xLeft = 0;
	static int xRight = 0;
	static int yTop = 0;
	static int yBottom = 0;

	static int greenWidth = 0;
	static int blackWidth = 0;

public static void main(String[] args)throws Exception{
	
	
	processArguments(args);
	readCSVData();
	makeBackgroundWhite();
	


	lineWidth = greenWidth;
	colour = RGBtoInt(0,255,0);
	processLine();
	

	lineWidth = blackWidth;
	colour = RGBtoInt(0,0,0);
	processLine();


	ImageIO.write(img, "PNG", new File("route.png"));


}


private static void makeBackgroundWhite(){
	colour = RGBtoInt(255,255,255);

	for (int i=0; i<img.getWidth(); i++)
		for(int j=0; j<img.getHeight(); j++)
			img.setRGB(i,j,colour);
}

private static void processArguments(String[] args) throws Exception{

	if (args.length < 6){
		System.err.println("There must be 6 arguments");
		System.exit(1)	;
	}

	xLeft = Integer.parseInt(args[0]);
	xRight = Integer.parseInt(args[1]);
	yBottom = Integer.parseInt(args[2]);
	yTop = Integer.parseInt(args[3]);
	blackWidth = Integer.parseInt(args[4])/2;
	greenWidth = Integer.parseInt(args[5])/2;

	img = new BufferedImage(xRight-xLeft,yTop-yBottom,BufferedImage.TYPE_3BYTE_BGR);
	

}


private static void readCSVData(){
	xCoords = new LinkedList<String>();
	yCoords = new LinkedList<String>();

 	// Location of file to read
        File file = new File("route.csv");
 
        try {
 
            Scanner scanner = new Scanner(file);
 
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                //System.out.println(line);
		String[] tokens = line.split(",");

		xCoords.add(tokens[0]);
		yCoords.add(tokens[1]);
		
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
	
}


private static void processLine() throws Exception{

	Double firstX = 0.0;
	Double firstY = 0.0;
	Double previousX = 1.0;
	Double previousY = 1.0;

	boolean first = true;

	Iterator<String> xIt = xCoords.iterator();
	Iterator<String> yIt = yCoords.iterator();

	while (xIt.hasNext() && yIt.hasNext()){
		if (!first){
			Double thisX = Double.parseDouble(xIt.next());
			Double thisY = Double.parseDouble(yIt.next());
			joinPoints(previousX, previousY, thisX, thisY);
			previousX = thisX;
			previousY = thisY;
			
		} else {
			first = false;
			firstX = Double.parseDouble(xIt.next());
			firstY = Double.parseDouble(yIt.next());
			previousX = firstX;
			previousY = firstY;
		}

	}

	joinPoints(previousX, previousY, firstX, firstY);	


}

private static int xToPixel(double x){

	return (int)(x *100) - xLeft;	
	
}

private static int yToPixel(double y){
	return yTop - (int)(y*100);
}


private static void joinPoints(double x1, double y1, double x2, double y2){

	int pixelX1 = xToPixel(x1);
	int pixelY1 = yToPixel(y1);
	int pixelX2 = xToPixel(x2);
	int pixelY2 = yToPixel(y2);


	double separation = Math.pow( Math.pow((double)pixelX2 - (double)pixelX1,2) + Math.pow((double)pixelY2 - (double)pixelY1,2), 0.5);

	int xPoint;
	int yPoint;
	
	for (int step =0; step <= separation; step++){
	
		xPoint = pixelX1 + (int)((pixelX2-pixelX1) * step/separation);
		yPoint = pixelY1 + (int)((pixelY2-pixelY1) * step/separation);

		addDot(xPoint,yPoint);

	} 

}


private static int RGBtoInt(int r, int g, int b){

	int rr = r << 16;
	int gr = g << 8;
	return rr + gr + b;
}



private static void addDot(int x, int y){
	
	int pixelX;
	int pixelY;

	for (int i=-lineWidth; i<lineWidth; i++)
		for(int j=-lineWidth; j<lineWidth; j++){
			
			pixelX = x + i;
			pixelY = y + j;
			if (pixelX >= 0 && pixelX < img.getWidth() && pixelY >=0 && pixelY < img.getHeight()){
				

				if (plotPointForRadius(x,y, pixelX, pixelY, lineWidth)){
					img.setRGB(pixelX,pixelY,colour);
					
				}else{
					
				}
			}
					
		}
			


}


private static boolean plotPointForRadius(int xCentre, int yCentre, int xPoint, int yPoint, int rad){

	double radius = Math.pow( Math.pow((double)xPoint - (double)xCentre,2) + Math.pow((double)yPoint - (double)yCentre,2), 0.5);

	if (radius <= rad)
	
		return true;
	else
		return false;

}





}