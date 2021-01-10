package rax.file2image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import p79068.bmpio.BmpWriter;

public class BMPToFile {
	
	public static void toFile(String bmp_path, String file_path)
	{
		try {
			convertBMPtoFile(bmp_path, file_path);
			System.out.println("bmp to File success.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected static void convertBMPtoFile(String bmp_path, String file_path) throws IOException {
		BmpWriter.ImageData img_data = new BmpWriter.ImageData();
		img_data.load(new FileInputStream(new File(bmp_path)));
		
		byte[] bytes = new byte[4];
		System.arraycopy(img_data.data, img_data.data.length - 4, bytes, 0, 4);
		int size = bytesToInt(bytes);
		
		FileOutputStream out = new FileOutputStream(new File(file_path));
		///
		out.write(img_data.data, 0, size);
		///
		out.flush();
		out.close();
	}
	
	protected static int bytesToInt(byte[] bytes) {
		int num = ((bytes[0] << 24) & 0xFF000000) | 
				  ((bytes[1] << 16) & 0xFF0000) | 
				  ((bytes[2] << 8)  & 0xFF00) |
				  ((bytes[3] << 0)  & 0xFF);
		return num;
	}
}
