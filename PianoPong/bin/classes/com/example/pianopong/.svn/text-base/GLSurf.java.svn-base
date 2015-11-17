package com.example.pianopong;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GLSurf extends GLSurfaceView {

	private RendererGame renderer;
	
	public GLSurf(Context c, RendererGame r) {
		super(c);
		this.renderer = r;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event != null && event.getAction() == MotionEvent.ACTION_DOWN){			
			float x = event.getX();
			float y = event.getY();
			renderer.ontouch(x, y);
		}
		return false;
	}
	
	public void doSetRenderer(RendererGame renderer) {
		setRenderer(renderer);
		this.renderer = renderer;
	}

}
