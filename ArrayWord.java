/**
 * ArrayWord
 *
 * @version 1.00 
 * @author Michael Kalinin
 */

import java.util.*;

public class ArrayWord<E> extends ArrayList<E> {

	private int minCoordX = 0;
	private int minCoordY = 0;
	private int width = 0;
	private int height = 0;
	private int intersectCount = 0;
	
	public ArrayWord () {
		super();
	}
	
	public ArrayWord ( int initialCapacity ) {
		super( initialCapacity );
	}
	public ArrayWord ( ArrayWord<Word> arrayWord ) {
		super( arrayWord.size() );		
		minCoordX = arrayWord.minX();
		minCoordY = arrayWord.minY();
		width = arrayWord.width();
		height = arrayWord.height();
		intersectCount = arrayWord.intersectCount();
	}
	public void setInterCount( int c ) {
		intersectCount = c;
	}
	public void setMinX ( int x ) {
		if ( x < minCoordX ) minCoordX = x;
	}
	public void setMinY ( int y ) {
		if ( y < minCoordY ) minCoordY = y;
	}
	public void setWidth ( int w ) {
		width = w;
	}
	public void setHeight ( int h ) {
		height = h;
	}
	public int minX () { return minCoordX; }
	public int minY () { return minCoordY; }
	public int width () { return width; }
	public int height () { return height; }
	public int intersectCount() { return intersectCount; }
	public void reset () { minCoordX = 0; minCoordY = 0;}
}
