package ctf.xorqr;

import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.qrcode.decoder.Decoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Andrey Khitryy
 * Email: andrey.khitryy@gmail.com
 */
public class Cracker {

    private static final Decoder decoder = new Decoder();
    private static final int CHECK_INDEX = 6;
    private int size;
    private boolean[] check_string;
    private boolean[][] source_qr;
    private final List<Index> question_indexes = new ArrayList<Index>();

    public Cracker() {

    }

    public void read(BufferedReader in) throws IOException {
        String l1 = in.readLine();

        this.size = l1.length();
        check_string = new boolean[size];
        source_qr = new boolean[size][size];

        fillCheckString();
        question_indexes.clear();

        for(int j = 0; j < size; j++) {
            source_qr[0][j] = (l1.charAt(j) == '+');
            if(l1.charAt(j) == '?') {
                question_indexes.add(new Index(0, j));
            }
        }

        for(int i = 1; i < size; i++) {
            String l = in.readLine();
            for(int j = 0; j < size; j++) {
                source_qr[i][j] = (l.charAt(j) == '+');
                if(l.charAt(j) == '?') {
                    question_indexes.add(new Index(i, j));
                }
            }
        }
    }

    public String crack() {
        boolean qrx[] = new boolean[this.size];

        for(int i = 0; i < this.size; i++) {
            qrx[i] = rxor(source_qr[CHECK_INDEX][i], check_string[i]);
        }

        for(int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                source_qr[i][j] = (source_qr[i][j] != qrx[j]);
            }
        }

        int questions = question_indexes.size();
        if(questions == 0) {
            return decode(source_qr);
        }

        String code = null;

        boolean[][] truth_table = getComb(questions);
        for(int i = 0; i < (1 << questions); i++) {
            for(int j = 0; j < questions; j++) {
                Index index = question_indexes.get(j);
                source_qr[index.i][index.j] = truth_table[i][j];
            }
            code = decode(source_qr);
            if(code != null) break;
        }

        return code;
    }

    private String decode(boolean[][] qr) {
        try {
            DecoderResult decoderResult = decoder.decode(qr);
            return decoderResult.getText();
        } catch (ChecksumException e) {
            return null;
        } catch (FormatException e) {
            return null;
        }
    }

    private void fillCheckString() {
        for(int i = 0; i < size; i++) {
            check_string[i] = (i < 7 || i > size - 7) || i % 2 == 0;
        }
    }

    private static boolean rxor(boolean s, boolean r) {
        return !(!s && !r) && (!s || !r);
    }

    private boolean[][] getComb(int n) {
        boolean[][] result = new boolean[1 << n][n];
        for (int mask = 0; mask < (1 << n); mask++) {
            for (int i = 0; i < n; i++) {
                result[mask][i] = ((mask >> i) & 1) == 1;
            }
        }
        return result;
    }
}
