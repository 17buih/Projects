import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;
import java.net.*;
import java.io.*;

/**
 * Code obtained from Dr. Kenny Hunt, The University of Wisconsin – La Crosse.
 * "Using image processing to teach cs1 and cs2" (2003). SIGCSE Bulletin.
 * 
 * 
 * Represents an image that can be manipulated without a thorough understanding
 * of the built-in image classes.  Performance (in terms of memory and speed) is sacrificed in order to provide
 * simplified access to pixel data.  Allows direct read/write access to individual pixels
 * and a factory method for creating EasyBufferedImages from files.  The image always
 * appears as either a gray-scale image, an RGB image or an RGBA image.  The data is
 * never packed or interleaved (at least with respect to the get/set pixel methods).
 * <p>
 * Please note that grayscale processing of a <b>color</b> image always maintains the color information of the image while
 * altering the <b>intensity</b> channel.  To perform operations on a purely gray-scale image, convert any color
 * images to grayscale prior to processing.
 * <p>
 * Since the EasyBufferedImage class is a sub-class of Image and BufferedImage, it
 * is fully integrated into the standard Swing libraries for viewing and manipulation.
 * To create an EasyBufferedImage you must use the factory method shown below:
 * <p><b><blockquote><pre>EasyBufferedImage image = EasyBufferedImage.createImage(filename);</pre></blockquote></b>
 * where the filename is a String representing an image file in either GIF, PNG, or JPEG
 * formats.  The following code fragment is an example of how to invert a gray-scale image:
 * <b><font color="#115500"><blockquote><pre>
 * int[] pixels = image.getPixels1D(EasyBufferedImage.GRAY);
 * for(int i=0; i &lt pixels.length; i++) {
 *   pixels[i] = 255-pixels[i];
 * }
 * image.setPixels(pixels);
 * </pre></blockquote></font></b>
 * @see         Image
 * @see BufferedImage
 */ 
 
public class EasyBufferedImage extends BufferedImage {
    public static final int GRAY = 4, RED = 0, GREEN = 1, BLUE = 2, ALPHA = 3;
    public static final int JPEG = 0, GIF = 1, PNG = 2;
    private static final String[] TYPES =  {"JPEG", "GIF", "PNG"};
    private static int windowCount = 0;

    /** 
     * Returns a String array listing the file formats supported by this class.
     * @param none
     * @return   an array of strings listing the supported file formats.
     */
    public String[] getSupportedFormats() {
	return TYPES;
    }


    public static EasyBufferedImage createImage(BufferedImage image) {
	return toBufferedImage(image);
    }

    /**
     * Returns an EasyBufferedImage object by reading the image file
     * specified by the filename.  The format of the file may be any
     * one of GIF, PNG, or JPEG.  This is a factory method to create 
     * EasyBufferedImages.  There is no direct constructor access.
     * <p>
     * @param  filename the name of the file to load.
     * @return      the EasyBufferedImage stored in the file.
     * @throws FileNotFoundException
     * @see         Image
     * @see BufferedImage
     */
    public static EasyBufferedImage createImage(String filename) throws FileNotFoundException {
	File file = new File(filename);
	if(!file.exists()) throw new FileNotFoundException(filename);

	return toBufferedImage(Toolkit.getDefaultToolkit().createImage(filename));
    }


    public static EasyBufferedImage createImage(File file) throws IOException, FileNotFoundException {
	if(!file.exists()) throw new FileNotFoundException(file.getName());

	return toBufferedImage(ImageIO.read(file));
    }

    /**
     * Returns an EasyBufferedImage object by reading the image
     * specified by the url.  The format of the file may be any
     * one of GIF, PNG, or JPEG.  This is a factory method to create 
     * EasyBufferedImages.  There is no direct constructor access.
     * <p>
     * @param  url is the URL of an image file to load.
     * @return      the EasyBufferedImage stored in the file.
     * @see         Image
     * @see BufferedImage
     */
    public static EasyBufferedImage createImage(URL url) {
	return toBufferedImage(Toolkit.getDefaultToolkit().createImage(url));
    }

