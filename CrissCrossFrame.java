/**
 * Criss Cross Game
 *
 * @version 1.00 
 * @author Michael Kalinin
 */
 
import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;


public class CrissCrossFrame extends JFrame {
    
    private WordArea area = new WordArea();
    
    public CrissCrossFrame() {
        setTitle ( "Criss Cross" );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            
        add( area );
        addMouseListener( new MouseHandler() );
        setMinimumSize( new Dimension( area.width(), area.height() ) );
        setSize( new Dimension( area.width(), area.height() ) );    
    }
    
    public static void main( String[] args ) {
        new CrissCrossFrame().setVisible( true );              
    }        
    /**
     * Обработчик нажатия на кнопку мыши
     */
    private class MouseHandler extends MouseAdapter {
        @Override
        public void mousePressed( MouseEvent event ) {
            area.nextArea();
            setMinimumSize( new Dimension( area.width(), area.height() ) );
            setSize( new Dimension( area.width(), area.height() ) );                
        }        
    }
}
