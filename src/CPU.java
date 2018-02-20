import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/*
 * Created By: Shruti Bidada
 * Net ID: sgb160130
 * */

public class CPU {
    public static final int USER_ADDR = 1000;
    public static final int SYS_ADDR = 1500;
    public static final int MAX_ADDR = 2000;

    int sp = 1000;
    int ac = 0;
    int x = 0;
    int y = 0;
    int pc = 0;

    PrintWriter pw;
    Scanner sc;

    boolean intrFlag = false;
    boolean timerFlag = false;
    int timerInst = Integer.MAX_VALUE;
    ;
    int timerInstCount = 0;

    public CPU() {
    }

    public void checkInstructions(int command) {
        int temp = 0;
        switch (command) {
            case 1://Load the value into the AC
                this.ac = this.readFromMemory(this.pc++);
                break;
            case 2://Load the value at the address into the AC
                temp = this.readFromMemory(this.pc++);
                if (!this.intrFlag && temp >= 1000) {
                    System.out.println("Memory violation: accessing system address " + temp + " in user mode");
                    break;
                }
                this.ac = this.readFromMemory(temp);
                break;
            case 3://Load the value from the address found in the given address into the AC
                this.ac = this.readFromMemory(this.readFromMemory(this.readFromMemory(this.pc++)));
                break;
            case 4://Load the value at (address+X) into the AC
                this.ac = this.readFromMemory(this.readFromMemory(this.pc++) + this.x);
                break;
            case 5://Load the value at (address+Y) into the AC
                this.ac = this.readFromMemory(this.readFromMemory(this.pc++) + this.y);
                break;
            case 6://Load from (Sp+X) into the AC (if SP is 990, and X is 1, load from 991).
                this.ac = this.readFromMemory(this.sp + this.x);
                break;
            case 7://Store the value in the AC into the address
                this.writeToMemory(this.readFromMemory(this.pc++), this.ac);
                break;
            case 8://Gets a random int from 1 to 100 into the AC
                this.ac = (int) (Math.random() * 100 + 1);
                break;
            case 9:/*If port=1, writes AC as an int to the screen If port=2, writes AC as a char to the screen*/
                temp = this.readFromMemory(this.pc++);
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
                this.pc = temp;
                break;
            case 21://Jump to the address only if the value in the AC is zero
                temp = this.readFromMemory(this.pc++);
                if (this.ac == 0) {
                    this.pc = temp;
                }
                break;
            case 22://Jump to the address only if the value in the AC is not zero
                temp = this.readFromMemory(this.pc++);
                if (this.ac != 0) {
                    this.pc = temp;
                }
                break;
            case 23://Push return address onto stack, jump to the address
                temp = this.readFromMemory(this.pc++);
                this.writeToMemory(--this.sp, this.pc);
                this.pc = temp;
                break;
            case 24://Pop return address from the stack, jump to the address
                if (this.sp < MAX_ADDR) {
                    this.pc = this.readFromMemory(this.sp);
                    sp++;
                }
                break;
            case 25://Increment the value in X
                this.x += 1;
                break;
            case 26://Decrement the value in X
                this.x -= 1;
                break;
            case 27://Push AC onto stack
                this.writeToMemory(--this.sp, this.ac);
                break;
            case 28://Pop from stack into AC
                if (this.sp < MAX_ADDR) {
                    this.ac = this.readFromMemory(this.sp);
                    sp++;
                }
                break;
            case 29://Perform system call
                //sava pc and sp on stack
                //jump to 1500
                if (!this.intrFlag) {
                    this.intrFlag = true;
                    this.writeToMemory(--this.sp, this.pc);
                    int tempSp = this.sp;
                    this.sp = MAX_ADDR - 1;
                    this.writeToMemory(--this.sp, tempSp);
                    this.pc = SYS_ADDR;
                }
                break;
            case 30://Return from system call
                //load the stored sp and pc
                //point back to old execution
                if (this.intrFlag) {
                    if (this.sp < MAX_ADDR) {
                        this.sp = this.readFromMemory(this.sp);
                        this.pc = this.readFromMemory(this.sp);
                        this.sp++;
                    }
                    this.intrFlag = false;
                }
                if (this.timerFlag) {
                    this.timerFlag = false;
                }
                break;
            case 50://End execution
                System.exit(0);
        }
    }

    public int readFromMemory(int addr) {
        this.pw.println(addr);
        this.pw.flush();
        int response = Integer.parseInt(this.sc.nextLine());
        return response;

    }

    public void printn(String msg) {
        System.out.println(msg);
    }

    public void writeToMemory(int addr, int data) {
        this.pw.println("w-" + addr + "-" + data);
        this.pw.flush();
    }

    public static void main(String args[]) {
        CPU cpu = new CPU();
        if (args.length == 0) {
            System.out.println("Missing Arguments. Please pass filename as arguments");
            return;
        } else if (args.length == 2) {
            cpu.timerInst = Integer.parseInt(args[1]);
        }
        if (args[0] == null || args[0].isEmpty()) {
            System.out.println("File name missing. Please pass a input file as parameter.");
            return;
        }
        Runtime rt = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = rt.exec("java Memory " + args[0]);
            InputStream is = proc.getInputStream(); //used for pipe
            OutputStream os = proc.getOutputStream();//used for pipe
            cpu.pw = new PrintWriter(os);
            cpu.sc = new Scanner(is);
            int response = 0;
            while (response != 50) {
                response = cpu.readFromMemory(cpu.pc++);
                cpu.checkInstructions(response);
                if (!cpu.timerFlag)
                    cpu.timerInstCount++;
                if (cpu.timerInstCount == cpu.timerInst && !cpu.intrFlag) {
                    cpu.timerFlag = true;
                    cpu.intrFlag = true;
                    cpu.timerInstCount = 0;
                    cpu.writeToMemory(--cpu.sp, cpu.pc);
                    int tempSp = cpu.sp;
                    cpu.writeToMemory(--cpu.sp, tempSp);
                    cpu.pc = USER_ADDR;
                } else if (cpu.timerInstCount == cpu.timerInst) {
                    cpu.timerInstCount = 0;
                }

            }
            cpu.pw.println(cpu.pc);
            cpu.pw.flush();
            proc.waitFor();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