    /**
     * Returns an EasyBufferedImage object represented by the specified pixels.
     * The dimensions of the pixel array must be [height][width][bands] where bands may
     * be either 1 (gray-scale) or 3 (red-green-blue).
     * This is a factory method to create EasyBufferedImages.  There is no direct constructor access.
     * <p>
     * @param       pixels is an array of [height][width][bands] pixels that represents the image.
     * @return      An EasyBufferedImage representation of the pixels.
     * @throws IllegalArgumentException if pixels is null 
     * @see         Image
     * @see BufferedImage
     */
    public static EasyBufferedImage createImage(int[][][] pixels) {
	if(pixels == null) throw new IllegalArgumentException("null pixels array");
	// create the BufferedImage (doesn't include transparency!)
	EasyBufferedImage result = new EasyBufferedImage(pixels[0].length,
							 pixels.length,
							 BufferedImage.TYPE_INT_RGB);

	result.setPixels(pixels);
	return result;
    }

    /**
     * Returns an EasyBufferedImage object represented by the specified pixels.
     * The dimensions of the pixel array must be [height][width] and is assumed
     * to be gray-scale.
     * This is a factory method to create EasyBufferedImages.  There is no direct constructor access.
     * <p>
     * @param       pixels is an array of [height][width] pixels that represents the image.
     * @return      An EasyBufferedImage representation of the pixels.
     * @throws      IllegalArgumentException if pixels is null
     * @see         Image
     * @see BufferedImage
     */
    public static EasyBufferedImage createImage(int[][] pixels) {
	if(pixels == null) throw new IllegalArgumentException("null pixels array");	
	// create the BufferedImage (doesn't include transparency!)
	EasyBufferedImage result = new EasyBufferedImage(pixels[0].length,
							 pixels.length,
							 BufferedImage.TYPE_INT_RGB);

	result.setPixels(pixels, RED);
	result.setPixels(pixels, GREEN);
	result.setPixels(pixels, BLUE);
	return result;
    }

    /**
     * Returns an EasyBufferedImage object represented by the specified pixels.
     * The dimensions of the pixel array must be [height * width] and layed out in
     * row-major format.  The image is assumed to be gray-scale.
     * This is a factory method to create EasyBufferedImages.  There is no direct constructor access.
     * <p>
     * @param       pixels is an array of [height * width] pixels that represents the image.
     * @param       height is the height (in pixels) of the image.
     * @param       width is the width (in pixels) of the image.
     * @return      An EasyBufferedImage representation of the pixels.
     * @throws      IllegalArgumentException if pixels is null or the length is not width * height.
     * @see         Image
     * @see BufferedImage
     */
    public static EasyBufferedImage createImage(int[] pixels, int width, int height) {
	if(pixels == null) {
	    throw new IllegalArgumentException("null pixels array");
	} else if((width * height) != pixels.length) {
	    throw new IllegalArgumentException("pixels dimensions doesn't match width/height parameters");
	}
	if(pixels == null) throw new IllegalArgumentException("null pixels array");
	// create the BufferedImage (doesn't include transparency!)
	EasyBufferedImage result = new EasyBufferedImage(width,
							 height,
							 BufferedImage.TYPE_INT_RGB);

	result.setPixels(pixels, RED);
	result.setPixels(pixels, GREEN);
	result.setPixels(pixels, BLUE);
	return result;
    }


    private EasyBufferedImage(int w, int h, int type) {
	super(w, h, type);
    }

    /**
     * Returns the value of v clamped to the range 0-255.
     * This is a convenience method for working with image pixel values.
     * <p>
     * @param  v is the value of a pixel to be clamped.
     * @return      the value v clamped to the range 0-255.
     */
    public static int clamp(double v) {
	if(v < 0) return 0;
	else if(v > 255) return 255;
	else return (int)v;
    }

