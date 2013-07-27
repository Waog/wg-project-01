package utils;

public class Pair<A, B> {

	public final A first;
	public final B second;

	private Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}
}