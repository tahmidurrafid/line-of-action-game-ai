import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IntroUI {
    public static JFrame me;
    int dimension = 8;
    IntroUI(){
        JFrame f=new JFrame();//creating instance of JFrame
        me = f;
        f.setLayout(null);
        JButton multiPlayer = new JButton("Play With Human");
        JButton withAI = new JButton("Play With AI");
        multiPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                BoardUI boardUI = new BoardUI(true, dimension);
                boardUI.createGUI();
                me.setVisible(false);
            }
        });
        withAI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                BoardUI boardUI = new BoardUI(false, dimension);
                boardUI.createGUI();
                me.setVisible(false);
            }
        });
        f.add(multiPlayer);
        f.add(withAI);

        JRadioButton r1=new JRadioButton("6x6");
        JRadioButton r2=new JRadioButton("8x8");
        JLabel label = new JLabel("Select Game Mode:");

        label.setBounds(70, 220, 200, 30);

        r1.setBounds(100,250,100,30);
        r2.setBounds(100,280,100,30);
        r1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dimension = 6 ;
            }
        });
        r2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dimension = 8;
            }
        });
        ButtonGroup bg=new ButtonGroup();
        bg.add(r1);bg.add(r2);
        f.add(r1);f.add(r2);
        f.add(label);

        multiPlayer.setBounds(50, 100, 150, 40);
        withAI.setBounds(50, 170, 150, 40);
        f.setVisible(true);//making the frame visible
        f.setSize(280,400);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
