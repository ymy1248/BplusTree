author: R04921094 Ye, Meng-Yuan

Running environment
--------------------------------------------------------------------------------
java version 1.8.0_111
It shoulde be ok in SE7


Abstract
--------------------------------------------------------------------------------
This project is written by java. For TA's convince, easier to check the answer, I output a result file in such format:
	Input command
	Answer format
I will introduce this function command in detail later.

This project contains the following file:
	README.txt: the entry of this project.
	All .java file: This is the code of this project; it will be explained in project report.
	All .class file: class file for java Main command.
	report.pdf: project report write in Chinese.
	ProjectB_data.txt: TA’s sample test data.
	test_data.txt: demo correctness data, this file will construct d = 2 integer tree.



Command
--------------------------------------------------------------------------------
java Main test_data.txt

For correctness, using test_data.txt file build a d=2 integer type b+ tree. This is related with the correctness demo in report. Uncommand Main.java in line 96 and line 97 to see tree structure after insertion. Uncommand Main.java in line 127 and line 128 to see tree structure after deletion. This command will out put a file named test_data_result.txt in answer format.

java Main ProjectB_data.txt

This project handle all the sample input. Enter this command, we will output ProjectB_data_result.txt file as well. TA can check all the output result in that file. In general, my project can finish all the input.

java Main your_file_name.txt

This commands is built for TA check any input file. This command allows you to input arbitrary input file, as long as java BufferedReader can read it. It output a result file named “your_file_name_result.txt”. 
