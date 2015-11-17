package com.example.pianopong;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.opengl.GLUtils;

public class Sprite{
	
	private FloatBuffer vertBuf;
	private FloatBuffer textureBuf;
	private int[] textures;
	private ShortBuffer pBuf;
	private int texture_index=0; 
	private int REPETITION_MODE = GL10.GL_REPEAT;
	public int time = 0;
	public float rot = 0;
	public int num;
	public Point position = new Point();
	public Point size = new Point();
	//kind: 0=note, 1=cross, 2= point, 3=b, 4=aufloesezeichen
	public int kind = 0;
	
	public Sprite deepCopy(){
		Sprite ret = new Sprite();
		ret.kind = kind;
		ret.num = num;
		ret.pBuf = pBuf;
		ret.position.x = position.x;
		ret.position.y = position.y;
		ret.REPETITION_MODE = REPETITION_MODE;
		ret.rot = rot;
		ret.size.x = size.x;
		ret.size.y = size.y;
		ret.texture_index = texture_index;
		ret.textureBuf = textureBuf;
		ret.textures = textures;
		ret.time = time;
		ret.vertBuf = vertBuf;
		return ret;
	}
	
	public Sprite(){
		
	}
	
	public Sprite(GL10 gl, Context ctx, String image_name){
		this(gl, ctx, image_name, 1, 1, 1);
	}
	
	public Sprite(GL10 gl, Context ctx, String image_name1, String image_name2){
		this(gl, ctx, image_name1, 1, 1, 2);
		loadGLTexture(gl, ctx, image_name2, 1);
	}
	
	public Sprite(GL10 gl, Context ctx, String image_name, int repeats_x, int repeats_y){
		this(gl, ctx, image_name, repeats_x, repeats_y, 1);
	}
	
	public Sprite(GL10 gl, Context ctx, String image_name, int repeats_x, int repeats_y, int num_of_textures){
		
		this.textures = new int[num_of_textures];
		gl.glGenTextures(num_of_textures,textures, 0);
	
		// Create texture vertices (depending on texture repetition)
		float[] texture_vertices = {
				0f,0,
				0f,1f * repeats_y,
				1f * repeats_x,1f * repeats_y, 
				1f * repeats_x,0					
			};
		
		// Define vertices of square
		float vertices[] = {
			0,0,0,
			0,1f,0,
			1f,1f,0,
			1f,0,0
		};
		
		//Order of vertices on square
		short pIndex[] = {0,1,2,3};
		//allocate memory for all floats in vertices, each 4 byte (1 float = 4 byte)
		ByteBuffer bbuf = ByteBuffer.allocateDirect(vertices.length*4);
		//rearrange bytes in bbuf to the native systems order (little/big endian)
		bbuf.order(ByteOrder.nativeOrder());
		//declare it as a FloatBuffer, put values, define where to start read
		vertBuf = bbuf.asFloatBuffer();
		vertBuf.put(vertices);
		vertBuf.position(0);
		
		//same for texture
		bbuf = ByteBuffer.allocateDirect(texture_vertices.length*4);
		bbuf.order(ByteOrder.nativeOrder());
		textureBuf = bbuf.asFloatBuffer();
		textureBuf.put(texture_vertices);
		textureBuf.position(0);
		
		//same for orientation buffer
		ByteBuffer pbuf = ByteBuffer.allocateDirect(pIndex.length*2);
		pbuf.order(ByteOrder.nativeOrder());
		pBuf = pbuf.asShortBuffer();
		pBuf.put(pIndex);
		pBuf.position(0);
		
		
		// Call to load the given texture (image):
		loadGLTexture(gl, ctx, image_name, 0);
	}
	
