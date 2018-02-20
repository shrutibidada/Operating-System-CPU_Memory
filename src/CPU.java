import java.io.*;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Logger;

public class CPU {
    int ip = 0;
    int sp = 2000;
    int ir = 0;
    int ac = 0;
    int x = 0;
    int y = 0;
    int pc = 0;
    //Stack<Integer> stack = new Stack<Integer>();
    Logger log;
    PrintWriter pw;
    Scanner sc;
    boolean intrFlag = false;

    public CPU() {
        log = Logger.getLogger(Memory.class.getName());
    }

    public void checkInstructions(int command) {
        int temp = 0;
        switch (command) {
            case 1://Load the value into the AC
                this.ac = this.readFromMemory(this.pc++);
                //System.out.println("AC: "+this.ac);
                break;
            case 2://Load the value at the address into the AC
                this.ac = this.readFromMemory(this.readFromMemory(this.pc++));
                break;
            case 3://Load the value from the address found in the given address into the AC
                this.ac = this.readFromMemory(this.readFromMemory(this.readFromMemory(this.pc++)));
                break;
            case 4://Load the value at (address+X) into the AC
                // System.out.println("executing 44: "+ this.ac+"  pc: "+this.pc+" x:" + this.x);
                this.ac = this.readFromMemory(this.readFromMemory(this.pc++) + this.x);
                // System.out.println("4: "+ this.ac);
                break;
            case 5://Load the value at (address+Y) into the AC
                this.ac = this.readFromMemory(this.readFromMemory(this.pc++) + this.y);
                break;
            case 6://Load from (Sp+X) into the AC (if SP is 990, and X is 1, load from 991).
                this.ac = this.readFromMemory(this.sp + this.x);
                break;
            case 7://Store the value in the AC into the address
                break;
            case 8://Gets a random int from 1 to 100 into the AC
                this.ac = (int) (Math.random() * 100 + 1);
                break;
            case 9:/*If port=1, writes AC as an int to the screen If port=2, writes AC as a char to the screen*/
                temp = this.readFromMemory(this.pc++);
                //System.out.println("printing ascii value" + this.ac);
                if (temp == 2)
                    System.out.print((char) this.ac);
                else if (temp == 1)
                    System.out.print(this.ac);
                break;
            case 10://Add the value in X to the AC
                this.ac += this.x;
                break;
            case 11://Add the value in Y to the AC
                this.ac += this.y;
                break;
            case 12://Subtract the value in X from the AC
                this.ac -= this.x;
                break;
            case 13://Subtract the value in Y from the AC
                this.ac -= this.y;
                break;
            case 14://Copy the value in the AC to X
                this.x = this.ac;
                //System.out.println("X value after 14 "+ this.x);
                break;
            case 15://Copy the value in X to the AC
                this.ac = this.x;
                break;
            case 16://Copy the value in Y to the AC
                this.y = this.ac;
                break;
            case 17://Copy the value in Y to the AC
                this.ac = this.y;
                break;
            case 18://Copy the value in AC to the SP
                this.sp = this.ac;
                break;
            case 19://Copy the value in SP to the AC
                this.ac = this.sp;
                break;
            case 20://Jump to the address
                temp = this.readFromMemory(this.pc++);
                //System.out.println("jumping to address "+ temp);
                this.pc = temp;
                break;
            case 21://Jump to the address only if the value in the AC is zero
                temp = this.readFromMemory(this.pc++);
                if (this.ac == 0) {
                    this.pc = temp;
                    // System.out.println("jumping to address ac is 0 "+ this.pc);
                }
                // System.out.println("temp value in 21: "+ temp);
                break;
            case 22://Jump to the address only if the value in the AC is not zero
                temp = this.readFromMemory(this.pc++);
                if (this.ac != 0) {
                    this.pc = temp;
                }
                break;
            case 23://Push return address onto stack, jump to the address
                temp = this.readFromMemory(this.pc++);
                //System.out.println("value of temp "+temp);
                this.writeToMemory(--this.sp,this.pc);
                //System.out.println(this.sp+1+ "---"+ this.pc);
                this.pc=temp;
                //this.sp--;
                break;
            case 24://Pop return address from the stack, jump to the address
                if (this.sp < 2000) {
                    this.pc=this.readFromMemory(this.sp);
                    sp++;
                }
                break;
            case 25://Increment the value in X
                this.x += 1;
                //System.out.println("X value"+this.x);
                break;
            case 26://Decrement the value in X
                this.x -= 1;
                break;
            case 27://Push AC onto stack
                this.writeToMemory(--this.sp, this.ac);
                //System.out.println(this.sp+1+ "---"+ this.ac);
                //this.sp--;
                break;
            case 28://Pop from stack into AC
                if (this.sp <= 1999) {
                    this.ac = this.readFromMemory(this.sp);
                    sp++;
                }
                break;
            case 29://Perform system call
                //sava pc and sp on stack
                //jump to 1500
                if (!this.intrFlag) {
                    this.intrFlag = true;
                }
                break;
            case 30://Return from system call
                //load the stored sp and pc
                //point back to old execution
                if (this.intrFlag) {
                    this.intrFlag = false;
                }
                break;
            case 50://End execution
                System.exit(0);
        }
    }

    public int readFromMemory(int addr) {
        this.pw.println(addr);
        this.pw.flush();
        //System.out.println("accessing address "+addr);
        int response = Integer.parseInt(this.sc.nextLine());
        return response;

    }

    public void writeToMemory(int addr, int data) {
        this.pw.println("w-" + addr + "-" + data);
        this.pw.flush();
        //System.out.println("accessing address "+addr);
        //int response = Integer.parseInt(this.sc.nextLine());
        //return response;

    }

    public static void main(String args[]) {
        CPU cpu = new CPU();
        Runtime rt = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = rt.exec("java Memory sample2.txt");
            InputStream is = proc.getInputStream(); //used for pipe
            OutputStream os = proc.getOutputStream();//used for pipe
            cpu.pw = new PrintWriter(os);
            cpu.sc = new Scanner(is);
            int response = 0;
            while (response != 50) {
                response = cpu.readFromMemory(cpu.pc++);
                //System.out.println("response "+response);
                cpu.checkInstructions(response);
            }
            cpu.pw.println(cpu.pc);
            cpu.pw.flush();
            proc.waitFor();
            int exitVal = proc.exitValue();
            System.out.println("Process exited: " + exitVal);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
