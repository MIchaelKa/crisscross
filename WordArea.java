/**
 * WordArea
 *
 * @version 1.00 
 * @author Michael Kalinin
 */
 
import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*; 
import java.awt.event.*;
import java.awt.font.*;
import javax.swing.*; 

public class WordArea extends JComponent {

	private ArrayWord<Word> mainWordArea = null;
	private ArrayList<ArrayWord<Word>> allWordArea;	
	private Font font;
	private FontRenderContext context;
	/**
         * Ширина и высота схемы
	 */
	private int width;
	private int height;
	/**
     * �������� ����� 
	 */							
	private List<String> rawWords;	
	private int numberOfWord;	
	private int sizeWordArea = 1000;	
	private int intersectWordArea = 0;
	private double density = 0;
	private int k = 0;	
	private double alpha = 0;

	public WordArea () {	
	    		
		try {
			readWords();			
			allWordArea = new ArrayList<ArrayWord<Word>>();
			alpha = (double) 1 / Math.pow( 1.4, numberOfWord );
			font = new Font ( "SansSerif", Font.PLAIN, 22 );
			String first = rawWords.remove(0);
			Word firstWord = new Word ( first, Orientation.HORIZ, 0, 0 );
			ArrayWord<Word> firstWordArea = new ArrayWord<Word>();
			firstWordArea.add( firstWord );
			
			wordsBackTracking( firstWordArea, rawWords );		
		
			Iterator<ArrayWord<Word>> iter = allWordArea.iterator();		
			while ( iter.hasNext() ) {
				assignCoordinate( iter.next() );
			}
			width = ( mainWordArea.width()+3 )*CharCell.CELL_SIZE;
			height = ( mainWordArea.height()+3 )*CharCell.CELL_SIZE;
			setSize( width, height );
		}
		catch ( IOException e ) {
			System.out.println( "Failed to read file." );
		}				
	}
	/**
     * �������������� paintComponent
	 */
	@Override
 	public void paintComponent ( Graphics g ) {
		Graphics2D g2D = ( Graphics2D ) g;		
		g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
							  RenderingHints.VALUE_ANTIALIAS_ON );	
		context = g2D.getFontRenderContext();		
		g2D.setFont( font );		
		showWords( g2D );
	}	
	/**
     * ������� ������� ����� � ����������� ������. 
	 */	
	private void showWords ( Graphics2D g2D ) {
		Iterator<Word> wordAreaIter = mainWordArea.iterator();		
		while ( wordAreaIter.hasNext() ) 
			wordAreaIter.next().showWord( g2D, font, context );		
	}
	/**
     *  ������� ���� ���� � ��������� (Backtracking) 
	 *  ���������� ����� �����-�����
	 */	
	private void wordsBackTracking ( ArrayWord<Word> wordArea, List<String> words ) {
				
		if ( accept( wordArea ) ) { 
			ArrayWord<Word> tempWordArea = new ArrayWord<Word>( wordArea );
			copyArrayWord( tempWordArea, wordArea );
			allWordArea.add( tempWordArea ); 
			mainWordArea = tempWordArea;
			return;
		}
		
		if ( reject( wordArea, words ) ) return;	
		
		for ( int i = 0 ; i < words.size() ; i++ ) {
			
			List<String> tempWords = new LinkedList<String>( words );
			String newWord = tempWords.get(i);
			tempWords.remove(i);			
			
			addNewWord( wordArea, tempWords, newWord );						
		}						
	}	
	/**
     * ���������� ����������� ����� � ������������ � ���� �������� -
	 * - ��������� ��� � ����� ( true ).
	 */		
	private void addNewWord ( ArrayWord<Word> wordArea, List<String> words, String newWord ) {		
		Word existentWord;			
		for ( int k = 0 ; k < wordArea.size() ; k++ ) {
			existentWord = wordArea.get(k);
	        ///���������� ������� � ����� � ��� ���������� ������
			for ( int i = 0 ; i < existentWord.length() ; i++ ) {
				for ( int j = 0 ; j < newWord.length() ; j++ ) {
					if ( existentWord.get(i).value() == newWord.charAt(j) ) {									
						int newOrient = invert( existentWord.orientation() );
						int newWordCoord = existentWord.get(i).coord();
						int initialVariableCoord = existentWord.coord() - j;
						Word word = new Word ( newWord, newOrient, newWordCoord, initialVariableCoord );
						///���� ����� "��������" �������� - ��������� ���
						int interCount = wordArea.intersectCount();
						if ( check ( wordArea, word, existentWord.coord() ) ) {					
							wordArea.add ( word );
							///��������� �������� ������������ (0,0) �������� ����������
							int minX = wordArea.minX();
							int minY = wordArea.minY();							
							if ( existentWord.orientation() == Orientation.HORIZ ) 	wordArea.setMinY( initialVariableCoord );						
							else wordArea.setMinX( initialVariableCoord );	
							///��������� ��������� ��������							
							wordsBackTracking ( wordArea, words );	
							///������� ��������� ����������� �����
							wordArea.remove( wordArea.size()-1 );
							wordArea.reset();
							wordArea.setMinX( minX );	
							wordArea.setMinY( minY );
							wordArea.setInterCount( interCount );												
						}
					}			
				}			
			} 
		}		
	} 
	/**
     *  ��������� ������������ �� ����������� ��������� �������
	 */	
	private boolean reject ( ArrayWord<Word> wordArea, List<String> words ) {
		
		///������� ������� ����� �� ������ ������� ������� �������		
		int currentSize = sizeWordArea ( wordArea );
		if (  currentSize >= sizeWordArea ) return true;
		///��������� ������� ����� �� ������ ��������� ������� �������	
		double currentDensity = (double) wordArea.intersectCount() / currentSize;				
		if ( currentDensity+alpha < density ) return true;		
		///������� ����� ���� � ����� ������ ��� ������� ����� ���������� ����
		double averageLengthWA = (double) sumWordLength(wordArea) / wordArea.size();
		int sumLengthWL = 0;
		Iterator<String> iter = words.iterator();		
		while ( iter.hasNext() ) 
			sumLengthWL += iter.next().length();
		double averageLengthWL = (double) sumLengthWL / words.size();
		if ( averageLengthWA >= averageLengthWL ) return true;
		
		return false;
	}
	/**
     *  ��������� �������� �� ����� ��������
	 */	
	private boolean accept ( ArrayWord<Word> wordArea ) {
		if ( wordArea.size() == numberOfWord ) {
			int currentSize = sizeWordArea ( wordArea );
			double currentDensity = (double) wordArea.intersectCount() / currentSize;
			if ( currentDensity > density) {			
				intersectWordArea = wordArea.intersectCount();
				sizeWordArea = currentSize;
				density = currentDensity;				
				return true;
			}
			else return false;
		}
		else return false;
	}
	/**
     * ��������� ����������� ���������� ������ ����� � ��������� �����	 
	 * orient - ���������� ������������ ����� + ������� ����� ����������� �� ������
	 */	
	private boolean check ( ArrayWord<Word> wordArea, Word newWord, int intersect ) {
	
		Iterator<Word> wordAreaIter = wordArea.iterator();	
		Word word;
		
		int intersectCount = wordArea.intersectCount();
		int orient = newWord.orientation();
		int newWordCoord = newWord.coord();		
		int newFirst = newWord.first();
		int newLast = newWord.last();			
		
		while ( wordAreaIter.hasNext() ) {
			word = wordAreaIter.next();
			int existFirst = word.first();
			int existLast = word.last();
			///��������� ��� ����� � ���� �� ��������� ��� � �����������
			if ( word.orientation () == orient ) {				
				if ( word.coord() == newWordCoord - 1 || word.coord() == newWordCoord + 1 ) {
					if ( !(	(newFirst == existLast) && (newFirst == intersect) ) && 
					     !( (newLast == existFirst) && (newLast == intersect) ) )
						if ( intersect( newFirst, newLast, existFirst, existLast) )					
							return false;							
				}
				else 
				if ( word.coord() == newWordCoord ) {
					if ( intersect( newFirst-1, newLast+1, existFirst, existLast) )					
						 return false;
				}								
			}
			///� � ���������������
			else {
				///����� ������� ��������������� � ��������� ������������ �����
				if ( range ( newFirst, newLast, word.coord() ) ) {
					for ( int i = 0 ; i < word.length() ; i++ ) {
						for ( int j = 0 ; j < newWord.length() ; j++ ) {
							if ( word.get(i).coord() == newWordCoord && newWord.get(j).coord() == word.coord() ) 
								if ( word.get(i).value() != newWord.get(j).value() ) return false;
								else intersectCount++;
						}
					}
					if ( ( existFirst == newWordCoord + 1 ) || ( existLast == newWordCoord - 1 ) ) 
						return false;
				}
				///����� ������� �� ����� �� ������������ �����
				if ( word.coord() == newFirst - 1 || word.coord() == newLast + 1 ) {
					if ( range ( existFirst, existLast, newWordCoord ) ) 
						return false;										
				}
			}
		}	
		if ( wordArea.intersectCount() == intersectCount ) wordArea.setInterCount( ++intersectCount );
		else wordArea.setInterCount( intersectCount ); 
		return true;
	}	
	/**
     * ��������� ������������ �� 2 ������� [a,b] � [c,d]	 
	 */
	private boolean intersect ( int a, int b, int c, int d ) {
		return range(a,b,c)||range(a,b,d)||range(c,d,a)||range(c,d,b);
	}
	/**
     * ��������� ����������� �� � ������� [a,b]	 
	 */
	private boolean range ( int a, int b, int x ) {
		return (x>=a) && (x<=b);
	}	
	/**
     * ���������� ��e ����� ����
	 */
	private boolean compareArrayWords ( ArrayWord<Word> a, ArrayWord<Word> b ) {
		if ( a.size() != b.size() ||
			 a.intersectCount() != b.intersectCount() ||
			 sizeWordArea(a) != sizeWordArea(b) ) return false;
		boolean result = false;
		Iterator<Word> iterA = a.iterator();		
		while ( iterA.hasNext() ) {	
			Word wordA = iterA.next();
			Iterator<Word> iterB = b.iterator();
			while ( iterB.hasNext() ) {
				if ( compareWords( wordA, iterB.next() ) ) {					
					result = true;
					break;
				}
			}
			if ( !result ) return false;
			else result = false;
		}
		return true;
		
	}
	/**
     * ���������� ��� ����� 
	 */
	private boolean compareWords ( Word a, Word b ) {
		if ( a.orientation() != b.orientation() ||
		     a.length() != b.length() ||
		     a.coord() != b.coord() ||
	         a.first() != b.first() ) return false;
		
		for ( int i = 0 ; i < a.length() ; i++ ) 
			if ( a.get(i).value() != b.get(i).value() )
				return false;
					
		return true;		
	}
	/**
     * ������������ ������(�������) �����   
	 */
	private int sizeWordArea ( ArrayWord<Word> wordArea ) {
		if ( wordArea.size() == 0 ) return 0;
		Iterator<Word> wordAreaIter = wordArea.iterator();
		Word word;		
		int minX = 0;
		int minY = 0;
		int maxX = 0;
		int maxY = 0;
		while ( wordAreaIter.hasNext() ) {
			word = wordAreaIter.next();	
			int wordCoord = word.coord();
			int first = word.first();
			int last = word.last();
			if ( word.orientation () == Orientation.HORIZ ) {				
				if ( wordCoord < minY ) minY = wordCoord;
				if ( wordCoord > maxY ) maxY = wordCoord;
				if ( first < minX ) minX = first;
				if ( last > maxX ) maxX = last;
			}
			else {
				if ( wordCoord < minX ) minX = wordCoord;
				if ( wordCoord > maxX ) maxX = wordCoord;
				if ( first < minY ) minY = first;
				if ( last > maxY ) maxY = last;
			}
		}
		int width = maxX - minX + 1;
		int height = maxY - minY + 1;
		wordArea.setWidth( width );
		wordArea.setHeight( height );
		return ( width * height );
	}
	/**
     * ������������ ��������� ����� ���� �����   
	 */
	private int sumWordLength ( ArrayWord<Word> wordArea ) {	
		int result = 0;
		Iterator<Word> wordAreaIter = wordArea.iterator();		
		while ( wordAreaIter.hasNext() ) 
			result += wordAreaIter.next().length();
		return result;			
	}
	/**
     * ������������� ���������� �������� ���������
	 */	
	private void assignCoordinate ( ArrayWord<Word> wordArea ) {		
		Iterator<Word> wordAreaIter = wordArea.iterator();		
		while ( wordAreaIter.hasNext() ) {
			wordAreaIter.next().increaseCoordinate ( wordArea.minX()-1, wordArea.minY()-1 );
		}
		wordArea.reset();	
	}	
	/**
     * ����������� ���������	 
	 */	
	private int invert ( int orient ) {
		if ( orient == Orientation.HORIZ ) return Orientation.VERTIC;
		else return Orientation.HORIZ;
	}
	/**
     * �������� ������ ���� 
	 */
	private void copyArrayWord ( ArrayWord<Word> newArray, ArrayWord<Word> initialArray ) {
		Iterator<Word> iter = initialArray.iterator();
		while ( iter.hasNext() ) newArray.add( new Word( iter.next() ) );
	}
	/**
     * ������ ������ ���� �� �����
	 */
	private void readWords () throws IOException {
		rawWords = new LinkedList<String>();		
		Scanner in = new Scanner ( new FileReader("words.txt") );
		while ( in.hasNext() ) {
			String next = in.next();
			ListIterator<String> iter = rawWords.listIterator();			
			while ( iter.hasNext() ) {
				if ( iter.next().length() >= next.length() ){
					iter.previous();
					break;
				}
			}			
			iter.add( next );		
		}
		numberOfWord = rawWords.size();
	}
	/**
     * ������������ ����� ���������� �������
	 */	
    public void nextArea() {
		if ( k < allWordArea.size() ) {
			mainWordArea = allWordArea.get( k++ );				
		}
		else {
			k = 0;
			mainWordArea = allWordArea.get( k++ );
		}
		width = ( mainWordArea.width()+3 )*CharCell.CELL_SIZE;
		height = ( mainWordArea.height()+3 )*CharCell.CELL_SIZE;
		setSize( width, height );
		repaint();            
    }  
	public int width(){ return width; }
	public int height(){ return height; }
}