	public Sprite(GL10 gl, Context ctx){
		
		// Define vertices of square
		float vertices[] = {
			0,0,0,
			0,1f,0,
			1f,1f,0,
			1f,0,0
		};
		
		//Order of vertices on square
		short pIndex[] = {0,1,2,3};
		
		//allocate memory for all floats in vertices, each 4 byte (1 float = 4 byte)
		ByteBuffer bbuf = ByteBuffer.allocateDirect(vertices.length*4);
		//rearrange bytes in bbuf to the native systems order (little/big endian)
		bbuf.order(ByteOrder.nativeOrder());
		//declare it as a FloatBuffer, put values, define where to start read
		vertBuf = bbuf.asFloatBuffer();
		vertBuf.put(vertices);
		vertBuf.position(0);
		
		//same for orientation buffer
		ByteBuffer pbuf = ByteBuffer.allocateDirect(pIndex.length*2);
		pbuf.order(ByteOrder.nativeOrder());
		pBuf = pbuf.asShortBuffer();
		pBuf.put(pIndex);
		pBuf.position(0);
	}
	
	public void set_texture_margins(float left, float top, float right, float bottom) {
		
		// Create texture vertices (depending on texture repetition)
		float[] texture_vertices = {
				left,top,
				left,1f - bottom,
				1f - right, 1f - bottom,  
				1f - right, top					
			};
				
		//same for texture
		ByteBuffer bbuf = ByteBuffer.allocateDirect(texture_vertices.length*4);
		bbuf.order(ByteOrder.nativeOrder());
		textureBuf = bbuf.asFloatBuffer();
		textureBuf.put(texture_vertices);
		textureBuf.position(0);
	}
	
	public void loadGLTexture(GL10 gl, Context context, String image_name, int texture_index){
		
		// Resolve name to id:
		int resID= context.getResources().getIdentifier("drawable/" + image_name, null, context.getPackageName());
		
		// Create bitmap options:
		BitmapFactory.Options options = new BitmapFactory.Options ();
        options.inScaled = false;  // No pre-scaling

        // Load the resource into the bitmap:
        // IMPORTANT: For the texture to be repeatable it needs to of size 2^t (t in |N)
        InputStream input = context.getResources().openRawResource (resID);
        Bitmap bitmap;
        try{
            bitmap = BitmapFactory.decodeStream (input, null, options);
        }
        finally{
            try{
                input.close ();
            }
            catch (IOException e){
                // Ignore.
            }
        }
        
		//gl.glGenTextures(1,textures, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[texture_index]);
		//gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		//GL_TEXTURE_2D, 0, GL_RGB, 2, 2, 0, GL_RGB, GL_FLOAT, pixels)
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
	}
	
	public void draw(GL10 gl, int posX, int posY, int width, int height, float rotation, int texture_index){
		this.texture_index = texture_index;
		draw(gl, posX, posY, width, height, rotation);
	}
	
	public void draw(GL10 gl) {
		draw(gl, this.position.x, this.position.y, this.size.x, this.size.y, 0);
	}
	
	public void draw(GL10 gl, float rotation) {
		draw(gl, this.position.x, this.position.y, this.size.x, this.size.y, rotation);
	}

	public void draw(GL10 gl, int posX, int posY, int width, int height, float rotation){
		
		time+=RendererGame.backgroundVelocity;
		
		gl.glEnable (GL10.GL_BLEND);
		gl.glBlendFunc (GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glLoadIdentity();
		
		// Scale, translate to origin, rotate, translate to destination
		// IMPORTANT: These operations are done in reverse order!
		gl.glTranslatef(posX, posY, 0);
		gl.glRotatef(rotation, 0, 0, 1);
		gl.glTranslatef(-width/2, -height/2, 0);
		gl.glScalef(width, height, 0.0f);
		
		//bind texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[texture_index]);
		
		// Set texture repetition:
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, this.REPETITION_MODE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, this.REPETITION_MODE);
		
		//define values in vertBuf to be our vertices, 3d since every vertex is defined using 3 components
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertBuf);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuf.position(0));
		// Draw primitives
		gl.glDrawElements(gl.GL_TRIANGLE_FAN, pBuf.capacity(), GL10.GL_UNSIGNED_SHORT, pBuf);
	}
	
	public void set_texture_index(int index) {
		this.texture_index = index;
	}
	
	public void set_repetition_mode(int mode) {
		this.REPETITION_MODE = mode;
	}
}