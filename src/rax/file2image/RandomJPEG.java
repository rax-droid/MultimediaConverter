package rax.file2image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import p79068.bmpio.AbstractRgb888Image;
import p79068.bmpio.BmpImage;
import p79068.bmpio.BmpWriter;
import p79068.bmpio.Rgb888Image;

public class RandomJPEG {

	private static int WIDTH_PIX = 1280;
	private static int HEIGHT_PIX = 720;
	
	private RandomJPEG()
	{
		
	}
	
	public static void createJPEG(String str_jpeg_path) throws IOException
	{
		File file = new File(str_jpeg_path);
		
//		Random random = new Random();
		
    	HashSet<Integer> set = new HashSet<Integer>();
    	randomSet(0, 0x00FFFFFF, RandomJPEG.WIDTH_PIX * RandomJPEG.HEIGHT_PIX, set);
    	Iterator<Integer> itor = set.iterator();
    	
		Rgb888Image image = new AbstractRgb888Image(WIDTH_PIX, HEIGHT_PIX) {
			public int getRgb888Pixel(int x, int y) {
				//return random.nextInt();
				if (!itor.hasNext())
				{
					return 0;
				}
				
				return itor.next();
			}
		};
		
		BmpImage bmp = new BmpImage();
		bmp.image = image;
		FileOutputStream out = new FileOutputStream(file);
		try {
			BmpWriter.write(out, bmp);
		} finally {
			out.close();
		}
		
		System.out.println("randmo jpeg created success.");
	}
	

	    private static void randomSet(int min, int max, int n, HashSet<Integer> set) {
	        if (n > (max - min + 1) || max < min) {
	            return;
	        }
	        for (int i = 0; i < n; i++) {
	            int num = (int) (Math.random() * (max - min)) + min;
	            set.add(num);
	        }
	        int setSize = set.size();
	        if (setSize < n) {
	        	randomSet(min, max, n - setSize, set);
	        }
	    }
	    
		public static int[] randomCommon(int min, int max, int n){
			if (n > (max - min + 1) || max < min) {
	            return null;
	        }
			int[] result = new int[n];
			int count = 0;
			while(count < n) {
				int num = (int) (Math.random() * (max - min)) + min;
				boolean flag = true;
				for (int j = 0; j < n; j++) {
					if(num == result[j]){
						flag = false;
						break;
					}
				}
				if(flag){
					result[count] = num;
					count++;
				}
			}
			return result;
		}
		
		 
		public static int[] randomArray(int min,int max,int n){
			int len = max-min+1;
			
			if(max < min || n > len){
				return null;
			}
			
			int[] source = new int[len];
	        for (int i = min; i < min+len; i++){
	        	source[i-min] = i;
	        }
	        
	        int[] result = new int[n];
	        Random rd = new Random();
	        int index = 0;
	        for (int i = 0; i < result.length; i++) {
	            index = Math.abs(rd.nextInt() % len--);
	            result[i] = source[index];
	            source[index] = source[len];
	        }
	        return result;
		}
}
