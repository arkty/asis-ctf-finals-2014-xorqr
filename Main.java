package ctf.xorqr;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {
        String flag = null;

        Socket s = new Socket();
        s.connect(new InetSocketAddress("asis-ctf.ir", 12431));
        s.setSoTimeout(3000);
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        boolean started = false;

        while (s.isConnected()) {
            String line = in.readLine();

            if(line == null)
                break;

            if(line.contains("ASIS_")) {
                flag = line.substring(line.indexOf("ASIS_"), line.length());
                break;
            }
            if (line.startsWith("send ")) {
                started = true;
                out.println("START");
            }

            if(started) {
                Cracker cracker = new Cracker();
                for(int i = 0; i < 14; i++) {
                    cracker.read(in);
                    String code = cracker.crack();
                    out.println(code);
                    in.readLine();
                }
                started = false;
            }
        }

        if(!s.isClosed()){
            s.close();
        }

        System.out.println(flag);
    }
}
