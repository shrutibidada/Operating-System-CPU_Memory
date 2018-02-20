import java.io.*;
import java.util.Scanner;
import java.util.logging.Logger;

/*
* Created By: Shruti Bidada
* Net ID: sgb160130
* */

public class Memory {
    int[] memoryArr;
    Logger log;

    public Memory() {
        memoryArr = new int[2000];
        log = Logger.getLogger(Memory.class.getName());
    }

    public void initializeMemory(String fileName) {
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            int count = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty())
                    continue;
                if (line.trim().charAt(0) == '.') {
                    count = Integer.parseInt(line.replace('.', ' ').split("//")[0].trim());
                    continue;
                }
                String parseVal = line.split("//")[0].trim();
                if (!parseVal.isEmpty()) {
                    Integer command = Integer.parseInt(parseVal);
                    this.memoryArr[count++] = command;
                } else {
                    continue;
                }
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void read(int addr) {
        System.out.println(memoryArr[addr]);
    }


    public void write(int addr, int data) {
        memoryArr[addr] = data;
    }

    public static void main(String args[]) {
        Memory memory = new Memory();
        if (args == null || args.length == 0) {
            System.out.println("Filename not passed");
            return;
        } else {
            Scanner sc = new Scanner(System.in);
            String s = "";
            while (sc.hasNext()) {
                s = sc.nextLine();
                if (s.equals("0"))
                    memory.initializeMemory(args[0]);
                if (s.contains("w-")) {
                    String[] arr = s.split("-");
                    memory.write(Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                } else {
                    //for read write
                    memory.read(Integer.parseInt(s));
                }
            }
        }

    }
}
