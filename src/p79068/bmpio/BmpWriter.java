package p79068.bmpio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public final class BmpWriter {
	
	public static void write(OutputStream out, BmpImage bmp) throws IOException {
		LittleEndianDataOutput out1 = new LittleEndianDataOutput(out);
		
		Rgb888Image image = bmp.image;
		int width = image.getWidth();
		int height = image.getHeight();
		int rowSize = (width * 3 + 3) / 4 * 4;  // 3 bytes per pixel in RGB888, round up to multiple of 4
		int imageSize = rowSize * height;
		
		// BITMAPFILEHEADER
		out1.writeBytes(new byte[]{'B', 'M'});  // FileType			0
		out1.writeInt32(14 + 40 + imageSize);   // FileSize			2
		out1.writeInt16(0);                     // Reserved1		6
		out1.writeInt16(0);                     // Reserved2		8
		out1.writeInt32(14 + 40);               // BitmapOffset		10
		
		// BITMAPINFOHEADER
		out1.writeInt32(40);                        // Size
		out1.writeInt32(width);                     // Width
		out1.writeInt32(height);                    // Height
		out1.writeInt16(1);                         // Planes
		out1.writeInt16(24);                        // BitsPerPixel
		out1.writeInt32(0);                         // Compression
		out1.writeInt32(imageSize);                 // SizeOfBitmap
		out1.writeInt32(bmp.horizontalResolution);  // HorzResolution
		out1.writeInt32(bmp.verticalResolution);    // VertResolution
		out1.writeInt32(0);                         // ColorsUsed
		out1.writeInt32(0);                         // ColorsImportant
		
		// Image data
		byte[] row = new byte[rowSize];
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				int color = image.getRgb888Pixel(x, y);
				row[x * 3 + 0] = (byte)(color >>>  0);  // Blue
				row[x * 3 + 1] = (byte)(color >>>  8);  // Green
				row[x * 3 + 2] = (byte)(color >>> 16);  // Red
			}
			out1.writeBytes(row);
		}
		
		out1.flush();
	}
	
