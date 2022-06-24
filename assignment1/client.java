import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class client {
	public static void main(String args[]) throws IOException {

		Scanner sc = new Scanner(System.in);
		System.out.print("Enter number of Queries:");
		int n1 = sc.nextInt();
		//sc.close();
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		
		
		
		
		File file=new File("querry.txt");    //creates a new file instance  
      		FileReader fr=new FileReader(file);   //reads the file which contains the records
       		BufferedReader br=new BufferedReader(fr); 
       
       		FileReader frx=new FileReader(file);//creates a buffering character input stream  
       		BufferedReader brx=new BufferedReader(frx); 
      		String line=""; 
      		String linex="";  
		
		
		
		
		
		
		
		
		
		for(int i =0;i<n1;i++)
		{
			Socket s=new Socket("localhost", 4999);
			
     			line=br.readLine();
                        linex=br.readLine();
						
			Runnable t1 = new Demo(s,line,linex);
			executorService.submit(t1);
		}	
			
			
			
			
			
			
			
			
		/*	
			
			String str = "";
			str = bf.readLine();
			System.out.println("Server: " + str);
			str = bf.readLine();
			System.out.println("Server: " + str);
			str = bf.readLine();
			System.out.println("Server: " + str);
	
			int n = sc.nextInt();
			pr.println(n);
			pr.flush();
			Runnable t1;
			/*if (n == 2) {
				System.out.println("Enter Start node");
				int a = sc.nextInt();
	
				System.out.println("Enter End node");
				int b = sc.nextInt();
	
				System.out.println("Enter the weight");
				Double x = sc.nextDouble();
				String Sx = Integer.toString(a) + " " + Integer.toString(b) + " " + Double.toString(x);
				t1 = new Demo(s,);
			
				// pr.println(Sx);
				// pr.flush();
	
			}
			else {
				System.out.print("Enter the Start node: ");
				int start = sc.nextInt();
				System.out.print("Enter the end node: ");
				int dest = sc.nextInt();
				String Sx = Integer.toString(start) + " " + Integer.toString(dest);
				System.out.println(Sx);
	
				t1 = new Demo(s,Sx);
			
				// pr.println(Sx);
				// pr.flush();
			}
	
			// str = bf.readLine();
			// System.out.println(str);
	
			// pr.flush();
	
			//Runnable t1 = new Demo(s);
			executorService.submit(t1);
			
		}*/

		

	}
}


class Demo implements Runnable 
{

    protected Socket s;
	public String Sx;
	public String str;
	
    public Demo(Socket s1, String st,String strx) 
    {
       		this.s = s1;
		this.Sx = st;
		this.str =strx;
    }

    public void run() // throws IOException,InterruptedException
    {
		try{
			PrintWriter pr=new PrintWriter(s.getOutputStream());	
			InputStreamReader in = new InputStreamReader(s.getInputStream());
			BufferedReader bf = new BufferedReader(in);
			
			pr.println(Sx);	
			pr.flush();
			
			pr.println(str);	
			pr.flush();			
			String str = bf.readLine();
			System.out.println(str);
			s.close();
		}
		catch(IOException e){
			return;
		}
		
		

	}
}
