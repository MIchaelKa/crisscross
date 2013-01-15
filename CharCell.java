/**
 * CharCell
 *
 * @version 1.00 
 * @author Michael Kalinin
 */
 
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
 
 public class CharCell {
 
	private char value;		
	private int variableCoord;	
	public final static int CELL_SIZE = 30;

	public CharCell () {
		this.value = ' ';
		this.variableCoord = 0;		
	}

	public CharCell( char value, int variableCoord ) {
		this.value = value;
		this.variableCoord = variableCoord;		
	}

	public CharCell( CharCell cell ) {
		this.value = cell.value();
		this.variableCoord = cell.coord();		
	}

	public void showCharCell ( Graphics2D g2D, Font font, FontRenderContext context, int orient, int constCoord ) {
		int coordX;
		int coordY;
		if ( orient == Orientation.HORIZ ) {
			coordX = variableCoord * CELL_SIZE;
			coordY = constCoord * CELL_SIZE;
		}
		else {
			coordX = constCoord * CELL_SIZE;
			coordY = variableCoord * CELL_SIZE;
		}
		String s = String.valueOf(value);
		Rectangle2D bounds = font.getStringBounds( s, context );
		LineMetrics metrics = font.getLineMetrics( s, context );		
		float descent = metrics.getDescent();
		float leading = metrics.getLeading();
		Rectangle2D.Double rect = new Rectangle2D.Double( coordX, coordY, CELL_SIZE, CELL_SIZE );
		double x = rect.getX() + 1 + (rect.getWidth() - bounds.getWidth())/2;
		double y = rect.getY() + rect.getHeight() - descent - leading;
		g2D.draw( rect );		
		g2D.drawString( s, (int)x, (int)y );
	}
	/**
         * Set and get functions
	 */
	public void setValue ( char value ) { this.value = value; }
	public char value () { return value; }
	
	public void setCoord( int x ){ variableCoord = x; }	
	public int coord(){ return variableCoord; }
	
 }
