import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;

import javax.imageio.ImageIO;




public class Bitmap2Text {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// argument 0 = input filename without the extention, it always assumes a bmp
		// argument 1 = scale of each pixel in metres
			
			
		try{
	
		
		File inputImageFile = new File(args[0]+".bmp");
		if (!inputImageFile.exists()){	
			System.out.println("no bmp with that file name exists, exiting");
			System.exit(0);
		}
		System.out.println("Opening: " + args[0]+".bmp");		
				
		BufferedImage inputImage = ImageIO.read(inputImageFile);	
		System.out.println("Image width (pixels): " + inputImage.getWidth());
		System.out.println("Image height (pixels): " + inputImage.getHeight());
		

		double scale = 0;

		if (args.length <2)
		{
			System.out.println("No scale specified, defaulting to 1 pixel = 0.01m");
			scale = 0.01;
		}else{
			scale = Double.parseDouble(args[1]);
			System.out.println("Scale set to 1 pixel = " + args[1] + "m");
			
		}
		System.out.println("The image will represent " + inputImage.getWidth() * scale + "m in X and "+ inputImage.getHeight() * scale + "m in the Y direction");

		
		double ImageLeft = (double)((double)inputImage.getWidth()/(2 * (1/scale))) * -1;
		double ImageBottom = (double)((double)inputImage.getHeight()/(2 * (1/scale)));
		
		
		
		File outputFile = new File(args[0] + ".txt");
		PrintWriter output = new PrintWriter(outputFile);
		
		// do top line
		
		output.print("0 ");
		for(int x = 0; x < inputImage.getWidth() ; x++ ){
			output.print(ImageLeft + (x * scale) + " ");
		}
		
		output.println("");
		
		for(int y = inputImage.getHeight() - 1; y >=0  ; y-- ){
			
			// do y value
			output.print(ImageBottom - (y*scale)+" ");
			
			for(int x = 0; x < inputImage.getWidth() ; x++ ){
				
				// do pixel value
				output.print(inputImage.getRGB(x, y)+ " ");
			}
			output.println("");
		}
		output.close();
		
		}catch(NumberFormatException e){
			System.err.println("The argument input for the scale was not a number, exiting");
			System.exit(0);
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

}
