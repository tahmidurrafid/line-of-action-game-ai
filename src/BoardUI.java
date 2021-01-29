import javafx.util.Pair;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class BoardUI extends JPanel {
    int dimension = 6;
    int cellSize = 50;
    Color color1 = new Color(240, 240, 240);
    Color color2 = new Color(180, 182, 187);
    Color pieceColor1 = new Color(17, 17, 17);
    Color pieceColor2 = new Color(13, 0, 200);
    boolean won = false;
    boolean multiPlayer = false;
    boolean moving = false;
    float moveProgress = 0;
    int [][]state;
    int turn = 1;
    BoardUI board;
    JFrame me;

    ArrayList<Pair<Integer, Integer>> director = new ArrayList<>();
    Pair<Integer, Integer> selected = new Pair<>(-1, -1);
    Pair<Integer, Integer> moveTo = new Pair<>(-1, -1);

    BoardUI(boolean multiPlayer, int dimension){
        board = this;
        this.dimension = dimension;
        this.multiPlayer = multiPlayer;
        state = new int[dimension][dimension];
        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){
                state[i][j] = 0;
            }
        }
        for(int i = 1; i < dimension-1; i++){
            state[0][i] = state[dimension-1][i] = -1;
            state[i][0] = state[i][dimension-1] = 1;
        }
    }

    Pair<Integer, Integer> posOnDir(Pair<Integer, Integer> pos, Pair<Integer, Integer> dim, int turn){
        int counter = -1;
        int x = pos.getKey(), y = pos.getValue();
        while(x < dimension && x >= 0 && y < dimension && y >= 0){
            counter += state[x][y] != 0? 1 : 0;
            x += dim.getKey();
            y += dim.getValue();
        }
        x = pos.getKey();
        y = pos.getValue();
        while(x < dimension && x >= 0 && y < dimension && y >= 0){
            counter += state[x][y] != 0? 1 : 0;
            x -= dim.getKey();
            y -= dim.getValue();
        }
        counter--;
        x = pos.getKey() + dim.getKey();
        y = pos.getValue() + dim.getValue();
        while(x < dimension && x >= 0 && y < dimension && y >= 0){
            if(counter == 0 && state[x][y] != turn){
                return new Pair<>(x, y);
            }
            if(state[x][y] == -turn) return null;
            counter--;
            x += dim.getKey();
            y += dim.getValue();
        }
        return null;
    }

    public void createGUI(){
        JFrame f=new JFrame();//creating instance of JFrame
        JPanel jp = this;
        jp.setPreferredSize(new Dimension(cellSize * dimension,cellSize*dimension));
        f.getContentPane().add( jp );
        f.pack();
        f.setVisible(true);//making the frame visible
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        me = f;
        this.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent mouseEvent){
                if(!multiPlayer && turn == -1) return;
                int x = mouseEvent.getX()/cellSize;
                int y = mouseEvent.getY()/cellSize;
                if(selected.getKey() != -1){
                    boolean valid = false;
                    for(Pair<Integer, Integer> pair : director){
                        if(pair.getKey() == x && pair.getValue() == y){
                            valid = true;
                            break;
                        }
                    }
                    if(valid){
                        movePiece(new Pair<>(selected.getKey(), selected.getValue()) , new Pair<>(x, y));
                        selected = new Pair<>(-1, -1);
                        validate();
                        repaint();
                        if(!multiPlayer && !won){
                            InteractBack back = new InteractBack(dimension, -1, state, board);
                        }
                        return;
                    }
                }
                director.clear();
                selected = new Pair<>(x, y);
                if(state[x][y] != 0){
                    if(!multiPlayer && state[x][y] == -1) return;
                    if(multiPlayer && state[x][y] != turn) return;
                    Pair<Integer, Integer> option;
                    for(int i = -1; i <= 1; i++){
                        for(int j = -1; j <= 1; j++){
                            if(i == 0 && j == 0) continue;
                            option = posOnDir(new Pair<>(x, y), new Pair<>(i, j), state[x][y]);
                            if(option != null){
                                director.add(new Pair<>(option.getKey(), option.getValue()));
                            }
                        }
                    }
                }
                repaint();
            }
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
            }
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
            }
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
            }
            @Override
            public void mouseExited(MouseEvent mouseEvent) {
            }
        });
    }

    void movePiece(Pair<Integer, Integer> from, Pair<Integer, Integer> to){
        moving = true;
        state[to.getKey()][to.getValue()] = state[from.getKey()][from.getValue()];
        state[from.getKey()][from.getValue()] = 0;
        repaint();
        String winningString = "";
        int result = utility();
        if(result == 2) winningString = (turn==1) ? "Black" : "Blue"  + " wins";
        else if(result != 0) winningString = ((result==1) ? "Black" : "Blue") + " wins";
        if(winningString.length() > 0){
            JOptionPane.showMessageDialog(null, winningString);
            IntroUI.me.setVisible(true);
            me.dispose();
            won = true;
        }
        moving = false;
        turn = -turn;
    }

    void dfsUtil(int x, int y, boolean[][] visited, int turn){
        if(x < 0 || x >= dimension || y < 0 || y >= dimension || visited[x][y] || state[x][y] != turn) return;
        visited[x][y] = true;
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                if(i == 0 && j == 0) continue;
                dfsUtil(x+i, y+j, visited, turn);
            }
        }
    }


    int utility(){
        boolean[][] visited = new boolean[dimension][dimension];
        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++)
                visited[i][j] = false;
        }
        int one =0, mone = 0;
        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){
                if(state[i][j] == 1 && visited[i][j] == false){
                    one++;
                    dfsUtil(i, j, visited, 1);
                }
            }
        }
        visited = new boolean[dimension][dimension];
        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++)
                visited[i][j] = false;
        }

        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){
                if(state[i][j] == -1 && visited[i][j] == false){
                    mone++;
                    dfsUtil(i, j, visited, -1);
                }
            }
        }
        if(one == 1 && mone == 1) return 2;
        if(one == 1) return 1;
        if(mone == 1) return -1;
        return 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){
                if((i+j)%2 == 0){
                    g.setColor(color1);
                }else{
                    g.setColor(color2);
                }
                g.fillRect(i*cellSize, j*cellSize, cellSize, cellSize);
            }
        }
        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){
                if(state[i][j] != 0){
                    if(state[i][j] == 1){
                        g.setColor(pieceColor1);
                    }else{
                        g.setColor(pieceColor2);
                    }
                    g.fillOval((int)((i+.25)*cellSize), (int)((j+.25)*cellSize),
                            (int)(cellSize*.5), (int)(cellSize*.5));
                }
            }
        }
        if(selected.getKey() != -1){
            for(Pair<Integer, Integer> pair : director){
                g.drawLine((int)((selected.getKey() + .5)*cellSize),  (int)((selected.getValue() + .5)*cellSize),
                        (int)((pair.getKey() + .5)*cellSize),  (int)((pair.getValue() + .5)*cellSize));
            }
        }
    }
}
