import java.io.*;
import java.util.Scanner;
import java.util.logging.Logger;

//TODO:   Memory will initialize itself by reading a program file.
public class Memory {
    int[] memoryArr;
    Logger log;

    public Memory() {
        memoryArr = new int[2000];
        log = Logger.getLogger(Memory.class.getName());
    }

    public void initializeMemory(String fileName) {
        try {
            FileReader fileReader = new FileReader("sample.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            int count = 0;
            while ((line = bufferedReader.readLine()) != null) {
                /* if condition to add commands at specified locations*/
                if(line.isEmpty())
                    continue;
                if (line.trim().charAt(0) == '.') {
                    count = Integer.parseInt(line.replace('.', ' ').split("//")[0].trim());
                    continue;
                }
                Integer command = Integer.parseInt(line.split("//")[0].trim());
                this.memoryArr[count++] = command;
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void read(int addr) {
        System.out.println(memoryArr[addr]);
    }

    public boolean write(int addr, int data) {
        System.out.println("Writing data:" + data + " at address:" + addr);
        memoryArr[addr] = data;
        return true;
    }

    public static void main(String args[]) {
        Memory memory = new Memory();
        if (args == null || args.length == 0) {
            System.out.println("Filename not passed");
            return;
        } else {
            Scanner sc = new Scanner(System.in);
            String s = "";
            while (sc.hasNext() && !s.equals("50")) {
                s = sc.nextLine();
                if (s.equals("0"))
                    memory.initializeMemory(args[0]);
                //for read write
                memory.read(Integer.parseInt(s));
            }
        }

    }
}
