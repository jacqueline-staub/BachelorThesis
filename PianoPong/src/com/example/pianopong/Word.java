package com.example.pianopong;

import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Point;

public class Word{
	
	public Sprite letters[];	// Letters are common sprites with some parameters set. See constructor
	private String text;		// Contains the text this word should represent
	private char alphabet[];	// The standard alphabet is generated in the constructor, can be overwritten later
	
	public Word(String name, GL10 gl, Context context, String word_text, String letter_texture, Point size, Point position) {
		
		initialize_alphabet();
		this.text = word_text;
		
		// Initialize array of letters:
		letters = new Sprite[text.length()];
		
		int pos;
		
		// Create each letter with the correct part of texture:
		for (int i = 0; i < text.length(); i++) {
			pos = String.valueOf(alphabet).indexOf(text.charAt(i));
			letters[i] = new Sprite(gl, context, "alphabet");
			letters[i].set_repetition_mode(GL10.GL_CLAMP_TO_EDGE);
			letters[i].position = new Point(position.x+i*size.x,position.y);
			letters[i].size= size;
			// Shift the letter_texture such that the correct letter is shown in the frame:
			letters[i].set_texture_margins(1/Float.valueOf(alphabet.length)*(pos%Float.valueOf(alphabet.length)), 0, (Float.valueOf(alphabet.length)-1-(pos%Float.valueOf(alphabet.length)))/Float.valueOf(alphabet.length), 0);
		}
	}
	
	
	/**
	 * Generate the standard alphabet:
	 * a, ..., z, A, ..., Z, 0, ..., 9
	 */
	private void initialize_alphabet() {
		alphabet = new char[26*2 + 10];
        
        for(char ch = 'a'; ch <= 'z'; ++ch)
        {
            alphabet[ch-'a'] = ch;
        }
        
        for(char ch = 'A'; ch <= 'Z'; ++ch)
        {
            alphabet[ch-'A' + 26] = ch;
        }
        
        for (char ch = '0'; ch <= '9'; ++ch) {
			alphabet[ch - '0' + 2*26] = ch;
		}
	}

	public void set_alphabet(char new_alphabet[]) {
		this.alphabet = new_alphabet;
	}

	public void draw(GL10 gl) {
		for (int i = 0; i < letters.length; i++) {
			letters[i].draw(gl, GL10.GL_TRIANGLE_FAN);
		}
	}

}
