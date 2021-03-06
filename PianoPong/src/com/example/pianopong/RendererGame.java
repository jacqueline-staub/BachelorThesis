package com.example.pianopong;

import java.util.ArrayList;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class RendererGame implements android.opengl.GLSurfaceView.Renderer {
	
	private static GL10 ogl;
	private static Context context;
	
	private Token token;
	
	//Song object to show
	private Song song = GameActivity.song;
	private ArrayList<Sprite> removableNotes = new ArrayList<Sprite>();
	private ArrayList<Sprite> removablePlayerNotes = new ArrayList<Sprite>();
	
	//health
	private Word health;
	private Word experience;
	private Word gameOver;
	private Word congratulation;
	private Word level;
	
	//health bar
	private float onePercentBar;
	private Sprite bar;
	private Sprite val;
	
	//sprite for immediate response to wrong note
	private static Sprite x;
	
	//experience bar
	private Sprite xpbar;
	private Sprite xpval;
	
	//left and right background picture 
	private Sprite left;
	private Sprite right;
	
	//back, next und retry
	private Sprite next;
	private Sprite back;
	private Sprite retry;
	
	//upper token
	private Sprite tokenDownAbove;
	private Sprite tokenUpAbove;
	
	//lower token
	private Sprite tokenDownBelow;
	private Sprite tokenUpBelow;
	
	//upper and lower lines, as well as play balken
	private static Sprite linesAbove;
	private static Sprite linesBelow;
	//private Sprite balken;TODO
	
	//violin and bass key
	private Sprite violin;
	private Sprite bass;
	
	//wrong and right note
	private static Sprite wrong;
	private static Sprite correct;
	
	//representation of Sprites for the notes
	private ArrayList<Sprite> notes = new ArrayList<Sprite>();
	private static ArrayList<Sprite> notesPlayed = new ArrayList<Sprite>();
	private static ArrayList<Sprite> userInfo = new ArrayList<Sprite>();
	
	//display size, position and position before (following notes, starting from 1, 0 is the key)
	private Point display_size;
	private static Point ds = new Point();
	private float pos;
	private int positionBefore = 1;
	
	//rotation of notes and y coordinate of objects 
	private float rot;
	private int y;
	
	//for the movement of the background and the notes
	private int time = 0;
	public static int backgroundVelocity = 6;
	
	//size of the items, movement of the token
	private int noteScalingFactor = 8;
	private boolean isUp = false;
	private static int numberOfPixelsPerHalfNote;
	
	//which one are full notes and which one are half notes, modulo 12 beginning with C
	private static boolean[] halfnotes = {false,true,false,true,false,false,true,false,true,false,true,false};

	public RendererGame(Context context){
		this.context = context;
		notesPlayed.clear();
		token = new Token(context);

		// Calculate dimensions, sizes, positions:
		display_size = new Point();
		Display display = ((GameActivity) context).getWindowManager().getDefaultDisplay();
		display.getSize(this.display_size);
		ds = display_size;
	}
	
	private void goToLevel(int level, int difficulty){
		//same effect as onestroy 
		((Activity)context).finish();
		
		Intent i = new Intent(context, GameActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("level", level);
        extras.putInt("difficulty", difficulty);
        i.putExtras(extras);
        context.startActivity(i);
	}
	
	public void ontouch(float x, float y){
		if(token.getHealth()<=0 || song.isEmpty() && notes.isEmpty() && notesPlayed.isEmpty()){
			
			//number of levels in this category, -1 because I need it as the index
			int numLevelsInCat = MainActivity.lm.getLevelsForCategory(GameActivity.difficulty).size()-1;
			
			if(x>=back.position.x-back.size.x/2 && x<=back.position.x+back.size.x/2 && y>=back.position.y-back.size.y/2 && y<=back.position.y+back.size.y/2){
				((Activity)context).finish();
			}else if(x>=retry.position.x-retry.size.x/2 && x<=retry.position.x+retry.size.x/2 && y>=retry.position.y-retry.size.y/2 && y<=retry.position.y+retry.size.y/2){
				goToLevel(GameActivity.level,GameActivity.difficulty);
			}else if(x>=next.position.x-next.size.x/2 && x<=next.position.x+next.size.x/2 && y>=next.position.y-next.size.y/2 && y<=next.position.y+next.size.y/2){
				//only if not game over
				if(token.getHealth()>0){
					if(GameActivity.level==numLevelsInCat && GameActivity.difficulty==4){
						 goToLevel(numLevelsInCat,4);
					}else if(GameActivity.difficulty==5 && GameActivity.level==numLevelsInCat){
						goToLevel(numLevelsInCat,5);
					}else if(GameActivity.level==numLevelsInCat){
						goToLevel(0,GameActivity.difficulty+1);
					}else{
						goToLevel(GameActivity.level+1,GameActivity.difficulty);
					}
				}
			}
		}
	}
	
	@Override
	public void onDrawFrame(GL10 gl){
		//update ogl instance
		ogl = gl;
		
		//clear screen and depth buffer
		ogl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		//enable client state on vertex array and texture array
		ogl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		ogl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		//vertecies in vertex and texture are defined clockwise
		ogl.glFrontFace(GL10.GL_CW);
		//enable depth testing
		ogl.glEnable(GL10.GL_DEPTH_TEST);
		
		//game over
		if(token.getHealth()<=0){
			//draw both background images
			drawBackground();
			
			//game over text
			gameOver.draw(ogl);
			
			drawBackRetryNext();
			GameActivity.running = false;
			
			//save user data (level and experience)
			token.storeToPrefs(context, token.getPoints());
			
			//adjust position of retry and back, because next is missing
			retry.position.x =display_size.x/2 + display_size.x/20;
			back.position.x = display_size.x/2 - display_size.x/20;
		}
		//won the game and no more notes visible
		else if(song.isEmpty() && notes.isEmpty() && notesPlayed.isEmpty()){
			
			//draw both background images
			drawBackground();
			
			//inform user
			congratulation.draw(ogl);
			
			drawBackRetryNext();
			GameActivity.running = false;
			
			//draw health and experience values as diagram
			drawHP();
			drawXP();
			experience.draw(ogl);
			health.draw(ogl);
			level.draw(ogl);
			
			token.increaseExperience(song.getEarnings());
			song.setEarnings(0);
			
			//save user data (level and experience)
			token.storeToPrefs(context, token.getPoints());
		}else{//just playing
			//check collision
			checkExplosionCollabs();
			checkCollision(tokenUpAbove.position.x);
			checkUserInfo();
			
			//clean up
			notes.removeAll(removableNotes);
			notesPlayed.removeAll(removablePlayerNotes);
			
			//draw both background images
			drawBackground();
			
			//draw balken
			//balken.draw(ogl);TODO
			
			//draw health, experience and level
			experience.draw(ogl);
			health.draw(ogl);
			level.draw(ogl);
			
			//draw health and experience values as diagram
			drawHP();
			drawXP();
			
			//draw both violin and bass lines
			drawLine();
			
			//draw bass and violin key
			drawKey(bass);
			drawKey(violin);
			
			//draw all elements in association with the notes (helperlines, notes, dots, kreuz)
			for(int i=0; i<notes.size(); i++){
				if(notes.get(i)!=null){
					drawNotes(notes.get(i));
				}
			}
			
			//draw all notes from notesHeard, the notes the player played
			for(int i=0; i<notesPlayed.size(); i++){
				if(notesPlayed.get(i)!=null){
					drawNotes(notesPlayed.get(i));
				}
			}

			//draw both upper and lower token
			drawToken(6);
			
			//draw X if there is something in userInfo
			if(!userInfo.isEmpty()){
				x.draw(ogl);
			}
		}
		
		//disable client state access to vertex array and texture array
		ogl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		ogl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	private void checkUserInfo() {
		if(!userInfo.isEmpty()){
			userInfo.remove(0);
		}
		
	}

	private void drawXP() {
		xpval.size.x = (int)(token.getExperience()*onePercentBar);
		xpval.position.x = (int) ((display_size.x/40 + display_size.x/7)-(100-token.getExperience())*onePercentBar/2);
		xpbar.draw(ogl);
		xpval.draw(ogl);
	}

	private void drawHP() {
		val.size.x = (int)(token.getHealth()*onePercentBar);
		val.position.x = (int) ((display_size.x-display_size.x/9)-(100-token.getHealth())*onePercentBar/2);
		bar.draw(ogl);
		val.draw(ogl);
	}
	
	private void checkExplosionCollabs() {
		for(Sprite measurement: notesPlayed){
			if(measurement!=null){
				for(Sprite note: notes){
					if(note!= null && note.kind==0 && measurement.position.x>=note.position.x && note.position.x!=0){
						if(measurement.kind==4){//collabs
							int x = note.position.x;
							for(Sprite s: notes){
								if(Math.abs(s.position.x-x)<=s.size.x/2){
									removableNotes.add(s);
								}
							}
						}else if(measurement.kind==5){//explosion, make damage
							int damage = 5;
							token.makeDamage(damage);
						}
						removablePlayerNotes.add(measurement);
						token.increaseExperience(1);
					}
				}
			}
		}
	}
	
	public static void correctlyPlayed(Note note){
		Sprite noteSprite = prepareNote(note.isUp(), correct, note);
		noteSprite.kind = 4;
		notesPlayed.add(noteSprite);
		
	}
	
	public static void incorrectlyPlayed(Note note){
		Sprite noteSprite = prepareNote(note.isUp(), wrong, note);
		noteSprite.kind = 5;
		notesPlayed.add(noteSprite);
		//do not add another x if there is already displayed one, otherwise it takes to long to go away
		if(userInfo.isEmpty()){
			for(int i=0; i<15; i++){
				userInfo.add(x);
			}
		}
	}

	private static Sprite prepareNote(boolean upper, Sprite s, Note note) {
		int y;
		int ref;
		int posLines;
		Sprite noteSprite = s.deepCopy();
		
		if(upper){
			posLines = linesAbove.position.y;
			ref = 65;
		}else{
			posLines = linesBelow.position.y;
			ref = 45;
		}
		
		int mem;
		if(note.getPitch()<0){
			mem = note.getPitch();
		}else if(halfnotes[note.getPitch()%12]){
			mem = pitchesAwayFromReference(note.getPitch()-1, ref);
		}else{
			mem =  pitchesAwayFromReference(note.getPitch(), ref);
		}
		
		if(note.getPitch()<0){//breaks are not played
			y = posLines+ 3*numberOfPixelsPerHalfNote;
		}else{
			y = posLines+(mem)*numberOfPixelsPerHalfNote;
		}
		
		noteSprite.position.y = y-4;
		return noteSprite;
	}

	private void checkCollision(int x) {
		for(Sprite note: notes){
			//if its a note and its position is behind the token
			if(note.position.x<=x && note.position.x!=0 && note.kind==0){
				//find shortest of the front notes
				ArrayList<Note> first = song.getAllFirstNotes();
				float l = Float.MAX_VALUE;
				for(Note n: first){
					if(n.getLength()<l){
						l=n.getLength();
					}
				}
				
				//remove note in case of collision with token
				ArrayList<ArrayList<Note>> phra = song.getPhrases();
				for (int i=0; i<phra.size(); i++){
					ArrayList<Note> p = phra.get(i);
					if(p.get(0).getLength() > l){
						Note n = new Note(p.get(0).getPitch(), (int)p.get(0).getLength()-l, i==0);
						p.set(0, n);
					}else{
						p.remove(0);
					}
				}
				
				//remove additional items
				for(Sprite n: notes){
					if(Math.abs(n.position.x-note.position.x) <= n.size.x/2){
						removableNotes.add(n);
					}
				}
				
				//make damage
				token.makeDamage(10);
				break;
			}
		}		
	}

	private void drawKey(Sprite keyObject) {
		//if isVisible and still existing
		if(keyObject!=null && display_size.x-keyObject.time>=0){
			keyObject.draw(ogl, display_size.x-keyObject.time, keyObject.position.y, keyObject.size.x, keyObject.size.y, 0);
		}else{
			keyObject = null;
		}
				
	}

	private void drawNotes(Sprite noteObject) {
		//65 equals a f4 in midi notation, and it is here the same as 0 deviation from the lines position
		//make that quarter use always the same space, so access unit note from MusicController
		int x;
		if(noteObject.kind==0 || noteObject.kind==2){//normal note or helper line
			x = display_size.x-noteObject.time+((int)(display_size.x/(5/MusicController.unit)))*noteObject.num;
		}else if(noteObject.kind == 1){//cross
			x = display_size.x-noteObject.time+((int)(display_size.x/(5/MusicController.unit)))*noteObject.num-noteObject.size.x/2;
		}else if(noteObject.kind==3){//dot
			x = display_size.x-noteObject.time+((int)(display_size.x/(5/MusicController.unit)))*noteObject.num+noteObject.size.x/2;
		}else if(noteObject.kind==4 || noteObject.kind==5){//player note
			x = noteObject.time + tokenUpAbove.position.x;
		}else{
			x = -1;
		}
		
		//set the position to this x
		noteObject.position.x = x;
		
		//if is visible and still existing
		if(noteObject.kind==2){//it is a helperLine
			noteObject.draw(ogl, x, noteObject.position.y, noteObject.size.x, noteObject.size.y, noteObject.rot);
		}else if(noteObject!=null && x>=0){
			noteObject.draw(ogl, x, noteObject.position.y, this.display_size.x/(noteScalingFactor*4), this.display_size.y/noteScalingFactor, noteObject.rot);
		}else{
			noteObject = null;
		}
	}

	private void drawLine() {
		linesAbove.draw(ogl);
		linesBelow.draw(ogl);
	}

	private void drawBackground() {
		left.draw(ogl, this.display_size.x/2-time, this.display_size.y/2, this.display_size.x, this.display_size.y, 0);
		right.draw(ogl, this.display_size.x*3/2-time, this.display_size.y/2, this.display_size.x, this.display_size.y, 0);
		
		
		if(time>=this.display_size.x){
			time=0;
		}else{
			time +=backgroundVelocity;
		}
	}
	
	private void drawBackRetryNext(){
		//don't show next button, if the player didn't win
		if(token.getHealth()<=0){
			back.draw(ogl);
			retry.draw(ogl);
		}else{
			back.draw(ogl);
			retry.draw(ogl);
			next.draw(ogl);
		}
	}

	private void drawToken(int frequencyChangeMove) {
		
		if(isUp){
			tokenUpAbove.draw(ogl, pos);
			tokenUpBelow.draw(ogl, pos);
		}else{
			tokenDownAbove.draw(ogl, pos);
			tokenDownBelow.draw(ogl, pos);
		}
		
		//update isUp for next iteration
		if(time%(frequencyChangeMove*backgroundVelocity)==0){
			isUp = !isUp;
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		//update ogl instance
		ogl = gl;
		
		ogl.glMatrixMode(GL10.GL_PROJECTION); //select the projection matrix
		ogl.glLoadIdentity();

		ogl.glOrthof(0, width, height, 0, 0, 1);
		ogl.glViewport(0, 0, width, height);

		ogl.glMatrixMode(GL10.GL_MODELVIEW);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
		//set ogl instance to gl
		ogl = gl;
		
		//TODO change to power of two, adjust 5 in case more backgrounds
		int levelnum = GameActivity.levelNum%5;
		int backgroundX = 800;
		int backgroundY = 1024;
		
		int noteSize = display_size.y/noteScalingFactor;
		onePercentBar = display_size.x/5/128f*126/100f;
		
		left = new Sprite(gl, context, "background"+levelnum, this.display_size.x/backgroundX, this.display_size.y/backgroundY);
		right = new Sprite(gl, context, "background"+levelnum,this.display_size.x/backgroundX, this.display_size.y/backgroundY);
		
		x = prepare(x, "x", display_size.x/2, display_size.y/2, display_size.y/3, display_size.y/3);
		//balken = prepare(balken, "balken", display_size.x/2, display_size.y*3/8, display_size.y/4, display_size.y/2);TODO
		tokenUpAbove = prepare(tokenUpAbove, "token_up", display_size.x/8, display_size.y/4, display_size.y/4, display_size.y/4);//display_size.x/2
		tokenDownAbove = prepare(tokenDownAbove, "token_down", display_size.x/8, display_size.y/4, display_size.y/4, display_size.y/4);
		tokenUpBelow = prepare(tokenUpBelow, "token_up", display_size.x/8, display_size.y/2, display_size.y/4, display_size.y/4);
		tokenDownBelow = prepare(tokenDownBelow, "token_down", display_size.x/8, display_size.y/2, display_size.y/4, display_size.y/4);
		linesAbove = prepare(linesAbove, "lines", display_size.x/2, display_size.y/4, display_size.x, noteSize);
		linesBelow = prepare(linesBelow, "lines", display_size.x/2, display_size.y/2, display_size.x, noteSize);
		
		bar = prepare(bar, "bar", display_size.x-display_size.x/9, 50, display_size.x/5, 50);
		val = prepare(val, "healthbar", display_size.x-display_size.x/9, 50, (int)(display_size.x/5/128f*126), (int)(50-50/128f*4));
		xpbar = prepare(xpbar, "experiencebar", display_size.x/40 + display_size.x/7, 50, display_size.x/5, 50);
		xpval = prepare(xpval, "xpval", (int) ((100-token.getExperience())*onePercentBar/2), (int)(50/128f*126), (int)(token.getExperience()*onePercentBar), (int)(50-50/128f*4));
		
		wrong = prepare(wrong, "wrong", display_size.x/8, 96, display_size.x/5, 50);
		correct = prepare(correct, "right", display_size.x/8, 96, display_size.x/5, 50);
		next = prepare(next, "next", display_size.x/2+display_size.x/10, display_size.x/5, display_size.x/10, display_size.x/10);
		back = prepare(back, "back", display_size.x/2-display_size.x/10 , display_size.x/5, display_size.x/10, display_size.x/10);
		retry = prepare(retry, "retry", display_size.x/2, display_size.x/5, display_size.x/10, display_size.x/10);
		
		//prepare Strings, represented as words
		level = health = new Word("level", gl, context, "Level "+String.valueOf(MainActivity.lm.getLevelNumber(GameActivity.difficulty, GameActivity.level)+1), "alphabet", new Point(display_size.x/40,display_size.y/40), new Point(5*display_size.x/12, 50));
		health = new Word("health", gl, context, "HP:", "alphabet", new Point(display_size.x/40,display_size.y/40), new Point(3*display_size.x/4, 50));
		experience = new Word("experience", gl, context, "XP:", "alphabet", new Point(display_size.x/40,display_size.y/40), new Point(50, 50));
		gameOver = new Word("gameOver", gl, context, "Game Over", "alphabet", new Point(display_size.x/40,display_size.y/40), new Point(display_size.x/2-display_size.x/10, display_size.y/2));
		congratulation = new Word("congratulation", gl, context, "congratulation", "alphabet", new Point(display_size.x/40,display_size.y/40), new Point(display_size.x/2-display_size.x/7, display_size.y/2));
		
		//the head of the note takes 1/4 of the 64bits in the texture, so the middle is around 1/8. -1 because not aligned to bottom line
		numberOfPixelsPerHalfNote = -(linesAbove.size.y/8-1);
		
		for (int i=0; i<song.getPhrases().size(); i++) {
			
			for(int j=1; j<song.getPhrases().get(i).size()+1; j++){
				Note note = song.getPhrases().get(i).get(j-1);
				float length = note.getLength();
				String texture_note = "";
				//set texture in case of a note
				if(note.getPitch()>0){//it's a note
					if(length == 0.25 || length == 0.375){
						texture_note = "sechzehntel";
					}else if(length == 0.5 || length == 0.75){
						texture_note = "achtel";
					}else if(length == 1 || length == 1.5){
						texture_note = "quarter";
					}else if(length == 2 || length == 3){
						texture_note = "half";
					}else if(length == 4 || length == 6){
						texture_note = "full";
					}
				}
				//set texture in case of a rest
				else{//it's a rest
					if(length == 0.25){
						texture_note = "sixteen_pause";
					}else if(length == 0.5){
						texture_note = "eight_pause";
					}else if(length == 1){
						texture_note = "one_pause";
					}else if(length == 2){
						texture_note = "two_pause";
					}else if(length == 4){
						texture_note = "four_pause";
					}
				}
				//find position, according to pitch
				if(i==0){
					texture_note = setYposition(noteSize, i, note, texture_note, linesAbove, 71, 65);
				}else{
					texture_note = setYposition(noteSize, i, note, texture_note, linesBelow, 50, 45);
				}
				
				if(length==0.375 || length==0.75 || length==1.5 || length==3 || length==6){
					//this note uses a dot
					Sprite dot = new Sprite(gl, context, "dot");
					dot.size = new Point(display_size.x/20, noteSize);
					dot.position = new Point(0, y-4);
					dot.num = positionBefore;
					dot.kind = 3;
					dot.rot = rot;
					notes.add(dot);
				}
				
				if(!texture_note.isEmpty()){
					Sprite noteSprite = new Sprite(gl, context, texture_note);
					noteSprite.size = new Point(display_size.x/20, noteSize);
					noteSprite.position = new Point(0, y-4);
					noteSprite.num = positionBefore;
					noteSprite.rot = rot;
					notes.add(noteSprite);
				}
				
				positionBefore = positionBefore+ (int)(note.getLength()/MusicController.unit);
				
				//reset rotation for the next note
				rot=0;
			}
			positionBefore = 1;
		}
		
		this.violin = new Sprite(gl, context, "violin");
		violin.position = new Point(display_size.x/3, display_size.y/4);
		violin.size = new Point(display_size.x/30, noteSize);
		
		this.bass = new Sprite(gl, context, "bass");
		bass.position = new Point(display_size.x/3, display_size.y/2);
		bass.size = new Point(display_size.x/15, noteSize);
		
		//set background color to black
		gl.glClearColor(0f, 0f, 0f, 1f);
		//disable dithering, better for performance and not needed
		gl.glDisable(GL10.GL_DITHER);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		//enable smooth shading
		gl.glShadeModel(GL10.GL_SMOOTH);
		//setup depth buffer
		gl.glClearDepthf(1.0f);
		//enable depth tesing
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);	
		//fast perspective calculation, need fast respondance
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		
		sortOutUnneededBreaks();
	}

	private void sortOutUnneededBreaks() {
		//TODO		
	}

	private Sprite prepare(Sprite token, String texture, int posX, int posY, int sizeX, int sizeY) {
		token = new Sprite(ogl, context, texture);
		token.position = new Point(posX, posY);
		token.size = new Point(sizeX, sizeY);
		return token;
	}

	private String setYposition(int noteSize, int i, Note note, String texture_note, Sprite lines, int turn, int ref) {
		helperLinesNeeded(i, note.getPitch(), positionBefore);
		if(note.getPitch()<0){
			y = lines.position.y;
		}else if(note.getPitch()>=turn){
			if(texture_note == "sechzehntel"|| texture_note == "achtel"){
				texture_note +="_oben";
			}
			rot = 180;
			int mem = findNextPitch(note.getPitch(),noteSize,lines, true, ref);
			y = lines.position.y+(mem+1)*numberOfPixelsPerHalfNote+7*lines.size.y/8;
		}else{
			int mem = findNextPitch(note.getPitch(),noteSize,lines, true, ref);
			y = lines.position.y+(mem)*numberOfPixelsPerHalfNote;
		}
		return texture_note;
	}

	private void helperLinesNeeded(int i, int pitch, int num) {
		if(pitch>0){//not for breaks
			if(i==0){//upper lines
				int n=1;
				int m=-1;
				if(pitch==61){
					getHelperLine(m, linesAbove, num);
					pitch++;
				}else{
					while(pitch>=81){
						if(pitch%12 == 0 | pitch%12 == 4 | pitch%12 == 9){
							getHelperLine(n, linesAbove, num);
							n++;
						}
						pitch--;
					}
					while(pitch<=60){
						if(pitch%12 == 0 | pitch%12 == 4 | pitch%12 == 9){
							getHelperLine(m, linesAbove, num);
							m--;
						}
						pitch++;
					}
				}
			}else{//lower lines
				int n=1;
				int m=-1;
				if(pitch==39){
					getHelperLine(m, linesBelow, num);
					pitch++;
				}else{
					while(pitch>=60){
						if(pitch%12 == 0 | pitch%12 == 4 | pitch%12 == 9){
							getHelperLine(n, linesBelow, num);
							n++;
						}
						pitch--;
					}
					while(pitch<=40){
						if(pitch%12 == 0 | pitch%12 == 4 | pitch%12 == 9){
							getHelperLine(m, linesBelow, num);
							m--;
						}
						pitch++;
					}
				}
			}
		}
	}

	private int findNextPitch(int pitch, int noteSize, Sprite above, boolean swap, int referencePitch) {
		if(pitch<0){
			return pitch;
		}else if(halfnotes[pitch%12]){
			int pitchesAway = pitchesAwayFromReference(pitch-1, referencePitch);
			
			//generate kreuz
			Sprite kreuz = new Sprite(ogl, context, "kreuz");
			kreuz.size = new Point(display_size.x/20, noteSize);
			int yc = above.position.y+pitchesAway*numberOfPixelsPerHalfNote - above.size.y/2;
			if(swap){
				yc += 7*above.size.y/8;
			}
			kreuz.position = new Point(0, yc);
			kreuz.num = positionBefore;
			kreuz.kind = 1;
			notes.add(kreuz);
			
			return pitchesAway;
		}else{
			return pitchesAwayFromReference(pitch, referencePitch);
		}
	}
	
	private static int pitchesAwayFromReference(int pitch, int referencePitch) {
		int diff=0;
		if(pitch>referencePitch){
			while(pitch!=referencePitch){
				if(!halfnotes[pitch%12]){
					diff++;
				}
				pitch--;
			}
		}else{
			while(pitch!=referencePitch){
				if(!halfnotes[pitch%12]){
					diff--;
				}
				pitch++;
			}
		}
		return diff;
	}

	private void getHelperLine(int nOfHelperLine, Sprite lines, int num){
		Sprite lineHelper = new Sprite(ogl, context, "line");
		lineHelper.size = new Point(display_size.x/25, lines.size.y/4);
		lineHelper.kind = 2;
		lineHelper.num = num;
		if(nOfHelperLine<0){//helperlines below lowest line
			lineHelper.position = new Point(0,lines.position.y+lines.size.y/2-nOfHelperLine*lineHelper.size.y/2-(nOfHelperLine+1)*lineHelper.size.y/2);
		}else{//helperline above to highest line
			lineHelper.position = new Point(0, lines.position.y-lines.size.y/2-5*lineHelper.size.y/14-nOfHelperLine*lineHelper.size.y/2-(nOfHelperLine-1)*lineHelper.size.y/2);
		}
		notes.add(lineHelper);
	}
}

