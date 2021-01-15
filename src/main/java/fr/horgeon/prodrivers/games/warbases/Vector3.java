package fr.horgeon.prodrivers.games.warbases;

public class Vector3<T> {
	private T x, y, z;

	public Vector3( T x, T y, T z ) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public T getX() {
		return x;
	}

	public T getY() {
		return y;
	}

	public T getZ() {
		return z;
	}

	public T setX( T x ) {
		return this.x = x;
	}

	public T setY( T y ) {
		return this.y = y;
	}

	public T setZ( T z ) {
		return this.z = z;
	}
}