//////
	
	public static void write2(OutputStream out, BmpImage bmp) throws IOException {
		LittleEndianDataOutput out1 = new LittleEndianDataOutput(out);
		
		Rgb888Image image = bmp.image;
		int width = image.getWidth();
		int height = image.getHeight();
		int rowSize = width * 3;
		int imageSize = rowSize * height;
		
		// BITMAPFILEHEADER
		out1.writeBytes(new byte[]{'B', 'M'});  // FileType
		out1.writeInt32(14 + 40 + imageSize);   // FileSize
		out1.writeInt16(0);                     // Reserved1
		out1.writeInt16(0);                     // Reserved2
		out1.writeInt32(14 + 40);               // BitmapOffset
		
		// BITMAPINFOHEADER
		out1.writeInt32(40);                        // Size
		out1.writeInt32(width);                     // Width
		out1.writeInt32(height);                    // Height
		out1.writeInt16(1);                         // Planes
		out1.writeInt16(24);                        // BitsPerPixel
		out1.writeInt32(0);                         // Compression
		out1.writeInt32(imageSize);                 // SizeOfBitmap
		out1.writeInt32(bmp.horizontalResolution);  // HorzResolution
		out1.writeInt32(bmp.verticalResolution);    // VertResolution
		out1.writeInt32(0);                         // ColorsUsed
		out1.writeInt32(0);                         // ColorsImportant
		
		// Image data
		byte[] row = new byte[rowSize];
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				int color = image.getRgb888Pixel(x, y);
				row[x * 3 + 0] = (byte)(color >>>  0);  // Blue
				row[x * 3 + 1] = (byte)(color >>>  8);  // Green
				row[x * 3 + 2] = (byte)(color >>> 16);  // Red
			}
			
			out1.writeBytes(row);
		}

		out1.flush();
	}

	public static class ImageData {
		public int width = 0;
		public int height = 0;
		public byte[] data = null;
		
		public ImageData() {
			
		}
		
		public void load(InputStream in) throws IOException {
			LittleEndianDataInput in1 = new LittleEndianDataInput(in);
			
			// BITMAPFILEHEADER (14 bytes)
			int fileSize;
			int imageDataOffset;
			if (in1.readInt16() != 0x4D42)  // "BM"
				throw new RuntimeException("Invalid BMP signature");
			fileSize = in1.readInt32();
			in1.skipFully(4);  // Skip reserved
			imageDataOffset = in1.readInt32();
			
			// BITMAPINFOHEADER
			int headerSize = in1.readInt32();
			int width;
			int height;
			boolean topToBottom;
			int bitsPerPixel;
			int compression;
			int colorsUsed;
			BmpImage bmp = new BmpImage();
			if (headerSize == 40) {
				int planes;
				int colorsImportant;
				width  = in1.readInt32();
				height = in1.readInt32();
				topToBottom = height < 0;
				height = Math.abs(height);
				planes = in1.readInt16();
				bitsPerPixel = in1.readInt16();
				compression = in1.readInt32();
				in1.readInt32();  // imageSize
				bmp.horizontalResolution = in1.readInt32();
				bmp.verticalResolution   = in1.readInt32();
				colorsUsed = in1.readInt32();
				colorsImportant = in1.readInt32();
				
				if (width <= 0)
					throw new RuntimeException("Invalid width: " + width);
				if (height == 0)
					throw new RuntimeException("Invalid height: " + height);
				if (planes != 1)
					throw new RuntimeException("Unsupported planes: " + planes);
				
				if (bitsPerPixel == 1 || bitsPerPixel == 4 || bitsPerPixel == 8) {
					if (colorsUsed == 0)
						colorsUsed = 1 << bitsPerPixel;
					if (colorsUsed > 1 << bitsPerPixel)
						throw new RuntimeException("Invalid colors used: " + colorsUsed);
					
				} else if (bitsPerPixel == 24 || bitsPerPixel == 32) {
					if (colorsUsed != 0)
						throw new RuntimeException("Invalid colors used: " + colorsUsed);
					
				} else
					throw new RuntimeException("Unsupported bits per pixel: " + bitsPerPixel);
				
				if (compression == 0) {
				} else if (bitsPerPixel == 8 && compression == 1 || bitsPerPixel == 4 && compression == 2) {
					if (topToBottom)
						throw new RuntimeException("Top-to-bottom order not supported for compression = 1 or 2");
				} else
					throw new RuntimeException("Unsupported compression: " + compression);
				
				if (colorsImportant < 0 || colorsImportant > colorsUsed)
					throw new RuntimeException("Invalid important colors: " + colorsImportant);
				
			} else
				throw new RuntimeException("Unsupported BMP header format: " + headerSize + " bytes");
			
			// Some more checks
			if (14 + headerSize + 4 * colorsUsed > imageDataOffset)
				throw new RuntimeException("Invalid image data offset: " + imageDataOffset);
			if (imageDataOffset > fileSize)
				throw new RuntimeException("Invalid file size: " + fileSize);
			
			// Read the image data
			in1.skipFully(imageDataOffset - (14 + headerSize + 4 * colorsUsed));
			int imageSize = fileSize - (14 + 40);
			byte[] tmpData = new byte[imageSize];
			in1.readFully(tmpData);			
			
			this.width = width;
			this.height = height;
			this.data = new byte[imageSize];
			
			// Image data
			for (int y = height - 1, yy = 0; y >= 0; y--, yy++) {
				for (int x = 0; x < width; x++) {
					this.data[x * 3 + 0 + y * width * 3] = tmpData[x * 3 + 0 + yy * width * 3];
					this.data[x * 3 + 1 + y * width * 3] = tmpData[x * 3 + 1 + yy * width * 3];
					this.data[x * 3 + 2 + y * width * 3] = tmpData[x * 3 + 2 + yy * width * 3];					
				}
			}
		}
	}
	
	// Not instantiable
	private BmpWriter() {}
	
}
