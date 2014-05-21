package Five;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.concurrent.locks.ReentrantLock;

class BoardData extends Observable
{
	private Block[][] block;
	Color[] colorArray = { Color.black, 
			Color.white, Color.orange, Color.yellow, 
			new Color(112, 238, 125), new Color(103, 98, 238), 
			new Color(238, 79, 85), new Color(207, 140, 238) };
	
	private HashSet<Block> chainBlock;
	RemoveBlock removeBlock;
	ReentrantLock blockAddressSetKey = new ReentrantLock();
	ReentrantLock updateScoreKey = new ReentrantLock();

	public BoardData(int x, int y)
	{
		block = new Block[y][x];
		chainBlock = new HashSet<Block>();

		initArray();
		findChain();
	}

	void initArray() {
		for (int row = 0; row < this.block.length; row++)
			for (int col = 0; col < this.block[row].length; col++)
				this.block[row][col] = new Block(row, col, randomColor());
	}

	Color randomColor()
	{
		int randomNum = (int)(Math.random() * 7 + 1);
		return colorArray[randomNum];
	}
	Block[][] gettingBlockArray() {
		return block;
	}
	boolean swapAble(int[] sourcePosition, int[] destinationPosition) {
		int xGap = Math.abs(sourcePosition[0] - destinationPosition[0]);
		int yGap = Math.abs(sourcePosition[1] - destinationPosition[1]);
		boolean swapable = 
				(xGap == 1) && (yGap == 0)?true:((xGap == 0 && yGap == 1)?true : false);
		return swapable;
	}
	void swapBlocks(int[] sourcePosition, int[] destinationPosition, SwapType swapType) {
		Block sourceBlock = block[sourcePosition[0]][sourcePosition[1]];
		Block destBlock = block[destinationPosition[0]][destinationPosition[1]];

		int sourceX = sourceBlock.getXCoordinate();
		int destX = destBlock.getXCoordinate();

		int sourceY = sourceBlock.getYCoordinate();
		int destY = destBlock.getYCoordinate();

		Color temp = sourceBlock.getColor();

		block[sourcePosition[0]][sourcePosition[1]].setColor(destBlock.getColor());
		block[destinationPosition[0]][destinationPosition[1]].setColor(temp);

		block[sourcePosition[0]][sourcePosition[1]].icon.repaint();
		block[destinationPosition[0]][destinationPosition[1]].icon.repaint();
	}

	void swap(int[] sourcePosition, int[] destinationPosition){
		if (!swapAble(sourcePosition, destinationPosition)){
			System.out.println("잘못된 이동 " +swapAble(sourcePosition, destinationPosition));
			return;
		}
		swapBlocks(sourcePosition, destinationPosition,SwapType.VERTICAL);

		if (!findChain()){
			swapBlocks(sourcePosition, destinationPosition,SwapType.VERTICAL);
		}
	}

	boolean findChain() {
		int chainCountRow = 0;
		int chainCountCol = 0;
		for (int row = this.block.length - 1; row >= 0; row--) {
			for (int col = this.block[row].length - 1; col > 0; col--) {
				if (block[row][col].getColor().equals(block[row][col - 1].getColor())) {
					chainCountRow++;
					countRowChain:
					switch (chainCountRow){ 
					
					case 0:case 1:
						break countRowChain ;
					case 2:
						chainBlock.add(block[row][col - 1]);
						chainBlock.add(block[row][col]);
						chainBlock.add(block[row][col + 1]);
					case 3:case 4:
						chainBlock.add(this.block[row][col - 1]);
						} 
//					System.out.println("chainCountrow :"+chainCountRow);
				} else {
					chainCountRow = 0;
				}
			}
			chainCountRow = 0;
		}
		for (int col = block[0].length - 1; col >= 0; col--) {
			for (int row = block.length - 1; row > 0; row--) {
				if (this.block[row][col].getColor().equals(this.block[(row - 1)][col].getColor())) {
					chainCountCol++;
					chainCountCol:
					switch (chainCountCol) { 
					case 0:case 1:
						break chainCountCol;
					case 2:
						chainBlock.add(block[row + 1][col]);
						chainBlock.add(block[row][col]);
						chainBlock.add(this.block[row - 1][col]);
						break;
					case 3: case 4:
						chainBlock.add(this.block[row - 1][col]);
						
					} 
				} else {
//					System.out.println("chainCountCol"+chainCountCol);
					chainCountCol = 0;
				}
			}
			chainCountCol = 0;
		}
		if (!chainBlock.isEmpty()) {
			blockProcess();
			return true;
		}
		return false;
	}

	void blockProcess() {
		Iterator<Block> itr = chainBlock.iterator();
		try {
			blockAddressSetKey.lock();
			while (itr.hasNext()) {
				Block temp = (Block)itr.next();
			int row = temp.getRow();
			int col = temp.getCol();
//				System.out.println("  : "+row+","+col);

				removeBlock = new RemoveBlock(row, col);
				removeBlock.start();
			}

			chainBlock.clear();
		} catch (Exception localException) {
		}
		finally {
			blockAddressSetKey.unlock();
		}
	}

	
	
