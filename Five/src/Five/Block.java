package Five;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import javax.swing.JPanel;

class Block extends Observable implements MouseListener{
  private Color color;
  private int row,col,x,y;
  
  Painting icon;

  public Block(int row, int col, Color designated){
    this.row = row;
    this.col = col;
    this.x = col * 60;
    this.y = row * 60;
    color = designated;
    icon = new Painting();
    icon.addMouseListener(this);
  }
  public void setColor(Color c) {
    color = c;
  }
  public Color getColor() {
    return color;
  }
  void setRowCol(int[] position) {
    row = position[0];
    col = position[1];
  }
  int getXCoordinate() {
    return x;
  }
  int getYCoordinate() {
    return y;
  }
  int getRow() {
    return row;
  }
  int getCol() {
    return col;
  }

  public void mouseClicked(MouseEvent e)
  {
    setChanged();
    notifyObservers(icon);
  }

  public void mouseEntered(MouseEvent e)
  {
  }

  public void mouseExited(MouseEvent e)
  {
  }

  public void mousePressed(MouseEvent e)
  {
  }

  public void mouseReleased(MouseEvent e)
  {
  }

  class Painting extends JPanel{
	private static final long serialVersionUID = 5518282091920478710L;
	 Image img1 = Toolkit.getDefaultToolkit().getImage("src\\images\\1.gif");
    Image img2 = Toolkit.getDefaultToolkit().getImage("src\\images\\2.gif");
    Image img3 = Toolkit.getDefaultToolkit().getImage("src\\images\\3.gif");
    Image img4 = Toolkit.getDefaultToolkit().getImage("src\\images\\4.gif");
    Image img5 = Toolkit.getDefaultToolkit().getImage("src\\images\\5.gif");
    Image img6 = Toolkit.getDefaultToolkit().getImage("src\\images\\6.gif");
    Image img7 = Toolkit.getDefaultToolkit().getImage("src\\images\\7.gif");

    Painting() {  } 
    
    protected void paintComponent(Graphics g) { 
   	 super.paintComponent(g);
      setOpaque(false); // 불투명 설정
      setBackground(null); 
      if (Block.this.getColor().equals(Color.black))
         g.drawImage(null, 0, 0, null);
      else if (Block.this.getColor().equals(Color.white))
        g.drawImage(img1, 0, 0, null);
      else if (Block.this.getColor().equals(Color.yellow))
        g.drawImage(img2, 0, 0, null);
      else if (Block.this.getColor().equals(new Color(112, 238, 125)))
        g.drawImage(img3, 0, 0, null);
      else if (Block.this.getColor().equals(new Color(103, 98, 238)))
        g.drawImage(img4, 0, 0, null);
      else if (Block.this.getColor().equals(new Color(238, 79, 85)))
        g.drawImage(img5, 0, 0,null);
      else if (Block.this.getColor().equals(Color.orange))
        g.drawImage(img6, 0, 0, null);
      else if (Block.this.getColor().equals(new Color(207, 140, 238)))
        g.drawImage(img7, 0, 0, null);
    }
  }
}