    /**
     * Returns an EasyBufferedImage object that is a gray-scale copy
     * of the calling image.
     * <p>
     * @param  None
     * @return      an EasyBufferedImage that is a gray-scale copy of the caller.
     */
    public EasyBufferedImage copyToGrayScale() {
	EasyBufferedImage result = new EasyBufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_BYTE_GRAY);
	WritableRaster input = getRaster();
	WritableRaster output = result.getRaster();
	for(int row=0; row < input.getHeight(); row++) {
	    for(int col=0; col < input.getWidth(); col++) {
		int red = input.getSample(col, row, 0);
		int green = input.getSample(col, row, 1);
		int blue = input.getSample(col, row, 2);
		output.setSample(col, row, 0, clamp((red+green+blue)/3.0));
	    }
	}
	return result;
    }
    
    private static EasyBufferedImage toBufferedImage(Image image) {
	// make sure that the Image is loaded (images can be loaded asynchronously)
	image = new ImageIcon(image).getImage();
	// create the BufferedImage (doesn't include transparency!)
	EasyBufferedImage result = new EasyBufferedImage(image.getWidth(null),
							 image.getHeight(null),
							 BufferedImage.TYPE_INT_RGB);

	// Draw the input onto the BufferedImage and return
	Graphics g = result.createGraphics();
	g.drawImage(image, 0, 0, null);
	g.dispose();
	return result;	

    }

    /**
     * Returns a 3D array of pixel values.  The dimensions of the array correspond
     * to the [height][width][bands] of the EasyBufferedImage.  The number of bands
     * will be 1 for a gray-scale or binary image, 3 for a color image without
     * transparency, and 4 for a color image with transparency.  The bands are (in
     * order) RED (or GRAY), GREEN, BLUE, ALPHA.  Pixel values are in the range 0-255.
     * <p>
     * @param  None
     * @return     an array of pixels accessed.
     */
    public int[][][] getPixels3D() {
	int width = getWidth();
	int height = getHeight();
	int bands = getSampleModel().getNumBands();
	int[][][] pixels = new int[height][width][bands];
	WritableRaster raster = getRaster();
	for(int i=0; i<height; i++) {
	    for(int j=0; j<width; j++) {
		for(int k=0; k<bands; k++) {
		    pixels[i][j][k] = raster.getSample(j,i,k);
		}
	    }
	}
	return pixels;
    }
    
    /**
     * Returns a 2D array of pixel values corresponding to the specified band.  
     * The dimensions of the array correspond to the [height][width] of the EasyBufferedImage.  
     * The number of bands will be 1 for a gray-scale or binary image, 3 for a color image without
     * transparency, and 4 for a color image with transparency.  The bands are (in
     * order) RED (or GRAY), GREEN, BLUE, ALPHA.  Pixel values are in the range 0-255.
     * <p>
     * @param  band is either RED, GREEN, BLUE, ALPHA, or GRAY
     * @return     an array of pixels.
     */
    public int[][] getPixels2D(int band) {
	int width = getWidth();
	int height = getHeight();
	int[][] pixels = new int[height][width];
	WritableRaster raster = getRaster();
	if(band == GRAY && isColor()) {
	    float hsb[] = new float[3];
	    for(int i=0; i<height; i++) {
		for(int j=0; j<width; j++) {
		    int pixel = getRGB(j, i);
		    Color.RGBtoHSB(raster.getSample(j, i, 0),
				   raster.getSample(j, i, 1),
				   raster.getSample(j, i, 2),
				   hsb);
		    pixels[i][j] = clamp(hsb[2] * 255);
		}
	    }
	} else {
	    for(int i=0; i<height; i++) {
		for(int j=0; j<width; j++) {
		    pixels[i][j] = raster.getSample(j,i,band);
		}
	    }
	}

	return pixels;
    }

    /**
     * Returns a 1D array of pixel values corresponding to the specified band.  
     * The array is layed out in row-major format and contains height*width pixel values.
     * The number of bands will be 1 for a gray-scale or binary image, 3 for a color image without
     * transparency, and 4 for a color image with transparency.  The bands are (in
     * order) RED (or GRAY), GREEN, BLUE, ALPHA.  Pixel values are in the range 0-255.
     * <p>
     * @param  band is either RED, GREEN, BLUE, ALPHA, or GRAY
     * @return     an array of pixels.
     */
    public int[] getPixels1D(int band) {
	int width = getWidth();
	int height = getHeight();
	int[]pixels = new int[height * width];
	WritableRaster raster = getRaster();
	for(int i=0; i<height; i++) {
	    for(int j=0; j<width; j++) {
		pixels[i*width + j] = raster.getSample(j,i,band);
	    }
	}

	return pixels;
    }


    /**
     * The array is layed out in row-major format and contains height*width pixel values.
     * The number of bands will be 1 for a gray-scale or binary image, 3 for a color image without
     * transparency, and 4 for a color image with transparency.  The bands are (in
     * order) RED (or GRAY), GREEN, BLUE, ALPHA.  Pixel values are in the range 0-255.
     * <p>
     * @return     an array of pixels.
     */
    public int[] getPixels1D() {
	int width = getWidth();
	int height = getHeight();
	int bands = getRaster().getNumBands();
	int[]pixels = new int[height * width * bands];
	WritableRaster raster = getRaster();
	for(int i=0; i<height; i++) {
	    for(int j=0; j<width; j++) {
		for(int k=0; k<bands; k++) {
		    pixels[i*width*bands + j*bands + k] = raster.getSample(j,i,k); // probably not correct here.............
		}
	    }
	}

	return pixels;
    }


    /**
     * Sets all of the EasyBufferedImages pixels in the specified band.  The number of elements
     * in the array must be equal to height*width of the EasyBufferedImage.  The band must
     * be either GRAY, RED, GREEN, BLUE, or ALPHA.  Pixel values not in the range 0-255
     * will be stripped of their higher-order bits.
     * <p>
     * @param  pixels is an array of the "new" image for the specified band.
     * @param  band is one of GRAY, RED, GREEN, BLUE, or ALPHA
     * @throws IllegalArgumentException if the pixels array is not compatible with the image.
     * @return     None
     */
    public void setPixels(int[] pixels, int band) {
	int width = getWidth();
	int height = getHeight();
	if(pixels == null || width * height != pixels.length) 
	    throw new IllegalArgumentException("pixel array doesn't match the image size");

	WritableRaster raster = getRaster();

	if(band == GRAY && isColor()) {
	    float hsb[] = new float[3];
	    for(int i=0; i<height; i++) {
		for(int j=0; j<width; j++) {
		    Color.RGBtoHSB(raster.getSample(j, i, 0),
				   raster.getSample(j, i, 1),
				   raster.getSample(j, i, 2),
				   hsb);
		    hsb[2] = (float)(pixels[i * width + j] / 255.0);
		    int pixel = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
		    setRGB(j, i, pixel);
		}
	    }
	} else {
	    for(int i=0; i<height; i++) {
		for(int j=0; j<width; j++) {
		    raster.setSample(j, i, band, pixels[i*width + j]);
		}
	    }
	}
    }

    /**
     * Sets all of the EasyBufferedImages pixels in the specified band.  The dimensions of the pixels array
     * must correspond to [height][width] of the EasyBufferedImage.  The band must
     * be either GRAY, RED, GREEN, BLUE, or ALPHA.  Pixel values not in the range 0-255
     * will be stripped of their higher-order bits.
     * <p>
     * @param  pixels is an array of the "new" image in the specified band.
     * @param  band is one of GRAY, RED, GREEN, BLUE, or ALPHA
     * @throws IllegalArgumentException if the pixels array is not compatible with the image.
     * @return     None
     */
    public void setPixels(int[][] pixels, int band) {
	int width = getWidth();
	int height = getHeight();
	if(pixels == null || 
	   pixels[0] == null || 
	   width != pixels[0].length || height != pixels.length) 
	    throw new IllegalArgumentException("pixel array doesn't match the image size");

	WritableRaster raster = getRaster();
	if(band == GRAY && isColor()) {
	    float hsb[] = new float[3];
	    for(int i=0; i<height; i++) {
		for(int j=0; j<width; j++) {
		    Color.RGBtoHSB(raster.getSample(j, i, 0),
				   raster.getSample(j, i, 1),
				   raster.getSample(j, i, 2),
				   hsb);
		    hsb[2] = (float)(pixels[i][j] / 255.0);
		    int pixel = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
		    setRGB(j, i, pixel);
		}
	    }
	} else {
	    for(int i=0; i<height; i++) {
		for(int j=0; j<width; j++) {
		    raster.setSample(j, i, band, pixels[i][j]);
		}
	    }
	}
    }

    /**
     * Sets all of the EasyBufferedImages pixels.  The dimensions of the pixels array
     * must correspond to [height][width][bands] of the EasyBufferedImage.  The number of bands
     * will be 1 for a gray-scale or binary image, 3 for a color image without
     * transparency, and 4 for a color image with transparency.  The bands are (in
     * order) RED (or GRAY), GREEN, BLUE, ALPHA.  Pixel values not in the range 0-255
     * will be stripped of their higher-order bits.
     * <p>
     * @param  pixels is a 3D array of HEIGHT by WIDTH by DEPTH pixels of the image
     * @throws IllegalArgumentException if the pixels array is not compatible with the image.
     * @return     None
     */
    public void setPixels(int[][][] pixels) {
	int width = getWidth();
	int height = getHeight();
	int bands = getSampleModel().getNumBands();
	if(pixels == null ||
	   pixels[0] == null ||
	   pixels[0][0] == null ||
	   width != pixels[0].length || height != pixels.length || bands != pixels[0][0].length) 
	    throw new IllegalArgumentException("pixel array doesn't match the image size");
	
	WritableRaster raster = getRaster();
	for(int i=0; i<height; i++) {
	    for(int j=0; j<width; j++) {
		for(int k=0; k<bands; k++) {
		    raster.setSample(j, i, k, pixels[i][j][k]);
		}
	    }
	}
    }

    /**
     * Returns true if the image is a color image and false otherwise.
     * @param      None
     * @return     true if the image is a color image and false otherwise.
     */
    public boolean isColor() {
	return getSampleModel().getNumBands() >= 3;
    }

    /**
     * Returns a copy of the calling EasyBufferedImage.
     * @param      None
     * @return     A copy of the EasyBufferedImage.
     */
    public EasyBufferedImage copy() {
	return toBufferedImage(this);
    }


    /**
     * Creates a window that will display this EasyBufferedImage
     * This method maintains a count of all windows opened via calls to this method
     * and will terminate the application if <b>all</b> such windows are closed.
     * @param      None
     * @return     None
     */
    public void show(String title) {
	JFrame window = new JFrame(title);
	window.getContentPane().add(new ImagePanel(this.copy()));
	window.setSize(getWidth(), getHeight());
	windowCount++;
	window.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    if(--windowCount == 0) System.exit(0); else e.getWindow().dispose();
		}
	    });
	
	window.setVisible(true);
    }

    private static class ImagePanel extends JPanel {
	BufferedImage image;

	ImagePanel(BufferedImage im) {
	    image = im;
	    setMinimumSize(new Dimension(im.getWidth(), im.getHeight()));
	}

	public void paintComponent(Graphics g) {
	    g.setColor(getBackground());
	    g.fillRect(0, 0, getWidth(), getHeight());

	    // center the image
	    int dx = (getWidth() - image.getWidth())/2;
	    if(dx < 0) dx = 0;
	    
	    int dy = (getHeight() - image.getHeight())/2;
	    if(dy < 0) dy = 0;
	    g.drawImage(image, dx, dy, this);
	}
    }

    /**
     * Creates an image file having the specified name and of the specified format.
     * @param      filename the name of the file to be saved
     * @param      format one of EasyBufferedImage.PNG, EasyBufferedImage.GIF or EasyBufferedImage.JPEG
     * @return     None
     * @throws IOException if the file cannot be created.
     */
    public void save(String filename, int format) throws IOException {
	if(format != JPEG && format != PNG && format != GIF) throw new IllegalArgumentException("Type must be either GIF, PNG, or JPEG");
	ImageIO.write(this, TYPES[format], new File(filename));
    }


    /**
     * A demo application that shows simple ways of using the EasyBufferedImage class.  Execute from the command line
     * by typing  
     * <p><b><blockquote><pre>java EasyBufferedImage ImageFileName AlphaValue</pre></blockquote></b>
     * where the <tt>AlphaValue</tt> is a percentage value in the range [0..100] and serves as a brightness control.  Values in the range
     * [0..1) will darken the image while values above 1 will brighten the input image.  This application also saves the processed
     * file in GIF format to a file named "Sample.gif".
     * <b><font color="#115500"><blockquote><pre>
    public static void main(String[] args) throws IOException {
	EasyBufferedImage image = EasyBufferedImage.createImage(args[0]);
	double alpha = Double.parseDouble(args[1]);
	EasyBufferedImage copy = image.copy();

	int[][] data = image.getPixels2D(EasyBufferedImage.RED);
	for(int i=0; i &lt image.getHeight(); i++){ 
	    for(int j=0; j &lt image.getWidth(); j++){ 
		data[i][j] = clamp(data[i][j] * alpha);
	    }
	}
	image.setPixels(data, EasyBufferedImage.RED);

	data = image.getPixels2D(EasyBufferedImage.GREEN);
	for(int i=0; i &lt image.getHeight(); i++){ 
	    for(int j=0; j &lt image.getWidth(); j++){ 
		data[i][j] = clamp(data[i][j] * alpha);
	    }
	}
	image.setPixels(data, EasyBufferedImage.GREEN);

	data = image.getPixels2D(EasyBufferedImage.BLUE);
	for(int i=0; i &lt image.getHeight(); i++){ 
	    for(int j=0; j &lt image.getWidth(); j++){ 
		data[i][j] = clamp(data[i][j] * alpha);
	    }
	}
	image.setPixels(data, EasyBufferedImage.BLUE);
	image.show(args[0]);

	data = copy.getPixels2D(EasyBufferedImage.GRAY);
	for(int i=0; i &lt copy.getHeight(); i++){ 
	    for(int j=0; j &lt copy.getWidth(); j++){ 
		data[i][j] = clamp(data[i][j] * alpha);
	    }
	}
	copy.setPixels(data, EasyBufferedImage.GRAY);
	copy.show("GRAY SCALE PROCESSING");
	
	image.save("Sample.gif", EasyBufferedImage.GIF);
    }
     </pre></blockquote></font></b>
    * @param args is an array of two strings.  The first is the filename of the file to process and the second is a perctage value to brighten/darken an image.
    * @throws IOException.
    */
    public static void main(String[] args) throws IOException {
	EasyBufferedImage image = EasyBufferedImage.createImage(args[0]);
	double alpha = Double.parseDouble(args[1]);
	EasyBufferedImage copy = image.copy();
	image.show("Original Image");

	int[][] data = image.getPixels2D(EasyBufferedImage.RED);
	for(int i=0; i<image.getHeight(); i++){ 
	    for(int j=0; j<image.getWidth(); j++){ 
		data[i][j] = clamp(data[i][j] * alpha);
	    }
	}
	image.setPixels(data, EasyBufferedImage.RED);

	data = image.getPixels2D(EasyBufferedImage.GREEN);
	for(int i=0; i<image.getHeight(); i++){ 
	    for(int j=0; j<image.getWidth(); j++){ 
		data[i][j] = clamp(data[i][j] * alpha);
	    }
	}
	image.setPixels(data, EasyBufferedImage.GREEN);

	data = image.getPixels2D(EasyBufferedImage.BLUE);
	for(int i=0; i<image.getHeight(); i++){ 
	    for(int j=0; j<image.getWidth(); j++){ 
		data[i][j] = clamp(data[i][j] * alpha);
	    }
	}
	image.setPixels(data, EasyBufferedImage.BLUE);
	image.show(args[0]);

	data = copy.getPixels2D(EasyBufferedImage.GRAY);
	for(int i=0; i<copy.getHeight(); i++){ 
	    for(int j=0; j<copy.getWidth(); j++){ 
		data[i][j] = clamp(data[i][j] * alpha);
	    }
	}
	copy.setPixels(data, EasyBufferedImage.GRAY);
	copy.show("Gray Scale Processing");
	
	image.save("Sample.gif", EasyBufferedImage.GIF);
    }
}
