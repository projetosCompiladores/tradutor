// FibonacciSeries.asm
// Computes the Fibonacci series and stores the result in RAM.

@401
D=M         // D = argument[1] (starting address)
@3
M=D         // THAT = argument[1]

@0
D=A
@3
A=M
M=D         // that[0] = 0

@1
D=A
@3
A=M+1
M=D         // that[1] = 1

@400
D=M         // D = argument[0]
@2
D=D-A       // D = argument[0] - 2
@400
M=D         // argument[0] -= 2

(LOOP_START)
@400
D=M         // D = num_of_elements
@COMPUTE_ELEMENT
D;JGT       // If num_of_elements > 0, goto COMPUTE_ELEMENT
@END_PROGRAM
0;JMP       // Otherwise, goto END_PROGRAM

(COMPUTE_ELEMENT)
@3
A=M
D=M         // D = that[0]
A=A+1
D=D+M      // D = that[0] + that[1]
A=A+1
M=D         // that[2] = that[0] + that[1]

@3
D=M+1       // that += 1
M=D

@400
M=M-1       // num_of_elements--

@LOOP_START
0;JMP

(END_PROGRAM)
@END_PROGRAM
0;JMP       // Ensures the program halts
