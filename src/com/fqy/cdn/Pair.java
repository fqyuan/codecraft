package com.fqy.cdn;

public class Pair implements Comparable {
	private int first;
	private int second;

	public Pair(int first, int second) {
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	public int compareTo(Object obj) {
		Pair pair = (Pair) obj;
		return this.getFirst() < pair.getFirst() ? 1 : (this.getFirst() == pair.getFirst() ? 0 : -1);
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

}