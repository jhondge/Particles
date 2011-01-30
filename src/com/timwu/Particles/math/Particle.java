package com.timwu.Particles.math;

import android.graphics.Color;

public class Particle {
	private Vector2d pos, v;
	private float r;
	private int color;
	private float bounce = 0.5f;
	private float friction = 0.9f;
	
	public Particle(Vector2d pos, Vector2d v, float radius, int color) {
		this.pos = new Vector2d(pos);
		this.v  = new Vector2d(v);
		this.r = radius;
		this.color = color;
	}
	
	public void move(float dt) {
		// Just doing a naive implementation where velocity will be
		// constant over the timeslice. Probably not too bad an approximation.
		pos.multiplyAdd(dt, v);
	}
	
	public void accelerate(float dt, Vector2d a) {
		v.multiplyAdd(dt, a);
	}
	
	public void bounce(Segment s) {
		v.reflect(s.getN());
		Vector2d scaledVector = new Vector2d(0, 0);
		scaledVector.multiplyAdd(v.dot(s.getN()) * bounce, s.getN());
		scaledVector.multiplyAdd(v.dot(s.getG()) * friction, s.getG());
		v = scaledVector;
	}
	
	public float impactTime(Segment s) {
		Vector2d startToPos = new Vector2d(pos).multiplyAdd(-1.0f, s.getStart());
		float pn = startToPos.dot(s.getN());
		float vn = v.dot(s.getN());
		
		if (pn / vn > 0) {
			// Reject if the velocity isn't going towards the segment
			color = Color.MAGENTA;
			return Float.MAX_VALUE;
		}
		
		// Calculate the time to impact
		float tImpact = 0.0f;
		// r needs to reduce whatever pn is towards 0
		if (pn < Physics.FUDGE) {
			tImpact = (-r - pn) / vn;
		} else {
			tImpact = (r - pn) / vn;
		}
		
		// We obviously can't go backwards in time
		if (tImpact < Physics.FUDGE) return Float.MAX_VALUE;
		
		// Check if the impact lies on the segment, if not there's no impact
		float gImpact = startToPos.dot(s.getG()) + v.dot(s.getG()) * tImpact;
		if (gImpact < Physics.FUDGE || gImpact > s.getLength()) {
			color = Color.YELLOW;
			return Float.MAX_VALUE;
		}
		color = Color.CYAN;
		return tImpact;
	}
	
	public Vector2d getPos() {
		return pos;
	}
	
	public Vector2d getV() {
		return v;
	}
	
	public float getR() {
		return r;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setColor(int c) {
		color = c;
	}

	public String toString() {
		return "Particle @ " + pos + " going " + v;
	}
}