	void incresePoint()
	{
		updateScoreKey.lock();
		if (Board.hasGameStarted.booleanValue()) {
			int currentPoint = Integer.parseInt(Board.labPointDisp.getText());
			currentPoint += 10;
			Board.labPointDisp.setText(String.valueOf(currentPoint));
		}

		updateScoreKey.unlock();
	}
	void findEmptyBlock() {
		int emptyHole = 0;
		while (true) {
			FillCol[] fillThread = new FillCol[block[0].length];
			for (int col = 0; col <block[0].length; col++) {
				fillThread[col] = new FillCol(col);
			}
			for (int col = 0; col < block[0].length; col++) {
				fillThread[col].start();
			}
			for (int col = 0; col < block[0].length; col++)
				try {
					fillThread[col].join();
				}
			catch (Exception localException)
			{
			}
			findChain();
			if (emptyHole == 0) {
				findChain();

				break;
			}
			emptyHole = 0;
		}
	}

	void fillEmptyBlock(int row, int col){
		int[] abovePosition = new int[2];
		int[] currentPosition = new int[2];

		if (row == 0) {
			block[row][col].setColor(randomColor());
			block[row][col].icon.repaint();
		}else{
			Block aboveBlock = this.block[(row - 1)][col];
			abovePosition[0] = aboveBlock.getRow();
			abovePosition[1] = aboveBlock.getCol();

			currentPosition[0] = this.block[row][col].getRow();
			currentPosition[1] = this.block[row][col].getCol();

			swapBlocks(currentPosition, abovePosition, SwapType.VERTICAL);
		}
	}

	class FillCol extends Thread{
		int col;
		public FillCol(int col){
			this.col = col;
		}
		public void run() { 
			int emptyHole = 0;
		while (true) {
			for (int row = block.length - 1; row >= 0; row--)
				if (block[row][col].getColor().equals(Color.black)) {
					fillEmptyBlock(row, col);
					emptyHole++;
					try {
						Thread.sleep(100);
					}
					catch (Exception localException){
					}
				}
			if (emptyHole == 0) {
				break;
				}
			emptyHole = 0;
			}
		}
	}


	class RemoveBlock extends Thread{ 
		int row,col,x,y;

		public RemoveBlock(int row, int col){
			this.row = row;
			this.col = col;
			x = block[row][col].getXCoordinate();
			y = block[row][col].getYCoordinate();
		}

		public void run(){ 
			try
			{
				block[row][col].icon.setBounds(x, y, 50, 50);
				sleep(100);

				block[row][col].icon.setBounds(x, y, 40, 40);
				sleep(100);

				block[row][col].icon.setBounds(x, y, 30, 30);
				sleep(100);

				block[row][col].icon.setBounds(x, y, 60, 20);
				sleep(100);

				block[row][col].icon.setBounds(x, y, 60, 10);
				sleep(100);
				block[row][col].setColor(Color.black);

				block[row][col].icon.setSize(60, 60);
				findEmptyBlock(); 

				incresePoint();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	class SwapAnimation extends Thread{
		int[] sourceXY;
		int[] destXY;
		int sourceRow;
		int sourceCol;
		int destRow;
		int destCol;
		int sourceX;
		int destX;
		int sourceY;
		int destY;
		SwapType swapType;

		public SwapAnimation(int[] sourcePosition, int[] destinationPosition, SwapType swapType){
			sourceRow = block[sourcePosition[0]][sourcePosition[1]].getRow();
			sourceCol = block[sourcePosition[0]][sourcePosition[1]].getCol();
			destRow = block[destinationPosition[0]][destinationPosition[1]].getRow();
			destCol = block[destinationPosition[0]][destinationPosition[1]].getCol();

			sourceX = block[sourcePosition[0]][sourcePosition[1]].getXCoordinate();
			sourceY = block[sourcePosition[0]][sourcePosition[1]].getYCoordinate();

			destX = block[destinationPosition[0]][destinationPosition[1]].getXCoordinate();
			destY = block[destinationPosition[0]][destinationPosition[1]].getYCoordinate();

			this.swapType = swapType;
		}

		void downing() {
			try {
				block[sourceRow][sourceCol].icon.setBounds
				((sourceX + destX) / 2, (sourceY + destY) / 2 - 20, 60, 60);
				block[sourceRow][sourceCol].icon.repaint();
				sleep(50L);
				block[sourceRow][sourceCol].icon.setBounds
				((sourceX + destX) / 2, (sourceY + destY) / 2 - 10, 60, 60);
				block[sourceRow][sourceCol].icon.repaint();
				sleep(50L);
				block[sourceRow][sourceCol].icon.setBounds
				((sourceX + destX) / 2, (sourceY + destY) / 2, 60, 60);
				sleep(50L);
				block[sourceRow][sourceCol].icon.setBounds
				((sourceX + destX) / 2, (sourceY + destY) / 2 + 10, 60, 60);
				block[sourceRow][sourceCol].icon.repaint();
				sleep(50L);
				block[sourceRow][sourceCol].icon.setBounds
				((sourceX + destX) / 2, (sourceY + destY) / 2 + 20, 60, 60);
				block[sourceRow][sourceCol].icon.repaint();
				sleep(50L);
			}
			catch (Exception localException)
			{
			}
		}

		public void run(){
			if (swapType == SwapType.VERTICAL) {
				downing();
			}
			super.run();
		}
	}
}