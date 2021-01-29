import javafx.util.Pair;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;

public class InteractBack implements Runnable{
    int [][] state;
    int turn;
    int dimension;
    BoardUI board;
    Thread t;
    InteractBack(int dimension, int turn, int [][] state, BoardUI board){
        this.dimension = dimension;
        this.turn = turn;
        this.state = state;
        this.board = board;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        System.out.println("Threading");
        getMove();
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> result = getMove();
        board.movePiece(result.getKey(), result.getValue());
    }

    Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> getMove(){
        try{
            ProcessBuilder builder = new ProcessBuilder("1605046");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            OutputStream stdin = process.getOutputStream ();
            InputStream stderr = process.getErrorStream ();
            InputStream stdout = process.getInputStream ();

            BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

            String input = toString();
            if (input.trim().equals("exit")) {
                writer.write("exit\n");
            } else {
                writer.write( input + "\n");
            }
            writer.flush();

            String line = reader.readLine();
//            while (line != null && ! line.trim().equals("--EOF--")) {
//                System.out.println ("Stdout: " + line);
//                line = reader.readLine();
//                break;
//            }
            reader.close();
            writer.close();
            if (line == null) {
                return null;
            }
            System.out.println(line);
            Pair< Pair<Integer, Integer>, Pair<Integer, Integer>> result;
            String []splitted = line.split(" ");
            result = new Pair<>(new Pair<>(Integer.valueOf(splitted[0]), Integer.valueOf(splitted[1]) ),
                        new Pair<>(Integer.valueOf(splitted[2]), Integer.valueOf(splitted[3]) ) );
            return result;
        }catch (Exception e){
        }
        return null;
    }

    @Override
    public String toString() {
        String s = dimension + " " + turn + " ";
        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){
                s += state[i][j] + " ";
            }
        }
        return s;
    }
}
