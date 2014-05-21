package Five;

import java.util.EventListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JLayeredPane;

public class BoardView extends JLayeredPane implements Observer, EventListener{
	private static final long serialVersionUID = 7704180319551340001L;

	private Block[][] blockView;
	boolean isSecond;
	BoardData process;
	private int[] sourcePosition;
	private int[] destinationPosition;

	public BoardView(int row, int col)
	{
		process = new BoardData(row, col);
		process.addObserver(this);
		blockView = process.gettingBlockArray();
		sourcePosition = new int[2];
		destinationPosition = new int[2];
		init();
	}

	BoardView callParent() {
		return this;
	}

	void init() {
		setSize(540, 540);
		setLayout(null);
		setOpaque(false);

		new RefreshPanel().start();
	}

	void refreshBlocks() { 
		blockView = process.gettingBlockArray();
		for (int row = 0; row < blockView.length; row++)
			for (int col = 0; col < blockView[row].length; col++) {
				blockView[row][col].icon.setBounds(blockView[row][col].getXCoordinate(), 
						blockView[row][col].getYCoordinate(),60, 60);

				blockView[row][col].addObserver(this);
				this.add(blockView[row][col].icon);
			}
	}

	void setSourceRowCol(int row, int col) 
	{
		this.sourcePosition[0] = row;
		this.sourcePosition[1] = col;
	}
	void setDestinationRowCol(int row, int col) {
		this.destinationPosition[0] = row;
		this.destinationPosition[1] = col;
	}

	void notifyPosition(int row, int col) { 
		if (!isSecond) {
			setSourceRowCol(row, col);
			isSecond = true;
		} else {
			setDestinationRowCol(row, col);
			process.swap(sourcePosition, destinationPosition);
			isSecond = false;
		}
	}

	public void update(Observable o, Object arg)
	{
		if ((o instanceof Block)) {
			Block clicked = (Block)o;
			notifyPosition(clicked.getRow(), clicked.getCol()); 
		}
	}

	class RefreshPanel extends Thread { 
		public RefreshPanel() {
			// TODO Auto-generated constructor stub
		}
		public void run(){
			BoardView.this.removeAll();
			BoardView.this.refreshBlocks();
		}
	}
}