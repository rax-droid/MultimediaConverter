package rax.file2image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import p79068.bmpio.AbstractRgb888Image;
import p79068.bmpio.BmpImage;
import p79068.bmpio.BmpWriter;
import p79068.bmpio.Rgb888Image;

public class FileToBMP {
	
	private static double WIDTH_BILI = 16.0;
	private static double HEIGHT_BILI = 9.0;
	
	private FileToBMP()
	{
		
	}
	
	public static void toBMP(String str_file_path, String str_jpeg_path)
	{
		try {
//			convertDataToBMP(str_file_path, str_jpeg_path);
			convertDataToBMP2(str_file_path, str_jpeg_path);
			System.out.println("File to bmp success.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected static void convertDataToBMP(String str_data_path, String str_jpeg_path) throws IOException
	{
		File file_data = new File(str_data_path);
		int n_width = calWidthBytSize(file_data.length());
		int n_hight = n_width * ((int)HEIGHT_BILI) / ((int)WIDTH_BILI);
		
		FileInputStream in = new FileInputStream(file_data);
		byte[] b_data_array = new byte[n_width * n_hight * 3];
		in.read(b_data_array, 0, (int) file_data.length());
		in.close();
		for (int i = (int) file_data.length(); i < (n_width * n_hight * 3); ++i)
		{
			b_data_array[i] = 0;
		}
		
		File file = new File(str_jpeg_path);
		
		Rgb888Image image = new AbstractRgb888Image(n_width, n_hight) {
			public int getRgb888Pixel(int x, int y) {
				int n_pixel = 0;
				byte b_0 = b_data_array[x * 0 + y * n_width];
				byte b_8 = b_data_array[x * 1 + y * n_width];
				byte b_16 = b_data_array[x * 2 + y * n_width];
				n_pixel = ((b_0 & 0xFF) | ((b_8 << 8) & 0xFF00) | ((b_16 << 16)) & 0xFF0000);
				
				return n_pixel;
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
	}
	
	/**
	 * @param n_size
	 * @return
	 */
	protected static int calWidthBytSize(long n_size)
	{
		//height = width * h_bili / w_bili
		// (width * 3 + 3) * width * h_bili / w_bili = n_size
		// (3 * h_bili / w_bili) * width^2 + (3 * h_bili / w_bili) * width + (- n_size) = 0
		//delta = (3 * h_bili / w_bili)^2 + 4 * (3 * h_bili / w_bili) * n_size
		//width > 0 => width = (-(3 * h_bili / w_bili) + sqrt(delta)) / 2 * (3 * h_bili / w_bili);
		int n_width = 0;
		
		do{
			if (0 >= n_size)
			{
				break;
			}
			
			double d_delta = (3 * HEIGHT_BILI / WIDTH_BILI) * (3 * HEIGHT_BILI / WIDTH_BILI) + 
					4 * (3 * HEIGHT_BILI / WIDTH_BILI) * ((double)n_size);
			
			double d_width = (0 - (3 * HEIGHT_BILI / WIDTH_BILI) + Math.sqrt(d_delta)) / (2 * (3 * HEIGHT_BILI / WIDTH_BILI));
			n_width = (int)d_width + 1;
		}while (false);
		
		int n_yushu = n_width % ((int)WIDTH_BILI);
		n_width +=  ((int)WIDTH_BILI) - n_yushu;
		
		return n_width;
	}
	
	//////
	
	protected static void convertDataToBMP2(String str_data_path, String str_jpeg_path) throws IOException
	{
		File file_data = new File(str_data_path);
		int n_width = calWidthBytSize(file_data.length());
		int n_hight = n_width * ((int)HEIGHT_BILI) / ((int)WIDTH_BILI);
		int data_array_len = n_width * n_hight * 3;
		if (4 > (data_array_len - file_data.length())) {
			System.out.println("img size not enough.");
			System.exit(-1);
		}
		
		FileInputStream in = new FileInputStream(file_data);
		byte[] b_data_array = new byte[data_array_len];
		in.read(b_data_array, 0, (int) file_data.length());
		in.close();
		
		System.arraycopy(b_data_array, 0, b_data_array, (int) file_data.length(), data_array_len - (int) file_data.length());
		byte[] len_bytes = intToBytes((int)file_data.length());
		System.arraycopy(len_bytes, 0, b_data_array, data_array_len - len_bytes.length, len_bytes.length);
		
		File file = new File(str_jpeg_path);
		Rgb888Image image = new AbstractRgb888Image(n_width, n_hight) {
			public int getRgb888Pixel(int x, int y) {
				int n_pixel = 0;
				byte b_0  = b_data_array[x * 3 + 0 + y * n_width * 3];
				byte b_8  = b_data_array[x * 3 + 1 + y * n_width * 3];
				byte b_16 = b_data_array[x * 3 + 2 + y * n_width * 3];
				n_pixel = ((b_0 & 0xFF) | ((b_8 << 8) & 0xFF00) | ((b_16 << 16)) & 0xFF0000);
								
				return n_pixel;
			}
		};
		
		BmpImage bmp = new BmpImage();
		bmp.image = image;
		FileOutputStream out = new FileOutputStream(file);
		try {
			BmpWriter.write2(out, bmp);
		} finally {
			out.close();
		}
	}

	protected static byte[] intToBytes(int num) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)((num >>> 24) & 0xFF);
		bytes[1] = (byte)((num >>> 16) & 0xFF);
		bytes[2] = (byte)((num >>> 8) & 0xFF);
		bytes[3] = (byte)((num) & 0xFF);
		
		return bytes;
	}
}
