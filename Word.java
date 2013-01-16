/**
 * Word
 * - a set of CharCell
 *
 * @version 1.00 
 * @author Michael Kalinin
 */ 

import java.awt.*;
import java.awt.font.*;
 
 public class Word {
 
    private CharCell [] cells;
    private int orientation;
    private int wordCoord;
    private int length;
    /**
     * Сoздает новый список
     */
    public Word( String word, int orientation, int wordCoord, int initialVariableCoord ) {
        
        this.orientation = orientation;    
        this.wordCoord = wordCoord;
        this.length = word.length();
        cells = new CharCell[length];
        
        for ( int i = 0 ; i < length ; i++ ) {
            cells[i] = new CharCell( word.charAt(i), initialVariableCoord + i ) ;            
        }                
    }    
    /**
     * Сoздает новый список, копируя существующий
     */
    public Word( Word word ) {
        
        this.orientation = word.orientation();    
        this.wordCoord = word.coord();
        this.length = word.length();
        cells = new CharCell[length];
        
        for ( int i = 0 ; i < length ; i++ ) {
            cells[i] = new CharCell ( word.get(i) );            
        }                
    }
    /**
     * Отрисовывает слово
     */
    public void showWord( Graphics2D g2D, Font font, FontRenderContext context ) {         
        for ( int i = 0 ; i < length ; i++ ) {
            cells[i].showCharCell ( g2D, font, context, orientation, wordCoord );        
        }
    }
    /**
     * Увеличивает координаты слова и всех его символов на велечину самого максимального
     * смещения относительно нуля - |minCoordX| и |minCoordY| ( <0 )
     */    
    public void increaseCoordinate ( int minCoordX, int minCoordY ) {
        if ( orientation == Orientation.HORIZ ) {
            wordCoord -= minCoordY;
            for ( int i = 0 ; i < length ; i++ ) 
                cells[i].setCoord ( cells[i].coord() - minCoordX );            
        }
        else {
            wordCoord -= minCoordX;
            for ( int i = 0 ; i < length ; i++ ) 
                cells[i].setCoord ( cells[i].coord() - minCoordY );
        }    
    }
    /**
     * Set and get functions
     */
    public CharCell get ( int i ) { 
        if ( i>=0 && i < length ) return cells[i]; 
        else return new CharCell();
    }        
    public int orientation () { return orientation; }
    public int coord () { return wordCoord; }    
    public int length () { return length; }        
    public int first () { return cells[0].coord(); }
    public int last () { return cells[length-1].coord(); }
    
 }
