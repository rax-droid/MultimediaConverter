package rax.main;

import java.io.File;
import java.io.IOException;

import rax.file2image.FileToBMP;
import rax.file2image.BMPToFile;

public class Main {
	public static void main(String[] args) 
	{
		if (0 >= args.length)
		{
			return;
		}
		
		File file = new File(args[0]);
		if (!file.exists())
		{
			return;
		}
		
//		toBMP(file);
		toFile(file);
	}
	
	protected static void toBMP(File file_path)
	{

		if (file_path.isDirectory())
		{
			File[] file_list = file_path.listFiles();
			for (File kid : file_list)
			{
				toBMP(kid);
			}
			
			return;
		}
		if (file_path.isHidden()) {
			return;
		}
		
		String str_jpeg_path = file_path.getAbsolutePath() + ".bmp";
		FileToBMP.toBMP(file_path.getAbsolutePath(), str_jpeg_path);
	}
	
	protected static void toFile(File file_path)
	{
		if (file_path.isDirectory())
		{
			File[] file_list = file_path.listFiles();
			for (File kid : file_list)
			{
				toFile(kid);
			}
			
			return;
		}
		if (file_path.isHidden()) {
			return;
		}
		
		if (!file_path.getName().endsWith(".bmp")) {
			return;
		}
		
		String str_jpeg_path = file_path.getAbsolutePath() + ".bin";
		BMPToFile.toFile(file_path.getAbsolutePath(), str_jpeg_path);
	}
}
