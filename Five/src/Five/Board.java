package Five;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

class Board extends JFrame implements ActionListener, Runnable{
	private static final long serialVersionUID = -5084498548964762315L;
	JLayeredPane panContainer = new JLayeredPane();
	JPanel panNorth = new JPanel();
	JPanel panCenter = new JPanel();
	

	JLabel labPoint = new JLabel("Point:");
	static JLabel labPointDisp = new JLabel("0");
	JLabel finalScore = new JLabel(" "); 

	JButton btStart = new JButton("시작");
	JPanel pnButton = new JPanel();

	CardLayout card = new CardLayout();
	JPanel imgpan = new JPanel(); 
	JPanel mainpan = new JPanel();
	JPanel outpan = new JPanel();

	static Boolean hasGameStarted = Boolean.valueOf(false);

	Font font = new Font("맑은고딕", 0, 12);
	Font fontMsg = new Font("맑은고딕", 0, 25);
	JPanel jpan;
	JProgressBar prog;
	BoardView mainBoard = new BoardView(8, 8); 
	BorderPan[][] border;
	Background panBackGround = new Background();

	public Board() throws InterruptedException {
		border = new BorderPan[8][8];
		setTitle("show me the money");
		setSize(new Dimension(600, 600));
		setDefaultCloseOperation(3);
		setVisible(true);
		setResizable(false);
		init();
	}
	public void init() { //이미지 처리부분
		getContentPane().add(panContainer);
		panContainer.setBounds(0, 0, 600, 600);

		panContainer.add(panBackGround, JLayeredPane.DEFAULT_LAYER);
		panBackGround.setBounds(0, 0, 600, 600);

		for (int row = 0; row < border.length; row++) {
			for (int col = 0; col <border[row].length; col++) {
				if ((row + col) % 2 == 0)
					border[row][col] = new BorderPan(true);
				else {
					border[row][col] = new BorderPan(false);
				}
				border[row][col].setBounds(row * 60 + 55, col * 60 + 50, 60, 60); //
				panContainer.add(this.border[row][col], JLayeredPane.PALETTE_LAYER);
			}
		}

		prog = new JProgressBar();
		prog.setForeground(new Color(190, 190, 240));
		panNorth.setLayout(null);
		panNorth.add(prog);
		prog.setBounds(35, 20, 220, 15);
		panNorth.add(btStart);
		btStart.setBounds(270, 10, 70, 30);
		btStart.setBackground(Color.white);
		btStart.setFont(font);
		btStart.addActionListener(this);
		panNorth.add(labPoint);
		labPoint.setBounds(370, 10, 50, 30);
		panNorth.add(labPointDisp);
		labPointDisp.setBounds(420, 10, 50, 30);
		panNorth.setOpaque(false);
		panNorth.setBounds(20, 5, 600, 100);

		panContainer.add(panNorth, JLayeredPane.MODAL_LAYER);

		panCenter.setLayout(card);
		panCenter.setOpaque(false);
		
		panCenter.add("1", imgpan); // 이미지 배경화면 ~!
		imgpan.setOpaque(false);
		panCenter.add("2",mainBoard); // 이미지 보드
		panCenter.add("3",outpan);	// 점수화면
		outpan.setBackground(new Color(0, 0, 0, 100));
		outpan.setBounds(0, 0, 480, 480);
		outpan.setLayout(new GridBagLayout());
		outpan.add(finalScore);	 // 점수 스코어
		panCenter.setBounds(55, 50, 480, 480);

		panContainer.add(panCenter, JLayeredPane.MODAL_LAYER);
	}

	public void run(){  // 시간 
		prog.setStringPainted(true);
		prog.setMaximum(60);
		prog.setFont(font);
		prog.setString("60 초");
		prog.setBounds(35, 20, 220, 15); // 프로세스바 위치지정
		for (int i = 60; i >= 0; i--) {
			prog.setValue(i);
			try {
				Thread.sleep(1000);
				prog.setString(i + " 초");
				if (i != 0)
					continue;
				card.next(panCenter);
				finalScore.setFont(fontMsg);
				finalScore.setForeground(Color.white);
				finalScore.setOpaque(false); //점수화면 불투명 설정
				finalScore.setText("당신의 점수는 " + labPointDisp.getText() + "점 입니다.");
				hasGameStarted = Boolean.valueOf(false); 
				mainBoard.process.initArray();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public synchronized void actionPerformed(ActionEvent e) { // 게임 시작
				if (hasGameStarted.booleanValue()) {
					mainBoard.refreshBlocks();
					mainBoard = new BoardView(8, 8);
					card.previous(panCenter);
					hasGameStarted = Boolean.valueOf(false);
				} else {
					new Thread(this).start();
					card.next(panCenter);
					hasGameStarted = Boolean.valueOf(true);
				}
			}
	}
	class Background extends JPanel{
		private static final long serialVersionUID = -4332938719172804538L;
		public Background() {
			// TODO Auto-generated constructor stub
		}
		public void paint(Graphics g){ // 이미지 
			super.paint(g);
			Image backGroundImg = Toolkit.getDefaultToolkit().getImage("src\\images\\wooden01.png");
			g.drawImage(backGroundImg, 0, 0, this); // 센터 중앙위치
		}
	}

	class BorderPan extends JPanel{ // 점수화면 불투명 설정
		private static final long serialVersionUID = 9197130582482282044L;
		boolean fillorDraw;

		public BorderPan(boolean fillorDraw) { 
			this.fillorDraw = fillorDraw;
			setOpaque(false);
		}
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			setOpaque(false);
			g.setColor(new Color(250, 250, 250, 80));
			if (this.fillorDraw)
				g.fillRect(0, 0, 59, 59);
			else
				g.drawRect(0, 0, 59, 59);
		}
	}
