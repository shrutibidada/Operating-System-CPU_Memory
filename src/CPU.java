import java.io.*;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Logger;

public class CPU {
    int ip = 0;
    int sp = 1000;
    int ir = 0;
    int ac = 0;
    int x = 0;
    int y = 0;
    int pc = 0;
    Logger log;
    PrintWriter pw;
    Scanner sc;
    boolean intrFlag = false;
    boolean timerFlag = false;
    int timerInst = Integer.MAX_VALUE;;
    int timerInstCount = 0;

    public CPU() {
        log = Logger.getLogger(Memory.class.getName());
    }

    public void checkInstructions(int command) {
        int temp = 0;
        switch (command) {
            case 1://Load the value into the AC
                this.ac = this.readFromMemory(this.pc++);
                print("1: AC: " + this.ac);
                break;
            case 2://Load the value at the address into the AC
                temp=this.readFromMemory(this.pc++);
                if(!this.intrFlag && temp>=1000){
                    System.out.println("Memory violation: accessing system address " +temp+ " in user mode");
                    break;
                }
                this.ac = this.readFromMemory(temp);
                print("2: PC : " + this.pc);
                print("2: AC : " + this.ac);
                break;
            case 3://Load the value from the address found in the given address into the AC
                this.ac = this.readFromMemory(this.readFromMemory(this.readFromMemory(this.pc++)));
                print("3: AC : " + this.ac);
                break;
            case 4://Load the value at (address+X) into the AC
                print("4: AC " + this.ac + "  pc: " + this.pc + " x:" + this.x);
                this.ac = this.readFromMemory(this.readFromMemory(this.pc++) + this.x);
                print("4: AC" + this.ac);
                break;
            case 5://Load the value at (address+Y) into the AC
                print("5: Address Read" + (this.pc+1+this.y));
                this.ac = this.readFromMemory(this.readFromMemory(this.pc++) + this.y);
                print("5: AC" + this.ac);
                break;
            case 6://Load from (Sp+X) into the AC (if SP is 990, and X is 1, load from 991).
                this.ac = this.readFromMemory(this.sp + this.x);
                print("6: AC" + this.ac);
                break;
            case 7://Store the value in the AC into the address
                this.writeToMemory(this.readFromMemory(this.pc++),this.ac);
                print("7: AC" + this.ac+ " PC: "+ (this.pc-1));
                break;
            case 8://Gets a random int from 1 to 100 into the AC
                this.ac = (int) (Math.random() * 100 + 1);
                print("8: AC" + this.ac);
                break;
            case 9:/*If port=1, writes AC as an int to the screen If port=2, writes AC as a char to the screen*/
                temp = this.readFromMemory(this.pc++);
               print("printing ascii value" + this.ac);
                if (temp == 2)
                    System.out.print((char) this.ac);
                else if (temp == 1)
                    System.out.print(this.ac);
                break;
            case 10://Add the value in X to the AC
                this.ac += this.x;
                print("10: AC" + this.ac);
                break;
            case 11://Add the value in Y to the AC
                this.ac += this.y;
                print("11: AC" + this.ac);
                break;
            case 12://Subtract the value in X from the AC
                this.ac -= this.x;
                print("12: AC" + this.ac);
                break;
            case 13://Subtract the value in Y from the AC
                this.ac -= this.y;
                print("13: AC" + this.ac);
                break;
            case 14://Copy the value in the AC to X
                this.x = this.ac;
                print("14: X :" + this.x);
                break;
            case 15://Copy the value in X to the AC
                this.ac = this.x;
                print("15: AC " + this.ac);
                break;
            case 16://Copy the value in Y to the AC
                this.y = this.ac;
                print("16: Y " + this.y);
                break;
            case 17://Copy the value in Y to the AC
                this.ac = this.y;
                print("17 : AC " + this.ac);
                break;
            case 18://Copy the value in AC to the SP
                this.sp = this.ac;
                print("18 : SP " + this.ac);
                break;
            case 19://Copy the value in SP to the AC
                this.ac = this.sp;
                print("19 : AC " + this.ac);
                break;
            case 20://Jump to the address
                temp = this.readFromMemory(this.pc++);
                this.pc = temp;
                print("20: PC " + temp);
                break;
            case 21://Jump to the address only if the value in the AC is zero
                temp = this.readFromMemory(this.pc++);
                if (this.ac == 0) {
                    this.pc = temp;
                    print("21: PC " + temp);
                }
                print("21: temp value: " + temp);
                break;
            case 22://Jump to the address only if the value in the AC is not zero
                temp = this.readFromMemory(this.pc++);
                if (this.ac != 0) {
                    this.pc = temp;
                }
                print("22: PC " + temp);
                break;
            case 23://Push return address onto stack, jump to the address
                temp = this.readFromMemory(this.pc++);
                print("23: temp " + temp);
                this.writeToMemory(--this.sp, this.pc);
                print(this.sp + "---" + this.pc);
                this.pc = temp;
                //this.sp--;
                break;
            case 24://Pop return address from the stack, jump to the address
                if (this.sp < 2000) {
                    this.pc = this.readFromMemory(this.sp);
                    print("24: pc " + this.pc);
                    print("24: SP " + this.sp);
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
                print("in 27: " + this.sp + "---" + this.ac);
                //this.sp--;
                break;
            case 28://Pop from stack into AC
                if (this.sp < 2000) {
                    print("Pop : 28: " + this.sp);
                    this.ac = this.readFromMemory(this.sp);
                    print("Pop result : 28: AC " + this.ac);
                    sp++;
                }
                break;
            case 29://Perform system call
                //sava pc and sp on stack
                //jump to 1500
                if (!this.intrFlag) {
                    this.intrFlag = true;
                    this.writeToMemory(--this.sp, this.pc);
                    print("Value of sp before switching "+this.sp);
                    print("Value of pc stored in user stack "+this.pc);
                    print(this.sp+ "---"+ this.pc);
                    int tempSp = this.sp;
                    this.sp = 1999;
                    this.writeToMemory(--this.sp, tempSp);
                    print("Value of sp after switching "+this.sp);
                    print("value.... ");
                    print(this.sp+ "---"+ tempSp);
                    this.pc = 1500;
                    //this.sp--;
                }
                break;
            case 30://Return from system call
                //load the stored sp and pc
                //point back to old execution
                if (this.intrFlag) {
                    if (this.sp < 2000) {
                        this.sp = this.readFromMemory(this.sp);
                        print("value of sp in 30 " + this.sp);
                        this.pc = this.readFromMemory(this.sp);
                        print("value of pc in 30 " + this.pc);
                        this.sp++;
                    }
                    this.intrFlag = false;
                }
                if(this.timerFlag){
                    this.timerFlag=false;
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

    public void print(String msg) {
     // System.out.println(msg);
    }

    public void writeToMemory(int addr, int data) {
        this.pw.println("w-" + addr + "-" + data);
        this.pw.flush();
    }

    public static void main(String args[]) {
        CPU cpu = new CPU();
        if(args.length==0){
            System.out.println("Missing Arguments. Please pass filename as arguments");
            return;
        }
        else if(args.length==2){
            cpu.timerInst=Integer.parseInt(args[1]);
        }
        if( args[0]==null || args[0].isEmpty()){
            System.out.println("File name missing. Please pass a input file as parameter.");
            return;
        }
        Runtime rt = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = rt.exec("java Memory "+args[0]);
            InputStream is = proc.getInputStream(); //used for pipe
            OutputStream os = proc.getOutputStream();//used for pipe
            cpu.pw = new PrintWriter(os);
            cpu.sc = new Scanner(is);
            int response = 0;
            while (response != 50) {
                response = cpu.readFromMemory(cpu.pc++);
                cpu.print("response "+response);
                cpu.checkInstructions(response);
                if(!cpu.timerFlag)
                    cpu.timerInstCount++;
                if (cpu.timerInstCount == cpu.timerInst && !cpu.intrFlag) {
                    cpu.print("executing the timer interupt------");
                    cpu.timerFlag=true;
                    cpu.intrFlag = true;
                    cpu.timerInstCount = 0;
                    cpu.writeToMemory(--cpu.sp, cpu.pc);
                    int tempSp = cpu.sp;
                    cpu.writeToMemory(--cpu.sp, tempSp);
                    cpu.print("Timer Interupt occured ");
                    cpu.print(cpu.sp + "---" + cpu.pc);
                    cpu.pc = 1000;
                }
                else if(cpu.timerInstCount==cpu.timerInst){
                    cpu.timerInstCount = 0;
                }

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
