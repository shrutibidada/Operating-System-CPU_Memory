import java.io.*;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Logger;

public class CPU {
    int ip = 0;
    int sp = 0;
    int ir = 0;
    int ac = 0;
    int x = 0;
    int y = 0;
    int pc = 0;
    Stack<Integer> stack = new Stack<Integer>();
    Logger log;

    public CPU() {
        log = Logger.getLogger(Memory.class.getName());
    }

    public void checkInstructions(int command) {
        switch (command) {
            case 1://Load the value into the AC
                break;
            case 2://Load the value at the address into the AC
                break;
            case 3://Load the value from the address found in the given address into the AC
                break;
            case 4://Load the value at (address+X) into the AC
                break;
            case 5://Load the value at (address+Y) into the AC
                break;
            case 6://Load from (Sp+X) into the AC (if SP is 990, and X is 1, load from 991).
                break;
            case 7://Store the value in the AC into the address
                break;
            case 8://Gets a random int from 1 to 100 into the AC
                System.out.println("Executing 8");
                this.ac = (int) (Math.random() * 100 + 1);
                break;
            case 9:/*If port=1, writes AC as an int to the screen If port=2, writes AC as a char to the screen*/

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
                break;
            case 21://Jump to the address only if the value in the AC is zero
                break;
            case 22://Jump to the address only if the value in the AC is not zero
                break;
            case 23://Push return address onto stack, jump to the address
                break;
            case 24://Pop return address from the stack, jump to the address
                break;
            case 25://Increment the value in X
                this.x += 1;
                break;
            case 26://Decrement the value in X
                this.x -= 1;
                break;
            case 27://Push AC onto stack
                this.stack.push(this.ac);
                break;
            case 28://Pop from stack into AC
                if (this.stack.size() > 0)
                    this.stack.pop();
                break;
            case 29://Perform system call

                break;
            case 30://Return from system call
                break;
            case 50://End execution
                return;
        }
    }

    public static void main(String args[]) {
        CPU cpu = new CPU();
        Runtime rt = Runtime.getRuntime();
        Process proc = null;
        String x;
        try {
            proc = rt.exec("java Memory sample.txt");
            InputStream is = proc.getInputStream(); //used for pipe
            OutputStream os = proc.getOutputStream();//used for pipe
            //trial
            PrintWriter pw = new PrintWriter(os);
            Scanner sc = new Scanner(is);
            int response = 0;
            //check this logic
            while (response != 50) {
                pw.println(cpu.pc);
                pw.flush();
                response = Integer.parseInt(sc.nextLine());
                System.out.println(response);
                cpu.checkInstructions(response);
                cpu.pc++;
            }
            pw.println(cpu.pc);
            pw.flush();
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
